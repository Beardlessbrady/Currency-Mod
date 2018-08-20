package gunn.modcurrency.mod.container;

import gunn.modcurrency.mod.client.gui.GuiWallet;
import gunn.modcurrency.mod.container.slot.SlotCustomizable;
import gunn.modcurrency.mod.container.util.INBTInventory;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-16
 */
public class ContainerWallet extends Container implements INBTInventory {
    //Slot Index's
    //0-35 = Player Inventory's
    //36-Onwards = Wallet Slots
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    private final int WALLET_COLUMN_COUNT = ItemWallet.WALLET_COLUMN_COUNT;
    private final int WALLET_ROW_COUNT = ItemWallet.WALLET_ROW_COUNT;
    private final int WALLET_TOTAL_COUNT = WALLET_COLUMN_COUNT * WALLET_ROW_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int WALLET_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;

    private final int GUI_XPOS_OFFPUT = GuiWallet.GUI_XPOS_OFFPUT;
    private List itemsAllowed;

    private ItemStackHandler itemStackHandler;
    private int slotId;

    public ContainerWallet(EntityPlayer player, ItemStack wallet){
        if(!wallet.hasTagCompound()){
            NBTTagCompound compound = new NBTTagCompound();
            wallet.setTagCompound(compound);
            writeInventoryTag(wallet, new ItemStackHandler(WALLET_TOTAL_COUNT));
        }
        itemsAllowed = new ArrayList();
        didInventorySizeChange(wallet, player);

        setupPlayerInv(player.inventory);
        setupWalletInv(wallet);

        checkmetadataOpen(wallet);
    }

    private void setupPlayerInv(InventoryPlayer invPlayer){
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 27;
        final int HOTBAR_YPOS = 178;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) addSlotToContainer(new Slot(invPlayer, x, (HOTBAR_XPOS + SLOT_X_SPACING * x) + GUI_XPOS_OFFPUT, HOTBAR_YPOS));

        final int PLAYER_INV_XPOS = 27;
        final int PLAYER_INV_YPOS = 120;

        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++){
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++){
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x;
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos + GUI_XPOS_OFFPUT, ypos));
            }
        }
    }

    private void setupWalletInv(ItemStack wallet){
        itemStackHandler = readInventoryTag(wallet, WALLET_TOTAL_COUNT);

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        for (int y = 0; y < WALLET_ROW_COUNT; y++){
            for (int x = 0; x < WALLET_COLUMN_COUNT; x++){
                int slotNum = y * WALLET_COLUMN_COUNT + x;
                int xpos =  7 + x * SLOT_X_SPACING;

                itemsAllowed.add(ModItems.itemBanknote);
                itemsAllowed.add(ModItems.itemCoin);

                switch(y) {
                    default:
                    case 0: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 35, itemsAllowed));
                        break;
                    case 1: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 54, itemsAllowed));
                        break;
                    case 2: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 18, itemsAllowed));
                        break;
                    case 3: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 72, itemsAllowed));
                        break;
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if (slotId >= 0) {
            if (inventorySlots.get(slotId).getStack().getItem().equals(ModItems.itemWallet)) return ItemStack.EMPTY;
        }

        ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
        writeInventoryTag(player.getHeldItemMainhand(), itemStackHandler);
        checkmetadataOpen(player.getHeldItemMainhand());
        return stack;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack sourceStack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);

        if(playerIn.getHeldItemMainhand() != inventorySlots.get(index).getStack()) {
            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {     //Player Inventory Slots
                    if (itemsAllowed.contains(slot.getStack().getItem())) {
                        if (!this.mergeItemStack(copyStack, WALLET_FIRST_SLOT_INDEX, WALLET_FIRST_SLOT_INDEX + WALLET_TOTAL_COUNT, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= WALLET_FIRST_SLOT_INDEX && index < WALLET_FIRST_SLOT_INDEX + WALLET_TOTAL_COUNT) {     //Wallet Inventory
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }
        }
        return sourceStack;
    }

    public void didInventorySizeChange(ItemStack stack, EntityPlayer player){
        ItemStackHandler handler = readInventoryTag(stack, WALLET_TOTAL_COUNT);
        int oldSize= handler.getSlots();

        if(oldSize != WALLET_TOTAL_COUNT){

            //Saving Inventory of old inventory
            ItemStack[] oldInventory = new ItemStack[oldSize];
            for(int i=0; i < oldSize; i++){
                oldInventory[i] = handler.getStackInSlot(i);
            }
            handler.setSize(WALLET_TOTAL_COUNT);

            //If inventory got bigger
            if(oldSize < WALLET_TOTAL_COUNT){
                //Putting old inventory back in
                for(int i=0; i < oldSize; i++){
                    handler.setStackInSlot(i, oldInventory[i]);
                }
                writeInventoryTag(stack, handler);

            //If inventory got smaller
            }else{
                //Putting old inventory back in that can fit
                for(int i=0; i < WALLET_TOTAL_COUNT; i++){
                    handler.setStackInSlot(i, oldInventory[i]);
                }
                writeInventoryTag(stack, handler);

                //Spawning item ingame that don't fit
                for(int i=WALLET_TOTAL_COUNT; i < oldSize; i++){
                    if(!oldInventory[i].isEmpty()) {
                        World world = player.getEntityWorld();
                        BlockPos pos = player.getPosition();
                        world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), oldInventory[i]));
                    }
                }
            }
        }
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        super.onContainerClosed(playerIn);
        if(playerIn.getHeldItemMainhand().getItem() == ModItems.itemWallet) checkMetadataClosed(playerIn.getHeldItemMainhand());
    }

    public void checkMetadataClosed(ItemStack stack){
        int slotsFilled = 0;
        for(int i = 0; i < itemStackHandler.getSlots(); i++){
            if(!itemStackHandler.getStackInSlot(i).isEmpty()) slotsFilled++;
        }

        int meta = 0;
        if(slotsFilled == 0){
        }else if (slotsFilled >= 1 && slotsFilled < (4 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 1;
        }else if (slotsFilled >= (4 * ItemWallet.WALLET_ROW_COUNT) && slotsFilled < (7 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 2;
        }else if (slotsFilled >= (7 * ItemWallet.WALLET_ROW_COUNT) && slotsFilled <= (9 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 3;
        }

        stack.setItemDamage(meta);
    }

    public void checkmetadataOpen(ItemStack stack){
        int slotsFilled = 0;
        for(int i = 0; i < itemStackHandler.getSlots(); i++){
            if(!itemStackHandler.getStackInSlot(i).isEmpty()) slotsFilled++;
        }

        int meta = 4;
        if(slotsFilled > 0) meta = 5;
        stack.setItemDamage(meta);


        //Now Setting Tag for client to read
        if(slotsFilled == 0){
            meta = 0;
        }else if (slotsFilled >= 1 && slotsFilled < (4 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 1;
        }else if (slotsFilled >= (4 * ItemWallet.WALLET_ROW_COUNT) && slotsFilled < (7 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 2;
        }else if (slotsFilled >= (7 * ItemWallet.WALLET_ROW_COUNT) && slotsFilled <= (9 * ItemWallet.WALLET_ROW_COUNT)){
            meta = 3;
        }

        NBTTagCompound compound = stack.getTagCompound();
        compound.setInteger("full", meta);
        stack.setTagCompound(compound);
    }
}
