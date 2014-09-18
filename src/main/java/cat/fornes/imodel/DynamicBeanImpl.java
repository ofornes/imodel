/*
 * This file is part of "imodel" project.
 * 
 * "imodel" is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * "imodel" is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with calendar.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2013 Octavi Fornés octavi@fornes.cat
 */
package cat.fornes.imodel;

import static cat.fornes.imodel.support.DynamicBeanUtils.isGetter;
import static cat.fornes.imodel.support.DynamicBeanUtils.isProperty;
import static cat.fornes.imodel.support.DynamicBeanUtils.propertyName;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import cat.fornes.imodel.DynamicBeanImplementationInfo.PropertyBeanDescriptor;
import cat.fornes.imodel.support.IDelegateMethodDispatcher;
import cat.fornes.imodel.support.ReturnMetadata;

/**
 * A proxy for create dynamic beans from interfaces.
 * 
 * For use with interfaces that represents a Java Bean. <br/>
 * <b>Use</b>
 * <pre>
 * InterfaceJavaBean a;
 * 
 * a = {@link DynamicBeanFactory}.newProxy(InterfaceJavaBean.class);
 * ...
 * a.setXXX("xxx");
 * </pre>
 * 
 * The proxy is a JDK Proxy framework instance.
 * 
 * @param <T> The interface model type
 * 
 * @author octavi@fornes.cat
 * @since 1.0.0
 */
class DynamicBeanImpl<T> implements InvocationHandler, Serializable
{
    private static final long serialVersionUID = 0L;
    
    private static final Logger logger = LoggerFactory.getLogger(DynamicBeanImpl.class);

    private DynamicBeanImplementationInfo<T> beanImplementationInfo;
    
    /** Value list */
    private Map<String, Object> values;
    /** Values for calculate hash code */
    private Object[] hashCodeValues;

    /** An optionally delegate method dispatcher, to call to on {@link #doInvoke(Method, Object...)}. */
    private Set<IDelegateMethodDispatcher> delegateMethodDispatchers;
    private DynamicBeanImpl()
    {
        values = Collections.synchronizedMap(new TreeMap<String, Object>());
    }
    /**
     * Copy constructor.
     * @param origin The origin of the copy
     */
    DynamicBeanImpl(DynamicBeanImpl<T> origin)
    {
        this();
        beanImplementationInfo = origin.beanImplementationInfo;
        delegateMethodDispatchers = origin.delegateMethodDispatchers;
        for(DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor oPb : beanImplementationInfo.getPropertyBeanDescriptors().values())
        {
            values.put(oPb.getName(), cloneValue(oPb, origin.values.get(oPb.getName())));
        }
    }
    /**
     * Constructor with type to implement.
     * @param typeToImplement The interface type to implement
     * @param dispatcher An optional dispatcher for methods other than the dynamically implemented
     * @throws IllegalArgumentException If the type is not an interface
     */
    DynamicBeanImpl(DynamicBeanImplementationInfo<T> metadata, Set<IDelegateMethodDispatcher> dispatchers)
    {
        this();
        
        beanImplementationInfo = metadata;
        delegateMethodDispatchers = dispatchers;
        // Assign default values
        for(DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor pbv : beanImplementationInfo.getPropertyBeanDescriptors().values())
        {
            values.put(pbv.getName(), nullSafeValue(null, pbv));
        }
    }
    /**
     * The implemented type.
     * @return The implemented type
     */
    public Class<T> getImplementedType()
    {
        return beanImplementationInfo.getImplementedType();
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        return doInvoke(method, args);
    }

