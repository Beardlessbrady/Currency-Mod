package gunn.modcurrency.mod.container;

import gunn.modcurrency.mod.container.slot.SlotCustomizable;
import gunn.modcurrency.mod.container.slot.SlotVendor;
import gunn.modcurrency.mod.container.util.INBTInventory;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tileentity.TileVending;
import gunn.modcurrency.mod.utils.UtilMethods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-08
 */
public class ContainerVending extends Container implements INBTInventory {
    //Slot Index
    //0-35 = Players Inv
    //36 = Input Slot
    //37-... Vend Slots & Buffer Slots
    public final int HOTBAR_SLOT_COUNT = 9;
    public final int PLAYER_INV_ROW_COUNT = 3;
    public final int PLAYER_INV_COLUMN_COUNT = 9;
    public final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    public final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    public int TE_VEND_COLUMN_COUNT = 3;
    public final int TE_VEND_ROW_COUNT = 5;
    public int TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;
    public final int TE_BUFFER_START = 31;
    public final int TE_BUFFER_COUNT = 4;

    public final int PLAYER_FIRST_SLOT_INDEX = 0;
    public final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    public final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;
    public int TE_BUFFER_FIRST_SLOT_INDEX = TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT;

    private List specialSlotItems;
    private TileVending tile;
    private int[] cachedFields;

