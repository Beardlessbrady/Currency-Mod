package gunn.modcurrency.client.render;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.tiles.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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
@SideOnly(Side.CLIENT)
public class RenderTileVendor extends TileEntitySpecialRenderer<TileVendor>{

    private IModel modelWindow;
    private IBakedModel bakedModelWindow;

    private IBakedModel getBakedModelWindow() {
        if (bakedModelWindow == null) {
            try {
                modelWindow = ModelLoaderRegistry.getModel(new ResourceLocation(ModCurrency.MODID, "block/vend_window"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            bakedModelWindow = modelWindow.bake(TRSRTransformation.identity(), DefaultVertexFormats.BLOCK,
                    location -> Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString()));
        }
        return bakedModelWindow;
    }

    public void renderTileEntityAt(TileVendor te, double x, double y, double z, float partialTicks, int destroyStage){
        GlStateManager.pushMatrix();
        int facing = te.getFaceData(); //0 = North, 1 = East, 2 = South, 3 = West

        // Ensures starting position is same for every facing direction
        switch(facing){
            case 0: //NORTH
                GlStateManager.translate(x,y,z);
                break;
            case 1: //EAST
                GlStateManager.translate(x + 1,y,z);
                GlStateManager.rotate(-90,0,1,0);
                break;
            case 2: //SOUTH
                GlStateManager.translate(x + 1,y,z + 1);
                GlStateManager.rotate(180,0,1,0);
                break;
            case 3: //WEST
                GlStateManager.translate(x,y,z + 1);
                GlStateManager.rotate(90,0,1,0);
                break;
        }

        renderWindow(te, x, y, z, partialTicks, destroyStage);

        GlStateManager.popMatrix();

    }

    public void renderWindow(TileVendor te, double x, double y, double z, float ticks, int stage){
        GlStateManager.pushMatrix();
        GL11.glEnable(GL11.GL_BLEND);

        this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        World world = te.getWorld();
        GlStateManager.translate(-te.getPos().getX(), -te.getPos().getY(), -te.getPos().getZ());


        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
                world,
                getBakedModelWindow(),
                world.getBlockState(te.getPos()),
                te.getPos(),
                Tessellator.getInstance().getBuffer(),false);
        tessellator.draw();

        GL11.glDisable(GL11.GL_BLEND);
        GlStateManager.popMatrix();
    }
}