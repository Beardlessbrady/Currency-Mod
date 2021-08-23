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
    private int storedCash = 0;
    private int playerCash = 0;
    private int editPrice = 0;

    public VendingStateData(){
    }

    public VendingStateData(int[] array){
        for(int i = 0; i < array.length; i++){
            this.set(i, array[i]);
        }
    }

    public void putIntoNBT(CompoundNBT compoundNBT){
        compoundNBT.putInt("mode", mode);
        compoundNBT.putInt("storedCash", storedCash);
        compoundNBT.putInt("playerCash", playerCash);
        compoundNBT.putInt("editPrice", editPrice);
    }

    public void readFromNBT(CompoundNBT compoundNBT){
        mode = compoundNBT.getInt("mode");
        storedCash = compoundNBT.getInt("storedCash");
        playerCash = compoundNBT.getInt("playerCash");
        editPrice = compoundNBT.getInt("editPrice");
    }

    // Vanilla Stuff, NO TOUCH
    public static final int MODE_INDEX = 0;
    public static final int STOREDCASH_INDEX = 1;
    public static final int PLAYERCASH_INDEX = 2;
    public static final int EDITPRICE_INDEX = 3;
    public static final int END_OF_INDEX_PLUS_ONE = 3 + 1;

    @Override
    public int get(int index) {
        validateIndex(index);
        switch (index){
            case MODE_INDEX:
                return mode;
            case STOREDCASH_INDEX:
                return storedCash;
            case PLAYERCASH_INDEX:
                return playerCash;
            case EDITPRICE_INDEX:
                return editPrice;
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
            case STOREDCASH_INDEX:
                storedCash = value;
                break;
            case PLAYERCASH_INDEX:
                playerCash = value;
                break;
            case EDITPRICE_INDEX:
                editPrice = value;
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
