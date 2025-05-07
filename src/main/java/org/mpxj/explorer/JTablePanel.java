
package org.mpxj.explorer;

import java.awt.GridLayout;
import java.awt.Point;
import java.beans.PropertyChangeEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.TableModel;

/**
 * Presents a pair of JTables side by side in a panel.
 * The scrolling and selection for these tables are synchronized.
 */
public class JTablePanel extends JPanel
{
   private final JTableExtra m_leftTable;
   private final JTableExtra m_rightTable;

   /**
    * Constructor.
    */
   public JTablePanel()
   {
      setBorder(null);
      setLayout(new GridLayout(1, 0, 0, 0));

      m_leftTable = new JTableExtra();
      m_leftTable.setFillsViewportHeight(true);
      m_leftTable.setBorder(null);
      m_leftTable.setShowVerticalLines(false);
      m_leftTable.setShowHorizontalLines(false);
      m_leftTable.setShowGrid(false);
      m_leftTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      m_leftTable.setCellSelectionEnabled(true);
      m_leftTable.setTableHeader(null);
      m_leftTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      JScrollPane leftScrollPane = new JScrollPane(m_leftTable);
      leftScrollPane.setBorder(null);
      add(leftScrollPane);

      m_rightTable = new JTableExtra();
      m_rightTable.setFillsViewportHeight(true);
      m_rightTable.setBorder(null);
      m_rightTable.setShowVerticalLines(false);
      m_rightTable.setShowHorizontalLines(false);
      m_rightTable.setShowGrid(false);
      m_rightTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      m_rightTable.setCellSelectionEnabled(true);
      m_rightTable.setTableHeader(null);
      m_rightTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

      JScrollPane rightScrollPane = new JScrollPane(m_rightTable);
      rightScrollPane.setBorder(null);
      add(rightScrollPane);

      leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
      leftScrollPane.getVerticalScrollBar().setModel(rightScrollPane.getVerticalScrollBar().getModel());
      leftScrollPane.getHorizontalScrollBar().setModel(rightScrollPane.getHorizontalScrollBar().getModel());
      m_leftTable.setSelectionModel(m_rightTable.getSelectionModel());
      m_leftTable.setColumnModel(m_rightTable.getColumnModel());

      m_leftTable.addPropertyChangeListener("selectedCell", this::firePropertyChange);

      m_rightTable.addPropertyChangeListener("selectedCell", this::firePropertyChange);
   }

   /**
    * Set the model used by the left table.
    *
    * @param model table model
    */
   public void setLeftTableModel(TableModel model)
   {
      TableModel old = m_leftTable.getModel();
      m_leftTable.setModel(model);
      firePropertyChange("leftTableModel", old, model);
   }

   /**
    * Retrieve the model used by the left table.
    *
    * @return table model
    */
   public TableModel getLeftTableModel()
   {
      return m_leftTable.getModel();
   }

   /**
    * Set the model used by the right table.
    *
    * @param model table model
    */
   public void setRightTableModel(TableModel model)
   {
      TableModel old = m_rightTable.getModel();
      m_rightTable.setModel(model);
      firePropertyChange("rightTableModel", old, model);
   }

   /**
    * Retrieve the model used by the right table.
    *
    * @return table model
    */
   public TableModel getRightTableModel()
   {
      return m_rightTable.getModel();
   }

   /**
    * Retrieve the currently selected cell.
    *
    * @return selected cell
    */
   public Point getSelectedCell()
   {
      return m_leftTable.getSelectedCell();
   }

   /**
    * Fire a property change event in response to a cell selection.
    *
    * @param evt event data
    */
   protected void firePropertyChange(PropertyChangeEvent evt)
   {
      firePropertyChange("selectedCell", evt.getOldValue(), evt.getNewValue());
   }
}
