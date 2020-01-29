package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.item.ModItems;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import static beardlessbrady.modcurrency.block.vending.TileVending.*;

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

    public ContainerVending(EntityPlayer entityPlayer, TileVending te) {
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

    private void setupTeInv() {
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
        for (int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++)
            addSlotToContainer(new SlotItemHandler(iItemHandler, TE_OUTPUT_FIRST_SLOT_INDEX + i, 44 + (i * SLOT_X_SPACING), 77));

    }

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        boolean creative = te.getField(TileVending.FIELD_CREATIVE) == 1;
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
                    if (UtilMethods.equalStacks(playerStack, te.getItemVendor(i).getStack())) {
                        if (playerStack.getCount() + te.getItemVendor(i).getSize() <= playerStack.getMaxStackSize()) {
                            playerStack.grow(te.getItemVendor(i).getSize());
                            te.voidItem(i);
                        } else {
                            int teSize = playerStack.getMaxStackSize() - playerStack.getCount();
                            playerStack.setCount(playerStack.getMaxStackSize());
                            te.getItemVendor(i).shrinkSize(teSize);
                        }
                        if (playerStack.getCount() == playerStack.getMaxStackSize())
                            break Loop;
                    }
                }
                return ItemStack.EMPTY;
            }
        }
        //</editor-fold>

        if (slotId == GUI_INPUT_INDEX) {
            if (playerStack.getItem().equals(ModItems.itemCurrency) || playerStack.isEmpty()) {
                player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);
            } else {
                return ItemStack.EMPTY;
            }
        }
        if (slotId >= 37 && slotId <= 61) {  //te Inventory
            if (te.getField(TileVending.FIELD_MODE) == 1) {            //ADMIN MODE
                if (te.getItemVendor(index).getSize() == 0) {
                    te.voidItem(index);
                }
                if (dragType == 0 || (dragType == 1 && clickTypeIn == ClickType.QUICK_CRAFT)) { //Left Click
                    if (playerStack.isEmpty()) {
                        if (!creative) {
                            player.inventory.setItemStack(te.getItemVendor(index).shrinkSizeWithStackOutput(64));
                        } else {
                            te.getItemVendor(index).shrinkSize(64);
                        }
                    } else {
                        if (!creative) {
                            if (te.getItemVendor(index).isEmpty()) {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                                te.setItemVendor(index, new ItemVendor(copyPlayerStack));

                            } else {
                                player.inventory.setItemStack(te.getItemVendor(index).growSizeWithStack(copyPlayerStack));
                            }
                        } else {
                            te.voidItem(index);
                            te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                        }
                    }
                    if (te.getItemVendor(index).getSize() == 0) {
                        int[] bundle = te.getItemVendor(te.getItemVendor(index).getBundleMainSlot()).getBundle();
                        if(bundle != null){
                            for(int i = 0; i < bundle.length; i++)
                                te.getItemVendor(bundle[i]).setBundle(null);
                        }

                        te.voidItem(index);
                    }
                } else if (dragType == 1) { //Right Click
                    if (te.getKey(KEY_SHIFT)) {
                        te.getItemVendor(index).setBundle(new int[0]);
                    } else {
                        //Moves Selected Slot
                        if (!(te.getField(FIELD_SELECTED) == slotId)) {
                            short toSelect = (short)index;
                            if (te.getItemVendor(index).getBundleMainSlot() != -1) {
                                toSelect = (short) te.getItemVendor(index).getBundleMainSlot();
                                te.setSelectedName("bundle");
                            } else {
                                te.setSelectedName(te.getItemVendor(index).getStack().getDisplayName());
                            }
                            te.setField(FIELD_SELECTED, toSelect);
                        }
                        if (te.getItemVendor(index).getStack().isEmpty()) { //Place 1
                            te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                            playerStack.shrink(1);
                        }
                    }
                } else if (dragType == 5) { //Quick Craft Right
                    if (te.getItemVendor(index).getStack().isEmpty()) { //Place 1
                        te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                        playerStack.shrink(1);
                    }
                }
                return ItemStack.EMPTY;
            } else { //SELL MODE
                if (dragType == 0) { //LEFT CLICK
                    //If not in a bundle buy normally
                    if (te.getItemVendor(index).getBundleMainSlot() == -1) {
                        buyItem(index, 1);
                    } else {
                        buyBundle(te.getItemVendor(index).getBundleMainSlot());
                    }
                } else if (dragType == 1) { //RIGHT CLICK
                }
                return ItemStack.EMPTY;
            }
        } else if (slotId >= 62 && slotId <= 66) { //OUTPUT Inventory
            if(!this.inventorySlots.get(slotId).getStack().isEmpty()) {
                if (!this.mergeItemStack(inventorySlots.get(slotId).getStack(), 0, PLAYER_TOTAL_COUNT, false)) {
                    te.setMessage("Not enough space in inventory!", (byte) 40);
                    player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 0.5F);
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotId) {
        ItemStack itemStack = this.inventorySlots.get(slotId).getStack();
        ItemStack copyStack = itemStack.copy();

        if (!itemStack.isEmpty()) {
            if (slotId < PLAYER_TOTAL_COUNT) {
                if (te.getField(TileEconomyBase.FIELD_MODE) == 0) {

                    if (itemStack.getItem().equals(ModItems.itemCurrency)) {
                        playerIn.world.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);

                        if (!this.mergeItemStack(itemStack, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    for (int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) {
                        if (UtilMethods.equalStacks(itemStack, inventorySlots.get(GUI_INVENTORY_FIRST_INDEX + i).getStack())) {
                            int count = 0;
                            if (te.getItemVendor(i).getSize() + itemStack.getCount() <= te.getItemVendor(i).getSizeLimit()) {
                                count = itemStack.getCount();
                            } else {
                                count = te.getItemVendor(i).getSizeLimit() - te.getItemVendor(i).getSize();
                            }
                            copyStack.setCount(count);
                            te.getItemVendor(i).growSize(copyStack.getCount());
                            itemStack.shrink(count);
                            if (itemStack.getCount() == 0)
                                this.inventorySlots.get(slotId).putStack(ItemStack.EMPTY);
                        }
                    }
                }
            } else if (slotId >= GUI_INVENTORY_FIRST_INDEX && slotId < GUI_OUTPUT_FIRST_INDEX) {
              /*
               if(te.getField(TileEconomyBase.FIELD_MODE) == 0){
                   int count = te.getItemSize(slotId - PLAYER_TOTAL_COUNT);
                   if(count > itemStack.getMaxStackSize()){
                       count = itemStack.getMaxStackSize();
                   }
                   copyStack.setCount(count);
                   if (!this.mergeItemStack(copyStack, 0, PLAYER_TOTAL_COUNT, false)) {
                       return ItemStack.EMPTY;
                   }
               }*/
            }
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

        for (int i = PLAYER_TOTAL_COUNT + TE_OUTPUT_FIRST_SLOT_INDEX; i < PLAYER_TOTAL_COUNT + TE_OUTPUT_FIRST_SLOT_INDEX + TE_OUTPUT_SLOT_COUNT; i++) {
            if (!inventorySlots.get(i).getStack().isEmpty()) {
                items = true;
                if (!this.mergeItemStack(inventorySlots.get(i).getStack(), 0, PLAYER_TOTAL_COUNT, false)) {
                    success = false;
                } else {
                    success = true;
                }
            }
        }

        if (items) {
            if (playerIn.getEntityWorld().isRemote) {
                if (success) {
                    playerIn.sendStatusMessage(new TextComponentString("The Vending Machine's output was placed in your inventory."), true);
                } else
                    playerIn.sendStatusMessage(new TextComponentString("Your inventory is full and unable to be filled by the Vending Machine's output."), true);
            }
        }
    }

    public ItemStack buyItem(int index, int count) {
        boolean infinite = te.getField(TileVending.FIELD_FINITE) == 0;
        int amount = te.getItemVendor(index).getAmount();

        //If Sneak button held down, show a full stack (or as close to it)
        //If Jump button held down, show half a stack (or as close to it)
        if (te.getKey(KEY_SHIFT)) {
            amount = te.sneakFullStack(index, amount);
        } else if (te.getKey(KEY_CONTROL)) {
            amount = te.jumpHalfStack(index, amount);
        }
        count = count * amount;

        if (!te.getItemVendor(index).getStack().isEmpty() && te.getItemVendor(index).getSize() != 0) {
            if (te.canAfford(index, count) && (amount <= te.getItemVendor(index).getSize() || infinite)) {
                ItemStack outputStack = te.getItemVendor(index).getStack().copy();

                outputStack.setCount(count);
                int outSlot = te.outputSlotCheck(outputStack, amount);

                if (outSlot == -1) {
                    if (player.getEntityWorld().isRemote) {
                        te.setMessage("OUTPUT FULL!", (byte) 40);
                        player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);

                    }

                    return ItemStack.EMPTY;
                } else {
                    if (te.growOutItemSize(outputStack, outSlot).equals(ItemStack.EMPTY)) {
                        player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.BLOCKS, 0.05F, 5.0F, false);

                        int newCashReserve = te.getField(TileEconomyBase.FIELD_CASHRESERVE) - (te.getItemVendor(index).getCost() * (count / te.getItemVendor(index).getAmount()));
                        int newCashRegister = te.getField(TileEconomyBase.FIELD_CASHREGISTER) + (te.getItemVendor(index).getCost() * (count / te.getItemVendor(index).getAmount()));
                        te.setField(TileEconomyBase.FIELD_CASHRESERVE, newCashReserve);
                        te.setField(TileEconomyBase.FIELD_CASHREGISTER, newCashRegister);
                        if(!infinite)
                            te.getItemVendor(index).shrinkSize(count);
                    }

                    return ItemStack.EMPTY;
                }
            }
            te.setMessage("NOT ENOUGH FUNDS!", (byte) 40);
            player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 3.0F, false);

        }
        return ItemStack.EMPTY;
    }

    public ItemStack buyBundle(int mainIndex) {
        boolean infinite = te.getField(TileVending.FIELD_FINITE) == 0;

        int[] bundle = te.getItemVendor(mainIndex).getBundle();

        boolean empty = true;
        for (int i = 0; i < bundle.length; i++) {
            if (te.getItemVendor(bundle[i]).getStack().isEmpty() || ((te.getItemVendor(bundle[i]).getSize() < te.getItemVendor(bundle[i]).getAmount()) && !infinite))
                empty = false;
        }

        if (empty && te.canAfford(mainIndex, te.getItemVendor(mainIndex).getAmount())) {
            boolean canOutput = te.bundleOutSlotCheck(te.getItemVendor(mainIndex).getBundle());
            if (canOutput) {
                for (int i = 0; i < bundle.length; i++) {
                    ItemStack outputStack = te.getItemVendor(bundle[i]).getStack().copy();
                    outputStack.setCount(te.getItemVendor(bundle[i]).getAmount());
                    int outSlot = te.outputSlotCheck(outputStack, te.getItemVendor(bundle[i]).getAmount());

                    if (te.growOutItemSize(outputStack, outSlot).equals(ItemStack.EMPTY) && !infinite) {
                        te.getItemVendor(bundle[i]).shrinkSize(te.getItemVendor(bundle[i]).getAmount());
                    }
                }

                int newCashReserve = te.getField(TileEconomyBase.FIELD_CASHRESERVE) - (te.getItemVendor(mainIndex).getCost());
                int newCashRegister = te.getField(TileEconomyBase.FIELD_CASHREGISTER) + (te.getItemVendor(mainIndex).getCost());
                te.setField(TileEconomyBase.FIELD_CASHRESERVE, newCashReserve);
                te.setField(TileEconomyBase.FIELD_CASHREGISTER, newCashRegister);
            }
        }
        return ItemStack.EMPTY;
    }




}