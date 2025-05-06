/*
 * file:       HexDumpView.java
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

import java.awt.Color;
import java.awt.FlowLayout;
import java.text.NumberFormat;

import javax.swing.BoxLayout;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.text.NumberFormatter;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.binding.beans.PropertyConnector;

/**
 * Implements the view component of the HexDump MVC.
 */
public class HexDumpView extends JPanel
{
   /**
    * Constructor.
    *
    * @param model HexDump model
    */
   public HexDumpView(final HexDumpModel model)
   {
      NumberFormatter integerFormat = new NumberFormatter(NumberFormat.getIntegerInstance());
      integerFormat.setValueClass(Integer.class);
      SpringLayout springLayout = new SpringLayout();
      setLayout(springLayout);

      //
      // Controls panel
      //
      JPanel controlsPanel = new JPanel();
      springLayout.putConstraint(SpringLayout.NORTH, controlsPanel, 0, SpringLayout.NORTH, this);
      springLayout.putConstraint(SpringLayout.WEST, controlsPanel, 0, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.EAST, controlsPanel, 0, SpringLayout.EAST, this);
      FlowLayout flowLayout = (FlowLayout) controlsPanel.getLayout();
      flowLayout.setAlignment(FlowLayout.LEFT);
      add(controlsPanel);

      JLabel columnsLabel = new JLabel("Columns");
      controlsPanel.add(columnsLabel);

      JFormattedTextField columns = new JFormattedTextField(integerFormat);
      controlsPanel.add(columns);
      columns.setBorder(new LineBorder(new Color(171, 173, 179)));
      columns.setColumns(10);

      JLabel offsetLabel = new JLabel("Offset");
      controlsPanel.add(offsetLabel);

      JFormattedTextField offset = new JFormattedTextField(integerFormat);
      controlsPanel.add(offset);
      offset.setBorder(new LineBorder(new Color(171, 173, 179)));
      offset.setColumns(10);

      //
      // Table panel
      //
      JTablePanel tablePanel = new JTablePanel();
      JPanel infoPanel = new JPanel();
      FlowLayout infoPanelLayout = new FlowLayout(FlowLayout.LEFT, 5, 5);
      infoPanel.setLayout(infoPanelLayout);
      infoPanel.setBorder(null);

      //
      // Selection data
      //
      JPanel infoPanelSelection = new JPanel();
      infoPanel.add(infoPanelSelection);
      infoPanelSelection.setLayout(new BoxLayout(infoPanelSelection, BoxLayout.Y_AXIS));

      JLabelledValue sizeValueLabel = new JLabelledValue("Size:");
      infoPanelSelection.add(sizeValueLabel);

      JLabelledValue currentSelectionValueLabel = new JLabelledValue("Current Selection:");
      infoPanelSelection.add(currentSelectionValueLabel);

      JLabelledValue previousSelectionValueLabel = new JLabelledValue("Previous Selection:");
      infoPanelSelection.add(previousSelectionValueLabel);

      JLabelledValue differenceValueLabel = new JLabelledValue("Difference:");
      infoPanelSelection.add(differenceValueLabel);

      //
      // Numeric data
      //
      JPanel infoPanelNumeric = new JPanel();
      infoPanel.add(infoPanelNumeric);
      infoPanelNumeric.setLayout(new BoxLayout(infoPanelNumeric, BoxLayout.Y_AXIS));

      JLabelledValue shortValueLabel = new JLabelledValue("Short:");
      infoPanelNumeric.add(shortValueLabel);

      JLabelledValue longSixValueLabel = new JLabelledValue("Long6:");
      infoPanelNumeric.add(longSixValueLabel);

      JLabelledValue longValueLabel = new JLabelledValue("Long:");
      infoPanelNumeric.add(longValueLabel);

      JLabelledValue doubleValueLabel = new JLabelledValue("Double:");
      infoPanelNumeric.add(doubleValueLabel);

      //
      // Duration data
      //
      JPanel infoPanelDuration = new JPanel();
      infoPanel.add(infoPanelDuration);
      infoPanelDuration.setLayout(new BoxLayout(infoPanelDuration, BoxLayout.Y_AXIS));

      JLabelledValue durationValueLabel = new JLabelledValue("Duration:");
      infoPanelDuration.add(durationValueLabel);

      JLabelledValue timeUnitsValueLabel = new JLabelledValue("TmeUnit:");
      infoPanelDuration.add(timeUnitsValueLabel);

      JLabelledValue workUnitsValueLabel = new JLabelledValue("Work Units:");
      infoPanelDuration.add(workUnitsValueLabel);

      //
      // Date and time  data
      //
      JPanel infoPanelDate = new JPanel();
      infoPanel.add(infoPanelDate);
      infoPanelDate.setLayout(new BoxLayout(infoPanelDate, BoxLayout.Y_AXIS));

      JLabelledValue dateValueLabel = new JLabelledValue("Date:");
      infoPanelDate.add(dateValueLabel);

      JLabelledValue timeValueLabel = new JLabelledValue("Time:");
      infoPanelDate.add(timeValueLabel);

      JLabelledValue timestampValueLabel = new JLabelledValue("Timestamp:");
      infoPanelDate.add(timestampValueLabel);

      //
      // Misc
      //
      JPanel infoPanelMisc = new JPanel();
      infoPanel.add(infoPanelMisc);
      infoPanelMisc.setLayout(new BoxLayout(infoPanelMisc, BoxLayout.Y_AXIS));

      JLabelledValue guidValueLabel = new JLabelledValue("GUID:");
      infoPanelMisc.add(guidValueLabel);

      JLabelledValue percentageValueLabel = new JLabelledValue("Percentage:");
      infoPanelMisc.add(percentageValueLabel);

      //
      // Split pane
      //
      final JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
      springLayout.putConstraint(SpringLayout.NORTH, splitPane, 0, SpringLayout.SOUTH, controlsPanel);
      springLayout.putConstraint(SpringLayout.WEST, splitPane, 0, SpringLayout.WEST, this);
      springLayout.putConstraint(SpringLayout.SOUTH, splitPane, 0, SpringLayout.SOUTH, this);
      springLayout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.EAST, this);
      add(splitPane);
      splitPane.setLeftComponent(tablePanel);
      splitPane.setRightComponent(infoPanel);
      SwingUtilities.invokeLater(() -> {
         splitPane.setDividerLocation(0.85);
         splitPane.setResizeWeight(0.85);
      });

