package com.mru.ld40.scene;

import com.jme3.app.Application;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class DecayState extends BaseAppState{
    private EntityData ed;
    private EntitySet decays;
    private final Map<EntityId, Float> decayMap = new HashMap<>();
    
    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        decays = ed.getEntities(DecayComponent.class);
    }
    
    @Override
    public void update(float tpf){
        if(decays.applyChanges()){
            decays.getAddedEntities().forEach(d -> {
                float time = d.get(DecayComponent.class).getDelay()/1000f;
                decayMap.put(d.getId(), time);
            });
        }
        decays.stream().map(d -> d.getId()).forEach(id -> {
            float t = decayMap.get(id);
            if((t = t-tpf) <= 0){
                ed.removeEntity(id);
                decayMap.remove(id);
            } else{
                decayMap.put(id, t);
            }
        });
    }

    @Override
    protected void cleanup(Application aplctn) {
        decays.release();
        decayMap.clear();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
