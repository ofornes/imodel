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

import javax.validation.constraints.NotNull;

import cat.fornes.imodel.annotations.DefaultImplementation;
import cat.fornes.imodel.annotations.DynamicBeanProperty;
import cat.fornes.imodel.annotations.NotNullDefault;

/**
 * A model with derived properties, annotated properties, etc.
 * @author Octavi Fornés <a href="mailto:ofornes@albirar.cat">ofornes@albirar.cat</a>
 * @since 1.1.0
 */
public interface IModelWithDerivedDynProperties
{
    /**
     * A derived DynBean property.
     * @return a property 
     */
    @NotNullDefault
    public IPropertyDynBean getDerivedDynProperty1();
    /**
     * A derived DynBean property.
     * @param derivedDynProperty a property 
     */
    public void setDerivedDynProperty1(IPropertyDynBean derivedDynProperty);
    /**
     * A derived DynBean property.
     * @return a property 
     */
    @DynamicBeanProperty
    public IModel getDynProperty2();
    /**
     * A derived DynBean property.
     * @param derivedDynProperty a property 
     */
    public void setDynProperty2(IModel derivedDynProperty);
    /**
     * A derived DynBean property.
     * @return a property 
     */
    public IPropertyDynBean getDerivedDynProperty3();
    /**
     * A derived DynBean property.
     * @param derivedDynProperty a property 
     */
    public void setDerivedDynProperty3(IPropertyDynBean derivedDynProperty);
    /**
     * Concrete property with a instantiable class.
     * @return The property
     */
    public ModelImpl getConcreteProperty();
    /**
     * Concrete property with a instantiable class.
     * @param m The property
     */
    @NotNullDefault
    public void setConcreteProperty(ModelImpl m);
    
    /**
     * A dynamic bean but defined default implementation property with a instantiable class.
     * @return The property
     */
    @DefaultImplementation(defaultImplementation=PropertyDynBeanImpl.class)
    @NotNull
    public IPropertyDynBean getAnnotatedProperty();
    /**
     * A dynamic bean but defined default implementation property with a instantiable class.
     * @param annotatedProperty The property
     */
    public void setAnnotatedProperty(IPropertyDynBean annotatedProperty);
}
