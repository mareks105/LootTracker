package loottracker;

import java.util.*;
import java.util.stream.DoubleStream;

public class Hunt {
	
	private final int huntID;
	private final ArrayList<Item> allLoot;
	private ArrayList<Item> allEquipment;
	private Map<DataKey, Double> dataTable;
	
	public Hunt(int huntID) {
            this.allLoot = new ArrayList<>();
            this.allEquipment = new ArrayList<>();
            this.dataTable = initDataTable();
            this.huntID = huntID;
	}
	
	private Map<DataKey, Double> initDataTable() {
		Map<DataKey, Double> table = new HashMap<DataKey, Double>();
		table.put(DataKey.Ammo, 0.0);
		table.put(DataKey.AmpDecayTT, 0.0);
                table.put(DataKey.AmpDecayWithMarkup, 0.0);
		table.put(DataKey.WeaponDecayTT, 0.0);
                table.put(DataKey.WeaponDecayWithMarkup, 0.0);
                table.put(DataKey.HealingDecayTT, 0.0);
                table.put(DataKey.HealingDecayWithMarkup, 0.0);
                table.put(DataKey.ArmorDecayTT, 0.0);
                table.put(DataKey.ArmorDecayWithMarkup, 0.0);
                table.put(DataKey.TotalDecayTT, 0.0);
                table.put(DataKey.TotalDecayWithMarkup, 0.0);
		table.put(DataKey.TotalCostTT, 0.0);
                table.put(DataKey.TotalCostWithMarkup, 0.0);
		table.put(DataKey.TotalLootTT, 0.0);
		table.put(DataKey.TotalLootWithMarkup, 0.0);
		table.put(DataKey.ReturnTT, 0.0);
		table.put(DataKey.ReturnWithMarkup, 0.0);
		return table;
	}
	
	public void addEquipment(Equipment equipment) {
		allEquipment.add(equipment);
	}
	
	public boolean removeEquipment(String name){
		int index = Utilities.getItemIndex(allEquipment, name);
		if(index != -1) {
			allEquipment.remove(index);
			return true;
		}
		else {
			return false;
		}
	}
	
	public void addLoot(Loot loot) {
		allLoot.add(loot);
	}
	
	public boolean removeLoot(String name) {
		int index = Utilities.getItemIndex(allEquipment, name);
		if(index != -1) {
			allEquipment.remove(index);
			return true;
		}
		else {
			return false;
		}
	}
	
        public void addData(DataKey key, double value){
            this.dataTable.put(key, value);
        }
        
	public void displayLoot() {
		for(Item item : allLoot) {
			item.print();
		}
	}
	
	public void displayEquipment() {
		for(Item item : allEquipment) {
			item.print();
		}
	}
	
	public void saveHunt() {
		saveDecayForEquipment();
                // TODO: Save the Loot from somewhere
                // TODO: Save Ammo
                computeTotalCost();
                computeReturnTT();
	}
        
        public void computeTotalCost(){
            this.dataTable.put(DataKey.TotalCostTT,
                    DoubleStream.of(
                            this.dataTable.get(DataKey.TotalDecayTT),
                            this.dataTable.get(DataKey.Ammo)
                    ).sum());
            this.dataTable.put(DataKey.TotalCostWithMarkup,
                    DoubleStream.of(
                            this.dataTable.get(DataKey.TotalDecayWithMarkup),
                            this.dataTable.get(DataKey.Ammo)
                    ).sum());
        }
	
        public void computeReturnTT(){
            this.dataTable.put(DataKey.ReturnTT, 
                    this.dataTable.get(DataKey.TotalLootTT) - 
                            this.dataTable.get(DataKey.TotalCost));
            this.dataTable.put(DataKey.ReturnTTpercent, 
                    this.dataTable.get(DataKey.ReturnTT) / 
                            this.dataTable.get(DataKey.TotalCostWithMarkup));
        }
        
        public void saveDecayForEquipment(){
            for(Item item : allEquipment){
                if(item instanceof Equipment){
                    Equipment e = (Equipment) item;
                    if(item instanceof Weapon){
                        this.dataTable.merge(DataKey.WeaponDecayTT, e.getDecayTT(), Double::sum);
                        this.dataTable.merge(DataKey.WeaponDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                    }
                    else if(item instanceof Amp){
                        this.dataTable.merge(DataKey.AmpDecayTT, e.getDecayTT(), Double::sum);
                        this.dataTable.merge(DataKey.AmpDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                    }
                    else if(item instanceof Armor){
                        this.dataTable.merge(DataKey.ArmorDecayTT, e.getDecayTT(), Double::sum);
                        this.dataTable.merge(DataKey.ArmorDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                    }
                    else if(item instanceof HealingTool){
                        this.dataTable.merge(DataKey.HealingDecayTT, e.getDecayTT(), Double::sum);
                        this.dataTable.merge(DataKey.HealingDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                    }
                }
            }
            this.dataTable.put(DataKey.TotalDecayTT,
                    DoubleStream.of(
                            this.dataTable.get(DataKey.AmpDecayTT),
                            this.dataTable.get(DataKey.WeaponDecayTT),
                            this.dataTable.get(DataKey.ArmorDecayTT),
                            this.dataTable.get(DataKey.HealingDecayTT)
                    ).sum());
            this.dataTable.put(DataKey.TotalDecayWithMarkup,
                    DoubleStream.of(
                            this.dataTable.get(DataKey.AmpDecayWithMarkup),
                            this.dataTable.get(DataKey.WeaponDecayWithMarkup),
                            this.dataTable.get(DataKey.ArmorDecayWithMarkup),
                            this.dataTable.get(DataKey.HealingDecayWithMarkup)
                    ).sum());
        }
        
	public Map<DataKey, Double> getDataForHunt(MarkupHandler markupHandler){
		getLootWithMarkup(markupHandler);
                getReturnWithMarkup(markupHandler);
		return this.dataTable;
	}
        
        public void getReturnWithMarkup(){
            this.dataTable.put(DataKey.ReturnWithMarkup,
                    this.dataTable.get(DataKey.LootWithMarkup) - 
                            this.dataTable.get(DataKey.TotalCostWithMarkup));
        }
	
	public void getLootWithMarkup(MarkupHandler markupHandler) {
		double totalLootWithMarkup = 0;
		for (Item item : allLoot) {
			double markup;
                        if(item instanceof Loot){
                            Loot loot = (Loot) item;
                            if(! loot.isSold()) {
				markup = markupHandler.getMarkup(loot.getName());
                            }
                            else {
                                    markup = loot.getMarkup();
                            }
                            totalLootWithMarkup += loot.getValue() * markup;
                        }
		}
		this.dataTable.put(DataKey.TotalLootWithMarkup, totalLootWithMarkup);
	}

	public double getLootTT() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getAmmo() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getHealingDecay() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getWeaponDecay() {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getArmorCost() {
		// TODO Auto-generated method stub
		return 0;
	}
}
