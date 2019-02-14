package beardlessbrady.modcurrency.block;

import beardlessbrady.modcurrency.ModCurrency;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

public class EconomyBlockBase extends Block {
    Class<? extends TileEconomyBase> tileClass;


    public EconomyBlockBase(String name, Class<? extends TileEconomyBase> tileClass) {
        super(Material.ROCK);
        setRegistryName(name);
        setUnlocalizedName(this.getRegistryName().toString());
        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        this.tileClass = tileClass;
    }

    public void registerTileEntity(){
        GameRegistry.registerTileEntity(tileClass, new ResourceLocation(ModCurrency.MODID, this.getRegistryName().toString() + "_te"));
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

        System.out.println("Tile Entity Broken for " + tileClass.getName());
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    public TileEconomyBase getTile(World world, BlockPos pos){
        if(world.getTileEntity(pos) instanceof TileEconomyBase)
            return (TileEconomyBase)world.getTileEntity(pos);

        return null;
    }

    public void registerModel(){
        //TODO
    }
}
