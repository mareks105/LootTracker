package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MobData {
	private Map<Integer, Hunt> hunts;
        private ArrayList<String> reportedLootForMob = new ArrayList<>();
	public MobData() {
                hunts = new HashMap<>();
	}

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
                generator.writeNumberField("huntID", huntEntry.getKey());
                // Write Data for Hunt
                Hunt hunt = huntEntry.getValue();
                hunt.saveToDisk(generator, df);
            }
            // End of all MobData Hunts
            generator.writeEndArray();
            // End of MobData object
            generator.writeEndObject();
        }
}
