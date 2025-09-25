package v1.sulphurapi.structure.custom.jtable;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import org.json.JSONArray;
import org.json.JSONObject;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;
import v1.sulphurapi.structure.custom.jtable.CustomEditorTable.CustomTableHeader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.*;
import java.util.stream.Collectors;

public class CustomEditorTableModel extends DefaultTableModel {

    /**
     * A map to hold the editor components for each cell in the table.
     * The key is a Pair of row and column indices, and the value is a Pair of TableCellEditor and JComponent.
     */
    private ArrayList<CustomTableRow> editorComponents;

    /** The parent node of this table model, which is an instance of <code>CustomTableNode</code>. */
    private final CustomTableNode parentNode;

     /** Flag holinding the state of whether the schema has been loaded lazily. */
    private Boolean lazySchemaLoaded;

     /** Constructor for CustomEditorTableModel. */
    public CustomEditorTableModel(CustomTableNode parentNode) {
        super(new Vector<>(CustomTableHeader.getColumnNames()), 0);
        this.parentNode = parentNode;
        this.lazySchemaLoaded = false;
        editorComponents = new ArrayList<>();
    }

    public List<CustomTableRow> getEditorComponents() {
        return editorComponents;
    }

    public void setEditorComponents(ArrayList<CustomTableRow> editorComponents) {
        this.editorComponents = editorComponents;
    }

    /**
     * Normalizes the model by sorting the rows with non-body cells first and updating the row indices.
     * This method is used to ensure that the table is displayed in a consistent order.
     */
    public void normalizeModel() {
        //sort the rows with non-body cells first
        editorComponents = editorComponents.stream()
                .sorted(Comparator.comparing(row -> row.getCell(CustomEditorTable.CustomTableHeader.getIndexByColumnName("Source"))
                .getValue()
                .startsWith("body")))
                .collect(Collectors.toCollection(ArrayList::new));

        for (int i = 0; i < editorComponents.size(); i++) {
            editorComponents.get(i).setRowIndex(i);
        }

        // Clear the existing rows in the table model
        setRowCount(0);
        for (CustomTableRow row : editorComponents) {
            Vector<Object> rowData = new Vector<>();
            for (int i = 0; i < row.getCells().size(); i++) {
                CustomCell cell = row.getCell(i);
                if (cell != null) {
                    rowData.add(cell.getValue());
                } else {
                    rowData.add(null);
                }
            }
            addRow(rowData);
        }
    }

