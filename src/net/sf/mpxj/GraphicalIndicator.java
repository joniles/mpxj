/*
 * file:       GraphicalIndicator.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       16-Feb-2006
 */
 
package net.sf.mpxj;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents the set of information which defines how
 * a Graphical Indicator will be presented for a single column in
 * a table within Microsoft Project.
 */
public class GraphicalIndicator
{
   /**
    * Sets the field type to which this indicator applies.
    * 
    * @param fieldType field type
    */
   public void setFieldType (FieldType fieldType)
   {
      m_fieldType = fieldType;
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
   public List getNonSummaryRowCriteria()
   {
      return (m_nonSummaryRowCriteria);
   }

   /**
    * Retrieve the criteria to be applied to the project summary.
    * 
    * @return list of project summary criteria
    */
   public List getProjectSummaryCriteria()
   {
      return (m_projectSummaryCriteria);
   }

   /**
    * Retrieve the criteria to be applied to summary rows.
    * 
    * @return list of summary row criteria
    */
   public List getSummaryRowCriteria()
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
   public void addNonSummaryRowCriteria (GraphicalIndicatorCriteria criteria)
   {
      m_nonSummaryRowCriteria.add(criteria);
   }

   /**
    * Add criteria relating to summary rows.
    * 
    * @param criteria indicator criteria
    */   
   public void addSummaryRowCriteria (GraphicalIndicatorCriteria criteria)
   {
      m_summaryRowCriteria.add(criteria);
   }

   /**
    * Add criteria relating to project summary.
    * 
    * @param criteria indicator criteria
    */   
   public void addProjectSummaryCriteria (GraphicalIndicatorCriteria criteria)
   {
      m_projectSummaryCriteria.add(criteria);
   }
   
   /**
    * {@inheritDoc}
    */
   public String toString ()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter (os);
      pw.println("[GraphicalIndicator");
      pw.println(" FieldType=" + m_fieldType);
      pw.println(" DisplayGraphicalIndicators=" + m_displayGraphicalIndicators);
      pw.println(" SummaryRowsInheritFromNonSummaryRows=" + m_summaryRowsInheritFromNonSummaryRows);
      pw.println(" ProjectSummaryInheritsFromSummaryRows=" + m_projectSummaryInheritsFromSummaryRows);
      pw.println(" ShowDataValuesInToolTips=" + m_showDataValuesInToolTips);
      pw.println(" NonSummaryRowCriteria=");
      Iterator iter = m_nonSummaryRowCriteria.iterator();
      while (iter.hasNext() == true)
      {
         pw.println("  " + iter.next());
      }
      pw.println(" SummaryRowCriteria=");      
      iter = m_summaryRowCriteria.iterator();
      while (iter.hasNext() == true)
      {
         pw.println("  " + iter.next());
      }
      pw.println(" ProjectSummaryCriteria=");      
      iter = m_projectSummaryCriteria.iterator();
      while (iter.hasNext() == true)
      {
         pw.println("  " + iter.next());
      }      
      pw.println("]");
      pw.flush();
      return (os.toString());     
   }
   
   private FieldType m_fieldType;
   private boolean m_displayGraphicalIndicators;
   private boolean m_summaryRowsInheritFromNonSummaryRows;
   private boolean m_projectSummaryInheritsFromSummaryRows;
   private boolean m_showDataValuesInToolTips;
   private List m_nonSummaryRowCriteria = new LinkedList();
   private List m_summaryRowCriteria = new LinkedList();
   private List m_projectSummaryCriteria = new LinkedList();
}
