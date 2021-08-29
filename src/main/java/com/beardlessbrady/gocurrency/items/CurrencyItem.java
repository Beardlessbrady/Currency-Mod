package com.beardlessbrady.gocurrency.items;

import com.beardlessbrady.gocurrency.ConfigHandler;
import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.init.CommonRegistry;
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
        if(currList != null) {
            if (this.isInGroup(group)) {
                for (CurrencyObject currency : currList) {
                    ItemStack subItemStack = new ItemStack(this, 1);
                    putIntoNBT(subItemStack, currency);
                    items.add(subItemStack);
                }
            }
        }
    }

    public static ItemStack getTabItem(){
        ItemStack tabItem = new ItemStack(CommonRegistry.ITEM_CURRENCY.get());
        CurrencyObject nbtCUrrency = new CurrencyObject((byte)0, "One Dollar", "1.00");
        putIntoNBT(tabItem,nbtCUrrency);

        return tabItem;
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

    public static float getPropertyOverride(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity livingEntity) {
        CurrencyObject currencyObject = fromNBT(itemStack);
        return currencyObject.getPropertyOverrideValue();
    }

    // NBT Methods
    private static CurrencyObject getValuefromID(byte ID) {
        CurrencyItem.CurrencyObject[] currList = GOCurrency.currencyList;

        if (currList != null) {
            for (CurrencyObject currency : currList) {
                if ((int)currency.getID() == (int)ID) return currency;
            }
        }
        return new CurrencyObject((byte)-1, "NULL", "-1.00");
    }

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
        return getValuefromID(currencyID);
    }

    public static int[] getCurrencyValue(ItemStack item) {
        CurrencyItem.CurrencyObject currency = CurrencyItem.fromNBT(item);

        int dollar = currency.getDollar() * item.getCount();
        int cent = currency.getCent() * item.getCount();

        int[] addition = roundCents(cent);
        dollar += addition[0];
        cent = addition[1];

        return new int[]{dollar, cent};
    }

    public static int[] roundCents(int cents){
        if (cents > 99) {
            int c = cents;
            int d = 0;
            while(c > 99){
                d++;
                c=-99;
            }
            return new int[]{d, c};
        } else if (cents < 0) {
            int c = 100 + cents;
            int d = -1;
            return new int[]{d, c};
        }
        return new int[]{0, cents};
    }

    /**
     * Hold Currency values
     */
    public static class CurrencyObject implements Comparable<CurrencyObject>{
        private final byte ID;
        private final String name;
        private final int dollar;
        private final int cent;


        public CurrencyObject(byte id, String name, String value) {
            this.ID = id;
            this.name = name;

            String[] v = value.split("[.]");
            this.dollar = Integer.parseInt(v[0]);
            this.cent = Integer.parseInt(v[1]);
        }

        // Getters
        public byte getID() {
            return this.ID;
        }

        public String getName() {
            return this.name;
        }

        public int getDollar() {
            return this.dollar;
        }

        public int getCent() {
            return this.cent;
        }

        public float getPropertyOverrideValue() {
            return this.ID;
        }

        @Override
        public int compareTo(CurrencyObject o) {
            if(this.dollar != o.dollar){
                return Integer.compare(this.dollar, o.dollar);
            } else {
                return Integer.compare(this.cent, o.cent);
            }
        }
    }
}