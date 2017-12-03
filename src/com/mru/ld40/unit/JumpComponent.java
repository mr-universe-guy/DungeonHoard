/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.unit;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class JumpComponent implements EntityComponent{
    private final float height;
    
    public JumpComponent(float height){
        this.height = height;
    }
    
    public float getHeight(){
        return height;
    }
}
