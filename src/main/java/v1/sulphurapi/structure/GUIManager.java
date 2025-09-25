package v1.sulphurapi.structure;

import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.structure.auth.AuthGrid;
import v1.sulphurapi.structure.bulk.BulkGrid;
import v1.sulphurapi.structure.endpoints.EndpointGrid;
import v1.sulphurapi.interfaces.GUIActionInterface;

import javax.swing.*;
import java.awt.*;

public class GUIManager implements GUIActionInterface {
    private JPanel ui;
    private JButton selectFileButton;
    private JTextField pathField;
    private JLabel labelField;
    private JTabbedPane tabbedPane1;
    private JPanel authenticationPanel;
    private JPanel usersPanel;
    private JPanel endpointsPanel;
    private JPanel topPanel;

    private EndpointGrid endpoints;
    private BulkGrid users;
    private AuthGrid auth;

    public GUIManager() {

        this.ui = new JPanel();
        SAPI.loadUIPanel(ui);
        this.ui.setLayout(new GridBagLayout());
        GridBagConstraints uiConstraints = new GridBagConstraints();

        this.topPanel = new JPanel();
        topPanel.setLayout(new GridBagLayout());
        GridBagConstraints topPanelConstraints = new GridBagConstraints();

        this.labelField = new JLabel("Api File Path:");

        topPanelConstraints.gridx = 0;
        topPanelConstraints.gridy = 0;
        topPanelConstraints.weightx = 0;
        topPanelConstraints.weighty = 0;
        topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        topPanelConstraints.insets = new Insets(10, 5, 5, 0);
        this.topPanel.add(this.labelField, topPanelConstraints);

        this.pathField = new JTextField();
        topPanelConstraints.gridx = 1;
        topPanelConstraints.gridy = 0;
        topPanelConstraints.weightx = 1;
        topPanelConstraints.weighty = 0;
        topPanelConstraints.insets = new Insets(10, 5, 5, 5);
        topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.topPanel.add(this.pathField, topPanelConstraints);

        this.selectFileButton = new JButton("Select File");
        topPanelConstraints.gridx = 2;
        topPanelConstraints.gridy = 0;
        topPanelConstraints.weightx = 0;
        topPanelConstraints.weighty = 0;
        topPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        topPanelConstraints.insets = new Insets(10, 0, 5, 5);
        this.topPanel.add(this.selectFileButton, topPanelConstraints);


        uiConstraints.gridx = 0;
        uiConstraints.gridy = 0;
        uiConstraints.weightx = 0;
        uiConstraints.weighty = 0;
        uiConstraints.fill = GridBagConstraints.HORIZONTAL;
        this.ui.add(this.topPanel, uiConstraints);

        this.tabbedPane1 = new JTabbedPane();
        uiConstraints.gridy = 1;
        uiConstraints.weightx = 1;
        uiConstraints.weighty = 1;
        uiConstraints.fill = GridBagConstraints.BOTH;
        this.ui.add(this.tabbedPane1, uiConstraints);

        this.endpointsPanel = new JPanel();
        this.endpointsPanel.setLayout(new GridBagLayout());
        this.tabbedPane1.addTab("Endpoints", endpointsPanel);
        this.authenticationPanel = new JPanel();
        this.authenticationPanel.setLayout(new GridBagLayout());
        this.tabbedPane1.addTab("Authentication", authenticationPanel);
        this.usersPanel = new JPanel();
        this.usersPanel.setLayout(new GridBagLayout());
        this.tabbedPane1.addTab("Users", usersPanel);

        this.endpoints = new EndpointGrid(endpointsPanel);
        this.users = new BulkGrid(usersPanel);
        this.auth = new AuthGrid(authenticationPanel);

        selectFileButton.addActionListener(e -> {
            endpoints.resetUI();
            users.resetUI();
            auth.resetUI();
            APILoader apiLoader = new APILoader(this, pathField);
        });
    }

    public Component getUi() {
        return this.ui;
    }

    public JPanel getUI() {
        return ui;
    }

    public JButton getSelectFileButton() {
        return selectFileButton;
    }

    public JTextField getPathField() {
        return pathField;
    }

    public JLabel getLabelField() {
        return labelField;
    }

    public JTabbedPane getTabbedPane1() {
        return tabbedPane1;
    }

    public JPanel getAuthenticationPanel() {
        return authenticationPanel;
    }

    public JPanel getUsersPanel() {
        return usersPanel;
    }

    public JPanel getEndpointsPanel() {
        return endpointsPanel;
    }

    public EndpointGrid getEndpoints() {
        return endpoints;
    }

    public AuthGrid getAuth() {
        return auth;
    }

    public BulkGrid getUsers() {
        return users;
    }
}
