package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IIntArray;

/**
 * Created by BeardlessBrady on 2021-03-05 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingStateData implements IIntArray {

    private int mode = 0;
    private int incomeDollar = 0;
    private int incomeCent = 0;
    private int cashDollar = 0;
    private int cashCent = 0;
    private int editPrice = 0;
    private int selectedSlot = 0;
    private int guiSide = 0; // 0 = Left 1 = Right
    private int buyMode = 0; // 0=1, 1=Half Stack (ALT), 2=Full Stack (SHIFT)

    public VendingStateData(){
    }

    public VendingStateData(int[] array){
        for(int i = 0; i < array.length; i++){
            this.set(i, array[i]);
        }
    }

    public void putIntoNBT(CompoundNBT compoundNBT){
        compoundNBT.putInt("mode", mode);
        compoundNBT.putInt("incomeDollar", incomeDollar);
        compoundNBT.putInt("incomeCent", incomeCent);
        compoundNBT.putInt("cashDollar", cashDollar);
        compoundNBT.putInt("cashCent", cashCent);
        compoundNBT.putInt("editPrice", editPrice);
        compoundNBT.putInt("selectedSlot", selectedSlot);
    }

    public void readFromNBT(CompoundNBT compoundNBT){
        mode = compoundNBT.getInt("mode");
        incomeDollar = compoundNBT.getInt("incomeDollar");
        incomeCent = compoundNBT.getInt("incomeCent");
        cashDollar = compoundNBT.getInt("cashDollar");
        cashCent = compoundNBT.getInt("cashCent");
        editPrice = compoundNBT.getInt("editPrice");
        selectedSlot = compoundNBT.getInt("selectedSlot");
    }

    // Vanilla Stuff, NO TOUCH
    public static final int MODE_INDEX = 0;
    public static final int INCOMEDOLLAR_INDEX = 1;
    public static final int INCOMECENT_INDEX = 2;
    public static final int CASHDOLLAR_INDEX = 3;
    public static final int CASHCENT_INDEX = 4;
    public static final int EDITPRICE_INDEX = 5;
    public static final int GUISIDE_INDEX = 6;
    public static final int SELECTEDSLOT_INDEX = 7;
    public static final int END_OF_INDEX_PLUS_ONE = 7 + 1;

    @Override
    public int get(int index) {
        validateIndex(index);
        switch (index){
            case MODE_INDEX:
                return mode;
            case INCOMEDOLLAR_INDEX:
                return incomeDollar;
            case INCOMECENT_INDEX:
                return incomeCent;
            case CASHDOLLAR_INDEX:
                return cashDollar;
            case CASHCENT_INDEX:
                return cashCent;
            case EDITPRICE_INDEX:
                return editPrice;
            case GUISIDE_INDEX:
                return guiSide;
            case SELECTEDSLOT_INDEX:
                return selectedSlot;
            default:
                return -1;
        }
    }

    @Override
    public void set(int index, int value) {
        validateIndex(index);
        switch (index){
            case MODE_INDEX:
                mode = value;
                if(mode == 1) // STOCK
                    editPrice = 0;
                break;
            case INCOMEDOLLAR_INDEX:
                incomeDollar = value;
                break;
            case INCOMECENT_INDEX:
                incomeCent = value;
                break;
            case CASHDOLLAR_INDEX:
                cashDollar = value;
                break;
            case CASHCENT_INDEX:
                cashCent = value;
                break;
            case EDITPRICE_INDEX:
                editPrice = value;
                break;
            case GUISIDE_INDEX:
                guiSide = value;
                break;
            case SELECTEDSLOT_INDEX:
                selectedSlot = value;
                break;
        }
    }

    @Override
    public int size() {
        return END_OF_INDEX_PLUS_ONE;
    }

    private void validateIndex(int index) throws IndexOutOfBoundsException {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException("Index out of bounds:"+index);
        }
    }
}
