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

import cat.fornes.imodel.annotations.DefaultImplementation;
import cat.fornes.imodel.support.IDynamicBeanModel;

/**
 * A default implementation for test the annotated {@link DefaultImplementation} cases.
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public class PropertyDynBeanImpl implements IPropertyDynBean
{
    private static final long serialVersionUID = -1341204344420294638L;
    private String name;
    /**
     * 
     */
    public PropertyDynBeanImpl()
    {
        // res a fer
    }
    /**
     * Copy constructor.
     * @param copy The origin of data
     */
    public PropertyDynBeanImpl(IPropertyDynBean copy)
    {
        if(copy != null)
        {
            name = copy.getName();
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public IDynamicBeanModel clone()
    {
        return new PropertyDynBeanImpl(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName()
    {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name)
    {
        this.name = name;
    }
}
