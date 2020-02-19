package loottracker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mege9
 */
public class InventoryParserTest {
    InventoryParser parser;
    
    private Path beforeFile = Paths.get("C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\test\\data", "testInventoryBefore.txt");
    private Path afterFile = Paths.get("C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\test\\data", "testInventoryAfter.txt");
    private Charset charset = Charset.forName("ISO-8859-1");
    
    @Before
    public void setUpClass() {
        parser = new InventoryParser();
        try{
            List<String> beforeInventory = Files.readAllLines(beforeFile, this.charset);
            List<String> afterInventory = Files.readAllLines(afterFile, this.charset);
            parser.loadInventory(beforeInventory, afterInventory);
        }
        catch (IOException e){
            System.out.println("Inventory files for test not found!");
        }
    }
    
    @After
    public void tearDownClass() {
    }
    
    /**
     * Test of getInventoryChangesForHunt.
     */
    @Test
    public void testGetInventoryChangesForHunt() {
        Map<String, ArrayList<Item>> changesForHunt = this.parser.getInventoryChangesForHunt();

        // Decay
        assertTrue(changesForHunt.get("Decay").size() == 1);// One item with decay
        assertTrue("ArMatrix LR-20 (L)".equals(changesForHunt.get("Decay").get(0).name));
        assertTrue(changesForHunt.get("Decay").get(0).valueTT == 4.0);
        
        // Loot
        assertTrue(changesForHunt.get("Loot").size() == 1);// One piece of loot
        assertTrue("ArMatrix LP-15 (L)".equals(changesForHunt.get("Loot").get(0).name));
        assertTrue(changesForHunt.get("Loot").get(0).valueTT == 46.0);
    }
}

