/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.math.ColorRGBA;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class ColorComponent implements EntityComponent{
    private final ColorRGBA color;
    
    public ColorComponent(ColorRGBA color){
        this.color = color;
    }
    
    public ColorRGBA getColor(){
        return color;
    }
}
