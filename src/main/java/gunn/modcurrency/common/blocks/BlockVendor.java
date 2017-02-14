package gunn.modcurrency.common.blocks;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.blocks.items.IBColored;
import gunn.modcurrency.common.core.handler.StateHandler;
import gunn.modcurrency.common.tiles.TileVendor;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-10-30.
 */
public class BlockVendor extends Block implements ITileEntityProvider{
    private static final AxisAlignedBB BOUND_BOX_N = new AxisAlignedBB(0.03125, 0, 0.28125, 0.96875, 1, 1);
    private static final AxisAlignedBB BOUND_BOX_E = new AxisAlignedBB(0.71875, 0, 0.03125, 0, 1, 0.96875);
    private static final AxisAlignedBB BOUND_BOX_S = new AxisAlignedBB(0.03125, 0, 0.71875, 0.96875, 1, 0);
    private static final AxisAlignedBB BOUND_BOX_W = new AxisAlignedBB(0.28125, 0, 0.03125, 1, 1, 0.96875);
    
    public BlockVendor() {
        super(Material.ROCK);
        setRegistryName("blockvendor");
        setUnlocalizedName(this.getRegistryName().toString());

        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);

        GameRegistry.register(this);
        GameRegistry.register(new IBColored(this), getRegistryName());
        GameRegistry.registerTileEntity(TileVendor.class, ModCurrency.MODID + "_tevendor");
    }

    public void recipe(){
        ItemStack basic = new ItemStack(Item.getItemFromBlock(this));
        ItemStack color = new ItemStack(Items.DYE);
        basic.setItemDamage(0);

        GameRegistry.addRecipe(basic,
                "ABA",
                "ACA",
                "ADA",
                'A', Items.IRON_INGOT,
                'B', Items.COMPARATOR,
                'C', Item.getItemFromBlock(Blocks.CHEST),
                'D', Items.IRON_DOOR);

        for(int i = 1; i < 16; i++) {
            ItemStack stack = new ItemStack(Item.getItemFromBlock(this));
            stack.setItemDamage(i);
            color.setItemDamage(i);
            GameRegistry.addShapelessRecipe(stack, color, basic);
            GameRegistry.addShapelessRecipe(basic, stack);
        }
    }

    public void initModel() {
        for (int i = 0; i < 16; i++) {
            //Im Lazy and I hate Mojangs EnumDyeColor, BE CONSISTENT (lightBlue, light_blue....)
            if (i == 12) {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 12, new ModelResourceLocation(getRegistryName(), "color=light_blue" + ",facing=north,item=true,open=false"));
            } else {
                ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), i, new ModelResourceLocation(getRegistryName(), "color=" + EnumDyeColor.byDyeDamage(i) + ",facing=north,item=true,open=false"));
            }
        }
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        try {
            int face = ((ModTile) source.getTileEntity(pos)).getField(7);

            switch (face) {
                default:
                case 0: return BOUND_BOX_N;
                case 1: return BOUND_BOX_E;
                case 2: return BOUND_BOX_S;
                case 3: return BOUND_BOX_W;
            }
        } catch (NullPointerException n) {
            return super.getBoundingBox(state, source, pos);
        }
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        try {
            int face = ((ModTile) worldIn.getTileEntity(pos)).getField(7);

            switch (face) {
                default:
                case 0: return BOUND_BOX_N;
                case 1: return BOUND_BOX_E;
                case 2: return BOUND_BOX_S;
                case 3: return BOUND_BOX_W;
            }
        } catch (NullPointerException n) {
            return super.getCollisionBoundingBox(blockState, worldIn, pos);
        }
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
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileVendor();
    }

    public TileVendor getTile(World world, BlockPos pos) {
        return (TileVendor) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing heldItem, float side, float hitX, float hitY) {
        if(getTile(world, pos).getPlayerUsing() == null) {      //Client and Server
            getTile(world, pos).setField(5, player.isCreative() ? 1 : 0);

            if (player.getHeldItemMainhand() != ItemStack.EMPTY && !world.isRemote) {      //Just Server
                if (player.getHeldItemMainhand().getItem() == Items.DYE) {
                    //<editor-fold desc="Saving Tile Variables">
                    ModTile tile = getTile(world, pos);

                    ItemStackHandler inputStackHandler = tile.getInputHandler();
                    ItemStackHandler vendStackHandler = tile.getVendHandler();
                    ItemStackHandler buffStackHandler = tile.getBufferHandler();

                    int bank = tile.getField(0);
                    int face = tile.getField(7);
                    int profit = tile.getField(4);
                    int locked = tile.getField(1);
                    int mode = tile.getField(2);
                    int infinite = tile.getField(6);
                    String owner = tile.getOwner();
                    int[] itemCosts = tile.getAllItemCosts();
                    //</editor-fold>

                    world.setBlockState(pos, state.withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(player.getHeldItemMainhand().getItemDamage())), 3);
                    world.setBlockState(pos.up(), world.getBlockState(pos.up()).withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(player.getHeldItemMainhand().getItemDamage())), 3);

                    //<editor-fold desc="Setting Tile Variables">
                    tile = getTile(world, pos);

                    tile.setStackHandlers(inputStackHandler, buffStackHandler, vendStackHandler);
                    tile.setField(0, bank);
                    tile.setField(7, face);
                    tile.setField(4, profit);
                    tile.setField(1, locked);
                    tile.setField(2, mode);
                    tile.setField(6, infinite);
                    tile.setOwner(owner);
                    tile.setAllItemCosts(itemCosts);
                    //</editor-fold>

                    if (!player.isCreative()) player.getHeldItemMainhand().shrink(1);
                    return true;
                } else if (player.getHeldItemMainhand() != ItemStack.EMPTY && world.isRemote) return true;
            }

            if ((player.isSneaking() && player.getUniqueID().toString().equals(getTile(world, pos).getOwner())) || (player.isSneaking() && player.isCreative())) {      //Client and Server

                if (getTile(world, pos).getField(2) == 1) {   //If True
                    getTile(world, pos).setField(2, 0);
                } else {
                    getTile(world, pos).setField(2, 1);
                }
                return true;
            }

            if(!world.isRemote){    //Just Server
                getTile(world, pos).openGui(player, world, pos);
                return true;
            } else return true;
        }
        return false;
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

        getTile(worldIn, pos).setField(7,face);
        EnumDyeColor color = state.getValue(StateHandler.COLOR);
        worldIn.setBlockState(pos.up(),ModBlocks.blockTop.getDefaultState().withProperty(StateHandler.COLOR, color));

        if(placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) placer;
            String playerName = player.getUniqueID().toString();
            getTile(worldIn, pos).setOwner(playerName);
        }
        worldIn.scheduleBlockUpdate(pos, worldIn.getBlockState(pos).getBlock(), 0, 0);
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

    //<editor-fold desc="Block States--------------------------------------------------------------------------------------------------------">
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {StateHandler.COLOR, StateHandler.FACING, StateHandler.ITEM, StateHandler.OPEN});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(StateHandler.COLOR, EnumDyeColor.byDyeDamage(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(StateHandler.COLOR)).getDyeDamage();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        EnumFacing face = EnumFacing.NORTH;
        TileVendor tile = (TileVendor) worldIn.getTileEntity(pos);
        int i = tile.getField(7);

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

        ArrayList<Integer> pio = new ArrayList<Integer>(4);

        return state.withProperty(StateHandler.FACING, face).withProperty(StateHandler.ITEM, false)
                .withProperty(StateHandler.OPEN, tile.getField(2) == 1);
    }
    //</editor-fold>
}
