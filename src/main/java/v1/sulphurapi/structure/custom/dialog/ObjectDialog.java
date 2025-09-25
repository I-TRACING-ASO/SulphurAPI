package v1.sulphurapi.structure.custom.dialog;

import io.swagger.v3.oas.models.media.Schema;
import v1.sulphurapi.core.SAPI;
import v1.sulphurapi.structure.custom.jtable.CustomEditorTable;
import v1.sulphurapi.structure.custom.jtable.CustomEditorTableModel;
import v1.sulphurapi.structure.custom.jtable.CustomTableNode;
import v1.sulphurapi.interfaces.GUIActionInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.WindowEvent;

public class ObjectDialog extends JDialog implements GUIActionInterface {
    private final JComponent relativeParent;
    private final JComboBox<String> paramListing;
    private final CustomEditorTable table;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton clearButton;
    private final JButton validateButton;
    private CustomTableNode loadedNode;
    /**
     * Constructor for the ObjectDialog class.
     * @param mainWindow The parent JPanel to which this dialog is relative. (Generally burpsuite main ui panel)
     */
    public ObjectDialog(JPanel mainWindow) {
        super(SwingUtilities.getWindowAncestor(SAPI.getUIPanel()), "Array Editor" , ModalityType.APPLICATION_MODAL);
        this.relativeParent = mainWindow;
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        table = new CustomEditorTable(this);
        paramListing = new JComboBox<>();
        addButton = new JButton("Add");
        removeButton = new JButton("Remove");
        clearButton = new JButton("Reset");
        loadedNode = null;

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(5,5,5,5);
        panel.add(paramListing, gbc);
        gbc.gridy = 0;
        gbc.gridx = 1;
        gbc.weighty = 0.0;
        gbc.weightx = 0.0;
        panel.add(addButton, gbc);
        gbc.gridy = 0;
        gbc.gridx = 2;
        gbc.weighty = 0.0;
        panel.add(removeButton, gbc);
        gbc.gridy = 0;
        gbc.gridx = 3;
        gbc.weighty = 0.0;
        panel.add(clearButton, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(table), gbc);
        gbc.gridy = 2;
        gbc.gridx = 3;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        validateButton = new JButton("Validate");
        panel.add(validateButton, gbc);
        this.setContentPane(panel);

        addButton.addActionListener((e) -> {
            if (loadedNode == null) {
                if (SAPI.getDebugMode()) {
                    SAPI.logError("Loaded node is null or has no children, cannot remove child.");
                }
                return;
            }
            loadedNode.addEmptyChild();
            loadedNode.getActiveChild().updateSchemaRows(loadedNode.getLazyObjectSchemaTrigger());
            loadedNode.getActiveChild().normalizeModel();
            updateComboBox();
            updateTable();
        });

        removeButton.addActionListener((e)-> {
            if (loadedNode == null) {
                if (SAPI.getDebugMode()) {
                    SAPI.logError("Loaded node is null or has no children, cannot remove child.");
                }
                return;
            }
            int index = paramListing.getSelectedIndex();
            loadedNode.removeChild(loadedNode.getActiveChild());
            if (loadedNode.getChildren().size() <= 0) {
                loadedNode.addEmptyChild();
                loadedNode.setActiveChildIndex(loadedNode.getChildren().size() - 1);
                loadedNode.getActiveChild().updateSchemaRows(loadedNode.getLazyObjectSchemaTrigger());
                loadedNode.getActiveChild().normalizeModel();
            } else {
                loadedNode.setActiveChildIndex(Math.max(index - 1, 0));
            }
            updateTable();
            updateComboBox();
        });

        clearButton.addActionListener(e ->  {
            loadedNode.getChildren().clear();
            loadedNode.addEmptyChild();
            loadedNode.getActiveChild().updateSchemaRows(loadedNode.getLazyObjectSchemaTrigger());
            loadedNode.getActiveChild().normalizeModel();
            updateTable();
            updateComboBox();
        });

        validateButton.addActionListener((e) -> {
            if (loadedNode == null) {
                if (SAPI.getDebugMode()) {
                    SAPI.logError("Loaded node is null or has no children, cannot change active child.");
                }
                return;
            }
            CustomTableNode parent = loadedNode.getParentNode();
            if (parent != null && (!parent.getChildren().isEmpty()) && parent.getParentNode() != null) {
                this.loadedNode = parent;
                updateTable();
                updateComboBox();
            } else if (this.getWindowListeners().length != 0) {
                    this.getWindowListeners()[0].windowClosing(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                    this.loadedNode.getParent().getEditor().stopCellEditing();
                    hideDialog();
            }
        });

        paramListing.addItemListener((e) -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                loadedNode.setActiveChildIndex(paramListing.getSelectedIndex());
                updateTable();
            }
        });

    }

    /**
     * Method used to display dialog on the main screen.
     * It will update the table model with the current active child of the loaded node.
     * It will also update the combo box with the current children of the loaded node.
     * Loaded node must be set via setLoadedNode() method before calling this method.
     */
    public void showDialog() {
        if (loadedNode == null) {
            SAPI.logError("Loaded node is null, cannot show dialog.");
        }
        for (int i = 0; i < loadedNode.getChildren().size(); i++) {
            if (loadedNode.getActiveChild().getLazySchemaLoaded())
                continue;
            loadedNode.getActiveChild().updateSchemaRows(loadedNode.getLazyObjectSchemaTrigger());
            loadedNode.getActiveChild().normalizeModel();
        }
        updateComboBox();
        this.table.setModel(loadedNode.getActiveChild());

        this.pack();
        this.setLocationRelativeTo(relativeParent);
        this.setVisible(true);
    }

    /** Updates the table with the current active child of the loaded node. */
    private void updateTable() {
        this.table.setModel(loadedNode.getActiveChild());
    }

    /** Removes the dialog from the screen without closing it. */
    public void hideDialog() {
        this.setVisible(false);
    }

    public CustomEditorTable getTable() {return table;}

    public JComboBox<String> getParamListing() {return paramListing;}

    /** Sets the schema for the table used by the table of the dialog to display the data. */
    public void setSchema(Schema<?> schema) {
        this.table.setTableSchema(schema);
    }

    /** Sets the model for the table used by the table. */
    public void setModel(CustomEditorTableModel model) {
        this.table.setModel(model);
        this.table.revalidate();
        this.table.repaint();
    }

    public CustomTableNode getLoadedNode() {
        return loadedNode;
    }

    /** Sets the loaded node. */
    public void setLoadedNode(CustomTableNode loadedNode) {
        this.loadedNode = loadedNode;
    }

    /**
     * Updates the dialog's combo box content based on the loaded node data.
     */
    private void updateComboBox() {
        paramListing.removeAllItems();
        int saveLoaded = loadedNode.getActiveChildIndex();
        for (int i = 0; i < loadedNode.getChildren().size(); i++) {
            paramListing.addItem("" + (i + 1));
        }
        loadedNode.setActiveChildIndex(saveLoaded);
        paramListing.setSelectedIndex(loadedNode.getActiveChildIndex());

        if (this.loadedNode.isArray()) {
            addButton.setEnabled(true);
            removeButton.setEnabled(true);
        } else {
            addButton.setEnabled(false);
            removeButton.setEnabled(false);
        }
    }
}
