package beardlessbrady.modcurrency.block.vending;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
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
        RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
        ItemStack itemStack = new ItemStack(Items.APPLE);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x+5, y+5, z);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();

        for(int i = 0; i < 5; i++){
           for(int j = 0; j < 5; j++){
                GlStateManager.translate(-1, 0, 0);
               // itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
            }
            GlStateManager.translate(+5, -1, 0);
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

}
