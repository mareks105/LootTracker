package loottracker;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.ArrayList;

public class Utilities {
	
	public static enum DataKey {
		HealingDecay,
		Ammo,
		AmpDecay,
		WeaponDecay,
		TotalCost,
		TotalLootTT,
		TotalLootWithMarkup,
		ReturnTT,
		ReturnWithMarkup
	}
	
	public static int getItemIndex(ArrayList<Item> allItems, String name) {
		for(int i = 0; i < allItems.size(); ++i) {
			if(allItems.get(i).getName().equals(name)) {
				return i;
			}
		}
		return -1;
	}
	
	public static String toPercentage(double d) {
		return String.format("%1$.2f", d * 100) + "%";
	}
}
