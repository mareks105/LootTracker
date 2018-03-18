package loottracker;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class MobData {
	private Map<Integer, Hunt> hunts;

	public MobData() {
                hunts = new HashMap<>();
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
        
        public Hunt getHunt(int ID){
            return hunts.get(ID);
        }
        
	public Map<DataKey, Double> getDataForHunts(MarkupHandler markupHandler) {
                Map<DataKey, Double> dataTable = new EnumMap<>(DataKey.class);
                hunts.forEach((Integer ID, Hunt hunt) -> {
                    hunt.getDataForHunt(markupHandler).forEach((DataKey k, Double v) -> {
                        if(k != DataKey.ReturnTTpercent && k != DataKey.ReturnWithMarkupPercent){
                            dataTable.merge(k, v, Double::sum);
                        }
                    });
                });
                dataTable.put(DataKey.ReturnTTpercent, 
                    Utilities.round(100 * dataTable.get(DataKey.TotalLootTT) / 
                    dataTable.get(DataKey.TotalCost),2));
                dataTable.put(DataKey.ReturnWithMarkupPercent, 
                    Utilities.round(100 * dataTable.get(DataKey.TotalLootWithMarkup) / 
                    dataTable.get(DataKey.TotalCost),2));
		return dataTable;
	}
}
