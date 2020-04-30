package beardlessbrady.modcurrency2.block.economyblocks.tradein;

import beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase;
import beardlessbrady.modcurrency2.item.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
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
    public final int TE_OUTPUT_SLOT_COUNT = 1;
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

    private TileTradein te;

    public ContainerTradein(EntityPlayer entityPlayer, TileTradein te){
        this.te = te;

        te.setPlayerUsing(entityPlayer.getUniqueID()); // Set playerUsing so only one player can access machine at a time */

        setupPlayerInv(entityPlayer.inventory);
        setupTeInv();
    }

    /** Required to allow block to be interacted with **/
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    /** Setup Player's inventory in the UI **/
    private void setupPlayerInv(InventoryPlayer invPlayer) {
        // Size of SLOT box X and Y */
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        // Starting positions of both the hotbar and the players inventory
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 157;
        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 99;

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
    private void setupTeInv(){
        IItemHandler iItemHandler = this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

        addSlotToContainer(new SlotItemHandler(iItemHandler, TE_INPUT_SLOT_INDEX, 15, 32)); // Add INPUT slot */

        // Size of SLOT box X and Y */
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        // Starting positions of tile inventory */
        final int TE_INV_XPOS = 44;
        int TE_INV_YPOS = -22;

        for (int y = 0; y < TE_INV_COLUMN_COUNT; y++) { // Loops through tile INVENTORY and add it to UI */
            for (int x = 0; x < TE_INV_ROW_COUNT; x++) {
                int slotNum = 1 + y * TE_INV_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(iItemHandler, slotNum, xpos, ypos));
            }
        }
    }

    /** Method activates when player clicks a slot in the UI **/
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        int index = slotId - 37; // All the UI's inventory is combined including the players. Therefore the TE's inventory starts at 37 */

        ItemStack playerStack = player.inventory.getItemStack();
        ItemStack copyPlayerStack = playerStack.copy();

        /* Special Case for Pickup_ALL clickType. I copied most of this block of code from the Container class. I modified it so Pickup_All only does
         * anything with the player inventory NOT the TE inventory since it would break things. */
        //<editor-fold desc="SPECIAL CASE: PICKUP_ALL">
        if (clickTypeIn == ClickType.PICKUP_ALL) {
            if (slotId >= 0 && slotId <= PLAYER_TOTAL_COUNT) {
                Slot slot = this.inventorySlots.get(slotId);
                if (!playerStack.isEmpty() && (slot == null || !slot.getHasStack() || !slot.canTakeStack(player))) {
                    int i = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                    int j = dragType == 0 ? 1 : -1;

                    for (int k = 0; k < 2; ++k) {
                        for (int l = i; l >= 0 && l <= PLAYER_TOTAL_COUNT && playerStack.getCount() < playerStack.getMaxStackSize(); l += j) {
                            Slot slot1 = this.inventorySlots.get(l);

                            if (slot1.getHasStack() && canAddItemToSlot(slot1, playerStack, true) && slot1.canTakeStack(player) && this.canMergeSlot(playerStack, slot1)) {
                                ItemStack itemstack2 = slot1.getStack();

                                if (k != 0 || itemstack2.getCount() != itemstack2.getMaxStackSize()) {
                                    int i1 = Math.min(playerStack.getMaxStackSize() - playerStack.getCount(), itemstack2.getCount());
                                    ItemStack itemstack3 = slot1.decrStackSize(i1);
                                    playerStack.grow(i1);

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
                return ItemStack.EMPTY;
            }
        }
        //</editor-fold>

        if (slotId >= 37 && slotId <= 61) { // TE Inventory Slots */
            if (te.getField(TileTradein.FIELD_MODE) == 1) { // Clicking in the TE inventory only does anything in STOCK MODE*/
                if (dragType == 0 || (dragType == 1 && clickTypeIn == ClickType.QUICK_CRAFT)) { // Left Click */

                    /* If the size of the currently clicked itemTradein Stack is > 0 that means items have been sold to the machine
                     * and are available to be picked up by the owner. This allows the owner to grab items from their slots  */
                    if(te.getItemTradein(index).getSize() > 0){
                        if(playerStack.isEmpty()){
                            player.inventory.setItemStack(te.getItemTradein(index).shrinkSizeWithStackOutput(64));
                        }

                    // If the itemTradein Stack is empty then clicking on it will either: */
                    } else {
                        if (playerStack.isEmpty()) { // If player hand is empty delete item */
                            te.voidItem(index);
                        } else { // If player hand has an item replace slot with it */
                            te.setItemTradein(index, new ItemTradein(copyPlayerStack));
                        }
                    }

                } else if (dragType == 1) { // Right Click */

                    // Right clicking moves the 'selection tag' to the clicked slot*/
                    if (!(te.getField(TileEconomyBase.FIELD_SELECTED) == slotId)) { // If 'selection tag' is not already on clicked slot */
                        te.setSelectedName(te.getItemTradein(index).getStack().getDisplayName()); // Updates displayed name on 'info tag' to selected item */
                        te.setField(TileEconomyBase.FIELD_SELECTED, index); // Saved currently selected slot to tile field */
                    }

                    if (te.getItemTradein(index).getStack().isEmpty()) { // If player right clicks with a stack will copy 1 of them to slot */
                        te.setItemTradein(index, new ItemTradein(copyPlayerStack));
                    }

                } else if (dragType == 5) {  // QUICK_CRAFT and right clicking with a stack will copy 1 of them to each slot dragged on */
                    if (te.getItemTradein(index).getStack().isEmpty()) { //Place 1
                        te.setItemTradein(index, new ItemTradein(copyPlayerStack));
                    }
                }
            }
            return ItemStack.EMPTY;
        } else if (slotId == 62){ //OUTPUT slot, players can click item and it will go directly to inventory or wont move if no room
            if(!this.inventorySlots.get(slotId).getStack().isEmpty()) {
                if (!this.mergeItemStack(inventorySlots.get(slotId).getStack(), 0, PLAYER_TOTAL_COUNT, false)) {
                    te.setMessage("Not enough space in inventory!", (byte) 40);
                    player.playSound(SoundEvents.BLOCK_FIRE_EXTINGUISH, 1, 0.5F);
                    return ItemStack.EMPTY;
                }
            }
            return ItemStack.EMPTY;
        }
        return super.slotClick(slotId, dragType, clickTypeIn, player); // Clicking player inventory & INPUT slot acts normally */
    }

    /** Method activates when player shift + clicks a slot **/
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int slotId) {
        ItemStack itemStack = this.inventorySlots.get(slotId).getStack();

        // If Item being SHIFTED is not empty and in Player Inventory */
        if (!itemStack.isEmpty()) {
            if (slotId < PLAYER_TOTAL_COUNT) {
                if (te.getField(TileEconomyBase.FIELD_MODE) == 1) { //STOCK MODE
                    // If Item is Currency then shift it into INPUT */
                    if (itemStack.getItem().equals(ModItems.itemCurrency)) {
                        playerIn.world.playSound(playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundCategory.BLOCKS, 1.0F, 30.0F, false);

                        if (!this.mergeItemStack(itemStack, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX, PLAYER_TOTAL_COUNT + TE_INPUT_SLOT_INDEX + 1, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else { //SHIFT clicking an item will place a ghost of it in the next empty slot of the machine
                        for(int i = 0; i < te.TE_INVENTORY_SLOT_COUNT; i++){
                            if(te.getItemTradein(i).getStack().equals(ItemStack.EMPTY)){
                                te.setItemTradein(i, new ItemTradein(itemStack.copy()));
                                break;
                            }
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    //<editor-fold desc="Client Sync">
    private int[] cachedFields;

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
    }
}