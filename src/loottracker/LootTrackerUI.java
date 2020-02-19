/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.awt.CardLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import DiskIO.DiskIO;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import javax.swing.ComboBoxModel;

/**
 *
 * @author mege9
 */
public class LootTrackerUI extends javax.swing.JFrame {

    private LootTracker lootTracker;
    private String oldGroupForDetailsPanel;
    private DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Creates new form LootTrackerUI
     */
    public LootTrackerUI() throws InvalidFormatException, ParseException, InvalidKeyException {
        this.lootTracker = new LootTracker();
        UtilitiesUI.showFrameOnScreen(this, 2);
        try {
            this.lootTracker = DiskIO.loadDataFromFile(df);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog((JFrame)this, "Failed to load data!", "Error Message", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        initComponents();
        initDataTables();
        displayDataFromLootTracker();
    }
    
    private void displayDataFromLootTracker(){
        this.groupSelector.addItem("All");
        this.lootTracker.getHuntingData().forEach((String group, MobData data) -> {
            this.groupSelector.addItem(group);
            this.mobSelector.addItem(group);
            data.getHunts().forEach((Integer ID, Hunt hunt) -> {
                DefaultTableModel dataTableModel = (DefaultTableModel)this.dataTable.getModel();
                Map<DataKey, Double> huntData = hunt.getDataForHunt(this.lootTracker.getMarkupHandler());
                dataTableModel.addRow(new Object[]{
                    group,
                    ID.toString(),
                    huntData.get(DataKey.TotalCost).toString(),
                    huntData.get(DataKey.TotalLootTT).toString(),
                    huntData.get(DataKey.Markup).toString(),
                    huntData.get(DataKey.ReturnTT).toString(),
                    huntData.get(DataKey.ReturnTTpercent).toString(),
                    huntData.get(DataKey.ReturnWithMarkup).toString(),
                    huntData.get(DataKey.ReturnWithMarkupPercent).toString(),
                    hunt.getNote()
                });
            });
        });
    }
    
    private void initDataTables(){
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(dataTable.getModel());
        sorter.setSortsOnUpdates(true);
        dataTable.setRowSorter(sorter);
        dataTable.setShowGrid(true);
        dataTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                int row = dataTable.rowAtPoint(e.getPoint());
                if(e.getClickCount() == 2){
                    showRunDetails();
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    dataTable.clearSelection();
                }
            }
        });
        
        equipmentSettingsTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                int row = equipmentSettingsTable.rowAtPoint(e.getPoint());
                if(e.getClickCount() == 2){
                    editEquipSettingsDialog(row);
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    equipmentSettingsTable.clearSelection();
                }
            }
        });
        
        equipmentTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                int row = equipmentTable.rowAtPoint(e.getPoint());
                if(e.getClickCount() == 2){
                    editEquipDialog(row);
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    equipmentTable.clearSelection();
                }
            }
        });
        
        markupTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                int row = markupTable.rowAtPoint(e.getPoint());
                if(e.getClickCount() == 2){
                    editMarkupDialog(row);
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    markupTable.clearSelection();
                }
            }
        });
        
        lootTable.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                int row = lootTable.rowAtPoint(e.getPoint());
                if(e.getClickCount() == 2){
                    editLootDialog(row);
                }
                else if(e.getButton() == MouseEvent.BUTTON3){
                    lootTable.clearSelection();
                }
            }
        });
        UtilitiesUI.addRightClickDeselect(markupTable);
        UtilitiesUI.addRightClickDeselect(equipmentSettingsTable);
        UtilitiesUI.addRightClickDeselect(equipmentTable);
        UtilitiesUI.addRightClickDeselect(lootTable);
        UtilitiesUI.addRightClickDeselect(dataTable);
    }
    
    
    private void showRunDetails(){
        int row = dataTable.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No run selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        prepareDetailsPanel(row);
        CardLayout card = (CardLayout)mainPanel.getLayout();
        card.show(mainPanel, "runDetails");
    }
    
    private void prepareDetailsPanel(int row){
        DefaultTableModel tableModel = (DefaultTableModel)this.dataTable.getModel();
        String group = (String)tableModel.getValueAt(row, 0);
        int runID = Integer.parseInt((String)tableModel.getValueAt(row, 1));
        Hunt hunt = this.lootTracker.getMobData(group).getHunt(runID);
        String note = hunt.getNote();
        double ammo = hunt.getAmmo();
        double universalAmmo = hunt.getUniversalAmmo();
        this.ammoField.setText(Double.toString(ammo));
        this.universalAmmoField.setText(Double.toString(universalAmmo));
        this.noteField.setText(note);
        ArrayList<Equipment> equipment = hunt.getEquipment();
        ArrayList<Loot> loot = hunt.getLoot();
        addEquipmentToTable(equipment);
        addLootToTable(loot);
        this.mobSelector.setSelectedItem(group);
        if(hunt.getEndDate() != null){
            this.dateField.setText(this.df.format(hunt.getEndDate()));
            endRunButton.setEnabled(false);
        }
        else{
            endRunButton.setEnabled(true);
        }
        this.oldGroupForDetailsPanel = group;
    }
    
    private void addEquipmentToTable(ArrayList<Equipment> equipment){
        DefaultTableModel tableModel = (DefaultTableModel)this.equipmentTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        equipment.forEach((Equipment e) -> {
            String type = EquipmentUtilities.getTypeForEquipment(e);
            tableModel.addRow(new Object[]
            {e.getName(),
            type,
            Double.toString(e.getValue()),
            Double.toString(e.getEndValue()),
            Double.toString(100*e.getMarkup())});
        });
    }
    
    private void addLootToTable(ArrayList<Loot> loot){
        DefaultTableModel tableModel = (DefaultTableModel)this.lootTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        loot.forEach((Loot l) -> {
            String markup = Double.toString(this.lootTracker.getMarkupHandler().getMarkup(l.getName()) * 100);
            tableModel.addRow(new Object[]
            {l.getName(),
            Double.toString(l.getValue()),
            markup});
        });
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        overviewPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        runDetailsPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        equipmentTable = new javax.swing.JTable();
        addEquipButton = new javax.swing.JButton();
        removeEquipButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        noteField = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        universalAmmoField = new javax.swing.JTextField();
        mobSelector = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        newMobButton = new javax.swing.JButton();
        dateField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        ammoField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lootTable = new javax.swing.JTable();
        addLootButton = new javax.swing.JButton();
        editLootButton = new javax.swing.JButton();
        removeLootButton = new javax.swing.JButton();
        updateRunButton = new javax.swing.JButton();
        endRunButton = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        equipmentSettingsTable = new javax.swing.JTable();
        addEquipSettingsButton = new javax.swing.JButton();
        editEquipSettingsButton = new javax.swing.JButton();
        saveEquipButton = new javax.swing.JButton();
        removeEquipSettingsButton = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane6 = new javax.swing.JScrollPane();
        markupTable = new javax.swing.JTable();
        addMarkupButton = new javax.swing.JButton();
        editMarkupButton = new javax.swing.JButton();
        removeMarkupButton = new javax.swing.JButton();
        saveMarkupButton = new javax.swing.JButton();
        totalDataPanel = new javax.swing.JPanel();
        totalCostLabel = new javax.swing.JLabel();
        totalLootLabel = new javax.swing.JLabel();
        totalMarkupLabel = new javax.swing.JLabel();
        returnTTLabel = new javax.swing.JLabel();
        returnMarkupPercentLabel = new javax.swing.JLabel();
        totalLootTTField = new javax.swing.JFormattedTextField();
        totalMarkupField = new javax.swing.JFormattedTextField();
        returnTTField = new javax.swing.JFormattedTextField();
        returnTTpercentField = new javax.swing.JFormattedTextField();
        returnTTPercentLabel = new javax.swing.JLabel();
        returnMarkupPercent = new javax.swing.JFormattedTextField();
        returnMarkupLabel = new javax.swing.JLabel();
        returnMarkupField = new javax.swing.JFormattedTextField();
        groupSelector = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        healingField = new javax.swing.JTextField();
        ampField = new javax.swing.JTextField();
        weaponField = new javax.swing.JTextField();
        armorField = new javax.swing.JTextField();
        totalCostField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        ammoStatField = new javax.swing.JTextField();
        settingsButton = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        overviewButton = new javax.swing.JButton();
        newRunButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        deleteRunButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Overview");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                actionOnWindowClosing(evt);
            }
        });

        mainPanel.setLayout(new java.awt.CardLayout());

        dataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mob", "ID", "Cost", "LootTT", "Markup", "ReturnTT", "ReturnTT %", "Return MU", "ReturnMU %", "Note"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(dataTable);
        if (dataTable.getColumnModel().getColumnCount() > 0) {
            dataTable.getColumnModel().getColumn(1).setResizable(false);
            dataTable.getColumnModel().getColumn(1).setPreferredWidth(20);
        }

        javax.swing.GroupLayout overviewPanelLayout = new javax.swing.GroupLayout(overviewPanel);
        overviewPanel.setLayout(overviewPanelLayout);
        overviewPanelLayout.setHorizontalGroup(
            overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(overviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1115, Short.MAX_VALUE)
                .addContainerGap())
        );
        overviewPanelLayout.setVerticalGroup(
            overviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 601, Short.MAX_VALUE)
        );

        mainPanel.add(overviewPanel, "overview");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Equipment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        equipmentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "start TT", "End TT", "Markup"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addEquipButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addGap(2, 2, 2)
                .addComponent(removeEquipButton)
                .addGap(675, 675, 675))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEquipButton)
                    .addComponent(removeEquipButton)
                    .addComponent(editButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
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
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 360, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel3.setText("Ammo");

        mobSelector.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        mobSelector.setToolTipText("");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel2.setText("Mob");

        newMobButton.setText("New Mob");
        newMobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMobButtonActionPerformed(evt);
            }
        });

        dateField.setEditable(false);

        jLabel9.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel9.setText("End Date");

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel10.setText("Universal");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(universalAmmoField, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newMobButton)
                .addGap(77, 77, 77)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(newMobButton))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(universalAmmoField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
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
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(lootTable);

        addLootButton.setText("Add");
        addLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLootButtonActionPerformed(evt);
            }
        });

        editLootButton.setText("Edit");
        editLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLootButtonActionPerformed(evt);
            }
        });

        removeLootButton.setText("Remove");
        removeLootButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLootButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(addLootButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editLootButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeLootButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addLootButton)
                    .addComponent(editLootButton)
                    .addComponent(removeLootButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        updateRunButton.setText("Update Run");
        updateRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateRunButtonActionPerformed(evt);
            }
        });

        endRunButton.setText("End Run");
        endRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endRunButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout runDetailsPanelLayout = new javax.swing.GroupLayout(runDetailsPanel);
        runDetailsPanel.setLayout(runDetailsPanelLayout);
        runDetailsPanelLayout.setHorizontalGroup(
            runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(runDetailsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addComponent(updateRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(endRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(92, Short.MAX_VALUE))
        );
        runDetailsPanelLayout.setVerticalGroup(
            runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(runDetailsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))))
        );

        mainPanel.add(runDetailsPanel, "runDetails");

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Equipment", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        equipmentSettingsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Type", "Last TT", "Markup"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(equipmentSettingsTable);
        if (equipmentSettingsTable.getColumnModel().getColumnCount() > 0) {
            equipmentSettingsTable.getColumnModel().getColumn(0).setMaxWidth(70);
        }

        addEquipSettingsButton.setText("Add");
        addEquipSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEquipSettingsButtonActionPerformed(evt);
            }
        });

        editEquipSettingsButton.setText("Edit");
        editEquipSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editEquipSettingsButtonActionPerformed(evt);
            }
        });

        saveEquipButton.setText("Save");
        saveEquipButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveEquipButtonActionPerformed(evt);
            }
        });

        removeEquipSettingsButton.setText("Remove");
        removeEquipSettingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeEquipSettingsButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(addEquipSettingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editEquipSettingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeEquipSettingsButton)
                        .addGap(4, 4, 4)
                        .addComponent(saveEquipButton)))
                .addContainerGap(60, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addEquipSettingsButton)
                    .addComponent(editEquipSettingsButton)
                    .addComponent(saveEquipButton)
                    .addComponent(removeEquipSettingsButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Markup Table", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        markupTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "Markup"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane6.setViewportView(markupTable);

        addMarkupButton.setText("Add");
        addMarkupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMarkupButtonActionPerformed(evt);
            }
        });

        editMarkupButton.setText("Edit");
        editMarkupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMarkupButtonActionPerformed(evt);
            }
        });

        removeMarkupButton.setText("Remove");

        saveMarkupButton.setText("Save");
        saveMarkupButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMarkupButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(addMarkupButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(editMarkupButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeMarkupButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(saveMarkupButton)))
                .addContainerGap(54, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addMarkupButton)
                    .addComponent(editMarkupButton)
                    .addComponent(removeMarkupButton)
                    .addComponent(saveMarkupButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6))
        );

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(settingsPanelLayout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 59, Short.MAX_VALUE))
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingsPanelLayout.createSequentialGroup()
                .addGroup(settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        mainPanel.add(settingsPanel, "settings");

        totalCostLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalCostLabel.setText("Total Cost");

        totalLootLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalLootLabel.setText("Total LootTT");

        totalMarkupLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        totalMarkupLabel.setText("Total MU");

        returnTTLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        returnTTLabel.setText("ReturnTT");

        returnMarkupPercentLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        returnMarkupPercentLabel.setText("ReturnMU %");

        totalLootTTField.setEditable(false);

        totalMarkupField.setEditable(false);

        returnTTField.setEditable(false);

        returnTTpercentField.setEditable(false);

        returnTTPercentLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        returnTTPercentLabel.setText("ReturnTT %");

        returnMarkupPercent.setEditable(false);

        returnMarkupLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        returnMarkupLabel.setText("Return MU");

        returnMarkupField.setEditable(false);

        groupSelector.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                groupSelectorItemStateChanged(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel1.setText("Mob");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel5.setText("Armor");

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel6.setText("Healing");

        jLabel7.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel7.setText("Amp");

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel8.setText("Weapon");

        healingField.setEditable(false);

        ampField.setEditable(false);

        weaponField.setEditable(false);

        armorField.setEditable(false);

        totalCostField.setEditable(false);

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel11.setText("Ammo");

        ammoStatField.setEditable(false);

        javax.swing.GroupLayout totalDataPanelLayout = new javax.swing.GroupLayout(totalDataPanel);
        totalDataPanel.setLayout(totalDataPanelLayout);
        totalDataPanelLayout.setHorizontalGroup(
            totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalDataPanelLayout.createSequentialGroup()
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalDataPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(groupSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalDataPanelLayout.createSequentialGroup()
                        .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addGap(3, 3, 3)
                                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel11))
                                .addGap(18, 18, 18)
                                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ampField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                    .addComponent(healingField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                    .addComponent(weaponField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                    .addComponent(armorField, javax.swing.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE)
                                    .addComponent(ammoStatField)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalDataPanelLayout.createSequentialGroup()
                                .addComponent(totalCostLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(85, 85, 85)
                        .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addComponent(totalMarkupLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(totalMarkupField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addComponent(totalLootLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                                .addComponent(totalLootTTField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addComponent(returnTTLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(returnTTField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addComponent(returnMarkupLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(returnMarkupField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalDataPanelLayout.createSequentialGroup()
                                .addComponent(returnTTPercentLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(returnTTpercentField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, totalDataPanelLayout.createSequentialGroup()
                                .addComponent(returnMarkupPercentLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addComponent(returnMarkupPercent, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(44, 44, 44))
        );
        totalDataPanelLayout.setVerticalGroup(
            totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalDataPanelLayout.createSequentialGroup()
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(groupSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalLootTTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalLootLabel)
                    .addComponent(weaponField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalMarkupField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(totalMarkupLabel)
                    .addComponent(jLabel7)
                    .addComponent(ampField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnTTLabel)
                    .addComponent(returnTTField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(healingField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnMarkupLabel)
                    .addComponent(returnMarkupField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(armorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(totalDataPanelLayout.createSequentialGroup()
                        .addGap(7, 7, 7)
                        .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(returnTTPercentLabel)
                            .addComponent(returnTTpercentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(totalDataPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(ammoStatField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnMarkupPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnMarkupPercentLabel)
                    .addComponent(totalCostLabel)
                    .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(681, 681, 681))
        );

        settingsButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        settingsButton.setText("Settings");
        settingsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                settingsButtonActionPerformed(evt);
            }
        });

        detailsButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        detailsButton.setText("Details");
        detailsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                detailsButtonActionPerformed(evt);
            }
        });

        overviewButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        overviewButton.setText("Overview");
        overviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overviewButtonActionPerformed(evt);
            }
        });

        newRunButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        newRunButton.setText("New Run");
        newRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newRunButtonActionPerformed(evt);
            }
        });

        deleteRunButton.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        deleteRunButton.setText("Delete Run");
        deleteRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteRunButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator1)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(overviewButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(detailsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(settingsButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(34, 34, 34)
                        .addComponent(totalDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(110, 110, 110)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(overviewButton)
                            .addComponent(detailsButton)
                            .addComponent(settingsButton)
                            .addComponent(newRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(totalDataPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 8, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 601, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void addEquipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEquipButtonActionPerformed
        String[] equipmentInput = EquipmentParser.getEquipmentInput((JFrame)this, lootTracker, null, null, null, null, null);
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

    private void overviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overviewButtonActionPerformed
        detailsButton.setEnabled(true);
        deleteRunButton.setEnabled(true);
        markupTable.clearSelection();
        equipmentSettingsTable.clearSelection();
        equipmentTable.clearSelection();
        lootTable.clearSelection();
        CardLayout card = (CardLayout)mainPanel.getLayout();
        card.show(mainPanel, "overview");
    }//GEN-LAST:event_overviewButtonActionPerformed

    private void detailsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_detailsButtonActionPerformed
        showRunDetails();
    }//GEN-LAST:event_detailsButtonActionPerformed

    private void newRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newRunButtonActionPerformed
        new NewRunUI(this.lootTracker, this).setVisible(true);
    }//GEN-LAST:event_newRunButtonActionPerformed

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        int row = equipmentTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editEquipDialog(row);
    }//GEN-LAST:event_editButtonActionPerformed

    private void editEquipDialog(int row){
        String[] equipmentInput = EquipmentParser.getEquipmentInput(
                (JFrame)this,
                lootTracker,
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
    }
    
    private void removeEquipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEquipButtonActionPerformed
        int row = equipmentTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel)equipmentTable.getModel();
        model.removeRow(row);
    }//GEN-LAST:event_removeEquipButtonActionPerformed

    private void newMobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMobButtonActionPerformed
        String newMob = JOptionPane.showInputDialog((JFrame)this, "Name of Mob:");
        this.mobSelector.addItem(newMob);
        this.mobSelector.setSelectedItem(newMob);
        this.groupSelector.addItem(newMob);
    }//GEN-LAST:event_newMobButtonActionPerformed

    private void groupSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_groupSelectorItemStateChanged
        updateStatisticsPanel();        
    }//GEN-LAST:event_groupSelectorItemStateChanged
    
    private void updateStatisticsPanel(){
        if(this.lootTracker.getHuntingData().size() > 0){
            String group = this.groupSelector.getSelectedItem().toString();
            if(this.lootTracker.getGroups().contains(group)){
                Map<DataKey, Double> statsForGroup = this.lootTracker.getStatsForGroup(group);
                Utilities.roundData(statsForGroup);
                setFields(statsForGroup);
            }
            else if(group.equals("All")){
                Map<DataKey, Double> statsForAll = this.lootTracker.getStatsForAllGroups();
                Utilities.roundData(statsForAll);
                setFields(statsForAll);
            }
        }
        else{
            setDefaultStatistics();
        }
    }
    
    private void setFields(Map<DataKey, Double> statsForGroup){
        this.weaponField.setText(statsForGroup.get(DataKey.WeaponDecayWithMarkup).toString());
        this.ampField.setText(statsForGroup.get(DataKey.AmpDecayWithMarkup).toString());
        this.healingField.setText(statsForGroup.get(DataKey.HealingDecayWithMarkup).toString());
        this.armorField.setText(statsForGroup.get(DataKey.ArmorDecayWithMarkup).toString());
        this.totalCostField.setText(statsForGroup.get(DataKey.TotalCost).toString());
        this.totalLootTTField.setText(statsForGroup.get(DataKey.TotalLootTT).toString());
        this.totalMarkupField.setText(statsForGroup.get(DataKey.Markup).toString());
        this.returnTTField.setText(statsForGroup.get(DataKey.ReturnTT).toString());
        this.returnMarkupField.setText(statsForGroup.get(DataKey.ReturnWithMarkup).toString());
        this.returnTTpercentField.setText(statsForGroup.get(DataKey.ReturnTTpercent).toString());
        this.returnMarkupPercent.setText(statsForGroup.get(DataKey.ReturnWithMarkupPercent).toString());
        this.ammoStatField.setText(Double.toString(Utilities.round(statsForGroup.get(DataKey.Ammo) + statsForGroup.get(DataKey.UniversalAmmo), 2)));
    }
    
    private void setDefaultStatistics(){
        String zeroString = "";
        this.weaponField.setText(zeroString);
        this.ampField.setText(zeroString);
        this.healingField.setText(zeroString);
        this.armorField.setText(zeroString);
        this.totalCostField.setText(zeroString);
        this.totalLootTTField.setText(zeroString);
        this.totalMarkupField.setText(zeroString);
        this.returnTTField.setText(zeroString);
        this.returnMarkupField.setText(zeroString);
        this.returnTTpercentField.setText(zeroString);
        this.returnMarkupPercent.setText(zeroString);
    }
    
    private void updateRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRunButtonActionPerformed
        saveDataForHunt(false);      
    }//GEN-LAST:event_updateRunButtonActionPerformed

    private void endRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endRunButtonActionPerformed
        saveDataForHunt(true);        
    }//GEN-LAST:event_endRunButtonActionPerformed

    private void actionOnWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_actionOnWindowClosing
        JFrame frame = (JFrame)evt.getComponent();
        if(JOptionPane.showConfirmDialog(frame, 
                "Are you sure?", "Confirm Close", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION){
            try {
                DiskIO.saveLootTracker(this.lootTracker, df);
                System.exit(0);
            } catch (IOException ex) {
                System.out.println("Failed to save data: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_actionOnWindowClosing

    private void addLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLootButtonActionPerformed
        
        String[] lootInput;
        ArrayList<String> reportedLootForRun = new ArrayList<String>();
        DefaultTableModel model = (DefaultTableModel)this.lootTable.getModel();
        Vector<Vector<String>> lootData = model.getDataVector();
        lootData.forEach((Vector<String> rowData) -> {
            reportedLootForRun.add(rowData.elementAt(0));
        });
        if(this.mobSelector.getItemCount() == 0){
            lootInput = LootParser.getLootInput((JFrame)this, reportedLootForRun, null, null);
        }
        else{
            ArrayList<String> reportedGroup = this.lootTracker.getReportedLootForGroup(this.mobSelector.getSelectedItem().toString());
            reportedGroup.forEach((String s) -> {
                if(! reportedLootForRun.contains(s)){
                    reportedLootForRun.add(s);
                }
            });
            lootInput = LootParser.getLootInput(
                    (JFrame)this,
                    reportedLootForRun,
                null, null);
        }
        
        if(lootInput.length > 0){
            String name = lootInput[0];
            String value = lootInput[1];
            String[] markupData = new String[2];
            try{
                markupData[1] = Double.toString(this.lootTracker.getMarkupHandler().getMarkup(name) * 100);
                markupData[0] = name;
            }
            catch(MarkupHandlerException e){
                markupData = MarkupParser.getMarkupInput((JFrame)this, name, null);
                this.lootTracker.getMarkupHandler().addMarkup(name, Double.parseDouble(markupData[1]) / 100);
            }
            model.addRow(new Object[]{
            name,
            value,
            markupData[1]});
            
        }
        
    }//GEN-LAST:event_addLootButtonActionPerformed

    private void editLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLootButtonActionPerformed
        int row = lootTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No loot entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editLootDialog(row);
    }//GEN-LAST:event_editLootButtonActionPerformed

    private void editLootDialog(int row){
        String[] lootInput;
        ArrayList<String> reportedLootForRun = new ArrayList<String>();
        DefaultTableModel model = (DefaultTableModel)this.lootTable.getModel();
        Vector<Vector<String>> lootData = model.getDataVector();
        lootData.forEach((Vector<String> rowData) -> {
            reportedLootForRun.add(rowData.elementAt(0));
        });
        if(this.mobSelector.getItemCount() == 0){
            lootInput = LootParser.getLootInput(
                    (JFrame)this,
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
                    (JFrame)this,
                reportedLootForRun,
                (String)lootTable.getValueAt(row, 0),
                (String)lootTable.getValueAt(row, 1));
        }
        
        if(lootInput.length > 0){
            model.setValueAt(lootInput[0], row, 0);
            model.setValueAt(lootInput[1], row, 1);
        }
    }
    
    private void removeLootButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLootButtonActionPerformed
        int row = lootTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No loot entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel)lootTable.getModel();
        model.removeRow(row);
    }//GEN-LAST:event_removeLootButtonActionPerformed

    private void deleteRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteRunButtonActionPerformed
        int row = this.dataTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No run selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int result = JOptionPane.showConfirmDialog((JFrame)this, "Are you sure? This is permanent!", "Confirm Delete Run",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION){
            DefaultTableModel model = (DefaultTableModel)this.dataTable.getModel();
            String group = model.getValueAt(row, 0).toString();
            int ID = Integer.parseInt(model.getValueAt(row, 1).toString());
            this.lootTracker.removeHunt(group, ID);
            if(! this.lootTracker.getGroups().contains(group)){
                this.groupSelector.removeItem(group);
                this.mobSelector.removeItem(group);
            }
            model.removeRow(row);
            updateStatisticsPanel();
        }
    }//GEN-LAST:event_deleteRunButtonActionPerformed

    private void settingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_settingsButtonActionPerformed
        showSettingsPanel();
    }//GEN-LAST:event_settingsButtonActionPerformed

    private void editMarkupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMarkupButtonActionPerformed
        int row = markupTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No markup entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editMarkupDialog(row);
    }//GEN-LAST:event_editMarkupButtonActionPerformed

    private void saveMarkupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMarkupButtonActionPerformed
        DefaultTableModel tableModel = (DefaultTableModel)markupTable.getModel();
        Vector<Vector<String>> markupData = tableModel.getDataVector();
        this.lootTracker.getMarkupHandler().updateMarkup(markupData);
    }//GEN-LAST:event_saveMarkupButtonActionPerformed

    private void addMarkupButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMarkupButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addMarkupButtonActionPerformed

    private void addEquipSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEquipSettingsButtonActionPerformed
        String[] equipmentInput = EquipmentParser.getEquipmentInputSettings((JFrame)this, null, null, null, "100");
        if(equipmentInput.length > 0){
            DefaultTableModel model = (DefaultTableModel) equipmentSettingsTable.getModel();
            model.addRow(new Object[]{
                equipmentInput[0], 
                equipmentInput[1], 
                equipmentInput[2], 
                equipmentInput[3]}
            );
        }
    }//GEN-LAST:event_addEquipSettingsButtonActionPerformed

    private void editEquipSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editEquipSettingsButtonActionPerformed
        int row = equipmentSettingsTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        editEquipSettingsDialog(row);
    }//GEN-LAST:event_editEquipSettingsButtonActionPerformed

    private void editEquipSettingsDialog(int row){
        String[] equipmentInput = EquipmentParser.getEquipmentInputSettings(
                (JFrame)this,
                (String)equipmentSettingsTable.getValueAt(row, 0),
                (String)equipmentSettingsTable.getValueAt(row, 1),
                (String)equipmentSettingsTable.getValueAt(row, 2),
                (String)equipmentSettingsTable.getValueAt(row, 3));
        if(equipmentInput.length > 0){
            DefaultTableModel model = (DefaultTableModel) equipmentSettingsTable.getModel();
            model.setValueAt(equipmentInput[0], row, 0);
            model.setValueAt(equipmentInput[1], row, 1);
            model.setValueAt(equipmentInput[2], row, 2);
            model.setValueAt(equipmentInput[3], row, 3);
        }
    }
    private void removeEquipSettingsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeEquipSettingsButtonActionPerformed
        int row = equipmentSettingsTable.getSelectedRow();
        if ( row == -1){
            JOptionPane.showMessageDialog((JFrame)this, "No equipment entry selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel)equipmentSettingsTable.getModel();
        model.removeRow(row);
    }//GEN-LAST:event_removeEquipSettingsButtonActionPerformed

    private void saveEquipButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveEquipButtonActionPerformed
        DefaultTableModel equipmentTableModel = (DefaultTableModel)equipmentSettingsTable.getModel();
        Vector<Vector<String>> equipmentData = equipmentTableModel.getDataVector();
        EquipmentUtilities.updateEquipmentSettingsFromData(this.lootTracker, equipmentData);
    }//GEN-LAST:event_saveEquipButtonActionPerformed
    
    private void showSettingsPanel(){
        prepareSettingsPanel();
        detailsButton.setEnabled(false);
        deleteRunButton.setEnabled(false);
        CardLayout card = (CardLayout)mainPanel.getLayout();
        card.show(mainPanel, "settings");
    }
    
    private void prepareSettingsPanel(){
        ArrayList<Equipment> allEquipment = this.lootTracker.getAllEquipment();
        Map<String, Double> markupData = lootTracker.getMarkupHandler().getMarkupTable();
        addEquipmentToSettingsPanel(allEquipment);
        addMarkupDataToSettingsPanel(markupData);
    }
    
    private void addEquipmentToSettingsPanel(ArrayList<Equipment> allEquipment){
        DefaultTableModel tableModel = (DefaultTableModel)this.equipmentSettingsTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        
        allEquipment.forEach((Equipment e)->{
            String type = EquipmentUtilities.getTypeForEquipment(e);
            tableModel.addRow(new Object[]
                {e.getName(),
                type,
                Double.toString(e.getValue()),
                Double.toString(e.getMarkup() * 100)}
            );
            
        });
    }
    
    private void addMarkupDataToSettingsPanel(Map<String, Double> markupData){
        DefaultTableModel tableModel = (DefaultTableModel)this.markupTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        
        markupData.forEach((String name, Double markup)->{
            tableModel.addRow(new Object[]{
                name,
                Double.toString(Utilities.round(100 * markup, 2))
            });
        });
    }
    
    private void editMarkupDialog(int row){
        String[] markupInput = MarkupParser.getMarkupInput(
                (JFrame)this,
                (String)markupTable.getValueAt(row, 0),
                (String)markupTable.getValueAt(row, 1));
        
        if(markupInput.length > 0){
           DefaultTableModel model = (DefaultTableModel) markupTable.getModel();
           model.setValueAt(markupInput[0], row, 0);
           model.setValueAt(markupInput[1], row, 1);
       }
    }
    private void saveDataForHunt(boolean end){
        DefaultTableModel equipmentTableModel = (DefaultTableModel)equipmentTable.getModel();
        Vector<Vector<String>> equipmentData = equipmentTableModel.getDataVector();
        DefaultTableModel lootTableModel = (DefaultTableModel)lootTable.getModel();
        Vector<Vector<String>> lootData = lootTableModel.getDataVector();
        
        DefaultTableModel dataTableModel = (DefaultTableModel)this.dataTable.getModel();
        int row = dataTable.getSelectedRow();
        int ID = Integer.parseInt((String)dataTableModel.getValueAt(row, 1));
        
        double ammo;
        double universalAmmo;
        
        if(Utilities.validateString(this.ammoField.getText(), true)){
            ammo = Double.parseDouble(this.ammoField.getText());
        }
        else{
            JOptionPane.showMessageDialog((JFrame)this, "Invalid Ammo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if(Utilities.validateString(this.universalAmmoField.getText(), true)){
            universalAmmo = Double.parseDouble(this.universalAmmoField.getText());
        }
        else{
            JOptionPane.showMessageDialog((JFrame)this, "Invalid Universal Ammo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String newGroup = (String)this.mobSelector.getSelectedItem();
        String note = this.noteField.getText();
        int result = JOptionPane.showConfirmDialog((JFrame)this, null, "Are you sure? Make sure all the data is correct!", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            if(newGroup != this.oldGroupForDetailsPanel){
                this.lootTracker.changeGroupForHunt(newGroup, this.oldGroupForDetailsPanel, ID);
                if(! this.lootTracker.getHuntingData().containsKey(this.oldGroupForDetailsPanel)){
                    this.groupSelector.removeItem(this.oldGroupForDetailsPanel);
                    this.mobSelector.removeItem(this.oldGroupForDetailsPanel);
                    this.groupSelector.setSelectedItem(newGroup);
                }
            }
            this.lootTracker.updateHunt(newGroup, ID, ammo, universalAmmo, lootData, equipmentData, note); 
            this.lootTracker.addLootForGroup(newGroup, this.lootTracker.getHunt(newGroup, ID).getLoot());
            Map<DataKey, Double> huntData = this.lootTracker.getDataForHunt(newGroup, ID);
            updateDataTableRow(dataTableModel, row, newGroup, huntData, note);
            updateStatisticsPanel();
            if(end){
                this.lootTracker.endHunt(newGroup, ID);
            }
            EquipmentUtilities.addEquipmentToSettingsFromData(lootTracker, equipmentData);
            CardLayout card = (CardLayout)mainPanel.getLayout();
            card.show(mainPanel, "overview"); 
        }
    }
    
    private void updateDataTableRow(DefaultTableModel dataTableModel, int row, String group, Map<DataKey, Double> huntData, String note){
        dataTableModel.setValueAt(group, row, 0);
        dataTableModel.setValueAt(huntData.get(DataKey.TotalCost).toString(), row, 2);
        dataTableModel.setValueAt(huntData.get(DataKey.TotalLootTT).toString(), row, 3);
        dataTableModel.setValueAt(huntData.get(DataKey.Markup).toString(), row, 4);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnTT).toString(), row, 5);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnTTpercent).toString(), row, 6);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnWithMarkup).toString(), row, 7);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnWithMarkupPercent).toString(), row, 8);
        dataTableModel.setValueAt(note, row, 9);
    }
    
    public JComboBox<String> getGroupSelector(){
        return this.groupSelector;
    }
    
    public void addRunToDisplay(int runID, String group, Hunt hunt){
        DefaultTableModel dataTableModel = (DefaultTableModel)dataTable.getModel();
        Map<DataKey, Double> huntData = hunt.getDataForHunt(this.lootTracker.getMarkupHandler());
        dataTableModel.addRow(new Object[]
        {group,
        Integer.toString(runID),
        huntData.get(DataKey.TotalCost).toString(),
        huntData.get(DataKey.TotalLootTT).toString(),
        huntData.get(DataKey.Markup).toString(),
        huntData.get(DataKey.ReturnTT).toString(),
        huntData.get(DataKey.ReturnTTpercent).toString(),
        huntData.get(DataKey.ReturnWithMarkup).toString(),
        huntData.get(DataKey.ReturnWithMarkupPercent).toString(),
        hunt.getNote()});
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel)this.groupSelector.getModel();
        if(comboBoxModel.getIndexOf(group) == -1){
            this.groupSelector.addItem(group);
            this.groupSelector.setSelectedItem(group);
            this.mobSelector.addItem(group);
        }
        updateStatisticsPanel();
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
            java.util.logging.Logger.getLogger(LootTrackerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LootTrackerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LootTrackerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LootTrackerUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {        
                try {
                    new LootTrackerUI().setVisible(true);
                } catch (InvalidFormatException | ParseException | InvalidKeyException ex) {
                    System.out.println("Invalid Json file with data!");
                    ex.printStackTrace();
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addEquipButton;
    private javax.swing.JButton addEquipSettingsButton;
    private javax.swing.JButton addLootButton;
    private javax.swing.JButton addMarkupButton;
    private javax.swing.JTextField ammoField;
    private javax.swing.JTextField ammoStatField;
    private javax.swing.JTextField ampField;
    private javax.swing.JTextField armorField;
    private javax.swing.JTable dataTable;
    private javax.swing.JTextField dateField;
    private javax.swing.JButton deleteRunButton;
    private javax.swing.JButton detailsButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editEquipSettingsButton;
    private javax.swing.JButton editLootButton;
    private javax.swing.JButton editMarkupButton;
    private javax.swing.JButton endRunButton;
    private javax.swing.JTable equipmentSettingsTable;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JComboBox<String> groupSelector;
    private javax.swing.JTextField healingField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable lootTable;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTable markupTable;
    private javax.swing.JComboBox<String> mobSelector;
    private javax.swing.JButton newMobButton;
    private javax.swing.JButton newRunButton;
    private javax.swing.JTextArea noteField;
    private javax.swing.JButton overviewButton;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JButton removeEquipButton;
    private javax.swing.JButton removeEquipSettingsButton;
    private javax.swing.JButton removeLootButton;
    private javax.swing.JButton removeMarkupButton;
    private javax.swing.JFormattedTextField returnMarkupField;
    private javax.swing.JLabel returnMarkupLabel;
    private javax.swing.JFormattedTextField returnMarkupPercent;
    private javax.swing.JLabel returnMarkupPercentLabel;
    private javax.swing.JFormattedTextField returnTTField;
    private javax.swing.JLabel returnTTLabel;
    private javax.swing.JLabel returnTTPercentLabel;
    private javax.swing.JFormattedTextField returnTTpercentField;
    private javax.swing.JPanel runDetailsPanel;
    private javax.swing.JButton saveEquipButton;
    private javax.swing.JButton saveMarkupButton;
    private javax.swing.JButton settingsButton;
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    private javax.swing.JPanel totalDataPanel;
    private javax.swing.JLabel totalLootLabel;
    private javax.swing.JFormattedTextField totalLootTTField;
    private javax.swing.JFormattedTextField totalMarkupField;
    private javax.swing.JLabel totalMarkupLabel;
    private javax.swing.JTextField universalAmmoField;
    private javax.swing.JButton updateRunButton;
    private javax.swing.JTextField weaponField;
    // End of variables declaration//GEN-END:variables
}
