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
package cat.fornes.imodel.models;

import java.io.Serializable;
import java.util.Date;

import cat.fornes.imodel.annotations.DefaultValue;

/**
 * A model for test purposes.
 * 
 * @author octavi@fornes.cat
 * @since 1.0.0
 */
public interface IModel extends Serializable, Cloneable
{
    /** A default value for {@link #getId()}. */
    public static final String ID_DEFAULT_VALUE = "346112";
	/**
	 * Internal ID, unique. 
	 * @return the Id
	 */
    @DefaultValue(ID_DEFAULT_VALUE)
	public long getId();
	/**
	 * Internal ID, unique.
	 * @param id The id
	 */
	public void setId(long id);
	/**
	 * The name.
	 * @return the name
	 */
	public String getName();
    /**
     * The name.
     * @param name the name
     */
	public void setName(String name);
    /**
     * The last name.
     * @return the last name
     */
	public String getLasName();
    /**
     * The last name.
     * @param lastName the last name
     */
	public void setLasName(String lastName);
    /**
     * The birth date.
     * @return the birth date
     */
	public Date getBirthDate();
    /**
     * The birth date.
     * @param birthDate the birth date
     */
	public void setBirthDate(Date birthDate);
    /**
     * Number of children.
     * @return the number of children
     */
	public int getNumberOfChildren();
    /**
     * Number of children.
     * @param numberOfChildren the number of children
     */
	public void setNumberOfChildren(int numberOfChildren);
    /**
     * The incoming for year.
     * @return the incoming for year
     */
	public double getIncomingYear();
    /**
     * The incoming for year.
     * @param incomingYear the incoming for year
     */
	public void setIncomingYear(double incomingYear);
    /**
     * The gender.
     * @return the gender
     */
	public EGender getGender();
    /**
     * The gender.
     * @param gender the gender
     */
	public void setGender(EGender gender);
	/**
	 * Clone this bean.
	 * @return The cloned bean
	 */
	public IModel clone();
}
