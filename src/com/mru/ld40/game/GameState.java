/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.game;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.math.ColorRGBA;
import com.mru.ld40.animation.AnimatedComponent;
import com.mru.ld40.animation.AnimationState;
import com.mru.ld40.audio.AudioState;
import com.mru.ld40.coin.CoinState;
import com.mru.ld40.health.HealthComponent;
import com.mru.ld40.health.HealthState;
import com.mru.ld40.physics.ForceState;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.physics.RigidBodyComponent;
import com.mru.ld40.physics.VelocityComponent;
import com.mru.ld40.scene.BackgroundComponent;
import com.mru.ld40.scene.CameraState;
import com.mru.ld40.scene.ColorComponent;
import com.mru.ld40.scene.DecayState;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.FacingComponent;
import com.mru.ld40.scene.ModelFactory;
import com.mru.ld40.scene.MoveState;
import com.mru.ld40.scene.SpriteComponent;
import com.mru.ld40.scene.TransformComponent;
import com.mru.ld40.scene.VisualState;
import com.mru.ld40.unit.AttackComponent;
import com.mru.ld40.unit.AttackDriver;
import com.mru.ld40.unit.AttackState;
import com.mru.ld40.unit.CanCarry;
import com.mru.ld40.unit.GoblinState;
import com.mru.ld40.unit.JumpComponent;
import com.mru.ld40.unit.JumpDriver;
import com.mru.ld40.unit.JumpState;
import com.mru.ld40.unit.JumpedComponent;
import com.mru.ld40.unit.WalkComponent;
import com.mru.ld40.unit.WalkDriver;
import com.mru.ld40.unit.WalkState;
import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.es.WatchedEntity;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.BaseAppState;
import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class GameState extends BaseAppState{
    private final float terrainWidth = 150;
    private Input input;
    private EntityData ed;
    private WatchedEntity player;
    private PlayerAction curAction;
    private long oldHealth = 0;
    private Label hpLabel, dead;
    private float elapsed = 0;
    
    @Override
    protected void initialize(Application aplctn) {
        System.out.println("Hello LD40 :D");
        ed = getState(EntityDataState.class).getEntityData();
        player = ed.watchEntity(spawnPlayer(), TransformComponent.class, WalkDriver.class,
                JumpDriver.class, AttackDriver.class, JumpedComponent.class, VelocityComponent.class,
                HealthComponent.class);
        hpLabel = new Label("");
        hpLabel.setTextHAlignment(HAlignment.Right);
        dead = new Label("");
        getState(GuiState.class).addInfo(hpLabel);
        input = new Input();
        input.registerInput(aplctn.getInputManager());
        getState(ScreenshotAppState.class).setFilePath(System.getProperty("user.dir"));
        //cam
        getState(CameraState.class).setTarget(player.getId());
        //terrain
        spawnFloor();
        for(float x = -terrainWidth/2; x < terrainWidth/2; x+=15){
            if(x != 0){
                spawnTorch(x);
            }
        }
        for(int y=0; y<10; y++){
            float mult = 60f;
            ColorRGBA col = new ColorRGBA(y/mult,y/mult,y/mult,1);
            spawnBgFade(y, col,-5);
        }
        //set states
        getStateManager().attachAll(
                new WalkState(),
                new HealthState(),
                new AttackState(),
                new MoveState(),
                new ForceState(),
                new CoinState(),
                new GoblinState(),
                new JumpState()
        );
    }
    
    private EntityId spawnTorch(float xpos){
        EntityId torch = ed.createEntity();
        TransformComponent trans = new TransformComponent(xpos, 0.5f, -0.25f);
        SpriteComponent sprite = new SpriteComponent(SpriteComponent.TORCH);
        FacingComponent facing = new FacingComponent(1);
        AnimatedComponent anim = new AnimatedComponent(AnimatedComponent.TORCH, 2,1,2,1f, false);
        ed.setComponents(torch, trans, sprite, anim, facing);
        return torch;
    }
    
    private EntityId spawnBgFade(int yPos, ColorRGBA col, float depth){
        EntityId bg = ed.createEntity();
        float sm = ModelFactory.getSizeMult();
        TransformComponent trans = new TransformComponent(0, yPos*32*sm, depth);
        BackgroundComponent bc = new BackgroundComponent(BackgroundComponent.GRADIENT, terrainWidth*2, 32*sm);
        ColorComponent color = new ColorComponent(col);
        ed.setComponents(bg, trans, bc, color);
        return bg;
    }
    
    private EntityId spawnFloor(){
        EntityId floor = ed.createEntity();
        float height = 150;
        Filter cat = new CategoryFilter(PhysicsState.FLOOR, PhysicsState.GOBLIN|PhysicsState.PLAYER);
        RigidBodyComponent rb = new RigidBodyComponent(0,cat, new Rectangle(terrainWidth, height));
        TransformComponent trans = new TransformComponent(0, (-height/2f)-1f, -2f);
        BackgroundComponent bg = new BackgroundComponent(BackgroundComponent.COBBLESTONE, terrainWidth, height);
        ed.setComponents(floor, trans, bg, rb);
        return floor;
    }
    
    private EntityId spawnPlayer(){
        EntityId id = ed.createEntity();
        TransformComponent trans = new TransformComponent(0,5,2f);
        SpriteComponent sprite = new SpriteComponent(SpriteComponent.CHARA);
        WalkComponent walk = new WalkComponent(10f);
        WalkDriver wd = new WalkDriver(0);
        JumpComponent jump = new JumpComponent(50f);
        JumpDriver jd = new JumpDriver(false);
        Filter cat = new CategoryFilter(PhysicsState.PLAYER, PhysicsState.FLOOR|PhysicsState.GOBLIN);
        RigidBodyComponent rb = new RigidBodyComponent(1,cat, new Rectangle(1.6,3.2));
        FacingComponent facing = new FacingComponent(1);
        CanCarry ccw = new CanCarry();
        AttackComponent attack = new AttackComponent(1, 1.5f);
        AttackDriver ad = new AttackDriver(false);
        HealthComponent health = new HealthComponent(10);
        VelocityComponent vel = new VelocityComponent(new Vector2(0,0));
        curAction = PlayerAction.IDLE;

        ed.setComponents(id, trans, sprite, rb, walk, wd, jump, jd, ccw, facing,
                attack, health, ad, vel);
        return id;
    }
    
    public EntityId getPlayerId(){
        return player.getId();
    }
    
    public void showEndScreen(){
        CoinState cs = getState(CoinState.class);
        String nl = System.lineSeparator();
        dead.setText("You have died!"+nl+
                "You had "+cs.getHoard()+" coins in your hoard when you died."+nl+
                "The biggest your hoard ever got was "+cs.getPeak()+" coins.");
        GuiState gui = getState(GuiState.class);
        dead.setLocalTranslation(gui.centerPanel(dead));
        gui.attachToUI(dead);
        setEnabled(false);
        getApplication().getInputManager().addRawInputListener(new AbstractRIL(){
            @Override
            public void onKeyEvent(KeyInputEvent kie) {
                if(kie.isPressed()){
                    kie.setConsumed();
                    getApplication().getInputManager().removeRawInputListener(this);
                    getStateManager().detach(GameState.this);
                }
            }
        });
    }
    
    @Override
    public void update(float tpf){
        elapsed += tpf;
        if(player.get(TransformComponent.class).getPos().y < -10){
            //player fell off the world and died
            curAction = PlayerAction.DEAD;
        }
        player.applyChanges();
        long health = player.get(HealthComponent.class).getHealth();
        if(health <= 0){
            oldHealth = health;
            hpLabel.setText(oldHealth+" : HP");
            curAction = PlayerAction.DEAD;
            //you are dead
        }else if(oldHealth > health){
            //player hurt
            getState(AudioState.class).playSound(AudioState.CHARA_HURT);
            oldHealth = health;
            hpLabel.setText(oldHealth+" : HP");
        } else if(oldHealth < health){
            //player was healed.. do somethin?
            oldHealth = health;
            hpLabel.setText(oldHealth+" : HP");
        }
        //get interrupts first
        EntityId id = player.getId();
        if(player.get(JumpedComponent.class) != null){
            //player has jumped
            curAction = PlayerAction.JUMP;
            ed.removeComponent(id, JumpedComponent.class);
        }
        Vector2 vel = player.get(VelocityComponent.class).getVelocity();
        switch(curAction){
            case IDLE:
                if(input.dir != 0){
                    curAction = PlayerAction.WALK;
                }
                player.set(new AnimatedComponent(AnimatedComponent.CHARA_IDLE, 1, 1, 1, -1, false));
                break;
            case WALK:
                if(input.dir == 0){
                    curAction = PlayerAction.IDLE;
                }
                player.set(new AnimatedComponent(AnimatedComponent.CHARA_WALK, 2, 2, 4, 0.5f, false));
                break;
            case JUMP:
                //run jump animation so long as moving up vertically
                if(vel.y >= 0){
                    player.set(new AnimatedComponent(AnimatedComponent.CHARA_JMP, 1, 1, 1, -1, false));
                } else{
                    curAction = PlayerAction.WALK;
                }
                break;
            case ATTACK:
                player.set(new AnimatedComponent(AnimatedComponent.CHARA_ATK, 2, 2, 3, 0.25f, false));
                if(elapsed > 0.25f){
                    curAction = PlayerAction.IDLE;
                }
                break;
            case DEAD:
                ed.removeComponent(id, AttackComponent.class);
                ed.removeComponent(id, WalkComponent.class);
                ed.removeComponent(id, JumpComponent.class);
                ed.removeComponent(id, RigidBodyComponent.class);
                ed.setComponent(id, new AnimatedComponent(AnimatedComponent.CHARA_DEAD, 1, 1, 1, 1, false));
                showEndScreen();
                break;
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        player.release();
        input.removeInput(aplctn.getInputManager());
        AppStateManager sm = getStateManager();
        sm.detach(sm.getState(WalkState.class));
        sm.detach(sm.getState(HealthState.class));
        sm.detach(getState(AttackState.class));
        sm.detach(sm.getState(MoveState.class));
        sm.detach(sm.getState(ForceState.class));
        sm.detach(sm.getState(CoinState.class));
        sm.detach(sm.getState(GoblinState.class));
        sm.detach(sm.getState(JumpState.class));
        sm.detach(sm.getState(VisualState.class));
        sm.detach(sm.getState(DecayState.class));
        sm.detach(sm.getState(CameraState.class));
        sm.detach(sm.getState(AnimationState.class));
        sm.detach(sm.getState(PhysicsState.class));
        sm.detach(sm.getState(EntityDataState.class));
        hpLabel.removeFromParent();
        dead.removeFromParent();
        getStateManager().attach(new MenuState());
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    private class Input{
        private static final String MOVE_LEFT="GAME_MOVE_LEFT",
                MOVE_RIGHT="GAME_MOVE_RIGHT",
                MOVE_JUMP="Game_MOVE_JUMP",
                ACTION_ATTACK="GAME_ACTION_ATTACK",
                GAME_RECORD="GAME_RECORD_VIDEO",
                GAME_SCREENSHOT="GAME_RECORD_SCREENSHOT";
        private float dir;
        
        private final ActionListener action = (String name, boolean isPressed, float f) -> {
            if(name.matches(MOVE_LEFT)){
                dir = 0;
                if(isPressed){
                    dir = -1;
                }
                ed.setComponents(player.getId(), new WalkDriver(dir),
                        new FacingComponent(FacingComponent.LEFT));
            } else if(name.matches(MOVE_RIGHT)){
                dir = 0;
                if(isPressed){
                    dir = 1;
                }
                ed.setComponents(player.getId(), new WalkDriver(dir),
                        new FacingComponent(FacingComponent.RIGHT));
            }
            if(name.matches(MOVE_JUMP)){
                ed.setComponent(player.getId(), new JumpDriver(isPressed));
            }
            if(name.matches(ACTION_ATTACK)){
                if(isPressed){
                    ed.setComponent(player.getId(), new AttackDriver(true));
                    elapsed = 0;
                    curAction = PlayerAction.ATTACK;
                }
            }
            if(name.matches(GAME_SCREENSHOT) && isPressed){
                getState(ScreenshotAppState.class).takeScreenshot();
                System.out.println("Snap");
            }
        };
        
        private final AnalogListener analog = (String name, float f, float f1) -> {
        };
        
        private void registerInput(InputManager im){
            im.addMapping(MOVE_LEFT, new KeyTrigger(KeyInput.KEY_LEFT));
            im.addMapping(MOVE_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT));
            im.addMapping(MOVE_JUMP, new KeyTrigger(KeyInput.KEY_X));
            im.addMapping(ACTION_ATTACK, new KeyTrigger(KeyInput.KEY_Z));
            im.addMapping(GAME_RECORD, new KeyTrigger(KeyInput.KEY_F10));
            im.addMapping(GAME_SCREENSHOT, new KeyTrigger(KeyInput.KEY_F12));
            im.addListener(action, MOVE_LEFT, MOVE_RIGHT, MOVE_JUMP, ACTION_ATTACK,
                    GAME_RECORD, GAME_SCREENSHOT);
            im.addListener(analog, MOVE_LEFT, MOVE_RIGHT, MOVE_JUMP, ACTION_ATTACK,
                    GAME_RECORD, GAME_SCREENSHOT);
        }
        
        private void removeInput(InputManager im){
            im.removeListener(action);
            im.removeListener(analog);
            im.deleteMapping(MOVE_LEFT);
            im.deleteMapping(MOVE_RIGHT);
            im.deleteMapping(MOVE_JUMP);
            im.deleteMapping(ACTION_ATTACK);
            im.deleteMapping(GAME_RECORD);
            im.deleteMapping(GAME_SCREENSHOT);
        }
    }
    
    private enum PlayerAction{
        WALK,
        JUMP,
        ATTACK,
        JUMP_ATK,
        WALK_ATK,
        WOUNDED,
        COIN,
        DEAD,
        IDLE;
    }
}
