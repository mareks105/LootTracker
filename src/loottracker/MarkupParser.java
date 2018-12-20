/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author mege9
 */
public class MarkupParser {
    
    public static String[] getMarkupInput(JFrame parent, String name, String oldMarkup){
        JPanel panel = new JPanel();
        JTextField nameField = new JTextField(name);
        nameField.setEditable(false);
        if(oldMarkup == null){
            oldMarkup = "100";
        }
        JTextField markupField = new JTextField(oldMarkup);
        panel.add(new JLabel("Name"));
        panel.add(nameField);
        panel.add(new JLabel("Markup"));
        panel.add(markupField);
        int result = JOptionPane.showConfirmDialog(parent, panel, "Add Markup",
                JOptionPane.OK_OPTION, JOptionPane.PLAIN_MESSAGE);
        if(result == JOptionPane.OK_OPTION){
            String markup = markupField.getText();
            if(validateOutput(markup)){
                return new String[]{name, markup};
            }
            else{
                JOptionPane.showMessageDialog(parent, "Invalid Markup", "", JOptionPane.WARNING_MESSAGE);
                return getMarkupInput(parent, name, markup);
            }
        }
        else{
            return new String[]{};
        }
    }
    
    private static boolean validateOutput(String markup){
        return Utilities.validateString(markup, true) 
                && Double.compare(Double.parseDouble(markup), 100) >= 0;
    }
}
