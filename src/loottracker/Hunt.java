package loottracker;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.DoubleStream;

public class Hunt {

    private ArrayList<Loot> allLoot;
    private ArrayList<Equipment> allEquipment;
    private Map<DataKey, Double> dataTable;
    private Date endDate;
    private String note;
    private String inventoryBefore;
    private String inventoryAfter;
    
    public Hunt(double ammo,
                double universalAmmo,
                Vector<Vector<String>> lootData,
                Vector<Vector<String>> equipmentData,
                String note,
                MarkupHandler markupHandler,
                String inventoryBefore,
                String inventoryAfter){
        // Init Fields in Hunt
        this.endDate = null;
        this.dataTable = Utilities.initDataTable();
        updateHunt(ammo, universalAmmo, lootData, equipmentData, note, markupHandler, inventoryBefore, inventoryAfter);
    }
    
    public Hunt(){
        this.endDate = null;
        this.dataTable = Utilities.initDataTable();
        allLoot = new ArrayList<>();
        allEquipment = new ArrayList<>();
    }
    
    
    
    public Map<DataKey, Double> getDataTable(){
        return this.dataTable;
    }
    
    public void computeDataForHunt(){
        computeDecayForEquipment();
        computeLootTT();
        computeTotalCost();
        computeReturnTT();
    }
    
