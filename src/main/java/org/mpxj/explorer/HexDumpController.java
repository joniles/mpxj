/*
 * file:       HexDumpController.java
 * author:     Jon Iles
 * copyright:  (c) Packwood Software 2014
 * date:       06/07/2014
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

package org.mpxj.explorer;

import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.common.InputStreamHelper;
import org.mpxj.mpp.DataAtOffset;

/**
 * Implements the controller component of the HexDump MVC.
 */
class HexDumpController
{
   private static final char[] HEX_DIGITS = {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'A',
      'B',
      'C',
      'D',
      'E',
      'F'
   };

   private final HexDumpModel m_model;

   /**
    * Constructor.
    *
    * @param model HexDump model
    */
   public HexDumpController(HexDumpModel model)
   {
      m_model = model;

      model.addPropertyChangeListener("columns", evt -> updateTables());

      model.addPropertyChangeListener("offset", evt -> updateTables());

      model.addPropertyChangeListener("selectedCell", evt -> updateSelection());
   }

   /**
    * Command to select a document from the POIFS for viewing.
    *
    * @param entry document to view
    */
   public void viewDocument(DocumentEntry entry)
   {
      InputStream is = null;

      try
      {
         is = new DocumentInputStream(entry);
         byte[] data = InputStreamHelper.readAvailable(is);
         m_model.setData(data);
         updateTables();
      }

      catch (IOException ex)
      {
         throw new RuntimeException(ex);
      }

      finally
      {
         AutoCloseableHelper.closeQuietly(is);
      }
   }

   /**
    * Update the content of the tables.
    */
   protected void updateTables()
   {
      byte[] data = m_model.getData();
      int columns = m_model.getColumns();
      int rows = (data.length / columns) + 1;
      int offset = m_model.getOffset();

      String[][] hexData = new String[rows][columns];
      String[][] asciiData = new String[rows][columns];

      int row = 0;
      int column = 0;
      StringBuilder hexValue = new StringBuilder();
      for (int index = offset; index < data.length; index++)
      {
         int value = data[index];
         hexValue.setLength(0);
         hexValue.append(HEX_DIGITS[(value & 0xF0) >> 4]);
         hexValue.append(HEX_DIGITS[value & 0x0F]);

         char c = (char) value;
         if ((c > 200) || (c < 27))
         {
            c = ' ';
         }

         hexData[row][column] = hexValue.toString();
         asciiData[row][column] = Character.toString(c);

         ++column;
         if (column == columns)
         {
            column = 0;
            ++row;
         }
      }

      String[] columnHeadings = new String[columns];
      TableModel hexTableModel = new DefaultTableModel(hexData, columnHeadings)
      {
         @Override public boolean isCellEditable(int r, int c)
         {
            return false;
         }
      };

      TableModel asciiTableModel = new DefaultTableModel(asciiData, columnHeadings)
      {
         @Override public boolean isCellEditable(int r, int c)
         {
            return false;
         }
      };

      m_model.setSizeValueLabel(Integer.toString(data.length));
      m_model.setHexTableModel(hexTableModel);
      m_model.setAsciiTableModel(asciiTableModel);
      m_model.setCurrentSelectionIndex(0);
      m_model.setPreviousSelectionIndex(0);
   }

   /**
    * Updates the selection information.
    */
   protected void updateSelection()
   {
      Point selectedCell = m_model.getSelectedCell();
      int selectionIndex = (selectedCell.y * m_model.getColumns()) + selectedCell.x;
      DataAtOffset data = new DataAtOffset(m_model.getData(), selectionIndex + m_model.getOffset());

      m_model.setPreviousSelectionIndex(m_model.getCurrentSelectionIndex());
      m_model.setCurrentSelectionIndex(selectionIndex);
      m_model.setPreviousSelectionValueLabel(m_model.getCurrentSelectionValueLabel());
      m_model.setCurrentSelectionValueLabel(selectedCell.y + "," + selectedCell.x);
      m_model.setSelectionDifferenceValueLabel(Integer.toString(Math.abs(m_model.getCurrentSelectionIndex() - selectionIndex)));
      m_model.setShortValueLabel(getLabel(data.getShort()));
      m_model.setLongSixValueLabel(getLabel(data.getLongSix()));
      m_model.setLongValueLabel(getLabel(data.getLong()));
      m_model.setDoubleValueLabel(getLabel(data.getDouble()));
      m_model.setDurationValueLabel(getLabel(data.getDuration()));
      m_model.setTimeUnitsValueLabel(getLabel(data.getTimeUnit()));
      m_model.setGuidValueLabel(getLabel(data.getGuid()));
      m_model.setPercentageValueLabel(getLabel(data.getPercentage()));
      m_model.setDateValueLabel(getDateLabel(data.getDate()));
      m_model.setTimeValueLabel(getTimeLabel(data.getTime()));
      m_model.setTimestampValueLabel(getDateTimeLabel(data.getTimestamp()));
      m_model.setWorkUnitsValueLabel(getLabel(data.getWorkTimeUnit()));
   }

   private String getLabel(Object o)
   {
      return o == null ? "" : o.toString();
   }

   private String getDateLabel(LocalDate o)
   {
      return o == null ? "" : DATE_FORMAT.format(o);
   }

   private String getTimeLabel(LocalTime o)
   {
      return o == null ? "" : TIME_FORMAT.format(o);
   }

   private String getDateTimeLabel(LocalDateTime o)
   {
      return o == null ? "" : TIMESTAMP_FORMAT.format(o);
   }

   private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
   private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
   private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
}
