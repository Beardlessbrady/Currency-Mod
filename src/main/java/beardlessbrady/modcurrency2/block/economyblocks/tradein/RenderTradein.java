package beardlessbrady.modcurrency2.block.economyblocks.tradein;

import beardlessbrady.modcurrency2.block.ModBlocks;
import beardlessbrady.modcurrency2.handler.StateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

import java.awt.*;
import java.util.Stack;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-15
 */

public class RenderTradein extends TileEntitySpecialRenderer<TileTradein> {
     @Override
     public void render(TileTradein te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
         if (!te.hasWorld())
             return;

         RayTraceResult ray = Minecraft.getMinecraft().objectMouseOver;
         if (ray != null) {
             if (te.getPos() != null && te.getPos().up() != null) {
                 if(ray.getBlockPos() != null) {
                     if (getWorld().getBlockState(te.getPos().up()).getBlock() == ModBlocks.blockTradein) {
                         if (ray.getBlockPos().equals(te.getPos()) || ray.getBlockPos().equals(te.getPos().up())) {

                             //Rendering Text
                             GlStateManager.pushMatrix();
                             GlStateManager.pushAttrib();
                             GlStateManager.translate(x + 0.25, y + 1.55, z + 0.9);
                             GlStateManager.scale(0.01, 0.01, 0.01);
                             GlStateManager.rotate(180, 1, 0, 0);

                             //Transforms TEXT based on which direction block is facing
                             int rotation = 0;
                             switch (this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)) {
                                 default:
                                     GlStateManager.translate(x + 50, 0, z + 78);
                                     rotation = 180;
                                     break;
                                 case EAST:
                                     GlStateManager.translate(x + 69, 0, z + 14);
                                     rotation = -90;
                                     break;
                                 case WEST:
                                     GlStateManager.translate(x - 18, 0, z + 67);
                                     rotation = 90;
                                     break;
                                 case SOUTH:
                             }
                             GlStateManager.rotate(rotation, 0f, 1f, 0f);


                             FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
                             fontRenderer.drawString("ITEMS", 0, 0, Color.GRAY.getRGB());
                             fontRenderer.drawString("FOR", 4, 10, Color.GRAY.getRGB());
                             GlStateManager.scale(2, 3, 1);
                             fontRenderer.drawString("$$$", -2, 6, Color.GREEN.getRGB());


                             GlStateManager.popAttrib();
                             GlStateManager.popMatrix();


                             //Render Items
                             rotation = 0;
                             RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

                             GlStateManager.pushMatrix();
                             GlStateManager.pushAttrib();
                             RenderHelper.enableStandardItemLighting();

                             //Transforms for ITEMS based on which direction block is facing
                             switch (this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)) {
                                 default:
                                     GlStateManager.translate(x + 0.34, y + 1.05, z + 0.13);
                                     break;
                                 case EAST:
                                     GlStateManager.translate(x + 0.88, y + 1.05, z + 0.35);
                                     rotation = -90;
                                     break;
                                 case WEST:
                                     GlStateManager.translate(x + 0.13, y + 1.05, z + 0.68);
                                     rotation = 90;
                                     break;
                                 case SOUTH:
                                     GlStateManager.translate(x + 0.68, y + 1.05, z + 0.88);
                                     rotation = 180;
                                     break;
                             }

                             GlStateManager.rotate(rotation, 0f, 1f, 0f);
                             GlStateManager.scale(0.16f, 0.16f, 0.16f);

                             //Grabs all items in machine
                             Stack<ItemStack> items = new Stack();
                             for (int i = 0; i < 5; i++) {
                                 for (int j = 0; j < 5; j++) {
                                     ItemTradein item = te.getItemTradein((j + (i * 5)));
                                     if (!item.getStack().isEmpty())
                                         items.push(te.getItemTradein((j + (i * 5))).getStack());
                                 }
                             }

                             //If items <= 5 just render not moving, otherwise animate
                             int size = items.size();
                             if (size == 1) {
                                 GlStateManager.translate(+1.7, 0, 0);
                                 itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.FIXED);
                             } else if (size == 2) {
                                 GlStateManager.translate(+2.1, 0, 0);
                                 itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(1), ItemCameraTransforms.TransformType.FIXED);
                             } else if (size == 3) {
                                 GlStateManager.translate(+2.5, 0, 0);
                                 itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(1), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(2), ItemCameraTransforms.TransformType.FIXED);
                             } else if (size == 4) {
                                 GlStateManager.translate(+2.8, 0, 0);
                                 itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(1), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(2), ItemCameraTransforms.TransformType.FIXED);
                                 GlStateManager.translate(-0.7, 0, 0);
                                 itemRenderer.renderItem(items.get(3), ItemCameraTransforms.TransformType.FIXED);
                             } else if (items.size() >= 5) {
                                 double count = te.getRenderCounter();
                                 GlStateManager.translate(-count * 0.03, 0, 0); //Causes movement via taking the counter from TE (increments every time its accessed)

                                 for (int i = 0; i < items.size() + 5; i++) {
                                     int k = i;
                                     if (i > items.size() - 1)
                                         k = i - items.size();
                                     GlStateManager.translate(+0.8, 0, 0);

                                     if ((i < 4 || count > 34 + (26.6 * (i - 5))) && count < 30 + (30 * (i))) //Ensures items are only drawn inside the machine screen
                                         itemRenderer.renderItem(items.get(k), ItemCameraTransforms.TransformType.FIXED);
                                 }

                                 if (count >= items.size() * 26.6) //Ensures the animation at the end of the counter aligns with the beginning
                                     te.setRenderCounter(0);
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
}
