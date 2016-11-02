package gunn.modcurrency.tiles;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import scala.actors.threadpool.Arrays;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class TileVendor extends TileEntity implements IInventory{

    public static final int MONEY_SLOT_COUNT = 1;
    public static final int VEND_SLOT_COUNT = 30;
    public static final int TOTAL_SLOTS_COUNT = MONEY_SLOT_COUNT + VEND_SLOT_COUNT;

    public static final int FIRST_MONEY_SLOT = 0;
    public static final int FIRST_VEND_SLOT = FIRST_MONEY_SLOT + VEND_SLOT_COUNT;

    private ItemStack[] vendorStacks = new ItemStack[TOTAL_SLOTS_COUNT];


    @Override
    public int getSizeInventory() {
        return vendorStacks.length;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return vendorStacks[index];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack currentStack = getStackInSlot(index);
        if(currentStack == null) return null;

        ItemStack removedStack;
        if(currentStack.stackSize <= count){
            removedStack = currentStack;
            setInventorySlotContents(index,null);
        }else{
            removedStack = currentStack.splitStack(count);
            if(currentStack.stackSize == 0){
                setInventorySlotContents(index,null);
            }
        }
        markDirty();
        return removedStack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack currentStack = getStackInSlot(index);
        if(currentStack != null) setInventorySlotContents(index, null);
        return currentStack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        vendorStacks[index] = stack;
        if(stack != null && stack.stackSize > getInventoryStackLimit()){
            stack.stackSize = getInventoryStackLimit();
        }
        markDirty();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        if(this.worldObj.getTileEntity(getPos()) != this) return false;
        final double X_OFFSET = 0.5;
        final double Y_OFFSET = 0.5;
        final double Z_OFFSET = 0.5;
        final double MAX_DISTANCE_SQ = 8.0 * 8.0;
        return player.getDistanceSq(pos.getX() + X_OFFSET, pos.getY() + Y_OFFSET, pos.getZ() + Z_OFFSET) < MAX_DISTANCE_SQ;
    }

    @Override
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }

    @Override
    public int getField(int id) { return 0; }

    @Override
    public void setField(int id, int value) {}

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
        Arrays.fill(vendorStacks,null);
    }

    @Override
    public String getName() {
        return "containers.vendor.name";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        NBTTagList dataForAllSlots = new NBTTagList();
        for(int i = 0; i < this.vendorStacks.length; i++){
            if(this.vendorStacks[i] != null){
                NBTTagCompound dataForThisSlot = new NBTTagCompound();
                dataForThisSlot.setByte("Slot", (byte) i);
                this.vendorStacks[i].writeToNBT(dataForThisSlot);
                dataForAllSlots.appendTag(dataForThisSlot);
            }
        }
        compound.setTag("Items", dataForAllSlots);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        final byte NBT_TYPE_COMPOUND = 10; //see NBTBase.createNewByType()
        NBTTagList dataForAllSlots = compound.getTagList("Items", NBT_TYPE_COMPOUND);

        Arrays.fill(vendorStacks, null); //Sets all to empty before inserting whats saved
        for(int i = 0; i < dataForAllSlots.tagCount(); ++i){
            NBTTagCompound dataForOneSlot = dataForAllSlots.getCompoundTagAt(i);
            int index = dataForOneSlot.getByte("Slot") & 255;

            if(index >= 0 && index < this.vendorStacks.length){
                this.vendorStacks[index] = ItemStack.loadItemStackFromNBT(dataForOneSlot);
            }
        }
    }
}



















