package beardlessbrady.modcurrency.item.playercurrency;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-09-27
 */

public class BakedPlayerCurrency implements IBakedModel {
    private final IBakedModel modelMain;
    private final BakedPlayerCurrency_Final modelFinal;
    private final OverridesList overridesList;

    public BakedPlayerCurrency(IBakedModel modelMain, IBakedModel shapeModel[], IBakedModel primeModel[]){
        this.modelMain = modelMain;
        this.modelFinal = new BakedPlayerCurrency_Final(this.modelMain, shapeModel, primeModel);
        this.overridesList = new OverridesList(this);
    }

    public BakedPlayerCurrency_Final getModelFinal(){
        return modelFinal;
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
        return this.overridesList;
    }

    private static class OverridesList extends ItemOverrideList{
        private BakedPlayerCurrency modelCurrency;

        public OverridesList(BakedPlayerCurrency modelCurrency){
            super(Collections.EMPTY_LIST);
            this.modelCurrency = modelCurrency;
        }

        @Override
        public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            return this.modelCurrency.getModelFinal().setCurrentItemStack(stack);
        }
    }
}
