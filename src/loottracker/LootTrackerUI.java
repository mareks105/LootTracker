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
        try {
            this.lootTracker = DiskIO.loadDataFromFile(df);
        } catch (IOException ex) {
            System.out.println("Failed to load data");
            ex.printStackTrace();
        }
        initComponents();
        initDataTables();
        //setupTestCase();
        displayDataFromLootTracker();
    }
    
    private void displayDataFromLootTracker(){
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
    
    private void setupTestCase(){
        String testGroup = new String("Kerberos");
        
        Hunt hunt = createTestHunt();
        hunt.end(new Date());
        int ID = this.lootTracker.addHuntToGroup(testGroup, hunt);
        addRunToDisplay(ID, testGroup, hunt);
        this.groupSelector.addItem(testGroup);
        this.groupSelector.setSelectedItem(testGroup);
        this.dateField.setText(df.format(hunt.getEndDate()));
    }
    
    private Hunt createTestHunt(){
        Vector<Vector<String>> equipmentData = new Vector<>();
        Vector<String> e = new Vector<>();
        e.add("LP-10(L)");
        e.add("Weapon");
        e.add("13.4");
        e.add("120");
        e.add("8.3");
        equipmentData.add(e);
        Vector<Vector<String>> lootData = new Vector<>();
        Vector<String> l = new Vector<>();
        l.add("Thin Wool");
        l.add("40.32");
        l.add("104");
        lootData.add(l);
        return new Hunt(25.0, lootData, equipmentData, "", this.lootTracker.getMarkupHandler());
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
        UtilitiesUI.addRightClickDeselect(equipmentTable);
        UtilitiesUI.addRightClickDeselect(lootTable);
    }
    
    
    private void showRunDetails(){
        int row = dataTable.getSelectedRow();
        if(row == -1){
            JOptionPane.showMessageDialog(null, "No run selected", "Action Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        prepareDetailsPanel(row);
        CardLayout card = (CardLayout)mainPanel.getLayout();
        //TODO: setupDetailsPanel();
        card.show(mainPanel, "runDetails");
    }
    
    private void prepareDetailsPanel(int row){
        DefaultTableModel tableModel = (DefaultTableModel)this.dataTable.getModel();
        String group = (String)tableModel.getValueAt(row, 0);
        int runID = Integer.parseInt((String)tableModel.getValueAt(row, 1));
        Hunt hunt = this.lootTracker.getMobData(group).getHunt(runID);
        String note = hunt.getNote();
        double ammo = hunt.getAmmo();
        this.ammoField.setText(Double.toString(ammo));
        this.noteField.setText(note);
        ArrayList<Equipment> equipment = hunt.getEquipment();
        ArrayList<Loot> loot = hunt.getLoot();
        addEquipmentToTable(equipment);
        addLootToTable(loot);
        this.mobSelector.setSelectedItem(group);
        this.dateField.setText(this.df.format(hunt.getEndDate()));
        this.oldGroupForDetailsPanel = group;
    }
    
    private void addEquipmentToTable(ArrayList<Equipment> equipment){
        DefaultTableModel tableModel = (DefaultTableModel)this.equipmentTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        equipment.forEach((Equipment e) -> {
            String type = EquipmentParser.getTypeForEquipment(e);
            tableModel.addRow(new Object[]
            {e.getName(),
            type,
            Double.toString(e.getValue()),
            Double.toString(100*e.getMarkup()),
            Double.toString(e.getEndValue())});
        });
    }
    
    private void addLootToTable(ArrayList<Loot> loot){
        DefaultTableModel tableModel = (DefaultTableModel)this.lootTable.getModel();
        tableModel.getDataVector().removeAllElements();
        revalidate();
        loot.forEach((Loot l) -> {
            tableModel.addRow(new Object[]
            {l.getName(),
            Double.toString(l.getValue())});
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
        saveEquipButton = new javax.swing.JButton();
        editButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        noteField = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        ammoField = new javax.swing.JTextField();
        mobSelector = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        newMobButton = new javax.swing.JButton();
        dateField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        lootTable = new javax.swing.JTable();
        addLootButton = new javax.swing.JButton();
        editLootButton = new javax.swing.JButton();
        removeLootButton = new javax.swing.JButton();
        updateRunButton = new javax.swing.JButton();
        endRunButton = new javax.swing.JButton();
        settingsPanel = new javax.swing.JPanel();
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
        jButton7 = new javax.swing.JButton();
        detailsButton = new javax.swing.JButton();
        overviewButton = new javax.swing.JButton();
        newRunButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Overview");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ActionOnWindowClosing(evt);
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
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1095, Short.MAX_VALUE)
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
                "Name", "Type", "start TT", "Markup", "End TT"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(addEquipButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(editButton)
                .addGap(2, 2, 2)
                .addComponent(removeEquipButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(saveEquipButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(598, 598, 598))
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
                    .addComponent(saveEquipButton)
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

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(newMobButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 91, Short.MAX_VALUE)
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
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(ammoField, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(mobSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(newMobButton))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(dateField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createCompoundBorder(), "Loot", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        lootTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Name", "TT"
            }
        ));
        jScrollPane4.setViewportView(lootTable);

        addLootButton.setText("Add");

        editLootButton.setText("Edit");

        removeLootButton.setText("Remove");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 619, Short.MAX_VALUE)
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
                .addContainerGap(15, Short.MAX_VALUE)
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
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 647, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addGap(85, 85, 85)
                        .addComponent(updateRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addComponent(endRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        runDetailsPanelLayout.setVerticalGroup(
            runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(runDetailsPanelLayout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(runDetailsPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, runDetailsPanelLayout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(runDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(updateRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(endRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        mainPanel.add(runDetailsPanel, "runDetails");

        javax.swing.GroupLayout settingsPanelLayout = new javax.swing.GroupLayout(settingsPanel);
        settingsPanel.setLayout(settingsPanelLayout);
        settingsPanelLayout.setHorizontalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1115, Short.MAX_VALUE)
        );
        settingsPanelLayout.setVerticalGroup(
            settingsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 601, Short.MAX_VALUE)
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

        javax.swing.GroupLayout totalDataPanelLayout = new javax.swing.GroupLayout(totalDataPanel);
        totalDataPanel.setLayout(totalDataPanelLayout);
        totalDataPanelLayout.setHorizontalGroup(
            totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(totalDataPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(totalDataPanelLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(groupSelector, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(totalDataPanelLayout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel5))
                                .addGap(18, 18, 18)
                                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(ampField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(healingField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(weaponField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(armorField, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(totalDataPanelLayout.createSequentialGroup()
                                .addComponent(totalCostLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(totalCostField)))
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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addGap(7, 7, 7)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(totalCostLabel)
                    .addComponent(totalCostField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnTTPercentLabel)
                    .addComponent(returnTTpercentField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(totalDataPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(returnMarkupPercent, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(returnMarkupPercentLabel))
                .addGap(681, 681, 681))
        );

        jButton7.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jButton7.setText("Settings");
        jButton7.setEnabled(false);

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
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(newRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(72, 72, 72)
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
                            .addComponent(jButton7)
                            .addComponent(newRunButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        String[] equipmentInput = EquipmentParser.getEquipmentInput(null, null, null, "100", null);
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

    private void newMobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMobButtonActionPerformed
        String newMob = JOptionPane.showInputDialog(null, "Name of Mob:");
        this.mobSelector.addItem(newMob);
        this.mobSelector.setSelectedItem(newMob);
        this.groupSelector.addItem(newMob);
    }//GEN-LAST:event_newMobButtonActionPerformed

    private void groupSelectorItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_groupSelectorItemStateChanged
        updateStatisticsPanel();
    }//GEN-LAST:event_groupSelectorItemStateChanged
    
    private void updateStatisticsPanel(){
        Map<DataKey, Double> statsForGroup = this.lootTracker.getStatsForGroup((String)this.groupSelector.getSelectedItem());
        Utilities.roundData(statsForGroup);
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
    }
    
    private void updateRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateRunButtonActionPerformed
        saveDataForHunt(false);      
    }//GEN-LAST:event_updateRunButtonActionPerformed

    private void endRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endRunButtonActionPerformed
        saveDataForHunt(true);        
    }//GEN-LAST:event_endRunButtonActionPerformed

    private void ActionOnWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ActionOnWindowClosing
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
    }//GEN-LAST:event_ActionOnWindowClosing
    
    private void saveDataForHunt(boolean end){
        DefaultTableModel equipmentTableModel = (DefaultTableModel)equipmentTable.getModel();
        Vector<Vector<String>> equipmentData = equipmentTableModel.getDataVector();
        DefaultTableModel lootTableModel = (DefaultTableModel)lootTable.getModel();
        Vector<Vector<String>> lootData = lootTableModel.getDataVector();
        
        DefaultTableModel dataTableModel = (DefaultTableModel)this.dataTable.getModel();
        int row = dataTable.getSelectedRow();
        int ID = Integer.parseInt((String)dataTableModel.getValueAt(row, 1));
        
        double ammo;
        
        if(Utilities.validateString(this.ammoField.getText(), true)){
            ammo = Double.parseDouble(this.ammoField.getText());
        }
        else{
            JOptionPane.showMessageDialog(null, "Invalid Ammo!", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String newGroup = (String)this.mobSelector.getSelectedItem();
        String note = this.noteField.getText();
        int result = JOptionPane.showConfirmDialog(null, null, "Are you sure?", JOptionPane.YES_OPTION, JOptionPane.NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            if(newGroup != this.oldGroupForDetailsPanel){
                this.lootTracker.changeGroupForHunt(newGroup, this.oldGroupForDetailsPanel, ID);
                if(! this.lootTracker.getHuntingData().containsKey(this.oldGroupForDetailsPanel)){
                    this.groupSelector.removeItem(this.oldGroupForDetailsPanel);
                    this.groupSelector.setSelectedItem(newGroup);
                }
            }
            this.lootTracker.updateHunt(newGroup, ID, ammo, lootData, equipmentData, note); 
            Map<DataKey, Double> huntData = this.lootTracker.getDataForHunt(newGroup, ID);
            updateDataTableRow(dataTableModel, row, newGroup, huntData);
            updateStatisticsPanel();
            if(end){
                this.lootTracker.endHunt(newGroup, ID);
            }
            CardLayout card = (CardLayout)mainPanel.getLayout();
            card.show(mainPanel, "overview"); 
        }
    }
    
    private void updateDataTableRow(DefaultTableModel dataTableModel, int row, String group, Map<DataKey, Double> huntData){
        dataTableModel.setValueAt(group, row, 0);
        dataTableModel.setValueAt(huntData.get(DataKey.TotalCost).toString(), row, 2);
        dataTableModel.setValueAt(huntData.get(DataKey.TotalLootTT).toString(), row, 3);
        dataTableModel.setValueAt(huntData.get(DataKey.Markup).toString(), row, 4);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnTT).toString(), row, 5);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnTTpercent).toString(), row, 6);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnWithMarkup).toString(), row, 7);
        dataTableModel.setValueAt(huntData.get(DataKey.ReturnWithMarkupPercent).toString(), row, 8);
    }
    
    public JComboBox<String> getGroupSelector(){
        return this.groupSelector;
    }
    
    public void addRunToDisplay(int runID, String group, Hunt hunt){
        DefaultTableModel dataTableModel = (DefaultTableModel)dataTable.getModel();
        Map<DataKey, Double> huntData = hunt.getDataForHunt(this.lootTracker.getMarkupHandler());
        dataTableModel.addRow(new Object[]
        {group,
        runID,
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
    private javax.swing.JButton addLootButton;
    private javax.swing.JTextField ammoField;
    private javax.swing.JTextField ampField;
    private javax.swing.JTextField armorField;
    private javax.swing.JTable dataTable;
    private javax.swing.JTextField dateField;
    private javax.swing.JButton detailsButton;
    private javax.swing.JButton editButton;
    private javax.swing.JButton editLootButton;
    private javax.swing.JButton endRunButton;
    private javax.swing.JTable equipmentTable;
    private javax.swing.JComboBox<String> groupSelector;
    private javax.swing.JTextField healingField;
    private javax.swing.JButton jButton7;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTable lootTable;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JComboBox<String> mobSelector;
    private javax.swing.JButton newMobButton;
    private javax.swing.JButton newRunButton;
    private javax.swing.JTextArea noteField;
    private javax.swing.JButton overviewButton;
    private javax.swing.JPanel overviewPanel;
    private javax.swing.JButton removeEquipButton;
    private javax.swing.JButton removeLootButton;
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
    private javax.swing.JPanel settingsPanel;
    private javax.swing.JTextField totalCostField;
    private javax.swing.JLabel totalCostLabel;
    private javax.swing.JPanel totalDataPanel;
    private javax.swing.JLabel totalLootLabel;
    private javax.swing.JFormattedTextField totalLootTTField;
    private javax.swing.JFormattedTextField totalMarkupField;
    private javax.swing.JLabel totalMarkupLabel;
    private javax.swing.JButton updateRunButton;
    private javax.swing.JTextField weaponField;
    // End of variables declaration//GEN-END:variables
}
