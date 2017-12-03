/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.unit;

import com.jme3.app.Application;
import com.mru.ld40.audio.AudioState;
import com.mru.ld40.physics.ImpulseComponent;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.scene.BackgroundComponent;
import com.mru.ld40.scene.EntityDataState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class JumpState extends BaseAppState implements StepListener{
    private EntityData ed;
    private EntitySet jumpers;
    private PhysicsState phys;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        jumpers = ed.getEntities(JumpComponent.class, JumpDriver.class);
        phys = getState(PhysicsState.class);
        phys.addListener(this);
    }

    @Override
    protected void cleanup(Application aplctn) {
        jumpers.release();
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
        jumpers.applyChanges();
        jumpers.forEach(j -> {
            EntityId id = j.getId();
            Body b = phys.getBodyFromId(id);
            if(b == null)return;
            if(b.getInContactBodies(false).stream().anyMatch(cb -> {
                return ed.getComponent(phys.getIdFromBody(cb), BackgroundComponent.class) != null;
            })){
                //touching something, we can jump :3
                if(j.get(JumpDriver.class).isJump()){
                    Vector2 force = new Vector2(0, j.get(JumpComponent.class).getHeight());
                    ed.setComponent(id, new JumpedComponent());
                    ed.setComponent(ed.createEntity(), new ImpulseComponent(id, force));
                    getState(AudioState.class).playSound(AudioState.CHARA_JUMP);
                }
            }
        });
    }
    
}
