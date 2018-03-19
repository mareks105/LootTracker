package loottracker;

import java.util.*;
import java.util.stream.DoubleStream;

public class Hunt {

    private ArrayList<Loot> allLoot;
    private ArrayList<Equipment> allEquipment;
    private Map<DataKey, Double> dataTable;
    private Date endDate;
    private String note;
    
    public Hunt(double ammo, Vector<Vector<String>> lootData, Vector<Vector<String>> equipmentData, String note, MarkupHandler markupHandler){
        // Init Fields in Hunt
        this.endDate = null;
        initDataTable();
        updateHunt(ammo, lootData, equipmentData, note, markupHandler);
    }

    private void initDataTable(){
        this.dataTable = new EnumMap<>(DataKey.class);
        for (DataKey key : DataKey.values()){
            this.dataTable.put(key, 0.0);
        }
    }
    
    public void updateHunt(double ammo, Vector<Vector<String>> lootData, Vector<Vector<String>> equipmentData, String note, MarkupHandler markupHandler){
        this.allLoot = new ArrayList<>();
        this.allEquipment = new ArrayList<>();
        addLootFromData(lootData, markupHandler);
        addEquipmentFromData(equipmentData);
        this.dataTable.put(DataKey.Ammo, ammo);
        this.note = note;
        computeDecayForEquipment();
        computeLootTT();
        computeTotalCost();
        computeReturnTT();
    }

    private void addLootFromData(Vector<Vector<String>> lootData, MarkupHandler markupHandler){
        lootData.forEach((Vector<String> item) -> {
            String name = item.elementAt(0);
            double valueTT = Double.parseDouble(item.elementAt(1));
            this.allLoot.add(new Loot(name, valueTT));
        });
    }

    private void addEquipmentFromData(Vector<Vector<String>> equipmentData){
        equipmentData.forEach((Vector<String> item) -> {
            String name = item.elementAt(0);
            String type = item.elementAt(1);
            double startValue = Double.parseDouble(item.elementAt(2));
            double markup = Double.parseDouble(item.elementAt(3)) / 100.0;
            double endValue = Double.parseDouble(item.elementAt(4));
            if(null == type){
                throw new RuntimeException("Invalid type for equipment!");
            }
            else switch (type) {
                case "Weapon":
                    allEquipment.add(new Weapon(name, startValue, markup, endValue));
                    break;
                case "Amp":
                    allEquipment.add(new Amp(name, startValue, markup, endValue));
                    break;
                case "Healing":
                    allEquipment.add(new HealingTool(name, startValue, markup, endValue));
                    break;
                case "Armor":
                    allEquipment.add(new Armor(name, startValue, markup, endValue));
                    break;
                default:
                    throw new RuntimeException("Invalid type for equipment!");
            }
        });
    }

    public void addEquipment(Equipment equipment) {
            allEquipment.add(equipment);
    }

    public ArrayList<Equipment> getEquipment(){
        return allEquipment;
    }

    public void addLoot(Loot loot) {
            allLoot.add(loot);
    }

    public ArrayList<Loot> getLoot(){
        return allLoot;
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
            if(date == null){
                this.endDate = new Date();
            }
            else{
                this.endDate = date;
            }       
    }

    public Date getEndDate(){
        return this.endDate;
    }

    private void computeLootTT() {
        double totalLootTT = 0;
        totalLootTT = allLoot.stream()
                .filter((item) -> (item instanceof Loot))
                .map((item) -> (Loot) item)
                .map((loot) -> loot.getValue())
                .reduce(totalLootTT, Double::sum);
        this.dataTable.put(DataKey.TotalLootTT, Utilities.round(totalLootTT,2));
    }

    private void computeTotalCost(){
        this.dataTable.put(DataKey.TotalCost,
                Utilities.round(DoubleStream.of(
                        this.dataTable.get(DataKey.TotalDecayWithMarkup),
                        this.dataTable.get(DataKey.Ammo)
                ).sum(),2));
    }

    private void computeReturnTT(){
        this.dataTable.put(DataKey.ReturnTT, 
                Utilities.round(this.dataTable.get(DataKey.TotalLootTT) - 
                        this.dataTable.get(DataKey.TotalCost),2));
        this.dataTable.put(DataKey.ReturnTTpercent, 
                Utilities.round(100 * this.dataTable.get(DataKey.TotalLootTT) / 
                        this.dataTable.get(DataKey.TotalCost),2));
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
                Utilities.round(DoubleStream.of(
                        this.dataTable.get(DataKey.AmpDecayTT),
                        this.dataTable.get(DataKey.WeaponDecayTT),
                        this.dataTable.get(DataKey.ArmorDecayTT),
                        this.dataTable.get(DataKey.HealingDecayTT)
                ).sum(),2));
        this.dataTable.put(DataKey.TotalDecayWithMarkup,
                Utilities.round(DoubleStream.of(
                        this.dataTable.get(DataKey.AmpDecayWithMarkup),
                        this.dataTable.get(DataKey.WeaponDecayWithMarkup),
                        this.dataTable.get(DataKey.ArmorDecayWithMarkup),
                        this.dataTable.get(DataKey.HealingDecayWithMarkup)
                ).sum(),2));
    }

    public Map<DataKey, Double> getDataForHunt(MarkupHandler markupHandler){
            getLootWithMarkup(markupHandler);
            getReturnWithMarkup();
            return this.dataTable;
    }
    
    public String getNote(){
        return this.note;
    }
    
    private void getReturnWithMarkup(){
        this.dataTable.put(DataKey.ReturnWithMarkup,
                Utilities.round(this.dataTable.get(DataKey.TotalLootWithMarkup) - 
                        this.dataTable.get(DataKey.TotalCost),2));
        this.dataTable.put(DataKey.ReturnWithMarkupPercent, 
                Utilities.round(100 * this.dataTable.get(DataKey.TotalLootWithMarkup) / 
                        this.dataTable.get(DataKey.TotalCost),2));
    }

    private void getLootWithMarkup(MarkupHandler markupHandler) {

            this.dataTable.put(DataKey.TotalLootWithMarkup,
                    Utilities.round(allLoot.stream()
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
                    .sum(),2)
            );
            this.dataTable.put(DataKey.Markup,
                    Utilities.round(this.dataTable.get(DataKey.TotalLootWithMarkup) -
                            this.dataTable.get(DataKey.TotalLootTT),2));
    }    
}
