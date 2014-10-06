/*
 * This file is part of "imodel".
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
 * Copyright (C) 2013 Octavi Fornés
 */
package cat.fornes.imodel.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.springframework.util.StringUtils;

/**
 * Some utilities for dynamic bean operations.
 * 
 * @author octavi@fornes.cat
 * @since 1.0.0
 */
public abstract class DynamicBeanUtils
{
	/**
	 * Check if the method name is a get/set/is property bean access method.
	 * @param name The method name
	 * @return <b>true</b> if is a property bean access method, <b>false</b> otherwise
	 */
	public static final boolean isProperty(String name)
	{
	    return ((name.startsWith("get") && name.length() > "get".length()) 
	            || (name.startsWith("set") && name.length() > "set".length())
	            || (name.startsWith("is") && name.length() > "is".length()));
	}
	/**
	 * Check for javaBean consistency with standards.
	 * @param getter The getter method, required
	 * @param setter The setter method, optional for read-only properties
	 * @return true if is a javaBean consistent
	 */
    public static boolean checkJavaBeanProperty(Method getter, Method setter)
    {
        if(getter == null)
        {
            return false; // Required!
        }
        // Check getter method
        if(!isProperty(getter.getName()))
        {
            return false;
        }
        if(setter != null)
        {
            // read-write
            
            // Check setter method
            if(!isProperty(setter.getName()))
            {
                return false;
            }
            // Number of parameters?
            if(setter.getParameterCount() != 1)
            {
                return false;
            }
            // Check type consistency
            return getter.getReturnType().equals(setter.getParameters()[0].getType());
        }
        // otherwise OK!
        return true;
    }

    /**
     * Indicates if the type allow to set an implementation.
     * @param type The type
     * @return true if allow and false if not
     */
    public static boolean isImplementationsAllowed(Class<?> type)
    {
        return (!type.isPrimitive()
                    && type.isInterface()
                    && !Modifier.isFinal(type.getModifiers()));
    }

    /**
     * Indicates if the type requires to set an implementation.
     * Cases like interfaces, abstract classes, etc.
     * @param type The type
     * @return true if required and false if not
     */
    public static boolean isImplementationsRequired(Class<?> type)
    {
        return (!type.isPrimitive() &&
                    (type.isInterface()
                     || Modifier.isAbstract(type.getModifiers())));
    }
	/**
	 * Check if the method name is a 'getter'
	 * @param name The method name
	 * @return <b>true</b> if is a 'getter' method, <b>false</b> otherwise
	 */
	public static final boolean isGetter(String name)
	{
	    return (name.startsWith("get") || name.startsWith("is"));
	}

	/**
	 * Gets the "property name" from a getter/setter method name.
	 * 
	 * @param methodName The method name
	 * @return The property name
	 * @throws IllegalArgumentException if method name is not a getter/setter method
	 */
	public static final String propertyName(String methodName)
	{
	    String propName;
	
	    if(isProperty(methodName) == false)
	    {
	        throw new IllegalArgumentException("The method name (" + methodName + ") is not a getter/setter method");
	    }
	    if(isGetter(methodName) == false)
	    {
	        propName = methodName.substring("set".length());
	    }
	    else
	    {
	        if(methodName.startsWith("get"))
	        {
	            propName = methodName.substring("get".length());
	        }
	        else
	        {
	            propName = methodName.substring("is".length());
	        }
	    }
	    return changeFirstCharToLower(propName);
	}

	/**
	 * Search for setter method paired with indicated getter method, if any.
	 * @param getter The getter method
	 * @return The setter method, or null if the property is read-only
	 */
	public static final Method getSetterMethodForGetterMethod(Method getter)
	{
	    String name;
	    
	    name = "set".concat(changeFirstCharToUpper(propertyName(getter.getName())));
	    try
        {
            return getter.getDeclaringClass().getMethod(name, getter.getReturnType());
        }
        catch(NoSuchMethodException e)
        {
            // No method, return null
            return null;
        }
	}
    /**
     * Changes the first char to lower case.
     * @param name The name
     * @return The name with the first char changed to lower case
     */
    public static final String changeFirstCharToLower(String name)
    {
        return name.substring(0, 1).toLowerCase().concat(name.substring(1));
    }
    /**
     * Changes the first char to upper case.
     * @param name The name
     * @return The name with the first char changed to upper case
     */
    public static final String changeFirstCharToUpper(String name)
    {
        return name.substring(0, 1).toUpperCase().concat(name.substring(1));
    }
	
	/**
	 * Safe conversion from a string to the final type, for primitive and String types.
	 * @param representationValue The representation value
	 * @param finalType The final type
	 * @return The value, or null if cannot be converted
	 */
	@SuppressWarnings("unchecked")
    public static final <T> T safeConvert(String representationValue, Class<T> finalType)
	{
	    T retorn;
	    
	    retorn = null;
	    if(StringUtils.hasText(representationValue))
	    {
    	    try
    	    {
                if(finalType.getName().equals("byte"))
                {
                    retorn = (T)Byte.valueOf(representationValue);
                }
                if(finalType.getName().equals("short"))
                {
                    retorn = (T)Short.valueOf(representationValue);
                }
                if(finalType.getName().equals("int"))
                {
                    retorn = (T)Integer.valueOf(representationValue);
                }
                if(finalType.getName().equals("long"))
                {
                    retorn = (T)Long.valueOf(representationValue);
                }
                if(finalType.getName().equals("float"))
                {
                    retorn = (T)Float.valueOf(representationValue);
                }
                if(finalType.getName().equals("double"))
                {
                    retorn = (T)Double.valueOf(representationValue);
                }
                if(finalType.getName().equals("char"))
                {
                    retorn = (T)Character.valueOf(representationValue.charAt(0));
                }
                if(finalType.getName().equals("boolean"))
                {
                    retorn = (T)Boolean.valueOf(representationValue);
                }
    	    }
    	    catch(NumberFormatException e)
    	    {
    	        retorn = null;
    	    }
            if(finalType.getSimpleName().equals("String"))
            {
                retorn = (T)representationValue;
            }
	    }
	    return retorn;
	}
}
