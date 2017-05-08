package gunn.modcurrency.mod.handler;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-14.
 */
public class StateHandler {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumTwoBlock> TWOTALL = PropertyEnum.create("twotall", EnumTwoBlock.class);

    public enum EnumTwoBlock implements IStringSerializable {
        one,
        TWOBOTTOM,
        TWOTOP;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}
