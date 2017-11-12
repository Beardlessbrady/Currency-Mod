package gunn.modcurrency.mod.tileentity;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.container.util.INBTInventory;
import gunn.modcurrency.mod.container.itemhandler.ItemHandlerVendor;
import gunn.modcurrency.mod.handler.StateHandler;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.network.PacketHandler;
import gunn.modcurrency.mod.network.PacketSetLongToClient;
import gunn.modcurrency.mod.network.PacketUpdateAllSizesToClient;
import gunn.modcurrency.mod.network.PacketUpdateSizeToClient;
import gunn.modcurrency.mod.utils.UtilMethods;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-08
 */
public class TileVending extends TileEntity implements ICapabilityProvider, ITickable, INBTInventory, IOwnable{
    private static final int INPUT_SLOT_COUNT = 1;
    public static final int VEND_SLOT_COUNT = 30;
    public static final int BUFFER_SLOT_COUNT = 4;

    private long bank, profit, walletTotal;
    private int selectedSlot, outputBill;
    private String owner, selectedName;
    private boolean locked, mode, creative, infinite, gearExtended, walletIn, twoBlock;
    private int[] itemCosts = new int[VEND_SLOT_COUNT];
    private ItemStackHandler inputStackHandler = new ItemStackHandler(INPUT_SLOT_COUNT);
    private ItemHandlerVendor vendStackHandler = new ItemHandlerVendor(VEND_SLOT_COUNT, this);
    private ItemStackHandler bufferStackHandler = new ItemStackHandler(BUFFER_SLOT_COUNT);
    private EntityPlayer playerUsing = null;
    private int[] slotSizes= new int[VEND_SLOT_COUNT];

    public final byte FIELD_LOCKED = 1;
    public final byte FIELD_MODE = 2;
    public final byte FIELD_SELECTSLOT = 3;
    public final byte FIELD_CREATIVE = 5;
    public final byte FIELD_INFINITE = 6;
    public final byte FIELD_TWOBLOCK = 7;
    public final byte FIELD_GEAREXT = 8;
    public final byte FIELD_WALLETIN = 9;
    public final byte FIELD_OUTPUTBILL = 11;

    public final byte LONG_BANK = 0;
    public final byte LONG_PROFIT = 1;
    public final byte LONG_WALLETTOTAL = 2;

    public TileVending() {
        bank = 0;
        profit = 0;
        selectedSlot = 37;
        walletTotal = 0;
        outputBill = 0;
        owner = "";
        selectedName = "No Item";
        for(int i = 0; i < VEND_SLOT_COUNT; i ++){
            slotSizes[i] = 0;
            itemCosts[i] = 0;
        }

        locked = false;
        mode = false;
        creative = false;
        infinite = false;
        gearExtended = false;
        walletIn = false;
        twoBlock = false;
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.getY(), pos.getZ());
        playerUsing = player;

        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
            PacketUpdateAllSizesToClient pack0 = new PacketUpdateAllSizesToClient();
            pack0.setData(getPos(), slotSizes.clone());
            PacketHandler.INSTANCE.sendTo(pack0, (EntityPlayerMP) getPlayerUsing());

