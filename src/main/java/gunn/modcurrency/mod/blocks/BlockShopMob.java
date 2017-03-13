package gunn.modcurrency.mod.blocks;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.tiles.TileShopMob;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-23
 */
public class BlockShopMob extends Block implements ITileEntityProvider{

    public BlockShopMob() {
        super(Material.ROCK);
        setRegistryName("blockshopmob");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
        GameRegistry.registerTileEntity(TileShopMob.class, ModCurrency.MODID + "_teshopmob");
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileShopMob();
    }

    public TileShopMob getTile(World world, BlockPos pos){
        return (TileShopMob) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.isSneaking() && (getTile(worldIn, pos).getOwner().equals(playerIn.getUniqueID().toString()))) {

            //Searches for mobs in the surrounding area (5 blocks in each direction
            //Checks if they are leashed by block owner

            System.out.println("____NEW______");
            List mobList = worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)).expand(5, 5, 5));
            for (int i = 0; i < mobList.size(); i++) {
                if (((EntityLiving) mobList.get(i)).getLeashed() && ((EntityLiving) mobList.get(i)).getLeashedToEntity() instanceof EntityPlayer) {
                    if(playerIn.getUniqueID().toString().equals(((EntityLiving) mobList.get(i)).getLeashedToEntity().getUniqueID().toString())) {
                        System.out.println("YES MOM");
                        System.out.println(mobList.get(i));
                        System.out.println("   ");
                    }
                }
            }
            return true;
        }


        return false;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if(placer instanceof EntityPlayer) {
           getTile(worldIn, pos).setOwner(((EntityPlayer) placer).getUniqueID().toString());
        }
        worldIn.scheduleBlockUpdate(pos, worldIn.getBlockState(pos).getBlock(), 0, 0);
    }
}
