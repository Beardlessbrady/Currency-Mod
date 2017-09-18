package gunn.modcurrency.mod.container;

import gunn.modcurrency.mod.client.gui.util.INBTInventory;
import gunn.modcurrency.mod.container.slot.SlotCustomizable;
import gunn.modcurrency.mod.container.slot.SlotVendor;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tileentity.TileExchanger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-08
 */
public class ContainerExchanger extends Container implements INBTInventory{
    //Slot Index
    //0-35 = Players Inv
    //36 = Input Slot
    //37-... Vend Slots & Buffer Slots
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    private int TE_VEND_COLUMN_COUNT = 3;
    private final int TE_VEND_ROW_COUNT = 5;
    private int TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    private final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;
    //private final int TE_VEND_BUFFER_SLOT = TE_VEND_MAIN_TOTAL_COUNT + 1;

    private Item specialSlotItems;
    private TileExchanger tile;
    private int[] cachedFields;

    public ContainerExchanger(InventoryPlayer invPlayer, TileExchanger te) {
        specialSlotItems = ModItems.itemBanknote;

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
        if (tile.getField(2) == 0) addSlotToContainer(new SlotItemHandler(itemHandler, 0, 152, 9));
        if (tile.getField(2) == 1) addSlotToContainer(new SlotCustomizable(itemHandler, 0, 152, 9, specialSlotItems));

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = 50;

        if(tile.getField(7) == 1) {
            TE_INV_YPOS = 32;
            TE_VEND_COLUMN_COUNT = 6;
            TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;
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
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        tile.voidPlayerUsing();
        tile.setField(8, 0);
        tile.setField(5, 0);
        tile.setField(2, 0);
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
        //Allows Drag clicking
        if (slotId == -999) return super.slotClick(slotId, dragType, clickTypeIn, player);

        //Ensures Pickup_All works without duplicating blocks
        if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
            Slot slot = this.inventorySlots.get(slotId);
            ItemStack itemstack1 = player.inventory.getItemStack();

            if (itemstack1 != null && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
                int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int j = dragType == 0 ? 1 : -1;

                for (int k = 0; k < 2; ++k) {
                    for (int l = i; l >= 0 && l <= PLAYER_TOTAL_COUNT && itemstack1.stackSize < itemstack1.getMaxStackSize(); l += j) {
                        Slot slot1 = this.inventorySlots.get(l);

                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack1, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack1, slot1)) {
                            ItemStack itemstack2 = slot1.getStack();

                            if (k != 0 || itemstack2.stackSize != itemstack2.getMaxStackSize()) {
                                int i1 = Math.min(itemstack1.getMaxStackSize() - itemstack1.stackSize, itemstack2.stackSize);
                                ItemStack itemstack3 = slot1.decrStackSize(i1);
                                itemstack1.stackSize = itemstack1.stackSize + i1;

                                if (itemstack3 == null) {
                                    slot1.putStack(null);
                                }

                                slot1.func_82870_a(player, itemstack3);
                            }
                        }
                    }
                }
            }
            this.detectAndSendChanges();
            return null;
        } else if (clickTypeIn == ClickType.PICKUP_ALL && slotId > PLAYER_TOTAL_COUNT) {
            return null;
        }

        if (tile.getField(2) == 1) {      //EDIT MODE
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if ((slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT && tile.getField(8) == 0)) {    //Vend Slots, normal Edit Mode
                if (player.inventory.getItemStack() != null && inventorySlots.get(slotId).getStack() == null) { //Player hand FULL, Slot EMPTY. Put ghost stack here
                    ItemStack ghostStack = player.inventory.getItemStack().copy();
                    ghostStack.stackSize = 1;
                    this.inventorySlots.get(slotId).putStack(ghostStack);
                } else { //Anything else, remove stack in slot
                    this.inventorySlots.get(slotId).putStack(null);
                }
                return player.inventory.getItemStack();
            } else if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT && tile.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                tile.setField(3, slotId);
                if (getSlot(slotId).getHasStack()) {
                    tile.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                } else {
                    tile.setSelectedName("No Item");
                }
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                return null;
            } else if (slotId > 66 && slotId < 73) {
                return super.slotClick(slotId, dragType, clickTypeIn, player); //Buffer Slots
            } else return null;
        } else {  //Sell Mode
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {           //Is Players Inv or Input Slot
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT) {  //Is TE Inv
                return null;
            }
        }
        return null;
    }


        /*
        if (tile.getField(2) == 1) {      //EDIT MODE
            if (slotId >= 0 && slotId <= 36) {
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if ((slotId >= 37 && slotId < 67 && tile.getField(8) == 0)) {    //Vend Slots, not in Selection mode
                InventoryPlayer inventoryPlayer = player.inventory;
                Slot ghostSlot = this.inventorySlots.get(slotId);
                if (clickTypeIn == ClickType.PICKUP) {      //LEFT
                    if (inventoryPlayer.getItemStack() != null && inventorySlots.get(slotId).getStack() == null) {
                        ItemStack ghostStack = inventoryPlayer.getItemStack().copy();
                        int gCount = 1;
                        if (tile.getItemAmount(slotId - 37) > 1) {
                            gCount = tile.getItemAmount(slotId - 37);
                        }

                        ghostStack.stackSize=(gCount);
                        ghostSlot.putStack(ghostStack);
                    } else {
                        tile.setItemAmount(-1, slotId - 37);
                        ghostSlot.putStack(null);
                    }
                }
                return inventoryPlayer.getItemStack();
            } else if (slotId >= 37 && slotId < 67 && tile.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                tile.setField(3, slotId);
                if (getSlot(slotId).getHasStack()) {
                    tile.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                } else {
                    tile.setSelectedName("No Item");
                }
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                return null;
            } else if (slotId > 66 && slotId < 73) {
                return super.slotClick(slotId, dragType, clickTypeIn, player); //Buffer Slots
            } else return null;
        } else {  //Sell Mode
            if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Input Slot
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= 37 && slotId < 67) {  //Is TE Inv
                return null;
            }
        }
       return null;
    }*/

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack sourceStack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack copyStack = slot.getStack();
            sourceStack = copyStack.copy();

            if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                if (tile.getField(2) == 0) {     //SELL MODE
                    if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                        return null;
                    }
                } else {
                    if (tile.getField(2) == 1) {
                        if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote) {
                            if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                                return null;
                            }
                        } else return null;
                    } else {
                        return null;
                    }
                }
            } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + 1) {  //TE Inventory
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return null;
                }
            } else if (index == TE_MONEY_FIRST_SLOT_INDEX) {
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return null;
                }
            }

            if (copyStack.stackSize == 0) {
                slot.putStack(null);
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
                if (fieldChanged[field]){
                    listener.sendWindowProperty(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tile.setField(id, data);
    }

}
