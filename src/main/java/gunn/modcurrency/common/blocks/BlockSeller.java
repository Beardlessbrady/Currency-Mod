package gunn.modcurrency.common.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.items.IBColored;
import gunn.modcurrency.common.core.handler.StateHandler;
import gunn.modcurrency.common.tiles.TileSeller;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

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
 * File Created on 2016-12-19
 */
public class BlockSeller extends Block implements ITileEntityProvider{

    public BlockSeller() {
        super(Material.ROCK);
        setRegistryName("blockseller");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
        GameRegistry.register(new IBColored(this), getRegistryName());
        GameRegistry.registerTileEntity(TileSeller.class, ModCurrency.MODID + "_teseller");
    }

    public void recipe(){
    }

    public void initModel(){}

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSeller();
    }

    public TileSeller getTile(World world, BlockPos pos) {
        return (TileSeller) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        getTile(world,pos).setField(5,player.isCreative() ? 1 : 0);
        if (world.isRemote) return true;

        if(heldItem != null) {
            if (heldItem.getItem() == Items.DYE) {
                //Saving tile variables
                int face = getTile(world, pos).getField(7);
                int bank = getTile(world, pos).getField(0);
                int[] itemCosts = getTile(world, pos).getAllItemCosts();
                ItemStackHandler stackHandler = getTile(world, pos).getStackHandler();

                world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);
                world.setBlockState(pos.up(), world.getBlockState(pos.up()).withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);

                //Setting tile variables
                getTile(world, pos).setField(7,face);
                getTile(world, pos).setField(0, bank);
                getTile(world, pos).setAllItemCosts(itemCosts);
                getTile(world, pos).setStackHandler(stackHandler);

                if (!player.isCreative()) heldItem.stackSize--;
                return true;
            }
        }

        if((player.isSneaking() && player.getUniqueID().toString().equals(getTile(world,pos).getOwner())) || (player.isSneaking() && player.isCreative())) {
            if (getTile(world, pos).getField(2) == 1) {   //If True
                getTile(world, pos).setField(2, 0);
            } else {
                getTile(world, pos).setField(2, 1);
            }
            getTile(world, pos).getWorld().notifyBlockUpdate(getTile(world, pos).getPos(), getTile(world, pos).getBlockType().getDefaultState(), getTile(world, pos).getBlockType().getDefaultState(), 3);
            return true;
        }

        getTile(world,pos).openGui(player,world,pos);
        return true;
    }
}
