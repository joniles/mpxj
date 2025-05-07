/*
 * file:       GraphicalIndicator.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2005
 * date:       16/02/2006
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

package org.mpxj;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.mpxj.common.NumberHelper;

/**
 * This class represents the set of information which defines how
 * a Graphical Indicator will be presented for a single column in
 * a table within Microsoft Project.
 */
public class GraphicalIndicator
{
   /**
    * Constructor.
    *
    * @param fieldType field type
    */
   GraphicalIndicator(FieldType fieldType)
   {
      m_fieldType = fieldType;
   }

   /**
    * This method evaluates if a graphical indicator should
    * be displayed, given a set of Task or Resource data. The
    * method will return -1 if no indicator should be displayed.
    *
    * @param container Task or Resource instance
    * @return indicator index
    */
   public int evaluate(FieldContainer container)
   {
      //
      // First step - determine the list of criteria we are should use
      //
      List<GraphicalIndicatorCriteria> criteria;
      if (container instanceof Task)
      {
         Task task = (Task) container;
         if (NumberHelper.getInt(task.getUniqueID()) == 0)
         {
            if (!m_projectSummaryInheritsFromSummaryRows)
            {
               criteria = m_projectSummaryCriteria;
            }
            else
            {
               if (!m_summaryRowsInheritFromNonSummaryRows)
               {
                  criteria = m_summaryRowCriteria;
               }
               else
               {
                  criteria = m_nonSummaryRowCriteria;
               }
            }
         }
         else
         {
            if (task.getSummary())
            {
               if (!m_summaryRowsInheritFromNonSummaryRows)
               {
                  criteria = m_summaryRowCriteria;
               }
               else
               {
                  criteria = m_nonSummaryRowCriteria;
               }
            }
            else
            {
               criteria = m_nonSummaryRowCriteria;
            }
         }
      }
      else
      {
         // It is possible to have a resource summary row, but at the moment
         // I can't see how you can determine this.
         criteria = m_nonSummaryRowCriteria;
      }

      //
      // Now we have the criteria, evaluate each one until we get a result
      //
      int result = -1;
      for (GraphicalIndicatorCriteria gic : criteria)
      {
         result = gic.evaluate(container);
         if (result != -1)
         {
            break;
         }
      }

      //
      // If we still don't have a result at the end, return the
      // default value, which is 0
      //
      if (result == -1)
      {
         result = 0;
      }

      return (result);
   }

   /**
    * Retrieves the field type to which this indicator applies.
    *
    * @return field type
    */
   public FieldType getFieldType()
   {
      return (m_fieldType);
   }

   /**
    * Retrieves a flag indicating if graphical indicators should be displayed
    * for this column, rather than the actual values.
    *
    * @return boolean flag
    */
   public boolean getDisplayGraphicalIndicators()
   {
      return (m_displayGraphicalIndicators);
   }

   /**
    * Sets a flag indicating if graphical indicators should be displayed
    * for this column, rather than the actual values.
    *
    * @param displayGraphicalIndicators boolean flag
    */
   public void setDisplayGraphicalIndicators(boolean displayGraphicalIndicators)
   {
      m_displayGraphicalIndicators = displayGraphicalIndicators;
   }

   /**
    * Retrieve the criteria to be applied to non-summary rows.
    *
    * @return list of non-summary row criteria
    */
   public List<GraphicalIndicatorCriteria> getNonSummaryRowCriteria()
   {
      return (m_nonSummaryRowCriteria);
   }

   /**
    * Retrieve the criteria to be applied to the project summary.
    *
    * @return list of project summary criteria
    */
   public List<GraphicalIndicatorCriteria> getProjectSummaryCriteria()
   {
      return (m_projectSummaryCriteria);
   }

   /**
    * Retrieve the criteria to be applied to summary rows.
    *
    * @return list of summary row criteria
    */
   public List<GraphicalIndicatorCriteria> getSummaryRowCriteria()
   {
      return m_summaryRowCriteria;
   }

   /**
    * Retrieves a flag which indicates if the project summary row inherits
    * criteria from the summary row.
    *
    * @return boolean flag
    */
   public boolean getProjectSummaryInheritsFromSummaryRows()
   {
      return (m_projectSummaryInheritsFromSummaryRows);
   }

