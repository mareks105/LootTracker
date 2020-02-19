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
public class ItemTest {
    private Item testItem;
    private double originalValue = 3.54;
    
    @Before
    public void setUpClass() {
        testItem = new Item("test", originalValue);
    }
    
    @After
    public void tearDownClass() {
    }

    /**
     * Test of getName method, of class Item.
     */
    @Test
    public void testGetName() {
        assert(testItem.getName().equals(new String("test")));
    }

    /**
     * Test of getValue method, of class Item.
     */
    @Test
    public void testGetValue() {
        assertEquals(Double.compare(originalValue, 
                testItem.getValue()), 0);
    }

    /**
     * Test of changeValue method, of class Item.
     */
    @Test
    public void testChangeValue() {
        double newValue = 23.0;
        testItem.changeValue(newValue);
        assertEquals(Double.compare(newValue,
                testItem.getValue()), 0);
    }

    /**
     * Test of addValue method, of class Item.
     */
    @Test
    public void testAddValue() {
        double valueToAdd = 5.0;
        testItem.addValue(valueToAdd);
        assertEquals(Double.compare(originalValue + valueToAdd,
                testItem.getValue()), 0);
    }    
}
