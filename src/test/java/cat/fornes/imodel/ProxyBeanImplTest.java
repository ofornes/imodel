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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;

import cat.fornes.imodel.models.EGender;
import cat.fornes.imodel.models.IModel;
import cat.fornes.imodel.models.ModelImpl;

/**
 * Test of {@link ProxyBeanImpl}
 * 
 * @author octavi@fornes.cat
 * @since 0.0.1-SNAPSHOT
 */
public class ProxyBeanImplTest
{
	/** Test value for name */
	private static final String NAME_VALUE = "NAME";
	/** Test value for last name */
	private static final String LAST_NAME_VALUE = "LAST_NAME";
	/**
	 * Test the 'getters' and 'setters' features
	 */
	@Test public void testGettersSetters()
	{
		IModel m;
		Calendar cal, cal1;
		
		m = ProxyBeanFactory.newProxy(IModel.class);
		Assert.assertNull(m.getName());
		Assert.assertNull(m.getLasName());
		Assert.assertNull(m.getBirthDate());
		Assert.assertNull(m.getGender());
		
		m.setName(NAME_VALUE);
		Assert.assertEquals(NAME_VALUE, m.getName());
		Assert.assertNull(m.getLasName());
		Assert.assertNull(m.getBirthDate());
		Assert.assertNull(m.getGender());
		
		m.setLasName(LAST_NAME_VALUE);
		Assert.assertEquals(NAME_VALUE, m.getName());
		Assert.assertEquals(LAST_NAME_VALUE, m.getLasName());
		Assert.assertNull(m.getBirthDate());
		Assert.assertNull(m.getGender());
		
		cal = Calendar.getInstance();
		cal1 = Calendar.getInstance();
		cal1.setTimeInMillis(cal.getTimeInMillis());
		m.setBirthDate(cal.getTime());
		Assert.assertEquals(NAME_VALUE, m.getName());
		Assert.assertEquals(LAST_NAME_VALUE, m.getLasName());
		Assert.assertEquals(cal1.getTime(), m.getBirthDate());
		Assert.assertNull(m.getGender());
		
		m.setGender(EGender.Male);
		Assert.assertEquals(NAME_VALUE, m.getName());
		Assert.assertEquals(LAST_NAME_VALUE, m.getLasName());
		Assert.assertEquals(cal1.getTime(), m.getBirthDate());
		Assert.assertEquals(EGender.Male,m.getGender());
	}
	/**
	 * Test the 'clone' feature
	 */
	@Test public void testClone()
	{
		IModel m1, m2;
		Calendar cal;
		
		m1 = ProxyBeanFactory.newProxy(IModel.class);
		
		m1.setName(NAME_VALUE);
		m1.setLasName(LAST_NAME_VALUE);
		cal = Calendar.getInstance();
		m1.setBirthDate(cal.getTime());
		m1.setGender(EGender.Female);
		
		m2 = m1.clone();
		
		Assert.assertEquals(m1, m2);
	}
	/**
	 * Test the 'hashCode' feature
	 */
	@Test public void testHashCode()
	{
		IModel m1, m2;
		Calendar cal;
		int n;
		
		m1 = ProxyBeanFactory.newProxy(IModel.class);
		
		m1.setName(NAME_VALUE);
		m1.setLasName(LAST_NAME_VALUE);
		cal = Calendar.getInstance();
		m1.setBirthDate(cal.getTime());
		m1.setGender(EGender.Female);
		
		m2 = m1.clone();

		for(n = 0; n < 10; n++)
		{
		    Assert.assertTrue(m1.hashCode() == m2.hashCode());
		}
	}
	/**
	 * Test the serialization feature.
	 */
	@Test public void testSerialize() throws Exception
	{
		ByteArrayOutputStream baos;
		IModel model, model1;
		ObjectOutputStream out;
		ByteArrayInputStream bais;
		ObjectInputStream in;
		
		model = ProxyBeanFactory.newProxy(IModel.class);
		model.setName("TEST1");
		model.setLasName("TEST-LAST_NAME");
		model.setBirthDate(Calendar.getInstance().getTime());
		model.setGender(EGender.Male);
		baos = new ByteArrayOutputStream();
		out = new ObjectOutputStream(baos);
		out.writeObject(model);
		out.flush();
		
		// Deserialize
		bais = new ByteArrayInputStream(baos.toByteArray());
		in = new ObjectInputStream(bais);
		model1 = (IModel)in.readObject();
		
		// Test if equals
		Assert.assertNotNull(model1);
		Assert.assertEquals(model, model1);
		Assert.assertNotSame(model, model1);
	}
	
	@Test public void testMixedClone()
	{
		IModel m1, m2;
		
		m1 = ProxyBeanFactory.newProxy(IModel.class);
		m1.setName(NAME_VALUE);
		m1.setLasName(LAST_NAME_VALUE);
		m1.setBirthDate(Calendar.getInstance().getTime());
		m1.setGender(EGender.Female);
		
		m2 = new ModelImpl(m1);
		
		Assert.assertEquals(m1, m2);
	}
}
