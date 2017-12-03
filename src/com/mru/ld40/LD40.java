/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.system.AppSettings;
import com.mru.ld40.audio.AudioState;
import com.mru.ld40.game.FpsState;
import com.mru.ld40.game.GuiState;
import com.mru.ld40.game.MenuState;

/**
 * Created for LD40 "The more you have the worse it is"
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class LD40 extends SimpleApplication{
    public static final String TITLE = "Dungeon Hoard";
    
    private LD40(){
        super(
                new GuiState(),
                new FpsState(),
                new AudioState(),
                new ScreenshotAppState(),
                new MenuState());
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LD40 app = new LD40();
        AppSettings settings = new AppSettings(true);
        settings.setTitle(TITLE);
        settings.setVSync(true);
        app.setShowSettings(false);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        
    }
    
}
