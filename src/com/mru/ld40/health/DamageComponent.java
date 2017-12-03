/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.health;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class DamageComponent implements EntityComponent{
    private final long damage;
    private final EntityId target;
    
    public DamageComponent(EntityId target, long damage){
        this.target = target;
        this.damage = damage;
    }
    
    public long getDamage(){
        return damage;
    }
    
    public EntityId getTarget(){
        return target;
    }
}
