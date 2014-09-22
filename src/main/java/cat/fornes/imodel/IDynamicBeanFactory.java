/*
 * This file is part of "imodel-core".
 * 
 * "imodel-core" is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later
 * version.
 * 
 * "imodel-core" is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with calendar. If not, see
 * <http://www.gnu.org/licenses/>.
 * 
 * Copyright (C) 2014 Octavi Fornés
 */
package cat.fornes.imodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import cat.fornes.imodel.support.IDelegateMethodDispatcher;

/**
 * The contract for dynamic bean factory.
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public interface IDynamicBeanFactory
{
    /**
     * Instantiate a dynamic bean implementation of the indicated interface type.
     * @param typeToImplement The interface class to implement; cannot be null and should to be an interface
     * @return The instantiated dynamic bean
     */
    public abstract <T> T instantiateBean(Class<T> typeToImplement);

    /**
     * Clone a dynamicBean.
     * @param dynamicBean The dynamicBean to clone
     * @return The newly cloned dynamic bean
     */
    public abstract <T> T cloneDynamicBean(T dynamicBean);

    /**
     * A list of generic dispatchers for each 'key' type.
     * The 'key' is the qualified name of the type, and the value list is a collection
     * of dispatchers for this type.
     * The order of the list establish the precedence on treatment; the first element has the
     * maximum precedence and the last is the minimum precedence.
     * @return collection of generic dispatchers
     */
    public abstract Map<String, Set<IDelegateMethodDispatcher>> getGenericDispatchers();

    /**
     * A list of generic dispatchers for each 'key' type.
     * The 'key' is the qualified name of the type, and the value list is a collection
     * of dispatchers for this type.
     * The order of the list establish the precedence on treatment; the first element has the
     * maximum precedence and the last is the minimum precedence.
     * @param genericDispatchers collection of generic dispatchers
     */
    public abstract void setGenericDispatchers(Map<String, Set<IDelegateMethodDispatcher>> genericDispatchers);

    /**
     * Adds a new dispatcher to the list for interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch, cannot be null and should to be an interface
     * @param dispatchers A collection of dispatchers, cannot be null
     * @throws IllegalArgumentException If any argument is null or invalid 
     */
    public abstract void addAllGenericDispatchers(Class<?> interfaceToDispatch, List<IDelegateMethodDispatcher> dispatchers);

    /**
     * Adds a new dispatcher to the list for interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch, cannot be null and should to be an interface
     * @param dispatcher The dispatcher, cannot be null
     * @throws IllegalArgumentException If any argument is null or invalid 
     */
    public abstract void addGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher);

    /**
     * Remove the dispatcher from the list of dispatcher for the interfaceToDispatch.
     * @param interfaceToDispatch The interface to dispatch.
     * @param dispatcher The dispatchers to remove to
     * @return true if removed and false if not found
     */
    public abstract boolean removeGenericDispatcher(Class<?> interfaceToDispatch, IDelegateMethodDispatcher dispatcher);

}