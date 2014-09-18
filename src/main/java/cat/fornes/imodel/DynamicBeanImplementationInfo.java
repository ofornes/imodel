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

import static cat.fornes.imodel.support.DynamicBeanUtils.isGetter;
import static cat.fornes.imodel.support.DynamicBeanUtils.isProperty;
import static cat.fornes.imodel.support.DynamicBeanUtils.propertyName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import org.springframework.util.Assert;

/**
 * The metadata information for a {@link DynamicBeanImpl dynamic bean implementation} for a specific type.
 * 
 * This information metadata can be shared among different <i>instances</i> of dynamic bean to reduce the amount of memory used and introspection processing time.
 * 
 * @param <T> The type to implement
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
class DynamicBeanImplementationInfo<T> implements Serializable
{
    private static final long serialVersionUID = 1156512358884027116L;

    /** The property descriptors. */
    private Map<String, PropertyBeanDescriptor> propertyBeanDescriptors;
    /** The implemented type. */
    private Class<T> implementedType;
    /** Property names in appearance order to help on hashCode() and toString(). */
    private List<String> propertyNames;
    /**
     * Default constructor.
     * @param typeToImplement The type to implement
     */
    public DynamicBeanImplementationInfo(Class<T> typeToImplement)
    {
        Assert.notNull(typeToImplement, "The type to implement is required");
        Assert.isTrue(typeToImplement.isInterface(), "The type to implement should to be an interface");

        implementedType = typeToImplement;
        propertyBeanDescriptors = Collections.synchronizedMap(new TreeMap<String, PropertyBeanDescriptor>());
        propertyNames = new Vector<String>();
        
        prepareMetadata();
    }
    /**
     * Process the {@link #implementedType} to extract the metadata information.
     */
    private void prepareMetadata()
    {
        PropertyBeanDescriptor pb;
        
        // Prepare the properties list
        for(Method method : implementedType.getMethods())
        {
            if(Modifier.isPublic(method.getModifiers()) && isProperty(method.getName()))
            {
                if((pb = propertyBeanDescriptors.get(propertyName(method.getName()))) == null)
                {
                    // Put them!
                    pb = new PropertyBeanDescriptor();
                    pb.name = propertyName(method.getName());
                    propertyBeanDescriptors.put(pb.name, pb);
                    propertyNames.add(pb.name);
                }
            }
        }
        resolveGettersSettersMethods();
        // From this state, the maps and collections should to be unmodifiable
        // so wear the collections with the unmodifiable proxy...
        propertyBeanDescriptors = Collections.unmodifiableMap(propertyBeanDescriptors);
        propertyNames = Collections.unmodifiableList(propertyNames);
    }
    /**
     * Resolve the 'getter method' for all properties.
     */
    private void resolveGettersSettersMethods()
    {
        PropertyBeanDescriptor pb;
        // Prepare the properties list
        for(Method method : implementedType.getMethods())
        {
            if(Modifier.isPublic(method.getModifiers()) && isProperty(method.getName()))
            {
                pb = propertyBeanDescriptors.get(propertyName(method.getName()));
                if(isGetter(method.getName()))
                {
                    pb.getterMethod = method;
                    pb.primitive = method.getReturnType().isPrimitive();
                }
                else
                {
                    pb.setterMethod = method;
                }
            }
        }
    }

    /**
     * On deserialize, reconstruct the implemented type information.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        resolveGettersSettersMethods();
    }
    
    // -------------- Getters
    /**
     * The property bean descriptor of the implemented type.
     * @return An unmodifiable map of descriptors, with the property name (without get/is) as the key.
     */
    public Map<String, PropertyBeanDescriptor> getPropertyBeanDescriptors()
    {
        return propertyBeanDescriptors;
    }
    /**
     * Gets the {@link PropertyBeanDescriptor} associated with the javaBean property name.
     * @param propertyName The javaBean property name
     * @return The {@link PropertyBeanDescriptor} for this property or null if none with this name exists
     */
    public PropertyBeanDescriptor getPropertyBeanDescriptor(String propertyName)
    {
        return propertyBeanDescriptors.get(propertyName);
    }
    /**
     * The implemented type class.
     * @return the class of the implemented type
     */
    public Class<T> getImplementedType()
    {
        return implementedType;
    }
    /**
     * The javaBean property names of the {@link #getImplementedType() implemented type}.
     * @return An unmodifiable list with the javaBean names
     */
    public List<String> getPropertyNames()
    {
        return propertyNames;
    }
    /**
     * A structure for hold the properties information of bean.
     */
    class PropertyBeanDescriptor implements Serializable
    {
        private static final long serialVersionUID = 0L;

        private String name;

        private transient Method getterMethod;

        private transient Method setterMethod;

        private boolean primitive;
        
        /**
         * Default constructor.
         */
        public PropertyBeanDescriptor()
        {
            // Nothing to do
        }
        /**
         * Copy constructor.
         * @param origin The origin
         */
        public PropertyBeanDescriptor(PropertyBeanDescriptor origin)
        {
            name = origin.name;
            getterMethod = origin.getterMethod;
            setterMethod = origin.setterMethod;
            primitive = origin.primitive;
        }
        
        /**
         * The property name.
         * @return the property name
         */
        public String getName()
        {
            return name;
        }
        /**
         * The getter method.
         * @return the getter method
         */
        public Method getGetterMethod()
        {
            return getterMethod;
        }
        /**
         * The setter method.
         * @return the setter method
         */
        public Method getSetterMethod()
        {
            return setterMethod;
        }
        /**
         * Flag indicating if this property is primitive.
         * @return true if its primitive and false if not.
         */
        public boolean isPrimitive()
        {
            return primitive;
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return String.format("PropertyBeanDescriptor [name=%s, getterMethod=%s, setterMethod=%s, primitive=%s]", name, getterMethod, setterMethod, primitive);
        }
    }
}
