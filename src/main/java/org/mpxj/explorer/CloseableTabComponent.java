/*
 * file:       CloseableTabComponent.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * Tab header component displaying a title and a small "x" button
 * which closes the tab when clicked.
 */
class CloseableTabComponent extends JPanel
{
   /**
    * Constructor.
    *
    * @param tabbedPane parent tabbed pane
    * @param title tab title
    */
   public CloseableTabComponent(JTabbedPane tabbedPane, String title)
   {
      super(new FlowLayout(FlowLayout.LEFT, 0, 0));
      setOpaque(false);

      JLabel label = new JLabel(title);
      label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 6));
      add(label);

      JButton button = new JButton(new CloseIcon());
      button.setToolTipText("Close");
      button.setBorder(BorderFactory.createEmptyBorder());
      button.setBorderPainted(false);
      button.setContentAreaFilled(false);
      button.setFocusable(false);
      button.setRolloverEnabled(true);
      button.addActionListener(e -> {
         int index = tabbedPane.indexOfTabComponent(this);
         if (index != -1)
         {
            tabbedPane.remove(index);
         }
      });
      add(button);
   }

   /**
    * Icon drawing a small "x", highlighted when the mouse is over the button.
    */
   private static class CloseIcon implements Icon
   {
      @Override public void paintIcon(Component c, Graphics g, int x, int y)
      {
         Graphics2D g2 = (Graphics2D) g.create();
         try
         {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            boolean rollover = false;
            boolean pressed = false;
            if (c instanceof AbstractButton)
            {
               ButtonModel model = ((AbstractButton) c).getModel();
               rollover = model.isRollover();
               pressed = model.isPressed();
            }

            if (rollover || pressed)
            {
               g2.setColor(pressed ? new Color(0, 0, 0, 70) : new Color(0, 0, 0, 40));
               g2.fillOval(x, y, SIZE, SIZE);
            }

            g2.setColor(rollover || pressed ? Color.DARK_GRAY : Color.GRAY);
            g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int inset = 4;
            g2.drawLine(x + inset, y + inset, x + SIZE - inset - 1, y + SIZE - inset - 1);
            g2.drawLine(x + SIZE - inset - 1, y + inset, x + inset, y + SIZE - inset - 1);
         }
         finally
         {
            g2.dispose();
         }
      }

      @Override public int getIconWidth()
      {
         return SIZE;
      }

      @Override public int getIconHeight()
      {
         return SIZE;
      }

      private static final int SIZE = 14;
   }
}
