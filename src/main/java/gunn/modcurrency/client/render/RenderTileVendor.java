package gunn.modcurrency.client.render;

import gunn.modcurrency.common.blocks.tiles.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
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
 * File Created on 2016-11-21.
 */
public class RenderTileVendor extends TileEntitySpecialRenderer<TileVendor>{

    //Currently not in use, trying some other methods (that are more efficient, less lag) Due for V1.1.0
    @Override
    public void renderTileEntityAt(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage) {
    }

    public void renderItems(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage) {

    }


    

}


/*
int facing = te.getFaceData(); //0 = North, 1 = East, 2 = South, 3 = West

//Ensures starting position is same for every facing direction
        switch(facing){
            case 0:
                GlStateManager.translate(x,y,z);
                break;
            case 1:
                GlStateManager.translate(x + 1,y,z);
                GlStateManager.rotate(90,0,1,0);
                break;
            case 2:
                GlStateManager.translate(x + 1,y,z + 1);
                break;
            case 3:
                GlStateManager.translate(x,y,z + 1);
                GlStateManager.rotate(90,0,1,0);
                break;
        }
 */

//West North == +
//South East == -
//renderItems(te,x,y,z,partialTicks,destroyStage);

