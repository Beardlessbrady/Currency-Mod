package beardlessbrady.modcurrency2.handler;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-19
 */

public class StateHandler {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyEnum<EnumTwoBlock> TWOTALL = PropertyEnum.create("twotall", EnumTwoBlock.class);
    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public enum EnumTwoBlock implements IStringSerializable {
        ONE,
        TWOBOTTOM,
        TWOTOP;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }
}