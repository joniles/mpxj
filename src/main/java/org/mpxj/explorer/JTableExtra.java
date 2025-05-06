/*
 * file:       JTableExtra.java
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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * An extension of JTable which presents the selected cell as an observable property
 * and sets all columns to a fixed width.
 */
public class JTableExtra extends JTable
{
   private Point m_selectedCell = new Point(-1, -1);
   private int m_columnWidth = 20;

   /**
    * Constructor.
    */
   public JTableExtra()
   {
      super();

      //
      // Fire selection event in response to mouse clicks
      //
      addMouseListener(new MouseAdapter()
      {
         @Override public void mouseClicked(MouseEvent e)
         {
            setSelectedCell(new Point(getSelectedColumn(), getSelectedRow()));
         }
      });

      //
      // Fire selection event in response to arrow key moving selection
      //
      addKeyListener(new KeyAdapter()
      {
         @Override public void keyPressed(KeyEvent e)
         {
            switch (e.getKeyCode())
            {
               case KeyEvent.VK_LEFT:
               case KeyEvent.VK_RIGHT:
               case KeyEvent.VK_UP:
               case KeyEvent.VK_DOWN:
               {
                  SwingUtilities.invokeLater(() -> setSelectedCell(new Point(getSelectedColumn(), getSelectedRow())));

                  break;
               }
            }
         }
      });
   }

   /**
    * Retrieve the currently selected cell.
    *
    * @return selected cell
    */
   public Point getSelectedCell()
   {
      return m_selectedCell;
   }

   /**
    * Set the current selected cell.
    *
    * @param selectedCell selected cell
    */
   public void setSelectedCell(Point selectedCell)
   {
      firePropertyChange("selectedCell", m_selectedCell, m_selectedCell = selectedCell);
   }

   /**
    * Retrieves the fixed column width used by all columns in the table.
    *
    * @return column width
    */
   public int getColumnWidth()
   {
      return m_columnWidth;
   }

   /**
    * Sets the column width used by all columns in the table.
    *
    * @param columnWidth column width
    */
   public void setColumnWidth(int columnWidth)
   {
      firePropertyChange("columnWidth", m_columnWidth, m_columnWidth = columnWidth);
   }

   /**
    * Updates the model. Ensures that we reset the columns widths.
    *
    * @param model table model
    */
   @Override public void setModel(TableModel model)
   {
      super.setModel(model);
      int columns = model.getColumnCount();
      TableColumnModel tableColumnModel = getColumnModel();
      for (int index = 0; index < columns; index++)
      {
         tableColumnModel.getColumn(index).setPreferredWidth(m_columnWidth);
      }
   }
}
