/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mru.ld40.scene;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author Matthew Universe <ff8loser@gmail.com>
 */
public class SpriteComponent implements EntityComponent{
    public static final String CHARA="sprite/Chara1.png";
    public static final String COIN="sprite/Coin.png";
    public static final String PILE="sprite/SmallPile.png";
    public static final String GOB="sprite/Gob.png";
    public static final String TORCH="sprite/Torch.png";
    private final String spriteImage;
    
    public SpriteComponent(String spriteImage){
        this.spriteImage = spriteImage;
    }
    
    public String getSpriteSheet(){
        return spriteImage;
    }
}
