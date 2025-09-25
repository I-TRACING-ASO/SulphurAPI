package v1.sulphurapi.structure.auth;

import javax.swing.*;
import java.awt.*;

public class AuthGrid {
    private final JPanel mainPanel;
    private final JPanel upLeftPanel;
    private final JPanel downLeftPanel;
    private final JPanel rightPanel;
    private final JList<String> authSchemes;
    private final JTextArea logArea;
    private final JButton tokenUrl;
    private final JLabel apiKeyLabel;
    private final JTextField apiKeyField;

    public AuthGrid(JPanel mainPanel) {
        this.mainPanel = mainPanel;

        this.upLeftPanel = new JPanel();
        this.upLeftPanel.setName("jpanel.upLeftPanel");
        this.upLeftPanel.setLayout(new GridBagLayout());
        this.upLeftPanel.setMinimumSize(new Dimension(mainPanel.getWidth()/4, mainPanel.getHeight()/4));
        this.upLeftPanel.setPreferredSize(new Dimension(mainPanel.getWidth()/2, mainPanel.getHeight()/2));
        this.upLeftPanel.setBorder(BorderFactory.createTitledBorder("Schemes"));

        this.downLeftPanel = new JPanel();
        this.downLeftPanel.setName("jpanel.downLeftPanel");
        this.downLeftPanel.setLayout(new GridBagLayout());
        this.downLeftPanel.setMinimumSize(new Dimension(mainPanel.getWidth()/4, mainPanel.getHeight()/4));
        this.downLeftPanel.setPreferredSize(new Dimension(mainPanel.getWidth()/2, mainPanel.getHeight()/2));
        this.downLeftPanel.setBorder(BorderFactory.createTitledBorder("Settings"));

        this.tokenUrl = new JButton("Load Token URL to Endpoint Grid");
        this.tokenUrl.setName("button.loadTokenUrl");
        this.tokenUrl.setEnabled(false);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(5, 5, 5, 5);

        this.downLeftPanel.add(tokenUrl,  gbc);

        this.apiKeyLabel = new JLabel("API Key:");
        this.apiKeyLabel.setName("label.apiKey");

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0;
        gbc.weightx = 0;

        this.downLeftPanel.add(apiKeyLabel, gbc);

        this.apiKeyField = new JTextField();
        this.apiKeyField.setName("textfield.apiKey");
        this.apiKeyField.setEditable(false);
        this.apiKeyField.setEnabled(false);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0;
        gbc.weightx = 1;

        this.downLeftPanel.add(apiKeyField, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        this.downLeftPanel.add(Box.createGlue(), gbc);

        this.rightPanel = new JPanel();
        this.rightPanel.setName("jpanel.rightPanel");
        this.rightPanel.setLayout(new GridBagLayout());
        this.rightPanel.setMinimumSize(new Dimension(mainPanel.getWidth()/4, mainPanel.getHeight()));
        this.rightPanel.setPreferredSize(new Dimension(mainPanel.getWidth()/2, mainPanel.getHeight()));
        this.rightPanel.setBorder(BorderFactory.createTitledBorder("Visualization"));

        this.mainPanel.add(upLeftPanel, new GridBagConstraints() {{
            gridx = 0;
            gridy = 0;
            fill = GridBagConstraints.BOTH;
            weightx = 1;
            weighty = 1;
            insets = new Insets(5, 0, 5, 0);
        }});

        this.mainPanel.add(downLeftPanel, new GridBagConstraints() {{
            gridx = 0;
            gridy = 1;
            fill = GridBagConstraints.BOTH;
            weightx = 1;
            weighty = 1;
            insets = new Insets(0, 0, 5, 0);
        }});

        this.mainPanel.add(rightPanel, new GridBagConstraints() {{
            gridx = 1;
            gridy = 0;
            gridheight = 2;
            fill = GridBagConstraints.BOTH;
            weightx = 1;
            weighty = 1;
            insets = new Insets(5, 5, 5, 0);
        }});

        this.authSchemes = new JList<>();
        this.authSchemes.setName("jlist.authSchemes");
        this.authSchemes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.authSchemes.setLayoutOrientation(JList.VERTICAL);

        this.upLeftPanel.add(new JScrollPane(this.authSchemes), new GridBagConstraints() {{
            gridx = 0;
            gridy = 0;
            fill = GridBagConstraints.BOTH;
            weightx = 1;
            weighty = 1;
        }});

        this.logArea = new JTextArea();
        this.logArea.setName("textarea.logArea");
        this.logArea.setEditable(false);
        this.logArea.setLineWrap(true);
        this.logArea.setWrapStyleWord(true);

        this.rightPanel.add(new JScrollPane(this.logArea), new GridBagConstraints() {{
            gridx = 0;
            gridy = 0;
            fill = GridBagConstraints.BOTH;
            weightx = 1;
            weighty = 1;
        }});
    }

    public JList<String> getAuthSchemesList() {
        return authSchemes;
    }

    public JPanel getDownLeftPanel() {
        return downLeftPanel;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JPanel getRightPanel() {
        return rightPanel;
    }

    public JPanel getUpLeftPanel() {
        return upLeftPanel;
    }

    public JTextArea getLogArea() {
        return logArea;
    }

    public JButton getLoadTokenUrlButton() {
        return tokenUrl;
    }

    public JLabel getApiKeyLabel() {return apiKeyLabel;}

    public JTextField getApiKeyField() {return apiKeyField;}

    /** Reset main auth Grid UI components */
    public void resetUI() {
        this.authSchemes.setModel(new DefaultListModel<>());
        this.logArea.setText("");
        this.tokenUrl.setText("Load Token URL to Endpoint Grid");
        this.apiKeyField.setText("");
        this.apiKeyField.setEditable(false);
        this.apiKeyField.setEnabled(false);
    }
}
