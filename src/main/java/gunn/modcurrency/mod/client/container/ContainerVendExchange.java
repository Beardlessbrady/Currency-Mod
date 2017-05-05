package gunn.modcurrency.mod.client.container;

import gunn.modcurrency.mod.core.network.PacketHandler;
import gunn.modcurrency.mod.tile.abAdvSell;
import gunn.modcurrency.mod.client.container.slots.SlotCustomizable;
import gunn.modcurrency.mod.client.container.slots.SlotVendor;
import gunn.modcurrency.mod.core.network.PacketItemSpawnToServer;
import gunn.modcurrency.mod.client.util.INBTInventory;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.tile.TileSeller;
import gunn.modcurrency.mod.tile.TileVendor;
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
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-02.
 */
public class ContainerVendExchange extends Container implements INBTInventory {
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

    private final int TE_VEND_COLUMN_COUNT = 6;
    private final int TE_VEND_ROW_COUNT = 5;
    private final int TE_VEND_MAIN_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    private final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;

    private final int TE_BUFFER_TOTAL_COUNT = 6;

    private Item[] specialSlotItems;

    private abAdvSell tile;
    private int[] cachedFields;

    public ContainerVendExchange(InventoryPlayer invPlayer, abAdvSell te) {
        tile = te;
        if (tile instanceof TileVendor) specialSlotItems = ((TileVendor) tile).specialSlotItems;
        if (tile instanceof TileSeller) specialSlotItems = ((TileSeller) tile).specialSlotItems;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        if(tile.getField(2) == 1 && tile instanceof TileVendor) checkGhostStacks();
        tile.voidPlayerUsing();
        tile.setField(8, 0);
        tile.outInputSlot();
        super.onContainerClosed(playerIn);
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
        if (tile instanceof TileVendor) {
            int xpos = 0;
            if (tile.getField(2) == 0) xpos = 152;
            if (tile.getField(2) == 1) xpos = -1000;
            addSlotToContainer(new SlotCustomizable(itemHandler, 0, xpos, 9, specialSlotItems));
        }
        if (tile instanceof TileSeller) {
            if (tile.getField(2) == 0) addSlotToContainer(new SlotItemHandler(itemHandler, 0, 152, 9));
            if (tile.getField(2) == 1)
                addSlotToContainer(new SlotCustomizable(itemHandler, 0, 152, 9, specialSlotItems));
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
                addSlotToContainer(new SlotVendor(itemHandler, slotNum, xpos, ypos));
            }
        }

        //Buffer Slots
        for (int x = 0; x < TE_BUFFER_TOTAL_COUNT; x++) {
            int slotNum = TE_VEND_MAIN_TOTAL_COUNT + 1 + x;
            int xpos = 0;
            if (tile.getField(2) == 1) xpos = 15;
            if (tile.getField(2) == 0) xpos = -1000;
            int ypos = 32 + x * 18;
            if (tile instanceof TileVendor)
                addSlotToContainer(new SlotCustomizable(itemHandler, slotNum, xpos, ypos, specialSlotItems));
            if (tile instanceof TileSeller) addSlotToContainer(new SlotItemHandler(itemHandler, slotNum, xpos, ypos));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tile.canInteractWith(playerIn);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (clickTypeIn == ClickType.PICKUP_ALL || slotId == -999) return ItemStack.EMPTY;
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
                    return ItemStack.EMPTY;
                } else if (slotId >= 37 && slotId < 67 && tile.getField(8) == 1 && clickTypeIn == ClickType.PICKUP && dragType == 1) {
                    return super.slotClick(slotId, 0, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67 && tile.getField(8) == 0){
                    if(((TileVendor) tile).isGhostSlot(slotId - PLAYER_TOTAL_COUNT - 1)){
                        this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getStackInSlot(slotId - PLAYER_TOTAL_COUNT).shrink(1);
                        ((TileVendor) tile).setGhostSlot( slotId - PLAYER_TOTAL_COUNT -1, false);
                    }
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }else{
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                }
            } else {  //Sell Mode
                if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Input Slot
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67) {  //Is TE Inv
                    if (!((TileVendor) tile).isGhostSlot( slotId -PLAYER_TOTAL_COUNT -1)) {
                        if (clickTypeIn == ClickType.PICKUP && dragType == 0) {   //Left Click = 1 item
                            return checkAfford(slotId, 1, player);
                        } else if (clickTypeIn == ClickType.PICKUP && dragType == 1) {   //Right Click = 10 item
                            return checkAfford(slotId, 10, player);
                        } else if (clickTypeIn == ClickType.QUICK_MOVE) {
                            return checkAfford(slotId, 64, player);
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }else return ItemStack.EMPTY;
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
                    if (clickTypeIn == ClickType.PICKUP) {      //LEFT
                        if (inventoryPlayer.getItemStack() != ItemStack.EMPTY && inventorySlots.get(slotId).getStack() == ItemStack.EMPTY) {
                            ItemStack ghostStack = inventoryPlayer.getItemStack().copy();
                            int gCount = 1;
                            if (((TileSeller) tile).getItemAmount(slotId - 37) > 1) {
                                gCount = ((TileSeller) tile).getItemAmount(slotId - 37);
                            }

                            ghostStack.setCount(gCount);
                            ghostSlot.putStack(ghostStack);
                        } else {
                            ((TileSeller) tile).setItemAmount(-1, slotId - 37);
                            ghostSlot.putStack(ItemStack.EMPTY);
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
                    return ItemStack.EMPTY;
                } else if (slotId > 66 && slotId < 73) {
                    return super.slotClick(slotId, dragType, clickTypeIn, player); //Buffer Slots
                } else return ItemStack.EMPTY;
            } else {  //Sell Mode
                if (slotId >= 0 && slotId <= 36) {           //Is Players Inv or Input Slot
                    return super.slotClick(slotId, dragType, clickTypeIn, player);
                } else if (slotId >= 37 && slotId < 67) {  //Is TE Inv
                    return ItemStack.EMPTY;
                }
            }
            //</editor-fold>
        }
        return ItemStack.EMPTY;
    }

