package v1.sulphurapi.structure.custom.jtable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.Parameter;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;
import v1.sulphurapi.structure.custom.dialog.ObjectDialogCellEditor;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.*;
import java.util.List;


public class CustomEditorTable extends JTable {

    /** Cell position record to hold the row and column indices of a cell. */
    public record CellPosition(int row, int column) {}

     /**  The schema of the table, which defines the structure of the data in the table. */
    private Schema<?> tableSchema;

     /** List of the parameters from the OpenAPI schema that are displayed in the table. */
    private ArrayList<Parameter> tableParameters;

     /** Main node of the table holding the data of the table for later serialization and deserialization. */
    private CustomTableNode activeNode;

     /** Reference to the dialog winodw used for editing the table cells. */
    private final ObjectDialog sharedDialog;

    public static class CustomTableHeader {
        /**
         * A map to hold the indices of columns by their names.
         * This allows for quick access to column indices based on their names.
         */
        private static final BiMap<String, Integer> columnIndices = HashBiMap.create();

        /**
         * Retrieves the column indices map, initializing it if it is empty.
         * This method ensures that the column names are set up correctly before use.
         * @return a BiMap containing column names as keys and their indices as values
         */
        public static BiMap<String, Integer> getColumnIndices() {
            if (columnIndices.isEmpty()) {
                Vector<String> columnNames = new Vector<>(List.of("Parameter", "Value", "Source", "Type"));
                for (int i = 0; i < columnNames.size(); i++) {
                    columnIndices.put(columnNames.get(i), i);
                }
            }
            return columnIndices;
        }

        /**
         * Retrieves the index of a column by its name.
         * @param columnName the name of the column
         * @return the index of the column, or -1 if the column does not exist
         */
        public static int getIndexByColumnName(String columnName) {
            if (!getColumnIndices().isEmpty() && columnIndices.containsKey(columnName)) {
                return columnIndices.get(columnName);
            } else {
                return -1;
            }
        }

        public static Set<String> getColumnNames() {
            return CustomTableHeader.getColumnIndices().keySet();
        }

    }

    /**
     * Constructor for the <code>CustomEditorTable</code> class.
     * Initializes the table with a default model and sets up the editor components map.
     * This Custom Implementation allows for custom cell editing with dialog components while
     * keeping track of Individual cell editors and their associated components in a Map.
     */
    public CustomEditorTable(ObjectDialog dialog) {
        super();
        tableParameters = new ArrayList<>();
        tableSchema = null;
        this.getTableHeader().setReorderingAllowed(false);
        this.getTableHeader().setResizingAllowed(true);
        this.putClientProperty("terminateEditOnFocusLost", true);
        sharedDialog = dialog;
        resetNode();
        this.revalidate();
        this.repaint();
    }

    /**
     * Override of the <code>JTable.editCellAt</code> method to handle DialogCellEditor.
     * @param row     the row to be edited
     * @param column  the column to be edited
     * @param e       event to pass into <code>shouldSelectCell</code>;
     *                  note that as of Java 2 platform v1.2, the call to
     *                  <code>shouldSelectCell</code> is no longer made
     * @return <code>true</code> if editing was started; <code>false</code> otherwise
     */
    @Override
    public boolean editCellAt(int row, int column, EventObject e) {
        if (activeNode.getActiveChild().getEditorComponents().get(row) != null &&
                activeNode.getActiveChild().getEditorComponents().get(row).getCell(column) != null) {
            if (getCellEditor(row, column) == null) {
                return false;
            }
            if (activeNode.getActiveChild().getEditorComponents().get(row).getCell(column).getEditor() instanceof ObjectDialogCellEditor editor) {
                if (cellEditor != null && !cellEditor.stopCellEditing()) {
                    return false;
                }
                if (row >= getRowCount() || column < 0 || column >= getColumnCount()) {
                    return false;
                }
                if (!isCellEditable(row, column))
                    return false;

                editor.getParentCell().getParentDialog().setLoadedNode(editor.getParentCell().getNode());
                editor.getParentCell().getNode().setLazyObjectSchemaTrigger(editor.getParentCell().getLazyObjectSchemaTrigger());
                this.editorComp = prepareDialogEditor(editor, row, column);
                if (editorComp == null) {
                    removeEditor();
                    return false;
                }
                this.editorComp.validate();
                this.editorComp.repaint();

                setCellEditor(editor);
                setEditingRow(row);
                setEditingColumn(column);
                editor.addCellEditorListener(this);
                sharedDialog.showDialog();
                this.editorComp = editor.getDialog();
                return true;
            }
        }
        return super.editCellAt(row, column, e);
    }

