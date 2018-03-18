/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 *
 * @author mege9
 */
public class LootTracker {

    private Map<String, MobData> huntingData;
    private MarkupHandler markupHandler;
    // Unique integer ID for every hunt created since the beginning of the program.
    // TODO: Save this to the data file so new hunts get unique ids compared to those saved to disk
    private int huntsCreated;

    public LootTracker() {
        huntsCreated = 0;
        huntingData = new HashMap<>();
        markupHandler = new MarkupHandler(Paths.get("C:\\Users\\mege9\\Documents\\NetBeansProjects\\LootTracker\\data\\markup_info.txt"));
    }

    public MarkupHandler getMarkupHandler(){
        return this.markupHandler;
    }
    
    public int addHuntToGroup(String group, Hunt hunt) {
        huntsCreated++;
        int ID = huntsCreated;
        if (huntingData.containsKey(group)) {
            huntingData.get(group).addHunt(ID, hunt);
        } else {
            MobData data = new MobData();
            data.addHunt(ID, hunt);
            huntingData.put(group, data);
        }
        return ID;
    }
    
    public void updateHunt(String group, int ID, double ammo, Vector<Vector<String>> lootData, Vector<Vector<String>> equipmentData, String note){
        huntingData.get(group).getHunt(ID).updateHunt(ammo, lootData, equipmentData, note, this.markupHandler);
    }

    public Map<DataKey, Double> getStatsForGroup(String group) {
        return huntingData.get(group).getDataForHunts(markupHandler);
    }
    /**
     * @param args the command line arguments
     */
    /*public static void main(String[] args) {
        InventoryParser inventoryParser = new InventoryParser();
		Map<String, ArrayList<Item>> lootForHunt = inventoryParser.getInventoryChangesForHunt();
		
		MarkupHandler markupHandler = new MarkupHandler(
				Paths.get("C:\\Users\\mege9\\Documents\\MyProjects\\LootTracker\\data", "markup_info.txt"));
    }
     */
}
