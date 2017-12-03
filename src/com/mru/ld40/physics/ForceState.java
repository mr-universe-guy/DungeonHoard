/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.jme3.app.Application;
import com.mru.ld40.scene.DecayComponent;
import com.mru.ld40.scene.EntityDataState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class ForceState extends BaseAppState{
    private EntityData ed;
    private EntitySet forces, impulses;
    private PhysicsState phys;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        forces = ed.getEntities(ForceComponent.class);
        impulses = ed.getEntities(ImpulseComponent.class);
        phys = getState(PhysicsState.class);
    }
    
    @Override
    public void update(float tpf){
        if(forces.applyChanges()){
            forces.getAddedEntities().forEach(f -> {
                ForceComponent force = f.get(ForceComponent.class);
                EntityId target = force.getTarget();
                phys.getBodyFromId(target).applyForce(force.getForce());
                ed.setComponent(f.getId(), new DecayComponent());
            });
        }
        if(impulses.applyChanges()){
            impulses.getAddedEntities().forEach(i -> {
                ImpulseComponent impulse = i.get(ImpulseComponent.class);
                EntityId target = impulse.getTarget();
                phys.getBodyFromId(target).applyImpulse(impulse.getImpulse());
                ed.setComponent(i.getId(), new DecayComponent());
            });
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        forces.release();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
