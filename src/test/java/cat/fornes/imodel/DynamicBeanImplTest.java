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
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;

import cat.fornes.imodel.models.EGender;
import cat.fornes.imodel.models.ICompoundModel;
import cat.fornes.imodel.models.IModel;
import cat.fornes.imodel.models.IModelDefaultValuesObjects;
import cat.fornes.imodel.models.IModelSpecial;
import cat.fornes.imodel.models.IModelWithDerivedDynProperties;
import cat.fornes.imodel.models.ModelImpl;
import cat.fornes.imodel.support.IDelegateMethodDispatcher;
import cat.fornes.imodel.support.ReturnMetadata;

/**
 * Test of {@link DynamicBeanImpl}.
 * 
 * @author octavi@fornes.cat
 * @since 1.0.0
 */
public class DynamicBeanImplTest
{
    /** Test value for dispatcher test. */
    private static final String VALUE_TEST_DELEGATE_ERROR = "xX";
    /** Test value for dispatcher test. */
    private static final String VALUE_TEST_DELEGATE = "35";
    /** Test value for id prop. */
    private static final long ID_VALUE = 43988L;
    /** Default value for id prop. */
    private static final long DEFAULT_ID_VALUE = Long.parseLong(IModel.ID_DEFAULT_VALUE);
	/** Test value for name prop. */
	private static final String NAME_VALUE = "NAME";
	/** Test value for last name prop. */
	private static final String LAST_NAME_VALUE = "LAST_NAME";
	private static final Date BIRTHDATE_VALUE;
    /** Test value for number of children prop. */
	private static final int NUMBER_OF_CHILDREN_VALUE = 3;
	/** Test value for incoming year prop. */
	private static final double INCOMING_YEAR_VALUE = 32056.12D;
	/** Test value for gender prop. */
	private static final EGender GENDER_VALUE = EGender.Female;
	
