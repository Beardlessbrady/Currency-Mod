package beard.modcurrency.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;
import java.util.List;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-09-27
 */

public class BakedModelCurrency implements IBakedModel {
    private final IBakedModel modelMain;
    private final BakedModelCurrencyFinalized modelFinal;

    public BakedModelCurrency(IBakedModel modelMain, IBakedModel attachmentModels){
        this.modelMain = modelMain;
        this.modelFinal = new BakedModelCurrencyFinalized(this.modelMain, attachmentModels);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return this.modelMain.getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return this.modelMain.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.modelMain.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return this.modelMain.isBuiltInRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return this.modelMain.getParticleTexture();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.modelMain.getOverrides();
    }
}