      //
      // Bindings
      //
      BeanAdapter<HexDumpModel> modelAdapter = new BeanAdapter<>(model, true);
      Bindings.bind(columns, modelAdapter.getValueModel("columns"));
      Bindings.bind(offset, modelAdapter.getValueModel("offset"));
      Bindings.bind(sizeValueLabel, "value", modelAdapter.getValueModel("sizeValueLabel"));
      Bindings.bind(currentSelectionValueLabel, "value", modelAdapter.getValueModel("currentSelectionValueLabel"));
      Bindings.bind(previousSelectionValueLabel, "value", modelAdapter.getValueModel("previousSelectionValueLabel"));
      Bindings.bind(differenceValueLabel, "value", modelAdapter.getValueModel("selectionDifferenceValueLabel"));
      Bindings.bind(shortValueLabel, "value", modelAdapter.getValueModel("shortValueLabel"));
      Bindings.bind(longSixValueLabel, "value", modelAdapter.getValueModel("longSixValueLabel"));
      Bindings.bind(longValueLabel, "value", modelAdapter.getValueModel("longValueLabel"));
      Bindings.bind(doubleValueLabel, "value", modelAdapter.getValueModel("doubleValueLabel"));
      Bindings.bind(durationValueLabel, "value", modelAdapter.getValueModel("durationValueLabel"));
      Bindings.bind(timeUnitsValueLabel, "value", modelAdapter.getValueModel("timeUnitsValueLabel"));
      Bindings.bind(guidValueLabel, "value", modelAdapter.getValueModel("guidValueLabel"));
      Bindings.bind(percentageValueLabel, "value", modelAdapter.getValueModel("percentageValueLabel"));
      Bindings.bind(dateValueLabel, "value", modelAdapter.getValueModel("dateValueLabel"));
      Bindings.bind(timeValueLabel, "value", modelAdapter.getValueModel("timeValueLabel"));
      Bindings.bind(timestampValueLabel, "value", modelAdapter.getValueModel("timestampValueLabel"));
      Bindings.bind(workUnitsValueLabel, "value", modelAdapter.getValueModel("workUnitsValueLabel"));

      PropertyConnector.connect(tablePanel, "leftTableModel", model, "hexTableModel");
      PropertyConnector.connect(tablePanel, "rightTableModel", model, "asciiTableModel");
      PropertyConnector.connect(tablePanel, "selectedCell", model, "selectedCell");
   }
}
