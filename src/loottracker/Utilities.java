package loottracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Utilities {

    public static Map<DataKey,Double> initDataTable(){
        Map<DataKey, Double> dataTable = new EnumMap<>(DataKey.class);
        for (DataKey key : DataKey.values()){
            dataTable.put(key, 0.0);
        }
        return dataTable;
    }
    
    /**
     * Rounds all the values in the data table to two decimals
     * @param dataTable
     */
    public static void roundData(Map<loottracker.DataKey, Double> dataTable) {
        dataTable.replaceAll((k, v) -> round(v,2));
    }
	
    /**
     *
     * @param key
     * @return key as a DataKey type
     * @throws InvalidKeyException
     */
    public static DataKey getDataKey(String key) throws InvalidKeyException{
        switch(key){
            case "Ammo":
                return DataKey.Ammo;
            case "UniversalAmmo":
                return DataKey.UniversalAmmo;
            case "AmpDecayTT":
                return DataKey.AmpDecayTT;
            case "AmpDecayWithMarkup":
                return DataKey.AmpDecayWithMarkup;
            case "WeaponDecayTT":
                return DataKey.WeaponDecayTT;
            case "WeaponDecayWithMarkup":
                return DataKey.WeaponDecayWithMarkup;
            case "HealingDecayTT":
                return DataKey.HealingDecayTT;
            case "HealingDecayWithMarkup":
                return DataKey.HealingDecayWithMarkup;
            case "ArmorDecayTT":
                return DataKey.ArmorDecayTT;
            case "ArmorDecayWithMarkup":
                return DataKey.ArmorDecayWithMarkup;
            case "TotalDecayTT":
                return DataKey.TotalDecayTT;
            case "TotalDecayWithMarkup":
                return DataKey.TotalDecayWithMarkup;
            case "TotalCost":
                return DataKey.TotalCost;
            case "TotalLootTT":
                return DataKey.TotalLootTT;
            case "TotalLootWithMarkup":
                return DataKey.TotalLootWithMarkup;
            case "Markup":
                return DataKey.Markup;
            case "ReturnTT":
                return DataKey.ReturnTT;
            case "ReturnTTpercent":
                return DataKey.ReturnTTpercent;
            case "ReturnWithMarkup":
                return DataKey.ReturnWithMarkup;
            case "ReturnWithMarkupPercent":
                return DataKey.ReturnWithMarkupPercent;
            default:
                throw new InvalidKeyException("unknown data key: " + key);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        //System.out.println("here:");
        //System.out.println(value);
        BigDecimal bd = new BigDecimal(0);
        try{
             bd = new BigDecimal(value);
        }
        catch(Exception e){
            System.out.println(value);
        }
            
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    /**
     * Get index of item with specific name in list of items
     * @param allItems
     * @param name
     * @return
     */
    public static int getItemIndex(ArrayList<Item> allItems, String name) {
        for(int i = 0; i < allItems.size(); ++i) {
                if(allItems.get(i).getName().equals(name)) {
                        return i;
                }
        }
        return -1;
    }
        
  
    public static String toPercentage(double d) {
        return String.format("%1$.2f", d * 100) + "%";
    }
        
    public static boolean validateString(String s, boolean shouldBeDouble){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
             "[+-]?(" + // Optional sign character
             "NaN|" +           // "NaN" string
             "Infinity|" +      // "Infinity" string

             // A decimal floating-point string representing a finite positive
             // number without a leading sign has at most five basic pieces:
             // Digits . Digits ExponentPart FloatTypeSuffix
             //
             // Since this method allows integer-only strings as input
             // in addition to strings of floating-point literals, the
             // two sub-patterns below are simplifications of the grammar
             // productions from section 3.10.2 of
             // The Java Language Specification.

             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

             // . Digits ExponentPart_opt FloatTypeSuffix_opt
             "(\\.("+Digits+")("+Exp+")?)|"+

             // Hexadecimal strings
             "((" +
              // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
              "(0[xX]" + HexDigits + "(\\.)?)|" +

              // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
              "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

              ")[pP][+-]?" + Digits + "))" +
             "[fFdD]?))" +
             "[\\x00-\\x20]*");// Optional trailing "whitespace"

        if(shouldBeDouble){
            if (Pattern.matches(fpRegex, s))
                return true;
            else return s.isEmpty();
        }
        else{
            if (Pattern.matches(fpRegex, s)){
                return false;
            }
            else{
                return true;
            }
        }
    }
}
