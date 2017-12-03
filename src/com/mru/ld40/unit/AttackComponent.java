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
public class AttackComponent implements EntityComponent{
    private final int attack;
    private final float reach;
    
    public AttackComponent(int attack, float reach){
        this.attack = attack;
        this.reach = reach;
    }
    
    public int getAttack(){
        return attack;
    }
    
    public float getReach(){
        return reach;
    }
}
