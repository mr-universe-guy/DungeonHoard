/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.unit;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.mru.ld40.audio.AudioState;
import com.mru.ld40.health.DamageComponent;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.FacingComponent;
import com.mru.ld40.scene.TransformComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import java.util.ArrayList;
import java.util.List;
import org.dyn4j.dynamics.DetectResult;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class AttackState extends BaseAppState implements StepListener{
    private EntityData ed;
    private EntitySet attackers;
    private PhysicsState phys;
    private final Convex attackBox = new Rectangle(0.5, 0.5);

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        attackers = ed.getEntities(AttackComponent.class, AttackDriver.class,
                TransformComponent.class, FacingComponent.class);
        phys = getState(PhysicsState.class);
        phys.addListener(this);
    }

    @Override
    protected void cleanup(Application aplctn) {
        attackers.release();
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
        attackers.applyChanges();
        attackers.stream().filter(a -> a.get(AttackDriver.class).isAttacking())
                .forEach(a -> {
            Vector3f pos = a.get(TransformComponent.class).getPos();
            AttackComponent attack = a.get(AttackComponent.class);
            float dir = a.get(FacingComponent.class).getFacing()*attack.getReach();
            EntityId id = a.getId();
            //TODO: spawn sword effect
            AudioState audio = getState(AudioState.class);
            audio.playSound(AudioState.SWORD_SWING);
            Transform trans = new Transform();
            trans.setTranslation(pos.x+dir, pos.y);
            List<DetectResult> results = new ArrayList<>();
            world.detect(attackBox, trans, results);
            results.stream()
                .filter(r -> id != phys.getIdFromBody(r.getBody()))
                .forEach(r -> {
                    EntityId targetId = phys.getIdFromBody(r.getBody());
                    System.out.println("Attack has hit an opponent "+targetId);
                    audio.playSound(AudioState.SWORD_HIT);
                    ed.setComponent(ed.createEntity(), new DamageComponent(targetId, 
                            attack.getAttack()));
                });
            ed.setComponent(id, new AttackDriver(false));
        });
    }
    
}
