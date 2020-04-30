package beardlessbrady.modcurrency2.block.economyblocks.vending;

import beardlessbrady.modcurrency2.ModCurrency;
import beardlessbrady.modcurrency2.block.BlockBase;
import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import beardlessbrady.modcurrency2.handler.StateHandler;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;


/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class BlockVending extends BlockBase {

    public BlockVending() {
        super("blockvending", TileVending.class);
    }

    /** Method activated when the block is RIGHT CLICKED */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileVending tile = (TileVending) getTile(worldIn, pos);

        if (TileEconomyBase.EMPTYID.equals(getTile(worldIn, pos).getPlayerUsing())) { // Compares stored PLAYERUSING with an empty playerID to see if a player currently has the machine opened */
            if(playerIn.getHeldItemMainhand().getItem() == Items.DYE){ // Checks if player is holding dye, if so color machine instead of opening*/

                if(!playerIn.isCreative())
                    playerIn.getHeldItemMainhand().shrink(1);

                tile.setColor(EnumDyeColor.byDyeDamage(playerIn.getHeldItemMainhand().getItemDamage()));

                // Code below used to update the block to force it realize it needs to change colour */
                worldIn.markBlockRangeForRenderUpdate(pos, pos);
                worldIn.notifyBlockUpdate(pos, worldIn.getBlockState(pos), worldIn.getBlockState(pos), 3);
                worldIn.scheduleBlockUpdate(pos, this,0,0);
                tile.markDirty();
            }else { // If player not holding dye, activate as normal */
                tile.restock(); // If machine is creative restock items

                if (playerIn.isSneaking() && tile.getOwner().equals(playerIn.getUniqueID())) {   // Owning/Creative & Sneaking machine will open in STOCK MODE */
                    tile.setField(TileEconomyBase.FIELD_MODE, 1);
                } else { // Opens machine in TRADE MODE */
                    tile.setField(TileEconomyBase.FIELD_MODE, 0);
                }

                playerIn.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN,0.2F, -100.0F);

                if (!worldIn.isRemote) { // If CLIENT open GUI */
                    ((TileVending) getTile(worldIn, pos)).openGui(playerIn, worldIn, pos);
                    return true;
                }
            }
        }
        return true;
    }

    /** Method activated when block is placed **/
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOBOTTOM) // Places the 'bottom' part of the block since it is a 2 block high entity*/
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        worldIn.setBlockState(pos.up(), state.withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.TWOTOP) // Places the 'top' part of the block */
                .withProperty(StateHandler.FACING, placer.getHorizontalFacing().getOpposite()));

        if(stack.getMetadata() == 1) // If the itemBlock is CREATIVE (metadata = 1) then set the block to creative
            getTile(worldIn, pos).setField(TileVending.FIELD_CREATIVE, 1);

        getTile(worldIn, pos).setOwner(placer.getUniqueID()); // Sets owner
        getTile(worldIn, pos).markDirty();
    }

    /** Method activated when block is broken **/
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(!worldIn.isRemote) { // if CLIENT
            if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP) { // If block activated is 2 high & block broken is 'top' break block under it as well
                worldIn.setBlockToAir(pos.down());
            } else if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOBOTTOM) { // If block activated is 2 high & block broken is 'bottom' break block above it as well
                TileVending te = (TileVending) getTile(worldIn, pos, state);

                //Outputs the change in STOCK then SELL MODE
                te.setField(TileEconomyBase.FIELD_MODE, 0);
                te.outChange(true);

                te.setField(TileEconomyBase.FIELD_MODE, 1);
                te.outChange(true);

                te.dropInventory();

                worldIn.setBlockToAir(pos.up());
            }
            super.breakBlock(worldIn, pos, state);
        }
    }

    /** Create method for blocks tile**/
    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
       if(state.getValue(StateHandler.TWOTALL) != StateHandler.EnumTwoBlock.TWOTOP)
           return super.createTileEntity(world, state);

       return null;
    }

    /** Getter method for the blocks tile**/
    @Override
    public TileEconomyBase getTile(World world, BlockPos pos) {
            if (world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){ // If 'top' part of block open tile from bottom under it since the tile is stored in the bottom block*/
                if (world.getTileEntity(pos.down()) instanceof TileVending)
                    return (TileVending) world.getTileEntity(pos.down());
            }else{ // If 'bottom' part of block open tile normally since the tile is stored in the bottom block*/
                if (world.getTileEntity(pos) instanceof TileVending)
                    return (TileVending) world.getTileEntity(pos);
            }
        return null;
    }

    /** Getter method for the blocks tile**/
    public TileEconomyBase getTile(World world, BlockPos pos, IBlockState state) {
        if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            if (world.getTileEntity(pos.down()) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos.down());
        }else{
            if (world.getTileEntity(pos) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos);
        }
        return null;
    }

    /** Getter method for the blocks tile**/
    @Override
    public TileEconomyBase getTile(IBlockAccess world, BlockPos pos, IBlockState state) {
        if (state.getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            if (world.getTileEntity(pos.down()) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos.down());
        }else{
            if (world.getTileEntity(pos) instanceof TileVending)
                return (TileVending) world.getTileEntity(pos);
        }
        return null;
    }

    /** Sub blocks of an item**/
    @Override
    public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> items) {
        if (tab == ModCurrency.tabCurrency) {
            items.add(new ItemStack(this, 1, 0));

            ItemStack creative = new ItemStack(this, 1, 1);
            creative.addEnchantment(Enchantment.getEnchantmentByID(28), 1);
            creative.setStackDisplayName("CREATIVE " + getLocalizedName());
            items.add(creative);
        }
    }

    /** Block State Methods **/
    //<editor-fold desc="Block State Methods">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, StateHandler.FACING, StateHandler.TWOTALL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(meta % 4))
                .withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.class.getEnumConstants()[meta / 4]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.FACING).getHorizontalIndex() + (state.getValue(StateHandler.TWOTALL).ordinal() * 4));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        TileVending tile;

        if (StateHandler.EnumTwoBlock.class.getEnumConstants()[getMetaFromState(state) / 4] == StateHandler.EnumTwoBlock.TWOTOP) {
            tile = (TileVending) worldIn.getTileEntity(pos.down());
        } else {
            tile = (TileVending) worldIn.getTileEntity(pos);
        }

        return this.getDefaultState().withProperty(StateHandler.FACING, EnumFacing.getHorizontal(getMetaFromState(state) % 4))
                .withProperty(StateHandler.TWOTALL, StateHandler.EnumTwoBlock.class.getEnumConstants()[getMetaFromState(state) / 4]);
    }
    //</editor-fold>

    /** Rendering Methods **/
    //<editor-fold desc="Rendering Methods">
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModel() {
        super.registerModel();
        ClientRegistry.bindTileEntitySpecialRenderer(TileVending.class, new RenderVending());
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 1, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.TRANSLUCENT || layer == BlockRenderLayer.SOLID;
    }
    //</editor-fold>
}
