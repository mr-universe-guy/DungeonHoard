/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.game;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.scene.Node;
import com.mru.ld40.animation.AnimationState;
import com.mru.ld40.physics.PhysicsState;
import com.mru.ld40.scene.CameraState;
import com.mru.ld40.scene.DecayState;
import com.mru.ld40.scene.EntityDataState;
import com.mru.ld40.scene.VisualState;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.SpringGridLayout;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class MenuState extends BaseAppState{
    private final Node helpMenu = new Node();
    private final Input input = new Input();
    
    @Override
    protected void initialize(Application aplctn) {
        GuiState gui = getState(GuiState.class);
        gui.clearInfo();
        Container text = new Container(new BoxLayout(Axis.Y, FillMode.None));
        Label title = new Label("Dungeon Hoard");
        title.setFontSize(24);
        title.setTextHAlignment(HAlignment.Center);
        text.addChild(title);
        Label intro = new Label("The magic in this dungeon is forming gold coins,"
                + " but you're not the only one who's noticed. Gobs are"
                + " coming to take these coins! Dispatch them with your trusty dagger"
                + " and collect as many coins as you can. The coins are very heavy"
                + " so you can only carry one at a time. Put the coins in the center"
                + " to collect them.");
        intro.setMaxWidth(300);
        text.addChild(intro);
        text.setLocalTranslation((gui.getWidth()/2f)-(text.getPreferredSize().x/2f),
                gui.getHeight()-20, 0);
        helpMenu.attachChild(text);
        Container controls = new Container(new SpringGridLayout(Axis.X, Axis.Y));
        AssetManager am = aplctn.getAssetManager();
        Panel la = new Panel(32,32);
        la.setBackground(new QuadBackgroundComponent(am.loadTexture("ui/ArrowKeyLeft.png")));
        controls.addChild(la,0,0);
        Panel ra = new Panel(32,32);
        ra.setBackground(new QuadBackgroundComponent(am.loadTexture("ui/ArrowKeyRight.png")));
        controls.addChild(ra,1,0);
        Label wl = new Label("   move Left/Right");
        controls.addChild(wl,2,0);
        Panel atk = new Panel(32, 32);
        atk.setBackground(new QuadBackgroundComponent(am.loadTexture("ui/ZKey.png")));
        controls.addChild(atk,0,1);
        Label atkl = new Label("   Attack");
        controls.addChild(atkl,2,1);
        Panel jmp = new Panel(32, 32);
        jmp.setBackground(new QuadBackgroundComponent(am.loadTexture("ui/XKey.png")));
        controls.addChild(jmp, 0, 2);
        Label jmpl = new Label("   Jump");
        controls.addChild(jmpl,2,2);
        Panel esc = new Panel(32, 32);
        esc.setBackground(new QuadBackgroundComponent(am.loadTexture("ui/ESCKey.png")));
        controls.addChild(esc, 0,3);
        Label escl = new Label("   Exit");
        controls.addChild(escl,2,3);
        controls.setLocalTranslation((gui.getWidth()/2f)-(controls.getPreferredSize().x/2f), 250, 0);
        helpMenu.attachChild(controls);
        
        String nl = System.lineSeparator();
        Label credits = new Label("Running in jMonkeyEngine v3.1.0"+nl
        +"External tools used: Krita & BFXR");
        credits.setLocalTranslation(0, credits.getPreferredSize().y, 0);
        helpMenu.attachChild(credits);
        
        Label credits2 = new Label("Made by Matthew Universe");
        credits2.setLocalTranslation(gui.getWidth()-credits2.getPreferredSize().x,
                credits2.getPreferredSize().y, 0);
        helpMenu.attachChild(credits2);
        
        gui.attachToUI(helpMenu);
    }

    @Override
    protected void cleanup(Application aplctn) {
        
    }

    @Override
    protected void enable() {
        input.registerInput(getApplication().getInputManager());
    }

    @Override
    protected void disable() {
        input.removeInput(getApplication().getInputManager());
    }
    
    private void startGame(){
        input.removeInput(getApplication().getInputManager());
        helpMenu.removeFromParent();
        AppStateManager sm = getStateManager();
        sm.detach(this);
        sm.attach(new EntityDataState());
        getApplication().enqueue(() -> {
            sm.attachAll(
                    new VisualState(),
                    new DecayState(),
                    new CameraState(),
                    new AnimationState(),
                    new PhysicsState()
            );
            sm.attach(new GameState());
        });
    }
    
    private class Input extends AbstractRIL{
        private void removeInput(InputManager im){
            im.removeRawInputListener(this);
        }
        private void registerInput(InputManager im){
            im.addRawInputListener(this);
        }

        @Override
        public void onKeyEvent(KeyInputEvent kie) {
            if(kie.isPressed()){
                kie.setConsumed();
                startGame();
            }
        }
    }
}
