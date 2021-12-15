package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MobData {
    private Map<Integer, Hunt> hunts;
    private ArrayList<String> reportedLootForMob = new ArrayList<>();
    public MobData() {
        hunts = new HashMap<>();
    }

    /**
     * Remember any new loot for this group of hunts, i.e. for a
     * specific mob
     * @param loot
     */
    public void addLootForGroup(ArrayList<Loot> loot){
        loot.forEach((Loot l) -> {
            if(! reportedLootForMob.contains(l.getName())){
                reportedLootForMob.add(l.getName());
            }
        });
    }
        
    public ArrayList<String> getReportedLootForGroup(){
        return this.reportedLootForMob;
    }

    public void setReportedLootForGroup(ArrayList<String> lootNames){
        this.reportedLootForMob = lootNames;
    }

    public void addHunt(int ID, Hunt hunt){
        hunts.put(ID, hunt);
    }

    public boolean removeHunt(int ID){
        if(hunts.containsKey(ID)){
            hunts.remove(ID);
            return true;
        }
        else{
            return false;
        }
    }

    public Map<Integer, Hunt> getHunts(){
        return hunts;
    }

    public Hunt getHunt(int ID){
        return hunts.get(ID);
    }

    public Map<DataKey, Double> getDataForHunts(MarkupHandler markupHandler) {
        Map<DataKey, Double> dataTable = Utilities.initDataTable();
        hunts.forEach((Integer ID, Hunt hunt) -> {
            if(hunt.getEndDate() != null){
                hunt.getDataForHunt(markupHandler).forEach((DataKey k, Double v) -> {
                    if(k != DataKey.ReturnTTpercent && k != DataKey.ReturnWithMarkupPercent){
                        dataTable.merge(k, Utilities.round(v, 2), Double::sum);
                    }
                });
            }
        });
        if(Double.compare(dataTable.get(DataKey.TotalCost), 0) > 0){
            dataTable.put(DataKey.ReturnTTpercent, 
            Utilities.round(100 * dataTable.get(DataKey.TotalLootTT) / 
            dataTable.get(DataKey.TotalCost), 2));
        dataTable.put(DataKey.ReturnWithMarkupPercent, 
            Utilities.round(100 * dataTable.get(DataKey.TotalLootWithMarkup) / 
            dataTable.get(DataKey.TotalCost), 2));
        dataTable.put(DataKey.MarkupScore,
                dataTable.get(DataKey.Markup) / dataTable.get(DataKey.TotalCost));
        }
        return dataTable;
    }

    public void saveToDisk(JsonGenerator generator, DateFormat df) throws IOException {
        generator.writeArrayFieldStart("loot");
        for(String s : this.reportedLootForMob){
            generator.writeString(s);
        }
        generator.writeEndArray();
        generator.writeNumberField("nrHunts", this.hunts.size());
        generator.writeArrayFieldStart("hunts");
        for (Map.Entry<Integer, Hunt> huntEntry : this.hunts.entrySet()) {
            generator.writeStartObject();
            // Write ID of Hunt
            generator.writeNumberField("huntID", huntEntry.getKey());
            // Write Data for Hunt
            Hunt hunt = huntEntry.getValue();
            hunt.saveToDisk(generator, df);
            // End Hunt Data
            generator.writeEndObject();
        }
        // End of all MobData Hunts
        generator.writeEndArray();
    }

    public void loadFromDisk(JsonParser parser, DateFormat df, int huntsCreated) throws IOException, InvalidKeyException, ParseException {
        parser.nextToken();
        parser.nextToken();
        ArrayList<String> lootForGroup = new ArrayList<>();
        while(parser.nextToken() != JsonToken.END_ARRAY){
            lootForGroup.add(parser.getValueAsString());
        }
        this.setReportedLootForGroup(lootForGroup);
        parser.nextToken();
        parser.nextToken();

        int nrHunts = parser.getValueAsInt();
        if(Settings.DEBUG){
            System.out.println(parser.getCurrentToken());
            System.out.println("nrHunts: " + nrHunts);
        }
        parser.nextToken();
        // Parse all hunts for MobData object
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();

        for(int j = huntsCreated; j < huntsCreated + nrHunts; j++){
            parser.nextToken();
            Hunt hunt = new Hunt();
            hunt.parseFromJson(parser, df);
            this.addHunt(j, hunt);
            parser.nextToken();
            parser.nextToken();
            parser.nextToken();
        }
    }
}
