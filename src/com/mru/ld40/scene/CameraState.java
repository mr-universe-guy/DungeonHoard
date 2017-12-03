/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.WatchedEntity;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class CameraState extends BaseAppState{
    private EntityData ed;
    private WatchedEntity target;
    private Camera cam;
    private float zoom = 1f/15f;
    private float distance = 20f;
    private float yOffset = 4f;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        cam = aplctn.getCamera();
        cam.setParallelProjection(true);
        int width = cam.getWidth();
        int height = cam.getHeight();
        cam.setFrustum(0.1f, 50f, -(width/2f)*zoom, (width/2f)*zoom, (height/2f)*zoom,
                -(height/2f)*zoom);
    }
    
    @Override
    public void update(float tpf){
        if(target != null){
            target.applyChanges();
            Vector3f pos = target.get(TransformComponent.class).getPos();
            Vector3f camPos = new Vector3f(pos.x, pos.y+yOffset, distance);
            Vector3f oldPos = cam.getLocation();
            
            cam.setLocation(FastMath.interpolateLinear(tpf, oldPos, camPos));
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        if(target != null){
            target.release();
        }
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    public void setTarget(EntityId target){
        this.target = ed.watchEntity(target, TransformComponent.class);
    }
    
    public void setDistance(float distance){
        this.distance = distance;
    }
    
    public float getDistance(){
        return distance;
    }
    
    public void setYOffset(float offset){
        this.yOffset = offset;
    }
    
    public float getYOffset(){
        return yOffset;
    }
}
