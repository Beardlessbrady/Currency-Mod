package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.UtilMethods;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.item.ModItems;
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

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class ContainerVending extends Container {
    //Slot Index
    //0-35 = Players Inv
    public final int HOTBAR_SLOT_COUNT = 9;
    public final int PLAYER_INV_ROW_COUNT = 3;
    public final int PLAYER_INV_COLUMN_COUNT = 9;
    public final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    public final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    public final int TE_INPUT_SLOT_COUNT = 1;
    public final int TE_INVENTORY_SLOT_COUNT = 25;
    public final int TE_OUTPUT_SLOT_COUNT = 5;
    public final int TE_INPUT_SLOT_INDEX = 0;
    public final int TE_INVENTORY_FIRST_SLOT_INDEX = TE_INPUT_SLOT_INDEX + TE_INPUT_SLOT_COUNT;
    public final int TE_OUTPUT_FIRST_SLOT_INDEX = TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT;

    public final int GUI_PLAYER_FIRST_INDEX = 0;
    public final int GUI_INPUT_INDEX = GUI_PLAYER_FIRST_INDEX + PLAYER_TOTAL_COUNT;
    public final int GUI_INVENTORY_FIRST_INDEX = GUI_INPUT_INDEX + TE_INPUT_SLOT_COUNT;
    public final int GUI_OUTPUT_FIRST_INDEX = GUI_INVENTORY_FIRST_INDEX + TE_INVENTORY_SLOT_COUNT;

    final int TE_INV_COLUMN_COUNT = 5;
    final int TE_INV_ROW_COUNT = 5;

    private EntityPlayer player;
    private TileVending te;
    private int[] cachedFields;

    public ContainerVending(EntityPlayer entityPlayer, TileVending te){
        player = entityPlayer;
        this.te = te;
        InventoryPlayer invPlayer = player.inventory;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    private void setupPlayerInv(InventoryPlayer invPlayer) {
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 184;
        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 126;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++)
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));


        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++) {
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x;
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    private void setupTeInv(){
        IItemHandler iItemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        //Input Slot
        addSlotToContainer(new SlotItemHandler(iItemHandler, TE_INPUT_SLOT_INDEX, 145, 3));

        //Inventory Slots
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = -30;

        for (int y = 0; y < TE_INV_COLUMN_COUNT; y++) {
            for (int x = 0; x < TE_INV_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_INV_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(iItemHandler, slotNum, xpos, ypos));
            }
        }

        //Output Slots
        for(int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++)
            addSlotToContainer(new SlotItemHandler(iItemHandler, TE_OUTPUT_FIRST_SLOT_INDEX + i, 44 + (i*SLOT_X_SPACING), 77));

    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        System.out.println(slotId);
        int index = slotId - 37;
        ItemStack playerStack = player.inventory.getItemStack();
        ItemStack copyPlayerStack = playerStack.copy();

        //Ensures Pickup_All works without pulling from the wrong slots
        //<editor-fold desc="PICKUP ALL">
        if (clickTypeIn == ClickType.PICKUP_ALL) {
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
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
                //TODO PICKUP_ALL FOR TE INVENTORY
            } else if (slotId >= GUI_INVENTORY_FIRST_INDEX && slotId < GUI_OUTPUT_FIRST_INDEX + 4) {
                return ItemStack.EMPTY;
            }
        }
        //</editor-fold>

        if (slotId == GUI_INPUT_INDEX) {
            if (playerStack.getItem().equals(ModItems.itemCurrency) || playerStack.isEmpty()) {
            } else {
                return ItemStack.EMPTY;
            }
        }

        if(slotId >= 37 && slotId <= 61) {  //te Inventory
            if(clickTypeIn == ClickType.CLONE) {
                if (!(te.getIntField(TileVending.FIELD_SELECTED) == slotId)) {
                    te.setIntField(te.FIELD_SELECTED, slotId - 37);
                    te.setSelectedName(te.getInvItemStack(index).getDisplayName());
                }
            }
            if (te.getIntField(TileVending.FIELD_MODE) == 1) {            //ADMIN MODE
                if (dragType == 0) { //Left Click
                  if (playerStack.isEmpty()) {
                        player.inventory.setItemStack(te.shrinkInvItemSize(64, index));
                    } else {
                        if (te.getInvItemStack(index).isEmpty()) {
                            player.inventory.setItemStack(te.setInvItem(copyPlayerStack, index, 0));
                        } else {
                            player.inventory.setItemStack(te.growInvItemSize(copyPlayerStack, index));
                        }
                    }
                } else if (dragType == 1) { //Right Click
                    if(clickTypeIn == ClickType.QUICK_CRAFT) { //Mimics Left Click
                        if (playerStack.isEmpty()) {
                            player.inventory.setItemStack(te.shrinkInvItemSize(64, index));
                        } else {
                            if (te.getInvItemStack(index).isEmpty()) {
                                player.inventory.setItemStack(te.setInvItem(copyPlayerStack, index, 0));
                            } else {
                                player.inventory.setItemStack(te.growInvItemSize(copyPlayerStack, index));
                            }
                        }
                    }else {
                        if (playerStack.isEmpty()) { //Pickup Half
                            int half = te.getItemSize(index) / 2;
                            if (half >= 64) half = 64;
                            player.inventory.setItemStack(te.shrinkInvItemSize(half, index));
                        } else {
                            if (te.getInvItemStack(index).isEmpty()) { //Place 1
                                player.inventory.setItemStack(te.setInvItemAndSize(copyPlayerStack, index, 1));
                            } else {
                                player.inventory.setItemStack(te.setInvItemAndSize(copyPlayerStack, index, 1));
                            }
                        }
                    }
                } else if (dragType == 5) {
                    if(clickTypeIn == ClickType.QUICK_CRAFT) { //Mimics Right Click
                        if (te.getInvItemStack(index).isEmpty()) { //Place 1
                            player.inventory.setItemStack(te.setInvItemAndSize(copyPlayerStack, index, 1));
                        } else {
                            player.inventory.setItemStack(te.setInvItemAndSize(copyPlayerStack, index, 1));
                        }
                    }
                }
                return ItemStack.EMPTY;
            } else { //SELL MODE
                if (dragType == 0){ //LEFT CLICK
                    buyItem(index, 1);
                }else if (dragType == 1){ //RIGHT CLICK
                    buyItem(index, te.getItemSize(index)/2);
                }
                return ItemStack.EMPTY;
            }
        }else if (slotId >= 62 && slotId <= 66){
            if (!this.mergeItemStack(inventorySlots.get(slotId).getStack(), 0, PLAYER_TOTAL_COUNT, false)) {
                return ItemStack.EMPTY;
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotId) {
      ItemStack itemStack = this.inventorySlots.get(slotId).getStack();

       if(!itemStack.isEmpty()) {
           if (slotId >= 0 && slotId < PLAYER_TOTAL_COUNT) {
               if(te.getIntField(TileEconomyBase.FIELD_MODE) == 0){
                   if(itemStack.getItem().equals(ModItems.itemCurrency)){
                       System.out.println(TE_INPUT_SLOT_INDEX);
                       if (!this.mergeItemStack(itemStack, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX + 1, false)) {
                           return ItemStack.EMPTY;
                       }
                   }
               }
           }
           //TODO SHIFT CLICK PLAYER INV TO TE INV FOR STOCK MODE
       }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    //<editor-fold desc="Client Sync">
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean fieldChanged[] = new boolean[te.getIntFieldCount()];

        if (cachedFields == null) cachedFields = new int[te.getIntFieldCount()];

        for (int i = 0; i < cachedFields.length; i++) {
            if (cachedFields[i] != te.getIntField(i)) {
                cachedFields[i] = te.getIntField(i);
                fieldChanged[i] = true;
            }
        }

        for (IContainerListener listener : this.listeners) {
            for (int field = 0; field < te.getIntFieldCount(); ++field) {
                if (fieldChanged[field]) {
                    listener.sendWindowProperty(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        te.setIntField(id, data);
    }
    //</editor-fold>


    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        te.voidPlayerUsing();
        te.setIntField(TileEconomyBase.FIELD_MODE, 0);

        for (int i = PLAYER_TOTAL_COUNT + TE_OUTPUT_FIRST_SLOT_INDEX; i < PLAYER_TOTAL_COUNT + TE_OUTPUT_FIRST_SLOT_INDEX + TE_OUTPUT_SLOT_COUNT; i++) {
            if (!this.mergeItemStack(inventorySlots.get(i).getStack(), 0, PLAYER_TOTAL_COUNT, false)) {
                //TODO CHAT MESSAGE IT DIDNT WORK
            }else{
                //TODO IT WORKED, CHAT MESSAGE
            }
        }
    }


    public ItemStack buyItem(int index, int count) {
        if (!te.getInvItemStack(index).isEmpty()) {
            if (te.canAfford(index, count)) {
                ItemStack outputStack = te.getInvItemStack(index).copy();
                outputStack.setCount(count);
                int outSlot = te.outputSlotCheck(outputStack);

                if (outSlot == -1) {
                    //TODO add OUT IS FULL WARNING
                    return ItemStack.EMPTY;
                } else {
                    if (te.growOutItemSize(outputStack, outSlot).equals(ItemStack.EMPTY)) {
                        long price = te.getLongField(TileEconomyBase.FIELD_LONG_CASHRESERVE) - (te.getItemCost(index) * count);
                        te.setLongField(TileEconomyBase.FIELD_LONG_CASHRESERVE, price);
                        te.shrinkInvItemSize(count, index);
                    } else {
                        //TODO FAIl because no slots left in output
                    }
                    return ItemStack.EMPTY;
                }
            }
            //TODO play failure sound
        }
        return ItemStack.EMPTY;
    }


}
