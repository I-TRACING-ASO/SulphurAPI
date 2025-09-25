package v1.sulphurapi.handlers.auth;

import com.google.common.collect.ImmutableBiMap;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.apache.commons.lang3.tuple.MutableTriple;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.structure.APILoader;
import v1.sulphurapi.structure.auth.AuthGrid;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionListener;
import java.util.HashMap;
import io.swagger.v3.oas.models.security.SecurityScheme.In;

public class SecurityHandler {

    /**
     * Map holding the schemes for authentication related security requirements.
     * */
    private final HashMap<String, SecurityScheme> securityRequirements;

    /** String holding the current selected scheme */
    private String selectedSecurityScheme;

    /** Holds the In, the Value of the query/header that holds API-Key, the value of the API key */
    public static MutableTriple<In, String, String> APIKeyData;

    /** UI Grid that this handler will update of type <code>AuthGrid</code> */
    private AuthGrid authGrid;

    /** APILoader instance that this handler will use to access the GUIManager */
    private APILoader apiLoader;

    /** Current list data model modified by the handler */
    private final DefaultListModel<String> securitySchemeListModel;

    /** BiMap holding the possible types of security handled by OpenAPI Specification 3.1.0 */
    private static final ImmutableBiMap<Integer, String> types = new ImmutableBiMap.Builder<Integer, String>()
            .put(0, "apiKey").put(1, "http").put(2, "mutualTLS").put(3, "oauth2").put(4, "openIdConnect").build();

    /**
     * Constructor for the SecurityHandler class.
     * @param apiLoader the APILoader instance that will be used to access the GUIManager and other components.
     *  */
    public SecurityHandler(APILoader apiLoader) {
        this.apiLoader = apiLoader;
        this.authGrid = apiLoader.getGuiManager().getAuth();
        securitySchemeListModel = new DefaultListModel<>();
        authGrid.getAuthSchemesList().setModel(securitySchemeListModel);
        if (SAPI.getLoadedAPI() != null && SAPI.getLoadedAPI().getComponents() != null) {
            securityRequirements = new HashMap<>(SAPI.getLoadedAPI().getComponents().getSecuritySchemes());
            for (String key : securityRequirements.keySet()) {
                securitySchemeListModel.addElement(key);
            }
            for (ListSelectionListener listener : authGrid.getAuthSchemesList().getListSelectionListeners()) {
                authGrid.getAuthSchemesList().removeListSelectionListener(listener);
            }
            authGrid.getAuthSchemesList().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    if (e.getSource() instanceof JList) {
                        JList<String> list = (JList<String>) e.getSource();
                        setSelectedSecurityScheme(list.getSelectedIndex());
                        if (selectedSecurityScheme != null && !selectedSecurityScheme.isEmpty() && securityRequirements.containsKey(selectedSecurityScheme)) {
                            updateVisualization();
                            updateButton();
                            updateAPIKeyField();
                        }
                    }
                }
            });
            APIKeyData = new MutableTriple<>();
            this.authGrid.getApiKeyField().getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    updateAPIKeyField(e);
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    updateAPIKeyField(e);
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    updateAPIKeyField(e);
                }
                private void updateAPIKeyField(DocumentEvent e) {
                    APIKeyData.setRight(authGrid.getApiKeyField().getText());
                }
            });
        } else {
            securityRequirements = null;
        }
    }

    /** Updates the button state based on the selected security scheme */
    private void updateButton() {
        if (securityRequirements.get(selectedSecurityScheme).getFlows() == null ||
                securityRequirements.get(selectedSecurityScheme).getFlows().getPassword() == null ||
                    securityRequirements.get(selectedSecurityScheme).getFlows().getPassword().getTokenUrl() == null) {
            authGrid.getLoadTokenUrlButton().setEnabled(false);
            authGrid.getLoadTokenUrlButton().setText("No Token URL for " + selectedSecurityScheme);
            return ;
        }
        String tokenUrl = securityRequirements.get(selectedSecurityScheme).getFlows().getPassword().getTokenUrl();
        authGrid.getLoadTokenUrlButton().setEnabled(true);
        authGrid.getLoadTokenUrlButton().setText("Load Token URL for " + selectedSecurityScheme);
        for (ActionListener listener : authGrid.getLoadTokenUrlButton().getActionListeners()) {
            authGrid.getLoadTokenUrlButton().removeActionListener(listener);
        }
        authGrid.getLoadTokenUrlButton().addActionListener(e -> {
            apiLoader.getGuiManager().getTabbedPane1().setSelectedIndex(0);
            ListModel<String> model = apiLoader.getGuiManager().getEndpoints().getEndpointList().getModel();
            for (int i = 0; i < model.getSize(); i++) {
                if (model.getElementAt(i).contains(tokenUrl)) {
                    apiLoader.getGuiManager().getEndpoints().getEndpointList().setSelectedIndex(i);
                    break;
                }
            }
        });
    }

    /** Updates the API Key field based on the selected security scheme */
    private void updateAPIKeyField() {
        authGrid.getApiKeyField().setText("");
        APIKeyData.setLeft(null);
        APIKeyData.setMiddle(null);
        APIKeyData.setRight(null);
        if (selectedSecurityScheme == null || selectedSecurityScheme.isEmpty() || !securityRequirements.containsKey(selectedSecurityScheme)) {
            authGrid.getApiKeyField().setEnabled(false);
            authGrid.getApiKeyField().setEditable(false);
            return ;
        }
        SecurityScheme.Type type = securityRequirements.get(selectedSecurityScheme).getType();
        if (type == null || !type.equals(SecurityScheme.Type.APIKEY)) {
            authGrid.getApiKeyField().setEnabled(false);
            authGrid.getApiKeyField().setEditable(false);
            return ;
        }
        authGrid.getApiKeyField().setEnabled(true);
        authGrid.getApiKeyField().setEditable(true);
        APIKeyData.setLeft(securityRequirements.get(selectedSecurityScheme).getIn());
        APIKeyData.setMiddle(securityRequirements.get(selectedSecurityScheme).getName());
    }

    /** updates the logArea of the panel with the selected scheme data */
    private void updateVisualization() {
        authGrid.getLogArea().setText(securityRequirements.get(selectedSecurityScheme).toString());
    }

    /** Updates the Selected Security Scheme with a <code>String</code> input */
    public void setSelectedSecurityScheme(String selectedSecurityScheme) {
        this.selectedSecurityScheme = selectedSecurityScheme;
    }

    /** Updates the Selected Security Scheme with a <code>int</code> input*/
    public void setSelectedSecurityScheme(int index) {
        if (index >= 0 && index < securitySchemeListModel.size()) {
            this.selectedSecurityScheme = securitySchemeListModel.getElementAt(index);
        } else {
            this.selectedSecurityScheme = null;
        }
    }

    /** Retrieves the current Selected Security Scheme */
    public String getSelectedSecurityScheme() {
        return selectedSecurityScheme;
    }

    /** Retrieves the current Security Scheme */
    public SecurityScheme getCurrentSecurityScheme() {
        if (securityRequirements != null && selectedSecurityScheme != null) {
            return securityRequirements.get(selectedSecurityScheme);
        }
        return null;
    }

    /** Clears the JList of all its values */
    public void clearSecurityRequirements() {
        this.securityRequirements.clear();
    }

}
