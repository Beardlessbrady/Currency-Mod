package gunn.modcurrency.client.model;

import gunn.modcurrency.common.core.handler.StateHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;

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
 * File Created on 2016-12-28
 */
public class BakedModelVendor implements IPerspectiveAwareModel{
    public static final ModelResourceLocation blockStates = new ModelResourceLocation("modcurrency:blockvendor");
    public static final ModelResourceLocation variantTag = new ModelResourceLocation("modcurrency:blockvendor", "normal");

    private IBakedModel baseModel;

    public BakedModelVendor(IBakedModel base){
        baseModel = base;
    }

    private IBakedModel handleBlockState(@Nullable IBlockState blockState){
        IBakedModel defModel = baseModel;

        if(blockState.getValue(StateHandler.OPEN) == true){
            System.out.println("IM OPEN");
        }

        Minecraft mc = Minecraft.getMinecraft();
        BlockRendererDispatcher blockRendererDispatcher = mc.getBlockRendererDispatcher();
        BlockModelShapes blockModelShapes = blockRendererDispatcher.getBlockModelShapes();
        IBakedModel model = blockModelShapes.getModelForState(blockState);

        return model;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return handleBlockState(state).getQuads(state, side, rand);
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return baseModel.getParticleTexture();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return baseModel.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return baseModel.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return baseModel.isBuiltInRenderer();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return baseModel.getOverrides();
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return baseModel.getItemCameraTransforms();
    }


    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        if (baseModel instanceof IPerspectiveAwareModel){
            Matrix4f matrix4f = ((IPerspectiveAwareModel)baseModel).handlePerspective(cameraTransformType).getRight();
            return Pair.of(this, matrix4f);
        } else {
            ItemCameraTransforms itemCameraTransforms = baseModel.getItemCameraTransforms();
            ItemTransformVec3f itemTransformVec3f = itemCameraTransforms.getTransform(cameraTransformType);
            TRSRTransformation tr = new TRSRTransformation(itemTransformVec3f);
            Matrix4f mat = null;
            if(tr != null) mat = tr.getMatrix();

            return Pair.of(this,mat);
        }
    }





}
