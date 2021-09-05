package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.init.CommonRegistry;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;
import java.util.function.ToIntFunction;

/**
 * Created by BeardlessBrady on 2021-02-22 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingBlock extends ContainerBlock {

    public VendingBlock(Properties properties) {
        super(Properties.create(Material.IRON).hardnessAndResistance(3.5F).sound(SoundType.LANTERN).setRequiresTool().notSolid()
                .setLightLevel(a -> (int)(15)));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return CommonRegistry.TILE_VENDING.get().create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        ((VendingTile) worldIn.getTileEntity(pos)).setOwner(placer.getUniqueID());

        // Checking if creative and setting as such
        if (stack.hasTag()) {
            CompoundNBT nbt = stack.getTag();

            CompoundNBT creativeNBT = nbt.getCompound("Creative");
            boolean creative = creativeNBT.getBoolean("Creative");
            ((VendingTile) worldIn.getTileEntity(pos)).setVendingStateData(VendingStateData.CREATIVE_INDEX, creative? 1: 0);
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) return ActionResultType.SUCCESS; // Client do nothing
        if(!((VendingTile) Objects.requireNonNull(worldIn.getTileEntity(pos))).isPlayerUsing()) {

            INamedContainerProvider namedContainerProvider = this.getContainer(state, worldIn, pos);
            if (namedContainerProvider != null) {
                if (!(player instanceof ServerPlayerEntity)) return ActionResultType.FAIL;
                ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;

                // OPEN GUI
                int[] dataArray = ((VendingTile) worldIn.getTileEntity(pos)).getVendingStateDataAsArray();
                NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, buf -> buf.writeVarIntArray(dataArray).writeBlockPos(pos));
            }
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof VendingTile) {
                VendingTile tile = (VendingTile) tileentity;
                tile.dropAllContents(worldIn, pos);
            }
            super.onReplaced(state, worldIn, pos, newState, isMoving);  // call it last, because it removes the TileEntity
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBlockHarvested(worldIn, pos, state, player);
        if(!player.isCreative()) InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(this));
    }

    // ---- STATE ----
    private static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING; // Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        World world = context.getWorld();
        BlockPos blockPos = context.getPos();

        Direction direction = context.getPlacementHorizontalFacing();  // north, east, south, or west
        float playerFacingDirectionAngle = context.getPlacementYaw(); //if you want more directions than just NESW, you can use the yaw instead.
        // likewise the pitch is also available for up/down placement.

        BlockState blockState = getDefaultState().with(FACING, direction);
        return blockState;
    }

    /**
     * Defines the properties needed for the BlockState
     */
    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this));

        // Creative Machine in Creative Menu
        ItemStack creativeStack = new ItemStack(this, 1);
        // TODO Differentiate creative machine
        creativeStack.setDisplayName(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.creative")));

        // Add NBT for creative
        CompoundNBT creativeCompound = new CompoundNBT();
        creativeCompound.putBoolean("Creative", true);
        creativeStack.setTagInfo("Creative", creativeCompound);
        //
        items.add(creativeStack);
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return false;
    }

    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return 0;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
