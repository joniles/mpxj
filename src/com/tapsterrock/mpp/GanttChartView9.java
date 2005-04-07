/*
 * file:       GanttChartView9.java
 * author:     Jon Iles
 * copyright:  (c) Tapster Rock Limited 2005
 * date:       Apr 7, 2005
 */
 
package com.tapsterrock.mpp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class represents the set of properties used to define the appearance
 * of a Gantt chart view in MS Project.
 */
public class GanttChartView9 extends View9
{
   /**
    * Create a GanttChartView from the dixed and var data blocks associated 
    * with a view.
    * 
    * @param fixedData fixed data block
    * @param varData var data block
    * @throws IOException
    */
   public GanttChartView9 (byte[] fixedData, Var2Data varData)
      throws IOException
   {
      super (fixedData);
      
      Props9 props = new Props9(new ByteArrayInputStream(varData.getByteArray(m_id, PROPERTIES)));      

      if (props != null)
      {
         byte[] viewPropertyData = props.getByteArray(VIEW_PROPERTIES);
   
         if (viewPropertyData != null)
         {
            m_sheetRowsGridLines = new GridLines(viewPropertyData, 99);
            m_sheetColumnsGridLines = new GridLines(viewPropertyData, 109);
            m_titleVerticalGridLines = new GridLines(viewPropertyData, 119);
            m_titleHorizontalGridLines = new GridLines(viewPropertyData, 129);
            m_majorColumnsGridLines = new GridLines(viewPropertyData, 139);
            m_minorColumnsGridLines = new GridLines(viewPropertyData, 149);
            m_ganttRowsGridLines = new GridLines(viewPropertyData, 159);
            m_barRowsGridLines = new GridLines(viewPropertyData, 169);
            m_currentDateGridLines = new GridLines(viewPropertyData, 179);
            m_pageBreakGridLines = new GridLines(viewPropertyData, 189);
            m_projectStartGridLines = new GridLines(viewPropertyData, 199);
            m_projectFinishGridLines = new GridLines(viewPropertyData, 209);
            m_statusDateGridLines = new GridLines(viewPropertyData, 219);
            
            m_nonWorkingDaysCalendarName = MPPUtility.getUnicodeString(viewPropertyData, 352);
            
            m_ganttBarHeight = mapGanttBarHeight(MPPUtility.getByte(viewPropertyData, 1163));
         }
      }
      
      //key = 574619661, 38 bytes per task bar, only modified task bars appear here, first 4 bytes are the task UID

      // byte 228 - bit flags, bit 4 is major scale use FY, bit 1 is major scale tick lines, but 5 minor scale use FY, bit 2 is minor scale tick lines, bit 3 is scale separator
      
      // byte 242 - major scale units
      
      // byte 244 - minor scale units
      
      // byte 246 - major scale count
      
      // byte 248 - minor scale count
      
      // byte 250 - major scale label format
      
      // byte 252 - minor scale label format
      
      // byte 254 - minor scale align
      
      // byte 256 - major scale align
      
      // byte 268 - timescale size percent
      
      // the last section of this block is in two parts. The first part
      // is a set of 58 byte blocks representing the default formats for the
      // gantt bars, the second part is the names associated with each of these types
      // the 58 byte blocks seem to start at offset 1190, is this fixed?
      
      // at offset 350 there seems to be a 2 byte integer which states
      // the remaining size of the data block.

      // byte 1152 represents the non-working time draw option
      
      // byte 1153 represents the non-working time color
      
      // byte 1154 represents the non-working time pattern
      
      // byte 1155 represents the link style
      
      // byte 1156 represents the show drawings flag
      
      // byte 1158 represents the round bars to whole days flag
      
      // byte 1160 show bar splits flag
      
      // at offset 1161 is a two byte count containing the number of bar types that have been defined
      
      // byte 1182 represents the date style - need to check how many bytes are actually used
      
      // byte 1186 represents the always roll up gantt bars flag
      
      // byte 1188 represents the hide rollup bars when summary expanded flag
      
      // progress lines are not stored in this block
      
      //byte[] unknown = props.getByteArray(new Integer (574619656));
      //System.out.println (MPPUtility.hexdump(unknown, true, 16));
      
   }

   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */
   public GridLines getSheetColumnsGridLines()
   {
      return (m_sheetColumnsGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */   
   public GridLines getSheetRowsGridLines()
   {
      return (m_sheetRowsGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */   
   public GridLines getStatusDateGridLines()
   {
      return (m_statusDateGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */   
   public GridLines getTitleHorizontalGridLines()
   {
      return (m_titleHorizontalGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */   
   public GridLines getTitleVerticalGridLines()
   {
      return (m_titleVerticalGridLines);
   }

   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getBarRowsGridLines()
   {
      return (m_barRowsGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */   
   public GridLines getCurrentDateGridLines()
   {
      return (m_currentDateGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getGanttRowsGridLines()
   {
      return (m_ganttRowsGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getMajorColumnsGridLines()
   {
      return (m_majorColumnsGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getMinorColumnsGridLines()
   {
      return (m_minorColumnsGridLines);
   }
   
   /**
    * Retrieve the name of the calendar used to define non-working days for
    * this view..
    * 
    * @return calendar name
    */      
   public String getNonWorkingDaysCalendarName()
   {
      return (m_nonWorkingDaysCalendarName);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getPageBreakGridLines()
   {
      return (m_pageBreakGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getProjectFinishGridLines()
   {
      return (m_projectFinishGridLines);
   }
   
   /**
    * Retrieve a grid lines definition.
    * 
    * @return grid lines definition
    */      
   public GridLines getProjectStartGridLines()
   {
      return (m_projectStartGridLines);
   }
   
   /**
    * Retrieve the height of the Gantt bars in this view.
    * 
    * @return Gantt bar height
    */
   public int getGanttBarHeight()
   {
      return (m_ganttBarHeight);
   }
   
   /**
    * This method maps the encoded height of a Gantt bar to
    * the height in pixels.
    * 
    * @param height encoded height
    * @return height in pixels
    */
   private int mapGanttBarHeight (int height)
   {
      switch (height)
      {
         case 0:
         {
            height = 6;
            break;
         }

         case 1:
         {
            height = 8;
            break;
         }

         case 2:
         {
            height = 10;
            break;
         }

         case 3:
         {
            height = 12;
            break;
         }
         
         case 4:
         {
            height = 14;
            break;
         }
         
         case 5:
         {
            height = 18;
            break;
         }
         
         case 6:
         {
            height = 24;
            break;
         }         
      }
      
      return (height);
   }
   
   /**
    * Generate a string representation of this instance.
    * 
    * @return string representation of this instance
    */   
   public String toString ()
   {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter (os);
      pw.println ("[GanttChartView");
      pw.println ("   " + super.toString());
      
      pw.println ("   SheetRowsGridLines=" + m_sheetRowsGridLines);
      pw.println ("   SheetColumnsGridLines=" + m_sheetColumnsGridLines);
      pw.println ("   TitleVerticalGridLines=" + m_titleVerticalGridLines);
      pw.println ("   TitleHorizontalGridLines=" + m_titleHorizontalGridLines);
      pw.println ("   MajorColumnsGridLines=" + m_majorColumnsGridLines);
      pw.println ("   MinorColumnsGridLines=" + m_minorColumnsGridLines);
      pw.println ("   GanttRowsGridLines=" + m_ganttRowsGridLines);
      pw.println ("   BarRowsGridLines=" + m_barRowsGridLines);
      pw.println ("   CurrentDateGridLines=" + m_currentDateGridLines);
      pw.println ("   PageBreakGridLines=" + m_pageBreakGridLines);
      pw.println ("   ProjectStartGridLines=" + m_projectStartGridLines);
      pw.println ("   ProjectFinishGridLines=" + m_projectFinishGridLines);
      pw.println ("   StatusDateGridLines=" + m_statusDateGridLines);
      pw.println ("   NonWorkingDaysCalendarName=" + m_nonWorkingDaysCalendarName);
      pw.println ("   GanttBarHeight=" + m_ganttBarHeight);
      pw.println ("]");
      pw.flush();
      return (os.toString());
   }
   
   private GridLines m_sheetRowsGridLines;
   private GridLines m_sheetColumnsGridLines;
   private GridLines m_titleVerticalGridLines;
   private GridLines m_titleHorizontalGridLines;
   private GridLines m_majorColumnsGridLines;
   private GridLines m_minorColumnsGridLines;
   private GridLines m_ganttRowsGridLines;
   private GridLines m_barRowsGridLines;
   private GridLines m_currentDateGridLines;
   private GridLines m_pageBreakGridLines;
   private GridLines m_projectStartGridLines;
   private GridLines m_projectFinishGridLines;
   private GridLines m_statusDateGridLines;

   private String m_nonWorkingDaysCalendarName;
   private int m_ganttBarHeight;
   
   private static final Integer PROPERTIES = new Integer (1);
   private static final Integer VIEW_PROPERTIES = new Integer (574619656);
}
