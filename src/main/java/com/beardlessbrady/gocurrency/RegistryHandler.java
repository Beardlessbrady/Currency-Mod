package com.beardlessbrady.gocurrency;

import com.beardlessbrady.gocurrency.blocks.DesignerBlock;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Created by BeardlessBrady on 2021-02-22 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class RegistryHandler {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GOCurrency.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GOCurrency.MODID);

    /**
     * Initializes registry
     */
    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    // Items
    public static final RegistryObject<Item> ITEM_CURRENCY = ITEMS.register("currency", () ->
            new CurrencyItem(new Item.Properties().group(ItemGroup.MATERIALS)));

    // Blocks
    public static final RegistryObject<Block> BLOCK_DESIGNER = BLOCKS.register("designer", () ->
            new DesignerBlock(Block.Properties.create(Material.ROCK)));

    // ItemBlocks
    public static final RegistryObject<Item> BLOCKITEM_DESIGNER = ITEMS.register("designer", () ->
            new BlockItem(BLOCK_DESIGNER.get(), new Item.Properties().group(ItemGroup.MATERIALS)));

}
