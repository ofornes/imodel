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
    static final IDynamicBeanFactory singletonDynamicBeanFactory = new ConfigurableDynamicBeanFactory();

    /**
     * The dynamic bean factory singleton instance.
     * @return the dynamic bean factory 
     */
    public static IDynamicBeanFactory dynamicBeanFactoryInstance()
    {
        return singletonDynamicBeanFactory;
    }
    
    
}
