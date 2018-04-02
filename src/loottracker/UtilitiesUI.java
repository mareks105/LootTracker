/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loottracker;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTable;

/**
 *
 * @author mege9
 */
public class UtilitiesUI {
    public static void addRightClickDeselect(JTable table){
        table.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e){
                if(e.getButton() == MouseEvent.BUTTON3){
                    table.clearSelection();
                }
            }
        });
    }
    
    public static void showFrameOnScreen(Window frame, int screen) {
        GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] graphicsDevices = graphicsEnvironment.getScreenDevices();
        GraphicsDevice graphicsDevice = ( screen > -1 && screen < graphicsDevices.length ) ? graphicsDevices[screen] : graphicsDevices.length > 0 ? graphicsDevices[0] : null;
        if (graphicsDevice == null)
        {
            throw new RuntimeException( "There are no screens !" );
        }
        Rectangle bounds = graphicsDevice.getDefaultConfiguration().getBounds();
        frame.setSize(bounds.width, bounds.height);
        frame.setLocation(bounds.x, bounds.y);
    }
}
