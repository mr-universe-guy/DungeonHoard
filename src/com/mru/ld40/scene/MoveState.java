/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
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
public class MoveState extends BaseAppState{
    private EntityData ed;
    private EntitySet moves;
    private final Map<EntityId, Vector3f> moveMap = new HashMap<>();
    
    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        moves = ed.getEntities(MoveComponent.class);
    }
    
    @Override
    public void update(float tpf){
        moveMap.clear();
        if(moves.applyChanges()){
            moves.getAddedEntities().forEach(e -> {
                MoveComponent move = e.get(MoveComponent.class);
                EntityId target = move.getTarget();
                Vector3f delta = new Vector3f(move.getTranslate().x, move.getTranslate().y, 0);
                Vector3f accum = moveMap.get(target);
                if(accum == null){
                    accum = delta;
                } else{
                    accum.addLocal(delta);
                }
                moveMap.put(target, accum);
                ed.setComponent(e.getId(), new DecayComponent());
            });
        }
        Iterator<EntityId> it = moveMap.keySet().iterator();
        while(it.hasNext()){
            EntityId id = it.next();
            TransformComponent trans = ed.getComponent(id, TransformComponent.class);
            if(trans != null){
                Vector3f pos = trans.getPos().add(moveMap.remove(id));
                ed.setComponents(id, new TransformComponent(pos));
            }
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        moves.release();
        moveMap.clear();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
