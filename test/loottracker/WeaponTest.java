/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author mege9
 */
public class WeaponTest {
    
    private Weapon testWeapon;
    private double startValue = 53.0;
    private double endValue = 49.0;
    private double markup = 1.4;
    public WeaponTest() {
    }
    
    @Before
    public void setUpClass() {
        testWeapon = new Weapon("test", startValue, markup, endValue);
    }
    
    @After
    public void tearDownClass() {
    }

    @Test
    public void testGetDecayTT() {
        assertEquals(Double.compare(this.testWeapon.getDecayTT(),
                startValue - endValue),0);
    }
    
    @Test
    public void testGetDecayWithMarkup() {
        assertEquals(Double.compare(this.testWeapon.getDecayWithMarkup(),
                (startValue - endValue) * 1.4), 0);
    }
}
