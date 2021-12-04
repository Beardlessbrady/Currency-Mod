package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.init.ClientRegistry;
import com.beardlessbrady.gocurrency.init.CommonRegistry;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.java.games.input.Keyboard;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputEvent;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.UUID;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingTile extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    private static final String CONTENTS_INVENTORY_TAG = "contents";

    public static final int STOCK_ROW_COUNT = 4;
    public static final int STOCK_COLUMN_COUNT = 4;
    public static final int STOCK_SLOT_COUNT = STOCK_ROW_COUNT * STOCK_COLUMN_COUNT;
    public static final int INPUT_SLOTS_COUNT = 1;
    public static final int OUTPUT_SLOTS_COUNT = 3;
    public static final int TOTAL_SLOTS_COUNT = STOCK_SLOT_COUNT + INPUT_SLOTS_COUNT + OUTPUT_SLOTS_COUNT;

    public static final int MAX_DOLLAR = 2147000000;

    private final VendingContentsOverloaded stockContents;
    private final VendingContents inputContents;
    private final VendingContents outputContents;
    private VendingContainer container;

    private final VendingStateData vendingStateData = new VendingStateData();
    private UUID owner;
    private UUID playerUsing = new UUID(0L, 0L);
    public static UUID EMPTYID = new UUID(0L, 0L);

    public VendingTile() {
        super(CommonRegistry.TILE_VENDING.get());
        stockContents = new VendingContentsOverloaded(STOCK_SLOT_COUNT, this::canPlayerUse, this::markDirty);
        inputContents = new VendingContents(INPUT_SLOTS_COUNT, this::canPlayerUse, this::markDirty);
        outputContents = new VendingContents(OUTPUT_SLOTS_COUNT, this::canPlayerUse, this::markDirty);
    }

    // ----- VANILLA -----
    @Override
    public void tick() {
        if (!world.isRemote) { // Server
            if (isPlayerUsing()) {
                if (vendingStateData.get(VendingStateData.MODE_INDEX) == 0) {
                    if (!inputContents.getStackInSlot(0).isEmpty()) {
                        long maxTest = (long)CurrencyItem.getCurrencyValue(inputContents.getStackInSlot(0))[0] + (long)vendingStateData.get(VendingStateData.CASHDOLLAR_INDEX);
                        boolean OVERMAXINT = maxTest >= Integer.MAX_VALUE;

                        if(!OVERMAXINT) {
                            addCurrency(CurrencyItem.getCurrencyValue(inputContents.getStackInSlot(0)), container.getVendingStateData(VendingStateData.MODE_INDEX));
                            inputContents.getStackInSlot(0).setCount(0);
                        }
                    }
                }
            }
        }
    }

    public boolean isPlayerUsing() {
        PlayerEntity player = world.getPlayerByUuid(playerUsing);

     boolean stillOpen = false;
        if (player != null) {
           stillOpen = player.openContainer != player.container;
        };
        return !playerUsing.equals(EMPTYID) && stillOpen;
    }

    public void setPlayerUsing(UUID uuid) {
        playerUsing = uuid;
    }

    public void voidPlayerUsing() {
        playerUsing = EMPTYID;
    }

    public boolean isOwner() {
        return playerUsing.equals(owner);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID uuid) {
        owner = uuid;
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.gocurrency.vending");
    }

    public boolean canPlayerUse(PlayerEntity player) {
        if (this.world.getTileEntity(this.pos) != this) return false;
        final double X_CENTRE_OFFSET = 0.5;
        final double Y_CENTRE_OFFSET = 0.5;
        final double Z_CENTRE_OFFSET = 0.5;
        final double MAXIMUM_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_CENTRE_OFFSET, pos.getY() + Y_CENTRE_OFFSET, pos.getZ() + Z_CENTRE_OFFSET) < MAXIMUM_DISTANCE_SQ;
    }

    public void dropStockContents() {
        for (int i = 0; i < stockContents.getSizeInventory(); i++) {
            while (stockContents.getStackSize(i) != 0) {
                ItemStack dropStack = stockContents.getStackInSlot(i);
                int dropSize = stockContents.getSizeInSlot(i);

                // If stack size larger than regular stack limit
                if (dropSize > dropStack.getMaxStackSize())
                    dropSize = dropStack.getMaxStackSize();

                dropStack.setCount(dropSize);
                stockContents.decrStackSize(i, dropSize);

                InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), dropStack);
            }
        }
    }

    public void dropAllContents(World world, BlockPos blockPos) {
        dropStockContents();
        InventoryHelper.dropInventoryItems(world, blockPos, inputContents);
        InventoryHelper.dropInventoryItems(world, blockPos, outputContents);

        // Get Cash Items
        NonNullList<ItemStack> cashList = NonNullList.create();
        for(ItemStack item: extractCurrency(0)) {
            if(!item.isEmpty()) {
                cashList.add(item);
            }
        }

        // Get Income Items
        NonNullList<ItemStack> incomeList = NonNullList.create();
        for(ItemStack item: extractCurrency(1)) {
            if(!item.isEmpty()) {
                incomeList.add(item);
            }
        }

        InventoryHelper.dropItems(world, blockPos, cashList);
        InventoryHelper.dropItems(world, blockPos, incomeList);
    }

    @Nullable
    @Override
    // Server side creation of Container
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        this.container = VendingContainer.createContainerServer(windowID, playerInventory, stockContents, inputContents, outputContents, vendingStateData, this);
        return this.container;
    }

    public int getVendingStateData(int index) {
        return vendingStateData.get(index);
    }

    public int[] getVendingStateDataAsArray() {
        int[] array = new int[vendingStateData.size()];

        for (int i = 0; i < vendingStateData.size(); i++) {
            array[i] = vendingStateData.get(i);
        }

        return array;
    }

    public void setVendingStateData(int index, int value) {
        this.vendingStateData.set(index, value);

        if(index == VendingStateData.MODE_INDEX){
            container.updateModeSlots();
        }
    }

    public void setStockPrice(int index, int[] price) {
        stockContents.setPrice(index, price);
    }

    // ---- CURRENCY ------

    /**
     * Goes through currency and outputs a list of currency item stacks
     * @param mode used to determine which currency is being extracted (0=cash, 1=income)
     * @return list of item stacks for currency
     */
    public ItemStack[] extractCurrency(int mode) {
        // Get Currency List and order from largest to smallest dollar/Cent value
        CurrencyItem.CurrencyObject[] currencyList = GOCurrency.currencyList.clone();
        Arrays.sort(currencyList, Comparator.reverseOrder());

        // Drop Cash/Income depending on Mode
        byte DOLLAR_INDEX = VendingStateData.CASHDOLLAR_INDEX;
        byte CENT_INDEX = VendingStateData.CASHCENT_INDEX;
        if(mode == 1){
            DOLLAR_INDEX = VendingStateData.INCOMEDOLLAR_INDEX;
            CENT_INDEX = VendingStateData.INCOMECENT_INDEX;
        }
        int dollar = getVendingStateData(DOLLAR_INDEX);
        int cent = getVendingStateData(CENT_INDEX);

        setVendingStateData(DOLLAR_INDEX, 0);
        setVendingStateData(CENT_INDEX, 0);

        // List of Currency Itemstacks to drop
        LinkedList<ItemStack> currencyDrop = new LinkedList<ItemStack>();

        // Iterate through currency list and see how many of each currency is needed for total currency
        for (int i = 0; i < currencyList.length; i++) {
            int count = 0;
            int currencyIndex = 0;
            while (dollar >= currencyList[i].getDollar() && cent >= currencyList[i].getCent()) {
                dollar -= currencyList[i].getDollar();
                cent -= currencyList[i].getCent();

                count++;
                currencyIndex = i;

                if (count == new ItemStack(CommonRegistry.ITEM_CURRENCY.get()).getMaxStackSize()) {
                    i--;
                    break;
                }
            }

            vendingStateData.set(DOLLAR_INDEX, dollar);
            vendingStateData.set(CENT_INDEX, cent);

            ItemStack stack = new ItemStack(CommonRegistry.ITEM_CURRENCY.get(), count);
            CurrencyItem.putIntoNBT(stack, currencyList[currencyIndex]);
            currencyDrop.add(stack);
        }

        return currencyDrop.toArray(new ItemStack[currencyDrop.size()]);
    }

    /**
     * Extract Currency and output into OUTPUT slots
     * @param mode determines which currency is output (0=cash, 1=income)
     */
    public void cashButton(int mode) {
        ItemStack[] currency = extractCurrency(mode);

        if (vendingStateData.get(VendingStateData.MODE_INDEX) == 0) { // SELL
            // Fill Output Slots
            for (int i = 0; i < currency.length; i++) {
                for (int j = 0; j < outputContents.getSizeInventory(); j++) {
                    if (outputContents.getStackInSlot(j).isEmpty()) {
                        ItemStack outStack = currency[i];
                        currency[i] = ItemStack.EMPTY;

                        outputContents.setInventorySlotContents(j, outStack);
                    }
                }
            }
        } else { // STOCK
            // Fill Input Slots
            for (int i = 0; i < currency.length; i++) {
                for (int j = 0; j < inputContents.getSizeInventory(); j++) {
                    if (inputContents.getStackInSlot(j).isEmpty()) {
                        ItemStack outStack = currency[i];
                        currency[i] = ItemStack.EMPTY;

                        inputContents.setInventorySlotContents(j, outStack);
                    }
                }
            }
        }

        // Check if money left over, if so put back into machine
        byte DOLLAR_INDEX = VendingStateData.CASHDOLLAR_INDEX;
        byte CENT_INDEX = VendingStateData.CASHCENT_INDEX;

        if(mode == 1){
            DOLLAR_INDEX = VendingStateData.INCOMEDOLLAR_INDEX;
            CENT_INDEX = VendingStateData.INCOMECENT_INDEX;
        }

        int dollar = container.getVendingStateData(DOLLAR_INDEX);
        int cent = container.getVendingStateData(CENT_INDEX);

        for (ItemStack leftover: currency) {
            if (!leftover.isEmpty()) {
                int[] leftoverValue = CurrencyItem.getCurrencyValue(leftover);
                dollar += leftoverValue[0];
                cent += leftoverValue[1];
            }
        }

        container.setVendingStateData(DOLLAR_INDEX, dollar);
        container.setVendingStateData(CENT_INDEX, cent);
    }

    /**
     * Input {DOLLAR, CENT} and add those to the mode's currency (0=cash, 1=income)
     * @param currency integer amounts {DOLLAR, CENT}
     * @param mode determines which currency to add to
     */
    public void addCurrency(int[] currency, int mode){
        byte DOLLAR_INDEX = VendingStateData.CASHDOLLAR_INDEX;
        byte CENT_INDEX = VendingStateData.CASHCENT_INDEX;

        if(mode == 1){
            DOLLAR_INDEX = VendingStateData.INCOMEDOLLAR_INDEX;
            CENT_INDEX = VendingStateData.INCOMECENT_INDEX;
        }

        int dollar = container.getVendingStateData(DOLLAR_INDEX);
        int cent = container.getVendingStateData(CENT_INDEX);

        dollar += currency[0];
        cent += currency[1];

        int[] dollarFromCents = CurrencyItem.roundCents(cent);
        dollar += dollarFromCents[0];
        cent = dollarFromCents[1];

        container.setVendingStateData(DOLLAR_INDEX, dollar);
        container.setVendingStateData(CENT_INDEX, cent);
    }

    // ---- NBT Stuff ----
    private final String STOCK_SLOTS_NBT = "stockSlots";
    private final String INPUT_SLOTS_NBT = "inputSlots";
    private final String OUTPUT_SLOTS_NBT = "outputSlots";

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.put(STOCK_SLOTS_NBT, stockContents.serializeNBT());
        compound.put(INPUT_SLOTS_NBT, inputContents.serializeNBT());
        compound.put(OUTPUT_SLOTS_NBT, outputContents.serializeNBT());


        compound.putUniqueId("owner", owner);
        compound.putUniqueId("player", playerUsing);

        vendingStateData.putIntoNBT(compound);
        return compound;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        super.read(state, nbt);
        CompoundNBT stockNBT = nbt.getCompound(STOCK_SLOTS_NBT);
        stockContents.deserializeNBT(stockNBT);

        CompoundNBT inputNBT = nbt.getCompound(INPUT_SLOTS_NBT);
        inputContents.deserializeNBT(inputNBT);

        CompoundNBT outputNBT = nbt.getCompound(OUTPUT_SLOTS_NBT);
        outputContents.deserializeNBT(outputNBT);

        vendingStateData.readFromNBT(nbt);

        owner = nbt.getUniqueId("owner");
        playerUsing = nbt.getUniqueId("player");

        if (stockContents.getSizeInventory() != STOCK_SLOT_COUNT || inputContents.getSizeInventory() != INPUT_SLOTS_COUNT
                || outputContents.getSizeInventory() != OUTPUT_SLOTS_COUNT)
            throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT updateTag = getUpdateTag();
        return new SUpdateTileEntityPacket(this.pos, 42, updateTag); //Type in # is arbitrary
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT updateTag = pkt.getNbtCompound();
        BlockState blockState = world.getBlockState(pos);
        handleUpdateTag(blockState, updateTag);
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT compoundNBT = new CompoundNBT();
        write(compoundNBT);
        return compoundNBT;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        read(state, tag);
    }
}
