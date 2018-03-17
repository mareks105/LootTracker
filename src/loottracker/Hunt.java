package loottracker;

import java.util.*;
import java.util.stream.DoubleStream;

public class Hunt {
	
	private final int huntID;
	private final ArrayList<Item> allLoot;
	private ArrayList<Item> allEquipment;
	private Map<DataKey, Double> dataTable;
	private Date endDate;
        
	public Hunt(int huntID) {
            this.endDate = null;
            this.allLoot = new ArrayList<>();
            this.allEquipment = new ArrayList<>();
            this.huntID = huntID;
            initDataTable();
	}
        
        private void initDataTable(){
            this.dataTable = new EnumMap<>(DataKey.class);
            for (DataKey key : DataKey.values()){
                this.dataTable.put(key, 0.0);
            }
        }
	
        public int getID(){
            return this.huntID;
        }
        
	public void addEquipment(Equipment equipment) {
		allEquipment.add(equipment);
	}
	
        public ArrayList<Item> getEquipment(){
            return allEquipment;
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
	
        public ArrayList<Item> getLoot(){
            return allLoot;
        }
        
	public boolean removeLoot(String name) {
            int index = Utilities.getItemIndex(allLoot, name);
            if(index != -1) {
                allLoot.remove(index);
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
            allLoot.forEach((item) -> {
                item.print();
            });
	}
	
	public void displayEquipment() {
            allEquipment.forEach((item) -> {
                item.print();
            });
	}
	
        public void addAmmo(double ammo){
            if(this.dataTable.containsKey(DataKey.Ammo)){
                this.dataTable.merge(DataKey.Ammo, ammo, Double::sum);
            }
            else{
                this.dataTable.put(DataKey.Ammo, ammo);
            }
            
        }
        
        public double getAmmo(){
            if(this.dataTable.containsKey(DataKey.Ammo)){
                return this.dataTable.get(DataKey.Ammo);
            }
            else{
                return 0;
            }
        }
        
    /**
     *  The idea: Call this function when the user says that the hunt ended.
     * Save the date, used later on to manage loot and markup
     * @param date: The current date when the hunt ended.
     */
    public void end(Date date) {
            // TODO: Verify that ALL data is ok for hunt
            this.endDate = date;
            computeDecayForEquipment();
            computeLootTT();
            computeTotalCost();
            computeReturnTT();
	}
        
        public Date getDate(){
            return this.endDate;
        }
        
        private void computeLootTT() {
            double totalLootTT = 0;
            totalLootTT = allLoot.stream()
                    .filter((item) -> (item instanceof Loot))
                    .map((item) -> (Loot) item)
                    .map((loot) -> loot.getValue())
                    .reduce(totalLootTT, Double::sum);
            this.dataTable.put(DataKey.TotalLootTT, totalLootTT);
        }
        
        private void computeTotalCost(){
            this.dataTable.put(DataKey.TotalCost,
                    DoubleStream.of(
                            this.dataTable.get(DataKey.TotalDecayWithMarkup),
                            this.dataTable.get(DataKey.Ammo)
                    ).sum());
        }
	
        private void computeReturnTT(){
            this.dataTable.put(DataKey.ReturnTT, 
                    this.dataTable.get(DataKey.TotalLootTT) - 
                            this.dataTable.get(DataKey.TotalCost));
            this.dataTable.put(DataKey.ReturnTTpercent, 
                    this.dataTable.get(DataKey.TotalLootTT) / 
                            this.dataTable.get(DataKey.TotalCost));
        }
        
        private void computeDecayForEquipment(){
            allEquipment.stream()
                    .filter((item) -> (item instanceof Equipment))
                    .map((item) -> (Equipment) item)
                    .forEachOrdered((e) -> {
                if(e instanceof Weapon){
                    this.dataTable.merge(DataKey.WeaponDecayTT, e.getDecayTT(), Double::sum);
                    this.dataTable.merge(DataKey.WeaponDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                }
                else if(e instanceof Amp){
                    this.dataTable.merge(DataKey.AmpDecayTT, e.getDecayTT(), Double::sum);
                    this.dataTable.merge(DataKey.AmpDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                }
                else if(e instanceof Armor){
                    this.dataTable.merge(DataKey.ArmorDecayTT, e.getDecayTT(), Double::sum);
                    this.dataTable.merge(DataKey.ArmorDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                }
                else if(e instanceof HealingTool){
                    this.dataTable.merge(DataKey.HealingDecayTT, e.getDecayTT(), Double::sum);
                    this.dataTable.merge(DataKey.HealingDecayWithMarkup, e.getDecayWithMarkup(), Double::sum);
                }
            });
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
                getReturnWithMarkup();
		return this.dataTable;
	}
        
        private void getReturnWithMarkup(){
            this.dataTable.put(DataKey.ReturnWithMarkup,
                    this.dataTable.get(DataKey.TotalLootWithMarkup) - 
                            this.dataTable.get(DataKey.TotalCost));
            this.dataTable.put(DataKey.ReturnWithMarkupPercent, 
                    this.dataTable.get(DataKey.TotalLootWithMarkup) / 
                            this.dataTable.get(DataKey.TotalCost));
        }
	
	private void getLootWithMarkup(MarkupHandler markupHandler) {
		
                this.dataTable.put(DataKey.TotalLootWithMarkup,
                        allLoot.stream()
                        .filter((item) -> (item instanceof Loot))
                        .map((item) -> (Loot) item)
                        .map((loot) -> {
                                    double markup;
                                    if(loot.isSold()){
                                        markup = loot.getMarkup();
                                    }
                                    else{
                                        markup = markupHandler.getMarkup(loot.getName());
                                    }
                                    return loot.getValue() * markup;
                                })
                        .mapToDouble((entry) -> (double) entry)
                        .sum()
                );
	}    
}
