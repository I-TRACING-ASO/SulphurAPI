package v1.sulphurapi.structure.bulk;

import v1.sulphurapi.handlers.gui.UserGridEventHandler;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Vector;

public class BulkGrid {
    private final JPanel mainPanel;
    private final JPanel dropListPanel;
    private final JComboBox<String> parametersList;
    private final JPanel parametersValuesPanel;
    private JList<String> parametersValuesList;
    private final Map<String, ListModel<String>> parametersValuesMap;
    private final JTextField parametersValuesField;
    private final JButton parametersValuesButton;
    private final JButton removeValueButton;
    private final JButton clearListButton;
    private final JButton dropListButton;

    private final JPanel buttonsPanel;
    private final JButton sendBulkButton;
    private final JButton loadEndpointButton;

    private final JPanel tablePanel;
    private final JTable table;


    public BulkGrid(JPanel mainPanel) {
        this.mainPanel = mainPanel;
        
        dropListPanel = new JPanel();
        dropListPanel.setName("jpanel.dropListPanel");
        dropListPanel.setLayout(new GridBagLayout());
        dropListPanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        dropListPanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        dropListPanel.setBorder(BorderFactory.createTitledBorder("Parameters"));


        //PARAMETERS PANEL --------------------------------------------------------------------------------------------------------------------------------------------
        parametersList = new JComboBox<>();
        parametersList.setName("jcombobox.parametersList");
        parametersList.setEditable(false);
        parametersValuesMap = new LinkedHashMap<String, ListModel<String>>();
        for (int i = 0; i < parametersList.getModel().getSize(); i++) {
            parametersValuesMap.put(parametersList.getModel().getElementAt(i), new DefaultListModel<>());
        }
        parametersList.addActionListener(e -> UserGridEventHandler.comboBoxListSelectionEvent(parametersList, parametersValuesList, parametersValuesMap));


        parametersValuesPanel = new JPanel();
        parametersValuesPanel.setName("jpanel.parametersValuesPanel");
        parametersValuesPanel.setLayout(new GridBagLayout());

        parametersValuesList = new JList<>();
        parametersValuesList.setName("jlist.parametersValuesList");
        parametersValuesList.setLayoutOrientation(JList.VERTICAL);
        parametersValuesList.setModel(new DefaultListModel<>());
        parametersValuesList.setBorder(new EtchedBorder());
        parametersValuesPanel.add(new JScrollPane(parametersValuesList), new GridBagConstraints() {{
            fill = GridBagConstraints.BOTH;
            weightx = 1.0;
            weighty = 1.0;
            gridx = 0;
            gridy = 0;
            insets = new Insets(5, 0, 5, 0);
        }});


        parametersValuesField = new JTextField();
        parametersValuesField.setName("jtextfield.parametersValuesField");

        parametersValuesButton = new JButton("Add");
        parametersValuesButton.setName("jbutton.parametersValuesButton");
        parametersValuesButton.addActionListener(e -> UserGridEventHandler.addValueToListEvent(parametersValuesField, parametersList, parametersValuesList, parametersValuesMap));

        dropListButton = new JButton("Clipboard List");
        dropListButton.setName("jbutton.dropListButton");
        dropListButton.addActionListener(e -> UserGridEventHandler.loadListEvent(parametersList, parametersValuesList, parametersValuesMap));

        removeValueButton = new JButton("Remove");
        removeValueButton.setName("jbutton.removeValueButton");
        removeValueButton.addActionListener(e -> UserGridEventHandler.removeValueFromListEvent(parametersList, parametersValuesList, parametersValuesMap));

        clearListButton = new JButton("Clear");
        clearListButton.setName("jbutton.clearListButton");
        clearListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                UserGridEventHandler.clearListEvent(parametersList, parametersValuesList, parametersValuesMap);
            }
        });

        GridBagConstraints dropListConstraints = new GridBagConstraints();
        dropListConstraints.fill = GridBagConstraints.HORIZONTAL;
        dropListConstraints.weightx = 1.0;
        dropListConstraints.weighty = 0.0;
        dropListConstraints.gridx = 0;
        dropListConstraints.gridy = 0;
        dropListConstraints.gridwidth = 5;
        dropListPanel.add(parametersList, dropListConstraints);
        dropListConstraints.gridx = 0;
        dropListConstraints.gridy = 1;
        dropListConstraints.weighty = 1;
        dropListConstraints.fill = GridBagConstraints.BOTH;
        dropListPanel.add(parametersValuesPanel, dropListConstraints);
        dropListConstraints.weighty = 0;
        dropListConstraints.gridx = 0;
        dropListConstraints.gridy = 2;
        dropListConstraints.gridwidth = 1;
        dropListConstraints.fill = GridBagConstraints.HORIZONTAL;
        dropListConstraints.weightx = 1.0;
        dropListPanel.add(parametersValuesField, dropListConstraints);
        dropListConstraints.weightx = 0;
        dropListConstraints.gridx = 1;
        dropListConstraints.gridy = 2;
        dropListConstraints.insets = new Insets(0, 5, 0, 5);
        dropListPanel.add(parametersValuesButton, dropListConstraints);
        dropListConstraints.gridx = 2;
        dropListConstraints.gridy = 2;
        dropListConstraints.insets = new Insets(0, 0, 0, 5);
        dropListPanel.add(removeValueButton, dropListConstraints);
        dropListConstraints.gridx = 3;
        dropListConstraints.gridy = 2;
        dropListConstraints.insets = new Insets(0, 0, 0, 5);
        dropListPanel.add(clearListButton, dropListConstraints);
        dropListConstraints.gridx = 4;
        dropListConstraints.gridy = 2;
        dropListConstraints.insets = new Insets(0, 0, 0, 0);
        dropListPanel.add(dropListButton, dropListConstraints);

        //BUTTONS PANEL -------------------------------------------------------------------------------------------------------------------------------------------

        buttonsPanel = new JPanel();
        buttonsPanel.setName("jpanel.buttonsPanel");
        buttonsPanel.setLayout(new GridBagLayout());
        buttonsPanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        buttonsPanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        buttonsPanel.setBorder(BorderFactory.createTitledBorder("Actions"));

        GridBagConstraints actionsConstraints = new GridBagConstraints();
        actionsConstraints.fill = GridBagConstraints.HORIZONTAL;
        actionsConstraints.weightx = 1.0;
        actionsConstraints.weighty = 0.0;
        actionsConstraints.gridx = 0;
        actionsConstraints.gridy = 0;
        actionsConstraints.anchor= GridBagConstraints.PAGE_START;
        actionsConstraints.insets = new Insets(5, 5, 5, 5);

        loadEndpointButton = new JButton("Load Endpoint");
        loadEndpointButton.setName("jbutton.loadEndpointButton");
        buttonsPanel.add(loadEndpointButton, actionsConstraints);

        actionsConstraints.weighty = 1.0;
        actionsConstraints.gridx = 0;
        actionsConstraints.gridy = 1;

        sendBulkButton = new JButton("Send Bulk");
        sendBulkButton.setName("jbutton.sendBulkButton");
        buttonsPanel.add(sendBulkButton, actionsConstraints);

        //TABLE PANEL -------------------------------------------------------------------------------------------------------------------------------------------

        tablePanel = new JPanel();
        tablePanel.setName("jpanel.tablePanel");
        tablePanel.setLayout(new GridBagLayout());
        tablePanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight()));
        tablePanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight()));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Users"));

        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setDropMode(DropMode.ON);
        table.setDragEnabled(true);
        table.setName("jtable.table");
        tablePanel.add(new JScrollPane(table), new GridBagConstraints() {{
            fill = GridBagConstraints.BOTH;
            weightx = 1.0;
            weighty = 1.0;
            gridx = 0;
            gridy = 0;
        }});

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(dropListPanel, c);
        c.gridy = 1;
        mainPanel.add(buttonsPanel, c);
        c.gridx=1;
        c.gridy=0;
        c.gridheight=2;
        mainPanel.add(tablePanel, c);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public void resetUI() {
        table.setModel(new DefaultTableModel());
        parametersList.removeAllItems();
        parametersValuesList.setModel(new DefaultListModel<>());
        parametersValuesField.setText("");
        table.revalidate();
        table.repaint();
        parametersList.revalidate();
        parametersValuesList.revalidate();
        parametersValuesField.revalidate();
        parametersList.repaint();
        parametersValuesList.repaint();
        parametersValuesField.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Updates the combo box with new values and clears the parametersValuesMap.
     * @param values Vector of String values to be added to the combo box.
     */
    public void updateComboBox(Vector<String> values) {
        parametersList.removeAllItems();
        parametersValuesMap.clear();
        for (String value : values) {
            parametersList.addItem(value);
        }
        parametersList.revalidate();
        parametersList.repaint();
    }

    public JComboBox<String> getParametersList() {
        return parametersList;
    }

    public JButton getLoadEndpointButton() {
        return loadEndpointButton;
    }

    public JButton getSendBulkButton() {
        return sendBulkButton;
    }

    public Map<String, ListModel<String>> getParametersValuesMap() {
        return parametersValuesMap;
    }

    public JTable getTable() {
        return table;
    }
}