    /**
     * Clears the body cells from the editor components.
     * This method filters out all rows that do not start with "body" in the "Source" column.
     */
    public void clearBodyCells() {
        editorComponents = editorComponents.stream().filter(
                        e -> !e
                                .getCell(CustomTableHeader.getIndexByColumnName("Source"))
                                .getValue()
                                .startsWith("body")).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Clears the parameter cells from the editor components.
     * This method filters out all rows that do not start with "body" in the "Source" column.
     */
    public void clearParameterCells() {
        editorComponents = editorComponents.stream().filter(
                        e -> e
                                .getCell(CustomTableHeader.getIndexByColumnName("Source"))
                                .getValue()
                                .startsWith("body"))
                                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Updates the schema rows in the editor components based on the provided table schema.
     * It clears the existing body cells and adds new rows for each property in the schema.
     * If the schema is null or has no properties, it does nothing.
     *
     * @param tableSchema The schema to update the rows with.
     */
    @SuppressWarnings("rawtypes")
    public void updateSchemaRows(Schema<?> tableSchema) {
        clearBodyCells();
        if (tableSchema != null) {
            if (tableSchema.getProperties() == null) {
                return ;
            }
            for (Map.Entry<String, Schema> entry : tableSchema.getProperties().entrySet()) {
                String source = "body";
                if (tableSchema.getRequired() != null && tableSchema.getRequired().contains(entry.getKey())) {
                    source += " (required)";
                }
                CustomTableRow row = new CustomTableRow(entry.getKey(), entry.getValue(), source, this, editorComponents.size());
                editorComponents.add(row);
            }
        }
        lazySchemaLoaded = true;
    }

    /**
     * Updates the parameter rows in the editor components based on the provided list of parameters.
     * It clears the existing parameter cells and adds new rows for each parameter in the list.
     * If the parameters list is null or empty, it does nothing.
     *
     * @param parameters The list of parameters to update the rows with.
     */
    public void updateParameterRows(ArrayList<Parameter> parameters) {
        clearParameterCells();
        if (parameters != null && !parameters.isEmpty()) {
            for (Parameter parameter : parameters) {
                editorComponents.add(new CustomTableRow(parameter, this, editorComponents.size()));
            }
        }
    }

    /**
     * Check the Map to retrieve the position of a specific component comparing the component reference.
     * @param component the JComponent to find in the this.activeModel.getEditorComponents() map
     * @return a List of Pair&lt;Integer, Integer> representing the row and column indices of the component
     */
    public ArrayList<CustomEditorTable.CellPosition> getPositionFromComponent(JComponent component) {
        ArrayList<CustomEditorTable.CellPosition> positions = new ArrayList<>();
        for (int x = 0; x < editorComponents.size(); x++){
            CustomTableRow row = editorComponents.get(x);
            for (int j = 0; j < row.getCells().size(); j++) {
                if (row.getCell(j).getComponent() == component) {
                    positions.add(new CustomEditorTable.CellPosition(x, j));
                }
            }
        }
        return positions;
    }

    @Override
    public void setValueAt(Object aValue, int row, int column) {
        super.setValueAt(aValue, row, column);
        if (row < 0 || row >= editorComponents.size()) {
            return;
        }
        editorComponents.get(row).getCell(column).setValue(aValue.toString());
    }

    public ObjectDialog getParentDialog() {
        if (parentNode != null) {
            return parentNode.getParentDialog();
        } else {
            return null;
        }
    }

    public Boolean getLazySchemaLoaded() {
        return lazySchemaLoaded;
    }

    public CustomTableNode getParentNode() {
        return parentNode;
    }

    /**
     * Serializes the model to a JSONObject.
     * It iterates through the editor components and retrieves the values from the "Parameter" and "Value" columns.
     * If the value is a CustomTableNode, it serializes it to a JSONObject or JSONArray.
     * If the value is a primitive type, it adds it directly to the JSONObject.
     *
     * @return A JSONObject representing the serialized model.
     */
    public JSONObject serializeModel() {
        JSONObject object = new JSONObject();
        for(CustomTableRow row : editorComponents) {
            if (row.getParameter() != null) {
                continue;
            }
            if (row.getCell(CustomTableHeader.getIndexByColumnName("Value")).getNode() != null) {
                CustomTableNode node = row.getCell(CustomTableHeader.getIndexByColumnName("Value")).getNode();
                if (node.isArray()) {
                    JSONArray array = (JSONArray) row.getCell(CustomTableHeader.getIndexByColumnName("Value")).getNode().serializeNode();
                    if (array.isEmpty()) {
                        continue;
                    }
                    object.put(row.getCell(CustomTableHeader.getIndexByColumnName("Parameter")).getValue(), array);
                } else {
                    JSONObject nodeObject = (JSONObject) row.getCell(CustomTableHeader.getIndexByColumnName("Value")).getNode().serializeNode();
                    if (nodeObject.isEmpty()) {
                        continue;
                    }
                    object.put(row.getCell(CustomTableHeader.getIndexByColumnName("Parameter")).getValue(), nodeObject);
                }
            } else {
                String value = row.getCell(CustomTableHeader.getIndexByColumnName("Value")).getValue();
                if (value == null || value.isEmpty()) {
                    continue;
                }
                object.put(row.getCell(CustomTableHeader.getIndexByColumnName("Parameter")).getValue(), value);
            }
        }
        return object;
    }
}
