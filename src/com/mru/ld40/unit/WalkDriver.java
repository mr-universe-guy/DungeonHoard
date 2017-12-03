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
public class WalkDriver implements EntityComponent{
    private final float percent;
    
    public WalkDriver(float percent){
        this.percent = percent;
    }
    
    public float getPercent(){
        return percent;
    }
    
    @Override
    public String toString(){
        return "Walk : "+percent;
    }
}
