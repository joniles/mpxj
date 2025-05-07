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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.mpxj.common.ByteArrayHelper;
import org.mpxj.common.InputStreamHelper;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import org.apache.poi.poifs.filesystem.DocumentInputStream;

import org.mpxj.Duration;
import org.mpxj.TimeUnit;
import org.mpxj.common.AutoCloseableHelper;
import org.mpxj.mpp.MPPUtility;

/**
 * Implements the controller component of the HexDump MVC.
 */
public class HexDumpController
{
   private static final char[] HEX_DIGITS =
   {
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
      byte[] data = m_model.getData();
      int offset = m_model.getOffset();
      Point selectedCell = m_model.getSelectedCell();

      DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");
      DateTimeFormatter timestampFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

      int selectionIndex = (selectedCell.y * m_model.getColumns()) + selectedCell.x;
      String selectionLabel = selectedCell.y + "," + selectedCell.x;
      String differenceLabel = Integer.toString(Math.abs(m_model.getCurrentSelectionIndex() - selectionIndex));

      String shortValueLabel = "";
      String longSixValueLabel = "";
      String longValueLabel = "";
      String doubleValueLabel = "";
      String durationValueLabel = "";
      String timeUnitsValueLabel = "";
      String guidValueLabel = "";
      String percentageValueLabel = "";
      String dateValueLabel = "";
      String timeValueLabel = "";
      String timestampValueLabel = "";
      String workUnitsValueLabel;

      if (selectionIndex + offset + 2 <= data.length)
      {
         shortValueLabel = Integer.toString(ByteArrayHelper.getShort(data, selectionIndex + offset));
         timeUnitsValueLabel = MPPUtility.getDurationTimeUnits(ByteArrayHelper.getShort(data, selectionIndex + offset)).toString();

         Double value = MPPUtility.getPercentage(data, selectionIndex + offset);
         if (value != null)
         {
            percentageValueLabel = value.toString();
         }

         LocalDateTime date = MPPUtility.getDate(data, selectionIndex + offset);
         if (date != null)
         {
            dateValueLabel = dateFormat.format(date);
         }

         timeValueLabel = timeFormat.format(MPPUtility.getTime(data, selectionIndex + offset));
      }

      //
      // 1 byte
      //
      workUnitsValueLabel = MPPUtility.getWorkTimeUnits(MPPUtility.getByte(data, selectionIndex + offset)).toString();

      //
      // 4 bytes
      //
      if (selectionIndex + offset + 4 <= data.length)
      {
         LocalDateTime timestamp = MPPUtility.getTimestamp(data, selectionIndex + offset);
         if (timestamp != null)
         {
            timestampValueLabel = timestampFormat.format(timestamp);
         }
      }

      //
      // 6 bytes
      //
      if (selectionIndex + offset + 6 <= data.length)
      {
         longSixValueLabel = Long.toString(MPPUtility.getLong6(data, selectionIndex + offset));
      }

      //
      // 8 bytes
      //
      if (selectionIndex + offset + 8 <= data.length)
      {
         longValueLabel = Long.toString(ByteArrayHelper.getLong(data, selectionIndex + offset));
         doubleValueLabel = Double.toString(MPPUtility.getDouble(data, selectionIndex + offset));
         durationValueLabel = Duration.getInstance(MPPUtility.getDouble(data, selectionIndex + offset) / 60000, TimeUnit.HOURS).toString();
      }

      //
      // 16 bytes
      //
      if (selectionIndex + offset + 16 <= data.length)
      {
         guidValueLabel = MPPUtility.getGUID(data, selectionIndex + offset).toString().toUpperCase();
      }

      m_model.setPreviousSelectionIndex(m_model.getCurrentSelectionIndex());
      m_model.setCurrentSelectionIndex(selectionIndex);
      m_model.setPreviousSelectionValueLabel(m_model.getCurrentSelectionValueLabel());
      m_model.setCurrentSelectionValueLabel(selectionLabel);
      m_model.setSelectionDifferenceValueLabel(differenceLabel);
      m_model.setShortValueLabel(shortValueLabel);
      m_model.setLongSixValueLabel(longSixValueLabel);
      m_model.setLongValueLabel(longValueLabel);
      m_model.setDoubleValueLabel(doubleValueLabel);
      m_model.setDurationValueLabel(durationValueLabel);
      m_model.setTimeUnitsValueLabel(timeUnitsValueLabel);
      m_model.setGuidValueLabel(guidValueLabel);
      m_model.setPercentageValueLabel(percentageValueLabel);
      m_model.setDateValueLabel(dateValueLabel);
      m_model.setTimeValueLabel(timeValueLabel);
      m_model.setTimestampValueLabel(timestampValueLabel);
      m_model.setWorkUnitsValueLabel(workUnitsValueLabel);
   }
}
