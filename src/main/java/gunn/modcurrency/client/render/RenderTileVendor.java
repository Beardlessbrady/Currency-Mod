package gunn.modcurrency.client.render;

import gunn.modcurrency.common.tiles.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.animation.FastTESR;
import scala.Array;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-28
 */
public class RenderTileVendor extends FastTESR<TileVendor> {

    @Override
    public void renderTileEntityFast(@Nonnull TileVendor te, double x, double y, double z, float partialTicks, int destroyStage, @Nonnull VertexBuffer VertexBuffer) {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();

        int facing = te.getField(7); //0 = North, 1 = East, 2 = South, 3 = West

        //Ensures starting position is same for every facing direction
        switch(facing){
            case 0:
                GlStateManager.translate(x-0.05, y+0.1, z+0.25);
                GlStateManager.rotate(180,0,1,0);
                break;
            case 1:
                GlStateManager.translate(x+0.75, y+0.1, z-0.05);
                GlStateManager.rotate(90,0,1,0);
                break;
            case 2:
                GlStateManager.translate(x+1.05, y+0.1, z+0.75);
                break;
            case 3:
                GlStateManager.translate(x+0.25, y+0.1, z+1.05);
                GlStateManager.rotate(270,0,1,0);
                break;
        }
        renderItems(te,x,y,z,partialTicks,destroyStage, VertexBuffer);
        GlStateManager.popMatrix();
    }

    public void renderItems(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage, VertexBuffer vertexBuffer) {
        Double xStart = -2.9;
        Double yStart = 5.6;
        Double xTrans = 0.45;
        Double yTrans = -0.7;

        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 5; row++) {
                for(int amnt = 0; amnt < 1; amnt++) {
                    GlStateManager.scale(0.30, 0.30, 0.30);
                    GlStateManager.translate(xStart + (xTrans * row), yStart + (yTrans * column), -0.5 - (0.2 * amnt ));

                    ItemStack poop = new ItemStack(Items.DIAMOND_CHESTPLATE);

                    List<BakedQuad> bakedQuadList = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(poop, getWorld(), null).getQuads(getWorld().getBlockState(te.getPos()), null, 0);

                    for(int i =0; i < bakedQuadList.size(); i++) {
                        GlStateManager.pushMatrix();
                        vertexBuffer.addVertexData(bakedQuadList.get(i).getVertexData());

                    }
                    GlStateManager.popMatrix();
                }

            }
        }
        GlStateManager.popMatrix();
    }


}