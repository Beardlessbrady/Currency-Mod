package gunn.modcurrency.common.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.common.blocks.items.ItemVendor;
import gunn.modcurrency.common.blocks.tiles.TileVendor;
import gunn.modcurrency.common.core.handler.StateHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.List;

import static gunn.modcurrency.common.blocks.ModBlocks.blockvendor;

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
 * File Created on 2016-10-30.
 */
public class BlockVendor extends BaseBlock implements ITileEntityProvider {
    
    public BlockVendor() {
        super(Material.ROCK, "blockvendor");
        setCreativeTab(ModCurrency.tabCurrency);
        setHardness(3.0F);
        setSoundType(SoundType.METAL);
        GameRegistry.register(new ItemVendor(this), getRegistryName());
        GameRegistry.registerTileEntity(TileVendor.class, ModCurrency.MODID + "_tevendor");
    }

    public void recipe(){
        ItemStack stack = new ItemStack(Item.getItemFromBlock(blockvendor));
        ItemStack stack2 = new ItemStack(Item.getItemFromBlock(blockvendor));
        ItemStack basic = new ItemStack(Item.getItemFromBlock(blockvendor));
        ItemStack color = new ItemStack(Items.DYE);
        stack.setItemDamage(15);

        GameRegistry.addRecipe(stack,
                "ABA",
                "ACA",
                "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.REPEATER,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);

        for(int i = 0; i < 16; i++) {
            if(i != 15) {
                stack2.setItemDamage(i);
                color.setItemDamage(15 - i);
                basic.setItemDamage(15);
                GameRegistry.addShapelessRecipe(stack2, color, basic);
                GameRegistry.addShapelessRecipe(basic, stack2);
            }
        }
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVendor();
    }

    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) return true;
        if(heldItem != null) {
            if (heldItem.getItem() == Items.DYE) {
                //Saving tile variables
                int face = getTile(world, pos).getFaceData();
                int bank = getTile(world, pos).getField(0);
                int[] itemCosts = getTile(world, pos).getAllItemCosts();
                ItemStackHandler stackHandler = getTile(world, pos).getStackHandler();

                world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);
                world.setBlockState(pos.up(), world.getBlockState(pos.up()).withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(heldItem.getItemDamage())), 3);
                
                //Setting tile variables
                getTile(world, pos).setFaceData(face);
                getTile(world, pos).setField(0, bank);
                getTile(world, pos).setAllItemCosts(itemCosts);
                getTile(world, pos).setStackHandler(stackHandler);

                if (!player.isCreative()) heldItem.stackSize--;
                return true;
            }
        }
        
        if(player.isSneaking() && player.getUniqueID().toString().equals(getTile(world,pos).getOwner())) {
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
    
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int face = 0;
        switch(placer.getHorizontalFacing().getOpposite()){
            case NORTH: face = 0;
                break;
            case EAST: face = 1;
                break;
            case SOUTH: face = 2;
                break;
            case WEST: face = 3;
                break;
        }

        getTile(worldIn, pos).setFaceData(face);
        EnumDyeColor color = state.getValue(StateHandler.COLOR);
        worldIn.setBlockState(pos.up(),ModBlocks.blocktopvendor.getDefaultState().withProperty(StateHandler.COLOR, color));

        if(placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            String playerName = player.getUniqueID().toString();
            getTile(worldIn, pos).setOwner(playerName);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isReplaceable(worldIn,pos.up());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileVendor te = getTile(worldIn, pos);
        te.setField(2,1);
        te.outChange();
        te.setField(2,0);
        te.outChange();
        
        te.dropItems();
        super.breakBlock(worldIn, pos, state);
        worldIn.setBlockToAir(pos.up());
    }

    @Override
    public int damageDropped(IBlockState state) {
        return getMetaFromState(state);
    }

    //<editor-fold desc="Models & Textures---------------------------------------------------------------------------------------------------">
    @Override
    public void initModel(){
        for(int i =0; i < 16; i++){
            //Im Lazy and I hate Mojangs EnumDyeColor, BE CONSISTENT (lightBlue, light_blue....)
            if(i == 3){
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 3, new ModelResourceLocation(getRegistryName(), "color=light_blue" + ",facing=north,item=true"));
            }else {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "color=" + EnumDyeColor.byDyeDamage(i) + ",facing=north,item=true"));
            }
        }
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
        list.add(new ItemStack(item, 1, 15));
    }
    //</editor-fold>

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.COLOR, StateHandler.FACING, StateHandler.ITEM});
    }
    
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return ((EnumDyeColor)state.getValue(StateHandler.COLOR)).getMetadata();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing face = EnumFacing.NORTH;
        TileVendor tile = (TileVendor)worldIn.getTileEntity(pos);
        int i = tile.getFaceData();
        
        switch(i){
            case 0: face = EnumFacing.NORTH;
                break;
            case 1: face = EnumFacing.EAST;
                break;
            case 2: face = EnumFacing.SOUTH;
                break;
            case 3: face = EnumFacing.WEST;
                break;
        }
        
        return state.withProperty(StateHandler.FACING, face).withProperty(StateHandler.ITEM, false);
    }
    
    public void renderItems(){
        
    }

    //</editor-fold>
}
