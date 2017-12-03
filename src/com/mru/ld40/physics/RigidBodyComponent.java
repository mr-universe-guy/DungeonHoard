/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.physics;

import com.simsilica.es.EntityComponent;
import org.dyn4j.collision.Filter;
import org.dyn4j.geometry.Convex;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class RigidBodyComponent implements EntityComponent{
    private final Convex[] shapes;
    private final double mass;
    private final Filter filter;
    
    public RigidBodyComponent(double mass, Filter filter, Convex... shapes){
        this.mass = mass;
        this.filter = filter;
        this.shapes = shapes;
    }
    
    public Convex[] getShapes(){
        return shapes;
    }
    
    public double getMass(){
        return mass;
    }
    
    public Filter getFilter(){
        return filter;
    }
}
