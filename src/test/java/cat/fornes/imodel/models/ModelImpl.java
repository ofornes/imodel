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

import java.util.Date;

import cat.fornes.imodel.utils.ObjectUtils;

/**
 * A implementation of {@link IModel} for test mixed operations (proxy and class)
 * @author octavi@fornes.cat
 * @since 0.0.1-SNAPSHOT
 */
public class ModelImpl implements IModel
{
	private static final long serialVersionUID = 2392710557526409071L;

	private String name;
	private String lastName;
	private Date birthDate;
	private EGender gender;
	/**
	 * Default constructor.
	 * The properties are left with default values
	 */
	public ModelImpl()
	{
		// Nothing to do
	}
	/**
	 * Copy constructor.
	 * @param origin The origin of data
	 */
	public ModelImpl(IModel origin)
	{
		if(origin != null)
		{
			name = origin.getName();
			lastName = origin.getLasName();
			birthDate = origin.getBirthDate() == null ? null : new Date(origin.getBirthDate().getTime());
			gender = origin.getGender();
		}
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setName(java.lang.String)
	 */
	@Override
	public void setName(String name)
	{
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getLasName()
	 */
	@Override
	public String getLasName()
	{
		return lastName;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setLasName(java.lang.String)
	 */
	@Override
	public void setLasName(String lastName)
	{
		this.lastName = lastName;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getBirthDate()
	 */
	@Override
	public Date getBirthDate()
	{
		return birthDate;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setBirthDate(java.util.Date)
	 */
	@Override
	public void setBirthDate(Date birthDate)
	{
		this.birthDate = birthDate == null ? null : new Date(birthDate.getTime());
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getGender()
	 */
	@Override
	public EGender getGender()
	{
		return gender;
	}

	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setGender(cat.fornes.imodel.beans.models.EGender)
	 */
	@Override
	public void setGender(EGender gender)
	{
		this.gender = gender;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result + ((birthDate == null) ? 0 : birthDate.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj == null || IModel.class.isAssignableFrom(obj.getClass()) == false)
		{
			return  false;
		}
		IModel other = (IModel)obj;
		
		return (ObjectUtils.nullSafeEquals(name, other.getName())
				&& ObjectUtils.nullSafeEquals(lastName, other.getLasName())
				&& ObjectUtils.nullSafeEquals(birthDate, other.getBirthDate())
				&& ObjectUtils.nullSafeEquals(gender, other.getGender())
				);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString()
	{
		return String.format("ModelImpl [name=%s, lastName=%s, birthDate=%s, gender=%s]", name, lastName, birthDate, gender);
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public IModel clone()
	{
		return new ModelImpl(this);
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getId()
	 */
	@Override
	public long getId()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setId(long)
	 */
	@Override
	public void setId(long id)
	{
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getNumberOfChilds()
	 */
	@Override
	public int getNumberOfChilds()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setNumberOfChilds(int)
	 */
	@Override
	public void setNumberOfChilds(int numberOfChilds)
	{
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#getIncomingYear()
	 */
	@Override
	public double getIncomingYear()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	/* (non-Javadoc)
	 * @see cat.fornes.imodel.beans.models.IModel#setIncomingYear(double)
	 */
	@Override
	public void setIncomingYear(double incomingYear)
	{
		// TODO Auto-generated method stub
		
	}

}
