/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.animation;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.VisualState;
import com.simsilica.es.Entity;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class AnimationState extends BaseAppState{
    private static final String MAT="matdef/Spritesheet.j3md";
    private EntityData ed;
    private EntitySet animated;
    private VisualState vis;
    private AssetManager am;
    private final Map<EntityId, Float> timeMap = new HashMap<>();
    private final Map<EntityId, Material> matMap = new HashMap<>();
    
    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        animated = ed.getEntities(AnimatedComponent.class);
        vis = getState(VisualState.class);
        am = aplctn.getAssetManager();
    }
    
    public void updateEntity(Entity a){
        EntityId id = a.getId();
            Spatial spat = vis.getSpatial(id);
            if(spat == null){
                ed.setComponent(id, a.get(AnimatedComponent.class));
                return;
            }
            Material mat = new Material(am, MAT);
            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
            mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
            AnimatedComponent ac = a.get(AnimatedComponent.class);
            Texture t = am.loadTexture(ac.getTexture());
            mat.setTexture("ColorMap", t);
            mat.setFloat("SizeX", ac.getXSplit());
            mat.setFloat("SizeY", ac.getYSplit());
            mat.setFloat("Position", 0);
            if(!timeMap.containsKey(id) || ac.isReset()){
                timeMap.put(id, 0f);
            }
            
            matMap.put(id, mat);
            spat.setMaterial(mat);
    }
    
    @Override
    public void update(float tpf){
        animated.applyChanges();
        animated.getAddedEntities().forEach(a ->  updateEntity(a));
        animated.getChangedEntities().forEach(a -> updateEntity(a));
        animated.forEach(a -> {
            EntityId id = a.getId();
            AnimatedComponent ac = a.get(AnimatedComponent.class);
            float dur = timeMap.get(id);
            float max = ac.getDuration();
            int position;
            if(max > 0){
                dur += tpf;
                dur %= max;
                float percent = dur/max;
                position = (int)(percent*ac.getMax());
            } else{
                position = 0;
            }
            matMap.get(id).setFloat("Position", position);
            timeMap.put(id, dur);
        });
    }

    @Override
    protected void cleanup(Application aplctn) {
        animated.release();
        timeMap.clear();
        matMap.clear();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