	/*
	 * Initialize instance
	 */
	static 
	{
	    Calendar c;
	    
	    c = Calendar.getInstance();
	    c.set(Calendar.YEAR, 1964);
	    c.set(Calendar.MONTH, Calendar.NOVEMBER);
	    c.set(Calendar.DATE, 10);
	    c.set(Calendar.HOUR, 0);
	    c.set(Calendar.MINUTE, 0);
	    c.set(Calendar.SECOND, 0);
	    c.set(Calendar.MILLISECOND, 0);
	    BIRTHDATE_VALUE = c.getTime();
	    
	    IDelegateMethodDispatcher dispatcher = new IDelegateMethodDispatcher()
	    {
	        @Override
	        public ReturnMetadata doInvoke(Object instance, Method method, Object... args)
	        {
	            ReturnMetadata rmd;
	            
	            rmd = new ReturnMetadata();
	            rmd.setContinueProcessing(true);
	            if("translateValue".equals(method.getName()))
	            {
	                String value;
	                
	                rmd.setContinueProcessing(false);
	                value = (String)args[0];
	                try
	                {
	                    rmd.setReturnedValue(Integer.parseInt(value));
	                }
	                catch(NumberFormatException e)
	                {
	                    rmd.setExceptionToBeThrown(e);
	                }
	            }
	            return rmd;
	        }
	    };
	    DynamicBeanFactory.dynamicBeanFactoryInstance().addGenericDispatcher(IModelSpecial.class, dispatcher);
	}
	/**
	 * Test the 'getters' and 'setters' features
	 */
	@Test public void testGettersSetters()
	{
		IModel m;
		
		m = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
		checkInitialValuesForModel(m);
		
		m.setId(ID_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertNull(m.getName());
        Assert.assertNull(m.getLasName());
        Assert.assertNull(m.getBirthDate());
        Assert.assertEquals(m.getNumberOfChildren(),0);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());
		
		m.setName(NAME_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
		Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertNull(m.getLasName());
        Assert.assertNull(m.getBirthDate());
        Assert.assertEquals(m.getNumberOfChildren(),0);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());
		
		m.setLasName(LAST_NAME_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertEquals(m.getLasName(),LAST_NAME_VALUE);
        Assert.assertNull(m.getBirthDate());
        Assert.assertEquals(m.getNumberOfChildren(),0);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());
		
		m.setBirthDate(BIRTHDATE_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertEquals(m.getLasName(),LAST_NAME_VALUE);
		Assert.assertEquals(m.getBirthDate(),new Date(BIRTHDATE_VALUE.getTime()));
        Assert.assertEquals(m.getNumberOfChildren(),0);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());

        m.setNumberOfChildren(NUMBER_OF_CHILDREN_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertEquals(m.getLasName(),LAST_NAME_VALUE);
        Assert.assertEquals(m.getBirthDate(),new Date(BIRTHDATE_VALUE.getTime()));
        Assert.assertEquals(m.getNumberOfChildren(),NUMBER_OF_CHILDREN_VALUE);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());

        m.setIncomingYear(INCOMING_YEAR_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertEquals(m.getLasName(),LAST_NAME_VALUE);
        Assert.assertEquals(m.getBirthDate(),new Date(BIRTHDATE_VALUE.getTime()));
        Assert.assertEquals(m.getNumberOfChildren(),NUMBER_OF_CHILDREN_VALUE);
        Assert.assertEquals(m.getIncomingYear(),0.0D,INCOMING_YEAR_VALUE);
        Assert.assertNull(m.getGender());
        
		m.setGender(GENDER_VALUE);
        Assert.assertEquals(m.getId(),ID_VALUE);
        Assert.assertEquals(m.getName(),NAME_VALUE);
        Assert.assertEquals(m.getLasName(),LAST_NAME_VALUE);
        Assert.assertEquals(m.getBirthDate(),new Date(BIRTHDATE_VALUE.getTime()));
        Assert.assertEquals(m.getNumberOfChildren(),NUMBER_OF_CHILDREN_VALUE);
        Assert.assertEquals(m.getIncomingYear(),0.0D,INCOMING_YEAR_VALUE);
		Assert.assertEquals(m.getGender(),GENDER_VALUE);
	}
	/**
	 * Test the 'clone' feature
	 */
	@Test public void testClone()
	{
		IModel m1, m2;
		
		m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
		assignValues(m1);
		
		m2 = m1.clone();
		
		Assert.assertEquals(m2,m1);
	}
	/**
	 * Test the 'hashCode' feature
	 */
	@Test public void testHashCode()
	{
		IModel m1, m2;
		int n;
		
		m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
		assignValues(m1);
		
		m2 = m1.clone();

		for(n = 0; n < 10; n++)
		{
		    Assert.assertTrue(m1.hashCode() == m2.hashCode());
		}
	}
	/**
	 * Test the serialization feature.
	 * @throws Exception If errors on serialization
	 */
	@Test public void testSerialize() throws Exception
	{
		ByteArrayOutputStream baos;
		IModel model, model1;
		ObjectOutputStream out;
		ByteArrayInputStream bais;
		ObjectInputStream in;
		
		// Prepare bean
		model = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
		assignValues(model);

		// Prepare stream
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
		Assert.assertEquals(model1,model);
		Assert.assertNotSame(model, model1);
	}
	/**
	 * Test the clone with explicit bean implementation.
	 */
	@Test public void testMixedClone()
	{
		IModel m1, m2;
		
		m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
		assignValues(m1);
		
		m2 = new ModelImpl(m1);
		
		Assert.assertEquals(m2,m1);
	}
	/**
	 * Test the delegation system for local.
	 */
	@Test public void testLocalDelegateUse()
	{
	    IModelSpecial m1;
	    int n;
	    
	    m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModelSpecial.class);
	    n = m1.translateValue(VALUE_TEST_DELEGATE);
	    Assert.assertEquals(n,Integer.parseInt(VALUE_TEST_DELEGATE));
	    
	    // An error value
	    try
	    {
	        n = m1.translateValue(VALUE_TEST_DELEGATE_ERROR);
	        Assert.fail("On error value!");
	    }
	    catch(NumberFormatException e)
	    {
	        // OK!
	    }
	}
	
