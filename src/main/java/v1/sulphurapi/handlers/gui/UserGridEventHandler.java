package v1.sulphurapi.handlers.gui;

import v1.sulphurapi.core.SAPI;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Map;

public class UserGridEventHandler {
    public static void addValueToListEvent(JTextField field,JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        String selectedItem = (String) comboBox.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        DefaultListModel<String> model = (DefaultListModel<String>) models.get(selectedItem);
        String value = field.getText();
        model.addElement(value);
        models.put(selectedItem, model);
        updateListModel(comboBox, list, models);
    }

    public static void removeValueFromListEvent(JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        String selectedItem = (String) comboBox.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        DefaultListModel<String> model = (DefaultListModel<String>) models.get(selectedItem);
        int selectedIndex = list.getSelectedIndex();
        if (selectedIndex != -1) {
            model.remove(selectedIndex);
        } else if (!model.isEmpty()) {
            model.removeElementAt(model.size() - 1);
        } else {
            return ;
        }
        models.put(selectedItem, model);
        updateListModel(comboBox, list, models);
    }

    public static void clearListEvent(JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        String selectedItem = (String) comboBox.getSelectedItem();
        if (selectedItem != null) {
            models.put(selectedItem, new DefaultListModel<>());
            updateListModel(comboBox, list, models);
        }
    }

    public static void loadListEvent(JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        try {
            String clipboard = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            for (String value : clipboard.split("\n")) {
                String selectedItem = (String) comboBox.getSelectedItem();
                if (selectedItem == null) {
                    return;
                }
                DefaultListModel<String> model = (DefaultListModel<String>) models.get(selectedItem);
                model.addElement(value);
                models.put(selectedItem, model);
                updateListModel(comboBox, list, models);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            SAPI.getAPI().logging().raiseErrorEvent(e.getMessage());
        }
    }

    public static void comboBoxListSelectionEvent(JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        comboBox.hidePopup();
        updateListModel(comboBox, list, models);
    }

    private static void updateListModel(JComboBox<String> comboBox, JList<String> list, Map<String, ListModel<String>> models) {
        String selectedItem = (String) comboBox.getSelectedItem();
        if (selectedItem != null && models.containsKey(selectedItem)) {
            list.setModel(models.get(selectedItem));
            list.revalidate();
            list.repaint();
        } else {
            models.put(selectedItem,new DefaultListModel<>());
            list.setModel(models.get(selectedItem));
            list.revalidate();
            list.repaint();
        }
    }

}
