/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;


public class Weapon extends Equipment {

    public Weapon(String name, double startValue, double markup) {
        super(name, startValue, markup);
    }

    public Weapon(String name, double startValue, double markup, double endValue) {
        super(name, startValue, markup, endValue);
    }
    
}