            PacketSetLongToClient pack = new PacketSetLongToClient();
            pack.setData(getPos(), LONG_BANK, bank);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) player);

            PacketSetLongToClient pack1 = new PacketSetLongToClient();
            pack1.setData(getPos(), LONG_PROFIT, profit);
            PacketHandler.INSTANCE.sendTo(pack1, (EntityPlayerMP) player);
        }
    }

    @Override
    public void update() {
      //  if(!world.isRemote && playerUsing != null) System.out.println(checkGhost(0));


        if (!world.isRemote) {
            if (playerUsing != null) {
                if (inputStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {

                    if (inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemBanknote) {
                        //<editor-fold desc="Banknote Update">
                        int amount;
                        switch (inputStackHandler.getStackInSlot(0).getItemDamage()) {
                            case 0:
                                amount = 100;
                                break;
                            case 1:
                                amount = 500;
                                break;
                            case 2:
                                amount = 1000;
                                break;
                            case 3:
                                amount = 2000;
                                break;
                            case 4:
                                amount = 5000;
                                break;
                            case 5:
                                amount = 10000;
                                break;
                            default:
                                amount = -1;
                                break;
                        }
                        amount = amount * inputStackHandler.getStackInSlot(0).getCount();
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        bank = bank + amount;

                        if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), LONG_BANK, bank);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        markDirty();
                        //</editor-fold>
                    } else if(inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemCoin){
                        long amount;
                        switch(inputStackHandler.getStackInSlot(0).getItemDamage()) {
                            case 0:
                                amount = 1;
                                break;
                            case 1:
                                amount = 5;
                                break;
                            case 2:
                                amount = 10;
                                break;
                            case 3:
                                amount = 25;
                                break;
                            case 4:
                                amount = 100;
                                break;
                            case 5:
                                amount = 200;
                                break;
                            default:
                                amount = -1;
                                break;
                        }
                        amount = amount * inputStackHandler.getStackInSlot(0).getCount();
                        inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        bank = bank + amount;
                        if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), LONG_BANK, bank);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        markDirty();
                    } else if (inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemWallet) {

                        //<editor-fold desc="Wallet Up">
                        walletTotal = getTotalCash();
                        if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), LONG_WALLETTOTAL, walletTotal);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        //</editor-fold>

                    }
                }

            }
            //<editor-fold desc="Dealing with Buffer Slots">
            if (locked) {
                int outputAmnt = getCashConversion(outputBill);
                if (profit >= outputAmnt) {
                    if (outputBill >= 0 && outputBill <= 5) {   //OUTPUT COIN
                        outLoop:
                        for (int i = 0; i < bufferStackHandler.getSlots(); i++) {
                            if (bufferStackHandler.getStackInSlot(i).isEmpty()) {
                                //Insert new stack

                                bufferStackHandler.setStackInSlot(i, new ItemStack(ModItems.itemCoin, 1, outputBill));
                                profit = profit - outputAmnt;
                                if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                    PacketSetLongToClient pack = new PacketSetLongToClient();
                                    pack.setData(getPos(), LONG_PROFIT, profit);
                                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                                }
                                break outLoop;
                            } else if (UtilMethods.equalStacks(bufferStackHandler.getStackInSlot(i), new ItemStack(ModItems.itemCoin, 1, outputBill)) && bufferStackHandler.getStackInSlot(i).getCount() < bufferStackHandler.getStackInSlot(i).getMaxStackSize()) {
                                //Grow Stack
                                bufferStackHandler.getStackInSlot(i).grow(1);
                                profit = profit - outputAmnt;
                                if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                    PacketSetLongToClient pack = new PacketSetLongToClient();
                                    pack.setData(getPos(), LONG_PROFIT, profit);
                                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                                }
                                break outLoop;
                            }
                        }
                    } else {        //OUTPUT DOLLAR BILL
                        outLoop:
                        for (int i = 0; i < bufferStackHandler.getSlots(); i++) {
                            if (bufferStackHandler.getStackInSlot(i).isEmpty()) {
                                //Insert new stack

                                bufferStackHandler.setStackInSlot(i, new ItemStack(ModItems.itemBanknote, 1, outputBill - 5));
                                profit = profit - outputAmnt;
                                if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                    PacketSetLongToClient pack = new PacketSetLongToClient();
                                    pack.setData(getPos(), LONG_PROFIT, profit);
                                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                                }
                                break outLoop;
                            } else if (UtilMethods.equalStacks(bufferStackHandler.getStackInSlot(i), new ItemStack(ModItems.itemBanknote, 1, outputBill - 5)) && bufferStackHandler.getStackInSlot(i).getCount() < bufferStackHandler.getStackInSlot(i).getMaxStackSize()) {
                                //Grow Stack
                                bufferStackHandler.getStackInSlot(i).grow(1);
                                profit = profit - outputAmnt;
                                if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                    PacketSetLongToClient pack = new PacketSetLongToClient();
                                    pack.setData(getPos(), LONG_PROFIT, profit);
                                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                                }
                                break outLoop;
                            }
                        }
                    }
                }
            }
        }
        //</editor-fold>
        walletIn = (inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemWallet);
    }

    //Drop Items
    public void dropItems() {
        for (int i = 0; i < vendStackHandler.getSlots(); i++) {
            ItemStack item = vendStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                vendStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
        for (int i = 0; i < bufferStackHandler.getSlots(); i++){
            ItemStack item = bufferStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                vendStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
    }

    //Drop Items
    public void dropTopItems() {
        for (int i = 15; i < vendStackHandler.getSlots(); i++) {
            ItemStack item = vendStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                vendStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
    }

    public boolean canInteractWith(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    public void unsucessfulNoise(){
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 10.0F, false);
    }

    //<editor-fold desc="Money Methods-------------------------------------------------------------------------------------------------------">
    public int getCashConversion(int meta){
        switch(meta){
            case 0: return 1;
            case 1: return 5;
            case 2: return 10;
            case 3: return 25;
            case 4: return 100;
            case 5: return 200;
            case 6: return 500;
            case 7: return 1000;
            case 8: return 2000;
            case 9: return 5000;
            case 10: return 10000;
        }
        return -1;
    }

    private int getTotalCash(){
        ItemStack item = inputStackHandler.getStackInSlot(0);
        if(item.hasTagCompound()) {
            ItemStackHandler itemStackHandler = readInventoryTag(inputStackHandler.getStackInSlot(0), ItemWallet.WALLET_TOTAL_COUNT);

            int totalCash = 0;
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (itemStackHandler.getStackInSlot(i).getItem().equals(ModItems.itemCoin)) {
                    switch (itemStackHandler.getStackInSlot(i).getItemDamage()) {
                        case 0:
                            totalCash = totalCash + itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 1:
                            totalCash = totalCash + 5 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 2:
                            totalCash = totalCash + 10 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 3:
                            totalCash = totalCash + 25 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 4:
                            totalCash = totalCash + 100 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 5:
                            totalCash = totalCash + 200 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        default:
                            totalCash = -1;
                            break;
                    }
                }else  if (itemStackHandler.getStackInSlot(i).getItem().equals(ModItems.itemBanknote)) {
                    switch (itemStackHandler.getStackInSlot(i).getItemDamage()) {
                        case 0:
                            totalCash = totalCash + 100 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 1:
                            totalCash = totalCash + 500 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 2:
                            totalCash = totalCash + 1000 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 3:
                            totalCash = totalCash + 2000 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 4:
                            totalCash = totalCash + 5000 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 5:
                            totalCash = totalCash + 10000 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        default:
                            totalCash = -1;
                            break;
                    }
                }
            }
            return totalCash;
        }
        return 0;
    }

    public boolean canAfford(int slot){
        if(walletIn){
            return itemCosts[slot] >= getTotalCash();
        }
        return bank >= itemCosts[slot];
    }

    public void outChange() {
        long amount = bank;
        if (mode) amount = profit;

        int[] dollarOut = new int[6];
        dollarOut[5] = Math.round(amount / 10000);
        amount = amount - (dollarOut[5] * 10000);

        dollarOut[4] = Math.round(amount / 5000);
        amount = amount - (dollarOut[4] * 5000);

        dollarOut[3] = Math.round(amount / 2000);
        amount = amount - (dollarOut[3] * 2000);

        dollarOut[2] = Math.round(amount / 1000);
        amount = amount - (dollarOut[2] * 1000);

        dollarOut[1] = Math.round(amount / 500);
        amount = amount - (dollarOut[1] * 500);

        dollarOut[0] = 0;

        int[] coinOut = new int[6];
        coinOut[5] = Math.round(amount / 200);
        amount = amount - (coinOut[5] * 200);

        coinOut[4] = Math.round(amount / 100);
        amount = amount - (coinOut[4] * 100);

        coinOut[3] = Math.round(amount / 25);
        amount = amount - (coinOut[3] * 25);

        coinOut[2] = Math.round(amount / 10);
        amount = amount - (coinOut[2] * 10);

        coinOut[1] = Math.round(amount / 5);
        amount = amount - (coinOut[1] * 5);

        coinOut[0] = Math.round(amount);

        if (!world.isRemote) {
            if (mode){
                profit = 0;
            }else bank = 0;

            for(int i = 0; i < dollarOut.length + coinOut.length; i++){
                ItemStack item;

                if(i < dollarOut.length){
                    item = new ItemStack(ModItems.itemBanknote);
                    item.setItemDamage(i);
                    item.setCount(dollarOut[i]);
                }else{
                    item = new ItemStack(ModItems.itemCoin);
                    item.setItemDamage(i - dollarOut.length);
                    item.setCount(coinOut[i - dollarOut.length]);
                }

                boolean check;
                if(i < dollarOut.length){
                    check = dollarOut[i] != 0;
                }else{
                    check = coinOut[i - dollarOut.length] != 0;
                }

                if(check){
                    boolean playerInGui= false;
                    if (playerUsing != null) playerInGui = true;

                    if (playerInGui) {
                        InventoryPlayer inventoryPlayer = playerUsing.inventory;
                        boolean placed = false;

                        //Looks for item in inventory before putting in a empty slot
                        searchLoop:
                        for (int j = 0; j < 36; j++) {
                            if (UtilMethods.equalStacks(item, inventoryPlayer.getStackInSlot(j))) {
                                if (inventoryPlayer.getStackInSlot(j).getCount() + item.getCount() <= inventoryPlayer.getStackInSlot(j).getMaxStackSize()) {
                                    inventoryPlayer.getStackInSlot(j).setCount(inventoryPlayer.getStackInSlot(j).getCount() + item.getCount());
                                    placed = true;
                                    break searchLoop;
                                }
                            }
                        }

                        if (!placed) {
                            if (inventoryPlayer.getFirstEmptyStack() != -1) {     //If Players Inventory has room
                                //Todo include a warning symbol that tells user they have no room in their inventory
                                //Todo if player has wallet try to place in WALLET first before inventory
                                inventoryPlayer.setInventorySlotContents(inventoryPlayer.getFirstEmptyStack(), item);
                            } else {
                                playerInGui = false;
                            }
                        }
                    }

                    if (!playerInGui) {       //If no room, spawn
                        int x = getPos().getX();
                        int z = getPos().getZ();

                        switch (this.getBlockMetadata()) {
                            case 0:
                                z = z + 1; //North
                                break;
                            case 1:
                                x = x - 1; //East
                                break;
                            case 2:
                                z = z - 2; //South
                                break;
                            case 3:
                                x = x + 1;//West
                                break;
                        }
                        world.spawnEntity(new EntityItem(world, x, getPos().up().getY(), z, item));
                    }
                }
            }

            if (mode) {
                if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
                    PacketSetLongToClient pack = new PacketSetLongToClient();
                    pack.setData(getPos(), LONG_PROFIT, profit);
                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                }
            } else {
                if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
                    PacketSetLongToClient pack = new PacketSetLongToClient();
                    pack.setData(getPos(), LONG_BANK, bank);
                    PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                }
            }
        }
    }

    public void outInputSlot(){
        if (inputStackHandler.getStackInSlot(0).getItem() != Item.getItemFromBlock(Blocks.AIR)) {
            if (!world.isRemote) {
                ItemStack item = inputStackHandler.getStackInSlot(0);
                inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);

                int x = getPos().getX();
                int z = getPos().getZ();
                switch (this.getBlockMetadata()) {
                    case 0:
                        z = z + 1; //North
                        break;
                    case 1:
                        x = x - 1; //East
                        break;
                    case 2:
                        z = z - 2; //South
                        break;
                    case 3:
                        x = x + 1;//West
                        break;
                }

                world.spawnEntity(new EntityItem(world, x, getPos().up().getY(), z, item));
            }
        }
    }
    //</editor-fold>

    //<editor-fold desc="NBT & Packet Stoof--------------------------------------------------------------------------------------------------">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("item", vendStackHandler.serializeNBT());
        compound.setTag("buffer", bufferStackHandler.serializeNBT());
        compound.setTag("input", inputStackHandler.serializeNBT());
        compound.setLong("bank", bank);
        compound.setLong("profit", profit);
        compound.setLong("walletTotal", walletTotal);
        compound.setInteger("output", outputBill);
        compound.setBoolean("locked", locked);
        compound.setBoolean("mode", mode);
        compound.setBoolean("creative", creative);
        compound.setBoolean("infinite", infinite);
        compound.setBoolean("gearExtended", gearExtended);
        compound.setBoolean("walletIn", walletIn);
        compound.setBoolean("twoBlock", twoBlock);
        compound.setInteger("selectedSlot", selectedSlot);
        compound.setString("selectedName", selectedName);
        compound.setString("owner", owner);

        NBTTagCompound itemCostsNBT = new NBTTagCompound();
        NBTTagCompound itemSizesNBT = new NBTTagCompound();

        for (int i = 0; i < VEND_SLOT_COUNT; i++){
            itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
            itemSizesNBT.setInteger("size" + i, slotSizes[i]);
        }
        compound.setTag("itemCosts", itemCostsNBT);
        compound.setTag("itemSizes", itemSizesNBT);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("item")) vendStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("item"));
        if (compound.hasKey("buffer")) bufferStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("buffer"));
        if (compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if (compound.hasKey("bank")) bank = compound.getLong("bank");
        if (compound.hasKey("profit")) profit = compound.getLong("profit");
        if (compound.hasKey("walletTotal")) walletTotal = compound.getLong("walletTotal");
        if (compound.hasKey("output")) outputBill = compound.getInteger("output");
        if (compound.hasKey("locked")) locked = compound.getBoolean("locked");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if (compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if (compound.hasKey("infinite")) infinite = compound.getBoolean("infinite");
        if (compound.hasKey("gearExtended")) gearExtended = compound.getBoolean("gearExtended");
        if (compound.hasKey("walletIn")) walletIn = compound.getBoolean("walletIn");
        if (compound.hasKey("twoBlock")) twoBlock = compound.getBoolean("twoBlock");
        if (compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
        if (compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if (compound.hasKey("owner")) owner = compound.getString("owner");

        if (compound.hasKey("itemCosts")) {
            NBTTagCompound itemCostsNBT = compound.getCompoundTag("itemCosts");
            for (int i = 0; i < VEND_SLOT_COUNT; i++)itemCosts[i] = itemCostsNBT.getInteger("cost" + i);
        }

        if (compound.hasKey("itemSizes")) {
            NBTTagCompound itemSizeNBT = compound.getCompoundTag("itemSizes");
            for (int i = 0; i < itemCosts.length; i++) slotSizes[i] = itemSizeNBT.getInteger("size" + i);
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("bank", bank);
        tag.setLong("profit", profit);
        tag.setLong("walletTotal", walletTotal);
        tag.setInteger("output", outputBill);
        tag.setBoolean("locked", locked);
        tag.setBoolean("mode", mode);
        tag.setBoolean("creative", creative);
        tag.setBoolean("infinite", infinite);
        tag.setBoolean("gearExtended", gearExtended);
        tag.setBoolean("walletIn", walletIn);
        tag.setBoolean("twoBlock", twoBlock);
        tag.setInteger("selectedSlot", selectedSlot);
        tag.setString("selectedName", selectedName);
        tag.setString("owner", owner);

        NBTTagCompound itemCostsNBT = new NBTTagCompound();
        NBTTagCompound itemSizeNBT = new NBTTagCompound();
        for (int i = 0; i < VEND_SLOT_COUNT; i++){
            itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
            itemSizeNBT.setInteger("size" + i, slotSizes[i]);
        }
        tag.setTag("itemCosts", itemCostsNBT);
        tag.setTag("itemSizes", itemSizeNBT);

        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        bank = pkt.getNbtCompound().getLong("bank");
        profit = pkt.getNbtCompound().getLong("profit");
        outputBill = pkt.getNbtCompound().getInteger("output");
        walletTotal = pkt.getNbtCompound().getLong("walletTotal");
        locked = pkt.getNbtCompound().getBoolean("locked");
        mode = pkt.getNbtCompound().getBoolean("mode");
        creative = pkt.getNbtCompound().getBoolean("creative");
        infinite = pkt.getNbtCompound().getBoolean("infinite");
        gearExtended = pkt.getNbtCompound().getBoolean("gearExtended");
        walletIn = pkt.getNbtCompound().getBoolean("walletIn");
        twoBlock = pkt.getNbtCompound().getBoolean("twoBlock");
        selectedSlot = pkt.getNbtCompound().getInteger("selectedSlot");
        selectedName = pkt.getNbtCompound().getString("selectedName");
        owner = pkt.getNbtCompound().getString("owner");

        NBTTagCompound itemCostsNBT = pkt.getNbtCompound().getCompoundTag("itemCosts");
        NBTTagCompound itemSizeNBT = pkt.getNbtCompound().getCompoundTag("itemSizes");
        for (int i = 0; i < VEND_SLOT_COUNT; i++){
            itemCosts[i] = itemCostsNBT.getInteger("cost" + i);
            slotSizes[i] = itemSizeNBT.getInteger("size" + i);
        }
    }
    //</editor-fold>--------------------------------

    //<editor-fold desc="ItemStackHandler Methods--------------------------------------------------------------------------------------------">
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == null || locked;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return (T) new CombinedInvWrapper(inputStackHandler, vendStackHandler, bufferStackHandler); //Inside Itself
            if (facing == EnumFacing.DOWN) return (T) bufferStackHandler;
            if (facing != EnumFacing.DOWN) return (T) vendStackHandler;
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    //<editor-fold desc="Getter & Setter Methods---------------------------------------------------------------------------------------------">
    public int getFieldCount() {
        return 9;
    }

    public void setField(int id, int value) {
        switch (id) {
            case FIELD_LOCKED:
                locked = (value == 1);
                break;
            case FIELD_MODE:
                mode = (value == 1);
                break;
            case FIELD_SELECTSLOT:
                selectedSlot = value;
                break;
            case FIELD_CREATIVE:
                creative = (value == 1);
                break;
            case FIELD_INFINITE:
                infinite = (value == 1);
                break;
            case FIELD_TWOBLOCK:
                twoBlock = (value == 1);
                break;
            case FIELD_GEAREXT:
                gearExtended = (value == 1);
                break;
            case FIELD_WALLETIN:
                walletIn = (value == 1);
                break;
            case FIELD_OUTPUTBILL:
                outputBill = value;
        }
    }

    public int getField(int id) {
        switch (id) {
            case FIELD_LOCKED:
                return (locked) ? 1 : 0;
            case FIELD_MODE:
                return (mode) ? 1 : 0;
            case FIELD_SELECTSLOT:
                return selectedSlot;
            case FIELD_CREATIVE:
                return (creative) ? 1 : 0;
            case FIELD_INFINITE:
                return (infinite) ? 1 : 0;
            case FIELD_TWOBLOCK:
                return (twoBlock) ? 1 : 0;
            case FIELD_GEAREXT:
                return (gearExtended) ? 1 : 0;
            case FIELD_WALLETIN:
                return (walletIn) ? 1 : 0;
            case FIELD_OUTPUTBILL:
                return outputBill;
        }
        return -1;
    }

    public void setLong(byte id, long value){
        switch(id){
            case LONG_BANK:
                bank = value;
                break;
            case LONG_PROFIT:
                profit = value;
                break;
            case LONG_WALLETTOTAL:
                walletTotal = value;
                break;
        }
        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
            PacketSetLongToClient pack = new PacketSetLongToClient();
            pack.setData(getPos(), id, value);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
        }
    }

    public long getLong(byte id){
        switch(id){
            case LONG_BANK:
                return bank;
            case LONG_PROFIT:
                return profit;
            case LONG_WALLETTOTAL:
                return walletTotal;
        }
        return -1;
    }

    public String getSelectedName() {
        return selectedName;
    }

    public void setSelectedName(String name) {
        selectedName = name;
    }

    public int getItemCost(int index) {
        return itemCosts[index];
    }

    public void setItemCost(int amount) {
        itemCosts[selectedSlot - 37] = amount;
    }

    public void setItemCost(int amount, int index) {
        itemCosts[index] = amount;
    }

    public int getItemSize(int index){
        return slotSizes[index];
    }

    public void setItemSize(int amount, int index){
        slotSizes[index] = amount;
    }

    public void growItemSize(int amount, int index) {
        slotSizes[index] += amount;
    }

    public void shrinkItemSize(int amount, int index){
        slotSizes[index] -= amount;
    }

    public ItemStackHandler getBufferStackHandler(){
        return bufferStackHandler;
    }

    public ItemStackHandler getInputStackHandler(){
        return inputStackHandler;
    }

    public ItemHandlerVendor getVendStackHandler(){
        return vendStackHandler;
    }

    public void setBufferStackHandler(ItemStackHandler buf){
        bufferStackHandler = buf;
    }

    public void setInputStackHandler(ItemStackHandler input){
        inputStackHandler = input;
    }

    public void setVendStackHandler(ItemHandlerVendor vend){
        vendStackHandler = vend;
    }

    @Override
    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public EntityPlayer getPlayerUsing(){
        return playerUsing;
    }

    public void voidPlayerUsing(){
        playerUsing = null;
    }
    //</editor-fold>
}
