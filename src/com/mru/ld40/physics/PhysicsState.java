/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.jme3.app.Application;
import com.jme3.math.Vector3f;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.TransformComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Map;
import org.dyn4j.Listener;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Transform;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class PhysicsState extends BaseAppState{
    public static final int FLOOR = 0x001,
            PLAYER = 0x002,
            GOBLIN = 0x004;
    private EntityData ed;
    private EntitySet bodies;
    private World world;
    private final Map<EntityId, Body> bodyMap = new HashMap<>();
    private final Map<Body, EntityId> idMap = new HashMap<>();
    
    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        bodies = ed.getEntities(RigidBodyComponent.class, TransformComponent.class);
        world = new World();
    }
    
    @Override
    public void update(float tpf){
        if(world.update(tpf)){
            if(bodies.applyChanges()){
                bodies.getAddedEntities().forEach(b -> {
                    RigidBodyComponent rb = b.get(RigidBodyComponent.class);
                    double mass = rb.getMass();
                    Convex[] shapes = rb.getShapes();
                    Body body = new Body();
                    body.setLinearDamping(1);
                    BodyFixture f = new BodyFixture(shapes[0]);
                    f.setFilter(rb.getFilter());
                    if(mass > 0){
                        f.setDensity(mass);
                        body.addFixture(f);
                        body.setMass(MassType.FIXED_ANGULAR_VELOCITY);
                    } else{
                        body.addFixture(f);
                        body.setMass(MassType.INFINITE);
                    }
                    
                    EntityId id = b.getId();
                    bodyMap.put(id, body);
                    idMap.put(body, id);
                    Vector3f pos = b.get(TransformComponent.class).getPos();
                    body.getTransform().setTranslation(pos.x, pos.y);
                    world.addBody(body);
                });
                bodies.getRemovedEntities().forEach(b -> {
                    Body body = bodyMap.remove(b.getId());
                    world.removeBody(body);
                    idMap.remove(body);
                });
            }
            
            bodies.forEach(b -> {
                EntityId id = b.getId();
                Body body = bodyMap.get(id);
                Transform t = body.getTransform();
                TransformComponent trans = new TransformComponent((float)t.getTranslationX(),
                        (float)t.getTranslationY(), b.get(TransformComponent.class).getPos().z);
                VelocityComponent vel = new VelocityComponent(body.getLinearVelocity());
                ed.setComponents(id, trans, vel);
            });
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        bodies.release();
        bodyMap.clear();
        idMap.clear();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    public EntityId getIdFromBody(Body b){
        return idMap.get(b);
    }
    
    public Body getBodyFromId(EntityId id){
        return bodyMap.get(id);
    }
    
    public void addListener(Listener l){
        world.addListener(l);
    }
}
