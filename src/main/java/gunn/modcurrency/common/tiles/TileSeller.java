package gunn.modcurrency.common.tiles;

import gunn.modcurrency.ModCurrency;
import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.items.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
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
 * File Created on 2016-12-19
 */
public class TileSeller extends ModTile implements ICapabilityProvider, ITickable{
    private static final int SELL_SLOT_COUNT = 1;
    private static final int VEND_SLOT_COUNT = 30;
    private static final int BUFFER_SLOT_COUNT = 6;
    private static final int TOTAL_SLOTS_COUNT = SELL_SLOT_COUNT + VEND_SLOT_COUNT + BUFFER_SLOT_COUNT;

    private int bank, selectedSlot, face, cashRegister;
    private String owner, selectedName;
    private boolean locked, mode, creative, infinite, gearExtended;
    private int[] itemCosts = new int[TOTAL_SLOTS_COUNT];       //Always Ignore slot 0
    private ItemStackHandler itemStackHandler = new ItemStackHandler(TOTAL_SLOTS_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }};

    public TileSeller() {
        bank = 0;
        selectedSlot = 37;
        face = 0;
        owner = "";
        selectedName = "No Item";
        locked = false;
        mode = false;
        creative = false;
        infinite = false;
        gearExtended = false;

        for (int i = 0; i < itemCosts.length; i++) itemCosts[i] = 0;
    }

    public void openGui(EntityPlayer player, World world, BlockPos pos){
        player.openGui(ModCurrency.instance, 31, world, pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public void update() {
        if (!worldObj.isRemote) {
            if (!mode) {        //SELL MODE
                if (itemStackHandler.getStackInSlot(0) != null) {
                    searchLoop:
                    for (int i = 1; i < itemStackHandler.getSlots(); i++) {
                        if (itemStackHandler.getStackInSlot(i) != null) {
                            if (itemStackHandler.getStackInSlot(0).getUnlocalizedName().equals(itemStackHandler.getStackInSlot(i).getUnlocalizedName())) {
                                int cost = getItemCost(i - 1);
                                if((cashRegister >= cost || infinite) && (itemStackHandler.getStackInSlot(i).stackSize < itemStackHandler.getStackInSlot(i).getMaxStackSize())){
                                    ItemStack inputItem = itemStackHandler.getStackInSlot(0);
                                    ItemStack stockitem = itemStackHandler.getStackInSlot(i);
                                    bank = bank + cost;
                                    if(!infinite) cashRegister = cashRegister - cost;
                                    inputItem.stackSize--;
                                    if(!infinite) stockitem.stackSize++;
                                }
                            }
                        }
                        if(itemStackHandler.getStackInSlot(0).stackSize == 0) {
                            itemStackHandler.setStackInSlot(0, null);
                            break searchLoop;
                        }
                    }
                }
            } else {        //EDIT MODE
                if (itemStackHandler.getStackInSlot(0) != null) {
                    if (itemStackHandler.getStackInSlot(0).getItem().equals(ModItems.itembanknote)) {
                        int amount;
                        switch (itemStackHandler.getStackInSlot(0).getItemDamage()) {
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
                        amount = amount * itemStackHandler.getStackInSlot(0).stackSize;
                        itemStackHandler.setStackInSlot(0, null);
                        cashRegister = cashRegister + amount;
                    }
                }
            }
        }
        markDirty();
    }

    //Outputs change in least amount of bills
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

        if (!worldObj.isRemote) {
            for (int i = 0; i < out.length; i++) {
                if (out[i] != 0) {
                    ItemStack item = new ItemStack(ModItems.itembanknote);
                    item.setItemDamage(i);
                    item.stackSize = out[i];

                    if(mode){
                        cashRegister = 0;
                    }else {
                        bank = 0;
                    }
                    worldObj.spawnEntityInWorld(new EntityItem(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                }
            }
        }
    }

    //Drop Items
    public void dropItems(){
        for(int i = 0; i < itemStackHandler.getSlots(); i++){
            ItemStack item = itemStackHandler.getStackInSlot(i);
            if(item != null){
                worldObj.spawnEntityInWorld(new EntityItem(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                itemStackHandler.setStackInSlot(i, null);   //Just in case
            }
        }
    }

    //Player must be in certain range to open GUI
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    //<editor-fold desc="NBT & Packet Stoof--------------------------------------------------------------------------------------------------">
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        compound.setInteger("bank", bank);
        compound.setInteger("face", face);
        compound.setInteger("cashRegister", cashRegister);
        compound.setBoolean("locked", locked);
        compound.setBoolean("mode", mode);
        compound.setBoolean("creative", creative);
        compound.setBoolean("infinite", infinite);
        compound.setBoolean("gearExtended", gearExtended);
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
        if (compound.hasKey("items")) itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        if (compound.hasKey("bank")) bank = compound.getInteger("bank");
        if (compound.hasKey("face")) face = compound.getInteger("face");
        if (compound.hasKey("cashRegister")) cashRegister = compound.getInteger("cashRegister");
        if (compound.hasKey("locked")) locked = compound.getBoolean("locked");
        if (compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if (compound.hasKey("creative")) creative = compound.getBoolean("creative");
        if (compound.hasKey("infinite")) infinite = compound.getBoolean("infinite");
        if (compound.hasKey("gearExtended")) gearExtended = compound.getBoolean("gearExtended");
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
        tag.setInteger("bank", bank);
        tag.setInteger("face", face);
        tag.setInteger("cashRegister", cashRegister);
        tag.setBoolean("locked", locked);
        tag.setBoolean("mode", mode);
        tag.setBoolean("creative", creative);
        tag.setBoolean("infinite", infinite);
        tag.setBoolean("gearExtended", gearExtended);
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
        bank = pkt.getNbtCompound().getInteger("bank");
        face = pkt.getNbtCompound().getInteger("face");
        cashRegister = pkt.getNbtCompound().getInteger("cashRegister");
        locked = pkt.getNbtCompound().getBoolean("locked");
        mode = pkt.getNbtCompound().getBoolean("mode");
        creative = pkt.getNbtCompound().getBoolean("creative");
        infinite = pkt.getNbtCompound().getBoolean("infinite");
        gearExtended = pkt.getNbtCompound().getBoolean("gearExtended");
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
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) itemStackHandler;
        }
        return super.getCapability(capability, facing);
    }
    //</editor-fold>

    //<editor-fold desc="Getter & Setter Methods---------------------------------------------------------------------------------------------">
    @Override
    public int getFieldCount() {return 9;}

    @Override
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
                face = value;
                break;
            case 8:
                gearExtended = (value == 1);
        }
    }

    @Override
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
                return face;
            case 8:
                return (gearExtended) ? 1 : 0;
        }
        return -1;
    }

    @Override
    public String getSelectedName() {return selectedName;}

    @Override
    public void setSelectedName (String name){selectedName = name;}

    @Override
    public int[] getAllItemCosts(){return itemCosts.clone();}

    @Override
    public void setAllItemCosts(int[] copy){itemCosts = copy.clone();}

    @Override
    public int getItemCost(int index) {return itemCosts[index];}

    @Override
    public void setItemCost(int amount) {itemCosts[selectedSlot - 37] = amount;}

    @Override
    public ItemStack getStack(int index){
        return itemStackHandler.getStackInSlot(index);
    }

    @Override
    public void setOwner(String owner){
        this.owner = owner;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    @Override
    public ItemStackHandler getStackHandler(){ return itemStackHandler; }

    @Override
    public void setStackHandler(ItemStackHandler copy){ itemStackHandler = copy; }
    //</editor-fold>

}
