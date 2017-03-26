package gunn.modcurrency.mod.block;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.core.data.BankAccountSavedData;
import gunn.modcurrency.mod.core.handler.StateHandler;
import gunn.modcurrency.mod.tile.TileATM;
import gunn.modcurrency.mod.tile.TileVendor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-15
 */
public class BlockATM extends Block implements ITileEntityProvider {

    public BlockATM() {
        super(Material.ROCK);
        setRegistryName("blockatm");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileATM.class, ModCurrency.MODID + "_teatm");
    }

    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileATM();
    }

    public TileATM getTile(World world, BlockPos pos) {
        return (TileATM) world.getTileEntity(pos);
    }


    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {    //Just Server
            if (getTile(world, pos).getPlayerUsing() == null) {     //Only one player can use GUI at time
                getTile(world, pos).openGui(player, world, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        EnumFacing face = EnumFacing.NORTH;
        switch(placer.getHorizontalFacing().getOpposite()){
            case NORTH: break;
            case EAST: face = EnumFacing.EAST;
                break;
            case SOUTH: face = EnumFacing.SOUTH;
                break;
            case WEST: face = EnumFacing.WEST;
                break;
        }
        worldIn.setBlockState(pos, state.withProperty(StateHandler.FACING, face));
    }


    //<editor-fold desc="Block States">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.FACING});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.FACING).getHorizontalIndex());
    }
    //</editor-fold>

    //<editor-fold desc="Render">
    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }
    //</editor-fold>
}
