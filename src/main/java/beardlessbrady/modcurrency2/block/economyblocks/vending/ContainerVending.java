package beardlessbrady.modcurrency2.block.economyblocks.vending;

import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import beardlessbrady.modcurrency2.item.ModItems;
import beardlessbrady.modcurrency2.utilities.UtilMethods;
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

import static beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class ContainerVending extends Container {

    /** Variables used to easily access slot counts and first slot indices **/
    //0-35 = Players Inv
    public final int HOTBAR_SLOT_COUNT = 9;
    public final int PLAYER_INV_ROW_COUNT = 3;
    public final int PLAYER_INV_COLUMN_COUNT = 9;
    public final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    public final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    //36 = INPUT
    //37-62 = TE INVENTORY
    //38 = OUTPUT
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

    /** Size of TE Inventory */
    final int TE_INV_COLUMN_COUNT = 5;
    final int TE_INV_ROW_COUNT = 5;

    private EntityPlayer player;
    private TileVending te;
    private int[] cachedFields;

    public ContainerVending(EntityPlayer entityPlayer, TileVending te) {
        player = entityPlayer;
        this.te = te;
        InventoryPlayer invPlayer = player.inventory;

        te.setPlayerUsing(player.getUniqueID()); // Set playerUsing so only one player can access machine at a time */

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    /** Setup Player's inventory in the UI **/
    private void setupPlayerInv(InventoryPlayer invPlayer) {
        // Size of SLOT box X and Y
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        // Starting positions of both the hotbar and the players inventory
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 184;
        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 126;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) // Loops through players HOTBAR and add it to UI */
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));


        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) { // Loops through players INVENTORY and add it to UI */
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++) {
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x; // Determines the current Slot Number by math */
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    /** Setup Tile's inventory in the UI **/
    private void setupTeInv() {
        IItemHandler iItemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        addSlotToContainer(new SlotItemHandler(iItemHandler, TE_INPUT_SLOT_INDEX, 145, 3)); // Add INPUT slot */

        // Size of SLOT box X and Y */
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        // Starting positions of tile inventory */
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = -30;

        for (int y = 0; y < TE_INV_COLUMN_COUNT; y++) { // Loops through tile INVENTORY and add it to UI */
            for (int x = 0; x < TE_INV_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_INV_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(iItemHandler, slotNum, xpos, ypos));
            }
        }

        //Output Slots
        for (int i = 0; i < TE_OUTPUT_SLOT_COUNT; i++)
            addSlotToContainer(new SlotItemHandler(iItemHandler, TE_OUTPUT_FIRST_SLOT_INDEX + i, 44 + (i * SLOT_X_SPACING), 77)); // Add OUTPUT slot */

    }

    /** Method activates when player clicks a slot in the UI **/
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        int index = slotId - 37; // All the UI's inventory is combined including the players. Therefore the TE's inventory starts at 37 */

        boolean creative = te.getField(TileVending.FIELD_CREATIVE) == 1; // Machine is Creative or not

        ItemStack playerStack = player.inventory.getItemStack();
        ItemStack copyPlayerStack = playerStack.copy();

        /* Special Case for Pickup_ALL clickType. I copied most of this block of code from the Container class. I modified it so Pickup_All only does
         * anything with the player inventory NOT the TE inventory since it would break things. */
        //<editor-fold desc="SPECIAL CASE: PICKUP_ALL">
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
                    if (UtilMethods.equalStacks(playerStack, te.getItemVendor(i).getStack(), false)) {
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

        if (slotId == GUI_INPUT_INDEX) {  // INPUT Slot */
            if (playerStack.getItem().equals(ModItems.itemCurrency) || playerStack.isEmpty()) { // If player has an empty hand or is trying to put currency in the input slot success
                player.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);
            } else {
                return ItemStack.EMPTY;
            }
        }

        if (slotId >= 37 && slotId <= 61) { // TE Inventory Slots
            if (te.getField(TileVending.FIELD_MODE) == 1) { // Clicking in the TE inventory in STOCK MODE*/
                if (te.getItemVendor(index).getSize() == 0) { // If the item being clicked has a size of 0 then delete the item as its empty
                    te.voidItem(index);
                }

                if (dragType == 0 || (dragType == 1 && clickTypeIn == ClickType.QUICK_CRAFT)) { //Left Click or Right Click with QUICK_CRAFT
                    // If player hand is empty then shrink the current slot by a stack and put in players hand (unless creative)
                    if (playerStack.isEmpty()) {
                        if (!creative) {
                            player.inventory.setItemStack(te.getItemVendor(index).shrinkSizeWithStackOutput(64));
                        } else {
                            te.getItemVendor(index).shrinkSize(64);
                        }
                    } else { // If player hand is NOT EMPTY
                        if (!creative) {
                            // If the slot is empty (and players hand is not) place the stack into the slot and remove it from the players hand
                            if (te.getItemVendor(index).isEmpty()) {
                                player.inventory.setItemStack(ItemStack.EMPTY);
                                te.setItemVendor(index, new ItemVendor(copyPlayerStack));
                            } else { //If player hand and slot are not empty then try to add players stack to slot (if they are same item)
                                player.inventory.setItemStack(te.getItemVendor(index).growSizeWithStack(copyPlayerStack));
                            }
                        } else { // If creative then void the slot being clicked and set it as players hand stack
                            te.voidItem(index);
                            te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                        }
                    }

                    // If slot size is 0 then check if it has a bundle and if so, break up bundle then void the item
                    if (te.getItemVendor(index).getSize() == 0) {
                        int[] bundle = te.getItemVendor(te.getItemVendor(index).getBundleMainSlot()).getBundle();
                        if(bundle != null){
                            for(int i = 0; i < bundle.length; i++)
                                te.getItemVendor(bundle[i]).setBundle(null);
                        }
                        te.voidItem(index);
                    }
                } else if (dragType == 1) { // Right Click
                    if (te.getKey(KEY_SHIFT)) { // If KEY_SHIFT is clicked then create a bundle
                        te.getItemVendor(index).setBundle(new int[0]);
                    } else {
                        // Right clicking moves the 'selection tag' to the clicked slot
                        if (!(te.getField(FIELD_SELECTED) == slotId)) { // If 'selection tag' is not already on clicked slot */
                            short toSelect = (short)index;
                            if (te.getItemVendor(index).getBundleMainSlot() != -1) { // If the slot is part of a bundle (mainSlot not = -1)
                                toSelect = (short) te.getItemVendor(index).getBundleMainSlot(); // Force selection to be the bundles main slot
                                te.setSelectedName("bundle"); // Name Bundle
                            } else { // If slot is NOT part of a bundle select as normal
                                te.setSelectedName(te.getItemVendor(index).getStack().getDisplayName());
                            }
                            te.setField(FIELD_SELECTED, toSelect);
                        }

                        // Right Click and slot is empty then place one of the players item stack
                        if (te.getItemVendor(index).getStack().isEmpty()) {
                            te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                            playerStack.shrink(1);
                        }
                    }


                } else if (dragType == 5) { // Quick_Craft and Right Click
                    // Place one of the players item stack
                    if (te.getItemVendor(index).getStack().isEmpty()) {
                        te.setItemVendor(index, new ItemVendor(copyPlayerStack, 1));
                        playerStack.shrink(1);
                    }
                }
            } else { // Clicking in the TE inventory in SELL MODE*/
                if (dragType == 0) { // Left Click
                    if (te.getItemVendor(index).getBundleMainSlot() == -1) { // If not in a bundle (mainSlot = -1) then buy item as normally
                        buyItem(index, 1);
                    } else { // In a bundle buy as a bundle
                        buyBundle(te.getItemVendor(index).getBundleMainSlot());
                    }
                }
            }
            return ItemStack.EMPTY;

        } else if (slotId >= 62 && slotId <= 66) { // Output Slots
            if(!this.inventorySlots.get(slotId).getStack().isEmpty()) { // If slot is NOT empty
                if (!this.mergeItemStack(inventorySlots.get(slotId).getStack(), 0, PLAYER_TOTAL_COUNT, false)) { // Try to merge with output slots otherwise error
                    te.setMessage("Not enough space in inventory!", (byte) 40);
                    player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 0.5F);
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    /** Method activates when player shift + clicks a slot **/
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotId) {
        ItemStack itemStack = this.inventorySlots.get(slotId).getStack();
        ItemStack copyStack = itemStack.copy();

        // If Item being SHIFTED is not empty and in Player Inventory */
        if (!itemStack.isEmpty()) {
            if (slotId < PLAYER_TOTAL_COUNT) {
                if (te.getField(TileEconomyBase.FIELD_MODE) == 0) { // SELL MODE

                    // If Item is Currency then shift it into INPUT */
                    if (itemStack.getItem().equals(ModItems.itemCurrency)) {
                        playerIn.world.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);

                        if (!this.mergeItemStack(itemStack, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else { // STOCK MODE
                    for (int i = 0; i < TE_INVENTORY_SLOT_COUNT; i++) {
                        if (UtilMethods.equalStacks(itemStack, inventorySlots.get(GUI_INVENTORY_FIRST_INDEX + i).getStack(), false)) {
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
                            return ItemStack.EMPTY;
                        }
                    }
                }
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

    /** Method activated on container close **/
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

    /** Buy an Item Method **/
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

                        long newCashReserve = te.getLongField(TileEconomyBase.LONG_FIELD_CASHRESERVE) - (te.getItemVendor(index).getCost() * (count / te.getItemVendor(index).getAmount()));
                        long newCashRegister = te.getLongField(TileEconomyBase.LONG_FIELD_CASHREGISTER) + (te.getItemVendor(index).getCost() * (count / te.getItemVendor(index).getAmount()));
                        te.setLongField(TileEconomyBase.LONG_FIELD_CASHRESERVE, newCashReserve);
                        te.setLongField(TileEconomyBase.LONG_FIELD_CASHREGISTER, newCashRegister);
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

                long newCashReserve = te.getLongField(TileEconomyBase.LONG_FIELD_CASHRESERVE) - (te.getItemVendor(mainIndex).getCost());
                long newCashRegister = te.getLongField(TileEconomyBase.LONG_FIELD_CASHREGISTER) + (te.getItemVendor(mainIndex).getCost());
                te.setLongField(TileEconomyBase.LONG_FIELD_CASHRESERVE, newCashReserve);
                te.setLongField(TileEconomyBase.LONG_FIELD_CASHREGISTER, newCashRegister);
            }
        }
        return ItemStack.EMPTY;
    }




}