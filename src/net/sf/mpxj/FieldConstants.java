/*
 * file:       FieldConstants.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       13-Feb-2006
 */
 
package net.sf.mpxj;

/**
 * Defines constants common to both task and resource fields.
 */
public class FieldConstants
{
   /**
    * Constructor.
    */
   protected FieldConstants ()
   {
      // protected constructor to prevent instantiation
   }
   
   public static final int PROJECT_FIELD_ID = 0;
   public static final int MPXJ_FIELD_ID = 1;
   public static final int PROJECT_FIELD_NAME = 2;
   public static final int FIELD_DATA_TYPE = 3;
   
   public static final int STRING_ATTRIBUTE = 1;
   public static final int DATE_ATTRIBUTE = 2;
   public static final int CURRENCY_ATTRIBUTE = 3;
   public static final int BOOLEAN_ATTRIBUTE = 4;
   public static final int NUMERIC_ATTRIBUTE = 5;
   public static final int DURATION_ATTRIBUTE = 6;
   
   public static final Integer STRING_ATTRIBUTE_OBJECT = new Integer (STRING_ATTRIBUTE);
   public static final Integer DATE_ATTRIBUTE_OBJECT = new Integer (DATE_ATTRIBUTE);
   public static final Integer CURRENCY_ATTRIBUTE_OBJECT = new Integer (CURRENCY_ATTRIBUTE);
   public static final Integer BOOLEAN_ATTRIBUTE_OBJECT = new Integer (BOOLEAN_ATTRIBUTE);
   public static final Integer NUMERIC_ATTRIBUTE_OBJECT = new Integer (NUMERIC_ATTRIBUTE);
   public static final Integer DURATION_ATTRIBUTE_OBJECT = new Integer (DURATION_ATTRIBUTE);
}
