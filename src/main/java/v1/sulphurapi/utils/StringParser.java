package v1.sulphurapi.utils;

import javax.swing.*;

public class StringParser {
    /**
     * Converts a string representation of an array to a JComboBox.
     * @param value String representation of an array, e.g. "[value1, value2, value3]"
     * @param comboBox JComboBox to populate with the values from the string.
     */
    public static void StringArrayToComboBox(String value, JComboBox<String> comboBox) {

        comboBox.removeAllItems();

        //remove []
        if (value.startsWith("[") && value.endsWith("]")) {
            value = value.substring(1, value.length() - 1);
        }
        //remove spaces
        value = value.replaceAll("\\s+", "");
        //split by comma
        String[] values = value.split(",");
        for (String val : values) {
            if (!val.isEmpty()) {
                comboBox.addItem(val);
            }
        }
        comboBox.setSelectedItem(0);
    }
}
