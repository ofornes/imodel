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
package cat.fornes.imodel.support;

import java.lang.reflect.Method;

/**
 * The contract to delegate the dispatch of methods on dynamic beans that aren't getter, setters or standard basic methods.
 * <p>A special model with methods other than get, is, set, {@link Object#hashCode()}, {@link Object#toString()}, {@link Object#equals(Object)} and {@link Object#clone()}.</p>
 * <p>A class to support this new methods should to be provided.</p>
 * <p>The semantic for dispatcher are:
 * <ul>
 * <li>The implementation of the {@link #doInvoke(Object, Method, Object...) doInvoke method} should to test if 
 * the method argument should to be dispatched</li>
 * <li>If should to be dispatched and the result is not an exception, the {@link ReturnMetadata} should to have the following values:
 * <ul>
 * <li>{@link ReturnMetadata#isContinueProcessing()} should to be <b>false</b></li> 
 * <li>{@link ReturnMetadata#getReturnedValue()} should to be the <b>return value</b></li> 
 * <li>{@link ReturnMetadata#getExceptionToBeThrown()} should to be <b>null</b></li>
 * </ul></li> 
 * <li>If should to be dispatched and the result is an exception, the {@link ReturnMetadata} should to have the following values:
 * <ul>
 * <li>{@link ReturnMetadata#isContinueProcessing()} should to be <b>false</b></li> 
 * <li>{@link ReturnMetadata#getReturnedValue()} doesn't matter</li> 
 * <li>{@link ReturnMetadata#getExceptionToBeThrown()} should to be the <b>exception to be thrown</b></li>
 * </ul></li> 
 * <li>If <strong>doesn't to be dispatched</strong>, the {@link ReturnMetadata} should to have the following values:
 * <ul>
 * <li>{@link ReturnMetadata#isContinueProcessing()} should to be <b>true</b></li> 
 * <li>{@link ReturnMetadata#getReturnedValue()} doesn't matter</li> 
 * <li>{@link ReturnMetadata#getExceptionToBeThrown()} doesn't matter</li>
 * </ul></li> 
 * </ul>
 *  
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public interface IDelegateMethodDispatcher
{
    /**
     * The callback to process the methods of beans.
     * @param instance The object instance
     * @param method The method metadata
     * @param args The arguments
     * @return The return metadata
     */
    public ReturnMetadata doInvoke(Object instance, Method method, Object... args);
}
