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

        if (tile.getField(tile.FIELD_MODE) == 1) { //EDIT MODE
            if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < (TE_VEND_FIRST_SLOT_INDEX+ TE_VEND_MAIN_TOTAL_COUNT)){
                if(tile.getField(tile.FIELD_GEAREXT) == 1){ //Gear Tab Open

                }else{ //Gear Tab Closed
                    if(player.inventory.getItemStack().equals(ItemStack.EMPTY)){ //Player has NO ITEM, Pick up


                    }else{ //Player has ITEM, place in slot
                        ItemStack copy = player.inventory.getItemStack().copy();
                        copy.setCount(1);

                        if(inventorySlots.get(slotId).getStack().equals(ItemStack.EMPTY)) { //If Slot is Empty
                            inventorySlots.get(slotId).putStack(copy);
                            tile.setItemSize(player.inventory.getItemStack().getCount(), slotId - 37);
                            player.inventory.setItemStack(ItemStack.EMPTY);
                        }else if(UtilMethods.equalStacks(copy, inventorySlots.get(slotId).getStack())){ //If Slot has exact same item, grow stack size by stack size amount
                            tile.growItemSize(player.inventory.getItemStack().getCount(), slotId - 37);
                            player.inventory.setItemStack(ItemStack.EMPTY);
                        }



                    }
                }
                return ItemStack.EMPTY;
            }
            return super.slotClick(slotId, dragType, clickTypeIn, player);

        } else { //SELL MODE

        }


        return ItemStack.EMPTY;
    }

    private ItemStack checkAfford(int slotId, int amnt, EntityPlayer player) {
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
                }
            }
            if (tile.getField(tile.FIELD_INFINITE) == 0)
                if (slotStack.getCount() < amnt && slotStack.getCount() != 0) amnt = slotStack.getCount();

            //If cant afford amount requested lowers amount till can afford or till amount is 1
            lower:
            while ((cost * amnt) > bank) {
                if (amnt == 1) break lower;
                amnt--;
            }

            if ((bank >= (cost * amnt))) {   //If has enough money, buy it
                if (slotStack.getCount() >= amnt || tile.getField(tile.FIELD_INFINITE) == 1) {
                    playBuyStack = slotStack.copy();
                    playBuyStack.setCount(amnt);

                    if (!player.inventory.getItemStack().isEmpty()) {       //Holding Item
                        playBuyStack.setCount(amnt + playStack.getCount());
                    }
                    player.inventory.setItemStack(playBuyStack);

                    if (tile.getField(tile.FIELD_INFINITE) == 0) {
                        if (slotStack.getCount() - amnt == 0) {
                        //    tile.setGhost(slotId - PLAYER_TOTAL_COUNT - 1, true);
                            slotStack.setCount(1);
                        } else slotStack.splitStack(amnt);
                    }

                    if (wallet) {
                        sellToWallet(itemHandler.getStackInSlot(0), cost * amnt);
                    } else {
                        tile.setLong(tile.LONG_BANK, bank - (cost * amnt));
                    }
                    tile.setLong(tile.LONG_PROFIT, tile.getLong(tile.LONG_PROFIT) + cost * amnt);
                }
            } else {
                tile.unsucessfulNoise();
            }
            return slotStack;
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
                } else {
                    if (tile.getField(tile.FIELD_MODE) == 1) {     //Only allow shift clicking from player inv in edit mode
                        if (!this.mergeItemStack(copyStack, TE_VEND_FIRST_SLOT_INDEX, TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT, false)) {
                            return ItemStack.EMPTY;
                        }else{ //If successful transfer to vending machine inventory
                           // tile.checkForWrongGhosts();
                        }
                    } else {
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
        return sourceStack;
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

    private void sellToWallet(ItemStack wallet, int amountRemovable) {
        //Todo
        }
}
