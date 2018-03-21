/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.util.NoSuchElementException;



/**
 *
 * @author mege9
 */
public class MarkupHandlerException extends NoSuchElementException{
    public MarkupHandlerException(String msg){
        super(msg);
    }
}
