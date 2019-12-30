/*
 * file:       CustomFieldValueReader.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2015
 * date:       28/04/2015
 */

/*
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */

package net.sf.mpxj.mpp;

import java.io.IOException;

import net.sf.mpxj.CustomFieldContainer;
import net.sf.mpxj.CustomFieldValueDataType;
import net.sf.mpxj.ProjectProperties;
import net.sf.mpxj.TimeUnit;
import net.sf.mpxj.common.ByteArrayHelper;

/**
 * Common implementation detail shared by custom field value readers.
 */
public abstract class CustomFieldValueReader
{
   /**
    * Constructor.
    *
    * @param properties project properties
    * @param container custom field config
    * @param outlineCodeVarMeta raw mpp data
    * @param outlineCodeVarData raw mpp data
    * @param outlineCodeFixedData raw mpp data
    * @param outlineCodeFixedData2 raw mpp data
    * @param taskProps raw mpp data
    */
   public CustomFieldValueReader(ProjectProperties properties, CustomFieldContainer container, VarMeta outlineCodeVarMeta, Var2Data outlineCodeVarData, FixedData outlineCodeFixedData, FixedData outlineCodeFixedData2, Props taskProps)
   {
      m_properties = properties;
      m_container = container;
      m_outlineCodeVarMeta = outlineCodeVarMeta;
      m_outlineCodeVarData = outlineCodeVarData;
      m_outlineCodeFixedData = outlineCodeFixedData;
      m_outlineCodeFixedData2 = outlineCodeFixedData2;
      m_taskProps = taskProps;
   }

   /**
    * Method implement by subclasses to read custom field values.
    */
   public abstract void process() throws IOException;

   /**
    * Convert raw value as read from the MPP file into a Java type.
    *
    * @param type MPP value type
    * @param value raw value data
    * @return Java object
    */
   protected Object getTypedValue(CustomFieldValueDataType type, byte[] value)
   {
      Object result;

      if (type == null)
      {
         result = valueAsString(value);
      }
      else
      {
         switch (type)
         {
            case DATE:
            case FINISH_DATE:
            {
               result = MPPUtility.getTimestamp(value, 0);
               break;
            }

            case DURATION:
            {
               TimeUnit units = MPPUtility.getDurationTimeUnits(MPPUtility.getShort(value, 4), m_properties.getDefaultDurationUnits());
               result = MPPUtility.getAdjustedDuration(m_properties, MPPUtility.getInt(value, 0), units);
               break;
            }

            case COST:
            {
               result = Double.valueOf(MPPUtility.getDouble(value, 0) / 100);
               break;
            }

            case NUMBER:
            {
               result = Double.valueOf(MPPUtility.getDouble(value, 0));
               break;
            }

            case TEXT:
            {
               result = MPPUtility.getUnicodeString(value, 0);
               break;
            }

            default:
            {
               result = valueAsString(value);
               break;
            }
         }
      }

      return result;
   }

   /**
    * Try to convert a bute array into a string. In the event of a
    * failure, fall back to dumping the byte array contents as
    * as string of hex bytes.
    *
    * @param value byte array
    * @return String instance
    */
   private String valueAsString(byte[] value)
   {
      String result;

      //
      // We don't know what this is, let's try making a string
      //
      try
      {
         result = MPPUtility.getUnicodeString(value, 0);
      }

      catch (Exception ex)
      {
         //
         // Handle failure gracefully and dump the byte array contents
         //
         result = ByteArrayHelper.hexdump(value, false);
      }
      return result;
   }

   protected ProjectProperties m_properties;
   protected CustomFieldContainer m_container;
   protected VarMeta m_outlineCodeVarMeta;
   protected Var2Data m_outlineCodeVarData;
   protected FixedData m_outlineCodeFixedData;
   protected FixedData m_outlineCodeFixedData2;
   protected Props m_taskProps;

   public static final Integer VALUE_LIST_VALUE = Integer.valueOf(22);
   public static final Integer VALUE_LIST_DESCRIPTION = Integer.valueOf(8);
   public static final Integer VALUE_LIST_UNKNOWN = Integer.valueOf(23);
}
