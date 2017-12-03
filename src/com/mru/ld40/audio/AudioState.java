/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.audio;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioNode;
import com.jme3.scene.Node;
import com.simsilica.lemur.event.BaseAppState;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class AudioState extends BaseAppState{
    public static final String PICKUP_COIN="AUDIO_COIN_PICKUP",
            DROP_COIN="AUDIO_COIN_DROP",
            CHARA_JUMP="AUDIO_CHARA_JUMP",
            CHARA_HURT="AUDIO_CHARA_HURT",
            SWORD_HIT="AUDIO_SWORD_HIT",
            SWORD_SWING="AUDIO_SWORD_SWING";
    private final Node audioNode = new Node("Audio");
    private AssetManager am;
    private final Map<String, AudioNode> audioMap = new HashMap<>();

    @Override
    protected void initialize(Application aplctn) {
        SimpleApplication app = (SimpleApplication)aplctn;
        app.getRootNode().attachChild(audioNode);
        am = aplctn.getAssetManager();
        //default sounds
        addAudio("audio/Pickup_Coin.wav", PICKUP_COIN);
        addAudio("audio/Droppoff_Coin.wav", DROP_COIN);
        addAudio("audio/Sword_Swing.wav", SWORD_SWING);
        addAudio("audio/Sword_Hit.wav", SWORD_HIT);
        addAudio("audio/Chara_Jump.wav", CHARA_JUMP);
        addAudio("audio/Chara_Hurt.wav", CHARA_HURT);
    }

    @Override
    protected void cleanup(Application aplctn) {
        
    }
    
    public void playSound(String audioKey){
        AudioNode an = audioMap.get(audioKey);
        an.playInstance();
    }
    
    public void addAudio(String asset, String audioKey){
        AudioNode an = new AudioNode(am, asset, AudioData.DataType.Buffer);
        audioMap.put(audioKey, an);
        audioNode.attachChild(an);
    }

    @Override
    protected void enable() {
        
    }

    @Override
    protected void disable() {
        
    }
    
}
