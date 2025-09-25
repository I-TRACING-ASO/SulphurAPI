package v1.sulphurapi.handlers.endpoints;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.json.JSONObject;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.handlers.http.ContentTypeHandler;
import v1.sulphurapi.interfaces.GUIActionInterface;
import v1.sulphurapi.listeners.endpoints.EndpointListMouseListener;
import v1.sulphurapi.listeners.endpoints.RequestTextAreaMouseListener;
import v1.sulphurapi.structure.APILoader;
import v1.sulphurapi.structure.endpoints.EndpointGrid;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import static v1.sulphurapi.handlers.openapi.SchemaHandler.getSchemaFromContent;

public class EndpointsHandler implements GUIActionInterface {
    private final Paths paths;
    private JPopupMenu menu;
    private final EndpointGrid grid;
    private Map<String, Schema<?>> propertiesMap;
    private String currentLoadedPath;
    private Map<String, Parameter> parametersMap;
    private ContentTypeHandler contentTypeHandler;
    /// TODO: Handle multiple servers in the future.
    private String selectedServer;

    public EndpointsHandler(APILoader apiLoader) {
        this.grid = apiLoader.getGuiManager().getEndpoints();
        this.selectedServer = SAPI.getLoadedAPI().getServers().getFirst().getUrl();
        paths = SAPI.getLoadedAPI().getPaths();
        if (!paths.isEmpty()) {

            DefaultListModel<String> listModel = new DefaultListModel<>();
            for (String path : paths.keySet()) {listModel.addElement(path);}

            grid.getEndpointList().setModel(listModel);
            grid.getEndpointList().addListSelectionListener(e -> grid.getEndpointList().setSelectionBackground(Color.lightGray));

            grid.getMediaTypeComboBox().addActionListener(e -> updateParametersTable());

            menu = new JPopupMenu("Actions");

            grid.getEndpointList().addMouseListener(new EndpointListMouseListener(this));
            grid.getEndpointsRequestTextArea().addMouseListener(new RequestTextAreaMouseListener(this));
            contentTypeHandler = new ContentTypeHandler();
        }
    }

    /**
     * Returns the URL of the selected endpoint by combining the server URL and the current loaded path.
     * @return the full URL of the selected endpoint.
     * TODO: Handle multiple servers.
     */
    public String getSelectedEndpointURL() {
        return SAPI.getLoadedAPI().getServers().getFirst().getUrl() + getEndpointURL();
    }

    /**
     * Creates a submenu for the current loaded path with options for each HTTP method (GET, POST, PUT, DELETE, PATCH, OPTIONS).
     * @return the created JMenu instance containing the options for the current loaded path.
     */
    public JMenu createSubMenu() {
        PathItem item = paths.get(currentLoadedPath);
        SAPI.getAPI().logging().raiseInfoEvent("Current Loaded Path: " + item + " paths : " + paths);
        JMenu optionsSubMenu = new JMenu("Options");
        if (item.getGet() != null) {
            operationHandling("GET", item.getGet(), optionsSubMenu);
        }
        if (item.getPost() != null) {
            operationHandling("POST", item.getPost(), optionsSubMenu);
        }
        if (item.getPut() != null) {
            operationHandling("PUT", item.getPut(), optionsSubMenu);
        }
        if (item.getDelete() != null) {
            operationHandling("DELETE", item.getDelete(), optionsSubMenu);
        }
        if (item.getPatch() != null) {
            operationHandling("PATCH", item.getPatch(), optionsSubMenu);
        }
        if (item.getOptions() != null) {
            operationHandling("OPTIONS", item.getOptions(), optionsSubMenu);
        }
        return optionsSubMenu;
    }


    /**
     * Method that will generate the action for selecting a specific HTTP method.
     * @param method the HTTP method to be handled (GET, POST, PUT, DELETE, PATCH, OPTIONS)
     * @param operation the Operation object that contains the details of the HTTP method
     * @param optionsSubMenu the JMenu where the options for the HTTP method will be added
     */
    private void operationHandling(String method, Operation operation, JMenu optionsSubMenu) {
        JMenuItem operationOption = new JMenuItem(method);
        operationOption.addActionListener(e -> runAsyncAction(() -> {
            propertiesMap = new LinkedHashMap<>();
            parametersMap = new LinkedHashMap<>();
            fillPropertiesMapFromOperation(operation);
            fillParametersMapFromOperation(operation);
            grid.getMediaTypeComboBox().removeAllItems();
            for (String key : propertiesMap.keySet()) {
                grid.getMediaTypeComboBox().addItem(key);
            }
            updateParametersTable();
            for (ActionListener listener : grid.getEndpointsSendRequestButton().getActionListeners()) {
                grid.getEndpointsSendRequestButton().removeActionListener(listener);
            }
            grid.getEndpointsSendRequestButton().setText("Send "+method+" Request To : " + currentLoadedPath);
            grid.getEndpointsSendRequestButton().addActionListener(e1 -> runAsyncAction(() -> {
                String body = "";
                List<String> headers = calculateHeaders();
                if (grid.getMediaTypeComboBox().getSelectedItem() != null) {
                    body = contentTypeHandler.calculateBody((JSONObject) getValuesFromTable(), getSelectedMediaType());
                    headers.add(getContentType());
                }
                EndpointsActionsHandler handler = new EndpointsActionsHandler();
                handler.getActionHandler(getSelectedEndpointURL(), method, grid.getEndpointsRequestTextArea(), grid.getEndpointsResponseTextArea(), body, headers);
            }, "Sent "+method+" Request"));
        }, "Filled Properties Map and added Event Listener to Send Request Button"));
        optionsSubMenu.add(operationOption);
    }

