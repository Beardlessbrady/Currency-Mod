package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.block.ModBlocks;
import beardlessbrady.modcurrency.block.vending.TileVending;
import beardlessbrady.modcurrency.handler.StateHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;

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
         if (te.getPos() != null && te.getPos().up() != null && ray.getBlockPos() != null) {
             if(getWorld().getBlockState(te.getPos().up()).getBlock() == ModBlocks.blockTradein) {
                 if (ray.getBlockPos().equals(te.getPos()) || ray.getBlockPos().equals(te.getPos().up())) {

                     int rotation = 0;
                     RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

                     GlStateManager.pushMatrix();
                     GlStateManager.pushAttrib();
                     RenderHelper.enableStandardItemLighting();

                     switch (this.getWorld().getBlockState(te.getPos()).getValue(StateHandler.FACING)) {
                         default:
                             GlStateManager.translate(x + 0.34, y + 1.05, z + 0.1);
                             break;
                         case EAST:
                             GlStateManager.translate(x + 0.9, y + 1.05, z + 0.35);
                             rotation = -90;
                             break;
                         case WEST:
                             GlStateManager.translate(x + 0.13, y + 1.05, z + 0.68);
                             rotation = 90;
                             break;
                         case SOUTH:
                             GlStateManager.translate(x + 0.68, y + 1.05, z + 0.9);
                             rotation = 180;
                             break;
                     }

                     GlStateManager.rotate(rotation, 0f, 1f, 0f);
                     GlStateManager.scale(0.16f, 0.16f, 0.16f);

                     Stack<ItemStack> items = new Stack();
                     for (int i = 0; i < 5; i++) {
                         for (int j = 0; j < 5; j++) {
                             ItemTradein item = te.getItemTradein((j + (i*5)));
                             if(!item.getStack().isEmpty())
                                items.push(te.getItemTradein((j + (i*5))).getStack());
                         }
                     }

                     int size = items.size();
                     if(size == 1){
                         GlStateManager.translate(+0.8, 0, 0 - 0.8);
                         itemRenderer.renderItem(items.get(0), ItemCameraTransforms.TransformType.FIXED);
                     }else if(size == 2){

                     }else if(size == 3){

                     }else if(size == 4){

                     }else if(size == 5){

                     }else if(items.size() > 5) {
                         double count = te.getRenderCounter();
                         GlStateManager.translate(-count * 0.03, 0, 0); //Causes movement via taking the counter from TE (increments every time its accessed)

                         for (int i = 0; i < items.size() + 5; i++) {
                             int k = i;
                             if(i > items.size() -1)
                                 k = i - items.size();
                             GlStateManager.translate(+0.8, 0, 0);

                             if((i < 4 || count > 34 + (26.6*(i-5))) && count < 30 + (30*(i))) //Ensures items are only drawn inside the machine screen
                                     itemRenderer.renderItem(items.get(k), ItemCameraTransforms.TransformType.FIXED);
                         }

                         if(count >= items.size()*26.6) //Ensures the animation at the end of the counter aligns with the beginning
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
