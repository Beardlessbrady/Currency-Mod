package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.init.CommonRegistry;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiScreenEvent;

import javax.swing.text.JTextComponent;
import java.math.BigInteger;
import java.util.LinkedList;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContainer extends Container {
    private VendingContentsOverloaded stockContents;
    private VendingContents inputContents;
    private VendingContents outputContents;
    private World world;

    // Slot Calculations
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;

    public static final int STOCK_ROW_COUNT = VendingTile.STOCK_ROW_COUNT;
    public static final int STOCK_COLUMN_COUNT = VendingTile.STOCK_COLUMN_COUNT;
    public static final int STOCK_SLOT_COUNT = VendingTile.STOCK_SLOT_COUNT;
    public static final int INPUT_SLOTS_COUNT = VendingTile.INPUT_SLOTS_COUNT;
    public static final int OUTPUT_SLOTS_COUNT = VendingTile.OUTPUT_SLOTS_COUNT;
    public static final int VENDING_TOTAL_SLOTS_COUNT = STOCK_SLOT_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    // Slot Index: The unique index for all slots in this container i.e. 0 - 35 for invPlayer then 36 - 41 for VendingContents
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int HOTBAR_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX;
    private static final int PLAYER_INVENTORY_FIRST_SLOT_INDEX = HOTBAR_FIRST_SLOT_INDEX + HOTBAR_SLOT_COUNT;
    private static final int FIRST_STOCK_SLOT_INDEX = PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int FIRST_INPUT_SLOT_INDEX = FIRST_STOCK_SLOT_INDEX + STOCK_SLOT_COUNT;
    private static final int FIRST_OUTPUT_SLOT_INDEX = FIRST_INPUT_SLOT_INDEX + INPUT_SLOTS_COUNT;

    // GUI pos of inventory grid
    public static final int PLAYER_INVENTORY_XPOS = 8;
    public static final int PLAYER_INVENTORY_YPOS = 128;
    public static final int HOTBAR_XPOS = 8;
    public static final int HOTBAR_YPOS = 186;
    public static final int STOCK_INVENTORY_XPOS = 39;
    public static final int STOCK_INVENTORY_YPOS = -31;
    public static final int OUTPUT_SLOTS_XPOS = 61;
    public static final int OUTPUT_SLOTS_YPOS = 71;
    public static final int INPUT_SLOTS_XPOS = 117;
    public static final int INPUT_SLOTS_YPOS = 10;
    public static final int SLOT_X_SPACING = 18;
    public static final int SLOT_Y_SPACING = 18;

    // slot number is the slot number within each component;
    // i.e. invPlayer slots 0 - 35 (hotbar 0 - 8 then main inventory 9 to 35)
    // and vending: input slots 0 - 2, output slots 0, stock slots 0 - 16 (for 1x2)

    private VendingStateData vendingStateData;
    private VendingTile tile;

    // --- VANILLA

    public static VendingContainer createContainerClient(int windowID, PlayerInventory playerInventory, PacketBuffer extraData) {
        VendingStateData vendingStateData = new VendingStateData(extraData.readVarIntArray());
        VendingContents input = new VendingContents(INPUT_SLOTS_COUNT);
        VendingContents output = new VendingContents(OUTPUT_SLOTS_COUNT);
        VendingContentsOverloaded stock = new VendingContentsOverloaded(STOCK_SLOT_COUNT);
        VendingTile tile = (VendingTile) playerInventory.player.world.getTileEntity(extraData.readBlockPos());

        return new VendingContainer(windowID, playerInventory, stock, input, output, vendingStateData, tile);
    }

    public static VendingContainer createContainerServer(int windowID, PlayerInventory playerInventory, VendingContentsOverloaded stock, VendingContents input, VendingContents output, VendingStateData vendingStateData, VendingTile tile) {
        return new VendingContainer(windowID, playerInventory, stock, input, output, vendingStateData, tile);
    }

    public VendingContainer(int windowID, PlayerInventory playerInventory, VendingContentsOverloaded stock, VendingContents input, VendingContents output, VendingStateData vendingStateData, VendingTile tile) {
        super(CommonRegistry.CONTAINER_VENDING.get(), windowID);
        if( CommonRegistry.CONTAINER_VENDING.get() == null)
            throw new IllegalStateException("Must initialise containerTypeVendingContainer before constructing a ContainerVending!");

        this.stockContents = stock;
        this.inputContents = input;
        this.outputContents = output;
        this.vendingStateData = vendingStateData;
        this.world = playerInventory.player.world;
        this.tile = tile;
        dragging = false;

        trackIntArray(this.vendingStateData);
        trackIntArray(this.stockContents.getStackSizeIntArray());
        trackIntArray(this.stockContents.vendingComponentContents.getPriceMillionArray());
        trackIntArray(this.stockContents.vendingComponentContents.getPriceThousandArray());
        trackIntArray(this.stockContents.vendingComponentContents.getPriceCentArray());

        generateSlots(playerInventory, stock, input, output);

        vendingStateData.set(VendingStateData.MODE_INDEX, 0);
        vendingStateData.set(VendingStateData.EDITPRICE_INDEX, 0);
        vendingStateData.set(VendingStateData.BUYMODE_INDEX, 0);

        tile.setPlayerUsing(playerInventory.player.getUniqueID());
    }

    @Override
    public void onContainerClosed(PlayerEntity playerIn) {
        super.onContainerClosed(playerIn);

        tile.voidPlayerUsing();
    }

    public VendingTile getTile(){
        return this.tile;
    }

    private void generateSlots(PlayerInventory invPlayer, VendingContentsOverloaded stock, VendingContents input, VendingContents output){
        this.stockContents = stock;
        this.inputContents = input;
        this.outputContents = output;
        this.world = invPlayer.player.world;

        // Add the players hotbar to the gui
        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) { // x represents slot num
            addSlot(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        // Add the expanded player inventory to gui
        for (int y = 0; y < PLAYER_INVENTORY_ROW_COUNT; y++) {
            for (int x = 0; x < PLAYER_INVENTORY_COLUMN_COUNT; x++) {
                int slotNumber = HOTBAR_SLOT_COUNT + y * PLAYER_INVENTORY_COLUMN_COUNT + x;
                int xpos = PLAYER_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INVENTORY_YPOS + y * SLOT_Y_SPACING;
                addSlot(new Slot(invPlayer, slotNumber,  xpos, ypos));
            }
        }

        // Add Stocks slot to gui
        final int STOCK_Y_SPACING = 22;
        for (int y = 0; y < STOCK_ROW_COUNT; y++) {
            for (int x = 0; x < STOCK_COLUMN_COUNT; x++) {
                int slotNumber = y * STOCK_COLUMN_COUNT + x;
                int xpos = STOCK_INVENTORY_XPOS + x * SLOT_X_SPACING;
                int ypos = STOCK_INVENTORY_YPOS + y * STOCK_Y_SPACING;
                addSlot(new StockSlot(stockContents, slotNumber, xpos, ypos));
            }
        }

        // Add Input slot to gui
        addSlot(new InputSlot(inputContents, 0, INPUT_SLOTS_XPOS, INPUT_SLOTS_YPOS));

        // Add Output slots to gui
        for (int x = 0; x < OUTPUT_SLOTS_COUNT; x++) { // x is slot num
            addSlot(new OutputSlot(outputContents, x, OUTPUT_SLOTS_XPOS + SLOT_X_SPACING * x, OUTPUT_SLOTS_YPOS));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return stockContents.isUsableByPlayer(playerIn) && inputContents.isUsableByPlayer(playerIn) && outputContents.isUsableByPlayer(playerIn);
    }

    // ---- Slot manipulation ----
    private boolean dragging = false;
    private LinkedList<Integer> dragSlots = new LinkedList<Integer>();

    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        //System.out.println(slotId + " " + dragType + " " + clickTypeIn);

        try {
            // Quick Crafting
            if(slotId == -999) {
                dragClick(slotId, dragType, clickTypeIn, player);
            }

            if (clickTypeIn == ClickType.QUICK_CRAFT && dragging){
                dragSlots.add(slotId);
                return ItemStack.EMPTY;
            } else if (clickTypeIn == ClickType.QUICK_CRAFT) { // Probably will never be reached
                return ItemStack.EMPTY;
            } else if (dragging){ // Dragging out of QUICK_CRAFT
                dragging = false;
                dragSlots = new LinkedList<Integer>();
            }


            if ((slotId >= HOTBAR_FIRST_SLOT_INDEX && //PLAYER INVENTORY
                    slotId < PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT)) {
                return playerSlotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= FIRST_STOCK_SLOT_INDEX && // STOCK INVENTORY
                    slotId < FIRST_INPUT_SLOT_INDEX) {
                return stockSlotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= FIRST_INPUT_SLOT_INDEX && // INPUT INVENTORY
                    slotId < FIRST_OUTPUT_SLOT_INDEX) {
                return inputSlotClick(slotId, dragType, clickTypeIn, player);
            } else if (slotId >= FIRST_OUTPUT_SLOT_INDEX && // OUTPUT INVENTORY
                    slotId < FIRST_OUTPUT_SLOT_INDEX + OUTPUT_SLOTS_COUNT){
                return outputSlotClick(slotId, dragType, clickTypeIn, player);
            } else {
                // SlotID -1...not sure what this is yet
            }
            return ItemStack.EMPTY;
        } catch (Exception exception) {
            System.out.println("CRASH IN VENDING CONTAINER- slotid:" + slotId + " dragType:" + dragType + " clickType:" + clickTypeIn + " player:" + player);
            System.out.println(exception);
            return ItemStack.EMPTY;
        }
    }

    private ItemStack playerSlotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player){
        // Follows VANILLA PICKUP_ALL except only pulls from players inventory
        if (clickTypeIn == ClickType.PICKUP_ALL && slotId >= 0) {
            Slot slot2 = this.inventorySlots.get(slotId);
            ItemStack itemstack5 = player.inventory.getItemStack();
            if (!itemstack5.isEmpty() && (slot2 == null || !slot2.getHasStack() || !slot2.canTakeStack(player))) {
                int j1 = dragType == 0 ? 0 : this.inventorySlots.size() - 1;
                int i2 = dragType == 0 ? 1 : -1;

                for(int j = 0; j < 2; ++j) {
                    for(int k = j1; k >= 0 && k < FIRST_STOCK_SLOT_INDEX && itemstack5.getCount() < itemstack5.getMaxStackSize(); k += i2) {
                        Slot slot1 = this.inventorySlots.get(k);
                        if (slot1.getHasStack() && canAddItemToSlot(slot1, itemstack5, true) && slot1.canTakeStack(player) && this.canMergeSlot(itemstack5, slot1)) {
                            ItemStack itemstack3 = slot1.getStack();
                            if (j != 0 || itemstack3.getCount() != itemstack3.getMaxStackSize()) {
                                int l = Math.min(itemstack5.getMaxStackSize() - itemstack5.getCount(), itemstack3.getCount());
                                ItemStack itemstack4 = slot1.decrStackSize(l);
                                itemstack5.grow(l);
                                if (itemstack4.isEmpty()) {
                                    slot1.putStack(ItemStack.EMPTY);
                                }

                                slot1.onTake(player, itemstack4);
                            }
                        }
                    }
                }
            }
            this.detectAndSendChanges();
            return ItemStack.EMPTY;
        }

        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    private ItemStack stockSlotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player){
        boolean creative = vendingStateData.get(VendingStateData.CREATIVE_INDEX) == 1;

        int index = slotId - FIRST_STOCK_SLOT_INDEX;
        ItemStack playerStack = player.inventory.getItemStack();
        ItemStack slotStack = this.stockContents.getStackInSlot(index);
        int slotCount = this.stockContents.getSizeInSlot(index);

        if (vendingStateData.get(VendingStateData.MODE_INDEX) == 0) { // Sell
            if (clickTypeIn == ClickType.PICKUP || clickTypeIn == ClickType.QUICK_MOVE) {
                int[] price = priceFromBuyMode(index);
                int priceD = price[0];
                int priceC = price[1];
                int priceCount = price[2];
                int cashD = vendingStateData.get(VendingStateData.CASHDOLLAR_INDEX);
                int cashC = vendingStateData.get(VendingStateData.CASHCENT_INDEX);

                long priceTest = (long)priceD + (long)vendingStateData.get(VendingStateData.INCOMEDOLLAR_INDEX);
                boolean OVERMAXINT = priceTest >= Integer.MAX_VALUE;

                if (canAfford(index) && (!OVERMAXINT)) {
                    ItemStack buyStack = slotStack.copy();
                    buyStack.setCount(priceCount);

                    // Check for room in output
                    for (int i = FIRST_OUTPUT_SLOT_INDEX; i < FIRST_OUTPUT_SLOT_INDEX + OUTPUT_SLOTS_COUNT; i++) {
                        boolean success = false;
                        if (getSlot(i).getStack().isEmpty()) { // EMPTY SLOT
                            getSlot(i).putStack(buyStack);
                            success = true;
                        } else { // Not Empty
                            if (canAddItemToSlot(getSlot(i), buyStack, false)) {
                                getSlot(i).getStack().grow(buyStack.getCount());
                                success = true;
                            }
                        }

                        if (success) {
                            // Reduce Price and Stock
                            if(!creative) {
                                stockContents.decrStackSize(index, buyStack.getCount());
                            }

                            cashD -= priceD;
                            cashC -= priceC;
                            int[]roundCents2 = CurrencyItem.roundCents(cashC); // If cents are negative, roundCents will deduct from dollar
                            cashD += roundCents2[0];
                            cashC = roundCents2[1];

                            int incomeD = vendingStateData.get(VendingStateData.INCOMEDOLLAR_INDEX);
                            int incomeC = vendingStateData.get(VendingStateData.INCOMECENT_INDEX);
                            incomeD += priceD;
                            incomeC += priceC;
                            int[]roundCents3 = CurrencyItem.roundCents(incomeC);
                            incomeD += roundCents3[0];
                            incomeC = roundCents3[1];

                            vendingStateData.set(VendingStateData.CASHDOLLAR_INDEX, cashD);
                            vendingStateData.set(VendingStateData.CASHCENT_INDEX, cashC);
                            if(!creative) {
                                vendingStateData.set(VendingStateData.INCOMEDOLLAR_INDEX, incomeD);
                                vendingStateData.set(VendingStateData.INCOMECENT_INDEX, incomeC);
                            }
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }
            return ItemStack.EMPTY;
        } else { // Restock
            // If EDIT PRICE is open, right click changes slot selected, left acts as usual
            if (vendingStateData.get(VendingStateData.EDITPRICE_INDEX) == 1 && (dragType == 1)) { // RIGHT
                vendingStateData.set(VendingStateData.SELECTEDSLOT_INDEX, index);
                return ItemStack.EMPTY;
            }

            if (clickTypeIn == ClickType.PICKUP_ALL) {
                if (!playerStack.isEmpty() && (slotStack.isEmpty())) {
                    for (int k = 0; k < this.stockContents.getSizeInventory(); k++) {
                        if (!stockContents.getStackInSlot(k).isEmpty() && areItemsAndTagsEqual(stockContents.getStackInSlot(k), playerStack)) {
                            int growthLeft = playerStack.getMaxStackSize() - playerStack.getCount();

                            if (stockContents.getStackSize(k) >= growthLeft) {
                                playerStack.grow(growthLeft);
                                stockContents.decrStackSize(k, growthLeft);
                            } else { // Stock size < growthLeft
                                playerStack.grow(stockContents.getStackSize(k));
                                stockContents.decrStackSize(k, stockContents.getStackSize(k));
                            }
                        }
                        if (playerStack.getMaxStackSize() == playerStack.getCount()) {
                            this.detectAndSendChanges();
                            return ItemStack.EMPTY;
                        }
                    }
                }
                this.detectAndSendChanges();
                return ItemStack.EMPTY;
            }

            if (clickTypeIn == ClickType.PICKUP) { // Regular Click = Place all/Pickup all
                if (playerStack.isEmpty()) { // Player Stack empty, PICKUP
                    if (!slotStack.isEmpty()) { //TODO In creative allow opening Creative menu to place item here if both empty
                        if (slotCount == 0) { // If Empty clear slot
                            stockContents.setStackAndSize(index, ItemStack.EMPTY);
                        } else {
                            int toExtract;
                            if (dragType == 0) { // LEFT Click: Grab Full Stack
                                int extractMax = slotStack.getMaxStackSize();
                                toExtract = extractMax - slotCount;

                                if (toExtract <= 0) { // Equal to VANILLA Max Stack Size or OVERLOADED past it
                                    toExtract = extractMax;
                                } else { // under VANILLA Max Stack Size
                                    toExtract = extractMax - toExtract;
                                }
                            } else { // RIGHT Click: Grab half of full stack or half of whats left
                                toExtract = slotCount / 2;

                                if (toExtract > slotStack.getMaxStackSize()) // Half of Stack is more than VANILLA stack limit
                                    toExtract = slotStack.getMaxStackSize() / 2;

                                if (toExtract == 0)
                                    toExtract = 1;
                            }
                            player.inventory.setItemStack(stockContents.decrStackSize(index, toExtract));
                        }
                    }
                } else { // Player Stack: PLACE STACK
                    if (dragType == 0) { // LEFT Click: Place full stack
                        if (slotStack.isEmpty() || slotCount == 0) { // Slot EMPTY, place without issue
                            stockContents.setStackAndSize(index, playerStack);
                            player.inventory.setItemStack(ItemStack.EMPTY);
                        } else { // Slot NOT EMPTY, try to merge or don't place if incompatible
                            player.inventory.setItemStack(stockContents.growInventorySlotSize(index, playerStack));
                        }
                    } else { // RIGHT click: Place one of stack
                        ItemStack copyOne = playerStack.copy();
                        copyOne.setCount(1);
                        if (slotStack.isEmpty()) { // Slot EMPTY, place without issue
                            stockContents.setStackAndSize(index, copyOne);
                            player.inventory.getItemStack().shrink(1);
                        } else { // Slot NOT EMPTY, try to merge or don't place if incompatible
                            // If return is 0, shrink by 1, if return is not don't shrink
                            player.inventory.getItemStack().shrink((stockContents.growInventorySlotSize(index, copyOne).getCount() == 0) ? 1 : 0);
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }

    private ItemStack inputSlotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        int index = slotId - FIRST_INPUT_SLOT_INDEX;
        ItemStack playerStack = player.inventory.getItemStack();
        ItemStack slotStack = this.stockContents.getStackInSlot(index);
        int slotCount = this.stockContents.getSizeInSlot(index);
        if (!playerStack.isEmpty()) {
            if (playerStack.getItem().equals(CommonRegistry.ITEM_CURRENCY.get())) {
                return super.slotClick(slotId, dragType, clickTypeIn, player);
            }
        } else {
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }

    private ItemStack outputSlotClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        if ((clickTypeIn == ClickType.PICKUP && player.inventory.getItemStack().isEmpty()) || clickTypeIn == ClickType.QUICK_MOVE) { // Pickup / Transfer
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        return ItemStack.EMPTY;
    }

    /**
     * When Dragging is activated, (slot = -999 flags start and stop)
     */
    public ItemStack dragClick(int slotId, int dragType, ClickType clickTypeIn, PlayerEntity player) {
        dragging = !dragging;
        if (!dragging) { // Dragging has completed
            // Validate Drag Slots
            LinkedList<Integer> tempList = new LinkedList<>();
            for (int i = 0; i < dragSlots.size(); i++) {
                // If Player Inv OR (Stock Inv AND Stock Mode)
                if ((dragSlots.get(i) >= HOTBAR_FIRST_SLOT_INDEX && dragSlots.get(i) < PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT) //PLAYER INVENTORY
                        ||
                        ((dragSlots.get(i) >= FIRST_STOCK_SLOT_INDEX && dragSlots.get(i) < FIRST_INPUT_SLOT_INDEX) // STOCK INVENTORY
                                && (vendingStateData.get(VendingStateData.MODE_INDEX) == 1))
                        ||
                        ((dragSlots.get(i) >= FIRST_INPUT_SLOT_INDEX && dragSlots.get(i) < FIRST_OUTPUT_SLOT_INDEX))) { // Stock Mode
                    tempList.push(dragSlots.get(i)); // Valid slots are added to a temp list
                }
            }
            dragSlots = tempList; // Set dragSlots to temp validated slot list

            if(dragSlots.size() > 0) { // Not Empty after validation
                ItemStack playerStack = player.inventory.getItemStack();
                int divCount = (dragType == 2 ? playerStack.getCount() / dragSlots.size() : 1);
                ItemStack divStack = playerStack.copy();
                divStack.setCount(divCount);

                // Divide Stack and put in slot if possible
                for (int i = 0; i < dragSlots.size(); i++) {
                    if (dragSlots.get(i) >= HOTBAR_FIRST_SLOT_INDEX && dragSlots.get(i) < PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT) { //PLAYER INVENTORY
                        Slot inputSlot = inventorySlots.get(dragSlots.get(i));

                        // Enough room for whole divisor stack
                        if (canAddItemToSlot(inputSlot, divStack.copy(), false )) {
                            if (inputSlot.getStack().isEmpty()) {
                                inputSlot.putStack(divStack.copy());
                            } else {
                                inputSlot.getStack().grow(divCount);
                            }
                            playerStack.shrink(divCount);
                        } else {
                            // Same item, add some of stack
                            if (canAddItemToSlot(inventorySlots.get(dragSlots.get(i)), divStack.copy(), true )) {
                                int growthAmount = divCount + (inputSlot.getStack().getMaxStackSize() - (inputSlot.getStack().getCount() + divCount));
                                inputSlot.getStack().grow(growthAmount);
                                playerStack.shrink(growthAmount);
                            }
                        }
                    } else if ((dragSlots.get(i) >= FIRST_STOCK_SLOT_INDEX && dragSlots.get(i) < FIRST_INPUT_SLOT_INDEX) // STOCK INVENTORY
                            && (vendingStateData.get(VendingStateData.MODE_INDEX) == 1)) { // Stock Mode

                        int stockIndex = dragSlots.get(i) - FIRST_STOCK_SLOT_INDEX;
                        ItemStack slotStack = this.stockContents.getStackInSlot(stockIndex);
                        int slotCount = this.stockContents.getSizeInSlot(stockIndex);

                        // Enough room for whole divisor stack
                        if (canAddStackToOverloadedStack(slotStack, slotCount, stockContents.getStackLimit(stockIndex), divStack.copy(), true)) {
                            if (stockContents.getStackInSlot(stockIndex).isEmpty()) {
                                stockContents.setStackAndSize(stockIndex, divStack.copy());
                            } else {
                                stockContents.growInventorySlotSize(stockIndex, divStack.copy());
                            }
                            playerStack.shrink(divCount);
                        } else {

                            // Same item, add some of stack
                            if (canAddStackToOverloadedStack(slotStack, slotCount, stockContents.getStackLimit(stockIndex), divStack.copy(), false)) {
                                playerStack.shrink(divCount - stockContents.growInventorySlotSize(stockIndex, divStack.copy()).getCount()); // Shrink by divcount - leftover
                            }
                        }
                    } else if ((dragSlots.get(i) >= FIRST_INPUT_SLOT_INDEX && dragSlots.get(i) < FIRST_OUTPUT_SLOT_INDEX)) {
                        if (playerStack.getItem().equals(CommonRegistry.ITEM_CURRENCY.get())) { // IS CURRENCY
                            Slot inputSlot = inventorySlots.get(dragSlots.get(i));

                            // Enough room for whole divisor stack
                            if (canAddItemToSlot(inputSlot, divStack.copy(), false )) {
                                if (inputSlot.getStack().isEmpty()) {
                                    inputSlot.putStack(divStack.copy());
                                } else {
                                    inputSlot.getStack().grow(divCount);
                                }
                                playerStack.shrink(divCount);
                            } else {
                                // Same item, add some of stack
                                if (canAddItemToSlot(inventorySlots.get(dragSlots.get(i)), divStack.copy(), true )) {
                                    int growthAmount = divCount + (inputSlot.getStack().getMaxStackSize() - (inputSlot.getStack().getCount() + divCount));
                                    inputSlot.getStack().grow(growthAmount);
                                    playerStack.shrink(growthAmount);
                                }
                            }
                        }
                    }
                }
            }
            dragSlots = new LinkedList<Integer>();
        }
        return ItemStack.EMPTY;
    }

    @Override // Shift clicking
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        Slot slot = this.inventorySlots.get(index);
        if (vendingStateData.get(VendingStateData.MODE_INDEX) == 1) { // Stock
            if ((index >= HOTBAR_FIRST_SLOT_INDEX && //PLAYER INVENTORY
                    index < PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT)) {
                // Stack has same item and can fit stack being shifted
                for (int i = 0; i < stockContents.getSizeInventory(); i++) {
                    // Has same item and can fit stack size
                    if (!stockContents.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(stockContents.getStackInSlot(i), stockContents.getStackSize(i), stockContents.getStackLimit(i), slot.getStack(), true)) {
                        stockContents.growInventorySlotSize(i, slot.getStack());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;

                        // Has same item and CANNOT fit stack size
                    } else if (!stockContents.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(stockContents.getStackInSlot(i), stockContents.getStackSize(i), stockContents.getStackLimit(i), slot.getStack(), false)) {
                        slot.getStack().setCount(stockContents.growInventorySlotSize(i, slot.getStack()).getCount());
                    }
                }

                // Since no matching items with room place in EMPTY slot if possible
                for (int i = 0; i < stockContents.getSizeInventory(); i++) {
                    if (stockContents.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(stockContents.getStackInSlot(i), stockContents.getStackSize(i), stockContents.getStackLimit(i), slot.getStack(), false)) {
                        stockContents.setStackAndSize(i, slot.getStack().copy());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // Stack has same item and can fit stack being shifted
                for (int i = VANILLA_FIRST_SLOT_INDEX; i < VANILLA_SLOT_COUNT; i++) {
                    // Has same item and can fit stack size
                    if (!getSlot(i).getStack().isEmpty() &&
                            canAddStackToOverloadedStack(getSlot(i).getStack(), getSlot(i).getStack().getCount(), getSlot(i).getStack().getMaxStackSize(), slot.getStack(), true)) {
                        getSlot(i).getStack().grow(slot.getStack().getCount());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;

                        // Has same item and CANNOT fit stack size
                    } else if (!getSlot(i).getStack().isEmpty() &&
                            canAddStackToOverloadedStack(getSlot(i).getStack(), getSlot(i).getStack().getCount(), getSlot(i).getStack().getMaxStackSize(), slot.getStack(), false)) {
                        int amount = slot.getStack().getMaxStackSize() - slot.getStack().getCount();
                        slot.getStack().grow(amount);
                        getSlot(i).getStack().shrink(amount);
                    }
                }

                // Since no matching items with room place in EMPTY slot if possible
                for (int i = VANILLA_FIRST_SLOT_INDEX; i < VANILLA_SLOT_COUNT; i++) {
                    if (getSlot(i).getStack().isEmpty() &&
                            canAddStackToOverloadedStack(getSlot(i).getStack(), getSlot(i).getStack().getCount(), getSlot(i).getStack().getMaxStackSize(), slot.getStack(), false)) {
                        getSlot(i).putStack(slot.getStack().copy());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;
                    }
                }
            }
        } else if (vendingStateData.get(VendingStateData.MODE_INDEX) == 0) { // Sell
            if ((index >= HOTBAR_FIRST_SLOT_INDEX && //PLAYER INVENTORY
                    index < PLAYER_INVENTORY_FIRST_SLOT_INDEX + PLAYER_INVENTORY_SLOT_COUNT)) {
                if (slot.getStack().getItem().equals(CommonRegistry.ITEM_CURRENCY.get())) { // IS CURRENCY
                    // Stack has same item and can fit stack being shifted
                    for (int i = 0; i < inputContents.getSizeInventory(); i++) {
                        // Has same item and can fit stack size
                        if (!inputContents.getStackInSlot(i).isEmpty() &&
                                canAddStackToOverloadedStack(inputContents.getStackInSlot(i), inputContents.getStackInSlot(i).getCount(), inputContents.getStackInSlot(i).getMaxStackSize(), slot.getStack(), true)) {
                            inputContents.getStackInSlot(i).grow(slot.getStack().getCount());
                            slot.decrStackSize(slot.getStack().getCount());
                            return ItemStack.EMPTY;

                            // Has same item and CANNOT fit stack size
                        } else  if (!inputContents.getStackInSlot(i).isEmpty() &&
                                canAddStackToOverloadedStack(inputContents.getStackInSlot(i), inputContents.getStackInSlot(i).getCount(), inputContents.getStackInSlot(i).getMaxStackSize(), slot.getStack(), false)) {
                            slot.getStack().setCount(stockContents.growInventorySlotSize(i, slot.getStack()).getCount());
                        }
                    }

                    // Since no matching items with room place in EMPTY slot if possible
                    for (int i = 0; i < inputContents.getSizeInventory(); i++) {
                        if (inputContents.getStackInSlot(i).isEmpty() &&
                                canAddStackToOverloadedStack(inputContents.getStackInSlot(i), inputContents.getStackInSlot(i).getCount(), inputContents.getStackInSlot(i).getMaxStackSize(), slot.getStack(), false)) {
                            inputContents.setInventorySlotContents(i, slot.getStack().copy());
                            slot.decrStackSize(slot.getStack().getCount());
                            return ItemStack.EMPTY;
                        }
                    }
                }
            } else if (index >= FIRST_OUTPUT_SLOT_INDEX && // OUTPUT INVENTORY
                    index < FIRST_OUTPUT_SLOT_INDEX + OUTPUT_SLOTS_COUNT) {
                // Stack has same item and can fit stack being shifted
                for (int i = 0; i < playerIn.inventory.getSizeInventory(); i++) {
                    // Has same item and can fit stack size
                    if (!playerIn.inventory.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(playerIn.inventory.getStackInSlot(i), playerIn.inventory.getStackInSlot(i).getCount(), playerIn.inventory.getStackInSlot(i).getMaxStackSize(), slot.getStack(), true)) {
                        playerIn.inventory.getStackInSlot(i).grow(slot.getStack().getCount());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;

                        // Has same item and CANNOT fit stack size
                    } else  if (!playerIn.inventory.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(playerIn.inventory.getStackInSlot(i), playerIn.inventory.getStackInSlot(i).getCount(), playerIn.inventory.getStackInSlot(i).getMaxStackSize(), slot.getStack(), false)) {
                        int amount = playerIn.inventory.getStackInSlot(i).getMaxStackSize() - playerIn.inventory.getStackInSlot(i).getCount() ;
                        playerIn.inventory.getStackInSlot(i).grow(amount);
                        slot.getStack().shrink(amount);
                    }
                }

                // Since no matching items with room place in EMPTY slot if possible
                for (int i = 0; i < playerIn.inventory.getSizeInventory(); i++) {
                    if (playerIn.inventory.getStackInSlot(i).isEmpty() &&
                            canAddStackToOverloadedStack(playerIn.inventory.getStackInSlot(i), playerIn.inventory.getStackInSlot(i).getCount(), playerIn.inventory.getStackInSlot(i).getMaxStackSize(), slot.getStack(), false)) {
                        playerIn.inventory.setInventorySlotContents(i, slot.getStack().copy());
                        slot.decrStackSize(slot.getStack().getCount());
                        return ItemStack.EMPTY;
                    }
                }
            } else {
                // SlotID -1...not sure what this is yet
            }
        }
        return ItemStack.EMPTY;
    }

    @Override // Try to merge from given source stack into slot
    public boolean canMergeSlot(ItemStack stack, Slot slotIn) {
        return super.canMergeSlot(stack, slotIn);
    }

    // ---- GETTERS AND SETTERS

    /**
     * Regular Stack -> Overloaded Stack Slot
     */
    public static boolean canAddStackToOverloadedStack(ItemStack slotStack, int slotSize, int slotLimit, ItemStack stack, boolean stackSizeMatters) {
        boolean flag = slotStack == null || slotStack.isEmpty();
        if (!flag && stack.isItemEqual(slotStack) && ItemStack.areItemStackTagsEqual(slotStack, stack)) {
            return slotSize + (!stackSizeMatters ? 0 : stack.getCount()) <= slotLimit;
        } else {
            return flag;
        }
    }

    public VendingContentsOverloaded getStockContents(){
        return stockContents;
    }

    public int getVendingStateData(int index){
        return vendingStateData.get(index);
    }

    public void setVendingStateData(int index, int value){
        vendingStateData.set(index, value);
    }

    public int[] priceFromBuyMode(int index) {
        int stackMax = stockContents.getStackInSlot(index).getMaxStackSize();
        int stackSize = stockContents.getStackSize(index);

        int[] price = stockContents.getPriceInt(index);
        int priceM = price[0];
        int priceT = price[1];
        int priceC = price[2];

        int amount = stackSize;
        switch(getVendingStateData(VendingStateData.BUYMODE_INDEX)) {
            case 0: // ONE
                amount = 1;
                break;
            case 1: // HALF
                int half = (int)Math.ceil(stackMax / 2);
                if (half == 0) half = 1;
                if (amount > half) {
                    amount = half;
                }
                break;
            case 2: // FULL
                if (amount > stackMax) {
                    amount = stackMax;
                }
                break;
        }

        int[] priceInt = CurrencyItem.multiplyPrice(Integer.parseInt(String.valueOf(priceM) + String.valueOf(priceT)), priceC, amount);
        int priceD = priceInt[0];
        priceC = priceInt[1];

        return new int[] {priceD, priceC, amount};
    }

    public String currencyToString(int mode){
        int INDEX_DOLLAR = VendingStateData.CASHDOLLAR_INDEX;
        int INDEX_CENT = VendingStateData.CASHCENT_INDEX;

        if (mode == 1) {
            INDEX_DOLLAR = VendingStateData.INCOMEDOLLAR_INDEX;
            INDEX_CENT = VendingStateData.INCOMECENT_INDEX;
        }

        String dollar = String.valueOf(getVendingStateData(INDEX_DOLLAR));
        String cent = String.valueOf(getVendingStateData(INDEX_CENT));

        if(cent.length() < 2){
            cent = "0" + cent;
        }

        return dollar + "." + cent;
    }

    public void updateModeSlots() {
        if (vendingStateData.get(VendingStateData.MODE_INDEX) == 1) { // SELL (NOT CHANGED YET SO OPPOSITE)
            // Inputs
         //   inventorySlots.get(FIRST_INPUT_SLOT_INDEX).xPos = INPUT_SLOTS_XPOS;
         //   inventorySlots.get(FIRST_INPUT_SLOT_INDEX).yPos = INPUT_SLOTS_YPOS;

            // Outputs
            for (int x = 0; x < OUTPUT_SLOTS_COUNT; x++) { // x is slot num
                inventorySlots.get(FIRST_OUTPUT_SLOT_INDEX + x).xPos = OUTPUT_SLOTS_XPOS + SLOT_X_SPACING * x;
                inventorySlots.get(FIRST_OUTPUT_SLOT_INDEX + x).yPos = OUTPUT_SLOTS_YPOS;
            }
        } else {
            // Inputs
         //   inventorySlots.get(FIRST_INPUT_SLOT_INDEX).xPos = -10000;
         //  inventorySlots.get(FIRST_INPUT_SLOT_INDEX).yPos = -10000;

            // Outputs
            for (int x = 0; x < OUTPUT_SLOTS_COUNT; x++) { // x is slot num
                inventorySlots.get(FIRST_OUTPUT_SLOT_INDEX + x).xPos = -1000;
                inventorySlots.get(FIRST_OUTPUT_SLOT_INDEX + x).yPos = -1000;
            }
        }
    }

    public boolean canAfford(int index) {
        int[] price = priceFromBuyMode(index);
        int priceD = price[0];
        int priceC = price[1];
        int amount = price[2];

        int cashD = vendingStateData.get(VendingStateData.CASHDOLLAR_INDEX);
        int cashC = vendingStateData.get(VendingStateData.CASHCENT_INDEX);

        boolean canAfford = cashC >= priceC;
        canAfford = cashD >= (priceD + (canAfford? 0 : 1)); // If Not enough cents then must check for price Dollar + 1

        boolean enoughStock = stockContents.getSizeInSlot(index) >= amount;

        return canAfford && enoughStock;
    }

    // --- Slot customization ----
    public class StockSlot extends Slot {
        public StockSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }
    }

    public class InputSlot extends Slot {
        public InputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }
    }

    public class OutputSlot extends Slot {
        public OutputSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
            super(inventoryIn, index, xPosition, yPosition);
        }
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
    }
}
