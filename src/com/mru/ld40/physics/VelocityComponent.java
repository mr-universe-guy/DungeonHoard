/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.simsilica.es.EntityComponent;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class VelocityComponent implements EntityComponent{
    private final Vector2 velocity;
    
    public VelocityComponent(Vector2 velocity){
        this.velocity = velocity;
    }
    
    public Vector2 getVelocity(){
        return velocity;
    }
}
