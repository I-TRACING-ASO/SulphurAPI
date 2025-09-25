package v1.sulphurapi.structure.endpoints;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;
import v1.sulphurapi.structure.custom.jtable.CustomEditorTable;
import v1.sulphurapi.structure.custom.jtable.CustomTableNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;

public class EndpointGrid {
    private final JPanel mainPanel;
    private final JPanel endpointListPanel;
    private final JPanel endpointRequestPanel;
    private final JPanel endpointResponsePanel;
    private final JPanel endpointParametersPanel;
    private final JList<String> endpointList;
    private final JTextArea endpointsRequestTextArea;
    private final JTextArea endpointsResponseTextArea;
    private final JButton endpointsSendRequestButton;
    private final CustomEditorTable parametersTable;
    private final JComboBox<String> mediaTypeComboBox;
    private final JButton addValueButton;
    private final ObjectDialog endpointTableDataDialog;
    private final CustomTableNode parametersNode;

    public EndpointGrid(JPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.endpointListPanel = new JPanel();
        endpointListPanel.setName("jpanel.list");
        endpointListPanel.setLayout(new GridBagLayout());
        endpointListPanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        endpointListPanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        endpointListPanel.setBorder(BorderFactory.createTitledBorder("Endpoints List"));

        this.endpointList = new JList<>();
        endpointList.setName("jlist.list");

        JScrollPane listScrollPane = new JScrollPane(endpointList);
        listScrollPane.setName("jscrollpane.list");
        endpointListPanel.add(listScrollPane, new GridBagConstraints() {{
            fill = GridBagConstraints.BOTH;
            weightx = 1.0;
            weighty = 1.0;
            gridx = 0;
            gridy = 0;
        }});

        this.endpointRequestPanel = new JPanel();
        endpointRequestPanel.setName("jpanel.request");
        endpointRequestPanel.setLayout(new GridBagLayout());
        endpointRequestPanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        endpointRequestPanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        endpointRequestPanel.setBorder(BorderFactory.createTitledBorder("Endpoints Requests"));

        this.endpointsRequestTextArea = new JTextArea();
        endpointsRequestTextArea.setName("jtextarea.request");
        endpointsRequestTextArea.setLineWrap(true);
        endpointsRequestTextArea.setEditable(false);
        endpointsRequestTextArea.setWrapStyleWord(true);

        JScrollPane requestScrollPane = new JScrollPane(endpointsRequestTextArea);
        requestScrollPane.setName("jscrollpane.request");
        endpointRequestPanel.add(requestScrollPane, new GridBagConstraints() {{
            fill = GridBagConstraints.BOTH;
            weightx = 1.0;
            weighty = 1.0;
            gridx = 0;
            gridy = 0;
        }});


        this.endpointResponsePanel = new JPanel();
        endpointResponsePanel.setName("jpanel.response");
        endpointResponsePanel.setLayout(new GridBagLayout());
        endpointResponsePanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        endpointResponsePanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        endpointResponsePanel.setBorder(BorderFactory.createTitledBorder("Endpoints Response"));

        this.endpointsResponseTextArea = new JTextArea();
        endpointsResponseTextArea.setName("jtextarea.response");
        endpointsResponseTextArea.setLineWrap(true);
        endpointsResponseTextArea.setEditable(false);
        endpointsResponseTextArea.setWrapStyleWord(true);

        JScrollPane responseScrollPane = new JScrollPane(endpointsResponseTextArea);
        responseScrollPane.setName("jscrollpane.response");
        endpointResponsePanel.add(responseScrollPane, new GridBagConstraints() {{
            fill = GridBagConstraints.BOTH;
            weightx = 1.0;
            weighty = 1.0;
            gridx = 0;
            gridy = 0;
        }});

        this.endpointParametersPanel = new JPanel();
        endpointParametersPanel.setName("jpanel.parameters");
        endpointParametersPanel.setLayout(new GridBagLayout());
        endpointParametersPanel.setMinimumSize(new Dimension(mainPanel.getWidth() / 4, mainPanel.getHeight() / 4));
        endpointParametersPanel.setPreferredSize(new Dimension(mainPanel.getWidth() / 2, mainPanel.getHeight() / 2));
        endpointParametersPanel.setBorder(BorderFactory.createTitledBorder("Endpoints Parameters"));

        this.endpointsSendRequestButton = new JButton("Send Request");
        endpointsSendRequestButton.setName("jbutton.send");

        JLabel mediaTypeLabel = new JLabel("Media Type");
        mediaTypeLabel.setName("jlabel.mediatype");

        this.mediaTypeComboBox = new JComboBox<>();
        mediaTypeComboBox.setName("jcombobox.mediatype");

        this.addValueButton = new JButton("Add Value");
        this.addValueButton.setName("jbutton.addvalue");

        this.endpointTableDataDialog = new ObjectDialog(SAPI.getUIPanel());

        parametersNode = new CustomTableNode(null, endpointTableDataDialog, false);
        this.parametersTable = new CustomEditorTable(endpointTableDataDialog);
        this.parametersTable.setRowSelectionAllowed(false);
        this.parametersTable.setColumnSelectionAllowed(false);

        parametersTable.setName("jtable.parameters");

        JScrollPane parametersScrollPane = new JScrollPane(parametersTable);

        GridBagConstraints parametersPanelConstraints = new GridBagConstraints();

        parametersPanelConstraints.fill = GridBagConstraints.NONE;
        parametersPanelConstraints.weightx = 0.0;
        parametersPanelConstraints.weighty = 0.0;
        parametersPanelConstraints.gridx = 0;
        parametersPanelConstraints.gridy = 0;
        parametersPanelConstraints.gridwidth = 1;
        parametersPanelConstraints.insets = new Insets(5, 5, 5, 5);
        endpointParametersPanel.add(mediaTypeLabel, parametersPanelConstraints);

        parametersPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        parametersPanelConstraints.weightx = 1.0;
        parametersPanelConstraints.weighty = 0.0;
        parametersPanelConstraints.gridx = 1;
        parametersPanelConstraints.gridy = 0;
        parametersPanelConstraints.gridwidth = 1;
        parametersPanelConstraints.insets = new Insets(5, 5, 5, 5);
        endpointParametersPanel.add(mediaTypeComboBox, parametersPanelConstraints);

        parametersPanelConstraints.fill = GridBagConstraints.NONE;
        parametersPanelConstraints.weightx = 0.0;
        parametersPanelConstraints.weighty = 0.0;
        parametersPanelConstraints.gridx = 2;
        parametersPanelConstraints.gridy = 0;
        parametersPanelConstraints.gridwidth = 1;
        parametersPanelConstraints.insets = new Insets(5, 5, 5, 5);
        endpointParametersPanel.add(addValueButton, parametersPanelConstraints);

        parametersPanelConstraints.fill = GridBagConstraints.BOTH;
        parametersPanelConstraints.weightx = 1.0;
        parametersPanelConstraints.weighty = 1.0;
        parametersPanelConstraints.gridx = 0;
        parametersPanelConstraints.gridy = 1;
        parametersPanelConstraints.gridwidth = 3;
        parametersPanelConstraints.insets = new Insets(5, 5, 5, 5);
        endpointParametersPanel.add(parametersScrollPane, parametersPanelConstraints);

        parametersPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        parametersPanelConstraints.weightx = 1.0;
        parametersPanelConstraints.weighty = 0.0;
        parametersPanelConstraints.gridx = 0;
        parametersPanelConstraints.gridy = 2;
        parametersPanelConstraints.gridwidth = 3;
        parametersPanelConstraints.insets = new Insets(5, 5, 5, 5);
        endpointParametersPanel.add(endpointsSendRequestButton, parametersPanelConstraints);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 1.0;
        c.weightx = 1.0;
        mainPanel.add(endpointListPanel, c);


        c.gridx = 1;
        c.gridy = 0;
        mainPanel.add(endpointRequestPanel, c);

        c.gridx = 1;
        c.gridy = 1;

        mainPanel.add(endpointResponsePanel, c);

        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(endpointParametersPanel, c);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
    public JPanel getEndpointListPanel() {
        return endpointListPanel;
    }
    public JPanel getEndpointRequestPanel() {
        return endpointRequestPanel;
    }
    public JPanel getEndpointResponsePanel() {
        return endpointResponsePanel;
    }
    public JPanel getEndpointParametersPanel() {
        return endpointParametersPanel;
    }
    public JList<String> getEndpointList() {
        return endpointList;
    }
    public JTextArea getEndpointsRequestTextArea() {
        return endpointsRequestTextArea;
    }
    public JTextArea getEndpointsResponseTextArea() {
        return endpointsResponseTextArea;
    }
    public JButton getEndpointsSendRequestButton() {
        return endpointsSendRequestButton;
    }
    public CustomEditorTable getParametersTable() {return parametersTable;}
    public JComboBox<String> getMediaTypeComboBox() {return mediaTypeComboBox;}
    public JButton getAddValueButton() {return addValueButton;}

    /** Resets the UI components of the EndpointGrid. */
    public void resetUI() {
        endpointList.setModel(new DefaultListModel<>());
        for (MouseListener event : endpointList.getMouseListeners()) {
            endpointList.removeMouseListener(event);
        }
        endpointsRequestTextArea.setText("");
        endpointsResponseTextArea.setText("");
        endpointParametersPanel.revalidate();
        endpointParametersPanel.repaint();
        endpointListPanel.revalidate();
        endpointListPanel.repaint();
        endpointRequestPanel.revalidate();
        endpointRequestPanel.repaint();
        endpointResponsePanel.revalidate();
        endpointResponsePanel.repaint();
        getMediaTypeComboBox().removeAllItems();
        for (ActionListener event : mediaTypeComboBox.getActionListeners()) {
            mediaTypeComboBox.removeActionListener(event);
        }
        for (ActionListener listener : addValueButton.getActionListeners()) {
            addValueButton.removeActionListener(listener);
        }
        parametersTable.resetNode();
        parametersTable.clearTableRows();
        endpointsSendRequestButton.setText("Send Request");
        endpointsSendRequestButton.revalidate();
        endpointsSendRequestButton.repaint();
        parametersTable.revalidate();
        parametersTable.repaint();
        for (ActionListener event : endpointsSendRequestButton.getActionListeners()) {
            endpointsSendRequestButton.removeActionListener(event);
        }
    }

}
