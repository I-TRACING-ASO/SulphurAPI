package v1.sulphurapi.handlers.openapi;

import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.Schema;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.structure.APILoader;
import v1.sulphurapi.structure.bulk.BulkGrid;
import v1.sulphurapi.handlers.endpoints.EndpointsHandler;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

public class SchemaHandler {
    BulkGrid bulkGrid;
    EndpointsHandler endpointsHandler;

    /**
     * Constructor for SchemaHandler that initializes the user grid and endpoints panel UI.
     * It also sets up action listeners for the load endpoint button and send bulk button.
     *
     * @param bulkGrid the UserGrid instance to be used for displaying parameters
     * @param endpointsHandler the EndpointsPanelUI instance to be used for managing endpoints
     */
    public SchemaHandler(APILoader apiLoader) {
        this.bulkGrid = apiLoader.getGuiManager().getUsers();
        this.endpointsHandler = apiLoader.getEndpointsHandler();
        for (ActionListener listener : bulkGrid.getLoadEndpointButton().getActionListeners()) {
            bulkGrid.getLoadEndpointButton().removeActionListener(listener);
        }
        bulkGrid.getLoadEndpointButton().addActionListener(e -> {loadSchemaToUserGrid();});
        for (ActionListener listener : bulkGrid.getSendBulkButton().getActionListeners()) {
            bulkGrid.getSendBulkButton().removeActionListener(listener);
        }
        bulkGrid.getSendBulkButton().addActionListener(e -> {sendBulkEvent();});
    }

    /**
     * This method loads the schema properties from the selected media type in the endpoints panel UI
     * and updates the user grid's combo box with the available properties.
     */
    @SuppressWarnings("rawtypes")
    public void loadSchemaToUserGrid() {
        Map<String, Schema> props;
        if (endpointsHandler.getPropertiesMap().isEmpty() ||
                endpointsHandler.getSelectedMediaType() == null ||
                endpointsHandler.getPropertiesMap().get(endpointsHandler.getSelectedMediaType()) == null ||
                (props = endpointsHandler.getPropertiesMap().get(endpointsHandler.getSelectedMediaType()).getProperties()).isEmpty()) {
            bulkGrid.updateComboBox(new Vector<>());
            return ;
        }
        Vector<String> properties = new Vector<>();
        properties.addAll(endpointsHandler.getParametersMap().keySet());
        properties.addAll(props.keySet());
        bulkGrid.updateComboBox(properties);
    }

    public void sendBulkEvent() {
        Map<String, ListModel<String>> parametersValuesMap = bulkGrid.getParametersValuesMap();
        JTable table= bulkGrid.getTable();

        table.setTableHeader(new JTableHeader(table.getColumnModel()));
        Vector<String> values = new Vector<>();
        for (String key : parametersValuesMap.keySet()) {
            if (parametersValuesMap.get(key).getSize() == 0) {
                continue;
            }
            values.add(key);
        }

        Vector<Vector<String>> data = new Vector<>();
        int maxSize= 0;
        for(String key : parametersValuesMap.keySet()) {
            if (parametersValuesMap.get(key).getSize() > maxSize) {
                maxSize = parametersValuesMap.get(key).getSize();
            }
        }
        for (int j = 0; j < maxSize; j++) {
            Vector<String> row = new Vector<>();
            for (String value : values) {
                if (parametersValuesMap.get(value).getSize() > j) {
                    row.add(parametersValuesMap.get(value).getElementAt(j));
                } else {
                    row.add("");
                }
            }
            data.add(row);
        }

        table.setModel(new DefaultTableModel(data, values));
        tableResizeRenderer(table);
    }

    /**
     * This method resizes the columns of a JTable to fit the content.
     * It iterates through each column, calculates the preferred width based on the header and cell renderers,
     * and sets the preferred width for each column.
     *
     * @param table the JTable whose columns need to be resized
     */
    public static void tableResizeRenderer(JTable table) {
        for (int column = 0; column < table.getColumnCount(); column++)
        {
            TableColumn tableColumn = table.getColumnModel().getColumn(column);
            int preferredWidth = tableColumn.getMinWidth();
            int maxWidth = tableColumn.getMaxWidth();

            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, tableColumn.getHeaderValue(), false, false, -1, column
            );
            preferredWidth = Math.max(preferredWidth, headerComp.getPreferredSize().width);

            for (int row = 0; row < table.getRowCount() ; row++) {
                TableCellRenderer cellRenderer = table.getCellRenderer(row, column);
                Component c = table.prepareRenderer(cellRenderer, row, column);
                int width = c.getPreferredSize().width + table.getIntercellSpacing().width;
                preferredWidth = Math.max(preferredWidth, width);

                if (preferredWidth >= maxWidth)
                {
                    preferredWidth = maxWidth;
                    break;
                }
            }

            tableColumn.setPreferredWidth(preferredWidth);
        }
    }

    /**
     * This method retrieves the schema from the content based on the provided media type.
     * If the media type is not specified, it returns the schema from the first entry in the content.
     * @param content the Content object containing media types and schemas
     * @param mediaType the media type to look for in the content
     * @return the Schema object if found, otherwise null
     */
    public static Schema<?> getSchemaFromContent(Content content, String mediaType) {
        if (!mediaType.isEmpty()) {
            if (content.get(mediaType) != null) {
                if (content.get(mediaType).getSchema() != null)
                    return content.get(mediaType).getSchema();
            }
        }
        else if (content.firstEntry().getValue() != null) {
                if (content.firstEntry().getValue().getSchema() != null) {
                    return content.firstEntry().getValue().getSchema();
                }
        }
        return null;
    }

    /**
     * This method retrieves the schema from a reference string.
     * It checks if the reference is in the format of a component schema and returns the corresponding Schema object.
     * @param ref the reference string in the format of "#/components/schemas/{componentName}"
     * @return the Schema object if found, otherwise null
     *
     */
    public static Schema<?> getValueOfSchemaComponent(String ref) {
        if (SAPI.getLoadedAPI() == null)
            return null;
        String[] parts = ref.split("/");
        if (parts.length == 4 && parts[1].equals("components") && parts[2].equals("schemas")) {
            String componentName = parts[3];
            if (SAPI.getLoadedAPI().getComponents() != null &&
                SAPI.getLoadedAPI().getComponents().getSchemas() != null) {
                return SAPI.getLoadedAPI().getComponents().getSchemas().get(componentName);
            }
        }
        return null;
    }

}
