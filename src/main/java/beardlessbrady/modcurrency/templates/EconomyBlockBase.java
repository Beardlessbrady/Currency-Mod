package beardlessbrady.modcurrency.templates;

import beardlessbrady.modcurrency.ModCurrency;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-08
 */

public class EconomyBlockBase extends Block {

    public EconomyBlockBase(String name) {
        super(Material.ROCK);
        setRegistryName(name);
        setUnlocalizedName(name);
        setHardness(3.0F);
        setCreativeTab(ModCurrency.tabCurrency);
        setSoundType(SoundType.METAL);
    }
}
