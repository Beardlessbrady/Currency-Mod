package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.proxy.ClientProxy;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
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
 * File Created 2019-07-31
 */

public class ContainerTradein extends Container {

    //Slot Index
    //0-35 = Players Inv
    public final int HOTBAR_SLOT_COUNT = 9;
    public final int PLAYER_INV_ROW_COUNT = 3;
    public final int PLAYER_INV_COLUMN_COUNT = 9;
    public final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    public final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    public final int TE_INPUT_SLOT_COUNT = 1;
    public final int TE_INVENTORY_SLOT_COUNT = 25;
    public final int TE_OUTPUT_SLOT_COUNT = 1;
    public final int TE_INPUT_SLOT_INDEX = 0;
    public final int TE_INVENTORY_FIRST_SLOT_INDEX = TE_INPUT_SLOT_INDEX + TE_INPUT_SLOT_COUNT;
    public final int TE_OUTPUT_FIRST_SLOT_INDEX = TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT;

    public final int GUI_PLAYER_FIRST_INDEX = 0;
    public final int GUI_INPUT_INDEX = GUI_PLAYER_FIRST_INDEX + PLAYER_TOTAL_COUNT;
    public final int GUI_INVENTORY_FIRST_INDEX = GUI_INPUT_INDEX + TE_INPUT_SLOT_COUNT;
    public final int GUI_OUTPUT_FIRST_INDEX = GUI_INVENTORY_FIRST_INDEX + TE_INVENTORY_SLOT_COUNT;

    private final KeyBinding[] keyBindings = ClientProxy.keyBindings.clone();

    final int TE_INV_COLUMN_COUNT = 5;
    final int TE_INV_ROW_COUNT = 5;

    private EntityPlayer player;
    private TileTradein te;
    private int[] cachedFields;

    public ContainerTradein(EntityPlayer entityPlayer, TileTradein te){
        player = entityPlayer;
        this.te = te;
        InventoryPlayer invPlayer = player.inventory;

        te.setPlayerUsing(player.getUniqueID());

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    private void setupPlayerInv(InventoryPlayer invPlayer) {
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 157;
        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 99;

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
        addSlotToContainer(new SlotItemHandler(iItemHandler, TE_INPUT_SLOT_INDEX, 15, 14));

        //Inventory Slots
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = -22;

        for (int y = 0; y < TE_INV_COLUMN_COUNT; y++) {
            for (int x = 0; x < TE_INV_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_INV_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(iItemHandler, slotNum, xpos, ypos));
            }
        }

        //Output Slots
        addSlotToContainer(new SlotItemHandler(iItemHandler, TE_OUTPUT_FIRST_SLOT_INDEX, 145, 14));

    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
     //   boolean creative = te.getField(TileTradein.FIELD_CREATIVE) == 1; TODO
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
            } else if (slotId >= GUI_INVENTORY_FIRST_INDEX && slotId < GUI_OUTPUT_FIRST_INDEX) {
                Loop:
                for (int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) {
                    if (UtilMethods.equalStacks(playerStack, te.getItemTradein(i).getStack())) {
                        if (playerStack.getCount() + te.getItemTradein(i).getSize() <= playerStack.getMaxStackSize()) {
                            playerStack.grow(te.getItemTradein(i).getSize());
                            te.voidItem(i);
                        } else {
                            int teSize = playerStack.getMaxStackSize() - playerStack.getCount();
                            playerStack.setCount(playerStack.getMaxStackSize());
                            te.getItemTradein(i).shrinkSize(teSize);
                        }
                        if (playerStack.getCount() == playerStack.getMaxStackSize())
                            break Loop;
                    }
                }
                return ItemStack.EMPTY;
            }
        }
        //</editor-fold>

        if (slotId >= 37 && slotId <= 61) {  //te Inventory
            if (te.getField(TileTradein.FIELD_MODE) == 1) { //ADMIN MODE
                if (te.getItemTradein(index).getSize() == 0) {
                    te.voidItem(index);
                }
                if (dragType == 0 || (dragType == 1 && clickTypeIn == ClickType.QUICK_CRAFT)) { //Left Click
                    if (playerStack.isEmpty()) {
                        //TODO CREATIVE
                       // if (!creative) {
                            player.inventory.setItemStack(te.getItemTradein(index).shrinkSizeWithStackOutput(64));
                      //  } else {
                      //      te.getItemVendor(index).shrinkSize(64);
                        //}
                    } else {
                      //  if (!creative) {
                            if (te.getItemTradein(index).isEmpty()) {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                                te.setItemVendor(index, new ItemTradein(copyPlayerStack));
                            } else {
                                player.inventory.setItemStack(te.getItemTradein(index).growSizeWithStack(copyPlayerStack));
                            }
                        //} else {
                        //    te.voidItem(index);
                       //     te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                       // }
                    }
                    if (te.getItemTradein(index).getSize() == 0) {
                        te.voidItem(index);
                    }
                } else if (dragType == 1) { //Right Click
                    if (!(te.getField(TileEconomyBase.FIELD_SELECTED) == slotId)) {
                        short toSelect = (short) index;
                        te.setSelectedName(te.getItemTradein(index).getStack().getDisplayName());

                        te.setField(TileEconomyBase.FIELD_SELECTED, toSelect);
                    }
                    if (te.getItemTradein(index).getStack().isEmpty()) { //Place 1
                        te.setItemVendor(index, new ItemTradein(copyPlayerStack, 1));
                        playerStack.shrink(1);
                    }

                } else if (dragType == 5) { //Quick Craft Right
                    if (te.getItemTradein(index).getStack().isEmpty()) { //Place 1
                        te.setItemVendor(index, new ItemTradein(copyPlayerStack, 1));
                        playerStack.shrink(1);
                    }
                }
                return ItemStack.EMPTY;
            }else{ //TRADE MODE
                return ItemStack.EMPTY;
            }
        } else if (slotId == 62) { //OUTPUT
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return super.transferStackInSlot(playerIn, index);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    //<editor-fold desc="Client Sync">
    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean fieldChanged[] = new boolean[te.getFieldCount()];

        if (cachedFields == null) cachedFields = new int[te.getFieldCount()];

        for (int i = 0; i < cachedFields.length; i++) {
            if (cachedFields[i] != te.getField(i)) {
                cachedFields[i] = te.getField(i);
                fieldChanged[i] = true;
            }
        }

        for (IContainerListener listener : this.listeners) {
            for (int field = 0; field < te.getFieldCount(); ++field) {
                if (fieldChanged[field]) {
                    listener.sendWindowProperty(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        te.setField(id, data);
    }
    //</editor-fold>

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        te.getWorld().scheduleBlockUpdate(te.getPos(), te.getBlockType(), 1,1);

        playerIn.playSound(SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE,0.2F, -100.0F);

        te.voidPlayerUsing();
        te.setField(TileEconomyBase.FIELD_MODE, 0);

        boolean success = false;
        boolean items = false;
    }
}