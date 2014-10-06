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

import static cat.fornes.imodel.support.DynamicBeanUtils.checkJavaBeanProperty;
import static cat.fornes.imodel.support.DynamicBeanUtils.isGetter;
import static cat.fornes.imodel.support.DynamicBeanUtils.isImplementationsAllowed;
import static cat.fornes.imodel.support.DynamicBeanUtils.isImplementationsRequired;
import static cat.fornes.imodel.support.DynamicBeanUtils.isProperty;
import static cat.fornes.imodel.support.DynamicBeanUtils.propertyName;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import cat.fornes.imodel.annotations.DefaultImplementation;
import cat.fornes.imodel.annotations.DefaultValue;
import cat.fornes.imodel.annotations.DynamicBeanProperty;
import cat.fornes.imodel.annotations.NotNullDefault;
import cat.fornes.imodel.support.DynamicBeanUtils;
import cat.fornes.imodel.support.IDynamicBeanModel;

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

    private static final Logger logger = LoggerFactory.getLogger(DynamicBeanImplementationInfo.class);
    
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
                if(isGetter(method.getName()))
                {
                    pb.getterMethod = method;
                }
                else
                {
                    pb.setterMethod = method;
                }
            }
        }
        resolveDetailMetadata();
        // From this state, the maps and collections should to be unmodifiable
        // so wear the collections with the unmodifiable proxy...
        propertyBeanDescriptors = Collections.unmodifiableMap(propertyBeanDescriptors);
        propertyNames = Collections.unmodifiableList(propertyNames);
    }
    private void resolveDetailMetadata()
    {
        // Introspect the detail property metadata
        for(PropertyBeanDescriptor pb: propertyBeanDescriptors.values())
        {
            if(!checkJavaBeanProperty(pb.getGetterMethod(), pb.getSetterMethod()))
            {
                throw new IllegalArgumentException(String.format("The property %s for model %s is incorrect, the types are different for get and set"
                        , pb.name, implementedType.getName()));
            }
            // Check if primitive,
            pb.primitive = pb.getPropertyType().isPrimitive();
            checkForImplementations(pb);
            checkForAnnotations(pb);
            // Test for implementation required and defined
            if(isImplementationsRequired(pb.getPropertyType()))
            {
                if(pb.getDefaultImplementationValue() == null && pb.isDynamicBean() == false)
                {
                    // Warning only!
                    logger.warn("The property {} for model {} maybe should to have a default implementation"
                            , pb.name, implementedType.getName());
                }
            }
        }
    }
    /**
     * Check a type for default implementations, like classes or recognized Dynamic Bean by derivation (after resolve getters and setters).
     * Check if {@link PropertyBeanDescriptor#getPropertyType()} is a non-abstract class or if its a
     * interface and is derived from {@link IDynamicBeanModel}.
     * Sets the values for {@link PropertyBeanDescriptor#defaultImplementation} and
     * {@link PropertyBeanDescriptor#dynamicBean}
     * @param pb The property bean descriptor
     */
    private void checkForImplementations(PropertyBeanDescriptor pb)
    {
        if(isImplementationsAllowed(pb.getPropertyType()))
        {
            if(isImplementationsRequired(pb.getPropertyType()))
            {
                // Check for interfaces
                if(pb.getPropertyType().isInterface())
                {
                    // Check for recognized dynamic bean by derivation
                    for(Class<?> ifaces : pb.getPropertyType().getInterfaces())
                    {
                        if(IDynamicBeanModel.class.equals(ifaces))
                        {
                            // Its a recognized dynamic bean by derivation
                            pb.dynamicBean = true;
                        }
                    }
                }
            }
            else
            {
                // Its a class, check for possible instantiation
                // Public default constructor?
                if(pb.getPropertyType().getConstructors().length > 0)
                {
                    // Search for default...
                    for(Constructor<?> constructor: pb.getPropertyType().getConstructors())
                    {
                        if(constructor.getParameterCount() == 0)
                        {
                            // OK
                            pb.defaultImplementation = pb.getPropertyType();
                        }
                    }
                }
            }
        }
    }
    /**
     * Check annotations for methods after check for implementations.
     * This is the last step on introspection of a property, after the {@link #checkForImplementations(PropertyBeanDescriptor)}
     * step.
     * @param pb The {@link PropertyBeanDescriptor descriptor} for property
     * @param method The method (can be get or set)
     * @param propertyType The property type (return of get and arg of set)
     * @param implementedType The implemented model type
     */
    private void checkForAnnotations(PropertyBeanDescriptor pb)
    {
        DefaultImplementation defImpVal;
        DefaultValue defVal;
        
        // If primitive or string, only default value
        if(pb.primitive || pb.getPropertyType().equals(String.class))
        {
            // Default value only for primitive or strings
            defVal = annotationForProperty(pb,DefaultValue.class);
            if(defVal != null)
            {
                // Check the conversion
                pb.defaultValue = DynamicBeanUtils.safeConvert(defVal.value(), pb.getPropertyType());
                if(pb.defaultValue == null)
                {
                    throw new IllegalArgumentException(String.format("The annotation for default value '%s' on property %s of type %s is incorrect!",
                            defVal.value(), pb.name, implementedType.getName()));
                }
            }
        }
        else
        {
            // Can make instrospection for implementation?
            if(DynamicBeanUtils.isImplementationsAllowed(pb.getPropertyType()))
            {
                // Check for recognized dynamic bean
                if(annotationPresentForProperty(pb,DynamicBeanProperty.class))
                {
                    // is a dynamic bean
                    pb.dynamicBean = true;
                }
                else
                {
                    // check for default implementation if defined
                    // If default implementation value
                    defImpVal = annotationForProperty(pb,DefaultImplementation.class);
                    if(defImpVal != null)
                    {
                        pb.defaultImplementation = defImpVal.defaultImplementation();
                        // Check for instantiation
                        try
                        {
                            pb.defaultImplementation.newInstance();
                        }
                        catch(InstantiationException | IllegalAccessException e)
                        {
                            throw new IllegalArgumentException(String.format("The annotation default implementation value %s on property %s of type %s cannot be instantiated"
                                    , pb.defaultImplementation.getName(), pb.name, implementedType.getName()), e);
                        }
                        // If dynabean, precedence is this annotation
                        pb.dynamicBean = false;
                    }
                }
            }
            // Finally, default value
            if(annotationPresentForProperty(pb,NotNullDefault.class)
                    || annotationPresentForProperty(pb, NotNull.class))
            {
                pb.defaultValue = ""; // Only for indicate that should to be instantiated
            }
        }
    }
    /**
     * Gets the annotation for property, from getter or setter.
     * @param <AT> The type of annotation
     * @param pb The property
     * @param annotationClass The annotation class
     * @return The annotation instance, if any
     */
    @SuppressWarnings("unchecked")
    private <AT extends Annotation> AT annotationForProperty(PropertyBeanDescriptor pb, Class<AT> annotationClass)
    {
        Annotation ant;
        
        ant = null;
        if(pb.getGetterMethod() != null)
        {
            ant = pb.getGetterMethod().getAnnotation(annotationClass);
        }
        if(ant == null && pb.getSetterMethod() != null)
        {
            ant = pb.getSetterMethod().getAnnotation(annotationClass);
        }
        return (AT)ant;
    }
    /**
     * Test for presence of annotation on property, from getter or setter.
     * @param pb The property descriptor
     * @param annotationClass The annotation class
     * @return true if present and false if not
     */
    private boolean annotationPresentForProperty(PropertyBeanDescriptor pb, Class<? extends Annotation> annotationClass)
    {
        boolean r;
        
        r = false;
        if(pb.getGetterMethod() != null)
        {
            r = pb.getGetterMethod().isAnnotationPresent(annotationClass);
        }
        if(!r && pb.getSetterMethod() != null)
        {
            r = pb.getSetterMethod().isAnnotationPresent(annotationClass);
        }
        return r;
    }
    /**
     * On deserialize, reconstruct the implemented type information.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
    {
        in.defaultReadObject();
        resolveGettersSettersMethods();
    }
    
    /**
     * Resolve the 'getter and setter method' for all properties.
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
                }
                else
                {
                    pb.setterMethod = method;
                }
            }
        }
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

        /** The property name, without get/set/is and the first character in lower case. */
        private String name;

        /** The getter method. */
        private transient Method getterMethod;

        /** The setter method. */
        private transient Method setterMethod;

        /** If property is primitive. */
        private boolean primitive;
        
        /**
         * Default value for the property of type string or primitive;
         * if is a dynamic bean, a concrete class or a {@link #defaultImplementation}
         * only means that should to be instantiated on construction.
         */
        private Object defaultValue;
        
        /** If property should to be initialized whit an instance of the default implementation value. */
        private Class<?> defaultImplementation;
        
        /** If the property are recognized as dynamicBean, by annotation or by derivation from {@link IDynamicBeanModel}. */
        private boolean dynamicBean;
        
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
            defaultImplementation = origin.defaultImplementation;
            defaultValue = origin.defaultValue;
            dynamicBean = origin.dynamicBean;
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
         * An optional default value for primitive or String property; also indicate not-null default for dynamic bean.
         * If the property is a dynamic bean and this is not-null, the property should to be instantiated by default
         * @return The default value or null if they cannot have any default value
         */
        public Object getDefaultValue()
        {
            return defaultValue;
        }
        /**
         * An optional default implementation class value for properties abstracts, or interfaces or non primitives.
         * @return the {@link Class} of the default implementation class value
         */
        public Class<?> getDefaultImplementationValue()
        {
            return defaultImplementation;
        }
        
        /**
         * Indicate that this property are recognized as a DynamicBean, by {@link DynamicBeanProperty annotation} or
         * by {@link IDynamicBeanModel derivation}.
         * @return true if this is recognized as dynamic bean and false otherwise
         */
        public boolean isDynamicBean()
        {
            return dynamicBean;
        }
        /**
         * The type of property.
         * @return The type
         */
        public Class<?> getPropertyType()
        {
            return getterMethod.getReturnType();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {
            return String.format("PropertyBeanDescriptor [name=%s, getterMethod=%s, setterMethod=%s, primitive=%s, defaultImplementationValue=%s, defaultValue=%s, dynamicBean=%s]"
                    , name, getterMethod, setterMethod, primitive, defaultImplementation, defaultValue, dynamicBean);
        }
    }
}
