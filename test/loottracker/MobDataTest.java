/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.nio.file.Paths;
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
public class MobDataTest {
    private MarkupHandler markupHandler = new MarkupHandler(
            "C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\data\\markup_info.txt");
    private MobData mobData;
    public MobDataTest() {
    }
    
    @Before
    public void setUpClass() {
        mobData = new MobData();
    }
    
    @After
    public void tearDownClass() {
        mobData = null;
        assertNull(mobData);
    }

    /**
     * Test of getDataForHunts method, of class MobData.
     */
    @Test
    public void testGetDataForHunts() {
        Hunt hunt1 = new Hunt();
        Hunt hunt2 = new Hunt();
        Loot loot = new Loot("test", 25.2);
        loot.setSoldWithMarkup(1.02);
        Weapon weapon = new Weapon("LLC", 34.0, 1.35, 29.0);
        hunt1.addLoot(loot);
        hunt2.addLoot(loot);
        hunt1.addEquipment(weapon);
        hunt2.addEquipment(weapon);
        hunt1.addAmmo(20.0);
        hunt2.addAmmo(20.0);
        hunt1.end(new Date());
        hunt2.end(new Date());
        hunt1.computeDataForHunt();
        hunt2.computeDataForHunt();
        mobData.addHunt(1, hunt1);
        mobData.addHunt(2, hunt2);
        Map<DataKey, Double> data = mobData.getDataForHunts(markupHandler);
        assertEquals(Double.compare(data.get(DataKey.Ammo), 40.0), 0);
        assertEquals(Double.compare(data.get(DataKey.TotalDecayWithMarkup), 2 * 5 * 1.35), 0);
        assertEquals(Double.compare(data.get(DataKey.TotalLootTT), 2 * 25.2), 0);
        assertEquals(Double.compare(data.get(DataKey.TotalLootWithMarkup), Utilities.round(1.02 * 2 * 25.2, 1)), 0);
        assertEquals(Double.compare(data.get(DataKey.TotalCost), Utilities.round(2 * 5 * 1.35 + 40.0, 2)), 0);
        assertEquals(Double.compare(data.get(DataKey.ReturnTT), Utilities.round(data.get(DataKey.TotalLootTT) - data.get(DataKey.TotalCost), 1)), 0);
    }
    
}
