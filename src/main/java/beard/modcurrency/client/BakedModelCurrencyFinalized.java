package beard.modcurrency.client;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
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
                primeList = colorQuads(primeList, color(68, 91, 117));
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

    public List<BakedQuad> colorQuads(List<BakedQuad> quads, int color) {
        for (int i = 0; i < quads.size(); i++) {
            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(Tessellator.getInstance().getBuffer(), quads.get(i), color);
        }
        return quads;
    }

    /**
	 * @param red   the red value of the color, between 0x00 (decimal 0) and 0xFF (decimal 255)
	 * @param green the red value of the color, between 0x00 (decimal 0) and 0xFF (decimal 255)
	 * @param blue  the red value of the color, between 0x00 (decimal 0) and 0xFF (decimal 255)
	 * @return the color in ARGB format
	 * @author Cadiboo
	 */
	public static int color(int red, int green, int blue) {

		red = MathHelper.clamp(red, 0x00, 0xFF);
		green = MathHelper.clamp(green, 0x00, 0xFF);
		blue = MathHelper.clamp(blue, 0x00, 0xFF);

		final int alpha = 0xFF;

		int colorRGBA = 0;
		colorRGBA |= red << 16;
		colorRGBA |= green << 8;
		colorRGBA |= blue << 0;
		colorRGBA |= alpha << 24;

		return colorRGBA;
	}
}
