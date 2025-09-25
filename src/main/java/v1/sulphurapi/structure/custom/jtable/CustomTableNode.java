package v1.sulphurapi.structure.custom.jtable;

import io.swagger.v3.oas.models.media.Schema;
import org.json.JSONArray;
import org.json.JSONObject;
import v1.sulphurapi.structure.custom.dialog.ObjectDialog;

import java.util.ArrayList;

public class CustomTableNode {

    /** Cell holding the node, null if this is the root node. */
    private final CustomCell parent;

     /** List of children nodes, each represented by a <code>CustomEditorTableModel</code>. */
    private final ArrayList<CustomEditorTableModel> children;

     /** Index of the currently active child node, -1 if no child is active. */
    private int activeChildIndex;

     /** Reference to the dialog that will be transfered to other components for display. */
    private final ObjectDialog parentDialog;

     /** Used to calculate the schema of the Dialog when the user clicks on the cell. (Prevents infinite recursion) */
    private Schema<?> lazyObjectSchemaTrigger;

     /** Indicates whether this node represents an array or a single object. */
    private final boolean isArray;

    /**
     * Constructor for CustomTableNode.
     *
     * @param parent       The parent cell of this node, null if this is the root node.
     * @param parentDialog The dialog that will be used to display this node.
     * @param isArray      Indicates whether this node represents an array or a single object.
     */
    public CustomTableNode(CustomCell parent, ObjectDialog parentDialog, boolean isArray) {
        this.parent = parent;
        this.isArray = isArray;
        this.children = new ArrayList<>();
        this.activeChildIndex = -1;
        this.parentDialog = parentDialog;
        this.lazyObjectSchemaTrigger = null;
    }

    /**Sets the index of the active child being used*/
    public void setActiveChildIndex(int activeChildIndex) {
        this.activeChildIndex = activeChildIndex;
    }

    /** This method sets the lazyObjectSchemaTrigger,
     *  which is used to calculate the schema of the Dialog when the user clicks on the cell.
     */
    public void setLazyObjectSchemaTrigger(Schema<?> lazyObjectSchemaTrigger) {
        this.lazyObjectSchemaTrigger = lazyObjectSchemaTrigger;
    }

    public Schema<?> getLazyObjectSchemaTrigger() {
        return lazyObjectSchemaTrigger;
    }

    public int getActiveChildIndex() {
        return activeChildIndex;
    }

    public CustomCell getParent() {
        return parent;
    }

    public ArrayList<CustomEditorTableModel> getChildren() {
        return children;
    }

    /** append a child to the list of children nodes. */
    public void addChild(CustomEditorTableModel child) {
        this.children.add(child);
    }

     /** append an empty  <code>CustomEditorTableModel</code> to the list of children nodes. */
    public void addEmptyChild() {
        this.children.add(new CustomEditorTableModel(this));
        this.activeChildIndex = this.children.size() - 1;
    }

     /** remove a child from the list of children nodes. */
    public void removeChild(CustomEditorTableModel child) {
        this.children.remove(child);
    }

    /**Retrieves the CustomEditorTableModel if index is valid*/
    public CustomEditorTableModel getChild(int index) {
        if (index < 0 || index >= children.size()) {
            throw new IndexOutOfBoundsException("Invalid child index: " + index);
        }
        return children.get(index);
    }

    /**Set the CustomTableModel of this node to a given value if index is valid*/
    public void setChild(int index, CustomEditorTableModel child) {
        if (index < 0 || index >= children.size()) {
            throw new IndexOutOfBoundsException("Invalid child index: " + index);
        }
        this.children.set(index, child);
    }

    /**Get the value of the model currently being used in the Dialog*/
    public CustomEditorTableModel getActiveChild() {
        if (activeChildIndex < 0 || activeChildIndex >= children.size()) {
            throw new IndexOutOfBoundsException("Invalid active child index: " + activeChildIndex);
        }
        return children.get(activeChildIndex);
    }

    public ObjectDialog getParentDialog() {
        return parentDialog;
    }

    /**Retrieves the node in case of recursive table calling*/
    public CustomTableNode getParentNode() {
        if (parent != null) {
            return parent.getParentRow().getEditorTableModel().getParentNode();
        } else {
            return null;
        }
    }

    /**
     * Serializes the node into a JSON object or array.
     * If the node is an array, it returns a JSONArray containing serialized children.
     * If the node is a single object, it returns a JSONObject representing the first child.
     *
     * @return A JSON representation of the node.
     */
    public Object serializeNode() {
        if (isArray) {
            JSONArray buffer = new JSONArray();
            for (CustomEditorTableModel child : children) {
                if (child.getLazySchemaLoaded() == false) {
                    continue;
                }
                JSONObject object = child.serializeModel();
                if (object.isEmpty()){
                    continue;
                }
                buffer.put(object);
            }
            return buffer;
        } else {
            return children.getFirst().serializeModel();
        }
    }

    public boolean isArray() {
        return isArray;
    }
}
