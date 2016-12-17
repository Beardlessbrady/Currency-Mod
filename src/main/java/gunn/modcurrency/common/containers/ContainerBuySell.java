package gunn.modcurrency.common.containers;

import gunn.modcurrency.common.items.ModItems;
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
    private final int TE_VEND_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;

    private TileVendor tileVendor;
    private TileSeller tileSeller;
    private int[] cachedFields;

    public ContainerBuySell(InventoryPlayer invPlayer, TileVendor tile) {
        this.tileVendor = tile;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    public ContainerBuySell(InventoryPlayer invPlayer, TileSeller tile) {
        this.tileSeller = tile;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    public void setTileField(int id, int data) {
        if (tileVendor != null) tileVendor.setField(id, data);
        if (tileSeller != null) tileSeller.setField(id, data);
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
        final int TE_MONEY_XPOS = 152;
        final int TE_MONEY_YPOS = 9;
        IItemHandler itemHandler;

        if (tileVendor != null) {
            itemHandler = this.tileVendor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            addSlotToContainer(new SlotBank(itemHandler, 0, TE_MONEY_XPOS, TE_MONEY_YPOS));
        } else {
            itemHandler = this.tileSeller.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            addSlotToContainer(new SlotItemHandler(itemHandler, 0, TE_MONEY_XPOS, TE_MONEY_YPOS));
        }

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        final int TE_INV_YPOS = 32;

        for (int y = 0; y < TE_VEND_COLUMN_COUNT; y++) {
            for (int x = 0; x < TE_VEND_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_VEND_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(itemHandler, slotNum, xpos, ypos));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (tileVendor != null) return tileVendor.canInteractWith(playerIn);
        return tileSeller.canInteractWith(playerIn);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (tileVendor != null) {
            //<editor-fold desc="Vendor Slot Click">
            if (tileVendor.getField(2) == 1) {               //EDIT MODE
                if (slotId >= 0 && slotId <= 36) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId <= 67 && tileVendor.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                    tileVendor.setField(3, slotId);
                    if (getSlot(slotId).getHasStack()) {
                        tileVendor.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                    } else {
                        tileVendor.setSelectedName("No Item");
                    }
                    tileVendor.getWorld().notifyBlockUpdate(tileVendor.getPos(), tileVendor.getBlockType().getDefaultState(), tileVendor.getBlockType().getDefaultState(), 3);
                    return null;
                } else if (slotId >= 37 && slotId <= 67 && tileVendor.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 1) {
                    return super.slotClick(slotId, 0, clickTypeIn, player);
                } else {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }
            } else {  //Sell Mode
                if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Money Slot
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId <= 67) {  //Is TE Inv
                    if (clickTypeIn == ClickType.PICKUP && dragType == 0) {   //Left Click = 1 item
                        return checkAfford(slotId, 1, player);
                    } else if (clickTypeIn == ClickType.PICKUP && dragType == 1) {   //Right Click = 10 item
                        return checkAfford(slotId, 10, player);
                    } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                        return checkAfford(slotId, 64, player);
                    }
                }
            }
            //</editor-fold>
        } else {
            if (tileSeller.getField(2) == 1) {        //Edit Mode
                if (slotId >= 0 && slotId <= 36) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId <= 67 && tileSeller.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 0) {
                    tileSeller.setField(3, slotId);
                    if (getSlot(slotId).getHasStack()) {
                        tileSeller.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                    } else {
                        tileSeller.setSelectedName("No Item");
                    }
                    tileSeller.getWorld().notifyBlockUpdate(tileSeller.getPos(), tileSeller.getBlockType().getDefaultState(), tileSeller.getBlockType().getDefaultState(), 3);
                    return null;
                } else if (slotId >= 37 && slotId <= 67 && tileSeller.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 1) {
                    return super.slotClick(slotId, 0, clickTypeIn, player);
                } else {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }

            } else {                                     //Sell Mode
                if (slotId >= 0 && slotId <= 36) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }
            }
        }
        return null;
    }


    //For Vendor
    public ItemStack checkAfford(int slotId, int amnt, EntityPlayer player){
        if(tileVendor != null) {
            IItemHandler itemHandler = this.tileVendor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack playStack = player.inventory.getItemStack();
            ItemStack slotStack = itemHandler.getStackInSlot(slotId - PLAYER_TOTAL_COUNT);
            ItemStack playBuyStack;
            int bank = tileVendor.getField(0);
            int cost = tileVendor.getItemCost(slotId - PLAYER_TOTAL_COUNT - 1);

            if (slotStack != null) {
                if (playStack != null) {
                    if (!((playStack.getDisplayName().equals(slotStack.getDisplayName())) &&
                            (playStack.getItem().getUnlocalizedName().equals(slotStack.getItem().getUnlocalizedName())))) {
                        return null; //Checks if player is holding stack, if its different then one being clicked do nothing
                    }
                }
                if (tileVendor.getField(6) == 0)
                    if (slotStack.stackSize < amnt && slotStack.stackSize != 0) amnt = slotStack.stackSize;

                if ((bank >= (cost * amnt))) {   //If has enough money, buy it
                    if (slotStack.stackSize >= amnt || tileVendor.getField(6) == 1) {
                        if (tileVendor.getField(6) == 0) slotStack.splitStack(amnt);
                        playBuyStack = slotStack.copy();
                        playBuyStack.stackSize = amnt;

                        if (player.inventory.getItemStack() != null) {       //Holding Item
                            playBuyStack.stackSize = amnt + playStack.stackSize;
                        }
                        player.inventory.setItemStack(playBuyStack);
                        tileVendor.setField(0, bank - (cost * amnt));
                        tileVendor.setField(4, tileVendor.getField(4) + cost * amnt);
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
        if(tileVendor != null) {
            ItemStack sourceStack = null;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                    if (inventorySlots.get(index).getStack().getItem() == ModItems.itembanknote) {
                        if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                            return null;
                        }
                    } else {
                        if (tileVendor.getField(2) == 1) {     //Only allow shift clicking from player inv in edit mode
                            if (!this.mergeItemStack(copyStack, TE_VEND_FIRST_SLOT_INDEX, TE_VEND_FIRST_SLOT_INDEX + TE_VEND_TOTAL_COUNT, false)) {
                                return null;
                            }
                        } else {
                            return null;
                        }
                    }
                } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_TOTAL_COUNT) {  //TE Inventory
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
        return null;
    }
    
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        if(tileVendor != null) {
            boolean fieldChanged[] = new boolean[tileVendor.getFieldCount()];

            if (cachedFields == null) cachedFields = new int[tileVendor.getFieldCount()];

            for (int i = 0; i < cachedFields.length; i++) {
                if (cachedFields[i] != tileVendor.getField(i)) {
                    cachedFields[i] = tileVendor.getField(i);
                    fieldChanged[i] = true;
                }
            }

            for (IContainerListener listener : this.listeners) {
                for (int field = 0; field < tileVendor.getFieldCount(); ++field) {
                    if (fieldChanged[field]) listener.sendProgressBarUpdate(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        setTileField(id,data);
    }
}