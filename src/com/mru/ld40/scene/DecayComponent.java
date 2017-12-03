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
public class DecayComponent implements EntityComponent{
    private final int delay;
    
    /**
     * 
     * @param delay in millis
     */
    public DecayComponent(int delay){
        this.delay = delay;
    }
    
    public DecayComponent(){
        this(0);
    }
    
    public int getDelay(){
        return delay;
    }
}
