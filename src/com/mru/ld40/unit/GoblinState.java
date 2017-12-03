/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.unit;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.mru.ld40.animation.AnimatedComponent;
import com.mru.ld40.coin.CoinState;
import com.mru.ld40.coin.HoldingComponent;
import com.mru.ld40.game.GameState;
import com.mru.ld40.game.GuiState;
import com.mru.ld40.health.DamageComponent;
import com.mru.ld40.health.DeadComponent;
import com.mru.ld40.health.HealthComponent;
import com.mru.ld40.physics.ImpulseComponent;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.physics.RigidBodyComponent;
import com.mru.ld40.scene.DecayComponent;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.FacingComponent;
import com.mru.ld40.scene.SpriteComponent;
import com.mru.ld40.scene.TransformComponent;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.WatchedEntity;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Map;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class GoblinState extends BaseAppState implements StepListener{
    private final int difficulty = 3;
    private EntityData ed;
    private EntitySet gobs, physGobs;
    private float delay = 0;
    private final Map<EntityId, GobAction> aiMap = new HashMap<>();
    private final Vector2f LEFT_SPAWN=new Vector2f(-70,1), RIGHT_SPAWN=new Vector2f(70,1);
    private WatchedEntity player;
    private final float maxDistance = 80;
    private Label gobsLabel;

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        gobs = ed.getEntities(GoblinComponent.class, TransformComponent.class, HealthComponent.class);
        physGobs = ed.getEntities(GoblinComponent.class, RigidBodyComponent.class, TransformComponent.class);
        EntityId playerId = getState(GameState.class).getPlayerId();
        player = ed.watchEntity(playerId, TransformComponent.class);
        gobsLabel = new Label("");
        gobsLabel.setTextHAlignment(HAlignment.Right);
        getState(GuiState.class).addInfo(gobsLabel);
        getState(PhysicsState.class).addListener(this);
    }
    
    private EntityId spawnGob(Vector2f pos){
        EntityId id = ed.createEntity();
        TransformComponent trans = new TransformComponent(new Vector3f(pos.x, pos.y, 1f));
        SpriteComponent sprite = new SpriteComponent(SpriteComponent.GOB);
        WalkComponent walk = new WalkComponent(4f);
        GoblinComponent gobgob = new GoblinComponent();
        CanCarry cc = new CanCarry();
        Filter cat = new CategoryFilter(PhysicsState.GOBLIN, PhysicsState.FLOOR|PhysicsState.PLAYER);
        RigidBodyComponent rb = new RigidBodyComponent(0.2,cat, new Rectangle(1.2,2.4));
        FacingComponent face = new FacingComponent(FacingComponent.LEFT);
        HealthComponent hp = new HealthComponent(1);
        ed.setComponents(id, trans, sprite, walk, gobgob, cc, rb, face, hp);
        aiMap.put(id, GobAction.LOOT);
        return id;
    }
    
    @Override
    public void update(float tpf){
        gobs.applyChanges();
        if((delay += tpf) >= 1/3f){
            long coins = getState(CoinState.class).getHoard();
            long maxGobs;
            maxGobs = (coins/difficulty)+1;
            int numGobs = gobs.size();
            gobsLabel.setText(numGobs+" : "+maxGobs+" Gobs");
            delay = 0;
            if(numGobs < maxGobs){
                Vector2f pos = LEFT_SPAWN;
                if(FastMath.nextRandomInt(0, 1) == 1){
                    pos = RIGHT_SPAWN;
                }
                spawnGob(pos);
            }
        }
        player.applyChanges();
        Vector3f ppos = player.get(TransformComponent.class).getPos();
        gobs.forEach(g -> {
            //am I dead?
            EntityId id = g.getId();
            long hp = g.get(HealthComponent.class).getHealth();
            if(hp <= 0){
                //I is dead
//                System.out.println("Goblin has died of natural causes");
                aiMap.put(id, GobAction.DEAD);
                ed.removeComponent(id, HealthComponent.class);
                ed.removeComponent(id, RigidBodyComponent.class);
                ed.setComponents(id, new DecayComponent(5000), new DeadComponent());
            }
            //evaluate ai
            Vector3f gpos = g.get(TransformComponent.class).getPos();
            if(gpos.x < -maxDistance || gpos.x > maxDistance){
                //gob will dissapear with it's gold coin
                ed.setComponent(id, new DecayComponent());
                return;
            }
            float dir = 1;
//            if(!aiMap.containsKey(id))aiMap.put(id, GobAction.LOOT);
            AnimatedComponent walk = new AnimatedComponent(AnimatedComponent.GOB_WALK, 2, 1, 2, 0.5f, false);
            switch(aiMap.get(id)){
                case LOOT:
                    //looting involves running towards the players hoard to steal
                    //for now player hoard is just gonna sit on 0,0
                    if(ed.getComponent(id, HoldingComponent.class) != null){
                        //has coin RUN
                        aiMap.put(id, GobAction.RUN);
                    } else if(ppos.distanceSquared(gpos) < 25){
                        aiMap.put(id, GobAction.ATTACK);
                    } else{
                        dir = -FastMath.sign(g.get(TransformComponent.class).getPos().x);
                        ed.setComponents(id, new WalkDriver(dir), new FacingComponent((int)dir), walk);
                    }
                    break;
                case ATTACK:
                    //chase the player :P
                    if(ppos.distanceSquared(gpos) < 25){
                        dir = FastMath.sign(ppos.subtract(gpos).x);
                        ed.setComponents(id, new WalkDriver(dir), walk);
                    } else{
                        aiMap.put(id, GobAction.LOOT);
                    }
                    break;
                case RUN:
                    //run away from the player
                    dir = FastMath.sign(gpos.subtract(ppos).x);
                    AnimatedComponent coin = new AnimatedComponent(AnimatedComponent.GOB_COIN,2,1,2,0.5f,false);
                    ed.setComponents(id, new WalkDriver(dir), coin);
                    break;
                case DEAD:
                    ed.removeComponent(id, WalkDriver.class);
                    //set to death animation and leave to rot
                    AnimatedComponent anim = new AnimatedComponent(AnimatedComponent.GOB_DEAD, 1, 1, 1, 1, false);
                    ed.setComponent(id, anim);
                    break;
            }
            ed.setComponent(id, new FacingComponent((int)dir));
        });
    }

    @Override
    protected void cleanup(Application aplctn) {
        player.release();
        gobs.release();
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
        PhysicsState phys = getState(PhysicsState.class);
        physGobs.applyChanges();
        player.applyChanges();
        physGobs.stream().forEach(g -> {
            EntityId id = g.getId();
            Body b = phys.getBodyFromId(id);
            if(b != null && b.getInContactBodies(false).stream()
                    .map(bod -> phys.getIdFromBody(bod))
                    .anyMatch(did -> player.getId().equals(did))){
                //in contact with player, apply force!
                Vector3f gobPos = g.get(TransformComponent.class).getPos();
                Vector3f p1Pos = player.get(TransformComponent.class).getPos();
                float dir = FastMath.sign(p1Pos.subtract(gobPos).x);
                //give the player a hearty bounce
                ed.setComponent(ed.createEntity(), new ImpulseComponent(player.getId(),
                        new Vector2(dir*500, 35)));
                ed.setComponent(ed.createEntity(), new DamageComponent(player.getId(), 1));
            }
        });
    }
    
    private enum GobAction{
        LOOT,
        ATTACK,
        RUN,
        DEAD;
    }
}
