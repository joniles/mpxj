/*
 * file:       Props.java
 * author:     Jon Iles
 * copyright:  Tapster Rock Limited
 * date:       27/05/2003
 */
package com.tapsterrock.mpp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class represents the Props files found in Microsoft Project MPP files.
 * These files appear to be collections of properties, indexed by an integer
 * key. The format of the properties section is not fully understood, so
 * reading data from the section may fail. To allow the rest of the file
 * to be read in successfully, any failure to read the props section will
 * not cause an exception, instead the complete flag is set to false to
 * indicate that the property data has not been fully retrieved. All properties
 * retrieved up to the point of failure will be available.
 */
final class Props extends MPPComponent
{
   /**
    * Constructor, reads the property data from an input stream.
    * 
    * @param is
    */
   Props (InputStream is)
   {
      try
      {
         readInt (is); // File size
         readInt (is); // Repeat of file size
         readInt(is); // unknown      
         int count = readShort(is); // Number of entries
         readShort(is); // unknown
         
         
         for (int loop=0; loop < count; loop++)
         {
            int attrib1 = readInt(is);
            int attrib2 = readShort(is);
            int attrib3 = is.read();
            int attrib4 = is.read();         
            int attrib5 = readInt(is);
            int size;
            byte[] data;
            
            if (attrib3 == 64)
            {
               size = attrib1;
            }
            else
            {
               size = attrib5;
            }
                                    
            if (attrib5 == 65536)
            {
               size = 4;
            }
                                       
            if (size != 0)
            {
               data = new byte[size];
               is.read(data);                       
            }         
            else
            {
               // bail out here as we don't understand the structure
               m_complete = false;
               break;
            }
            
            m_map.put(new Integer (attrib2), new ByteArray (data));
            
            //
            // Align to two byte boundary
            //
            if (data.length % 2 != 0)
            {
               is.skip(1);
            }
         }      
      }
      
      catch (IOException ex)
      {
         m_complete = false;         
      }         
   }


   /**
    * Retrieves a short int value from the property data
    * 
    * @param type Type identifier
    * @return short int value
    */      
   public int getShort (Integer type)
   {
      int result = 0;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getShort(item.byteArrayValue());
      }
         
      return (result);         
   }

   /**
    * Retrieves an integer value from the property data
    * 
    * @param type Type identifier
    * @return integer value
    */            
   public int getInt (Integer type)
   {
      int result = 0;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getInt(item.byteArrayValue());
      }
         
      return (result);         
   }

   /**
    * Retrieves a double value from the property data
    * 
    * @param type Type identifier
    * @return double value
    */      
   public double getDouble (Integer type)
   {
      double result = 0;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getDouble(item.byteArrayValue());
      }
         
      return (result);         
   }

   /**
    * Retrieves a timestamp from the property data
    * 
    * @param type Type identifier
    * @return timestamp
    */      
   public Date getTime (Integer type)
   {
      Date result = null;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getTime(item.byteArrayValue());
      }
         
      return (result);         
   }

   /**
    * Retrieves a boolean value from the property data
    * 
    * @param type Type identifier
    * @return boolean value
    */      
   public boolean getBoolean (Integer type)
   {
      boolean result = false;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = !(MPPUtility.getShort(item.byteArrayValue()) == 0);
      }
         
      return (result);         
   }

   /**
    * Retrieves a string value from the property data
    * 
    * @param type Type identifier
    * @return string value
    */      
   public String getUnicodeString (Integer type)
   {
      String result = null;
         
      ByteArray item = (ByteArray)m_map.get (type);         
      if (item != null)
      {
         result = MPPUtility.getUnicodeString(item.byteArrayValue());
      }
         
      return (result);
   }

   /**
    * This method dumps the contents of this properties block as a String.
    * Note that this facility is provided as a debugging aid.
    *
    * @return formatted contents of this block
    */
   public String toString ()
   {
      StringWriter sw = new StringWriter ();
      PrintWriter pw = new PrintWriter (sw);

      pw.println ("BEGIN Props");
      if (m_complete == true)
      {
         pw.println ("   COMPLETE");
      }
      else
      {
         pw.println ("   INCOMPLETE");         
      }
               
      Iterator iter = m_map.keySet().iterator();
      Integer key;
      
      while (iter.hasNext() == true)
      {
         key = (Integer)iter.next();
         pw.println ("   Key: " + key + " Value: " + MPPUtility.hexdump(((ByteArray)m_map.get(key)).byteArrayValue(), true));   
      }
           
      pw.println ("END Props");

      pw.println ();
      pw.close();
      return (sw.toString());
   }
   
   /**
    * Data types
    */
   public static final Integer CURRENCY_SYMBOL = new Integer (16);
   public static final Integer CURRENCY_PLACEMENT = new Integer (17);   
   public static final Integer CURRENCY_DIGITS = new Integer (18);
      
   public static final Integer DURATION_UNITS = new Integer (21);
   public static final Integer WORK_UNITS = new Integer (22);
   public static final Integer TASK_UPDATES_RESOURCE = new Integer (25);
   public static final Integer SPLIT_TASKS = new Integer (26);
   public static final Integer START_TIME = new Integer (28);
   public static final Integer HOURS_PER_DAY = new Integer (29);
   public static final Integer HOURS_PER_WEEK = new Integer (30);
   public static final Integer STANDARD_RATE = new Integer (31);
   public static final Integer OVERTIME_RATE = new Integer (32);
   public static final Integer END_TIME = new Integer (33);
   
   private boolean m_complete = true;
   private TreeMap m_map = new TreeMap (); 
}
