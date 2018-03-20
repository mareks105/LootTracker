/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;


public class Amp extends Equipment {
    
    public Amp(String name, double startValue, double markup) {
        this(name, startValue, markup, 0.0);
    }
    
    public Amp(String name, double startValue, double markup, double endValue) {
        super(name, startValue, markup, endValue);
        this.type = EquipmentType.Amp;
    }
    
}
