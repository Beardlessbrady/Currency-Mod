package gunn.modcurrency.mod.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-10
 */
public class BakedModelVendor implements IBakedModel {
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


       // ModelLoaderRegistry.getModel(model);

       // quads.addAll(model.getQuads(state, side, rand));


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