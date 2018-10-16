package beard.modcurrency.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    private final IBakedModel shapeModel[];
    private final IBakedModel primeModel[];
    private ItemStack itemStack;

    public BakedModelCurrencyFinalized(IBakedModel model, IBakedModel[] shapeModel, IBakedModel[] primeModel){
        this.model = model;
        this.shapeModel = shapeModel;
        this.primeModel = primeModel;
        this.itemStack = null;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        List<BakedQuad> shapeList;
        List<BakedQuad> primeList;

        if (this.itemStack.hasTagCompound()) {
            NBTTagCompound nbtTagCompound = this.itemStack.getTagCompound();

            if (nbtTagCompound.hasKey("shape")) {
                shapeList = this.shapeModel[nbtTagCompound.getInteger("shape")].getQuads(state, side, rand);
                list.addAll(shapeList);
            }

            if (nbtTagCompound.hasKey("prime")) {
                primeList = this.primeModel[nbtTagCompound.getInteger("prime")].getQuads(state, side, rand);
                list.addAll(primeList);
                list.addAll(primeList);
            }

            return list;
        }

        return model.getQuads(state,side,rand);
    }

    public BakedModelCurrencyFinalized setCurrentItemStack(ItemStack itemStack) {
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
