package loottracker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Utilities {
	
    public static enum DataKey {
            HealingDecay,
            Ammo,
            AmpDecay,
            WeaponDecay,
            TotalCost,
            TotalLootTT,
            TotalLootWithMarkup,
            ReturnTT,
            ReturnWithMarkup
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
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
            else {
                return false;
            }
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
