/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author mege9
 */
public class HuntTest {
    private Hunt hunt = new Hunt();
    private Hunt testHunt;
    private MarkupHandler markupHandler = new MarkupHandler(
        "C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\data\\markup_info.txt");
    
    public HuntTest() {
    }
    
    @Before
    public void setUp() {
        testHunt = hunt;
    }
    
    @After
    public void tearDown() {
        testHunt = null;
        assertNull(testHunt);
    }

    /**
     * Test of addEquipment method, of class Hunt.
     */
    @Test
    public void testAddEquipment() {
        int equipmentBefore = this.testHunt.getEquipment().size();
        this.testHunt.addEquipment(new Weapon("test", 1.0, 1.05, 0.5));
        assertEquals(this.testHunt.getEquipment().size(), equipmentBefore + 1);
    }

    /**
     * Test of addLoot method, of class Hunt.
     */
    @Test
    public void testAddLoot() {
        int lootBefore = this.testHunt.getLoot().size();
        this.testHunt.addLoot(new Loot("Muscle Oil", 2.3));
        ArrayList<Loot> loot = this.testHunt.getLoot();
        assertEquals(loot.size(), lootBefore + 1);
        assertEquals(loot.get(0).getName(), "Muscle Oil");
        assertEquals(Double.compare(2.3, loot.get(0).getValue()), 0);
    }

    /**
     * Test of addData method, of class Hunt.
     */
    @Test
    public void testAddData() {

        this.testHunt.addData(DataKey.Ammo, 10.0);
        assertEquals(Double.compare(this.testHunt.getAmmo(),
                10.0), 0);
    }

    

    /**
     * Test of addAmmo method, of class Hunt.
     */
    @Test
    public void testAddAmmo() {
        double ammo = 25.23;
        double ammoBefore = this.testHunt.getAmmo();
        this.testHunt.addAmmo(ammo);
        assertEquals(Double.compare(this.testHunt.getAmmo(),
                ammoBefore + ammo),0);
    }

    /**
     * Test of end method, of class Hunt.
     */
    @Test
    public void testEndAndGetDataForHunt() {
        Loot loot = new Loot("Muscle Oil", 14.42);
        loot.setSoldWithMarkup(1.03);
        this.testHunt.addLoot(loot);
        this.testHunt.addEquipment(new Weapon("Adapted Isis LLC-10 (L)", 53.0, 1.4, 51.0));
        this.testHunt.addAmmo(45.0);
        this.testHunt.end(new Date());
        this.testHunt.computeDataForHunt();
        Map<DataKey, Double> dataTable = this.testHunt.getDataForHunt(this.markupHandler);
        assertEquals(Double.compare(dataTable.get(DataKey.Ammo), 45.0), 0);
        assertEquals(Double.compare(dataTable.get(DataKey.TotalCost), (2 * 1.4) + 45.0), 0);
        assertEquals(Double.compare(dataTable.get(DataKey.WeaponDecayWithMarkup), 2 * 1.4), 0);
        assertEquals(Double.compare(dataTable.get(DataKey.TotalLootWithMarkup), Utilities.round(loot.valueTT * loot.markup, 2)), 0);
    }

    /**
     * Test of getEndDate method, of class Hunt.
     */
    @Test
    public void testGetDate() {
        Date expResult = new Date();
        testHunt.end(expResult);
        Date result = testHunt.getEndDate();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
    }
}
