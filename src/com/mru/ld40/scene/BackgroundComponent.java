/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class BackgroundComponent implements EntityComponent{
    public static final String COBBLESTONE="floor.png";
    public static final String GRADIENT="sprite/DarkGradient.png";
    private final float width, height;
    private final String texture;
    
    public BackgroundComponent(String texture, float width, float height){
        this.texture = texture;
        this.width = width;
        this.height = height;
    }
    
    public String getTexture(){
        return texture;
    }
    
    public float getWidth(){
        return width;
    }
    
    public float getHeight(){
        return height;
    }
}
