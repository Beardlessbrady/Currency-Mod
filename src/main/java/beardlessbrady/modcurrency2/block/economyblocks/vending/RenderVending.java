package beardlessbrady.modcurrency2.block.economyblocks.vending;

import beardlessbrady.modcurrency2.handler.StateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-15
 */

public class RenderVending extends TileEntitySpecialRenderer<TileVending> {
     @Override
     public void render(TileVending te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
         if (!te.hasWorld())
             return;

         RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
         if (ray != null) {
             if (te.getPos() != null && te.getPos().up() != null) {
                 if(ray.getBlockPos() != null) {
                     if (ray.getBlockPos().equals(te.getPos()) || ray.getBlockPos().equals(te.getPos().up())) {

                         int rotation = 0;

                         RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

                         GlStateManager.pushMatrix();
                         GlStateManager.pushAttrib();
                         RenderHelper.enableStandardItemLighting();

                         switch (this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)) {
                             default:
                                 GlStateManager.translate(x + 0.93, y + 1.5, z + 0.1);
                                 break;
                             case EAST:
                                 GlStateManager.translate(x + 0.9, y + 1.5, z + 0.93);
                                 rotation = -90;
                                 break;
                             case WEST:
                                 GlStateManager.translate(x + 0.1, y + 1.5, z + 0.07);
                                 rotation = 90;
                                 break;
                             case SOUTH:
                                 GlStateManager.translate(x + 0.07, y + 1.5, z + 0.9);
                                 rotation = 180;
                                 break;
                         }

                         GlStateManager.rotate(rotation, 0f, 1f, 0f);
                         GlStateManager.scale(0.16f, 0.16f, 0.16f);


                         ItemStack itemStack;
                         for (int i = 0; i < 5; i++) {
                             for (int j = 0; j < 5; j++) {
                                 itemStack = te.getItemVendor((j + (i * 5))).getStack();

                                 GlStateManager.translate(-0.7, 0, 0);
                                 if (!itemStack.isEmpty())
                                     itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);

                             }
                             GlStateManager.translate(3.5, -1.8, 0);
                         }
                         RenderHelper.disableStandardItemLighting();
                         GlStateManager.popAttrib();
                         GlStateManager.popMatrix();
                     }
                 }
             }
         }
     }
}
