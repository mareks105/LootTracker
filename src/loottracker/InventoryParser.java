package loottracker;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * 
 */

/**
 * @author mege9
 *
 */
public class InventoryParser {
	
	private Path beforeFile = Paths.get("C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\data", "inventoryBefore2.txt");
	private Path afterFile = Paths.get("C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\data", "inventoryAfter2.txt");
	private Charset charset = Charset.forName("ISO-8859-1");
        
        List<String> beforeInventory;
        List<String> afterInventory;
        
        private enum InputIndex {
            Name,
            Quantity,
            Value,
            Container
        }
        
        public void loadInventory(List<String> beforeInventory, List<String> afterInventory){
            this.beforeInventory = beforeInventory;
            this.afterInventory = afterInventory;
        }
        
	public Map<String, ArrayList<Item>> getInventoryChangesForHunt(){
            ArrayList<Item> lootForHunt = new ArrayList<>();
            ArrayList<Item> itemsBeforeHunt = parseInventory(beforeInventory);
            ArrayList<Item> itemsAfterHunt = parseInventory(afterInventory);
            return getInventoryChanges(itemsBeforeHunt, itemsAfterHunt);
	}
	
	private Map<String, ArrayList<Item>> getInventoryChanges(ArrayList<Item> itemsBeforeHunt, ArrayList<Item> itemsAfterHunt){
            ArrayList<Item> loot = new ArrayList<>();
            ArrayList<Item> decay = new ArrayList<>();
            ArrayList<String> usedItems = new ArrayList<>();
            for (Item l : itemsAfterHunt) {
                    double val = l.getValue();
                    boolean newItem = true;
                    for (Item k : itemsBeforeHunt) {
                            if(l.getName().equals(k.getName())) {
                                    double valueBefore = k.getValue();
                                    double valueAfter = l.getValue();
                                    int compare = Double.compare(valueAfter, valueBefore);
                                    if(compare > 0) {
                                            val = valueAfter - valueBefore;
                                            loot.add(new Loot(l.getName(), val));
                                    }
                                    else if(compare < 0) {
                                            val = valueBefore - valueAfter;
                                            decay.add(new Item(l.getName(), val));
                                    }
                                    newItem = false;
                                    break;
                                    // TODO: Special Case when we loot an item that is also decaying?
                                    // TODO: Handle this by knowing the items that the user is using :)
                            }
                    }
                    if(newItem) {
                            loot.add(l);
                    }
            }
            Map<String, ArrayList<Item>> result = new HashMap();
            result.put("Loot", loot);
            result.put("Decay", decay);
            return result;
	}
	// List<String> lines = Files.readAllLines(file, this.charset);
	private ArrayList<Item> parseInventory(List<String> inventory){
            ArrayList<Item> items = new ArrayList<>();	
            for (int i = 26; i < inventory.size(); i++) {
                String line = inventory.get(i);
                String[] parts = line.split("\t");
                if(parts.length > 1){
                    String itemName = parts[InputIndex.Name.ordinal()];
                    String valueString = parts[InputIndex.Value.ordinal()];
                    double valueTT = Double.parseDouble(valueString);
                    int index = Utilities.getItemIndex(items, itemName);
                    if(index == -1) {
                            items.add(new Item(itemName, valueTT));
                    }
                    else {
                            items.get(index).addValue(valueTT);
                    }
                }
            }
            return items;
	}
}
