/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

/**
 *
 * @author mege9
 */
public class LootTracker {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        InventoryParser inventoryParser = new InventoryParser();
		Map<String, ArrayList<Item>> lootForHunt = inventoryParser.getInventoryChangesForHunt();
		
		MarkupHandler markupHandler = new MarkupHandler(
				Paths.get("C:\\Users\\mege9\\Documents\\MyProjects\\LootTracker\\data", "markup_info.txt"));
    }
    
}
