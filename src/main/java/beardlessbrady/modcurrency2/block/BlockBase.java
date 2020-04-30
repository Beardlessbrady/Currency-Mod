package beardlessbrady.modcurrency2.block;

import beardlessbrady.modcurrency2.ModCurrency;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-08
 */

public class BlockBase extends Block {
    Class<? extends TileEntity> tileClass;


    public BlockBase(String name, Class<? extends TileEntity> tileClass) {
        super(Material.ROCK);
        setRegistryName(name);
        setUnlocalizedName(this.getRegistryName().toString());
        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        this.tileClass = tileClass;
    }

    public void registerTileEntity(){
        GameRegistry.registerTileEntity(tileClass, new ResourceLocation(this.getRegistryName().toString() + "_te"));
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        try {
            return tileClass.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEntity getTile(World world, BlockPos pos){
        if(world.getTileEntity(pos) instanceof TileEntity)
            return world.getTileEntity(pos);
        return null;
    }

    public TileEntity getTile(IBlockAccess world, BlockPos pos, IBlockState state) {
        if(world.getTileEntity(pos) instanceof TileEntity)
            return world.getTileEntity(pos);
        return null;
    }

    public void registerModel(){
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }


}
