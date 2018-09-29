package beard.modcurrency.client;

import beard.modcurrency.item.ItemCurrency;
import javafx.util.Pair;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-09-27
 */

public class BakedModelCurrencyFinalized implements IBakedModel {
    private final IBakedModel model;
    private final IBakedModel attachmentModels;
    private ItemStack itemStack;

    public BakedModelCurrencyFinalized(IBakedModel model, IBakedModel attachmentModels){
        this.model = model;
        this.attachmentModels = attachmentModels;
        this.itemStack = null;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        List<BakedQuad> list1 = this.model.getQuads(state,side,rand);
        List<BakedQuad> list2 = this.attachmentModels.getQuads(state, side, rand);

        System.out.println("BAGEEK)");

        list.addAll(list1);
        list.addAll(list2);
        return list;
    }

    public BakedModelCurrencyFinalized setCurrentItemStack(ItemStack itemStack){
        this.itemStack = itemStack;
        return this;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.model.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.model.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.model.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.model.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.model.getOverrides();
    }
}
