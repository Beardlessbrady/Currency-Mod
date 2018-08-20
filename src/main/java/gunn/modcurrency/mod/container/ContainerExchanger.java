package gunn.modcurrency.mod.container;

import gunn.modcurrency.mod.container.slot.SlotVendor;
import gunn.modcurrency.mod.container.util.INBTInventory;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tileentity.TileExchanger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
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
public class ContainerExchanger extends Container implements INBTInventory {
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
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 152, 9));

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
            addSlotToContainer(new SlotItemHandler(itemHandler, TE_BUFFER_START + i, 13, 42 + yshift + i * SLOT_Y_SPACING));
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
        //Allows Drag clicking
        if(slotId == -999) return super.slotClick(slotId, dragType, clickTypeIn, player);

        //Ensures Pickup_All works without duplicating blocks
        if(clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
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
        }else if (clickTypeIn == ClickType.PICKUP_ALL && slotId > PLAYER_TOTAL_COUNT) {
            return ItemStack.EMPTY;
        }

        if (tile.getField(tile.FIELD_MODE) == 1) {      //EDIT MODE
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if ((slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT && tile.getField(tile.FIELD_GEAREXT) == 0)) {    //Vend Slots, normal Edit Mode
                if ((!player.inventory.isEmpty()) && inventorySlots.get(slotId).getStack().isEmpty()) { //Player hand FULL, Slot EMPTY. Put ghost stack here
                    ItemStack ghostStack = player.inventory.getItemStack().copy();
                    ghostStack.setCount(1);
                    this.inventorySlots.get(slotId).putStack(ghostStack);
                } else { //Anything else, remove stack in slot
                    this.inventorySlots.get(slotId).putStack(ItemStack.EMPTY);
                }
                return player.inventory.getItemStack();
            } else if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT && tile.getField(tile.FIELD_GEAREXT) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                tile.setField(tile.FIELD_SELECTSLOT, slotId);
                if (getSlot(slotId).getHasStack()) {
                    tile.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                } else {
                    tile.setSelectedName("No Item");
                }
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                return ItemStack.EMPTY;
            }else if (slotId >= TE_BUFFER_FIRST_SLOT_INDEX && slotId < TE_BUFFER_FIRST_SLOT_INDEX + TE_BUFFER_COUNT){
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            }
        } else {  //Sell Mode
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {           //Is Players Inv or Input Slot
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= TE_VEND_FIRST_SLOT_INDEX && slotId < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT) {  //Is TE Inv
                return ItemStack.EMPTY;
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
                if (tile.getField(tile.FIELD_MODE) == 0) {     //SELL MODE
                    if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    if (tile.getField(tile.FIELD_MODE) == 1) {
                        if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote || inventorySlots.get(index).getStack().getItem() == ModItems.itemCoin) {
                            if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else return ItemStack.EMPTY;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + 1) {  //TE Inventory
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == TE_MONEY_FIRST_SLOT_INDEX) {
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= TE_BUFFER_FIRST_SLOT_INDEX && index < TE_BUFFER_FIRST_SLOT_INDEX + TE_BUFFER_COUNT){
                if(tile.getField(tile.FIELD_MODE) == 1){
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                }else{
                    return ItemStack.EMPTY;
                }
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
