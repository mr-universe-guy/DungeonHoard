/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class TransformComponent implements EntityComponent{
    private final Vector3f pos;
    
    public TransformComponent(Vector3f pos){
        this.pos = pos;
    }
    
    public TransformComponent(float x, float y, float z){
        this(new Vector3f(x, y, z));
    }
    
    public Vector3f getPos(){
        return pos;
    }
    
    @Override
    public String toString(){
        return "Transform : "+pos;
    }
}
