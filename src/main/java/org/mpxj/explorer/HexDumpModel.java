/*
 * file:       HexDumpModel.java
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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/**
 * Implements the model component of the HexDump MVC.
 */
public class HexDumpModel
{
   private final PropertyChangeSupport m_changeSupport = new PropertyChangeSupport(this);
   private byte[] m_data = new byte[0];
   private TableModel m_hexTableModel = new DefaultTableModel();
   private TableModel m_asciiTableModel = new DefaultTableModel();
   private int m_columns = 16;
   private int m_offset;
   private String m_sizeValueLabel;
   private int m_currentSelectionIndex;
   private int m_previousSelectionIndex;
   private String m_previousSelectionValueLabel;
   private String m_currentSelectionValueLabel;
   private String m_selectionDifferenceValueLabel;
   private Point m_selectedCell;
   private String m_shortValueLabel;
   private String m_longSixValueLabel;
   private String m_longValueLabel;
   private String m_doubleValueLabel;
   private String m_durationValueLabel;
   private String m_timeUnitsValueLabel;
   private String m_guidValueLabel;
   private String m_percentageValueLabel;
   private String m_dateValueLabel;
   private String m_timeValueLabel;
   private String m_timestampValueLabel;
   private String m_workUnitsValueLabel;

   /**
    * Set the file size.
    *
    * @param size file size
    */
   public void setSizeValueLabel(String size)
   {
      m_changeSupport.firePropertyChange("sizeValueLabel", m_sizeValueLabel, m_sizeValueLabel = size);
   }

   /**
    * Retrieve the file size.
    *
    * @return file size
    */
   public String getSizeValueLabel()
   {
      return m_sizeValueLabel;
   }

   /**
    * Retrieve the number of columns in the view tables.
    *
    * @return number of columns
    */
   public int getColumns()
   {
      return m_columns;
   }

   /**
    * Set the number of columns in the view tables.
    *
    * @param columns number of columns
    */
   public void setColumns(int columns)
   {
      m_changeSupport.firePropertyChange("columns", m_columns, m_columns = columns);
   }

   /**
    * Retrieve the offset into the file at which the display starts.
    *
    * @return offset
    */
   public int getOffset()
   {
      return m_offset;
   }

   /**
    * Set the offset into the file at which the display starts.
    *
    * @param offset offset
    */
   public void setOffset(int offset)
   {
      m_changeSupport.firePropertyChange("offset", m_offset, m_offset = offset);
   }

   /**
    * Set the contents of the file represented as a byte array.
    *
    * @param data file contents
    */
   public void setData(byte[] data)
   {
      m_changeSupport.firePropertyChange("data", m_data, m_data = data);
   }

   /**
    * Retrieve the contents of the file represented as a byte array.
    *
    * @return file contents
    */
   public byte[] getData()
   {
      return m_data;
   }

   /**
    * Set the model used by the hex table.
    *
    * @param tableModel table model
    */
   public void setHexTableModel(TableModel tableModel)
   {
      m_changeSupport.firePropertyChange("hexTableModel", m_hexTableModel, m_hexTableModel = tableModel);
   }

   /**
    * Retrieve the model used by the hex table.
    *
    * @return table model
    */
   public TableModel getHexTableModel()
   {
      return m_hexTableModel;
   }

   /**
    * Set the model used by the ASCII table.
    *
    * @param tableModel table model
    */
   public void setAsciiTableModel(TableModel tableModel)
   {
      m_changeSupport.firePropertyChange("asciiTableModel", m_asciiTableModel, m_asciiTableModel = tableModel);
   }

   /**
    * Retrieve the table used by the ASCII model.
    *
    * @return table model
    */
   public TableModel getAsciiTableModel()
   {
      return m_asciiTableModel;
   }

   /**
    * Retrieve the index of the currently selected byte in the hex or ASCII table.
    *
    * @return currently selected byte index
    */
   public int getCurrentSelectionIndex()
   {
      return m_currentSelectionIndex;
   }

   /**
    * Set the index of the currently selected byte in the hex or ASCII table.
    *
    * @param currentSelectionIndex currently selected byte index
    */
   public void setCurrentSelectionIndex(int currentSelectionIndex)
   {
      m_changeSupport.firePropertyChange("currentSelectionIndex", m_currentSelectionIndex, m_currentSelectionIndex = currentSelectionIndex);
   }

