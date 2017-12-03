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
public class WalkComponent implements EntityComponent{
    private final float speed;
    
    public WalkComponent(float speed){
        this.speed = speed;
    }
    
    public float getSpeed(){
        return speed;
    }
}