    public ItemStack checkAfford(int slotId, int amnt, EntityPlayer player) {
        if (tile instanceof TileVendor) {
            IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            ItemStack playStack = player.inventory.getItemStack();
            ItemStack slotStack = itemHandler.getStackInSlot(slotId - PLAYER_TOTAL_COUNT);
            ItemStack playBuyStack;

            boolean wallet = false;
            int bank = 0;

            if (itemHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                if (itemHandler.getStackInSlot(0).getItem() == ModItems.itemWallet) {
                    wallet = true;
                    bank = tile.getField(10);
                }
            } else bank = tile.getField(0);
            int cost = tile.getItemCost(slotId - PLAYER_TOTAL_COUNT - 1);

            if (slotStack != ItemStack.EMPTY) {
                if (playStack.getItem() != Item.getItemFromBlock(Blocks.AIR)) {
                    if (!((playStack.getDisplayName().equals(slotStack.getDisplayName())) && (playStack.getItem().getUnlocalizedName().equals(slotStack.getItem().getUnlocalizedName())))) {
                        return ItemStack.EMPTY; //Checks if player is holding stack, if its different then one being clicked do nothing
                    }
                }
                if (tile.getField(6) == 0)
                    if (slotStack.getCount() < amnt && slotStack.getCount() != 0) amnt = slotStack.getCount();


                if ((bank >= (cost * amnt))) {   //If has enough money, buy it
                    if (slotStack.getCount() >= amnt || tile.getField(6) == 1) {
                        playBuyStack = slotStack.copy();
                        playBuyStack.setCount(amnt);

                        if (!player.inventory.getItemStack().isEmpty()) {       //Holding Item
                            playBuyStack.setCount(amnt + playStack.getCount());
                        }
                        player.inventory.setItemStack(playBuyStack);

                        if (tile.getField(6) == 0){
                            if(slotStack.getCount() - amnt == 0){
                                ((TileVendor) tile).setGhostSlot( slotId - PLAYER_TOTAL_COUNT -1, true);
                                slotStack.setCount(1);
                            }else slotStack.splitStack(amnt);
                        }


                        if (wallet) {
                            sellToWallet(itemHandler.getStackInSlot(0), cost * amnt);
                        } else {
                            tile.setField(0, bank - (cost * amnt));
                        }

                        tile.setField(4, tile.getField(4) + cost * amnt);
                    }
                }else{
                    if(tile instanceof TileVendor) ((TileVendor) tile).unsucessfulNoise();

                }
                return slotStack;
            }
        }
        return ItemStack.EMPTY;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        if (tile instanceof TileVendor) {
            //<editor-fold desc="Vendor Shifting">
            ItemStack sourceStack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                    if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote || inventorySlots.get(index).getStack().getItem() == ModItems.itemWallet) {
                        if (tile.getField(2) == 0) {
                            if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (tile.getField(2) == 1) {     //Only allow shift clicking from player inv in edit mode
                            if (!this.mergeItemStack(copyStack, TE_VEND_FIRST_SLOT_INDEX, TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT, false)) {
                                return ItemStack.EMPTY;
                            }
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (index == TE_MONEY_FIRST_SLOT_INDEX) {
                    if (tile.getField(2) == 0) {
                        if (index == 36) {
                            if (!this.mergeItemStack(copyStack, PLAYER_FIRST_SLOT_INDEX, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                                return ItemStack.EMPTY;
                            }
                        }
                    }
                } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + TE_BUFFER_TOTAL_COUNT) {  //TE Inventory
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
            //</editor-fold>
        } else {
            //<editor-fold desc="Seller Shifting">
            ItemStack sourceStack = ItemStack.EMPTY;
            Slot slot = this.inventorySlots.get(index);

            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {        //Player Inventory Slots
                    if (tile.getField(2) == 0) {     //SELL MODE
                        if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        if (tile.getField(2) == 1) {
                            if (inventorySlots.get(index).getStack().getItem() == ModItems.itemBanknote) {
                                if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX, TE_MONEY_FIRST_SLOT_INDEX + 1, false)) {
                                    return ItemStack.EMPTY;
                                }
                            } else return ItemStack.EMPTY;
                        } else {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_MAIN_TOTAL_COUNT + TE_BUFFER_TOTAL_COUNT) {  //TE Inventory
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index == TE_MONEY_FIRST_SLOT_INDEX) {
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
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

    /**
     * Used to deduct the price of 'amountRemovable' from the wallet's inventory of bills
     *
     * @param wallet
     * @param amountRemovable
     */
    public void sellToWallet(ItemStack wallet, int amountRemovable) {
        int amount = amountRemovable;

        int one = getTotalOfBill(wallet, 0);
        int five = getTotalOfBill(wallet, 1);
        int ten = getTotalOfBill(wallet, 2);
        int twenty = getTotalOfBill(wallet, 3);
        int fifty = getTotalOfBill(wallet, 4);
        int hundo = getTotalOfBill(wallet, 5);

        int[] out = new int[6];

        out[5] = Math.round(amount / 100);
        while (out[5] > hundo) out[5]--;
        amount = amount - (out[5] * 100);

        out[4] = Math.round(amount / 50);
        while (out[4] > fifty) out[4]--;
        amount = amount - (out[4] * 50);

        out[3] = Math.round(amount / 20);
        while (out[3] > twenty) out[3]--;
        amount = amount - (out[3] * 20);

        out[2] = Math.round(amount / 10);
        while (out[2] > ten) out[2]--;
        amount = amount - (out[2] * 10);

        out[1] = Math.round(amount / 5);
        while (out[1] > five) out[1]--;
        amount = amount - (out[1] * 5);

        out[0] = Math.round(amount);
        while (out[0] > one) out[0]--;
        amount = amount - (out[0] * 1);


        ItemStackHandler itemHandler = readInventoryTag(wallet, ItemWallet.WALLET_TOTAL_COUNT);

        for (int i = 0; i < 6; i++) {
            searchLoop:
            for (int j = 0; j < itemHandler.getSlots(); j++) {
                ItemStack stack = itemHandler.getStackInSlot(j);

                if (stack != ItemStack.EMPTY) {
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

                    if (stack.getCount() == 0) itemHandler.setStackInSlot(j, ItemStack.EMPTY);  //Removes stack is 0
                }
            }
        }

        /*If there is still an amount that needs to be paid for,
        will pick the closed highest bill in the wallet, remove it
        and send the 'change' to the bank variable of the vendor then output it with outChange
         */
        if (amount != 0) {
            int billDamage = 0;
            searchLoop:
            //Searches wallet for the first highest bill that can handle rest of amount
            for (int i = 0; i < itemHandler.getSlots(); i++) {
                if (itemHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                    int billWorth = getBillWorth(itemHandler.getStackInSlot(i).getItemDamage(), itemHandler.getStackInSlot(i).getCount());
                    if (billWorth > amount) {
                        billDamage = itemHandler.getStackInSlot(i).getItemDamage();
                        if (itemHandler.getStackInSlot(i).getCount() == 1) {
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        } else {
                            itemHandler.getStackInSlot(i).shrink(-1);
                        }
                        break searchLoop;
                    }
                }
            }

            //Calculates change be deducting the bills worth with the amount deductible that was left
            int change = 0;
            switch (billDamage) {
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
                    change = 20 - amount;
                    break;
                case 4:
                    change = 50 - amount;
                    break;
                case 5:
                    change = 100 - amount;
                    break;
            }

            //Adds change to the bank variable in vendor
            tile.setField(0, tile.getField(0) + change);

            //Forces the vendor to output the change
            PacketItemSpawnToServer pack = new PacketItemSpawnToServer();
            pack.setBlockPos(tile.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);
        }

        writeInventoryTag(wallet, itemHandler);
    }

    /**
     * Get the total AMOUNT of a bill (not the total of what its worth) from a wallet
     *
     * @param stack
     * @param billDamage
     * @return
     */
    public int getTotalOfBill(ItemStack stack, int billDamage) {
        ItemStackHandler itemStackHandler = readInventoryTag(stack, ItemWallet.WALLET_TOTAL_COUNT);

        int totalOfBill = 0;
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            if (itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                if (itemStackHandler.getStackInSlot(i).getItemDamage() == billDamage) {
                    totalOfBill = totalOfBill + 1 * itemStackHandler.getStackInSlot(i).getCount();
                }
            }
        }
        return totalOfBill;
    }

    public int getBillWorth(int itemDamage, int stackSize) {
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
                cash = 20;
                break;
            case 4:
                cash = 50;
                break;
            case 5:
                cash = 100;
                break;
        }

        return cash * stackSize;
    }

    public void checkGhostStacks(){
        IItemHandler itemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        for(int i=0; i < TE_VEND_MAIN_TOTAL_COUNT; i++){
            if(((TileVendor)tile).isGhostSlot(i) && itemHandler.getStackInSlot(i+1).getCount() > 1){
                itemHandler.getStackInSlot(i+1).shrink(1);
                ((TileVendor) tile).setGhostSlot(i, false);
            }
        }
    }
}