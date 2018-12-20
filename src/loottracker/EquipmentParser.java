/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author mege9
 */
public class EquipmentParser {
    
    public static String[] getEquipmentInput(JFrame parent, LootTracker lootTracker, String oldName, String oldType, String oldStart, String oldEnd, String oldMarkup){
        JTextField startField = new JTextField(oldStart);
        JTextField markupField = new JTextField(oldMarkup);
        JTextField endField = new JTextField(oldEnd);
        String[] types = {"Weapon", "Amp", "Healing", "Armor"};
        JComboBox<String> typeSelector = new JComboBox<>(types);
        JComboBox nameSelector;
        if(oldType != null){
            typeSelector.setSelectedItem(oldType);
        }
        String type = typeSelector.getSelectedItem().toString();
        nameSelector = new JComboBox(lootTracker.getAllNames(type).toArray());
        String name = nameSelector.getSelectedItem().toString();
        Equipment e = EquipmentUtilities.getEquipmentByName(name, lootTracker.getAllEquipment());
        if(oldStart == null && oldEnd == null){
            startField.setText(Double.toString(e.getValue()));
            endField.setText(Double.toString(e.getValue()));
        }
        if(oldMarkup == null){
            markupField.setText(Double.toString(e.getMarkup() * 100));
        }
        
        typeSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event){
                String newType = (String) typeSelector.getSelectedItem();
                
                nameSelector.removeAllItems();
                ArrayList<String> names = lootTracker.getAllNames(newType);
                names.forEach(name ->{
                    nameSelector.addItem(name);
                });  
            }
        });
        String[] output = new String[5];
        
        if(oldName != null){
            nameSelector.setSelectedItem(oldName);
        }
        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String newName = JOptionPane.showInputDialog(parent, "Name");
                if(Utilities.validateString(newName, false)){
                    nameSelector.addItem(newName);
                    nameSelector.setSelectedItem(newName);
                    markupField.setText("100");
                }
            }
        });
        
        nameSelector.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent ie) {
                String name = (String) nameSelector.getSelectedItem();
                if(name != null){
                    Equipment e = EquipmentUtilities.getEquipmentByName(name, lootTracker.getAllEquipment());
                    if(e != null){
                        startField.setText(Double.toString(e.getValue()));
                        endField.setText(Double.toString(e.getValue()));
                        markupField.setText(Double.toString(e.getMarkup() * 100));
                    }
                    else{
                        startField.setText("");
                        markupField.setText("100");
                    }
                }
            }
        });
        
        
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(typeSelector);
        panel.add(new JLabel("Name;"));
        panel.add(nameSelector);
        panel.add(newButton);
        panel.add(new JLabel("Start Value:"));
        panel.add(startField);
        panel.add(new JLabel("End Value:"));
        panel.add(endField);
        panel.add(new JLabel("Markup:"));
        panel.add(markupField);
        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Equipment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if( result == JOptionPane.OK_OPTION){
            output[0] = (String)nameSelector.getSelectedItem();
            output[1] = (String)typeSelector.getSelectedItem();
            output[2] = startField.getText();
            output[3] = endField.getText();
            output[4] = markupField.getText();
            if(validateOutput(output)){
                return output;
            }
            else{
                JOptionPane.showMessageDialog(parent, "Invalid Data", "", JOptionPane.WARNING_MESSAGE);
                return getEquipmentInput(parent, lootTracker, output[0], output[1], output[2], output[3], output[4]);
            }
            
        }
        else{
            return new String[]{};
        }
    }
     
    public static String[] getEquipmentInputSettings(JFrame parent, String oldName, String oldType, String oldValue, String oldMarkup){
        String[] types = {"Weapon", "Amp", "Healing", "Armor"};
        JComboBox<String> typeSelector = new JComboBox<>(types);
        if(oldType != null){
            typeSelector.setSelectedItem(oldType);
        }
        String[] output = new String[4];
        JTextField nameField = new JTextField(oldName);
        JTextField valueField = new JTextField(oldValue);
        JTextField markupField = new JTextField(oldMarkup);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(typeSelector);
        panel.add(new JLabel("Name;"));
        panel.add(nameField);
        panel.add(new JLabel("Current Value:"));
        panel.add(valueField);
        panel.add(new JLabel("Markup:"));
        panel.add(markupField);
        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Equipment",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if( result == JOptionPane.OK_OPTION){
            output[0] = nameField.getText();
            output[1] = (String)typeSelector.getSelectedItem();
            output[2] = valueField.getText();
            output[3] = markupField.getText();
            if(validateOutputSettings(output)){
                return output;
            }
            else{
                JOptionPane.showMessageDialog(parent, "Invalid Data", "", JOptionPane.WARNING_MESSAGE);
                return getEquipmentInputSettings(parent, output[0], output[1], output[2], output[3]);
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
        String endValue = output[3];
        String markup = output[4];

        return Utilities.validateString(name, false) 
                && Utilities.validateString(type, false) 
                && Utilities.validateString(startValue, true)
                && Utilities.validateString(markup, true)
                && Utilities.validateString(endValue, true)
                && ! startValue.equals("")
                && ! endValue.equals("")
                && ! markup.equals("")
                && Double.compare(Double.parseDouble(markup), 100) >= 0
                && Double.compare(Double.parseDouble(startValue), Double.parseDouble(endValue)) >= 0;
    }
    
    private static boolean validateOutputSettings(String[] output){
        String name = output[0];
        String type = output[1];
        String value = output[2];
        String markup = output[3];

        return Utilities.validateString(name, false) 
                && Utilities.validateString(type, false) 
                && Utilities.validateString(value, true)
                && Utilities.validateString(markup, true)
                && Double.compare(Double.parseDouble(markup), 100) >= 0
                && Double.compare(Double.parseDouble(value), 0) >= 0;
    }
}
