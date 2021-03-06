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
/**
 * Utilities for beans dynamic implementations.
 * 
 * Enables data definition as JavaBean applying the "facade pattern".
 * Next, you can create a implementation with the Proxy utility.<br/>
 * Features:
 * <ul>
 * <li>Implement getters and setters as declared on interface</li>
 * <li>Implement equals, hashCode and toString default methods</li>
 * <li>The proxy can be serialized safely; only errors if some java bean property was defined as non-serializable</li>
 * <li>Also implement a clone method if declared on interface</li>
 * </ul>
 * @since 1.0.0
 */
package cat.fornes.imodel;