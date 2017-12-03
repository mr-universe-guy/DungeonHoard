/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.game;

import com.jme3.app.Application;
import com.simsilica.lemur.HAlignment;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class FpsState extends BaseAppState{
    private int frames = 0;
    private float timer = 0;
    private Label framesLabel;

    @Override
    protected void initialize(Application aplctn) {
        framesLabel = new Label("");
        framesLabel.setTextHAlignment(HAlignment.Right);
        getState(GuiState.class).addInfo(framesLabel);
    }
    
    @Override
    public void update(float tpf){
        timer += tpf;
        frames++;
        if(timer >= 1){
            GuiState gui = getState(GuiState.class);
            if(!gui.infoContains(framesLabel)) gui.addInfo(framesLabel);
            framesLabel.setText(frames+" : fps");
            timer = 0;
            frames = 0;
        }
    }

    @Override
    protected void cleanup(Application aplctn) {
        framesLabel.removeFromParent();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
