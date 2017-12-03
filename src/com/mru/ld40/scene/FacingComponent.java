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
public class FacingComponent implements EntityComponent{
    public static final int RIGHT=1, LEFT=-1;
    private final int facing;
    
    public FacingComponent(int facing){
        this.facing = facing;
    }
    
    public int getFacing(){
        return facing;
    }
}