    public ContainerVending(InventoryPlayer invPlayer, TileVending te) {
        specialSlotItems = new ArrayList();
        specialSlotItems.add(ModItems.itemBanknote);
        specialSlotItems.add(ModItems.itemCoin);
        specialSlotItems.add(ModItems.itemWallet);

        tile = te;
        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    private void setupPlayerInv(InventoryPlayer invPlayer) {
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 211;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++)
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));

        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 153;

        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++) {
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x;
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    private void setupTeInv() {
        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        //Input Slot
        addSlotToContainer(new SlotCustomizable(itemHandler, 0, 152, 9, specialSlotItems));

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = 50;

        if (tile.getField(tile.FIELD_TWOBLOCK) == 1) {
            TE_INV_YPOS = 32;
            TE_VEND_COLUMN_COUNT = 6;
            TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;
            TE_BUFFER_FIRST_SLOT_INDEX = TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT;
        }

        //Main Slots
        for (int y = 0; y < TE_VEND_COLUMN_COUNT; y++) {
            for (int x = 0; x < TE_VEND_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_VEND_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotVendor(itemHandler, slotNum, xpos, ypos));
            }
        }

        //Buffer Slots
        int yshift = 0;
        if (tile.getField(tile.FIELD_TWOBLOCK) == 1) yshift = 8;

        for (int i = 0; i < TE_BUFFER_COUNT; i++) {
            addSlotToContainer(new SlotCustomizable(itemHandler, TE_BUFFER_START + i, 13, 42 + yshift + i * SLOT_Y_SPACING, ModItems.itemBanknote));
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        tile.voidPlayerUsing();
        tile.setField(tile.FIELD_GEAREXT, 0);
        tile.setField(tile.FIELD_CREATIVE, 0);
        tile.setField(tile.FIELD_MODE, 0);
        tile.outInputSlot();

        super.onContainerClosed(playerIn);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.canInteractWith(playerIn);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        ItemStack itemStack = ItemStack.EMPTY;
        //Allows drag clicking
        if (slotId == -999) return super.slotClick(slotId, dragType, clickTypeIn, player);

        //Ensures Pickup_All works without duplicating blocks
        //<editor-fold desc="PICKUP_ALL">
        if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
            Slot slot = this.inventorySlots.get(slotId);
            ItemStack itemstack1 = player.inventory.getItemStack();

            if (!itemstack1.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
                int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int j = dragType == 0 ? 1 : -1;

                for (int k = 0; k < 2; ++k) {
                    for (int l = i; l >= 0 && l <= PLAYER_TOTAL_COUNT && itemstack1.getCount() < itemstack1.getMaxStackSize(); l += j) {
                        Slot slot1 = this.inventorySlots.get(l);

                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1)) {
                            ItemStack itemstack2 = slot1.getStack();

                            if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize()) {
                                int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.getCount(), itemstack2.getCount());
                                ItemStack itemstack3 = slot1.decrStackSize(i1);
                                itemstack1.grow(i1);

                                if (itemstack3.isEmpty()) {
                                    slot1.putStack(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack3);
                            }
                        }
                    }
                }
            }
            this.detectAndSendChanges();
            return ItemStack.EMPTY;
        } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId > PLAYER_TOTAL_COUNT) {
            return ItemStack.EMPTY;
        }
        //</editor-fold>

        if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < (TE_VEND_FIRST_SLOT_INDEX+ TE_VEND_MAIN_TOTAL_COUNT)) {
            if (tile.getField(tile.FIELD_MODE) == 1) { //EDIT MODE
                if (tile.getField(tile.FIELD_GEAREXT) == 1) { //Gear Tab Open
                    if (getSlot(slotId).getHasStack()) {
                        tile.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                    } else tile.setSelectedName("No Item");
                    tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                    if (!(tile.getField(tile.FIELD_SELECTSLOT) == slotId)) {
                        tile.setField(tile.FIELD_SELECTSLOT, slotId);
                        return ItemStack.EMPTY;
                    }
                } else { //Gear Tab Closed
                    if (player.inventory.getItemStack().isEmpty()) { //Player has NO ITEM, Pick up
                        ItemStack toPlayer = inventorySlots.get(slotId).getStack().copy();

                        if(dragType == 0) {
                            if (inventorySlots.get(slotId).getStack().getMaxStackSize() < tile.getItemSize(slotId - 37)) { //If slot size is GREATER than the max stack size of the item
                                toPlayer.setCount(toPlayer.getMaxStackSize());
                                tile.shrinkItemSize(toPlayer.getCount(), slotId - 37);
                                player.inventory.setItemStack(toPlayer);
                            } else { //If slot size is LESS than itemstack max size
                                toPlayer.setCount(tile.getItemSize(slotId - 37));
                                inventorySlots.get(slotId).putStack(ItemStack.EMPTY);
                                player.inventory.setItemStack(toPlayer);
                                tile.setItemSize(0, slotId - 37);
                            }
                        }else if (dragType == 1){
                            if (inventorySlots.get(slotId).getStack().getMaxStackSize() < tile.getItemSize(slotId - 37)/2) { //If slot size is GREATER than the max stack size of the item
                                toPlayer.setCount(toPlayer.getMaxStackSize());
                                tile.shrinkItemSize(toPlayer.getCount(), slotId - 37);
                                player.inventory.setItemStack(toPlayer);
                            } else { //If slot size is LESS than itemstack max size
                                toPlayer.setCount(tile.getItemSize(slotId - 37)/2);
                                player.inventory.setItemStack(toPlayer);
                                tile.shrinkItemSize(toPlayer.getCount(), slotId - 37);
                            }
                        }
                    } else { //Player has ITEM, place in slot
                        ItemStack copy = player.inventory.getItemStack().copy();
                        itemStack = copy.copy();
                        copy.setCount(1);

                        if (inventorySlots.get(slotId).getStack().isEmpty()) { //If Slot is Empty
                            inventorySlots.get(slotId).putStack(copy);

                            if(dragType == 1 && clickTypeIn == ClickType.PICKUP) { //If Player RIGHT CLICKS, place 1
                                tile.setItemSize(1, slotId - 37);
                                if(player.inventory.getItemStack().getCount() < 2){
                                    player.inventory.setItemStack(ItemStack.EMPTY);
                                }else player.inventory.getItemStack().shrink(1);



                            } else { //If Player LEFT CLICKS, place as much as possible
                                if (player.inventory.getItemStack().getCount() > tile.getField(tile.FIELD_LIMIT)) { //placed itemstack size is larger than vending limit
                                    tile.setItemSize(tile.getField(tile.FIELD_LIMIT), slotId - 37);
                                    player.inventory.getItemStack().shrink(tile.getField(tile.FIELD_LIMIT));
                                } else {
                                    tile.setItemSize(player.inventory.getItemStack().getCount(), slotId - 37); //Lower than or equal to limit, put in whole stack
                                    player.inventory.setItemStack(ItemStack.EMPTY);
                                }
                            }
                        } else if (UtilMethods.equalStacks(copy, inventorySlots.get(slotId).getStack())) { //If Slot has exact same item, grow stack size by stack size amount
                            if(dragType == 1 && clickTypeIn == ClickType.PICKUP) { //If Player RIGHT CLICKS, place 1
                                if(!(tile.getItemSize(slotId - 37) >= tile.getItemSize(tile.FIELD_LIMIT))){
                                    tile.growItemSize(1, slotId - 37);
                                    if(player.inventory.getItemStack().getCount() <= 1){
                                        player.inventory.setItemStack(ItemStack.EMPTY);
                                    }else{
                                        player.inventory.getItemStack().shrink(1);
                                    }
                                }
                            } else {
                                if (tile.getItemSize(slotId - 37) + player.inventory.getItemStack().getCount() > tile.getField(tile.FIELD_LIMIT)) { //placed itemstack size is larger than vending limit
                                    player.inventory.getItemStack().setCount(tile.getItemSize(slotId - 37) - (tile.getField(tile.FIELD_LIMIT) - player.inventory.getItemStack().getCount()));
                                    tile.setItemSize(tile.getField(tile.FIELD_LIMIT), slotId - 37);
                                } else { //Lower than or equal to limit
                                    tile.growItemSize(player.inventory.getItemStack().getCount(), slotId - 37);
                                    player.inventory.setItemStack(ItemStack.EMPTY);
                                }
                            }
                        }
                    }
                }
            } else { //SELL MODE
                ItemStackHandler vendStack = tile.getVendStackHandler();
                int stackLim;

                if (clickTypeIn == ClickType.PICKUP && dragType == 0 || clickTypeIn == ClickType.QUICK_CRAFT && dragType == 0) {   //Left Click = 1 item
                    return checkAfford(slotId, 1, player);
                } else if (clickTypeIn == ClickType.PICKUP && dragType == 1 || clickTypeIn == ClickType.QUICK_CRAFT && dragType == 1) {   //Right Click = Quarter Stack (or close to it)
                    if (tile.getField(tile.FIELD_LIMIT) > vendStack.getStackInSlot(slotId - 37).getMaxStackSize()) {
                        stackLim = vendStack.getStackInSlot(slotId - 37).getMaxStackSize() / 5;
                    } else {
                        stackLim = tile.getField(tile.FIELD_LIMIT) /5 ;
                    }

                    while (stackLim % tile.getBundleAmnt(slotId - 37) != 0) stackLim--;

                    return checkAfford(slotId, stackLim / tile.getBundleAmnt(slotId - 37), player);
                } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                    if (tile.getField(tile.FIELD_LIMIT) > vendStack.getStackInSlot(slotId - 37).getMaxStackSize()) {
                        stackLim = vendStack.getStackInSlot(slotId - 37).getMaxStackSize();
                    } else {
                        stackLim = tile.getField(tile.FIELD_LIMIT);
                    }

                    while (stackLim % tile.getBundleAmnt(slotId - 37) != 0) stackLim--;

                    return checkAfford(slotId, stackLim / tile.getBundleAmnt(slotId - 37), player);
                }
            }
            return itemStack;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private ItemStack checkAfford(int slotId, int amnt, EntityPlayer player) {
        int multiple = tile.getBundleAmnt(slotId - 37);


        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        ItemStack playStack = player.inventory.getItemStack();
        ItemStack slotStack = itemHandler.getStackInSlot(slotId - PLAYER_TOTAL_COUNT);
        ItemStack playBuyStack;

        boolean wallet = false;
        long bank = tile.getLong(tile.LONG_BANK);

        if (itemHandler.getStackInSlot(0) != ItemStack.EMPTY) {
            if (itemHandler.getStackInSlot(0).getItem() == ModItems.itemWallet) {
                wallet = true;
                bank = tile.getLong(tile.LONG_WALLETTOTAL);
            }
        }

        int cost = tile.getItemCost(slotId - PLAYER_TOTAL_COUNT - 1);

        if (slotStack != ItemStack.EMPTY) {
            if (playStack.getItem() != Item.getItemFromBlock(Blocks.AIR)) {
                if (!UtilMethods.equalStacks(playStack, slotStack)) {
                    return ItemStack.EMPTY; //Checks if player is holding stack, if its different then one being clicked do nothing
                }else{
                    if(playStack.getCount() >= playStack.getMaxStackSize()) return ItemStack.EMPTY;
                }
            }

            //If inventory is not infinite, will first check to see if buying item has enough slots for 1 multiple,
            //if it does it will check to see if it has enough for what the player is trying to buy, if not will lower till it does
            if (tile.getField(tile.FIELD_INFINITE) == 0) {
                if (tile.getItemSize(slotId - 37) < multiple) return ItemStack.EMPTY;
                while (tile.getItemSize(slotId - 37) < (amnt * multiple)) amnt --;
            }

            if(playStack.getCount() + (multiple * amnt) > playStack.getMaxStackSize())
                while (playStack.getCount() + (multiple * amnt) > playStack.getMaxStackSize()) amnt --;

            //If player can't afford there current multiple, will lower until they can,
            //if goes down to 0 amnt, play unsuccessful noise
            if(cost * amnt > bank){
                while (cost * amnt > bank) amnt--;
            }

            if (amnt == 0){
                tile.unsucessfulNoise();
                return ItemStack.EMPTY;
            }

            if ((bank >= (cost * amnt))) {   //If has enough money, buy it
                if (tile.getItemSize(slotId - 37) >= amnt*multiple || tile.getField(tile.FIELD_INFINITE) == 1) {
                    playBuyStack = slotStack.copy();
                    playBuyStack.setCount(amnt * multiple);

                    if (!player.inventory.getItemStack().isEmpty()) {       //Holding Item
                        playBuyStack.setCount((amnt * multiple) + playStack.getCount());
                    }
                    player.inventory.setItemStack(playBuyStack);

                    if (tile.getField(tile.FIELD_INFINITE) == 0) {
                        if (tile.getItemSize(slotId - 37) - (amnt * multiple) == 0) {
                            tile.setItemSize(0, slotId - 37);
                        } else tile.shrinkItemSize(amnt * multiple, slotId - 37);
                    }

                    if (wallet) {
                        sellToWallet(itemHandler.getStackInSlot(0), cost * amnt );
                    } else {
                        tile.setLong(tile.LONG_BANK, bank - (cost * amnt));
                    }
                    tile.setLong(tile.LONG_PROFIT, tile.getLong(tile.LONG_PROFIT) + cost * amnt);
                }
            } else {
                tile.unsucessfulNoise();
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack sourceStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack copyStack = slot.getStack();
            sourceStack = copyStack.copy();

            if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote || inventorySlots.get(index).getStack().getItem() == ModItems.itemWallet || inventorySlots.get(index).getStack().getItem() == ModItems.itemCoin) {
                    if (tile.getField(tile.FIELD_MODE) == 0) {
                        if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                }else{
                    if (tile.getField(tile.FIELD_MODE) == 1) {
                        for (int i = 0; i < TE_VEND_MAIN_TOTAL_COUNT; i++) {
                            if (UtilMethods.equalStacks(inventorySlots.get(index).getStack(), inventorySlots.get(TE_VEND_FIRST_SLOT_INDEX + i).getStack()) &&
                                    tile.getItemSize(i) < tile.getField(tile.FIELD_LIMIT)) {
                                if(inventorySlots.get(index).getStack().getCount() + tile.getItemSize(i) <= tile.getField(tile.FIELD_LIMIT)) { //If combined total is LESS OR EQUAL to stack limit
                                    tile.growItemSize(inventorySlots.get(index).getStack().getCount(), TE_VEND_FIRST_SLOT_INDEX + i - 37);
                                    inventorySlots.get(index).putStack(ItemStack.EMPTY);
                                    return ItemStack.EMPTY;
                                } else { //Total combined is MORE than stack size
                                    inventorySlots.get(index).getStack().setCount(tile.getItemSize(i) - (tile.getField(tile.FIELD_LIMIT) - inventorySlots.get(index).getStack().getCount()));
                                    tile.setItemSize(tile.getField(tile.FIELD_LIMIT), i);
                                    return ItemStack.EMPTY;
                                }
                            }
                        }
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index == TE_MONEY_FIRST_SLOT_INDEX) {
                if (tile.getField(tile.FIELD_MODE) == 0) {
                    if (index == 36) {
                        if (!this.mergeItemStack(copyStack, PLAYER_FIRST_SLOT_INDEX, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + 4) {  //TE Inventory
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                return ItemStack.EMPTY;
            }

            if (copyStack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean fieldChanged[] = new boolean[tile.getFieldCount()];

        if (cachedFields == null) cachedFields = new int[tile.getFieldCount()];

        for (int i = 0; i < cachedFields.length; i++) {
            if (cachedFields[i] != tile.getField(i)) {
                cachedFields[i] = tile.getField(i);
                fieldChanged[i] = true;
            }
        }

        for (IContainerListener listener : this.listeners) {
            for (int field = 0; field < tile.getFieldCount(); ++field) {
                if (fieldChanged[field]) {
                    listener.sendWindowProperty(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tile.setField(id, data);
    }

    private int getTotalOfBill(ItemStack stack, int billDamage) {
        ItemStackHandler itemStackHandler = readInventoryTag(stack, ItemWallet.WALLET_TOTAL_COUNT);

        int totalOfBill = 0;
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            if (itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                if(itemStackHandler.getStackInSlot(i).getItem() == ModItems.itemBanknote) {
                    if (itemStackHandler.getStackInSlot(i).getItemDamage() == billDamage) {
                        totalOfBill = totalOfBill + itemStackHandler.getStackInSlot(i).getCount();
                    }
                }
            }
        }
        return totalOfBill;
    }

    private int getTotalOfCoin(ItemStack stack, int coinDamage){
        ItemStackHandler itemStackHandler = readInventoryTag(stack, ItemWallet.WALLET_TOTAL_COUNT);

        int totalOfCoin = 0;
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            if (itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                if(itemStackHandler.getStackInSlot(i).getItem() == ModItems.itemCoin) {
                    if (itemStackHandler.getStackInSlot(i).getItemDamage() == coinDamage) {
                        totalOfCoin = totalOfCoin + itemStackHandler.getStackInSlot(i).getCount();
                    }
                }
            }
        }
        return totalOfCoin;
    }

    private int getBillWorth(int itemDamage, int stackSize) {
        int cash = 0;
        switch (itemDamage) {
            case 0:
                cash = 100;
                break;
            case 1:
                cash = 500;
                break;
            case 2:
                cash = 1000;
                break;
            case 3:
                cash = 2000;
                break;
            case 4:
                cash = 5000;
                break;
            case 5:
                cash = 10000;
                break;
        }

        return cash * stackSize;
    }

    private int getCoinWorth(int itemDamage, int stackSize) {
        int cash = 0;
        switch (itemDamage) {
            case 0:
                cash = 1;
                break;
            case 1:
                cash = 5;
                break;
            case 2:
                cash = 10;
                break;
            case 3:
                cash = 25;
                break;
            case 4:
                cash = 100;
                break;
            case 5:
                cash = 200;
                break;
        }

        return cash * stackSize;
    }

    private void sellToWallet(ItemStack wallet, int amountRemovable) {
        int amount = amountRemovable;

        int five = getTotalOfBill(wallet, 1);
        int ten = getTotalOfBill(wallet, 2);
        int twenty = getTotalOfBill(wallet, 3);
        int fifty = getTotalOfBill(wallet, 4);
        int hundo = getTotalOfBill(wallet, 5);

        int[] out = new int[6];

        out[5] = Math.round(amount / 10000);
        while (out[5] > hundo) out[5]--;
        amount = amount - (out[5] * 10000);

        out[4] = Math.round(amount / 5000);
        while (out[4] > fifty) out[4]--;
        amount = amount - (out[4] * 5000);

        out[3] = Math.round(amount / 2000);
        while (out[3] > twenty) out[3]--;
        amount = amount - (out[3] * 2000);

        out[2] = Math.round(amount / 1000);
        while (out[2] > ten) out[2]--;
        amount = amount - (out[2] * 1000);

        out[1] = Math.round(amount / 500);
        while (out[1] > five) out[1]--;
        amount = amount - (out[1] * 500);

        int oneCent = getTotalOfCoin(wallet, 0);
        int fiveCent = getTotalOfCoin(wallet, 1);
        int tenCent = getTotalOfCoin(wallet, 2);
        int twentyFiveCent = getTotalOfCoin(wallet, 3);
        int oneDollar = getTotalOfCoin(wallet, 4);
        int twoDollar = getTotalOfCoin(wallet, 5);

        int[] outCoin = new int[6];

        outCoin[5] = Math.round(amount / 200);
        while (outCoin[5] > twoDollar) outCoin[5]--;
        amount = amount - (outCoin[5] * 200);

        outCoin[4] = Math.round(amount / 100);
        while (outCoin[4] > oneDollar) outCoin[4]--;
        amount = amount - (outCoin[4] * 100);

        outCoin[3] = Math.round(amount / 25);
        while (outCoin[3] > twentyFiveCent) outCoin[3]--;
        amount = amount - (outCoin[3] * 25);

        outCoin[2] = Math.round(amount / 10);
        while (outCoin[2] > tenCent) outCoin[2]--;
        amount = amount - (outCoin[2] * 10);

        outCoin[1] = Math.round(amount / 5);
        while (outCoin[1] > fiveCent) outCoin[1]--;
        amount = amount - (outCoin[1] * 5);

        outCoin[0] = Math.round(amount / 1);
        while (outCoin[0] > oneCent) outCoin[0]--;
        amount = amount - (outCoin[0] * 1);

        ItemStackHandler itemHandler = readInventoryTag(wallet, ItemWallet.WALLET_TOTAL_COUNT);

        for (int i = 0; i < 6; i++) {
            searchLoop:
            for (int j = 0; j < itemHandler.getSlots(); j++) {
                ItemStack stack = itemHandler.getStackInSlot(j);

                if (stack != ItemStack.EMPTY) {
                    if(stack.getItem() == ModItems.itemBanknote){
                        if (stack.getItemDamage() == i) {
                            if (stack.getCount() >= out[i]) {  //If Stack size can handle all amount of bill
                                itemHandler.getStackInSlot(j).shrink(out[i]);
                                out[0] = 0;

                                break searchLoop;
                            } else {  //If Stack size is smaller then amount of bills
                                out[i] = out[i] - stack.getCount();
                                itemHandler.setStackInSlot(j, ItemStack.EMPTY);
                            }
                        }
                    }

                    if(stack.getItem() == ModItems.itemCoin){
                        if (stack.getItemDamage() == i) {
                            if (stack.getCount() >= outCoin[i]) {  //If Stack size can handle all amount of bill
                                itemHandler.getStackInSlot(j).shrink(outCoin[i]);
                                outCoin[0] = 0;

                                break searchLoop;
                            } else {  //If Stack size is smaller then amount of bills
                                outCoin[i] = outCoin[i] - stack.getCount();
                                itemHandler.setStackInSlot(j, ItemStack.EMPTY);
                            }
                        }
                    }





                    if (stack.getCount() == 0) itemHandler.setStackInSlot(j, ItemStack.EMPTY);  //Removes stack is 0
                }
            }
        }
        /*If there is still an amount that needs to be paid for,
        will pick the closest highest bill in the wallet, remove it
        and send the 'change' to the bank variable of the vendor then output it with outChange
         */
        if (amount != 0) {
            System.out.println("DESS");
            int itemDamage = 0;
            searchLoop:
            //Searches wallet for the first highest bill/coin that can handle rest of amount
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (itemHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                    if (itemHandler.getStackInSlot(i).getItem() == ModItems.itemBanknote) {
                        int billWorth = getBillWorth(itemHandler.getStackInSlot(i).getItemDamage(), itemHandler.getStackInSlot(i).getCount());
                        if (billWorth > amount) {
                            itemDamage = itemHandler.getStackInSlot(i).getItemDamage() + 6;
                            if (itemHandler.getStackInSlot(i).getCount() == 1) {
                                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                            } else {
                                itemHandler.getStackInSlot(i).shrink(1);
                            }
                            break searchLoop;
                        }
                    } else if (itemHandler.getStackInSlot(i).getItem() == ModItems.itemCoin) {
                        int coinWorth = getCoinWorth(itemHandler.getStackInSlot(i).getItemDamage(), itemHandler.getStackInSlot(i).getCount());
                        System.out.println(coinWorth);
                        if (coinWorth > amount) {
                            itemDamage = itemHandler.getStackInSlot(i).getItemDamage();
                            if (itemHandler.getStackInSlot(i).getCount() == 1) {
                                itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                            } else {
                                itemHandler.getStackInSlot(i).shrink(1);
                            }
                            break searchLoop;
                        }
                    }
                }
            }

            //Calculates change by deducting the bills/coins worth with the amount deductible that was left
            int change = 0;
            switch (itemDamage) {
                case 0:
                    change = 0;
                    break;
                case 1:
                    change = 5 - amount;
                    break;
                case 2:
                    change = 10 - amount;
                    break;
                case 3:
                    change = 25 - amount;
                    break;
                case 4:
                    change = 100 - amount;
                    break;
                case 5:
                    change = 200 - amount;
                    break;
                case 6:
                    change = 100 - amount;
                    break;
                case 7:
                    change = 500 - amount;
                    break;
                case 8:
                    change = 1000 - amount;
                    break;
                case 9:
                    change = 2000 - amount;
                    break;
                case 10:
                    change = 5000 - amount;
                    break;
                case 11:
                    change = 10000 - amount;
                    break;
            }
            //Adds change to the bank variable in vendor
            tile.setLong(tile.LONG_BANK, tile.getLong(tile.LONG_BANK) + change);
        }
        writeInventoryTag(wallet, itemHandler);
        tile.outChange();
    }
}
