/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class ImpulseComponent implements EntityComponent{
    private final EntityId target;
    private final Vector2 impulse;
    
    public ImpulseComponent(EntityId target, Vector2 impulse){
        this.target = target;
        this.impulse = impulse;
    }
    
    public EntityId getTarget(){
        return target;
    }
    
    public Vector2 getImpulse(){
        return impulse;
    }
}
