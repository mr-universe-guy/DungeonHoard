/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.jme3.math.Vector2f;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class ForceComponent implements EntityComponent{
    private final EntityId target;
    private final Vector2 force;
    
    public ForceComponent(EntityId target, Vector2 force){
        this.target = target;
        this.force = force;
    }
    
    public ForceComponent(EntityId target, Vector2f force){
        this(target, new Vector2(force.x, force.y));
    }
    
    public EntityId getTarget(){
        return target;
    }
    
    public Vector2 getForce(){
        return force;
    }
}
