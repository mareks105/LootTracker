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
public class EquipmentParser {
    public static String[] getEquipmentInput(String oldName, String oldType, String oldStart, String oldMarkup, String oldEnd){
        String[] types = {"Weapon", "Amp", "Healing", "Armor"};
        JComboBox<String> typeSelector = new JComboBox<>(types);
        if(oldType != null){
            typeSelector.setSelectedItem(oldType);
        }
        String[] output = new String[5];
        JTextField nameField = new JTextField(oldName);
        JTextField startField = new JTextField(oldStart);
        JTextField markupField = new JTextField(oldMarkup);
        JTextField endField = new JTextField(oldEnd);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(typeSelector);
        panel.add(new JLabel("Name;"));
        panel.add(nameField);
        panel.add(new JLabel("Start Value:"));
        panel.add(startField);
        panel.add(new JLabel("Markup:"));
        panel.add(markupField);
        panel.add(new JLabel("End Value:"));
        panel.add(endField);
        int result = JOptionPane.showConfirmDialog(null, panel, "Add Equipment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if( result == JOptionPane.OK_OPTION){
            output[0] = nameField.getText();
            output[1] = (String)typeSelector.getSelectedItem();
            output[2] = startField.getText();
            output[3] = markupField.getText();
            output[4] = endField.getText();
            if(validateOutput(output)){
                return output;
            }
            else{
                JOptionPane.showMessageDialog(null, "Invalid Data", "", JOptionPane.WARNING_MESSAGE);
                return getEquipmentInput(output[0], output[1], output[2], output[3], output[4]);
            }
            
        }
        else{
            return new String[]{};
        }
    }
        
    private static boolean validateOutput(String[] output){
        String name = output[0];
        String type = output[1];
        String startValue = output[2];
        String markup = output[3];
        String endValue = output[4];

        return Utilities.validateString(name, false) 
                && Utilities.validateString(type, false) 
                && Utilities.validateString(startValue, true)
                && Utilities.validateString(markup, true)
                && Utilities.validateString(endValue, true)
                && Double.compare(Double.parseDouble(markup), 100) >= 0
                && Double.compare(Double.parseDouble(startValue), Double.parseDouble(endValue)) >= 0;
    }
}
