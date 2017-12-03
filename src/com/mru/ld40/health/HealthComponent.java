/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.health;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class HealthComponent implements EntityComponent{
    private final long health;
    
    public HealthComponent(long health){
        this.health = health;
    }
    
    public long getHealth(){
        return health;
    }
}
