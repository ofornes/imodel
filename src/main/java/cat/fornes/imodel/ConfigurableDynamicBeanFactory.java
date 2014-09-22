/*
 * This file is part of "imodel-core".
 * 
 * "imodel-core" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "imodel-core" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2014 Octavi Fornés
 */
package cat.fornes.imodel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import cat.fornes.imodel.annotations.DelegateMethodDispatcher;
import cat.fornes.imodel.support.IDelegateMethodDispatcher;

/**
 * A configurable dynamic bean factory.
 * Can be used with dependency injection containers.  
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
@Component
public class ConfigurableDynamicBeanFactory implements IDynamicBeanFactory
{
    private static final Logger logger = LoggerFactory.getLogger(ConfigurableDynamicBeanFactory.class);
    
    private Map<String, Set<IDelegateMethodDispatcher>> genericDispatchers;
    private Map<String, DynamicBeanImplementationInfo<?>> metadataTypes;
    /**
     * Default constructor.
     */
    public ConfigurableDynamicBeanFactory()
    {
        genericDispatchers = Collections.synchronizedMap(new TreeMap<String, Set<IDelegateMethodDispatcher>>());
        metadataTypes = Collections.synchronizedMap(new TreeMap<String, DynamicBeanImplementationInfo<?>>());
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Set<IDelegateMethodDispatcher>> getGenericDispatchers()
    {
        return genericDispatchers;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void setGenericDispatchers(Map<String, Set<IDelegateMethodDispatcher>> genericDispatchers)
    {
        this.genericDispatchers = genericDispatchers;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addAllGenericDispatchers(Class<?> interfaceToDispatch, List<IDelegateMethodDispatcher> dispatchers)
    {
        Assert.notNull(interfaceToDispatch, "The interfaceToDispatch is required");
        Assert.isTrue(interfaceToDispatch.isInterface(), "The interfaceToDispatch should to be an interface");
        Assert.notNull(dispatchers, "The dispatcher collection is required");
        
        if(genericDispatchers == null)
        {
            genericDispatchers = Collections.synchronizedMap(new TreeMap<String, Set<IDelegateMethodDispatcher>>());
        }
        ensureListDispatchers(interfaceToDispatch.getName()).addAll(dispatchers);
    }
    /**
     * Ensure the list dispatchers for the name.
     * @param name The name associated with the dispatcher's list.
     * @return The list itself.
     */
    private Set<IDelegateMethodDispatcher> ensureListDispatchers(String name)
    {
        Set<IDelegateMethodDispatcher> listDispatchers;
        listDispatchers = genericDispatchers.get(name);
        if(listDispatchers == null)
        {
            listDispatchers = new TreeSet<IDelegateMethodDispatcher>(new Comparator<IDelegateMethodDispatcher>()
            {
                @Override
                public int compare(IDelegateMethodDispatcher o1, IDelegateMethodDispatcher o2)
                {
                    return o1.getClass().getName().compareTo(o2.getClass().getName());
                }
                
            });
            genericDispatchers.put(name, listDispatchers);
        }
        return listDispatchers;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void addGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        Assert.notNull(interfaceToDispatch, "The interfaceToDispatch is required");
        Assert.isTrue(interfaceToDispatch.isInterface(), "The interfaceToDispatch should to be an interface");
        Assert.notNull(dispatcher, "The dispatcher is required");
        
        if(genericDispatchers == null)
        {
            genericDispatchers = Collections.synchronizedMap(new TreeMap<String, Set<IDelegateMethodDispatcher>>());
        }
        ensureListDispatchers(interfaceToDispatch.getName()).add(dispatcher);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        Set<IDelegateMethodDispatcher> listDispatchers;
        boolean retorn;

        Assert.notNull(interfaceToDispatch, "The interfaceToDispatch is required");
        Assert.isTrue(interfaceToDispatch.isInterface(), "The interfaceToDispatch should to be an interface");
        Assert.notNull(dispatcher, "The dispatcher is required");
        
        if(genericDispatchers == null)
        {
            genericDispatchers = Collections.synchronizedMap(new TreeMap<String, Set<IDelegateMethodDispatcher>>());
        }
        listDispatchers = genericDispatchers.get(interfaceToDispatch.getName());
        if(listDispatchers == null)
        {
            return false;
        }
        if( (retorn = listDispatchers.remove(dispatcher)) && listDispatchers.isEmpty())
        {
            genericDispatchers.remove(interfaceToDispatch.getName());
        }
        return retorn;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T instantiateBean(Class<T> typeToImplement)
    {
        DynamicBeanImplementationInfo<T> metadata;
        Set<IDelegateMethodDispatcher> dispatchers;
        
        metadata = resolveMetadata(typeToImplement);
        
        if(genericDispatchers != null)
        {
            dispatchers = genericDispatchers.get(typeToImplement.getName());
        }
        else
        {
            dispatchers = null;
        }
        return (T)Proxy.newProxyInstance(typeToImplement.getClassLoader()
               , new Class[] {typeToImplement}, new DynamicBeanImpl<T>(this, metadata,dispatchers));
    }
    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T cloneDynamicBean(T dynamicBean)
    {
        if(DynamicBeanImpl.class.isAssignableFrom(dynamicBean.getClass()))
        {
            DynamicBeanImpl<T> db;
            
            db = (DynamicBeanImpl<T>)dynamicBean;
            return (T)Proxy.newProxyInstance(db.getImplementedType().getClassLoader()
                    , new Class[] {db.getImplementedType()}
                    , new DynamicBeanImpl<T>(db));
        }
        throw new IllegalArgumentException(String.format("The instance '%s' is not a dynamic bean!",dynamicBean));
    }
    /**
     * Resolve metadata for typeToImplement.
     * Search for metadata on registered metadata and, if found, return it.
     * If not found, creates new one and add to registered metadata. Then return it.
     * @param typeToImplement The type to implement
     * @return The metadata
     */
    @SuppressWarnings("unchecked")
    private <T> DynamicBeanImplementationInfo<T> resolveMetadata(Class<T> typeToImplement)
    {
        DynamicBeanImplementationInfo<T> metadata;
        Set<String> dispatchers;
        Set<IDelegateMethodDispatcher> dInstances;
        
        Assert.notNull(typeToImplement , "typeToImplement cannot to be null");
        metadata = (DynamicBeanImplementationInfo<T>)metadataTypes.get(typeToImplement.getName());
        if(metadata == null)
        {
            // Construct and register the metadata
            metadata = new DynamicBeanImplementationInfo<T>(typeToImplement);
            metadataTypes.put(typeToImplement.getName(), metadata);
        }
        dispatchers = Collections.synchronizedSet(new TreeSet<String>());
        // Check for delegateDispatcher from annotations
        for(Class<?> interfaceToCheck: typeToImplement.getInterfaces())
        {
            recopileDispatchers(dispatchers, interfaceToCheck);
        }
        // if dispatchers...
        if(!dispatchers.isEmpty())
        {
            // Materialize...
            dInstances = ensureListDispatchers(typeToImplement.getName());
            for(String d : dispatchers)
            {
                try
                {
                    dInstances.add((IDelegateMethodDispatcher)Class.forName(d).newInstance());
                }
                catch(InstantiationException | IllegalAccessException | ClassNotFoundException e)
                {
                    logger.error(String.format("On instantiate delegate method dispatcher '%s', ommit them! (%s)",d,e.getMessage()),e);
                    // Ommit this delegate dispatcher
                }
            }
        }
        return metadata;
    }
    
    private void recopileDispatchers(Set<String> dispatchers, Class<?> interfaceToCheck)
    {
        Annotation annotation;
        
        // Check for parents...
        for(Class<?> otherInterface : interfaceToCheck.getInterfaces())
        {
            recopileDispatchers(dispatchers, otherInterface);
        }
        // Now check annotations on this
        annotation = interfaceToCheck.getAnnotation(DelegateMethodDispatcher.class);
        if(annotation != null)
        {
            dispatchers.add(((DelegateMethodDispatcher)annotation).delegateDispatcher().getName());
        }
    }
}