    public void updateHunt(
            double ammo,
            double universalAmmo,
            Vector<Vector<String>> lootData,
            Vector<Vector<String>> equipmentData,
            String note,
            MarkupHandler markupHandler,
            String inventoryBefore,
            String inventoryAfter){
        this.allLoot = new ArrayList<>();
        this.allEquipment = new ArrayList<>();
        this.dataTable = Utilities.initDataTable();
        addLootFromData(lootData, markupHandler);
        EquipmentUtilities.updateEquipmentFromData(allEquipment, equipmentData);
        this.dataTable.put(DataKey.Ammo, ammo);
        this.dataTable.put(DataKey.UniversalAmmo, universalAmmo);
        this.note = note;
        this.inventoryBefore = inventoryBefore;
        this.inventoryAfter = inventoryAfter;
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
    
    public void addUniversalAmmo(double ammo){
        if(this.dataTable.containsKey(DataKey.UniversalAmmo)){
            this.dataTable.merge(DataKey.UniversalAmmo, ammo, Double::sum);
        }
        else{
            this.dataTable.put(DataKey.UniversalAmmo, ammo);
        }

    }
    
    public double getUniversalAmmo(){
        if(this.dataTable.containsKey(DataKey.UniversalAmmo)){
            return this.dataTable.get(DataKey.UniversalAmmo);
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
                        this.dataTable.get(DataKey.Ammo),
                        this.dataTable.get(DataKey.UniversalAmmo)
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
    
    public void setNote(String note){
        this.note = note;
    }
    
    private void getReturnWithMarkup(){
        double totalCost = this.dataTable.get(DataKey.TotalCost);
        if(totalCost > 0){
            this.dataTable.put(DataKey.ReturnWithMarkup,
                Utilities.round(this.dataTable.get(DataKey.TotalLootWithMarkup) - 
                        totalCost,2));
            this.dataTable.put(DataKey.ReturnWithMarkupPercent, 
                    Utilities.round(100 * this.dataTable.get(DataKey.TotalLootWithMarkup) / 
                            totalCost,2));
        }
        else{
            this.dataTable.put(DataKey.ReturnWithMarkup, 0.0);
            this.dataTable.put(DataKey.ReturnWithMarkupPercent, 0.0);
        }        
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
    
    public String getInventoryBefore(){
        return this.inventoryBefore;
    }
    
    public void setInventoryBefore(String inventoryBefore){
        this.inventoryBefore = inventoryBefore;
    }
    
    public String getInventoryAfter(){
        return this.inventoryAfter;
    }
    
    public void setInventoryAfter(String inventoryAfter){
        this.inventoryAfter = inventoryAfter;
    }
    
    public void saveToDisk(JsonGenerator generator, DateFormat df) throws IOException {
        generator.writeArrayFieldStart("allLoot");
        for (Loot loot : this.allLoot) {
            loot.saveToDisk(generator);
        }
        generator.writeEndArray();
        
        generator.writeArrayFieldStart("allEquipment");
        for (Equipment e : this.allEquipment) {
            e.saveToDisk(generator);
        }
        generator.writeEndArray();

        generator.writeObjectFieldStart("dataTable");
        for (Map.Entry<DataKey, Double> dataEntry : this.dataTable.entrySet()) {
            generator.writeNumberField(dataEntry.getKey().toString(), dataEntry.getValue());
        }
        generator.writeEndObject();
        if(this.endDate == null){
            generator.writeStringField("endDate", "null");
        }
        else{
            generator.writeStringField("endDate", df.format(this.endDate));
        }

        generator.writeStringField("note", this.note);
        generator.writeStringField("inventoryBefore", this.inventoryBefore);
        generator.writeStringField("inventoryAfter", this.inventoryAfter);
    }
    
    public void parseFromJson(JsonParser parser, DateFormat df) throws IOException, InvalidKeyException, ParseException {
        parser.nextToken();
        parser.nextToken();
        // parse Loot
        while(parser.nextToken() != JsonToken.END_ARRAY){
            addLootFromJson(parser);
        }
        parser.nextToken();
        parser.nextToken();
        // parse Equipment
        while(parser.nextToken() != JsonToken.END_ARRAY){
            addEquipmentFromJson(parser);
        }
        parser.nextToken();

        parser.nextToken();
        updateDataTableFromJson(parser);
        parser.nextToken();
        parser.nextToken();
        parser.nextToken();
        String date = parser.getValueAsString();
        if( ! date.equals("null")){
            Date endDate = df.parse(parser.getValueAsString());
            if(Settings.DEBUG){
                System.out.println(endDate);
            }
            this.end(endDate);
        }
        
        parser.nextToken();
        parser.nextToken();
        String note = parser.getValueAsString();
        this.setNote(note);
        parser.nextToken();
        parser.nextToken();
        if(Settings.DEBUG){
            System.out.println(parser.getCurrentToken());
            System.out.println(parser.getCurrentName());
        }
        String inventoryBefore = parser.getValueAsString();
        this.setInventoryBefore(inventoryBefore);
        parser.nextToken();
        parser.nextToken();
        if(Settings.DEBUG){
            System.out.println(parser.getCurrentToken());
            System.out.println(parser.getCurrentName());
        }
        String inventoryAfter = parser.getValueAsString();
        this.setInventoryAfter(inventoryAfter);
    }
    
    private void addLootFromJson(JsonParser parser) throws IOException {
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
        this.addLoot(new Loot(name, valueTT));
        parser.nextToken();
    }
    
    private void addEquipmentFromJson(JsonParser parser) throws IOException {
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
                this.addEquipment(new Weapon(name, valueTT, markup, endValue));
                break;
            case "Amp":
                this.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            case "Healing":
                this.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            case "Armor":
                this.addEquipment(new Amp(name, valueTT, markup, endValue));
                break;
            default:
                throw new InvalidFormatException("Unknown equipment type found: " + type, null, null);
        }
        parser.nextToken();
    }
    
    private void updateDataTableFromJson(JsonParser parser) throws IOException, InvalidKeyException {
        if(Settings.DEBUG){
            System.out.println(parser.getCurrentName());
            System.out.println(parser.getCurrentToken());
        }
        for(int i = 0; i < 20; i++){
            parser.nextToken();
            parser.nextToken();
            if(Settings.DEBUG){
                System.out.println(parser.getCurrentToken());
                System.out.println(parser.getCurrentName());
            }
            this.dataTable.put(Utilities.getDataKey(parser.getCurrentName()), parser.getValueAsDouble());
        }
    }
}