    /**
     * Invocation operation.
     * @param method The invoked method 
     * @param args The arguments, if any
     * @return The result. Can be the value for a property or the result of {@link #equals(Object)}, {@link #hashCode()} or {@link #toString()}
     */
    protected Object doInvoke(Method method, Object... args) throws Throwable
    {
        DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor pb;
        String name;
        ReturnMetadata returnMetadata;

        name = method.getName();
        // Check if is a get/set method
        if(isProperty(name))
        {
            if((pb = beanImplementationInfo.getPropertyBeanDescriptors().get(propertyName(name))) != null)
            {
                if(isGetter(name))
                {
                    return doGetter(pb);
                }
                // is a setter
                doSetter(pb, args);
                return null;
            }
        }
        // Can be hashCode, toString or equals
        if("hashCode".equals(name))
        {
            return hashCode();
        }
        if("toString".equals(name))
        {
            return toString();
        }
        if("equals".equals(name))
        {
            return equals(args[0]);
        }
        if("clone".equals(name))
        {
            return clone();
        }
        // Other methods, try with dispatchers...
        
        // Try to dispatch with dispatchers
        returnMetadata = dispatch(delegateMethodDispatchers, method, args);
        if(returnMetadata != null)
        {
            if(returnMetadata.getExceptionToBeThrown() != null)
            {
                throw returnMetadata.getExceptionToBeThrown();
            }
            return returnMetadata.getReturnedValue();
        }
        // Not support any other method call
        throw new UnsupportedOperationException("Call to '" + method.getName() + "'");
    }
    /**
     * 
     * @param dispatchers
     * @param method
     * @param args
     * @return
     */
    private ReturnMetadata dispatch(Set<IDelegateMethodDispatcher> dispatchers, Method method, Object...args)
    {
        ReturnMetadata returnMetadata;
        
        if(dispatchers != null)
        {
            for(IDelegateMethodDispatcher dispatcher: dispatchers)
            {
                returnMetadata = dispatcher.doInvoke(this, method, args);
                if(!returnMetadata.isContinueProcessing())
                {
                    return returnMetadata;
                }
            }
        }
        return null;
    }

    /**
     * Do the getter call.
     * @param pb The propertybean descriptor
     * @return the property value
     */
    protected Object doGetter(DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor pb)
    {
        return values.get(pb.getName());
    }

    /**
     * Do the setter call.
     * @param pb The propertybean descriptor
     * @param arguments The arguments
     */
    protected void doSetter(DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor pb, Object... arguments)
    {
        values.put(pb.getName(), nullSafeValue(arguments[0], pb));
    }

