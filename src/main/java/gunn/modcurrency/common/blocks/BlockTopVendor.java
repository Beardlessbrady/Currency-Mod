package gunn.modcurrency.common.blocks;

import gunn.modcurrency.common.blocks.tiles.TileVendor;
import gunn.modcurrency.common.core.handler.StateHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.Random;

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
 * File Created on 2016-11-20.
 */
public class BlockTopVendor extends BaseBlock{
    
    public BlockTopVendor() {
        super(Material.ROCK, "blocktopvendor");
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
    }

    public boolean isBlockBelow(World world, BlockPos pos){
        return world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockvendor);
    }

    public boolean isBlockBelow(IBlockAccess world, BlockPos pos){
        return world.getBlockState(pos.down()).getBlock().equals(ModBlocks.blockvendor);
    }
    
    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos.down());
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if(isBlockBelow(world, pos)) {
            if (heldItem != null){
                if (heldItem.getItem() == Items.DYE) {
                    //Saving tile variables
                    int face = getTile(world, pos).getFaceData();
                    int bank = getTile(world, pos).getField(0);
                    int[] itemCosts = getTile(world, pos).getAllItemCosts();
                    ItemStackHandler stackHandler = getTile(world, pos).getStackHandler();


                    world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);
                    world.setBlockState(pos.down(), world.getBlockState(pos.down()).withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);

                    //Setting tile variables
                    getTile(world, pos).setFaceData(face);
                    getTile(world, pos).setField(0, bank);
                    getTile(world, pos).setAllItemCosts(itemCosts);
                    getTile(world, pos).setStackHandler(stackHandler);

                    if (!player.isCreative()) heldItem.stackSize--;
                    return true;
                }
            }
            if(player.isSneaking()) {
                if (getTile(world, pos).getField(2) == 1) {   //If True
                    getTile(world, pos).setField(2, 0);
                } else {
                    getTile(world, pos).setField(2, 1);
                }
                getTile(world, pos).getWorld().notifyBlockUpdate(getTile(world, pos).getPos(), getTile(world, pos).getBlockType().getDefaultState(), getTile(world, pos).getBlockType().getDefaultState(), 3);
                return true;
            }
            getTile(world,pos).openGui(player,world,pos.down());

        }
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockToAir(pos.down());
    }

    @Nullable
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(ModBlocks.blockvendor);
    }

    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(StateHandler.COLOR).getMetadata();
    }

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.COLOR,StateHandler.FACING});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumDyeColor)state.getValue(StateHandler.COLOR)).getDyeDamage();
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (isBlockBelow(worldIn, pos)) {
            EnumFacing face = EnumFacing.NORTH;
            TileVendor tile = (TileVendor) worldIn.getTileEntity(pos.down());
            int i = tile.getFaceData();

            switch (i) {
                case 0:
                    face = EnumFacing.NORTH;
                    break;
                case 1:
                    face = EnumFacing.EAST;
                    break;
                case 2:
                    face = EnumFacing.SOUTH;
                    break;
                case 3:
                    face = EnumFacing.WEST;
                    break;
            }

            return state.withProperty(StateHandler.FACING, face);
        }
        return state.withProperty(StateHandler.FACING, EnumFacing.NORTH);
    }
    //</editor-fold>
}