   /**
    * Retrieve the index of the previously selected byte in the hex or ASCII table.
    *
    * @return previously selected byte index
    */
   public int getPreviousSelectionIndex()
   {
      return m_previousSelectionIndex;
   }

   /**
    * Set the index of the previously selected byte in the hex or ASCII table.
    *
    * @param previousSelectionIndex previously selected byte index
    */
   public void setPreviousSelectionIndex(int previousSelectionIndex)
   {
      m_changeSupport.firePropertyChange("previousSelectionIndex", m_previousSelectionIndex, m_previousSelectionIndex = previousSelectionIndex);
   }

   /**
    * Retrieve a description of the previously selected index in the hex or ASCII table.
    *
    * @return previously selected index description
    */
   public String getPreviousSelectionValueLabel()
   {
      return m_previousSelectionValueLabel;
   }

   /**
    * Set a description of the previously selected index in the hex or ASCII table.
    *
    * @param previousSelectionValueLabel previously selected index description
    */
   public void setPreviousSelectionValueLabel(String previousSelectionValueLabel)
   {
      m_changeSupport.firePropertyChange("previousSelectionValueLabel", m_previousSelectionValueLabel, m_previousSelectionValueLabel = previousSelectionValueLabel);
   }

   /**
    * Retrieve the description of the current hex and ASCII table selection.
    *
    * @return current selection description
    */
   public String getCurrentSelectionValueLabel()
   {
      return m_currentSelectionValueLabel;
   }

   /**
    * Set the description of the current hex and ASCII table selection.
    *
    * @param currentSelectionValueLabel current selection description
    */
   public void setCurrentSelectionValueLabel(String currentSelectionValueLabel)
   {
      m_changeSupport.firePropertyChange("currentSelectionValueLabel", m_currentSelectionValueLabel, m_currentSelectionValueLabel = currentSelectionValueLabel);
   }

   /**
    * Retrieve the difference in bytes between the previous and the current selection.
    *
    * @return difference in bytes
    */
   public String getSelectionDifferenceValueLabel()
   {
      return m_selectionDifferenceValueLabel;
   }

   /**
    * Set the difference in bytes between the previous and the current selection.
    *
    * @param selectionDifferenceValueLabel difference in bytes
    */
   public void setSelectionDifferenceValueLabel(String selectionDifferenceValueLabel)
   {
      m_changeSupport.firePropertyChange("selectionDifferenceValueLabel", m_selectionDifferenceValueLabel, m_selectionDifferenceValueLabel = selectionDifferenceValueLabel);
   }

   /**
    * Retrieve the current table selection as a row and column point.
    *
    * @return current table selection
    */
   public Point getSelectedCell()
   {
      return m_selectedCell;
   }

   /**
    * Set the current table selection as a row and column point.
    *
    * @param selectedCell current table selection
    */
   public void setSelectedCell(Point selectedCell)
   {
      m_changeSupport.firePropertyChange("selectedCell", m_selectedCell, m_selectedCell = selectedCell);
   }

