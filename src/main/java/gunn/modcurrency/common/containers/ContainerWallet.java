package gunn.modcurrency.common.containers;

import gunn.modcurrency.client.guis.GuiWallet;
import gunn.modcurrency.common.core.util.SlotCustomizable;
import gunn.modcurrency.common.items.ItemWallet;
import gunn.modcurrency.common.items.ModItems;
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

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
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
 * File Created on 2017-01-16
 */
public class ContainerWallet extends Container{
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

    private ItemStackHandler itemStackHandler;

    public ContainerWallet(EntityPlayer player, ItemStack wallet){
        if(!wallet.hasTagCompound()){
            NBTTagCompound compound = new NBTTagCompound();
            wallet.setTagCompound(compound);
            writeInventoryTag(wallet, new ItemStackHandler(WALLET_TOTAL_COUNT));
        }

        didInventorySizeChange(wallet, player);

        setupPlayerInv(player.inventory);
        setupWalletInv(wallet);
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
        itemStackHandler = readInventoryTag(wallet);

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        for (int y = 0; y < WALLET_ROW_COUNT; y++){
            for (int x = 0; x < WALLET_COLUMN_COUNT; x++){
                int slotNum = y * WALLET_COLUMN_COUNT + x;
                int xpos =  7 + x * SLOT_X_SPACING;

                switch(y) {
                    default:
                    case 0: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 35, ModItems.itemBanknote));
                        break;
                    case 1: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 54, ModItems.itemBanknote));
                        break;
                    case 2: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 18, ModItems.itemBanknote));
                        break;
                    case 3: addSlotToContainer(new SlotCustomizable(itemStackHandler, slotNum, xpos, 72, ModItems.itemBanknote));
                        break;
                }
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    public void writeInventoryTag(ItemStack stack, ItemStackHandler inventory){
        NBTTagCompound compound = stack.getTagCompound();
        compound.setTag("inventory", inventory.serializeNBT());
        stack.setTagCompound(compound);
    }

    public ItemStackHandler readInventoryTag(ItemStack stack){
        NBTTagCompound compound = stack.getTagCompound();
        ItemStackHandler itemStackHandler = new ItemStackHandler(WALLET_TOTAL_COUNT);
        itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("inventory"));

        return itemStackHandler;
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId >= 0 && slotId <= WALLET_FIRST_SLOT_INDEX + WALLET_TOTAL_COUNT) {
            if(player.getHeldItemMainhand() != inventorySlots.get(slotId).getStack()) {
                ItemStack stack = super.slotClick(slotId, dragType, clickTypeIn, player);
                writeInventoryTag(player.getHeldItemMainhand(), itemStackHandler);
                return stack;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack sourceStack = null;
        Slot slot = this.inventorySlots.get(index);

        if(playerIn.getHeldItemMainhand() != inventorySlots.get(index).getStack()) {
            if (slot != null && slot.getHasStack()) {
                ItemStack copyStack = slot.getStack();
                sourceStack = copyStack.copy();

                if (index < PLAYER_TOTAL_COUNT) {     //Player Inventory Slots
                    if (slot.getStack().getItem() == ModItems.itemBanknote) {
                        if (!this.mergeItemStack(copyStack, WALLET_FIRST_SLOT_INDEX, WALLET_FIRST_SLOT_INDEX + WALLET_TOTAL_COUNT, false)) {
                            return null;
                        }
                    } else {
                        return null;
                    }
                } else if (index >= WALLET_FIRST_SLOT_INDEX && index < WALLET_FIRST_SLOT_INDEX + WALLET_TOTAL_COUNT) {     //Wallet Inventory
                    if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        }
        return sourceStack;
    }

    public void didInventorySizeChange(ItemStack stack, EntityPlayer player){
        ItemStackHandler handler = readInventoryTag(stack);
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

                //Spawning items ingame that don't fit
                for(int i=WALLET_TOTAL_COUNT; i < oldSize; i++){
                    if(oldInventory[i] != null) {
                        World world = player.getEntityWorld();
                        BlockPos pos = player.getPosition();
                        world.spawnEntityInWorld(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), oldInventory[i]));
                    }
                }
            }
        }
    }






















}