   /**
    * Sets a flag which indicates if the project summary row inherits
    * criteria from the summary row.
    *
    * @param projectSummaryInheritsFromSummaryRows boolean flag
    */
   public void setProjectSummaryInheritsFromSummaryRows(boolean projectSummaryInheritsFromSummaryRows)
   {
      m_projectSummaryInheritsFromSummaryRows = projectSummaryInheritsFromSummaryRows;
   }

   /**
    * Retrieves a flag which indicates if summary rows inherit
    * criteria from non-summary rows.
    *
    * @return boolean flag
    */
   public boolean getSummaryRowsInheritFromNonSummaryRows()
   {
      return (m_summaryRowsInheritFromNonSummaryRows);
   }

   /**
    * Sets a flag which indicates if summary rows inherit
    * criteria from non-summary rows.
    *
    * @param summaryRowsInheritFromNonSummaryRows boolean flag
    */
   public void setSummaryRowsInheritFromNonSummaryRows(boolean summaryRowsInheritFromNonSummaryRows)
   {
      m_summaryRowsInheritFromNonSummaryRows = summaryRowsInheritFromNonSummaryRows;
   }

   /**
    * Retrieve the flag which indicates that data values should be shown
    * as tool tips.
    *
    * @return boolean flag
    */
   public boolean getShowDataValuesInToolTips()
   {
      return (m_showDataValuesInToolTips);
   }

   /**
    * Set the flag which indicates that data values should be shown
    * as tool tips.
    *
    * @param showDataValuesInToolTips boolean flag
    */
   public void setShowDataValuesInToolTips(boolean showDataValuesInToolTips)
   {
      m_showDataValuesInToolTips = showDataValuesInToolTips;
   }

   /**
    * Add criteria relating to non summary rows.
    *
    * @param criteria indicator criteria
    */
   public void addNonSummaryRowCriteria(GraphicalIndicatorCriteria criteria)
   {
      m_nonSummaryRowCriteria.add(criteria);
   }

   /**
    * Add criteria relating to summary rows.
    *
    * @param criteria indicator criteria
    */
   public void addSummaryRowCriteria(GraphicalIndicatorCriteria criteria)
   {
      m_summaryRowCriteria.add(criteria);
   }

   /**
    * Add criteria relating to project summary.
    *
    * @param criteria indicator criteria
    */
   public void addProjectSummaryCriteria(GraphicalIndicatorCriteria criteria)
   {
      m_projectSummaryCriteria.add(criteria);
   }

   @Override public String toString()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(os);
      pw.println("[GraphicalIndicator");
      pw.println(" FieldType=" + m_fieldType);
      pw.println(" DisplayGraphicalIndicators=" + m_displayGraphicalIndicators);
      pw.println(" SummaryRowsInheritFromNonSummaryRows=" + m_summaryRowsInheritFromNonSummaryRows);
      pw.println(" ProjectSummaryInheritsFromSummaryRows=" + m_projectSummaryInheritsFromSummaryRows);
      pw.println(" ShowDataValuesInToolTips=" + m_showDataValuesInToolTips);
      pw.println(" NonSummaryRowCriteria=");
      for (GraphicalIndicatorCriteria gi : m_nonSummaryRowCriteria)
      {
         pw.println("  " + gi);
      }
      pw.println(" SummaryRowCriteria=");
      for (GraphicalIndicatorCriteria gi : m_summaryRowCriteria)
      {
         pw.println("  " + gi);
      }
      pw.println(" ProjectSummaryCriteria=");
      for (GraphicalIndicatorCriteria gi : m_projectSummaryCriteria)
      {
         pw.println("  " + gi);
      }
      pw.println("]");
      pw.flush();
      return (os.toString());
   }

   private final FieldType m_fieldType;
   private boolean m_displayGraphicalIndicators;
   private boolean m_summaryRowsInheritFromNonSummaryRows;
   private boolean m_projectSummaryInheritsFromSummaryRows;
   private boolean m_showDataValuesInToolTips;
   private final List<GraphicalIndicatorCriteria> m_nonSummaryRowCriteria = new ArrayList<>();
   private final List<GraphicalIndicatorCriteria> m_summaryRowCriteria = new ArrayList<>();
   private final List<GraphicalIndicatorCriteria> m_projectSummaryCriteria = new ArrayList<>();
}
