/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import java.nio.FloatBuffer;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class ModelFactory {
    private final String MAT = "Common/MatDefs/Misc/Unshaded.j3md";
    private final AssetManager am;
    private static final float SIZEMULT = 1/10f;
    
    public ModelFactory(AssetManager am){
        this.am = am;
    }
    
    public Spatial loadEntity(String sprite){
        Texture tex = am.loadTexture(sprite);
        tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        Image im = tex.getImage();
        Mesh m = new Quad(im.getWidth()*SIZEMULT, im.getHeight()*SIZEMULT);
        Material mat = new Material(am, MAT);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.getAdditionalRenderState().setFaceCullMode(RenderState.FaceCullMode.Off);
        mat.setTexture("ColorMap", tex);
        Node center = new Node("SpriteNode");
        Geometry geo = new Geometry("Sprite", m);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        geo.setLocalTranslation(-im.getWidth()*SIZEMULT/2f, -im.getHeight()*SIZEMULT/2f, 0);
        geo.setMaterial(mat);
        center.attachChild(geo);
        return center;
    }
    
    public Spatial loadRepeating(String texture, float width, float height){
        Texture tex = am.loadTexture(texture);
        tex.setMinFilter(Texture.MinFilter.BilinearNoMipMaps);
        tex.setMagFilter(Texture.MagFilter.Nearest);
        tex.setWrap(Texture.WrapMode.Repeat);
        Image im = tex.getImage();
        Mesh m = new Quad(width, height);
        //write uv coords
        FloatBuffer fb = m.getFloatBuffer(VertexBuffer.Type.TexCoord);
        float[] floats = new float[fb.capacity()];
        fb.get(floats);
        for(int x=0; x<floats.length; x+=2){
            floats[x]*=(width/im.getWidth())/SIZEMULT;
            floats[x+1]*=(height/im.getHeight())/SIZEMULT;
        }
        m.setBuffer(VertexBuffer.Type.TexCoord, 2, floats);
        Material mat = new Material(am, MAT);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        mat.setTexture("ColorMap", tex);
        Node center = new Node("FixedNode");
        Geometry geo = new Geometry("BG", m);
        geo.setQueueBucket(RenderQueue.Bucket.Transparent);
        geo.setLocalTranslation(-width/2f, -height/2f, 0);
        geo.setMaterial(mat);
        center.attachChild(geo);
        return center;
    }
    
    public static float getSizeMult(){
        return SIZEMULT;
    }
}
