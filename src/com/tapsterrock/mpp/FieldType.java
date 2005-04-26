/*
 * file:       FieldType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 16, 2005
 */
 
package com.tapsterrock.mpp;

import java.util.Locale;

/**
 * This interface is implemented by classes which represent a field
 * in either the Task or Resource entity. 
 */
public interface FieldType
{
   /**
    * Retrieve the name of this field using the default locale.
    * 
    * @return field name
    */
   public String getName();
   
   /**
    * Retrieve the name of this field using the supplied locale.
    * 
    * @param locale target locale
    * @return field name
    */
   public String getName (Locale locale);
}
