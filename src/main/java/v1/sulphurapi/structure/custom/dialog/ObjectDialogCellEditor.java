package v1.sulphurapi.structure.custom.dialog;

import io.swagger.v3.oas.models.media.Schema;
import v1.sulphurapi.structure.custom.jtable.CustomCell;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

public class ObjectDialogCellEditor extends AbstractCellEditor implements TableCellEditor {

    private final ObjectDialog dialog;
    private final CustomCell parentCell;

    public ObjectDialogCellEditor(ObjectDialog dialog, Schema<?> schema, CustomCell cell) {
        this.dialog = dialog;
        this.parentCell = cell;
    }

    @Override
    public Object getCellEditorValue() {
        return "Stored Object Data";
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        return dialog;
    }

    public ObjectDialog getDialog() {
        return dialog;
    }

    public CustomCell getParentCell() {
        return parentCell;
    }
}
