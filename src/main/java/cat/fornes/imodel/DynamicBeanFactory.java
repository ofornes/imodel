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
package cat.fornes.imodel;

import cat.fornes.imodel.support.IDelegateMethodDispatcher;

/**
 * The dynamic bean static factory creator.
 * 
 * For use with interfaces that represents a Java Bean. <br/>
 * <b>Use</b>
 * <pre>
 * InterfaceJavaBean a;
 * 
 * a = DynamicBeanFactory.newDynamicBean(InterfaceJavaBean.class);
 * ...
 * a.setXXX("xxx");
 * </pre>
 * 
 * FIXME Add a configuration system to add genericDispatchers
 * 
 * @author octavi@fornes.cat
 * @since 1.0.0
 */
public abstract class DynamicBeanFactory
{
    /**
     * The genericDispatchers, applicable automatically to any new dynamic bean.
     * The index is the qualified name of the interface.
     */
    static final ConfigurableDynamicBeanFactory singletonDynamicBeanFactory = new ConfigurableDynamicBeanFactory();
    /**
     * Creates a dynamic bean for the type.
     * @param typeToImplement The interface type to implement
     * @return The dynamic bean, as typeToImplement type.
     * @throws IllegalArgumentException If the type is not an interface
     * @throws IllegalArgumentException If null is passed
     */
    public static final <T> T newDynamicBean(Class<T> typeToImplement)
    {
        return singletonDynamicBeanFactory.instantiateBean(typeToImplement);
    }
    /**
     * Add a new generic dispatcher associated with an interface.
     * @param interfaceToDispatch The interface class, cannot be null and should to be an interface
     * @param dispatcher The dispatcher, cannot be null
     * @throws IllegalArgumentException If any argument are null or if interfaceToDispatch is not an interface
     */
    public static final void addGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        singletonDynamicBeanFactory.addGenericDispatcher(interfaceToDispatch,dispatcher);
    }
    /**
     * Remove a dispatcher for a interface.
     * @param interfaceToDispatch The interface class, cannot be null and should to be an interface
     * @param dispatcher The dispatcher, cannot be null
     * @return true if removed or false if not
     * @throws IllegalArgumentException If any argument are null or if interfaceToDispatch is not an interface
     */
    public static final boolean removeGenericDispatcher(Class<?>interfaceToDispatch, IDelegateMethodDispatcher dispatcher)
    {
        return singletonDynamicBeanFactory.removeGenericDispatcher(interfaceToDispatch, dispatcher);
    }
    /**
     * Clone a dynamicBean instance.
     * @param dynamicBean The dynamicBean
     * @return The cloned instance
     * @throws IllegalArgumentException If the instance isn't a dynamicBean
     */
    public static final <T> T cloneDynamicBean(DynamicBeanImpl<T> dynamicBean)
    {
        return singletonDynamicBeanFactory.cloneDynamicBean(dynamicBean);
    }
}
