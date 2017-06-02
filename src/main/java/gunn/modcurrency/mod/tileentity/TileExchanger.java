package gunn.modcurrency.mod.tileentity;

import gunn.modcurrency.mod.ModCurrency;
import gunn.modcurrency.mod.client.gui.util.INBTInventory;
import gunn.modcurrency.mod.container.itemhandler.ItemHandlerCustom;
import gunn.modcurrency.mod.container.itemhandler.ItemHandlerVendor;
import gunn.modcurrency.mod.handler.StateHandler;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
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
 * File Created on 2017-05-28
 */
public class TileExchanger extends TileEntity implements ICapabilityProvider, ITickable, INBTInventory, IOwnable{
    private static final int INPUT_SLOT_COUNT = 1;
    public static final int VEND_SLOT_COUNT = 30;

    private int bank, selectedSlot, cashRegister;
    private String owner, selectedName;
    private boolean locked, mode, creative, infinite, gearExtended, twoBlock;
    private int[] itemCosts = new int[VEND_SLOT_COUNT];
    private int[] itemAmounts = new int[VEND_SLOT_COUNT];
    private ItemStackHandler inputStackHandler = new ItemStackHandler(INPUT_SLOT_COUNT);
    private ItemStackHandler vendStackHandler = new ItemStackHandler(VEND_SLOT_COUNT);
    private ItemStackHandler bufferStackHandler = new ItemHandlerVendor(1);
    private ItemHandlerCustom automationInputStackHandler = new ItemHandlerCustom(1);
    private EntityPlayer playerUsing = null;

