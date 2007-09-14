/*
 * file:       ViewType.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2002-2006
 * date:       27/01/2006
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

package net.sf.mpxj;

import net.sf.mpxj.utility.MpxjEnum;


/**
 * This class represents the enumeration of the valid types of view.
 */
public final class ViewType implements MpxjEnum
{
   /**
    * This constructor takes the numeric enumerated representation of a
    * view type and populates the class instance appropriately.
    * Note that unrecognised values are treated as "UNKNOWN".
    *
    * @param type int version of the view type
    */
   private ViewType (int type)
   {
      if (type < UNKNOWN_VALUE || type > RESOURCE_USAGE_VALUE)
      {
         m_value = UNKNOWN_VALUE;
      }
      else
      {
         m_value = type;
      }
   }

   /**
    * This method takes a numeric enumerated view type value
    * and populates the class instance appropriately. Note that unrecognised
    * values are treated as "UNKNOWN".
    *
    * @param type numeric enumerated view type
    * @return ViewType class instance
    */
   public static ViewType getInstance (int type)
   {
      if (type < UNKNOWN_VALUE || type > RESOURCE_USAGE_VALUE)
      {
         type = UNKNOWN_VALUE;
      }

      return (TYPE_VALUES[type]);
   }


   /**
    * Accessor method used to retrieve the numeric representation of the
    * view type.
    *
    * @return int representation of the view type
    */
   public int getValue ()
   {
      return (m_value);
   }

   /**
    * {@inheritDoc}
    */
   @Override public String toString ()
   {
      return (TYPE_NAMES[m_value]);
   }
   
   public static final int UNKNOWN_VALUE = 0;
   public static final int GANTT_CHART_VALUE = 1;
   public static final int NETWORK_DIAGRAM_VALUE = 2;
   public static final int RELATIONSHIP_DIAGRAM_VALUE = 3;
   public static final int TASK_FORM_VALUE = 4;
   public static final int TASK_SHEET_VALUE = 5;
   public static final int RESOURCE_FORM_VALUE = 6;
   public static final int RESOURCE_SHEET_VALUE = 7;
   public static final int RESOURCE_GRAPH_VALUE=8;
   public static final int TASK_DETAILS_FORM_VALUE = 10;
   public static final int TASK_NAME_FORM_VALUE = 11;
   public static final int RESOURCE_NAME_FORM_VALUE = 12;
   public static final int CALENDAR_VALUE = 13;
   public static final int TASK_USAGE_VALUE = 14;
   public static final int RESOURCE_USAGE_VALUE=15;

   public static final ViewType UNKNOWN = new ViewType (UNKNOWN_VALUE);
   public static final ViewType GANTT_CHART = new ViewType (GANTT_CHART_VALUE);
   public static final ViewType NETWORK_DIAGRAM = new ViewType (NETWORK_DIAGRAM_VALUE);
   public static final ViewType RELATIONSHIP_DIAGRAM = new ViewType (RELATIONSHIP_DIAGRAM_VALUE);
   public static final ViewType TASK_FORM = new ViewType (TASK_FORM_VALUE);
   public static final ViewType TASK_SHEET = new ViewType (TASK_SHEET_VALUE);
   public static final ViewType RESOURCE_FORM = new ViewType (RESOURCE_FORM_VALUE);
   public static final ViewType RESOURCE_SHEET = new ViewType (RESOURCE_SHEET_VALUE);
   public static final ViewType RESOURCE_GRAPH = new ViewType (RESOURCE_GRAPH_VALUE);
   public static final ViewType TASK_DETAILS_FORM = new ViewType (TASK_DETAILS_FORM_VALUE);
   public static final ViewType TASK_NAME_FORM = new ViewType (TASK_NAME_FORM_VALUE);
   public static final ViewType RESOURCE_NAME_FORM = new ViewType (RESOURCE_NAME_FORM_VALUE);   
   public static final ViewType CALENDAR = new ViewType (CALENDAR_VALUE);
   public static final ViewType TASK_USAGE = new ViewType (TASK_USAGE_VALUE);
   public static final ViewType RESOURCE_USAGE = new ViewType (RESOURCE_USAGE_VALUE);   
   
   /**
    * Array of type values matching the above constants.
    */
   private static final ViewType[] TYPE_VALUES =
   {
      UNKNOWN,
      GANTT_CHART,
      NETWORK_DIAGRAM,
      RELATIONSHIP_DIAGRAM,
      TASK_FORM,
      TASK_SHEET,
      RESOURCE_FORM,
      RESOURCE_SHEET,
      RESOURCE_GRAPH,
      UNKNOWN,
      TASK_DETAILS_FORM,
      TASK_NAME_FORM,
      RESOURCE_NAME_FORM,
      CALENDAR,
      TASK_USAGE,
      RESOURCE_USAGE
   };


   private static final String[] TYPE_NAMES =
   {
      "UNKNOWN",
      "GANTT_CHART",
      "NETWORK_DIAGRAM",
      "RELATIONSHIP_DIAGRAM",
      "TASK_FORM",
      "TASK_SHEET",
      "RESOURCE_FORM",
      "RESOURCE_SHEET",
      "RESOURCE_GRAPH",
      "UNKNOWN",
      "TASK_DETAILS_FORM",
      "TASK_NAME_FORM",
      "RESOURCE_NAME_FORM",
      "CALENDAR",
      "TASK_USAGE",
      "RESOURCE_USAGE"
   };
   
   /**
    * Internal representation of the view type.
    */
   private int m_value;
}
