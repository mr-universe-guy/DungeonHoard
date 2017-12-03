/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.animation;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class AnimatedComponent implements EntityComponent{
    public static final String COIN="sprite/CoinSheet.png";
    public static final String CHEST_OPEN_FULL="sprite/TreasureChestOpenFull.png";
    public static final String CHEST_OPEN_EMPTY="sprite/TreasureChestOpenEmpty.png";
    public static final String CHEST_CLOSED="sprite/TreasureChestClosed.png";
    public static final String CHARA_ATK="sprite/CharaAtk.png";
    public static final String CHARA_JMP="sprite/CharaJump1.png";
    public static final String CHARA_WALK="sprite/CharaWalk.png";
    public static final String CHARA_IDLE="sprite/CharaIdle1.png";
    public static final String CHARA_DEAD="sprite/CharaDead.png";
    public static final String GOB_DEAD="sprite/Gob_Dead.png";
    public static final String GOB_WALK="sprite/GobWalk.png";
    public static final String GOB_COIN="sprite/GobCoin.png";
    public static final String TORCH="sprite/TorchAnim.png";
    private final String texture;
    private final int x,y, max;
    private final float dur;
    private final boolean reset;
    
    public AnimatedComponent(String texture, int x, int y, int max, float dur, boolean reset){
        this.texture = texture;
        this.dur = dur;
        this.x = x;
        this.y = y;
        this.max = max;
        this.reset = reset;
    }
    
    public String getTexture(){
        return texture;
    }
    
    public float getDuration(){
        return dur;
    }
    
    public int getXSplit(){
        return x;
    }
    
    public int getYSplit(){
        return y;
    }
    
    public int getMax(){
        return max;
    }
    
    public boolean isReset(){
        return reset;
    }
}
