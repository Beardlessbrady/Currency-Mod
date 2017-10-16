package gunn.modcurrency.mod.tileentity;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.client.gui.util.INBTInventory;
import gunn.modcurrency.mod.container.itemhandler.ItemHandlerVendor;
import gunn.modcurrency.mod.handler.StateHandler;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.network.PacketHandler;
import gunn.modcurrency.mod.network.PacketSetLongToClient;
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

    private double bank, profit, walletTotal;
    private int selectedSlot, outputBill;
    private String owner, selectedName;
    private boolean locked, mode, creative, infinite, gearExtended, walletIn, twoBlock;
    private int[] itemCosts = new int[VEND_SLOT_COUNT];
    private ItemStackHandler inputStackHandler = new ItemStackHandler(INPUT_SLOT_COUNT);
    private ItemHandlerVendor vendStackHandler = new ItemHandlerVendor(VEND_SLOT_COUNT);
    private ItemStackHandler bufferStackHandler = new ItemStackHandler(BUFFER_SLOT_COUNT);
    private EntityPlayer playerUsing = null;

    public final byte FIELD_LOCKED = 1;
    public final byte FIELD_MODE = 2;
    public final byte FIELD_SELECTSLOT = 3;
    public final byte FIELD_CREATIVE = 5;
    public final byte FIELD_INFINITE = 6;
    public final byte FIELD_TWOBLOCK = 7;
    public final byte FIELD_GEAREXT = 8;
    public final byte FIELD_WALLETIN = 9;
    public final byte FIELD_OUTPUTBILL = 11;

    public final byte DOUBLE_BANK = 0;
    public final byte DOUBLE_PROFIT = 1;
    public final byte DOUBLE_WALLETTOTAL = 2;

    public TileVending() {
        bank = 0;
        profit = 0;
        selectedSlot = 37;
        walletTotal = 0;
        outputBill = 0;
        owner = "";
        selectedName = "No Item";
        locked = false;
        mode = false;
        creative = false;
        infinite = false;
        gearExtended = false;
        walletIn = false;
        twoBlock = false;

        for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = 0;
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else player.openGui(ModCurrency.instance, 30, world, pos.getX(), pos.getY(), pos.getZ());
        playerUsing = player;

        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
            PacketSetLongToClient pack = new PacketSetLongToClient();
            pack.setData(getPos(), DOUBLE_BANK, bank);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) player);

            PacketSetLongToClient pack1 = new PacketSetLongToClient();
            pack1.setData(getPos(), DOUBLE_PROFIT, profit);
            PacketHandler.INSTANCE.sendTo(pack1, (EntityPlayerMP) player);

        }
    }

    @Override
    public void update() {
        if (!world.isRemote) {
            if (playerUsing != null) {
                if (inputStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                    if (inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemBanknote) {
                        //<editor-fold desc="Banknote Update">
                        int amount;
                        switch (inputStackHandler.getStackInSlot(0).getItemDamage()) {
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
                                amount = 20;
                                break;
                            case 4:
                                amount = 50;
                                break;
                            case 5:
                                amount = 100;
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
                            pack.setData(getPos(), DOUBLE_BANK, bank);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        markDirty();
                        //</editor-fold>
                    } else if(inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemCoin){
                        double amount;
                        switch(inputStackHandler.getStackInSlot(0).getItemDamage()) {
                            case 0:
                                amount = 0.01;
                                break;
                            case 1:
                                amount = 0.05;
                                break;
                            case 2:
                                amount = 0.10;
                                break;
                            case 3:
                                amount = 0.25;
                                break;
                            case 4:
                                amount = 1;
                                break;
                            case 5:
                                amount = 2;
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
                            pack.setData(getPos(), DOUBLE_BANK, bank);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        markDirty();

                    } else if (inputStackHandler.getStackInSlot(0).getItem() == ModItems.itemWallet) {

                        //<editor-fold desc="Wallet Up">
                        walletTotal = getTotalCash();
                        if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), DOUBLE_WALLETTOTAL, walletTotal);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                        //</editor-fold>

                    }
                }

            }
            //<editor-fold desc="Dealing with Buffer Slots">
            if (locked) {
                int outputAmnt = getCashConversion(outputBill);
                outLoop:
                if (profit >= outputAmnt) {
                    for (int i = 0; i < bufferStackHandler.getSlots(); i++) {
                        if (bufferStackHandler.getStackInSlot(i).isEmpty()) {
                            //Insert new stack
                            bufferStackHandler.setStackInSlot(i, new ItemStack(ModItems.itemBanknote, 1, outputBill));
                            profit = profit - outputAmnt;
                            if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                PacketSetLongToClient pack = new PacketSetLongToClient();
                                pack.setData(getPos(), DOUBLE_PROFIT, profit);
                                PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                            }
                            break outLoop;
                        } else if (UtilMethods.equalStacks(bufferStackHandler.getStackInSlot(i), new ItemStack(ModItems.itemBanknote, 1, outputBill)) && bufferStackHandler.getStackInSlot(i).getCount() < bufferStackHandler.getStackInSlot(i).getMaxStackSize()) {
                            //Grow Stack
                            bufferStackHandler.getStackInSlot(i).grow(1);
                            profit = profit - outputAmnt;
                            if (!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null) {
                                PacketSetLongToClient pack = new PacketSetLongToClient();
                                pack.setData(getPos(), DOUBLE_PROFIT, profit);
                                PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                            }
                            break outLoop;
                        }
                    }
                }
            }
            //</editor-fold>
        }

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
            case 3: return 20;
            case 4: return 50;
            case 5: return 100;
        }
        return -1;
    }

    private int getTotalCash(){
        ItemStack item = inputStackHandler.getStackInSlot(0);
        if(item.hasTagCompound()) {
            ItemStackHandler itemStackHandler = readInventoryTag(inputStackHandler.getStackInSlot(0), ItemWallet.WALLET_TOTAL_COUNT);

            int totalCash = 0;
            for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                if (itemStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
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
                            totalCash = totalCash + 20 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 4:
                            totalCash = totalCash + 50 * itemStackHandler.getStackInSlot(i).getCount();
                            break;
                        case 5:
                            totalCash = totalCash + 100 * itemStackHandler.getStackInSlot(i).getCount();
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
        long amount = (long)bank;
        double change = bank - amount;

        int[] out = new int[6];

        out[5] = Math.round(amount / 100);
        amount = amount - (out[5] * 100);

        out[4] = Math.round(amount / 50);
        amount = amount - (out[4] * 50);

        out[3] = Math.round(amount / 20);
        amount = amount - (out[3] * 20);

        out[2] = Math.round(amount / 10);
        amount = amount - (out[2] * 10);

        out[1] = Math.round(amount / 5);
        amount = amount - (out[1] * 5);

        out[0] = Math.round(amount);

        if (!world.isRemote) {
            for (int i = 0; i < out.length; i++) {
                if (out[i] != 0) {
                    ItemStack item = new ItemStack(ModItems.itemBanknote);
                    item.setItemDamage(i);
                    item.setCount(out[i]);

                    if (mode) {
                        profit = 0;
                        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), DOUBLE_PROFIT, 0);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                    } else {
                        bank = 0;
                        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
                            PacketSetLongToClient pack = new PacketSetLongToClient();
                            pack.setData(getPos(), DOUBLE_BANK, 0);
                            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
                        }
                    }

                    boolean playerInGui= false;
                    if (playerUsing != null) playerInGui = true;


                    if (playerInGui) {
                        InventoryPlayer inventoryPlayer = playerUsing.inventory;
                        if (inventoryPlayer.getFirstEmptyStack() != -1) {     //If Players Inventory has room
                            inventoryPlayer.setInventorySlotContents(inventoryPlayer.getFirstEmptyStack(), item);
                        } else {
                            playerInGui = false;
                        }
                    }

                    if (!playerInGui) {       //If no room, spawn
                        int x = getPos().getX();
                        int z = getPos().getZ();

                        switch (this.getBlockMetadata()) {
                            case 0:
                                z = z - 2; //North
                                break;
                            case 1:
                                x = x + 2; //East
                                break;
                            case 2:
                                z = z + 2; //South
                                break;
                            case 3:
                                x = x - 2;//West
                                break;
                        }
                        world.spawnEntity(new EntityItem(world, x, getPos().up().getY(), z, item));
                    }
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
                        z = z - 2; //North
                        break;
                    case 1:
                        x = x + 2; //East
                        break;
                    case 2:
                        z = z + 2; //South
                        break;
                    case 3:
                        x = x - 2;//West
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
        compound.setDouble("bank", bank);
        compound.setDouble("profit", profit);
        compound.setDouble("walletTotal", walletTotal);
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
        for (int i = 0; i < itemCosts.length; i++) itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
        compound.setTag("itemCosts", itemCostsNBT);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("item")) vendStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("item"));
        if (compound.hasKey("buffer")) bufferStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("buffer"));
        if (compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if (compound.hasKey("bank")) bank = compound.getDouble("bank");
        if (compound.hasKey("profit")) profit = compound.getDouble("profit");
        if (compound.hasKey("walletTotal")) walletTotal = compound.getDouble("walletTotal");
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
            for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = itemCostsNBT.getInteger("cost" + i);
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
        tag.setDouble("bank", bank);
        tag.setDouble("profit", profit);
        tag.setDouble("walletTotal", walletTotal);
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
        for (int i = 0; i < itemCosts.length; i++) itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
        tag.setTag("itemCosts", itemCostsNBT);

        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        bank = pkt.getNbtCompound().getDouble("bank");
        profit = pkt.getNbtCompound().getDouble("profit");
        outputBill = pkt.getNbtCompound().getInteger("output");
        walletTotal = pkt.getNbtCompound().getDouble("walletTotal");
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
        for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = itemCostsNBT.getInteger("cost" + i);
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

    public void setDouble(byte id, double value){
        switch(id){
            case DOUBLE_BANK:
                bank = value;
                break;
            case DOUBLE_PROFIT:
                profit = value;
                break;
            case DOUBLE_WALLETTOTAL:
                walletTotal = value;
                break;
        }
        if(!getWorld().isRemote && getPlayerUsing() != null && PacketHandler.INSTANCE != null){
            PacketSetLongToClient pack = new PacketSetLongToClient();
            pack.setData(getPos(), id, value);
            PacketHandler.INSTANCE.sendTo(pack, (EntityPlayerMP) getPlayerUsing());
        }
    }

    public double getDouble(byte id){
        switch(id){
            case DOUBLE_BANK:
                return bank;
            case DOUBLE_PROFIT:
                return profit;
            case DOUBLE_WALLETTOTAL:
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

    public ItemStack getStack(int index) {
        return vendStackHandler.getStackInSlot(index);
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

    public boolean isGhostSlot(int slot){
        return vendStackHandler.isGhost(slot);
    }

    public void setGhostSlot(int slot, boolean bool){
        vendStackHandler.setGhost(slot, bool);
    }
    //</editor-fold>
}
