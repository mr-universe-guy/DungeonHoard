/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
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
public class VisualState extends BaseAppState{
    private final Map<EntityId, Spatial> spatMap = new HashMap<>();
    private final Node visualNode = new Node("Visuals");
    private EntityData ed;
    private EntitySet models, background, colors;
    private ModelFactory factory;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        models = ed.getEntities(TransformComponent.class, SpriteComponent.class, FacingComponent.class);
        background = ed.getEntities(TransformComponent.class, BackgroundComponent.class);
        colors = ed.getEntities(ColorComponent.class);
        SimpleApplication app = (SimpleApplication)aplctn;
        app.getRootNode().attachChild(visualNode);
        factory = new ModelFactory(app.getAssetManager());
    }
    
    private void transformSpatial(EntityId e, Vector3f pos, int facing){
        Spatial s = spatMap.get(e);
        s.setLocalTranslation(pos.x, pos.y, pos.z);
        s.setLocalScale(facing, 1, 1);
    }
    
    @Override
    public void update(float tpf){
        if(models.applyChanges()){
            models.getAddedEntities().forEach(m -> {
                Vector3f pos = m.get(TransformComponent.class).getPos();
                String sprite = m.get(SpriteComponent.class).getSpriteSheet();
                Spatial s = factory.loadEntity(sprite);
                spatMap.put(m.getId(), s);
                visualNode.attachChild(s);
                transformSpatial(m.getId(), pos, m.get(FacingComponent.class).getFacing());
            });
            models.getRemovedEntities().forEach(m -> {
                spatMap.remove(m.getId()).removeFromParent();
            });
            models.getChangedEntities().forEach(m -> {
                Vector3f pos = m.get(TransformComponent.class).getPos();
                transformSpatial(m.getId(), pos, m.get(FacingComponent.class).getFacing());
            });
        }
        if(background.applyChanges()){
            background.getAddedEntities().forEach(b -> {
                Vector3f pos = b.get(TransformComponent.class).getPos();
                BackgroundComponent bg = b.get(BackgroundComponent.class);
                String img = bg.getTexture();
                float width = bg.getWidth();
                float height = bg.getHeight();
                Spatial s = factory.loadRepeating(img, width, height);
                spatMap.put(b.getId(), s);
                visualNode.attachChild(s);
                transformSpatial(b.getId(), pos, 1);
            });
            background.getRemovedEntities().forEach(b -> {
                spatMap.remove(b.getId()).removeFromParent();
            });
        }
        if(colors.applyChanges()){
            colors.getAddedEntities().forEach(c -> {
                Spatial s = spatMap.get(c.getId());
                if(s != null){
                    ColorRGBA col = c.get(ColorComponent.class).getColor();
                    if(s instanceof Geometry){
                        Geometry geo = (Geometry)s;
                        geo.getMaterial().setColor("Color", col);
                    } else if(s instanceof Node){
                        Node n = (Node)s;
                        ((Node) s).descendantMatches(Geometry.class).forEach(geo -> {
                            geo.getMaterial().setColor("Color", col);
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        models.release();
        background.release();
        visualNode.detachAllChildren();
        visualNode.removeFromParent();
        spatMap.clear();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    protected void setSpatial(EntityId id, Spatial spat){
        if(spatMap.containsKey(id)){
            spatMap.remove(id).removeFromParent();
        }
        spatMap.put(id, spat);
    }
    
    public Spatial getSpatial(EntityId id){
        return spatMap.get(id);
    }
}
