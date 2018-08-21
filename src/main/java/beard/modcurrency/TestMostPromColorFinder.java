package beard.modcurrency;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-25
 */
public class TestMostPromColorFinder {

    public void getColor(ItemStack itemStack){
        TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(itemStack).getParticleTexture();
        BufferedImage bufferedImage = new BufferedImage(textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight() * textureAtlasSprite.getFrameCount(), BufferedImage.TYPE_4BYTE_ABGR);
        for (int i = 0; i < textureAtlasSprite.getFrameCount(); i++) {
            int[][] frameTextureData = textureAtlasSprite.getFrameTextureData(i);
            int[] largestMipMapTextureData = frameTextureData[0];
            bufferedImage.setRGB(0, i * textureAtlasSprite.getIconHeight(), textureAtlasSprite.getIconWidth(), textureAtlasSprite.getIconHeight(), largestMipMapTextureData, 0, textureAtlasSprite.getIconWidth());
        }

        for(int i = 0; i < 16; i++) {
            for(int j = 0; j < 16; j++) {
                int r = (bufferedImage.getRGB(i, j) & 0x00ff0000) >> 16;
                int g = (bufferedImage.getRGB(i, j) & 0x0000ff00) >> 8;
                int b = bufferedImage.getRGB(i, j) & 0x000000ff;

                String hexColor = String.format( "#%02x%02x%02x", r, g, b );
                System.out.println(hexColor);
            }
        }
    }


}
