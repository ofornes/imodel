IModel
======
[![Build Status](https://travis-ci.org/ofornes/imodel.svg?branch=master)](https://travis-ci.org/ofornes/imodel)

A little framework of utilities for dynamic beans implementations.

Enables data definition as JavaBean applying the "facade pattern".

Use
---

First of all, should to define the model:

```java
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
```

Next, create an implementation class with the Proxy utility:

```java
        IModel m;

        // Create a class implementation        
        m = ProxyBeanFactory.newProxy(IModel.class);
        // Use it
        m.setName("Name");
        m.setLasName("Last name");
```

Features
--------

* Implement getters and setters as declared on interface
* Implement **equals**, **hashCode** and **toString** default methods
* The proxy can be serialized safely; only errors if some java bean property type is non-serializable
* Also implement a **clone** method if the model extends Cloneable interface


