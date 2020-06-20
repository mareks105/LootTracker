/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

/**
 *
 * @author mege9
 */
public class AmmoUtilities {
    public static boolean IsAmmo(Item item)
    {
        AmmoType[] ammoTypes = AmmoType.values();
        for(AmmoType type : ammoTypes){
            if( item.getName().equals(type.toString())){
                return true;
            }
        }
        return false;
    }
}
