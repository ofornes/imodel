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

import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.springframework.util.Assert;

import cat.fornes.imodel.support.IDelegateMethodDispatcher;

/**
 * A configurable dynamic bean factory.
 * Can be used with dependency injection containers.  
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public class ConfigurableDynamicBeanFactory
{
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
     * A list of generic dispatchers for each 'key' type.
     * The 'key' is the qualified name of the type, and the value list is a collection
     * of dispatchers for this type.
     * The order of the list establish the precedence on treatment; the first element has the
     * maximum precedence and the last is the minimum precedence.
     * @return collection of generic dispatchers
     */
    public Map<String, Set<IDelegateMethodDispatcher>> getGenericDispatchers()
    {
        return genericDispatchers;
    }
    /**
     * A list of generic dispatchers for each 'key' type.
     * The 'key' is the qualified name of the type, and the value list is a collection
     * of dispatchers for this type.
     * The order of the list establish the precedence on treatment; the first element has the
     * maximum precedence and the last is the minimum precedence.
     * @param genericDispatchers collection of generic dispatchers
     */
    public void setGenericDispatchers(Map<String, Set<IDelegateMethodDispatcher>> genericDispatchers)
    {
        this.genericDispatchers = genericDispatchers;
    }
    /**
     * Adds a new dispatcher to the list for interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch, cannot be null and should to be an interface
     * @param dispatchers A collection of dispatchers, cannot be null
     * @throws IllegalArgumentException If any argument is null or invalid 
     */
    public void addAllGenericDispatchers(Class<?> interfaceToDispatch, List<IDelegateMethodDispatcher> dispatchers)
    {
        Set<IDelegateMethodDispatcher> listDispatchers;
        Assert.notNull(interfaceToDispatch, "The interfaceToDispatch is required");
        Assert.isTrue(interfaceToDispatch.isInterface(), "The interfaceToDispatch should to be an interface");
        Assert.notNull(dispatchers, "The dispatcher collection is required");
        
        if(genericDispatchers == null)
        {
            genericDispatchers = Collections.synchronizedMap(new TreeMap<String, Set<IDelegateMethodDispatcher>>());
        }
        listDispatchers = genericDispatchers.get(interfaceToDispatch.getName());
        if(listDispatchers == null)
        {
            listDispatchers = new HashSet<IDelegateMethodDispatcher>();
            genericDispatchers.put(interfaceToDispatch.getName(), listDispatchers);
        }
        listDispatchers.addAll(dispatchers);
    }
    /**
     * Adds a new dispatcher to the list for interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch, cannot be null and should to be an interface
     * @param dispatcher The dispatcher, cannot be null
     * @throws IllegalArgumentException If any argument is null or invalid 
     */
    public void addGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        Set<IDelegateMethodDispatcher> listDispatchers;

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
            listDispatchers = new HashSet<IDelegateMethodDispatcher>();
            genericDispatchers.put(interfaceToDispatch.getName(), listDispatchers);
        }
        listDispatchers.add(dispatcher);
    }
    /**
     * Remove the dispatcher from the list of dispatcher for the interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch.
     * @param dispatcher The dispatchers to remove to
     * @return true if removed and false if not found
     */
    public boolean removeGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        Set<IDelegateMethodDispatcher> listDispatchers;

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
        return listDispatchers.remove(dispatcher);
    }
    /**
     * Instantiate a dynamic bean implementation of the indicated interface type.
     * @param typeToImplement The interface class to implement; cannot be null and should to be an interface
     * @return The instantiated dynamic bean
     */
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
               , new Class[] {typeToImplement}, new DynamicBeanImpl<T>(metadata,dispatchers));
    }
    /**
     * Clone a dynamicBean.
     * @param dynamicBean The dynamicBean to clone
     * @return The newly cloned dynamic bean
     */
    @SuppressWarnings("unchecked")
    public <T> T cloneDynamicBean(DynamicBeanImpl<T> dynamicBean)
    {
        if(DynamicBeanImpl.class.isAssignableFrom(dynamicBean.getClass()))
        {
            return (T)Proxy.newProxyInstance(dynamicBean.getImplementedType().getClassLoader()
                    , new Class[] {dynamicBean.getImplementedType()}
                    , new DynamicBeanImpl<T>(dynamicBean));
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
        
        Assert.notNull(typeToImplement , "typeToImplement cannot to be null");
        metadata = (DynamicBeanImplementationInfo<T>)metadataTypes.get(typeToImplement.getName());
        if(metadata == null)
        {
            // Construct and register the metadata
            metadata = new DynamicBeanImplementationInfo<T>(typeToImplement);
            metadataTypes.put(typeToImplement.getName(), metadata);
        }
        return metadata;
    }
}
