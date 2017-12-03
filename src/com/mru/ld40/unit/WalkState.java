/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.unit;

import com.jme3.app.Application;
import com.jme3.math.Vector2f;
import com.mru.ld40.physics.ImpulseComponent;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.physics.VelocityComponent;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.MoveComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class WalkState extends BaseAppState implements StepListener{
    private EntityData ed;
    private EntitySet walkers;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        walkers = ed.getEntities(WalkComponent.class, WalkDriver.class);
        getState(PhysicsState.class).addListener(this);
    }
    
    @Override
    public void update(float tpf){
        
    }

    @Override
    protected void cleanup(Application aplctn) {
        walkers.release();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }

    @Override
    public void begin(Step step, World world) {
        
    }

    @Override
    public void updatePerformed(Step step, World world) {
        
    }

    @Override
    public void postSolve(Step step, World world) {
        
    }

    @Override
    public void end(Step step, World world) {
        float tpf = (float)step.getDeltaTime();
        walkers.applyChanges();
        walkers.forEach(w -> {
            EntityId id = w.getId();
            float speed = w.get(WalkComponent.class).getSpeed();
            float dir = w.get(WalkDriver.class).getPercent();
            float target = speed*dir;
            VelocityComponent vel = ed.getComponent(id, VelocityComponent.class);
            if(vel != null){
                double lon = vel.getVelocity().x;
                float delta = target-(float)lon;
                Vector2 force = new Vector2(delta, 0);
                ed.setComponent(ed.createEntity(), new ImpulseComponent(id, force));
            } else{
                ed.setComponent(ed.createEntity(), new MoveComponent(id, new Vector2f(target*tpf, 0)));
            }
        });
    }
    
}
