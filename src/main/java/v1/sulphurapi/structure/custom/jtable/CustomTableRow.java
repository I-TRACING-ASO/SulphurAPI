package v1.sulphurapi.structure.custom.jtable;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;

import java.util.*;


public class CustomTableRow {
    /** List of <code>CustomCell</code> objects representing the cells in the row. */
    private final List<CustomCell> cells;

     /** Schema of the row, used to determine the data of the cells. */
    private final Schema<?> schema;

    private final Parameter parameter;
    private final CustomEditorTableModel editorTableModel;
    private int rowIndex;

    /**Constructor for a Row of a CustomTable with 4 cells*/
    public CustomTableRow(String name, Schema<?> schema, String source, CustomEditorTableModel tableModel, int rowIndex) {
        this.rowIndex = rowIndex;
        this.editorTableModel = tableModel;
        this.parameter = null;
        this.schema = schema;
        this.cells = new ArrayList<>();
        this.getEditorTableModel().addRow(new Vector<>());
        cells.add(new CustomCell(0, this, name)); // parameter
        cells.add(new CustomCell(1, this, "")); // value
        cells.add(new CustomCell(2, this, source)); // source
        cells.add(new CustomCell(3, this, extractTypesFromSchema(schema))); // type
    }

    /**Constructor for a Row of a CustomTable with 4 cells for parameter*/
    public CustomTableRow(Parameter parameter,  CustomEditorTableModel tableModel, int rowIndex) {
        this.rowIndex = rowIndex;
        this.editorTableModel = tableModel;
        this.parameter = parameter;
        this.schema = parameter.getSchema();
        this.cells = new ArrayList<>();
        this.getEditorTableModel().addRow(new Vector<>());
        cells.add(new CustomCell(0, this, parameter.getName())); // parameter
        cells.add(new CustomCell(1, this, "")); // value
        cells.add(new CustomCell(2, this, parameter.getIn())); // source
        cells.add(new CustomCell(3, this, extractTypesFromSchema(parameter.getSchema()))); // type
    }

    public CustomCell getCell(int index) {
        return cells.get(index);
    }

    public Schema<?> getSchema() {
        return schema;
    }

    /**
     * Returns a list of Types from a schema
     *
     * @param schema the schema that will be parsed;
     * @return a string representation of the types in the schema, or an empty string if no types are found in the format ['type1','type2',...]
     */
    private String extractTypesFromSchema(Schema<?> schema) {
        Set<String> types = new HashSet<>();
        if (schema.getAnyOf() != null && !schema.getAnyOf().isEmpty()) {
            for (Schema<?> anyOf : schema.getAnyOf()) {
                if (anyOf.getType() != null) {
                    types.add(anyOf.getType());
                }
                if (anyOf.getTypes() != null && !anyOf.getTypes().isEmpty()) {
                    types.addAll(anyOf.getTypes());
                }
            }
        }
        if (types.isEmpty()){
            if (schema.getType() != null) {
                types.add(schema.getType());
            } else if (schema.getTypes() != null && !schema.getTypes().isEmpty()) {
                types.addAll(schema.getTypes());
            }
        }
        return types.toString();
    }


    public CustomEditorTableModel getEditorTableModel() {
        return editorTableModel;
    }

    public List<CustomCell> getCells() {
        return cells;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(int rowIndex) {
        this.rowIndex = rowIndex;
    }

    public int getColumnCount() {
        return editorTableModel.getColumnCount();
    }

    public ObjectDialog getParentDialog() {
        return editorTableModel.getParentDialog();
    }

    public Parameter getParameter() {
        return parameter;
    }
}
