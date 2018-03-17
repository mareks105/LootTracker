package loottracker;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class MobData {
	private ArrayList<Hunt> hunts;
	private String mobName;

	public MobData(String mobName) {
		this.mobName = mobName;
                hunts = new ArrayList<>();
	}

        public void addHunt(Hunt hunt){
            hunts.add(hunt);
        }
        
        public boolean removeHunt(int ID){
            int index = Utilities.getHuntIndex(hunts, ID);
            if(index != -1) {
                hunts.remove(index);
                return true;
            }
            else {
                return false;
            }
        }
        
	public Map<DataKey, Double> getDataForHunts(MarkupHandler markupHandler) {
                Map<DataKey, Double> dataTable = new EnumMap<>(DataKey.class);
                hunts.forEach((h) -> {
                    h.getDataForHunt(markupHandler).forEach((k, v) -> {
                        dataTable.merge(k, v, Double::sum);
                    });
                });
		return dataTable;
	}
}
