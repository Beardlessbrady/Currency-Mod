package com.beardlessbrady.gocurrency.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Optional;

/**
 * Created by BeardlessBrady on 2021-02-22 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class CurrencyItem extends Item {
    public static final String NBT_TAG_CURRENCY = "currency";


    public CurrencyItem(Properties properties) {
        super(properties);
    }

    /**
     * Returns the currency enum for the itemstack, if it exists
     *
     * @param stack
     * @return
     */
    public static EnumCurrency getCurrency(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        return EnumCurrency.fromNBT(compoundNBT, NBT_TAG_CURRENCY);
    }

    /**
     * Set NBT currency value for item
     *
     * @param stack
     * @param enumCurrency
     */
    public static void setCurrency(ItemStack stack, EnumCurrency enumCurrency) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        enumCurrency.putIntoNBT(compoundNBT, NBT_TAG_CURRENCY);
    }

    /**
     * What to add to creative menu
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        for (EnumCurrency currency : EnumCurrency.values()) {
            ItemStack subItemStack = new ItemStack(this, 1);
            setCurrency(subItemStack, currency);
            items.add(subItemStack);
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        ModLoadingContext.get().con
        return super.getDisplayName(stack);
    }

    public enum EnumCurrency implements IStringSerializable {
        // TODO pull values from CONFIG
        NULL(0, 0, "Null Coin"),
        LOONIE(1, 100, "Loonie Coin"),
        Toonie(2, 200, "Toonie Coin");

        private final byte nbtID;
        private final String name;
        private final int value;

        EnumCurrency(int id, int value, String name) {
            this.nbtID = (byte) id;
            this.name = name;
            this.value = value;
        }

        // Getters
        @Override
        public String getString() {
            return this.name;
        }

        public int getValue() {
            return this.value;
        }

        private static Optional<EnumCurrency> getValuefromID(byte ID) {
            for (EnumCurrency currency : EnumCurrency.values()) {
                if (currency.nbtID == ID) return Optional.of(currency);
            }
            return Optional.empty();
        }

        // NBT Methods
        public static EnumCurrency fromNBT(CompoundNBT compoundNBT, String tagname) {
            byte currencyID = 0;
            if (compoundNBT != null & compoundNBT.contains(tagname)) {
                currencyID = compoundNBT.getByte(tagname);
            }
            Optional<EnumCurrency> currencyValue = getValuefromID(currencyID);
            return currencyValue.orElse(NULL);
        }

        public void putIntoNBT(CompoundNBT compoundNBT, String tagname) {
            compoundNBT.putByte(tagname, nbtID);
        }
    }
}