    /**
     * Will generate a list of header lines based on the parameters in parameters table
     * @return a list of string in format: ["header1: value1", "header2: value2", ...]
     */
    private List<String> calculateHeaders() {
        List<String> headers = new ArrayList<>();
        JTable table = grid.getParametersTable();
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!table.getValueAt(i,2).toString().equals("header")) {
                continue;
            }
            String key = table.getValueAt(i, 0).toString();
            String value = table.getValueAt(i, 1).toString();
            if (!key.isEmpty() && !value.isEmpty()) {
                headers.add(key+": " + value);
            }
        }
        return headers;
    }

    /**
     * Fills the parametersMap with the parameters from the operation.
     * The parametersMap will be filled in the format: <code>{"parameterName" : Parameter object}</code>
     *
     * @param item the Operation object that contains the parameters to be filled into the map.
     */
    private void fillParametersMapFromOperation(Operation item) {
        if (item == null || item.getParameters() == null) {return;}
        for (Parameter parameter : item.getParameters()) {
            parametersMap.put(parameter.getName(), parameter);
        }
    }


    /**
     * fills propertyMap in the format : <code>{"application/json" : {Schema object}, "application/x-www-form-urlencoded" : {Schema object}}</code>
     * @param item the Operation object that contains the request body content to be filled into the map.
     */
    public void fillPropertiesMapFromOperation(Operation item) {
        if (item == null || item.getRequestBody() == null) {return;}
        Content content = item.getRequestBody().getContent();
        for (String key : content.keySet()) {
            propertiesMap.put(key, getSchemaFromContent(content, key));
        }
    }

    private void fillTableFromPropertiesMap() {
        Schema<?> schema = propertiesMap.get(getSelectedMediaType());
        if (schema == null) {
            return ;
        }
        grid.getParametersTable().setTableSchema(schema);
    }

    private void fillTableWithPathParameters() {
        ArrayList<Parameter> parameters = new ArrayList<>();
        for (String key : parametersMap.keySet()) {
            parameters.add(parametersMap.get(key));
        }
        grid.getParametersTable().setTableParameters(parameters);
    }


    /**
     * Extracts the data of the parameters Table
     * @return a map of key-value pairs where the key is the parameter name and the value is the parameter value.
     */
    private Object getValuesFromTable() {
        return grid.getParametersTable().getActiveNode().serializeNode();
    }

    /**
     * Returns the endpoint URL based on the current loaded path and the parameters in the parameters table.
     * It replaces path parameters in the URL with their values and appends query parameters as a query string.
     * @return the complete endpoint URL with replaced path parameters and appended query parameters.
     */
    private String getEndpointURL() {
        if (parametersMap.isEmpty()) {
            return this.currentLoadedPath;
        }
        StringBuilder endpointURL = new StringBuilder(this.currentLoadedPath);
        StringBuilder query = new StringBuilder();
        for (String parameter : parametersMap.keySet()) {
            TableModel model = grid.getParametersTable().getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i,0) == parameter) {
                    if (model.getValueAt(i,1) == null) {
                        continue;
                    }
                    if (model.getValueAt(i, 2).toString().equals("path")) {
                        endpointURL = new StringBuilder(endpointURL.toString().replace("{" +
                                parameter + "}", model.getValueAt(i, 1).toString()));
                    } else if (model.getValueAt(i, 2).toString().equals("query")) {
                        if (model.getValueAt(i,1) == null ||
                                model.getValueAt(i,1).toString().isEmpty()) {
                            continue;
                        }
                        if (query.isEmpty()) {
                            query.append("/?");
                        } else {
                            query.append("&");
                        }
                        query.append(model.getValueAt(i, 0).toString()).append("=").append(
                                URLEncoder.encode(model.getValueAt(i, 1).toString(), StandardCharsets.UTF_8));
                    }
                    break;
                }
            }
        }
        return endpointURL.toString() + query;
    }

    /**
     * This method updates the parameters table by clearing the editor components,
     * filling the table with properties from the properties map, and updating the types in the table.
     */
    private void updateParametersTable() {
        grid.getMediaTypeComboBox().hidePopup();
        grid.getParametersTable().clearTableRows();
        fillTableFromPropertiesMap();
        fillTableWithPathParameters();
    }

    /**
     * Returns the content type header based on the selected media type in the combo box.
     * If no media type is selected, it returns null.
     * @return the content type header string or null if no media type is selected.
     */
    private String getContentType() {
        if (grid.getMediaTypeComboBox().getSelectedItem() == null) {return null;}
        return "Content-Type: " + getSelectedMediaType();
    }

    /**
     * Returns the selected media type from the combo box in the grid.
     * If no item is selected, it returns an empty string.
     * @return the selected media type as a string.
     */
    public String getSelectedMediaType() {
        if (grid.getMediaTypeComboBox().getSelectedItem() == null) {return "";}
        return grid.getMediaTypeComboBox().getSelectedItem().toString();
    }

    public Map<String, Parameter> getParametersMap() {return parametersMap;}
    public Map<String, Schema<?>> getPropertiesMap() {return propertiesMap;}

    public EndpointGrid getGrid() {return grid;}
    public JPopupMenu getMenu() {return menu;}
    public String getCurrentLoadedPath() {return currentLoadedPath;}
    public void setCurrentLoadedPath(String currentLoadedPath) {this.currentLoadedPath = currentLoadedPath;}
}
