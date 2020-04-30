package beardlessbrady.modcurrency2.block.economyblocks;

import beardlessbrady.modcurrency2.block.BlockBase;
import beardlessbrady.modcurrency2.block.ModBlocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-09-20
 */

public class ModBlockColors implements IBlockColor {
    public static final IBlockColor INSTANCE = new ModBlockColors();

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {

        if(state.getBlock() instanceof BlockBase) {
            if (((BlockBase) state.getBlock()).getTile(worldIn, pos, state) != null) {
                EnumDyeColor dyeColor = ((TileEconomyBase)((BlockBase) state.getBlock()).getTile(worldIn, pos, state)).getColor();

                switch (dyeColor) {
                    case RED: return 0xD2443F;
                    case BLUE: return 0x1C53A8;
                    case CYAN: return 0x3C8EB0;
                    case GRAY: return 0x3A3A3A;
                    case LIME: return 0x76C610;
                    case PINK: return 0xF7B4D6;
                    case BLACK: return 0x1E1E26;
                    case BROWN: return 0x704425;
                    case GREEN: return 0x4A6B18;
                    case WHITE: return 0xEAEAEA;
                    case ORANGE: return 0xE69E34;
                    case PURPLE: return 0xA453CE;
                    case SILVER: return 0xBABAC1;
                    case YELLOW: return 0xE7E72A;
                    case MAGENTA: return 0xCB69C5;
                    case LIGHT_BLUE: return 0x8FB9F4;
                }
            }
        }
        return 0xFFFFFF;
    }

    public static void registerBlockColors(){
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(INSTANCE, ModBlocks.blockVending);
        Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler(INSTANCE, ModBlocks.blockTradein);
    }
}
