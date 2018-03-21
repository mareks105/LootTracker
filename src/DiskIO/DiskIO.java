/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DiskIO;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import loottracker.*;

/**
 *
 * @author mege9
 */
public class DiskIO {

    public class InvalidJsonException extends Exception  {
        public InvalidJsonException(String message){
            super(message);
        }
    }
    public static void saveLootTracker(LootTracker lootTracker, DateFormat df) throws IOException {
        JsonFactory factory = new JsonFactory();
        JsonGenerator generator = factory.createGenerator(new File(Settings.dataFile), JsonEncoding.UTF8);
        generator.writeStartObject();
        saveHuntingDataToJson(generator, lootTracker, df);
        saveMarkupHandlerToJson(generator, lootTracker.getMarkupHandler());
        generator.writeEndObject();
        generator.close();
        ObjectMapper mapper = new ObjectMapper();
        Object json = mapper.readValue(new File(Settings.dataFile), Object.class);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(Settings.dataFile), json);
    }

    private static void saveHuntingDataToJson(JsonGenerator generator, 
            LootTracker lootTracker, 
            DateFormat df) throws IOException {
        Map<String, MobData> huntingData = lootTracker.getHuntingData();
        int huntsCreated = lootTracker.getHuntsCreated();
        generator.writeObjectFieldStart("huntingData");
        generator.writeNumberField("nrGroups", huntingData.size());
        for (Map.Entry<String, MobData> entry : huntingData.entrySet()) {
            generator.writeObjectFieldStart(entry.getKey());
            generator.writeArrayFieldStart("loot");
            for(String s : entry.getValue().getReportedLootForGroup()){
                generator.writeString(s);
            }
            generator.writeEndArray();
            generator.writeNumberField("nrHunts", entry.getValue().getHunts().size());
            generator.writeArrayFieldStart("hunts");
            Map<Integer, Hunt> hunts = entry.getValue().getHunts();
            for (Map.Entry<Integer, Hunt> huntEntry : hunts.entrySet()) {
                generator.writeStartObject();
                generator.writeNumberField("huntID", huntsCreated);
                // Write Data for Hunt
                Hunt hunt = huntEntry.getValue();
                generator.writeArrayFieldStart("allLoot");
                for (Loot loot : hunt.getLoot()) {
                    generator.writeStartObject();
                    generator.writeStringField("name", loot.getName());
                    generator.writeNumberField("valueTT", loot.getValue());
                    generator.writeEndObject();
                }
                generator.writeEndArray();
                generator.writeArrayFieldStart("allEquipment");
                for (Equipment e : hunt.getEquipment()) {
                    generator.writeStartObject();
                    generator.writeStringField("name", e.getName());
                    generator.writeStringField("type", e.getType().toString());
                    generator.writeNumberField("valueTT", e.getValue());
                    generator.writeNumberField("markup", e.getMarkup());
                    generator.writeNumberField("endValue", e.getEndValue());
                    generator.writeEndObject();
                }
                generator.writeEndArray();

                generator.writeObjectFieldStart("dataTable");
                for (Map.Entry<DataKey, Double> dataEntry : hunt.getDataTable().entrySet()) {
                    generator.writeNumberField(dataEntry.getKey().toString(), dataEntry.getValue());
                }
                generator.writeEndObject();
                if(hunt.getEndDate() == null){
                    generator.writeStringField("endDate", "null");
                }
                else{
                    generator.writeStringField("endDate", df.format(hunt.getEndDate()));
                }
                
                generator.writeStringField("note", hunt.getNote());
                // End Hunt Data
                generator.writeEndObject();
            }
            // End of all MobData Hunts
            generator.writeEndArray();
            // End of MobData object
            generator.writeEndObject();
        }
        // Finished writing the huntingData to json
        generator.writeEndObject();
    }

    private static void saveMarkupHandlerToJson(JsonGenerator generator, MarkupHandler markupHandler) throws IOException {
        generator.writeObjectFieldStart("markupHandler");
        generator.writeObjectFieldStart("markupTable");
        for (Map.Entry<String, Double> entry : markupHandler.getMarkupTable().entrySet()) {
            generator.writeNumberField(entry.getKey(), entry.getValue());
        }
        generator.writeEndObject();
        generator.writeEndObject();
    }
    
    public static LootTracker loadDataFromFile(DateFormat df) throws IOException, InvalidFormatException, ParseException, InvalidKeyException{
        LootTracker lootTracker = new LootTracker();
        JsonFactory factory = new JsonFactory();
        JsonParser parser = factory.createJsonParser(new File(Settings.dataFile));
        parseJson(parser, lootTracker, df);
        return lootTracker;
    }
    
    private static void parseJson(JsonParser parser, LootTracker lootTracker, DateFormat df) throws InvalidFormatException, IOException, ParseException, InvalidKeyException{
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        parseHuntingData(parser, lootTracker, df);
        parser.nextToken();
        parser.nextToken();
        parseMarkupData(parser, lootTracker);        
    }
    
    private static void parseHuntingData(JsonParser parser, LootTracker lootTracker, DateFormat df) throws IOException, ParseException, InvalidKeyException{
        parser.nextToken();
        parser.nextToken();
        int nrGroups = parser.getValueAsInt();
        if(Settings.DEBUG){
            System.out.println("groups: " + nrGroups);
        }
        for(int i = 0; i < nrGroups; i++){
            parser.nextToken();
            parser.nextToken();
            parseHuntingGroup(parser, lootTracker, df);
        }
    }
    
    private static void parseHuntingGroup(JsonParser parser, LootTracker lootTracker, DateFormat df) throws IOException, ParseException, InvalidKeyException{
        // Parse a group
        MobData data = new MobData();
        String group = parser.getCurrentName();
        if(Settings.DEBUG){
            System.out.println(group);
        }
        parser.nextToken();
        parser.nextToken();
        ArrayList<String> lootForGroup = new ArrayList<>();
        while(parser.nextToken() != JsonToken.END_ARRAY){
            lootForGroup.add(parser.getValueAsString());
        }
        data.setReportedLootForGroup(lootForGroup);
        parser.nextToken();
        parser.nextToken();
        
        int nrHunts = parser.getValueAsInt();
        if(Settings.DEBUG){
            System.out.println(parser.getCurrentToken());
            System.out.println("nrHunts: " + nrHunts);
        }
        lootTracker.getHuntingData().put(group, data);
        parser.nextToken();
        // Parse all hunts for group
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        
        for(int j = 0; j < nrHunts; j++){
            parser.nextToken();
            parseHunt(parser, lootTracker, group, df);
        }
    }
    
    private static void parseHunt(JsonParser parser, LootTracker lootTracker, String group, DateFormat df) throws IOException, InvalidKeyException, ParseException{
        int runID = parser.getValueAsInt();
        if(Settings.DEBUG){
            System.out.println("runID: " + runID);
        }
        Hunt hunt = new Hunt();
        parser.nextToken();
        
        parser.nextToken();
        // parse Loot
        while(parser.nextToken() != JsonToken.END_ARRAY){
            parseLoot(parser, hunt);
        }
        parser.nextToken();
        
        parser.nextToken();
        // parse Equipment
        while(parser.nextToken() != JsonToken.END_ARRAY){
            parseEquipment(parser, hunt);
        }
        parser.nextToken();

        parser.nextToken();
        parseDataTable(parser, hunt);
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        String date = parser.getValueAsString();
        if( ! date.equals("null")){
            Date endDate = df.parse(parser.getValueAsString());
            if(Settings.DEBUG){
                System.out.println(endDate);
            }
            hunt.end(endDate);
        }
        
        parser.nextToken();
        parser.nextToken();
        String note = parser.getValueAsString();
        hunt.setNote(note);
        lootTracker.addHuntToGroup(group, hunt);
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        //System.out.println(parser.getCurrentToken());
            
    }
    
    private static void parseDataTable(JsonParser parser, Hunt hunt) throws IOException, InvalidKeyException{
        Map<DataKey, Double> dataTable = hunt.getDataTable();
        //System.out.println(parser.getCurrentName());
        //System.out.println(parser.getCurrentToken());
        for(int i = 0; i < 20; i++){
            parser.nextToken();
            parser.nextToken();
            //System.out.println(parser.getCurrentToken());
            //System.out.println(parser.getCurrentName());
            dataTable.put(Utilities.getDataKey(parser.getCurrentName()), parser.getValueAsDouble());
        }
        
    }
    
    private static void parseEquipment(JsonParser parser, Hunt hunt) throws IOException{
        parser.nextToken();
        parser.nextToken();
        String name = parser.getValueAsString();
        if(Settings.DEBUG){
            System.out.println("name: " + name);
        }
        parser.nextToken();
        parser.nextToken();
        
        String type = parser.getValueAsString();
        if(Settings.DEBUG){
            System.out.println("type: " + type);
        }
        parser.nextToken();
        parser.nextToken();
        double valueTT = parser.getValueAsDouble();
        if(Settings.DEBUG){
            System.out.println("valueTT: "  + valueTT);
        }
        parser.nextToken();
        parser.nextToken();
        double markup = parser.getValueAsDouble();
        if(Settings.DEBUG){
            System.out.println("markup: "  + markup);
        }
        parser.nextToken();
        parser.nextToken();
        double endValue = parser.getValueAsDouble();
        if(Settings.DEBUG){
            System.out.println("endValue: "  + endValue);
        }
        switch(type){
            case "Weapon":
                hunt.addEquipment(new Weapon(name, valueTT, markup, endValue));
                break;
            case "Amp":
                hunt.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            case "HealingTool":
                hunt.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            case "Armor":
                hunt.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            default:
                throw new InvalidFormatException("Unknown equipment type found: " + type, null, null);
        }
        parser.nextToken();
    }
    
    private static void parseLoot(JsonParser parser, Hunt hunt) throws IOException{
        parser.nextToken();
        parser.nextToken();
        String name = parser.getValueAsString();
        if(Settings.DEBUG){
            System.out.println("name: " + name);
        }
        parser.nextToken();
        parser.nextToken();
        double valueTT = parser.getValueAsDouble();
        if(Settings.DEBUG){
            System.out.println("value: " + valueTT);
        }
        hunt.addLoot(new Loot(name, valueTT));
        parser.nextToken();
    }
    
    private static void parseMarkupData(JsonParser parser, LootTracker lootTracker) throws IOException{
        Map<String, Double> markupTable = new HashMap<>();
        parser.nextToken();
        while(parser.nextToken() != JsonToken.END_OBJECT){
            parser.nextToken();
            String name = parser.getCurrentName();
            double markup = parser.getValueAsDouble();
            markupTable.put(name, markup);
            
        }
        lootTracker.addMarkupHandler(new MarkupHandler(markupTable));
    }
}