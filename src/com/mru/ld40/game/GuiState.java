/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.game;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.Axis;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.FillMode;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class GuiState extends BaseAppState{
    public Node uiNode = new Node("UI");
    private final int[] borders = new int[]{10,10,10,10};
    private Container info;

    @Override
    protected void initialize(Application aplctn) {
        GuiGlobals.initialize(aplctn);
        SimpleApplication app = (SimpleApplication)aplctn;
        app.getGuiNode().attachChild(uiNode);
        info = new Container(new BoxLayout(Axis.Y, FillMode.None));
        uiNode.attachChild(info);
    }
    
    public void addInfo(Panel p){
        info.addChild(p);
        info.setLocalTranslation(getWidth()-info.getPreferredSize().x-borders[2],
                getHeight()-borders[1], 0);
    }
    
    public boolean infoContains(Panel p){
        return info.hasChild(p);
    }
    
    public void clearInfo(){
        info.clearChildren();
    }

    @Override
    protected void cleanup(Application aplctn) {
        uiNode.removeFromParent();
        uiNode.detachAllChildren();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    public Vector3f centerPanel(Panel p){
        float x = (getWidth()/2f)-(p.getPreferredSize().x/2f);
        float y = (getHeight()/2f)-(p.getPreferredSize().y/2f);
        return new Vector3f(x,y,0);
    }
    
    public int getWidth(){
        return getApplication().getCamera().getWidth();
    }
    
    public int getHeight(){
        return getApplication().getCamera().getHeight();
    }
    
    public void attachToUI(Spatial s){
        uiNode.attachChild(s);
    }
    
    public int[] getBorders(){
        return borders;
    }
}