   /**
    * Retrieve the value from the currently selected byte as a short int.
    *
    * @return short value
    */
   public String getShortValueLabel()
   {
      return m_shortValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a short int.
    *
    * @param shortValueLabel short value
    */
   public void setShortValueLabel(String shortValueLabel)
   {
      m_changeSupport.firePropertyChange("shortValueLabel", m_shortValueLabel, m_shortValueLabel = shortValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a six byte long.
    *
    * @return long6 value
    */
   public String getLongSixValueLabel()
   {
      return m_longSixValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a six byte long.
    *
    * @param longSixValueLabel long6 value
    */
   public void setLongSixValueLabel(String longSixValueLabel)
   {
      m_changeSupport.firePropertyChange("longSixValueLabel", m_longSixValueLabel, m_longSixValueLabel = longSixValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a long int.
    *
    * @return long int value
    */
   public String getLongValueLabel()
   {
      return m_longValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a long int.
    *
    * @param longValueLabel long int value
    */
   public void setLongValueLabel(String longValueLabel)
   {
      m_changeSupport.firePropertyChange("longValueLabel", m_longValueLabel, m_longValueLabel = longValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a double.
    *
    * @return double value
    */
   public String getDoubleValueLabel()
   {
      return m_doubleValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a double.
    *
    * @param doubleValueLabel double value
    */
   public void setDoubleValueLabel(String doubleValueLabel)
   {
      m_changeSupport.firePropertyChange("doubleValueLabel", m_doubleValueLabel, m_doubleValueLabel = doubleValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a duration.
    *
    * @return duration value
    */
   public String getDurationValueLabel()
   {
      return m_durationValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a duration.
    *
    * @param durationValueLabel duration value
    */
   public void setDurationValueLabel(String durationValueLabel)
   {
      m_changeSupport.firePropertyChange("durationValueLabel", m_durationValueLabel, m_durationValueLabel = durationValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a time unit.
    *
    * @return time unit value
    */
   public String getTimeUnitsValueLabel()
   {
      return m_timeUnitsValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a time unit.
    *
    * @param timeUnitsValueLabel time unit value
    */
   public void setTimeUnitsValueLabel(String timeUnitsValueLabel)
   {
      m_changeSupport.firePropertyChange("timeUnitsValueLabel", m_timeUnitsValueLabel, m_timeUnitsValueLabel = timeUnitsValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a GUID.
    *
    * @return GUID value
    */
   public String getGuidValueLabel()
   {
      return m_guidValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a GUID.
    *
    * @param guidValueLabel GUID value
    */
   public void setGuidValueLabel(String guidValueLabel)
   {
      m_changeSupport.firePropertyChange("guidValueLabel", m_guidValueLabel, m_guidValueLabel = guidValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a percentage.
    *
    * @return percentage value
    */
   public String getPercentageValueLabel()
   {
      return m_percentageValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a percentage.
    *
    * @param percentageValueLabel percentage value
    */
   public void setPercentageValueLabel(String percentageValueLabel)
   {
      m_changeSupport.firePropertyChange("percentageValueLabel", m_percentageValueLabel, m_percentageValueLabel = percentageValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a date.
    *
    * @return date value
    */
   public String getDateValueLabel()
   {
      return m_dateValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a date.
    *
    * @param dateValueLabel date value
    */
   public void setDateValueLabel(String dateValueLabel)
   {
      m_changeSupport.firePropertyChange("dateValueLabel", m_dateValueLabel, m_dateValueLabel = dateValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a time.
    *
    * @return time value
    */
   public String getTimeValueLabel()
   {
      return m_timeValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a time.
    *
    * @param timeValueLabel time value
    */
   public void setTimeValueLabel(String timeValueLabel)
   {
      m_changeSupport.firePropertyChange("timeValueLabel", m_timeValueLabel, m_timeValueLabel = timeValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as a timestamp.
    *
    * @return timestamp value
    */
   public String getTimestampValueLabel()
   {
      return m_timestampValueLabel;
   }

   /**
    * Set the value from the currently selected byte as a timestamp.
    *
    * @param timestampValueLabel timestamp value
    */
   public void setTimestampValueLabel(String timestampValueLabel)
   {
      m_changeSupport.firePropertyChange("timestampValueLabel", m_timestampValueLabel, m_timestampValueLabel = timestampValueLabel);
   }

   /**
    * Retrieve the value from the currently selected byte as work time units.
    *
    * @return work time units
    */
   public String getWorkUnitsValueLabel()
   {
      return m_workUnitsValueLabel;
   }

   /**
    * set the value from the currently selected byte as work time units.
    *
    * @param workUnitsValueLabel work time units
    */
   public void setWorkUnitsValueLabel(String workUnitsValueLabel)
   {
      m_changeSupport.firePropertyChange("workUnitsValueLabel", m_workUnitsValueLabel, m_workUnitsValueLabel = workUnitsValueLabel);
   }

   /**
    * Add a property change listener.
    *
    * @param listener property change listener
    */
   public void addPropertyChangeListener(PropertyChangeListener listener)
   {
      m_changeSupport.addPropertyChangeListener(listener);
   }

   /**
    * Add a property change listener for a named property.
    *
    * @param propertyName property name
    * @param listener listener
    */
   public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener)
   {
      m_changeSupport.addPropertyChangeListener(propertyName, listener);
   }

   /**
    * Remove a property change listener.
    *
    * @param listener property change listener
    */
   public void removePropertyChangeListener(PropertyChangeListener listener)
   {
      m_changeSupport.removePropertyChangeListener(listener);
   }
}
