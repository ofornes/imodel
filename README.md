IModel
======

A little framework to create, dinamically, class implementations from beans definitions interfaces.

Use
---

First of all, you should define your model:

    :::java
        public interface IModel extends Serializable, Cloneable
        {
                /** Internal ID, unique */
                public long getId();
                /** Internal ID, unique */
                public void setId(long id);
                /** Name */
                public String getName();
                /** Name */
                public void setName(String name);
                /** Last name */
                public String getLasName();

After, use the proxy to get a class implementation on-the-fly

    :::java
        IModel m;

        // Create a class implementation        
        m = ProxyBeanFactory.newProxy(IModel.class);
        // Use it
        m.setName("Name");
        m.setLasName("Last name");

The class implementation puts equals, hasCode, toString methods. Also, if specified,
puts the methods to clone (if extends Cloneable).

Is serializable-safe, because implement the seralize and deseralize methods to ensure
the recreation of de dinamyc class on destination


