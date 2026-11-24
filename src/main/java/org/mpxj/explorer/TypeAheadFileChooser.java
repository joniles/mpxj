/*
 * file:       TypeAheadFileChooser.java
 * author:     Jon Iles
 * date:       2026-11-07
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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * File chooser which allows the user to type the start of a file name
 * to move the selection in the file list to the first matching file.
 * Look and feels derived from BasicFileChooserUI (Metal, Windows, GTK)
 * already provide this behaviour; others (for example the macOS Aqua
 * look and feel) present the file list as a JTable without it.
 */
class TypeAheadFileChooser extends JFileChooser
{
   @Override public void updateUI()
   {
      super.updateUI();
      if (!(getUI() instanceof BasicFileChooserUI))
      {
         installTypeAhead(this);
      }
   }

   /**
    * Recursively find tables within the file chooser and add type-ahead support.
    *
    * @param container parent container
    */
   private void installTypeAhead(Container container)
   {
      for (Component component : container.getComponents())
      {
         if (component instanceof JTable)
         {
            JTable table = (JTable) component;
            if (table.getClientProperty(TypeAheadKeyListener.class) == null)
            {
               table.putClientProperty(TypeAheadKeyListener.class, Boolean.TRUE);
               table.addKeyListener(new TypeAheadKeyListener(table));
            }
         }
         else
         {
            if (component instanceof Container)
            {
               installTypeAhead((Container) component);
            }
         }
      }
   }

   /**
    * Retrieve the name of the file shown in a table row.
    *
    * @param table file list table
    * @param row view row index
    * @return file name
    */
   private String getRowName(JTable table, int row)
   {
      Object value = table.getModel().getValueAt(table.convertRowIndexToModel(row), 0);
      if (value instanceof File)
      {
         String name = getName((File) value);
         return name == null ? ((File) value).getName() : name;
      }
      return value == null ? "" : value.toString();
   }

   /**
    * Accumulates typed characters and selects the first row
    * whose file name starts with them.
    */
   private class TypeAheadKeyListener extends KeyAdapter
   {
      /**
       * Constructor.
       *
       * @param table file list table
       */
      public TypeAheadKeyListener(JTable table)
      {
         m_table = table;
      }

      @Override public void keyTyped(KeyEvent e)
      {
         char c = e.getKeyChar();
         if (Character.isISOControl(c) || (e.getModifiersEx() & (InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK | InputEvent.ALT_DOWN_MASK)) != 0)
         {
            return;
         }

         int rowCount = m_table.getRowCount();
         if (rowCount == 0)
         {
            return;
         }

         long time = e.getWhen();
         if (time - m_lastTime > TIMEOUT)
         {
            m_prefix.setLength(0);
         }
         m_lastTime = time;

         // Typing the same character repeatedly cycles through
         // the files starting with that character
         if (m_prefix.length() == 1 && m_prefix.charAt(0) == c)
         {
            m_prefix.setLength(0);
         }

         m_prefix.append(Character.toLowerCase(c));
         String prefix = m_prefix.toString();

         int selectedRow = m_table.getSelectedRow();
         int startRow;
         if (selectedRow == -1)
         {
            startRow = 0;
         }
         else
         {
            startRow = prefix.length() == 1 ? selectedRow + 1 : selectedRow;
         }

         for (int index = 0; index < rowCount; index++)
         {
            int row = (startRow + index) % rowCount;
            if (getRowName(m_table, row).toLowerCase().startsWith(prefix))
            {
               m_table.setRowSelectionInterval(row, row);
               m_table.scrollRectToVisible(m_table.getCellRect(row, 0, true));
               break;
            }
         }

         e.consume();
      }

      private final JTable m_table;
      private final StringBuilder m_prefix = new StringBuilder();
      private long m_lastTime;
   }

   private static final long TIMEOUT = 1000;
}
