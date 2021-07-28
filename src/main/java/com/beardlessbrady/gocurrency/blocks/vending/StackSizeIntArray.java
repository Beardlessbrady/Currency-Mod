package com.beardlessbrady.gocurrency.blocks.vending;

import net.minecraft.util.IIntArray;
import net.minecraft.util.NonNullList;

/**
 * Created by BeardlessBrady on 2021-07-25 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class StackSizeIntArray implements IIntArray {
    private final NonNullList<Integer> intSize;

    public StackSizeIntArray(int size){
        intSize = NonNullList.withSize(size, 0);
        END_OF_INDEX_PLUS_ONE = intSize.size() + 1;
    }

    // Vanilla Stuff, NO TOUCH
    public static int END_OF_INDEX_PLUS_ONE;

    @Override
    public int get(int index) {
        if (index >= 0 && index < intSize.size()) {
            validateIndex(index);
            return intSize.get(index);
        }

        return 0;
    }

    @Override
    public void set(int index, int value) {
        if (index >= 0 && index < intSize.size()) {
            validateIndex(index);
            intSize.set(index, value);
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