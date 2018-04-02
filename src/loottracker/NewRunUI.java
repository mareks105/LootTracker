/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import javax.swing.ComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author mege9
 */
public class NewRunUI extends javax.swing.JFrame {

    private LootTracker lootTracker;
    private LootTrackerUI mainUI;
    /**
     * Creates new form NewRunUI
     * @param lootTracker The BackEnd Data handler
     */
    public NewRunUI(LootTracker lootTracker, LootTrackerUI mainUI) {
        this.lootTracker = lootTracker;
        this.mainUI = mainUI;
        UtilitiesUI.showFrameOnScreen(this, 2);
        initComponents();
        setupUserInterface();
    }
    
    private void setupUserInterface(){
        ComboBoxModel<String> comboBoxModel = this.mainUI.getGroupSelector().getModel();
        for(int i = 0; i < comboBoxModel.getSize(); i++){
            this.mobSelector.addItem(comboBoxModel.getElementAt(i));
        }
        UtilitiesUI.addRightClickDeselect(lootTable);
        UtilitiesUI.addRightClickDeselect(equipmentTable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        contentPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        equipmentTable = new javax.swing.JTable();
        addEquipButton = new javax.swing.JButton();
        removeEquipButton = new javax.swing.JButton();
        saveEquipButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        lootTable = new javax.swing.JTable();
        addLootButton = new javax.swing.JButton();
        removeLootButton = new javax.swing.JButton();
        editLootButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        noteField = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        ammoField = new javax.swing.JTextField();
        mobSelector = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        newMobButton = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        universalAmmoField = new javax.swing.JTextField();
        saveRunButton = new javax.swing.JButton();
        endRunButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                actionOnWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Equipment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        equipmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "start TT", "End TT", "Markup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(equipmentTable);

        addEquipButton.setText("Add");
        addEquipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEquipButtonActionPerformed(evt);
            }
        });

        removeEquipButton.setText("Remove");
        removeEquipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEquipButtonActionPerformed(evt);
            }
        });

        saveEquipButton.setText("Save");
        saveEquipButton.setEnabled(false);

        editButton.setText("Edit");
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(addEquipButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeEquipButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveEquipButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEquipButton)
                    .addComponent(removeEquipButton)
                    .addComponent(saveEquipButton)
                    .addComponent(editButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Loot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lootTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "TT", "Markup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(lootTable);

        addLootButton.setText("Add");
        addLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLootButtonActionPerformed(evt);
            }
        });

        removeLootButton.setText("Remove");
        removeLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLootButtonActionPerformed(evt);
            }
        });

        editLootButton.setText("Edit");
        editLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLootButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(addLootButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editLootButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeLootButton))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addLootButton)
                    .addComponent(removeLootButton)
                    .addComponent(editLootButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Note", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 12))); // NOI18N

        noteField.setColumns(20);
        noteField.setRows(5);
        jScrollPane2.setViewportView(noteField);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
        );

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Ammo");

        mobSelector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Mob");

        newMobButton.setText("New Mob");
        newMobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMobButtonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Universal Ammo");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(28, 28, 28)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(universalAmmoField, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newMobButton)
                .addGap(47, 47, 47))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(newMobButton))
                .addContainerGap(18, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(universalAmmoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        saveRunButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        saveRunButton.setText("Save Run");
        saveRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveRunButtonActionPerformed(evt);
            }
        });

        endRunButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        endRunButton.setText("End Run");
        endRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endRunButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(saveRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(endRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(contentPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(saveRunButton, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
                    .addComponent(endRunButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(52, 52, 52))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(contentPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(contentPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
    
    private void addEquipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEquipButtonActionPerformed
        String[] equipmentInput = EquipmentParser.getEquipmentInput(null, null, null, null, "100");
        if(equipmentInput.length > 0){
            DefaultTableModel model = (DefaultTableModel) equipmentTable.getModel();
            model.addRow(new Object[]{
                equipmentInput[0], 
                equipmentInput[1], 
                equipmentInput[2], 
                equipmentInput[3], 
                equipmentInput[4]}
            );
        }
        
    }//GEN-LAST:event_addEquipButtonActionPerformed

    private void newMobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMobButtonActionPerformed
        String newMob = JOptionPane.showInputDialog(null, "Name of Mob:");
        this.mobSelector.addItem(newMob);
        this.mobSelector.setSelectedItem(newMob);
    }//GEN-LAST:event_newMobButtonActionPerformed
    
    
    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int row = equipmentTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog(null, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] equipmentInput = EquipmentParser.getEquipmentInput(
                (String)equipmentTable.getValueAt(row, 0),
                (String)equipmentTable.getValueAt(row, 1),
                (String)equipmentTable.getValueAt(row, 2),
                (String)equipmentTable.getValueAt(row, 3),
                (String)equipmentTable.getValueAt(row, 4));
        if(equipmentInput.length > 0){
            DefaultTableModel model = (DefaultTableModel) equipmentTable.getModel();
            model.setValueAt(equipmentInput[0], row, 0);
            model.setValueAt(equipmentInput[1], row, 1);
            model.setValueAt(equipmentInput[2], row, 2);
            model.setValueAt(equipmentInput[3], row, 3);
            model.setValueAt(equipmentInput[4], row, 4);
        }
    }//GEN-LAST:event_editButtonActionPerformed

    private void removeEquipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEquipButtonActionPerformed
        int row = equipmentTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog(null, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel)equipmentTable.getModel();
        model.removeRow(row);
    }//GEN-LAST:event_removeEquipButtonActionPerformed

    private void addLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLootButtonActionPerformed
        String[] lootInput;
        ArrayList<String> reportedLootForRun = new ArrayList<String>();
        DefaultTableModel model = (DefaultTableModel)this.lootTable.getModel();
        Vector<Vector<String>> lootData = model.getDataVector();
        lootData.forEach((Vector<String> rowData) -> {
            reportedLootForRun.add(rowData.elementAt(0));
        });
        if(this.mobSelector.getItemCount() == 0){
            lootInput = LootParser.getLootInput(reportedLootForRun, null, null);
        }
        else{
            ArrayList<String> reportedGroup = this.lootTracker.getReportedLootForGroup(this.mobSelector.getSelectedItem().toString());
            reportedGroup.forEach((String s) -> {
                if(! reportedLootForRun.contains(s)){
                    reportedLootForRun.add(s);
                }
            });
            lootInput = LootParser.getLootInput(
                    reportedLootForRun,
                null, null);
        }
        
        
        if(lootInput.length > 0){
            String name = lootInput[0];
            String value = lootInput[1];
            String markup;
            try{
                markup = Double.toString(this.lootTracker.getMarkupHandler().getMarkup(name) * 100);
            }
            catch(MarkupHandlerException e){
                markup = MarkupParser.getMarkupInput(name, null);
                this.lootTracker.getMarkupHandler().addMarkup(name, Double.parseDouble(markup) / 100);
            }
            model.addRow(new Object[]{
            name,
            value,
            markup});
            
        }
        
    }//GEN-LAST:event_addLootButtonActionPerformed

    private void editLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLootButtonActionPerformed
        int row = lootTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog(null, "No loot entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String[] lootInput;
        ArrayList<String> reportedLootForRun = new ArrayList<String>();
        DefaultTableModel model = (DefaultTableModel)this.lootTable.getModel();
        Vector<Vector<String>> lootData = model.getDataVector();
        lootData.forEach((Vector<String> rowData) -> {
            reportedLootForRun.add(rowData.elementAt(0));
        });
        if(this.mobSelector.getItemCount() == 0){
            lootInput = LootParser.getLootInput(
                reportedLootForRun,
                (String)lootTable.getValueAt(row, 0),
                (String)lootTable.getValueAt(row, 1));
        }
        else{
            ArrayList<String> reportedGroup = this.lootTracker.getReportedLootForGroup(this.mobSelector.getSelectedItem().toString());
            reportedGroup.forEach((String s) -> {
                if(! reportedLootForRun.contains(s)){
                    reportedLootForRun.add(s);
                }
            });
            lootInput = LootParser.getLootInput(
                reportedLootForRun,
                (String)lootTable.getValueAt(row, 0),
                (String)lootTable.getValueAt(row, 1));
        }
        
        if(lootInput.length > 0){
            model.setValueAt(lootInput[0], row, 0);
            model.setValueAt(lootInput[1], row, 1);
        }
    }//GEN-LAST:event_editLootButtonActionPerformed

    private void removeLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLootButtonActionPerformed
        int row = lootTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog(null, "No loot entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel)lootTable.getModel();
        model.removeRow(row);
    }//GEN-LAST:event_removeLootButtonActionPerformed

    private void saveRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveRunButtonActionPerformed
        saveDataForHunt(false);
    }//GEN-LAST:event_saveRunButtonActionPerformed

    private void endRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endRunButtonActionPerformed
        saveDataForHunt(true);
    }//GEN-LAST:event_endRunButtonActionPerformed

    private void actionOnWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_actionOnWindowClosing
        JFrame frame = (JFrame)evt.getComponent();
        if(JOptionPane.showConfirmDialog(frame, 
                "Are you sure? All entered data will be lost", "Confirm Close", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            frame.setVisible(false);
        }
    }//GEN-LAST:event_actionOnWindowClosing
    
    private void saveDataForHunt(boolean end){
        DefaultTableModel equipmentTableModel = (DefaultTableModel)equipmentTable.getModel();
        Vector<Vector<String>> equipmentData = equipmentTableModel.getDataVector();
        DefaultTableModel lootTableModel = (DefaultTableModel)lootTable.getModel();
        Vector<Vector<String>> lootData = lootTableModel.getDataVector();
        double ammo;
        double universalAmmo;
        String group;
        boolean noAmmo = false;
        boolean noUniversalAmmo = false;
        if(Utilities.validateString(this.ammoField.getText(), true)){
            if(this.ammoField.getText().isEmpty()){
                ammo = 0.0;
                noAmmo = true;
            }
            else{
                ammo = Double.parseDouble(this.ammoField.getText());
            }
            
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid Ammo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(Utilities.validateString(this.universalAmmoField.getText(), true)){
            if(this.universalAmmoField.getText().isEmpty()){
                universalAmmo = 0.0;
                noUniversalAmmo = true;
            }
            else{
                universalAmmo = Double.parseDouble(this.universalAmmoField.getText());
            }
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid Universal Ammo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(noUniversalAmmo && noAmmo){
            JOptionPane.showMessageDialog(null, "No Ammo entered!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(this.mobSelector.getItemCount() > 0 
                && Utilities.validateString(this.mobSelector.getSelectedItem().toString(), false) ){
            group = (String)this.mobSelector.getSelectedItem().toString();
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid groupName!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String note = this.noteField.getText();
        int result = JOptionPane.showConfirmDialog(null, null, "Are you sure?", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            Hunt hunt = new Hunt(ammo, universalAmmo, lootData, equipmentData, note, lootTracker.getMarkupHandler());
            
            if(end){
                hunt.end(new Date());
            }
            int runID = this.lootTracker.addHuntToGroup(group, hunt);
            this.lootTracker.addLootForGroup(group, hunt.getLoot());
            this.mainUI.addRunToDisplay(runID, group, hunt);
            this.setVisible(false);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewRunUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewRunUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewRunUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewRunUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEquipButton;
    private javax.swing.JButton addLootButton;
    private javax.swing.JTextField ammoField;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editLootButton;
    private javax.swing.JButton endRunButton;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable lootTable;
    private javax.swing.JComboBox<String> mobSelector;
    private javax.swing.JButton newMobButton;
    private javax.swing.JTextArea noteField;
    private javax.swing.JButton removeEquipButton;
    private javax.swing.JButton removeLootButton;
    private javax.swing.JButton saveEquipButton;
    private javax.swing.JButton saveRunButton;
    private javax.swing.JTextField universalAmmoField;
    // End of variables declaration//GEN-END:variables
}
