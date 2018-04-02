/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

/**
 *
 * @author mege9
 */
public class LootTracker {
    private Map<String, MobData> huntingData;
    private MarkupHandler markupHandler;
    private int huntsCreated;

    public LootTracker() {
        huntsCreated = 0;
        huntingData = new HashMap<>();
        //markupHandler = new MarkupHandler(Settings.markupFile);
        
    }
    
    public void addMarkupHandler(MarkupHandler markupHandler){
        this.markupHandler = markupHandler;
    }
    
    public int getHuntsCreated(){
        return huntsCreated;
    }
    
    public Set<String> getGroups(){
        return huntingData.keySet();
    }

    public MarkupHandler getMarkupHandler(){
        return this.markupHandler;
    }
    
    public Map<String, MobData> getHuntingData(){
        return this.huntingData;
    }
    
    public int addHuntToGroup(String group, Hunt hunt){
        huntsCreated++;
        int ID = huntsCreated;
        return this.addHuntToGroup(group, hunt, ID);
    }
    
    public int addHuntToGroup(String group, Hunt hunt, int ID) {
        if (huntingData.containsKey(group)) {
            huntingData.get(group).addHunt(ID, hunt);
        } else {
            MobData data = new MobData();
            data.addHunt(ID, hunt);
            huntingData.put(group, data);
        }
        return ID;
    }
    
    public void changeGroupForHunt(String newGroup, String oldGroup, int ID){
        Hunt hunt = this.getHunt(oldGroup, ID);
        this.removeHunt(oldGroup, ID);
        this.addHuntToGroup(newGroup, hunt, ID);
        /*if(huntingData.get(oldGroup).getHunts().size() == 0){
            huntingData.remove(oldGroup);
        }*/
    }
    
    public MobData getMobData(String group){
        return huntingData.get(group);
    }
    
    public ArrayList<String> getReportedLootForGroup(String group){
        if(huntingData.containsKey(group)){
            return huntingData.get(group).getReportedLootForGroup();
        }
        else{
            return new ArrayList<String>();
        }
    }
    
    public void addLootForGroup(String group, ArrayList<Loot> loot){
        this.huntingData.get(group).addLootForGroup(loot);
    }
    
    public Hunt getHunt(String group, int ID){
        return huntingData.get(group).getHunt(ID);
    }
    
    public void removeHunt(String group, int ID){
        System.out.println("Hunts: " + huntingData.get(group).getHunts().size());
        if(!this.huntingData.get(group).removeHunt(ID)){
            System.out.println("Failed to remove hunt");
        }
        System.out.println("Hunts After: " + huntingData.get(group).getHunts().size());
        if(huntingData.get(group).getHunts().size() == 0){
            huntingData.remove(group);
        }
    }
    
    public Map<DataKey, Double> getDataForHunt(String group, int ID){
        return huntingData.get(group).getHunt(ID).getDataForHunt(markupHandler);
    }
    
    public void updateHunt(String group, int ID, double ammo, double universalAmmo, Vector<Vector<String>> lootData, Vector<Vector<String>> equipmentData, String note){
        huntingData.get(group).getHunt(ID).updateHunt(ammo, universalAmmo, lootData, equipmentData, note, this.markupHandler);
    }
    
    
    public void endHunt(String group, int ID){
        huntingData.get(group).getHunt(ID).end(new Date());;
    }
    
    public Map<DataKey, Double> getStatsForGroup(String group) {
        return huntingData.get(group).getDataForHunts(markupHandler);
    }
}

