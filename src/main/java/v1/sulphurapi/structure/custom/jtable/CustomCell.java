package v1.sulphurapi.structure.custom.jtable;

import io.swagger.v3.oas.models.media.Schema;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;
import v1.sulphurapi.structure.custom.dialog.ObjectDialogCellEditor;
import v1.sulphurapi.handlers.openapi.SchemaHandler;
import v1.sulphurapi.structure.custom.dialog.listeners.DialogWindowListener;
import v1.sulphurapi.utils.StringParser;
import v1.sulphurapi.structure.custom.jtable.CustomEditorTable.CustomTableHeader;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Objects;

public class CustomCell {
    /** Index of the column in the table. A BiMap is used to map column names to their indices. */
    private final int column;

     /** Instance of the TableCellEditor used for editing the cell. */
    private TableCellEditor editor;

     /** Represents the component used for the cell, such as JTextField or JComboBox. */
    private Component component;

     /** The value of the cell, which can be a string representation of the data. */
    private String value;

     /** The parent row of the cell, instance of CustomTableRow. */
    private final CustomTableRow parentRow;

     /** In the case of this cell being in a Row with a type "array" or "object", this cell will have a node. */
    private CustomTableNode node;

     /** Used by the cell to determine the type of the cell and editors of the parent row. */
    private String types;

     /** Instance of the <code>JComboBox</code> used for selecting types in the cell, if applicable. */
    private JComboBox<String> typesComboBox;

     /** Used to calculate the schema of the Dialog when the user clicks on the cell. (Prevents infinite recursion) */
    private Schema<?> lazyObjectSchemaTrigger;

    /**
     * Constructor for CustomCell.
     *
     * @param column    The column index of the cell.
     * @param parent    The parent row of the cell.
     */
    public CustomCell(int column, CustomTableRow parent) {
        this.parentRow = parent;
        this.column = column;
        this.component = new JTextField();
        this.editor = new DefaultCellEditor((JTextField) this.component);
        this.node = null;
        this.types = null;
        this.typesComboBox = null;
    }

    /**
     * Constructor for CustomCell with a value.
     *
     * @param column    The column index of the cell.
     * @param parent    The parent row of the cell.
     * @param value     The initial value of the cell.
     */
    public CustomCell(int column, CustomTableRow parent, String value) {
        this(column, parent);
        this.value = value;
        this.parentRow.getEditorTableModel().setValueAt(value, parentRow.getRowIndex(), column);
        updateTypeComboxBox();
    }

    /** Sets the component of a cell (e.g., JTextField, JComboBox, etc.) */
    public void setComponent(Component component) {
        this.component = component;
    }

    public Component getComponent() {
        return component;
    }

     /** Sets the editor for the cell (e.g., DefaultCellEditor, ObjectDialogCellEditor, etc.) */
    public void setEditor(TableCellEditor editor) {
        this.editor = editor;
    }

    public TableCellEditor getEditor() {
        return editor;
    }

    public int getColumn() {
        return column;
    }

    public String getValue() {
        return value;
    }

     /**  Sets the value of the cell. */
    public void setValue(String value) {
        this.value = value;
    }

    public CustomTableRow getParentRow() {
        return parentRow;
    }

    /** If the cell is in the "Type" column,
     *  it will update the combo boxes based on the types of the schema of the parent row.
     */
    public void updateTypeComboxBox() {
        if (this.column == CustomTableHeader.getIndexByColumnName("Type")) {
            if (this.typesComboBox == null) {
                this.typesComboBox = new JComboBox<>();
            }

            if (this.value.startsWith("[") && this.value.endsWith("]")) {
                this.types = this.value;
            }

            StringParser.StringArrayToComboBox(this.types, this.typesComboBox);

            this.component = this.typesComboBox;
            this.editor = new DefaultCellEditor((JComboBox<?>) this.component);


            ((JComboBox<?>) this.component).addItemListener(this::selectNewTypeEvent);
            if (((JComboBox<?>) this.component).getSelectedItem() != null && !((JComboBox<?>) this.component).getSelectedItem().toString().isEmpty()) {
                this.value = ((JComboBox<?>) this.component).getSelectedItem().toString();
                parentRow.getCell(CustomTableHeader.getIndexByColumnName("Value")).updateValueField(((JComboBox<?>) this.component).getSelectedItem().toString());
            } else {
                parentRow.getCell(CustomTableHeader.getIndexByColumnName("Value")).updateValueField("");
            }
        }
    }

