package gunn.modcurrency.common.containers;

import gunn.modcurrency.common.items.ModItems;
import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import gunn.modcurrency.common.core.util.SlotBank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2016-11-02.
 */
public class ContainerBuySell extends Container {
    //Slot Index's
    //0-35 = Player Inventory's
    //36 = Money Slot
    //37-67 = Vend Slots
    //68-73 = Buffer Slots
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    private final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;

    private final int TE_VEND_COLUMN_COUNT = 6;
    private final int TE_VEND_ROW_COUNT = 5;
    private final int TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;

    private final int TE_BUFFER_TOTAL_COUNT = 6;

    private ModTile tile;
    private int[] cachedFields;

    public ContainerBuySell(InventoryPlayer invPlayer, ModTile te) {
        tile = te;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        tile.voidPlayerUsing();
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
        IItemHandler itemHandler  = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        //Input Slot
        if(tile instanceof TileVendor){
            int xpos = 0;
            if(tile.getField(2) == 0) xpos = 152;
            if(tile.getField(2) == 1) xpos = -1000;
            addSlotToContainer(new SlotBank(itemHandler, 0, xpos,9));
        }
        if(tile instanceof TileSeller){
            if(tile.getField(2) == 0) addSlotToContainer(new SlotItemHandler(itemHandler, 0, 152, 9));
            if(tile.getField(2) == 1) addSlotToContainer(new SlotBank(itemHandler, 0, 152, 9));
        }

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        final int TE_INV_YPOS = 32;

        //Main Slots
        for (int y = 0; y < TE_VEND_COLUMN_COUNT; y++) {
            for (int x = 0; x < TE_VEND_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_VEND_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(itemHandler, slotNum, xpos, ypos));
            }
        }

