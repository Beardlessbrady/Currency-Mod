package gunn.modcurrency.mod.client.model;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad;
import net.minecraftforge.client.model.pipeline.VertexTransformer;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.annotation.Nullable;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-07
 */
public class BakedModelVending implements IBakedModel{
    IBakedModel basicModel;

    public BakedModelVending(IBakedModel basic){
        basicModel = basic;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        //Initialize a list of quads and add the basic model to it
            List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(basicModel.getQuads(state, side, rand));
        ItemStack item = new ItemStack(Items.APPLE,1);
        ItemModelMesher modelMesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        IBakedModel model = modelMesher.getItemModel(item);
        List<BakedQuad> itemQuads = new ArrayList<>();
        itemQuads.addAll(model.getQuads(state,side,rand));

        TRSRTransformation test = new TRSRTransformation(new Vector3f(0,1,0),null, new Vector3f(0.4f,0.4f,0.4f), null);
        for(int i = 0; i < itemQuads.size(); i++){
            itemQuads.set(i, transform(itemQuads.get(i), test));
        }

        quads.addAll(itemQuads);

        return quads;
    }

    /**Credits to https://github.com/Vazkii/Botania/blob/master/src/main/java/vazkii/botania/client/model/GunModel.java#L86-L110*/
    private static BakedQuad transform(BakedQuad quad, final TRSRTransformation transform){
        UnpackedBakedQuad.Builder builder = new UnpackedBakedQuad.Builder(DefaultVertexFormats.ITEM);
        final IVertexConsumer consumer = new VertexTransformer(builder) {
            @Override
            public void put(int element, float... data) {
                VertexFormatElement formatElement = DefaultVertexFormats.ITEM.getElement(element);
                switch(formatElement.getUsage()) {
                    case POSITION: {
                        float[] newData = new float[4];
                        Vector4f vec = new Vector4f(data);
                        transform.getMatrix().transform(vec);
                        vec.get(newData);
                        parent.put(element, newData);
                        break;
                    }
                    default: {
                        parent.put(element, data);
                        break;
                    }
                }
            }
        };
        quad.pipe(consumer);
        return builder.build();
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
