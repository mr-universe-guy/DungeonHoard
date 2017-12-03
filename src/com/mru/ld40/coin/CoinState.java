/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.coin;

import com.jme3.app.Application;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.mru.ld40.animation.AnimatedComponent;
import com.mru.ld40.audio.AudioState;
import com.mru.ld40.game.GameState;
import com.mru.ld40.game.GuiState;
import com.mru.ld40.health.DeadComponent;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.scene.DecayComponent;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.FacingComponent;
import com.mru.ld40.scene.SpriteComponent;
import com.mru.ld40.scene.TransformComponent;
import com.mru.ld40.unit.CanCarry;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;
import com.simsilica.es.WatchedEntity;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.BaseAppState;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dyn4j.dynamics.Body;
import org.dyn4j.dynamics.DetectResult;
import org.dyn4j.dynamics.Step;
import org.dyn4j.dynamics.StepListener;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Transform;

/**
 * Spawns coins randomly a set distance from the player.
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class CoinState extends BaseAppState implements StepListener{
    private final float minDistance = 15, maxDistance = 30;
    private float spawnDelay = 0;
    private final float coinInvul = 0.5f, chestOpenDur=0.5f;
    private float chestTimer = 0.5f;
    private long hoard = 0;
    private long peak = 0;
    private EntityData ed;
    private EntitySet coins, carrying, canCarry, deaths;
    private WatchedEntity coinPile;
    private final Label coinLabel = new Label(""), peakLabel = new Label("");
    private final Convex coinHit = new Rectangle(1.6, 1.6);
    private final Convex pileHit = new Rectangle(3.2, 3.2);
    private PhysicsState phys;
    private GameState game;
    private final Map<EntityId, Float> invulMap = new HashMap<>();

    @Override
    protected void initialize(Application aplctn) {
        ed = getState(EntityDataState.class).getEntityData();
        coins = ed.getEntities(CoinComponent.class, TransformComponent.class);
        carrying = ed.getEntities(HoldingComponent.class);
        canCarry = ed.getEntities(CanCarry.class);
        deaths = ed.getEntities(DeadComponent.class, TransformComponent.class);
        GuiState gui = getState(GuiState.class);
//        gui.attachToUI(coinLabel);
        coinLabel.setMaxWidth(100);
        coinLabel.setTextHAlignment(HAlignment.Right);
        peakLabel.setMaxWidth(100);
        peakLabel.setTextHAlignment(HAlignment.Right);
        gui.addInfo(coinLabel);
        gui.addInfo(peakLabel);
//        coinLabel.setLocalTranslation(width-coinLabel.getMaxWidth()-borders[3],
//                height-borders[2], 0);
        updateCounter();
        getState(PhysicsState.class).addListener(this);
        coinPile = spawnCoinPile();
        phys = getState(PhysicsState.class);
        game = getState(GameState.class);
    }
    
    protected WatchedEntity spawnCoinPile(){
        EntityId id = ed.createEntity();
        TransformComponent trans = new TransformComponent(0, 0.6f, -1f);
        SpriteComponent sprite = new SpriteComponent(SpriteComponent.PILE);
        FacingComponent face = new FacingComponent(1);
        ed.setComponents(id, trans, sprite, face);
        return ed.watchEntity(id, TransformComponent.class);
    }
    
    private Vector3f getRandomPos(){
        float pos = minDistance+(FastMath.nextRandomFloat()*(maxDistance-minDistance));
        if(FastMath.nextRandomFloat() < 0.5f){
            pos *= -1;
        }
        return new Vector3f(pos, 1, 0f);
    }
    
    protected EntityId spawnCoin(){
        return spawnCoin(getRandomPos());
    }
    
    protected EntityId spawnCoin(Vector3f pos){
        EntityId id = ed.createEntity();
        TransformComponent trans = new TransformComponent(pos.x, 1f, 3f);
        SpriteComponent sprite = new SpriteComponent(SpriteComponent.COIN);
        AnimatedComponent anim = new AnimatedComponent(AnimatedComponent.COIN, 2, 2, 4, 0.5f, false);
        CoinComponent coin = new CoinComponent();
        FacingComponent face = new FacingComponent(1);
        ed.setComponents(id, trans, sprite, coin, face, anim);
        return id;
    }
    
    @Override
    public void update(float tpf){
        spawnDelay -= tpf;
        if(spawnDelay <= 0){
            //check if there are few enough coins to spawn a new one
            coins.applyChanges();
            if(coins.size() < 1){
                //not many coins, lets spawn a new one
                spawnCoin();
            }
            spawnDelay = 1f/10f;
        }
        carrying.applyChanges();
        canCarry.applyChanges();
        if(deaths.applyChanges()){
            deaths.getAddedEntities().forEach(d -> {
                Vector3f pos = d.get(TransformComponent.class).getPos();
                spawnCoin(pos);
//                System.out.println("Dead entity left a coin at "+pos);
            });
        }
        if(coins.applyChanges()){
            coins.getAddedEntities().forEach(c -> {
                invulMap.put(c.getId(), 0f);
            });
        }
        Iterator<EntityId> it =  invulMap.keySet().iterator();
        while(it.hasNext()){
            EntityId id = it.next();
            float v = invulMap.get(id)+tpf;
            if(v > coinInvul){
                it.remove();
            } else{
                invulMap.put(id, v);
            }
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        coins.release();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    public void updateCounter(){
        coinLabel.setText(hoard+" : Coins");
        peakLabel.setText(peak+" : Peak");
    }
    
    private void setHoard(long hoard){
        this.hoard = hoard;
        if(hoard > peak){
            peak = hoard;
        }
        updateCounter();
    }
    
    public long getHoard(){
        return hoard;
    }
    
    public long getPeak(){
        return peak;
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
    
    private void pickupCoin(EntityId id){
        ed.setComponent(id, new HoldingComponent());
        getState(AudioState.class).playSound(AudioState.PICKUP_COIN);
//        System.out.println("Picked up coin");
    }
    
    private void chestAnim(float tpf){
        String anim;
        if(chestTimer > 0){
            if(hoard >= 5) anim = AnimatedComponent.CHEST_OPEN_FULL;
            else anim = AnimatedComponent.CHEST_OPEN_EMPTY;
            chestTimer -= tpf;
        } else{
            anim = AnimatedComponent.CHEST_CLOSED;
        }
        coinPile.set(new AnimatedComponent(anim, 1, 1, 1, -1, false));
    }

    @Override
    public void end(Step step, World world) {
        if(coins.applyChanges()){
            //
        }
        coins.forEach(c -> {
            //am I invul?
            if(invulMap.containsKey(c.getId())){
                return;
            }
            //check for collisions
            Vector3f pos = c.get(TransformComponent.class).getPos();
            Transform trans = new Transform();
            trans.setTranslation(pos.x, pos.y);
            List<DetectResult> results = new ArrayList<>();
            world.detect(coinHit, trans, results);
            if(results.size() > 0){
                results.forEach(r -> {
                    Body b = r.getBody();
                    EntityId id = phys.getIdFromBody(b);
                    if(canCarry.containsId(id) && !carrying.containsId(id)){
                        ed.setComponent(c.getId(), new DecayComponent());
                        pickupCoin(id);
                    }
                });
            }
        });
        
        coinPile.applyChanges();
        Vector3f pos = coinPile.get(TransformComponent.class).getPos();
        Transform trans = new Transform();
        trans.setTranslation(pos.x, pos.y);
        List<DetectResult> results = new ArrayList<>();
        world.detect(pileHit, trans, results);
        if(results.size() > 0){
            results.forEach(r -> {
                Body b = r.getBody();
                EntityId id = phys.getIdFromBody(b);
                if(canCarry.containsId(id)){
                    if(carrying.containsId(id)){
                        if(id.equals(game.getPlayerId())){
                            ed.removeComponent(id, HoldingComponent.class);
                            setHoard(hoard+1);
                            getState(AudioState.class).playSound(AudioState.DROP_COIN);
                            chestTimer = chestOpenDur;
                        }
                    } else if(hoard > 0){
                        if(!id.equals(game.getPlayerId())){
                            setHoard(hoard-1);
                            pickupCoin(id);
                            chestTimer = chestOpenDur;
                        }
                    }
                }
            });
        }
        chestAnim((float)world.getStep().getDeltaTime());
    }
}
