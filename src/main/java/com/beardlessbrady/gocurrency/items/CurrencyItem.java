package com.beardlessbrady.gocurrency.items;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.handlers.ConfigHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Optional;

/**
 * Created by BeardlessBrady on 2021-02-22 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class CurrencyItem extends Item {
    public static final String NBT_TAG_CURRENCY = "currency";
    private final CurrencyObject[] currencyList;

    public CurrencyItem(Properties properties) {
        super(properties);

        currencyList = new CurrencyObject[ConfigHandler.configCurrencyName.get().size()];
        // Generate Currency from config
        List<? extends String> currNames = ConfigHandler.configCurrencyName.get();
        List<? extends Double> currValues = ConfigHandler.configCurrencyValue.get();
        for(byte i = 0; i < currNames.size(); i++) {
            currencyList[i] = new CurrencyObject(i, currNames.get(i), currValues.get(i));
        }
    }

    /**
     * Returns the currency enum for the itemstack, if it exists
     *
     * @param stack
     * @return
     */
    public CurrencyObject getCurrency(ItemStack stack) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        return fromNBT(compoundNBT, NBT_TAG_CURRENCY);
    }

    /**
     * Set NBT currency value for item
     * @param stack
     * @param currencyObject
     */
    public static void setCurrency(ItemStack stack, CurrencyObject currencyObject) {
        CompoundNBT compoundNBT = stack.getOrCreateTag();
        putIntoNBT(compoundNBT, NBT_TAG_CURRENCY, currencyObject.getID());
    }

    /**
     * What to add to creative menu
     *
     * @param group
     * @param items
     */
    @Override
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        for (CurrencyObject currency : currencyList) {
            ItemStack subItemStack = new ItemStack(this, 1);
            setCurrency(subItemStack, currency);
            items.add(subItemStack);
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return super.getDisplayName(stack);
    }

    // NBT Methods
    private Optional<CurrencyObject> getValuefromID(byte ID) {
        for (CurrencyObject currency : currencyList) {
            if (currency.getID() == ID) return Optional.of(currency);
        }
        return Optional.empty();
    }

    public CurrencyObject fromNBT(CompoundNBT compoundNBT, String tagname) {
        byte currencyID = 0;
        if (compoundNBT != null & compoundNBT.contains(tagname)) {
            currencyID = compoundNBT.getByte(tagname);
        }
        Optional<CurrencyObject> currencyValue = getValuefromID(currencyID);
        return new CurrencyObject((byte)-1, "Error", 0.00);
    }

    public static void putIntoNBT(CompoundNBT compoundNBT, String tagname, byte nbtID) {
        compoundNBT.putByte(tagname, nbtID);
    }

    /**
     * Hold Currency values
     */
    public static class CurrencyObject{
        private byte ID;
        private String name;
        private double value;


        public CurrencyObject(byte id, String name, double value){
            this.ID = id;
            this.name = name;
            this.value = value;
        }

        // Getters
        public byte getID() {
            return this.ID;
        }
        public String getString() {
            return this.name;
        }
        public double getValue() {
            return this.value;
        }
    }
}