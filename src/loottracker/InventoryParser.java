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
	
	private Path beforeFile = Paths.get("C:\\Users\\mege9\\Documents\\MyProjects\\LootTracker\\data", "inventoryBefore.txt");
	private Path afterFile = Paths.get("C:\\Users\\mege9\\Documents\\MyProjects\\LootTracker\\data", "inventoryAfter.txt");
	private Charset charset = Charset.forName("ISO-8859-1");
	
	public Map<String, ArrayList<Item>> getInventoryChangesForHunt(){
		ArrayList<Item> lootForHunt = new ArrayList<Item>();
		ArrayList<Item> itemsBeforeHunt = parseInventory(this.beforeFile);
		ArrayList<Item> itemsAfterHunt = parseInventory(this.afterFile);
		return getInventoryChanges(itemsBeforeHunt, itemsAfterHunt);
	}
	
	private Map<String, ArrayList<Item>> getInventoryChanges(ArrayList<Item> itemsBeforeHunt, ArrayList<Item> itemsAfterHunt){
		ArrayList<Item> loot = new ArrayList<Item>();
		ArrayList<Item> decay = new ArrayList<Item>();
		ArrayList<String> usedItems = new ArrayList<String>();
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
		System.out.println("Loot:" + loot.size());
		System.out.println("Decay:" + decay.size());
		Map<String, ArrayList<Item>> result = new HashMap();
		result.put("Loot", loot);
		result.put("Decay", decay);
		
//		System.out.println("Loot:");
//		for (Item j : loot) {
//			j.print();
//		}
//		System.out.println("Decay:");
//		for (Item j : decay) {
//			j.print();
//		}
		return result;
	}
	
	private ArrayList<Item> parseInventory(Path file){
		ArrayList<Item> items = new ArrayList<Item>();
		try{
			List<String> lines = Files.readAllLines(file, this.charset);
			for (String line : lines) {
				if(line != lines.get(0)) {
					String[] parts = line.split("\t");
					String itemName = parts[1];
					String valueString = parts[3].split(" ")[0];
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
		}
		catch (IOException e) {
			System.out.println("Inventory File not Found!");
		}
		return items;
	}
}
