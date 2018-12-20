/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class LootParser {
    public static String[] getLootInput(JFrame parent, ArrayList<String> reportedLoot, String oldName, String oldValue){
        JComboBox nameSelector;
        if(reportedLoot.size() == 0){
            nameSelector = new JComboBox();
        }
        else{
            nameSelector = new JComboBox(reportedLoot.toArray());
        }
        JButton newButton = new JButton("New");
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String newName = JOptionPane.showInputDialog(parent, "Name");
                if(Utilities.validateString(newName, false)){
                    nameSelector.addItem(newName);
                    nameSelector.setSelectedItem(newName);
                }
            }
        });
        JTextField valueField = new JTextField(oldValue);
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Name"));
        panel.add(nameSelector);
        panel.add(newButton);
        panel.add(new JLabel("Value"));
        panel.add(valueField);
        String[] output = new String[3];
        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Loot",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result == JOptionPane.OK_OPTION){
            if(nameSelector.getItemCount() == 0){
                JOptionPane.showMessageDialog(parent, "No name given!", "", JOptionPane.WARNING_MESSAGE);
                return getLootInput(parent, reportedLoot, output[0], output[1]);
            }
            output[0] = nameSelector.getSelectedItem().toString();
            output[1] = valueField.getText();
            if(validateOutput(output)){
                return output;
            }
            else{
                JOptionPane.showMessageDialog(parent, "Invalid Data", "", JOptionPane.WARNING_MESSAGE);
                return getLootInput(parent, reportedLoot, output[0], output[1]);
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
                && Utilities.validateString(value, true)
                && ! value.isEmpty();
                //&& Double.compare(Double.parseDouble(value), 0.0) > 0;
    }
}
