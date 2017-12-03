/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.math.Vector2f;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class MoveComponent implements EntityComponent{
    private final EntityId target;
    private final Vector2f translate;
    
    public MoveComponent(EntityId target, Vector2f translate){
        this.target = target;
        this.translate = translate;
    }
    
    public EntityId getTarget(){
        return target;
    }
    
    public Vector2f getTranslate(){
        return translate;
    }
}
