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
public enum AmmoType {
    UniversalAmmo {
        @Override
        public String toString(){
            return "Universal Ammo";
        }
    },
    WeaponCells {
        @Override
        public String toString(){
            return "Weapon Cells";
        }
    },
    BLP {
        @Override
        public String toString(){
            return "BLP";
        }
    }
}