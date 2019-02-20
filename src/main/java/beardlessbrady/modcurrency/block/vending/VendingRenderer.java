package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-15
 */

public class VendingRenderer extends TileEntitySpecialRenderer<TileVending> {

    @Override
    public void render(TileVending te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        if(!te.hasWorld())
            return;


        int rotation = 0;
        switch(this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)){
            case EAST:
                rotation = -90;
                break;
            case WEST:
                rotation = 90;
                break;
            case NORTH:
                rotation = 0;
                break;
            case SOUTH:
                rotation = 180;
                break;
        }

        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        ItemStack itemStack = new ItemStack(Items.APPLE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.913, y + 1.77, z + 0.2);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(rotation, 0f, 1f, 0f);
        GlStateManager.scale(0.13f, 0.13f, 0.13f);

        for(int i = 0; i < 5; i++){
           for(int j = 0; j < 5; j++){
               GlStateManager.translate(-0.9, 0, 0);
               itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);

               for(int k = 0; k < 5; k++){
                   GlStateManager.translate(0, 0, +0.5);
                   itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
               }
               GlStateManager.translate(0, 0, -2.5);
            }
            GlStateManager.translate(4.5, -2, 0);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

}
