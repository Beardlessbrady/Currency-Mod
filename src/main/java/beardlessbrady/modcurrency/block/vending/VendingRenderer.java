package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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

public class VendingRenderer extends TileEntitySpecialRenderer<TileVending> {
     @Override
    public void render(TileVending te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
         if (!te.hasWorld())
             return;

         RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
         if (te.getPos() != null && te.getPos().up() != null && ray.getBlockPos() != null) {
             if (ray.getBlockPos().equals(te.getPos()) || ray.getBlockPos().equals(te.getPos().up())) {

                 int rotation = 0;

                 RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

                 GlStateManager.pushMatrix();
                 GlStateManager.pushAttrib();
                 RenderHelper.enableStandardItemLighting();

                 switch (this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)) {
                     default:
                         GlStateManager.translate(x + 0.913, y + 1.77, z + 0.2);
                         break;
                     case EAST:
                         GlStateManager.translate(x + 0.813, y + 1.77, z + 0.913);
                         rotation = -90;
                         break;
                     case WEST:
                         GlStateManager.translate(x + 0.15, y + 1.77, z + 0.085);
                         rotation = 90;
                         break;
                     case SOUTH:
                         GlStateManager.translate(x + 0.087, y + 1.77, z + 0.813);
                         rotation = 180;
                         break;
                 }

                 GlStateManager.rotate(rotation, 0f, 1f, 0f);
                 GlStateManager.scale(0.13f, 0.13f, 0.13f);


                 ItemStack itemStack;
                 for (int i = 0; i < 5; i++) {
                     for (int j = 0; j < 5; j++) {
                        itemStack = te.getInvItemStack((j + (i*5)));

                        GlStateManager.translate(-0.9, 0, 0);
                        if(!itemStack.isEmpty()) itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);

                     }
                     GlStateManager.translate(4.5, -1.95, 0);
                 }
                 RenderHelper.disableStandardItemLighting();
                 GlStateManager.popAttrib();
                 GlStateManager.popMatrix();
             }
         }
     }
}
