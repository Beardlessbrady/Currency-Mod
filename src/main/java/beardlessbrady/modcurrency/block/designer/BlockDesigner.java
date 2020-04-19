package beardlessbrady.modcurrency.block.designer;

import beardlessbrady.modcurrency.block.BlockBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class BlockDesigner extends BlockBase {
    public BlockDesigner() {
        super("blockcurrencydesigner", TileDesigner.class);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
      //  if (!worldIn.isRemote) { // If CLIENT open GUI
            ((TileDesigner) getTile(worldIn, pos)).openGui(playerIn, worldIn, pos);
            return true;
      //  }
       // return true;
    }
}
