/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.health;

import com.jme3.app.Application;
import com.mru.ld40.scene.DecayComponent;
import com.mru.ld40.scene.EntityDataState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class HealthState extends BaseAppState{
    private EntityData ed;
    private EntitySet damages;
    private final Map<EntityId, Long> damageMap = new HashMap<>();

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        damages = ed.getEntities(DamageComponent.class);
    }
    
    @Override
    public void update(float tpf){
        damageMap.clear();
        if(damages.applyChanges()){
            damages.getAddedEntities().forEach(d -> {
                DamageComponent dc = d.get(DamageComponent.class);
                EntityId target = dc.getTarget();
                long damage = dc.getDamage();
                if(damageMap.containsKey(target)) damage += damageMap.get(target);
                damageMap.put(target, damage);
                ed.setComponent(d.getId(), new DecayComponent());
            });
            Iterator<EntityId> it = damageMap.keySet().iterator();
            while(it.hasNext()){
                EntityId id = it.next();
                long health = -damageMap.get(id);
                HealthComponent hp = ed.getComponent(id, HealthComponent.class);
                if(hp != null){
                    health = hp.getHealth()+health;
                }
                ed.setComponent(id, new HealthComponent(health));
            }
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
