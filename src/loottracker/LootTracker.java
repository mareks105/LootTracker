/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author mege9
 */
public class LootTracker {
    private Map<String, MobData> huntingData;
    private MarkupHandler markupHandler;
    private int huntsCreated;
    //private Map<EquipmentType, Map<String, Double>> equipmentData;
    private ArrayList<Equipment> weapons;
    private ArrayList<Equipment> amps;
    private ArrayList<Equipment> healingTools;
    private ArrayList<Equipment> armor;
    
    public LootTracker() {
        huntsCreated = 0;
        huntingData = new HashMap<>();    
        initEquipmentData();
        
        markupHandler = new MarkupHandler(Settings.markupFile);
    }
    
    private void initEquipmentData(){
        weapons = new ArrayList<>();
        amps = new ArrayList<>();
        healingTools = new ArrayList<>();
        armor = new ArrayList<>();
    }
    
    
    
    public void addMarkupHandler(MarkupHandler markupHandler){
        this.markupHandler = markupHandler;
    }
    
    public ArrayList<Equipment> getWeapons(){
        return weapons;
    }
    
    public ArrayList<Equipment> getAmps(){
        return amps;
    }
    
    public ArrayList<Equipment> getHealingTools(){
        return healingTools;
    }
    
    public ArrayList<Equipment> getArmors(){
        return armor;
    }
    
    public ArrayList<Equipment> getAllEquipment(){
        ArrayList<Equipment> result = new ArrayList<>();
        result.addAll(weapons);
        result.addAll(amps);
        result.addAll(healingTools);
        result.addAll(armor);
        return result;
    }
    
    public ArrayList<String> getAllNames(String type){
        switch(type){
            case "Weapon":
                return EquipmentUtilities.getAllNames(weapons);
            case "Amp":
                return EquipmentUtilities.getAllNames(amps);
            case "Healing":
                return EquipmentUtilities.getAllNames(healingTools);
            case "Armor":
                return EquipmentUtilities.getAllNames(armor);
            default:
                throw new RuntimeException("Invalid type for equipment!");     
        }
        
    }
    
    public void clearAllEquipment(){
        weapons.clear();
        amps.clear();
        healingTools.clear();
        armor.clear();
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
        if(!this.huntingData.get(group).removeHunt(ID)){
            System.out.println("Failed to remove hunt");
        }
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
    
    public Map<DataKey, Double> getStatsForAllGroups() {
        Map<DataKey, Double> allStats = Utilities.initDataTable();
        huntingData.values().forEach((MobData d) -> {
            d.getDataForHunts(markupHandler).forEach((DataKey k, Double v ) ->{
                if(k != DataKey.ReturnTTpercent && k != DataKey.ReturnWithMarkupPercent){
                    allStats.merge(k, Utilities.round(v, 2), Double::sum);
                }
            });
        });
        if(Double.compare(allStats.get(DataKey.TotalCost), 0) > 0){
            allStats.put(DataKey.ReturnTTpercent, 
                Utilities.round(100 * allStats.get(DataKey.TotalLootTT) / 
                allStats.get(DataKey.TotalCost),2));
            allStats.put(DataKey.ReturnWithMarkupPercent, 
                Utilities.round(100 * allStats.get(DataKey.TotalLootWithMarkup) / 
                allStats.get(DataKey.TotalCost),2));
        }
        return allStats;
    }
    
    public void saveToDisk(DateFormat df) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(new File(Settings.dataFile), JsonEncoding.UTF8);
        generator.writeStartObject();
        saveHuntingDataToJson(generator, this.huntingData, df);
        saveEquipmentDataToJson(generator, this.getAllEquipment());
        saveMarkupHandlerToJson(generator);
        generator.writeEndObject();
        generator.close();
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(new File(Settings.dataFile), Object.class);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(Settings.dataFile), json);
    }
    
    private static void saveHuntingDataToJson(JsonGenerator generator, 
            Map<String, MobData> huntingData, 
            DateFormat df) throws IOException {
        generator.writeObjectFieldStart("huntingData");
        generator.writeNumberField("nrGroups", huntingData.size());
        for (Map.Entry<String, MobData> entry : huntingData.entrySet()) {
            generator.writeObjectFieldStart(entry.getKey());
            MobData mobData = entry.getValue();
            mobData.saveToDisk(generator, df);
        }
        // Finished writing the huntingData to json
        generator.writeEndObject();
    }
    
    private static void saveEquipmentDataToJson(JsonGenerator generator, ArrayList<Equipment> equipmentData) throws IOException {
        generator.writeArrayFieldStart("allEquipment");
        for (Equipment e : equipmentData) {
            e.saveToDisk(generator);
        }
        generator.writeEndArray();    
    }
    
    private void saveMarkupHandlerToJson(JsonGenerator generator) throws IOException {
        generator.writeObjectFieldStart("markupHandler");
        generator.writeObjectFieldStart("markupTable");
        this.markupHandler.saveToDisk(generator);
        generator.writeEndObject();
        generator.writeEndObject();
    }
}