    public Component prepareDialogEditor(TableCellEditor editor, int row, int column) {
        super.prepareEditor(editor, row, column);
        return new JTextField();
    }

    /**
     * Override of the <code>JTable.prepareRenderer</code> method to set alternating row colors.
     */
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        if (row % 2 == 0) {
            c.setBackground(new Color(210,210,210));
        } else {
            c.setBackground(Color.white);
        }
        return c;
    }

    @Override
    public TableCellEditor getCellEditor() {
        return super.getCellEditor();
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        if (activeNode.getActiveChild().getEditorComponents().get(row) != null && activeNode.getActiveChild().getEditorComponents().get(row).getCell(column) != null) {
            return activeNode.getActiveChild().getEditorComponents().get(row).getCell(column).getEditor();
        }
        return super.getCellEditor(row, column);
    }

    /**
     * Retrieves the TableCellEditor at a specific cell position in the table.
     * @param row the row index of the cell
     * @param column the column index of the cell
     * @return the TableCellEditor associated with the cell, or null if not found
     */
    public TableCellEditor getEditorCellEditor(int row, int column) {
        if (activeNode.getActiveChild().getEditorComponents().get(row) == null || activeNode.getActiveChild().getEditorComponents().get(row).getCell(column) == null) {
            return null;
        }
        return activeNode.getActiveChild().getEditorComponents().get(row).getCell(column).getEditor();
    }

    /**
     * Sets the cell editor for a specific cell in the table.
     * @param row the row index of the cell
     * @param column the column index of the cell
     * @param component the JComponent to be used as the editor for the cell
     */
    public void setCellEditor(int row, int column, Component component) {
        activeNode.getActiveChild().getEditorComponents().get(row).getCell(column).setComponent(component);
    }

    /**
     * Sets the cell editor for a specific cell in the table with a TableCellEditor.
     * @param row the row index of the cell
     * @param column the column index of the cell
     * @param editor the TableCellEditor to be used for the cell
     */
    public void setCellEditor(int row, int column, TableCellEditor editor) {
        activeNode.getActiveChild().getEditorComponents().get(row).getCell(column).setEditor(editor);
    }

    /**
     * Clears all editor components from the table.
     * This method is useful for resetting the table state or when the table is no longer needed.
     */
    public void clearTableRows() {
        activeNode.getActiveChild().getEditorComponents().clear();
    }

    public void setTableSchema(Schema<?> schema) {
        this.tableSchema = schema;
        activeNode.getActiveChild().updateSchemaRows(schema);
        activeNode.getActiveChild().normalizeModel();
    }

    public Schema<?> getTableSchema() {
        return tableSchema;
    }

    /** Loades a list of parameters, updating the table and the model. */
    public void setTableParameters(ArrayList<Parameter> parameters) {
        this.tableParameters = parameters;
        activeNode.getActiveChild().updateParameterRows(parameters);
        activeNode.getActiveChild().normalizeModel();
    }

    public ArrayList<Parameter> getTableParameters() {
        return tableParameters;
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        if ((dataModel instanceof CustomEditorTableModel)) {
            activeNode.setChild(activeNode.getActiveChildIndex(), (CustomEditorTableModel) dataModel);
        }
    }

    public int getRowIndex(CustomTableRow row) {
        return activeNode.getActiveChild().getEditorComponents().indexOf(row);
    }

    public CustomTableRow getRowAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= activeNode.getActiveChild().getEditorComponents().size()) {
            return null;
        }
        return activeNode.getActiveChild().getEditorComponents().get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return activeNode.getActiveChild().getEditorComponents().size();
    }

    public int getColumnCount() {
        return CustomTableHeader.getColumnIndices().size();
    }

    @Override
    public String getColumnName(int column) {
        return CustomTableHeader.getColumnIndices().inverse().get(column);
    }

    public CustomTableNode getActiveNode() {
        return activeNode;
    }

    public void setActiveModel(CustomEditorTableModel activeModel) {
        activeNode.setChild(activeNode.getActiveChildIndex(),activeModel);
        activeNode.getActiveChild().normalizeModel();
        this.setModel(activeNode.getActiveChild());
    }

    /** Resets the active node to a new instance of <code>CustomTableNode</code>. */
    public void resetNode() {
        activeNode = new CustomTableNode(null, sharedDialog, false);
        activeNode.addEmptyChild();
        this.setModel(activeNode.getActiveChild());
        activeNode.getActiveChild().updateParameterRows(tableParameters);
        activeNode.getActiveChild().updateSchemaRows(tableSchema);
    }

}
