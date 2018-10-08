package beard.modcurrency.item;

import net.minecraft.item.Item;
import net.minecraft.util.IStringSerializable;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-25
 */
public class ItemCurrency extends Item{

    public ItemCurrency(){

        setRegistryName("currency");
        setUnlocalizedName(getRegistryName().toString());
    }

}

