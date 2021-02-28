package com.beardlessbrady.gocurrency.items;

import com.beardlessbrady.gocurrency.GOCurrency;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
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
     * What to add to creative menu
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        CurrencyItem.CurrencyObject[] currList = GOCurrency.currencyList;

        if (currList != null) {
            for (CurrencyObject currency : currList) {
                ItemStack subItemStack = new ItemStack(this, 1);
                putIntoNBT(subItemStack, currency);
                items.add(subItemStack);
            }
        }
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        CurrencyObject object = fromNBT(stack);
        return super.getTranslationKey(stack) + "_" + object.getID();
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CurrencyObject object = fromNBT(stack);
        String name = "ERROR";
        if (object != null) {
            name = object.name;
        }
        return new StringTextComponent(name);
    }

    public static float getPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity)
    {
        CurrencyObject currencyObject = fromNBT(itemStack);
        return currencyObject.getPropertyOverrideValue();
    }

    // NBT Methods
    private static Optional<CurrencyObject> getValuefromID(byte ID) {
        CurrencyItem.CurrencyObject[] currList = GOCurrency.currencyList;

        if (currList != null) {
            for (CurrencyObject currency : currList) {
                if (currency.getID() == ID) return Optional.of(currency);
            }
        }
        return Optional.empty();
    }

    /**
     * Set NBT currency value for item
     *
     * @param stack
     * @param currencyObject
     */
    public static void putIntoNBT(ItemStack stack, CurrencyObject currencyObject) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        compoundNBT.putByte(NBT_TAG_CURRENCY, currencyObject.getID());
        stack.setTag(compoundNBT);
    }

    public static CurrencyObject fromNBT(ItemStack stack) {
        byte currencyID = -1;
        CompoundNBT compoundNBT = stack.getOrCreateTag();

        if (compoundNBT.contains(NBT_TAG_CURRENCY)) {
            currencyID = compoundNBT.getByte(NBT_TAG_CURRENCY);
        }
        Optional<CurrencyObject> currencyValue = getValuefromID(currencyID);
        return currencyValue.orElse(new CurrencyObject((byte) -1, "Error", 0.00));
    }

    /**
     * Hold Currency values
     */
    public static class CurrencyObject {
        private byte ID;
        private String name;
        private double value;


        public CurrencyObject(byte id, String name, double value) {
            this.ID = id;
            this.name = name;
            this.value = value;
        }

        // Getters
        public byte getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public double getValue() {
            return this.value;
        }

        public float getPropertyOverrideValue() {
            return this.ID;
        }
    }
}