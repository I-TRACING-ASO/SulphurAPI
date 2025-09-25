package v1.sulphurapi.structure.custom.dialog.listeners;

import javax.swing.table.TableCellEditor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class DialogWindowListener extends WindowAdapter {

    private TableCellEditor editor;

    /** Listener for handling custom editors using a dialog window. */
    public DialogWindowListener(TableCellEditor editor) {
        this.editor = editor;
    }

    @Override
    public void windowClosing(WindowEvent e) {
        editor.stopCellEditing();
    }
}