    /**
     * Dynamically implemented {@link Object#toString()} method.
     * Returns a String with the pattern:
     * <pre>
     * SimpleNameOfImplementedType [ propertyName=value, ...]
     * </pre>
     * For the {@link DynamicBeanImplementationInfo#getImplementedType() implemented type}
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder stb;
        
        stb = new StringBuilder(beanImplementationInfo.getImplementedType().getSimpleName());
        stb.append(" [");
        // Put the properties in appearance order
        for(String name: beanImplementationInfo.getPropertyNames())
        {
            stb.append(name).append("=").append("" + values.get(name)).append(", ");
        }
        // Change the last element (",") by "]"
        stb.replace(stb.length() - 2, stb.length(), "]");
        return stb.toString();
    }

    /**
     * Dynamically implemented {@link Object#equals(Object)} method.
     * @param o The 'other' object
     * @return as equals specification
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    @SuppressWarnings(
    {
            "rawtypes", "unchecked"
    })
    public boolean equals(Object o)
    {
        T theOther;
        PropertyBeanDescriptor pb;
        DynamicBeanImpl pbo;

        // Check if 'other' is a null
        if(o == null)
        {
            return false;
        }
        // Check for self-equals...
        if(o == this)
        {
            return true;
        }
        // Check if 'the other' is a 'implementedType' type...
        // Precondition: isn't a DynamicBean
        pbo = null;
        if(beanImplementationInfo.getImplementedType().isAssignableFrom(o.getClass()) == false)
        {
            // ...if not, can be a DynamicBeanImpl?...
            if(DynamicBeanImpl.class.isAssignableFrom(o.getClass()))
            {
                pbo = (DynamicBeanImpl) o;

                // Yes, check if the implementedType is the same or derived
                if(beanImplementationInfo.getImplementedType().isAssignableFrom(pbo.beanImplementationInfo.getImplementedType()) == false)
                {
                    return false;
                }
            }
            else
            {
                // Is not implemented type nor DynamicBeanImpl...
                return false;
            }
        }
        theOther = (T) o;
        
        for(String name: beanImplementationInfo.getPropertyNames())
        {
            pb = beanImplementationInfo.getPropertyBeanDescriptor(name);
            try
            {
                Object value;
                if(pbo != null)
                {
                    value = pbo.values.get(name);
                }
                else
                {
                    value = pb.getGetterMethod().invoke(theOther);
                }
                if(ObjectUtils.nullSafeEquals(values.get(name), value) == false)
                {
                    return false;
                }
            }
            catch(SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
            {
                throw new RuntimeException("On equals call!", e);
            }
        }
        return true;
    }

    /**
     * Test if the value is for a primitive type and return an object representation with default (0) value.
     * If value is null and the type is primitive, return a representation of default value for the primitive corresponding type.
     * @param value the value, can be null
     * @param pb The property bean descriptor
     * @return The value or the default value representation for the primitive type (0)
     */
    private Object nullSafeValue(Object value, DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor pb)
    {
        if(!pb.isPrimitive())
        {
            return value;
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("byte"))
        {
            return (value == null ? Byte.valueOf((byte) 0) : (Byte) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("short"))
        {
            return (value == null ? Short.valueOf((short) 0) : (Short) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("int"))
        {
            return (value == null ? Integer.valueOf(0) : (Integer) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("long"))
        {
            return (value == null ? Long.valueOf(0L) : (Long) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("float"))
        {
            return (value == null ? Float.valueOf(0F) : (Float) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("double"))
        {
            return (value == null ? Double.valueOf(0D) : (Double) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("char"))
        {
            return (value == null ? Character.valueOf('\u0000') : (Character) value);
        }
        if(pb.getGetterMethod().getReturnType().getName().equals("boolean"))
        {
            return (value == null ? Boolean.FALSE : (Boolean) value);
        }
        if(pb.getGetterMethod().getReturnType().getSimpleName().equals("String"))
        {
            return value;
        }
        // ERROR!!!
        logger.error(String.format("On manage property bean for class %s: pb: [%s]"
                , beanImplementationInfo.getImplementedType().getName()
                , pb));
        // Unknown??
        throw new IllegalArgumentException("No handler found for type '" + pb.getGetterMethod().getReturnType().getSimpleName() + "'.");
    }

    /**
     * Dynamically implemented {@link Object#hashCode()} method.
     * Includes all properties. The properties are processed in the same order that
     * they appear on interface (see {@link DynamicBeanImplementationInfo#getPropertyNames()}).
     * 
     * @return The calculated hashCode
     * 
     * @see ObjectUtils#nullSafeHashCode(Object[])
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        int n;

        if(hashCodeValues == null)
        {
            hashCodeValues = new Object[beanImplementationInfo.getPropertyNames().size()];
        }
        n = 0;
        for(String name : beanImplementationInfo.getPropertyNames())
        {
            hashCodeValues[n] = values.get(name);
            n++;
        }
        return ObjectUtils.nullSafeHashCode(hashCodeValues);
    }

    /**
     * Clone the implemented bean with the same values
     */
    public T clone()
    {
        return DynamicBeanFactory.<T>cloneDynamicBean(this);
    }
    /**
     * Clone a value for a property bean.
     * @param propertyBeanDescriptor The property bean descriptor
     * @param value The original value, can be null
     * @return The cloned value
     */
    private Object cloneValue(DynamicBeanImplementationInfo<T>.PropertyBeanDescriptor propertyBeanDescriptor, Object value)
    {
        Method clonem;

        if(propertyBeanDescriptor.isPrimitive() || value == null)
        {
            return value;
        }
        try
        {
            clonem = propertyBeanDescriptor.getGetterMethod().getReturnType().getMethod("clone");
            return (Object)clonem.invoke(value);
        }
        catch(NoSuchMethodException e)
        {
            // Cannot clone the value...
            return value;
        }
        catch(IllegalAccessException | SecurityException | IllegalArgumentException | InvocationTargetException e)
        {
            logger.error("On clone property value for pb: '" + propertyBeanDescriptor + "'", e);
            throw new RuntimeException("On clone property value for pb: '" + propertyBeanDescriptor + "'", e);
        }
    }
}
