package loottracker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MobData {
	private ArrayList<Hunt> hunts;
	private String mobName;

	public MobData(String mobName) {
		this.mobName = mobName;
	}

	public Map<String, Double> getDataForHunts(MarkupHandler markupHandler) {
		
		double totalArmorCost = 0, totalAmpCost = 0, totalWeaponDecay = 0, totalHealingDecay = 0, totalAmmo = 0,
				totalCosts = 0, totalLootTT = 0, totalLootMarkup = 0;
		for (Hunt hunt : hunts) {
			Map<DataKey, Double> dataForHunt = hunt.getDataForHunt(markupHandler);
		}
		totalCosts = totalArmorCost + totalAmpCost + totalWeaponDecay + totalHealingDecay + totalAmmo;
		Map<String, Double> data = new HashMap();
		data.put("totalArmorCost", totalArmorCost);
		data.put("totalAmpCost", totalAmpCost);
		data.put("totalWeaponDecay", totalWeaponDecay);
		data.put("totalHealingDecay", totalHealingDecay);
		data.put("totalAmmo", totalAmmo);
		data.put("totalCosts", totalCosts);
		data.put("totalLootTT", totalLootTT);
		data.put("totalLootMarkup", totalLootMarkup);
		data.put("returnPEDTT", totalLootTT - totalCosts);
		data.put("returnPEDMarkup", totalLootMarkup - totalCosts);
		data.put("return%TT", totalLootTT / totalCosts);
		data.put("return%Markup", totalLootMarkup / totalCosts);
		return data;
	}
}
