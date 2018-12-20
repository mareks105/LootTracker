/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author mege9
 */
public class EquipmentUtilities {
    
    public static void updateEquipmentFromData(ArrayList<Equipment> allEquipment, Vector<Vector<String>> equipmentData){
        allEquipment.clear();
        equipmentData.forEach((Vector<String> item) -> {
            String name = item.elementAt(0);
            String type = item.elementAt(1);
            double startValue = Double.parseDouble(item.elementAt(2));
            double endValue = Double.parseDouble(item.elementAt(3));
            double markup = Double.parseDouble(item.elementAt(4)) / 100.0;
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
    
    public static void updateEquipmentSettingsFromData(LootTracker lootTracker, Vector<Vector<String>> equipmentData){
        lootTracker.clearAllEquipment();
        equipmentData.forEach((Vector<String> item) -> {
            String name = item.elementAt(0);
            String type = item.elementAt(1);
            double startValue = Double.parseDouble(item.elementAt(2));
            double markup = Double.parseDouble(item.elementAt(3)) / 100.0;
            if(null == type){
                throw new RuntimeException("Invalid type for equipment!");
            }
            else switch (type) {
                case "Weapon":
                    lootTracker.getWeapons().add(new Weapon(name, startValue, markup));
                    break;
                case "Amp":
                    lootTracker.getAmps().add(new Amp(name, startValue, markup));
                    break;
                case "Healing":
                    lootTracker.getHealingTools().add(new HealingTool(name, startValue, markup));
                    break;
                case "Armor":
                    lootTracker.getArmors().add(new Armor(name, startValue, markup));
                    break;
                default:
                    throw new RuntimeException("Invalid type for equipment!");
            }
        });
    }
    
    public static void addEquipmentToSettingsFromData(LootTracker lootTracker, Vector<Vector<String>> equipmentData){
        equipmentData.forEach((Vector<String> item) -> {
            String name = item.elementAt(0);
            String type = item.elementAt(1);
            double startValue = Double.parseDouble(item.elementAt(2));
            double endValue = Double.parseDouble(item.elementAt(3));
            double markup = Double.parseDouble(item.elementAt(4)) / 100.0;
            Equipment e = EquipmentUtilities.getEquipmentByName(name, lootTracker.getAllEquipment());
            
            if(e == null){
                if(null == type){
                throw new RuntimeException("Invalid type for equipment!");
                }
                else switch (type) {
                    case "Weapon":
                        lootTracker.getWeapons().add(new Weapon(name, endValue, markup));
                        break;
                    case "Amp":
                        lootTracker.getAmps().add(new Amp(name, endValue, markup));
                        break;
                    case "Healing":
                        lootTracker.getHealingTools().add(new HealingTool(name, endValue, markup));
                        break;
                    case "Armor":
                        lootTracker.getArmors().add(new Armor(name, endValue, markup));
                        break;
                    default:
                        throw new RuntimeException("Invalid type for equipment!");
                }
            }
            else if(type != "Amp" && type != "Armor"){
              e.changeValue(endValue);
            } 
        });
            
            
    }
    
    public static String getTypeForEquipment(Equipment e){
        if(e instanceof Weapon){
            return "Weapon";
        }
        else if(e instanceof Amp){
            return "Amp";
        }
        else if(e instanceof HealingTool){
            return "Healing";
        }
        else if(e instanceof Armor){
            return "Armor";
        }
        else{
            return "";
        }
    }
    
    public static void addEquipment(ArrayList<Equipment> allEquipment, Equipment e){
        allEquipment.add(e);
    }
    
    public static void clearEquipment(ArrayList<Equipment> allEquipment){
        allEquipment.clear();
    }
    
    public static ArrayList<String> getAllNames(ArrayList<Equipment> equipment){
        ArrayList<String> result = new ArrayList<>();
        equipment.forEach((Equipment e) -> {
            result.add(e.getName());
        });
        return result;
    }
    
    public static Equipment getEquipmentByName(String name, ArrayList<Equipment> equipment){
        double value = 0.0;
        for(Equipment e : equipment){
            if(e.getName().equals(name)){
                return e;
            }
        }
        return null;
    }
}
