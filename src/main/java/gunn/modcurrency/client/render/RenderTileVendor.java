package gunn.modcurrency.client.render;

import gunn.modcurrency.common.blocks.ModBlocks;
import gunn.modcurrency.common.tiles.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2017-01-28
 */
public class RenderTileVendor extends TileEntitySpecialRenderer<TileVendor> {

    @Override
    public void renderTileEntityAt(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage) {
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

        renderItems(te,x,y,z,partialTicks,destroyStage);
        GlStateManager.popMatrix();
    }


    public void renderItems(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        Double xStart = -2.9;
        Double yStart = 5.6;
        Double xTrans = 0.45;
        Double yTrans = -0.7;

        for (int column = 0; column < 6; column++) {
            for (int row = 0; row < 5; row++) {
                for(int amnt = 0; amnt < 1; amnt++) {
                    GlStateManager.pushMatrix();
                    GlStateManager.scale(0.30, 0.30, 0.30);
                    GlStateManager.translate(xStart + (xTrans * row), yStart + (yTrans * column), -0.5 - (0.2 * amnt ));
                    Minecraft.getMinecraft().getRenderItem().renderItem(new ItemStack(Items.DIAMOND_CHESTPLATE), ItemCameraTransforms.TransformType.GROUND);
                    GlStateManager.popMatrix();
                }
            }
        }

    }
}