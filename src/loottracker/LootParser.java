/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.awt.GridLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author mege9
 */
public class LootParser {
    public static String[] getLootInput(String oldName, String oldValue){
        JTextField nameField = new JTextField(oldName);
        JTextField valueField = new JTextField(oldValue);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Value"));
        panel.add(valueField);
        String[] output = new String[3];
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Loot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result == JOptionPane.OK_OPTION){
            output[0] = nameField.getText();
            output[1] = valueField.getText();
            if(validateOutput(output)){
                return output;
            }
            else{
                JOptionPane.showMessageDialog(null, "Invalid Data", "", JOptionPane.WARNING_MESSAGE);
                return getLootInput(output[0], output[1]);
            }
        }
        else{
            return new String[]{};
        }
    }
        
    private static boolean validateOutput(String[] output){
        String name = output[0];
        String value = output[1];
        
        return Utilities.validateString(name, false) 
                && Utilities.validateString(value, true);
                //&& Double.compare(Double.parseDouble(value), 0.0) > 0;
    }
}