    public TileExchanger() {
        bank = 0;
        cashRegister = 0;
        selectedSlot = 37;
        owner = "";
        selectedName = "No Item";
        locked = false;
        mode = false;
        creative = false;
        infinite = false;
        gearExtended = false;
        twoBlock = false;
        automationInputStackHandler.setAllowedItem(ModItems.itemBanknote);

        for (int i = 0; i < itemCosts.length; i++){
            itemCosts[i] = 0;
            itemAmounts[i] = 0;
        }
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos) {
        if(world.getBlockState(pos).getValue(StateHandler.TWOTALL) == StateHandler.EnumTwoBlock.TWOTOP){
            player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.down().getY(), pos.getZ());
        }else player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.getY(), pos.getZ());
        playerUsing = player;
    }

    @Override
    public void update() {
        if(playerUsing != null) {
            if (!world.isRemote) {
                if (automationInputStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                    if (automationInputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemBanknote)) {
                        int amount;
                        switch (automationInputStackHandler.getStackInSlot(0).getItemDamage()) {
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
                        amount = amount * automationInputStackHandler.getStackInSlot(0).getCount();
                        automationInputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                        cashRegister = cashRegister + amount;
                    }
                }

                if (!mode) {        //SELL MODE
                    if (inputStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                        searchLoop:
                        for (int i = 0; i < vendStackHandler.getSlots(); i++) {
                            if (vendStackHandler.getStackInSlot(i) != ItemStack.EMPTY) {
                                if (inputStackHandler.getStackInSlot(0).getItem().equals(vendStackHandler.getStackInSlot(i).getItem()) &&
                                        inputStackHandler.getStackInSlot(0).getItemDamage() == vendStackHandler.getStackInSlot(i).getItemDamage()) {
                                    int cost = getItemCost(i);
                                    boolean isThereRoom = false;
                                    int buffSlot = 0;

                                    if (bufferStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                                        if ((bufferStackHandler.getStackInSlot(0).getItem().equals(inputStackHandler.getStackInSlot(0).getItem()) &&
                                                bufferStackHandler.getStackInSlot(0).getItemDamage() == inputStackHandler.getStackInSlot(0).getItemDamage()
                                                && (bufferStackHandler.getStackInSlot(0).getCount() < bufferStackHandler.getStackInSlot(0).getMaxStackSize())))
                                            isThereRoom = true;
                                    } else isThereRoom = true;


                                    if ((cashRegister >= cost || infinite) && isThereRoom) {
                                        ItemStack inputItem = inputStackHandler.getStackInSlot(0);
                                        bank = bank + cost;
                                        if (!infinite) {
                                            cashRegister = cashRegister - cost;
                                            if (bufferStackHandler.getStackInSlot(buffSlot) != ItemStack.EMPTY)
                                                bufferStackHandler.getStackInSlot(buffSlot).grow(1);
                                            if (bufferStackHandler.getStackInSlot(buffSlot) == ItemStack.EMPTY) {
                                                ItemStack newStack = inputItem.copy();
                                                newStack.setCount(1);
                                                bufferStackHandler.setStackInSlot(buffSlot, newStack);
                                            }
                                        }
                                        inputItem.shrink(1);
                                        if (itemAmounts[i] > 1) {
                                            vendStackHandler.getStackInSlot(i).shrink(1);
                                            itemAmounts[i]--;
                                        }else if (itemAmounts[i] == 1){
                                            vendStackHandler.setStackInSlot(i, ItemStack.EMPTY);
                                            itemAmounts[i] = -1;
                                        }
                                    }
                                }
                            }
                            if (inputStackHandler.getStackInSlot(0).getCount() == 0) {
                                inputStackHandler.setStackInSlot(0, ItemStack.EMPTY);
                                break searchLoop;
                            }
                        }
                    }
                } else {        //EDIT MODE
                    if (inputStackHandler.getStackInSlot(0) != ItemStack.EMPTY) {
                        if (inputStackHandler.getStackInSlot(0).getItem().equals(ModItems.itemBanknote)) {
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
                            cashRegister = cashRegister + amount;
                        }
                    }
                }
            }
            markDirty();
        }
    }

    //Drop Items
    public void dropItems() {
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
        for (int i = 3; i < bufferStackHandler.getSlots(); i++){
            ItemStack item = bufferStackHandler.getStackInSlot(i);
            if (item != ItemStack.EMPTY) {
                world.spawnEntity(new EntityItem(world, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                bufferStackHandler.setStackInSlot(i, ItemStack.EMPTY);   //Just in case
            }
        }
    }

    public boolean canInteractWith(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    //<editor-fold desc="Money Methods-------------------------------------------------------------------------------------------------------">
    public void outChange() {
        int amount = bank;
        if(mode) amount = cashRegister;

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

                    if(mode){
                        cashRegister = 0;
                    }else {
                        bank = 0;
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
        compound.setTag("autoInput", automationInputStackHandler.serializeNBT());
        compound.setInteger("bank", bank);
        compound.setInteger("cashRegister", cashRegister);
        compound.setBoolean("locked", locked);
        compound.setBoolean("mode", mode);
        compound.setBoolean("creative", creative);
        compound.setBoolean("infinite", infinite);
        compound.setBoolean("gearExtended", gearExtended);
        compound.setBoolean("twoBlock", twoBlock);
        compound.setInteger("selectedSlot", selectedSlot);
        compound.setString("selectedName", selectedName);
        compound.setString("owner", owner);

        NBTTagCompound itemCostsNBT = new NBTTagCompound();
        for (int i = 0; i < itemCosts.length; i++) itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
        compound.setTag("itemCosts", itemCostsNBT);

        NBTTagCompound itemAmountNBT = new NBTTagCompound();
        for (int i = 0; i < itemAmounts.length; i++) itemAmountNBT.setInteger("amount" + i, itemAmounts[i]);
        compound.setTag("itemAmounts", itemAmountNBT);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("item")) vendStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("item"));
        if (compound.hasKey("buffer")) bufferStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("buffer"));
        if (compound.hasKey("input")) inputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("input"));
        if (compound.hasKey("autoInput")) automationInputStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("autoInput"));
        if (compound.hasKey("bank")) bank = compound.getInteger("bank");
        if (compound.hasKey("cashRegister")) cashRegister = compound.getInteger("cashRegister");
        if (compound.hasKey("locked")) locked = compound.getBoolean("locked");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if (compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if (compound.hasKey("infinite")) infinite = compound.getBoolean("infinite");
        if (compound.hasKey("gearExtended")) gearExtended = compound.getBoolean("gearExtended");
        if (compound.hasKey("twoBlock")) twoBlock = compound.getBoolean("twoBlock");
        if (compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
        if (compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
        if (compound.hasKey("owner")) owner = compound.getString("owner");

        if (compound.hasKey("itemCosts")) {
            NBTTagCompound itemCostsNBT = compound.getCompoundTag("itemCosts");
            for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = itemCostsNBT.getInteger("cost" + i);
        }

        if (compound.hasKey("itemAmounts")) {
            NBTTagCompound itemAmountsNBT = compound.getCompoundTag("itemAmounts");
            for (int i = 0; i < itemAmounts.length; i++) itemAmounts[i] = itemAmountsNBT.getInteger("amount" + i);
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
        tag.setInteger("bank", bank);
        tag.setInteger("cashRegister", cashRegister);
        tag.setBoolean("locked", locked);
        tag.setBoolean("mode", mode);
        tag.setBoolean("creative", creative);
        tag.setBoolean("infinite", infinite);
        tag.setBoolean("gearExtended", gearExtended);
        tag.setBoolean("twoBlock", twoBlock);
        tag.setInteger("selectedSlot", selectedSlot);
        tag.setString("selectedName", selectedName);
        tag.setString("owner", owner);

        NBTTagCompound itemCostsNBT = new NBTTagCompound();
        for (int i = 0; i < itemCosts.length; i++) itemCostsNBT.setInteger("cost" + i, itemCosts[i]);
        tag.setTag("itemCosts", itemCostsNBT);

        NBTTagCompound itemAmountsNBT = new NBTTagCompound();
        for (int i = 0; i < itemAmounts.length; i++) itemAmountsNBT.setInteger("amount" + i, itemAmounts[i]);
        tag.setTag("itemAmounts", itemAmountsNBT);

        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        bank = pkt.getNbtCompound().getInteger("bank");
        cashRegister = pkt.getNbtCompound().getInteger("cashRegister");
        locked = pkt.getNbtCompound().getBoolean("locked");
        mode = pkt.getNbtCompound().getBoolean("mode");
        creative = pkt.getNbtCompound().getBoolean("creative");
        infinite = pkt.getNbtCompound().getBoolean("infinite");
        gearExtended = pkt.getNbtCompound().getBoolean("gearExtended");
        twoBlock = pkt.getNbtCompound().getBoolean("twoBlock");
        selectedSlot = pkt.getNbtCompound().getInteger("selectedSlot");
        selectedName = pkt.getNbtCompound().getString("selectedName");
        owner = pkt.getNbtCompound().getString("owner");

        NBTTagCompound itemCostsNBT = pkt.getNbtCompound().getCompoundTag("itemCosts");
        for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = itemCostsNBT.getInteger("cost" + i);

        NBTTagCompound itemAmountsNBT = pkt.getNbtCompound().getCompoundTag("itemAmounts");
        for (int i = 0; i < itemAmounts.length; i++) itemAmounts[i] = itemAmountsNBT.getInteger("amounts" + i);
    }
    //</editor-fold>--------------------------------

    //<editor-fold desc="ItemStackHandler Methods--------------------------------------------------------------------------------------------">
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if(facing == null) return true;
            if(!locked) return false;
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == null) return (T) new CombinedInvWrapper(inputStackHandler, vendStackHandler, bufferStackHandler); //Inside Itself
            if (facing == EnumFacing.DOWN) return (T) bufferStackHandler;
            if (facing != EnumFacing.DOWN) return (T) automationInputStackHandler;
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    //<editor-fold desc="Getter & Setter Methods---------------------------------------------------------------------------------------------">
    public int getFieldCount() {
        return 8;
    }

    public void setField(int id, int value) {
        switch (id) {
            case 0:
                bank = value;
                break;
            case 1:
                locked = (value == 1);
                break;
            case 2:
                mode = (value == 1);
                break;
            case 3:
                selectedSlot = value;
                break;
            case 4:
                cashRegister = value;
                break;
            case 5:
                creative = (value == 1);
                break;
            case 6:
                infinite = (value == 1);
                break;
            case 7:
                twoBlock = (value == 1);
                break;
            case 8:
                gearExtended = (value == 1);
                break;
        }
    }

    public int getField(int id) {
        switch (id) {
            case 0:
                return bank;
            case 1:
                return (locked) ? 1 : 0;
            case 2:
                return (mode) ? 1 : 0;
            case 3:
                return selectedSlot;
            case 4:
                return cashRegister;
            case 5:
                return (creative) ? 1 : 0;
            case 6:
                return (infinite) ? 1 : 0;
            case 7:
                return (twoBlock) ? 1 : 0;
            case 8:
                return (gearExtended) ? 1 : 0;
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

    public int getItemAmount(int index) {
        return itemAmounts[index];
    }

    public void setItemAmount(int amount){
        itemAmounts[selectedSlot - 37] = amount;
        if(amount == -1){
            vendStackHandler.getStackInSlot(selectedSlot - 37).setCount(1);
        }else {
            vendStackHandler.getStackInSlot(selectedSlot - 37).setCount(itemAmounts[selectedSlot - 37]);
        }
    }

    public void setItemAmount(int amount, int index){
        itemAmounts[index] = amount;
        if(amount == -1){
            vendStackHandler.getStackInSlot(index).setCount(1);
        }else {
            vendStackHandler.getStackInSlot(index).setCount(itemAmounts[index]);
        }
    }

    public ItemStackHandler getBufferStackHandler(){
        return bufferStackHandler;
    }

    public ItemStackHandler getInputStackHandler(){
        return inputStackHandler;
    }

    public ItemStackHandler getVendStackHandler(){
        return vendStackHandler;
    }

    public void setBufferStackHandler(ItemStackHandler buf){
        bufferStackHandler = buf;
    }

    public void setInputStackHandler(ItemStackHandler input){
        inputStackHandler = input;
    }

    public void setVendStackHandler(ItemStackHandler vend){
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
    //</editor-fold>
}