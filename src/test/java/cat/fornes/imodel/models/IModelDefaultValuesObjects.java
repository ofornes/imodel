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
package cat.fornes.imodel.models;

import java.util.List;
import java.util.Vector;

import cat.fornes.imodel.annotations.DefaultImplementation;
import cat.fornes.imodel.annotations.NotNullDefault;

/**
 * A model with object properties and default implementation objects.
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public interface IModelDefaultValuesObjects extends IModel
{
    /**
     * A prop with list.
     * @return The list
     */
    @DefaultImplementation(defaultImplementation=Vector.class)
    @NotNullDefault
    public List<String> getList();
    /**
     * A prop with list.
     * @param list The list
     */
    public void setList(List<String> list);
    /**
     * A prop with list.
     * @return The list
     */
    @DefaultImplementation(defaultImplementation=Vector.class)
    public List<String> getList2();
    /**
     * A prop with list.
     * @param list The list
     */
    public void setList2(List<String> list);
}
