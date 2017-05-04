package gunn.modcurrency.mod.core.handler;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.item.EnumDyeColor;
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
    public static final PropertyBool ITEM = PropertyBool.create("item");
    public static final PropertyEnum<EnumTopTypes> TOP = PropertyEnum.create("top", EnumTopTypes.class);
    public static final IUnlistedProperty<ItemStack>[] UNLISTED_CONTAIN = new UnlistedPropertyContain[30];

    public enum EnumTopTypes implements IStringSerializable {
        VENDOR,
        SELLER,
        SELLEROPEN;

        @Override
        public String getName() {
            return name().toLowerCase();
        }
    }

    public static class UnlistedPropertyContain implements IUnlistedProperty<ItemStack>{

        public UnlistedPropertyContain(){

        }

        @Override
        public String getName() {
            return "unlistedContain";
        }

        @Override
        public boolean isValid(ItemStack value) {
            return true;
        }

        @Override
        public Class<ItemStack> getType() {
            return ItemStack.class;
        }

        @Override
        public String valueToString(ItemStack value) {
            return value.toString();
        }
    }
}