    /**
     * Test the delegation system for local.
     */
    @Test public void testGenericDelegateUse()
    {
        IModelSpecial m1;
        int n;
        
        m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModelSpecial.class);
        n = m1.translateValue(VALUE_TEST_DELEGATE);
        Assert.assertEquals(n,Integer.parseInt(VALUE_TEST_DELEGATE));
        
        // An error value
        try
        {
            n = m1.translateValue(VALUE_TEST_DELEGATE_ERROR);
            Assert.fail("On error value!");
        }
        catch(NumberFormatException e)
        {
            // OK!
        }
    }
    /**
     * Test the default object values system.
     */
    @Test public void testDefaultObjectValues()
    {
        IModelDefaultValuesObjects m1;
        
        m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModelDefaultValuesObjects.class);
        checkInitialValuesForModel(m1);
        Assert.assertNotNull(m1.getList());
        Assert.assertTrue(m1.getList().isEmpty());
        Assert.assertNull(m1.getList2());
    }
    /**
     * Test for autodetect properties.
     */
    @Test public void testAutoDetectProperties()
    {
        IModelWithDerivedDynProperties m1;
        
        m1 = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModelWithDerivedDynProperties.class);
        
        // Check for correct default values
        Assert.assertNotNull(m1.getDerivedDynProperty1()); // NotNull bu annotation
        Assert.assertNull(m1.getDynProperty2());  // null by default
        Assert.assertNull(m1.getDerivedDynProperty3());  // Autodetect, null
        Assert.assertNotNull(m1.getAnnotatedProperty());  // By annotation, not autodetect, null
        
    }
    /**
     * Test the {@link IDynamicBeanFactory#instantiateBeanPartialyCloned(Class, Object)}.
     */
    @Test public void testPartialCloneInstantiation()
    {
        IModel m;
        ICompoundModel cm;
        
        m = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBean(IModel.class);
        assignValues(m);
        cm = DynamicBeanFactory.dynamicBeanFactoryInstance().instantiateBeanPartialyCloned(ICompoundModel.class, m);
        checkReferencesValues(cm);
    }
    /**
     * Test the initial values for model base.
     * @param m The model
     */
    private void checkInitialValuesForModel(IModel m)
    {
        Assert.assertEquals(m.getId(),DEFAULT_ID_VALUE);
        Assert.assertNull(m.getName());
        Assert.assertNull(m.getLasName());
        Assert.assertNull(m.getBirthDate());
        Assert.assertEquals(m.getNumberOfChildren(),0);
        Assert.assertEquals(m.getIncomingYear(),0.0D,0.0D);
        Assert.assertNull(m.getGender());
    }
	/**
	 * Assign the test reference values for the model.
	 * @param model The model to assign to
	 */
	private void assignValues(IModel model)
	{
        model.setId(ID_VALUE);
        model.setName(NAME_VALUE);
        model.setLasName(LAST_NAME_VALUE);
        model.setBirthDate(BIRTHDATE_VALUE);
        model.setNumberOfChildren(NUMBER_OF_CHILDREN_VALUE);
        model.setIncomingYear(INCOMING_YEAR_VALUE);
        model.setGender(GENDER_VALUE);
	}
	/**
	 * Check a model if properties are the reference values as assigned in {@link #assignValues(IModel)}.
	 * @param model The model to check
	 */
	private void checkReferencesValues(IModel model)
	{
        Assert.assertEquals(model.getId(),ID_VALUE);
        Assert.assertEquals(model.getName(),NAME_VALUE);
        Assert.assertEquals(model.getLasName(), LAST_NAME_VALUE);
        Assert.assertEquals(model.getBirthDate(),BIRTHDATE_VALUE);
        Assert.assertEquals(model.getNumberOfChildren(),NUMBER_OF_CHILDREN_VALUE);
        Assert.assertEquals(model.getIncomingYear(),INCOMING_YEAR_VALUE);
        Assert.assertEquals(model.getGender(),GENDER_VALUE);
	}
}