    /**
     * Updates the value field of the cell based on the type of the cell.
     * If the cell is an array or object, it will create a new ObjectDialogCellEditor.
     * If the cell is null, it will set the component and editor to null.
     * If the cell is a primitive type, it will create a JTextField as the component.
     *
     * @param cellType The type of the cell (e.g., "array", "object", "null", "string", "integer").
     */
    public void updateValueField(String cellType) {
        CustomEditorTableModel table = parentRow.getEditorTableModel();
        ObjectDialog dialog = getParentDialog();
        switch (cellType) {
            case "array": {
                if (!(this.editor instanceof ObjectDialogCellEditor)) {
                    this.editor = new ObjectDialogCellEditor(dialog, parentRow.getSchema(), this);
                }
                this.component = dialog;
                dialog.addWindowListener(new DialogWindowListener(editor));
                Schema<?> item = parentRow.getSchema().getItems();
                if (item.get$ref() != null) {
                    item = SchemaHandler.getValueOfSchemaComponent(item.get$ref());
                }
                lazyObjectSchemaTrigger = item;
                if (this.node == null) {
                    this.node = new CustomTableNode(this, getParentDialog(), true);
                    this.node.addEmptyChild();
                }
                break;
            }
            case "object": {
                if (!(this.editor instanceof ObjectDialogCellEditor)) {
                    this.editor = new ObjectDialogCellEditor(dialog, parentRow.getSchema(), this);
                }
                this.component = dialog;
                dialog.addWindowListener(new DialogWindowListener(editor));
                Schema<?> item = parentRow.getSchema().getItems();
                if (item.get$ref() != null) {
                    item = SchemaHandler.getValueOfSchemaComponent(item.get$ref());
                }
                lazyObjectSchemaTrigger = item;
                if (this.node == null) {
                    this.node = new CustomTableNode(this, getParentDialog(), false);
                    this.node.addEmptyChild();
                }
                break;
            }
            case "null": {
                this.component = null;
                this.editor = null;
                this.value = null;
                table.normalizeModel();
                break;
            }
            default: {
                /// TODO: integrate verification of other types (e.g., integer, string, boolean, etc.)
                JTextField textField = new JTextField();
                this.component = textField;
                this.editor = new DefaultCellEditor(textField);
            }
        }
    }

    public Schema<?> getLazyObjectSchemaTrigger() {
        return lazyObjectSchemaTrigger;
    }

    public ObjectDialog getParentDialog() {
        return parentRow.getParentDialog();
    }

    public CustomTableNode getNode() {
        return node;
    }

    /**
     * Handles the event when a new type is selected in the JComboBox.
     * @param event the ItemEvent triggered by the JComboBox selection change.
     */
    private void selectNewTypeEvent(ItemEvent event) {
        if (event.getStateChange() == ItemEvent.SELECTED) {
            JComboBox<?> comboBox = (JComboBox<?>) event.getSource();
            CustomEditorTable.CellPosition position = parentRow.getEditorTableModel().getPositionFromComponent(comboBox).getFirst();
            if (position != null) {
                this.value = Objects.requireNonNull(((JComboBox<?>) event.getSource()).getSelectedItem()).toString();
                parentRow.getCell(CustomTableHeader.getIndexByColumnName("Value")).updateValueField(event.getItem().toString());
                parentRow.getEditorTableModel().normalizeModel();
            }
        }
    }
}
