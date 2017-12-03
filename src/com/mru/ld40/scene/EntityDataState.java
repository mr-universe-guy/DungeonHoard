/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.app.Application;
import com.simsilica.es.EntityData;
import com.simsilica.es.base.DefaultEntityData;
import com.simsilica.lemur.event.BaseAppState;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class EntityDataState extends BaseAppState{
    private EntityData ed;

    @Override
    protected void initialize(Application aplctn) {
        ed = new DefaultEntityData();
    }

    @Override
    protected void cleanup(Application aplctn) {
        ed.close();
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
    public EntityData getEntityData(){
        return ed;
    }
}