        //Buffer Slots
        for (int x = 0; x < TE_BUFFER_TOTAL_COUNT; x++){
            int slotNum = TE_VEND_MAIN_TOTAL_COUNT + 1 + x;
            int xpos = 0;
            if(tile.getField(2) == 1) xpos = 15;
            if(tile.getField(2) == 0) xpos = -1000;
            int ypos = 32 + x * 18;
            if(tile instanceof TileVendor) addSlotToContainer(new SlotBank(itemHandler,slotNum,xpos,ypos));
            if(tile instanceof TileSeller) addSlotToContainer(new SlotItemHandler(itemHandler,slotNum,xpos,ypos));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.canInteractWith(playerIn);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (tile instanceof TileVendor) {
            //<editor-fold desc="Vendor Slot Click">
            if (tile.getField(2) == 1) {               //EDIT MODE
                if (slotId >= 0 && slotId <= 36) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67 && tile.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                    tile.setField(3, slotId);
                    if (getSlot(slotId).getHasStack()) {
                        tile.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                    } else {
                        tile.setSelectedName("No Item");
                    }
                    tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                    return null;
                } else if (slotId >= 37 && slotId < 67 && tile.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 1) {
                    return super.slotClick(slotId, 0, clickTypeIn, player);
                } else {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }
            } else {  //Sell Mode
                if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Input Slot
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67) {  //Is TE Inv
                    if (clickTypeIn == ClickType.PICKUP && dragType == 0) {   //Left Click = 1 item
                        return checkAfford(slotId, 1, player);
                    } else if (clickTypeIn == ClickType.PICKUP && dragType == 1) {   //Right Click = 10 item
                        return checkAfford(slotId, 10, player);
                    } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                        return checkAfford(slotId, 64, player);
                    } else {
                        return null;
                    }
                }
            }
            //</editor-fold>
        } else if (tile instanceof TileSeller) {
            //<editor-fold desc="Seller Slot Click">
            if (tile.getField(2) == 1) {      //EDIT MODE
                if (slotId >= 0 && slotId <= 36) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if ((slotId >= 37 && slotId < 67 && tile.getField(8) == 0)) {    //Vend Slots, not in Selection mode
                    InventoryPlayer inventoryPlayer = player.inventory;
                    Slot ghostSlot = this.inventorySlots.get(slotId);
                    if (dragType == 0 && clickTypeIn == ClickType.PICKUP) {      //LEFT
                        if (inventoryPlayer.getItemStack() != null) {
                            ItemStack ghostStack = inventoryPlayer.getItemStack().copy();

                            ghostStack.stackSize = 1;
                            ghostSlot.putStack(ghostStack);
                        }
                    } else if (dragType == 1 && clickTypeIn == ClickType.PICKUP) {  //RIGHT
                        ghostSlot.putStack(null);
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
                } else if (slotId > 66 && slotId < 73){
                    return super.slotClick(slotId, dragType, clickTypeIn, player); //Buffer Slots
                } else return null;
            } else {  //Sell Mode
                if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Input Slot
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67) {  //Is TE Inv
                    return null;
                }
            }
            //</editor-fold>
        }
        return null;
    }

    public ItemStack checkAfford(int slotId, int amnt, EntityPlayer player) {
        if (tile instanceof TileVendor) {
            IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack playStack = player.inventory.getItemStack();
            ItemStack slotStack = itemHandler.getStackInSlot(slotId - PLAYER_TOTAL_COUNT);
            ItemStack playBuyStack;
            int bank = tile.getField(0);
            int cost = tile.getItemCost(slotId - PLAYER_TOTAL_COUNT - 1);

            if (slotStack != null) {
                if (playStack != null) {
                    if (!((playStack.getDisplayName().equals(slotStack.getDisplayName())) &&
                            (playStack.getItem().getUnlocalizedName().equals(slotStack.getItem().getUnlocalizedName())))) {
                        return null; //Checks if player is holding stack, if its different then one being clicked do nothing
                    }
                }
                if (tile.getField(6) == 0)
                    if (slotStack.stackSize < amnt && slotStack.stackSize != 0) amnt = slotStack.stackSize;

                if ((bank >= (cost * amnt))) {   //If has enough money, buy it
                    if (slotStack.stackSize >= amnt || tile.getField(6) == 1) {
                        if (tile.getField(6) == 0) slotStack.splitStack(amnt);
                        playBuyStack = slotStack.copy();
                        playBuyStack.stackSize = amnt;

                        if (player.inventory.getItemStack() != null) {       //Holding Item
                            playBuyStack.stackSize = amnt + playStack.stackSize;
                        }
                        player.inventory.setItemStack(playBuyStack);
                        tile.setField(0, bank - (cost * amnt));
                        tile.setField(4, tile.getField(4) + cost * amnt);
                    }
                } else {
                }
                return slotStack;
            }
        }
        return null;
    }
    
    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (tile instanceof TileVendor) {
            //<editor-fold desc="Vendor Shifting">
            ItemStack sourceStack = null;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                    if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote) {
                        if(tile.getField(2) == 0) {
                            if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                                return null;
                            }
                        }else{
                            return null;
                        }
                    } else {
                        if (tile.getField(2) == 1) {     //Only allow shift clicking from player inv in edit mode
                            if (!this.mergeItemStack(copyStack, TE_VEND_FIRST_SLOT_INDEX, TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT, false)) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + TE_BUFFER_TOTAL_COUNT) {  //TE Inventory
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return null;
                    }
                } else {
                   return null;
                }

                if (copyStack.stackSize == 0) {
                    slot.putStack(null);
                } else {
                    slot.onSlotChanged();
                }
            }
            return sourceStack;
            //</editor-fold>
        } else {
            //<editor-fold desc="Seller Shifting">
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
                            }else return null;
                        } else {
                            return null;
                        }
                    }
                } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + TE_BUFFER_TOTAL_COUNT) {  //TE Inventory
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
            //</editor-fold>
        }
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
                if (fieldChanged[field]) listener.sendProgressBarUpdate(this, field, cachedFields[field]);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tile.setField(id, data);
    }

    public void attemptChangeToInv(){

    }
}