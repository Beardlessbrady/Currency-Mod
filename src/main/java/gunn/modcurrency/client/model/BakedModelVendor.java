package gunn.modcurrency.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
 * File Created on 2017-01-11
 */
public class BakedModelVendor implements IBakedModel{
    IBakedModel basicModel;

    public BakedModelVendor(IBakedModel basic){
        basicModel= basic;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        //Initialize a list of quads and add the basic model to it
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        quads.addAll(basicModel.getQuads(state, side, rand));


        ItemStack item = new ItemStack(Items.APPLE,1);

        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        IBakedModel model = modelMesher.getItemModel(item);

        quads.addAll(model.getQuads(state, side, rand));


        return quads;
    }

    //<editor-fold desc="Methods Needed for IBakedModel">
    @Override
    public boolean isAmbientOcclusion() {
        return basicModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return basicModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return basicModel.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return basicModel.getParticleTexture();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return basicModel.getItemCameraTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return basicModel.getOverrides();
    }
    //</editor-fold>
}