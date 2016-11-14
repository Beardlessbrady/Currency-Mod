package gunn.modcurrency.tiles;

import gunn.modcurrency.items.ModItems;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-10-30.
 */
public class TileVendor extends TileEntity implements ICapabilityProvider, ITickable{

    public static final int MONEY_SLOT_COUNT = 1;
    public static final int VEND_SLOT_COUNT = 30;
    public static final int TOTAL_SLOTS_COUNT = MONEY_SLOT_COUNT + VEND_SLOT_COUNT;

    private int bank, selectedSlot;
    private boolean locked, mode;
    //Mode 0 == Sell, 1 == Edit
    private String selectedName;
    private ItemStackHandler itemStackHandler = new ItemStackHandler(TOTAL_SLOTS_COUNT) {
        @Override
        protected void onContentsChanged(int slot) { markDirty(); }
    };
    
    private int[] itemCosts = new int[TOTAL_SLOTS_COUNT];
    
    

    public TileVendor(){
        bank = 0;
        locked = false;
        mode = false;
        selectedSlot = 37;
        selectedName = "No Item";
        
        for(int i = 0; i < itemCosts.length; i++){
            if(i == 1){
            }
            itemCosts[i] = 0;
        }
    }

    @Override
    public void update() {
        if(!worldObj.isRemote){
            if(itemStackHandler.getStackInSlot(0) != null){
                int amnt;
                switch(itemStackHandler.getStackInSlot(0).getItemDamage()){
                    case 0:         //One Dollar Bill
                        amnt = 1;
                        break;
                    case 1:         //Five Dollar Bill
                        amnt = 5;
                        break;
                    case 2:         //Ten Dollar Bill
                        amnt = 10;
                        break;
                    case 3:         //Twenty Dollar Bill
                        amnt = 20;
                        break;
                    case 4:         //Fifty Dollar Bill
                        amnt = 50;
                        break;
                    case 5:         //One Hundred Dollar Bill
                        amnt = 100;
                        break;
                    default:
                        amnt = -1;
                        break;
                }
                amnt = amnt * itemStackHandler.getStackInSlot(0).stackSize;
                itemStackHandler.setStackInSlot(0, null);
                bank = bank + amnt;
                markDirty();
            }
        }
    }
    
    //Outputs change in least amount of bills
    public void outChange(){
        int amount = bank;
        int[] out = new int[6];

        out[5] = Math.round(amount / 100);
        amount = amount - (out[5] * 100);

        out[4] =  Math.round(amount / 50);
        amount = amount - (out[4] * 50);

        out[3] = Math.round(amount / 20);
        amount = amount - (out[3] * 20);

        out[2] = Math.round(amount / 10);
        amount = amount - (out[2] * 10);

        out[1] = Math.round(amount / 5);
        amount = amount - (out[1] * 5);

        out[0] = Math.round(amount);
        amount = amount - out[0];

        if(amount != 0) System.err.print("Calculating Change messed up somewhere....");
        bank = 0;

        if(!worldObj.isRemote) {
            for(int i = 0; i < out.length; i++) {
                if(out[i] != 0) {
                    ItemStack item = new ItemStack(ModItems.itembanknote);
                    item.setItemDamage(i);
                    item.stackSize = out[i];
                    worldObj.spawnEntityInWorld(new EntityItem(worldObj, getPos().getX(), getPos().getY(), getPos().getZ(), item));
                }
            }
        }
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if(capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return (T) itemStackHandler;
        }
        return super.getCapability(capability, facing);
    }
    
    public boolean canInteractWith(EntityPlayer player){
        return !isInvalid() && player.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if(compound.hasKey("items")) itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        if(compound.hasKey("bank")) bank = compound.getInteger("bank");
        if(compound.hasKey("locked")) locked = compound.getBoolean("locked");
        if(compound.hasKey("mode")) mode = compound.getBoolean("mode");
        if(compound.hasKey("selectedSlot")) selectedSlot = compound.getInteger("selectedSlot");
        if(compound.hasKey("selectedName")) selectedName = compound.getString("selectedName");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        compound.setInteger("bank", bank);
        compound.setBoolean("locked", locked);
        compound.setBoolean("mode", mode);
        compound.setInteger("selectedSlot", selectedSlot);
        compound.setString("selectedName", selectedName);
        return compound;
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
        tag.setBoolean("locked", locked);
        tag.setBoolean("mode", mode);
        tag.setInteger("selectedSlot", selectedSlot);
        tag.setString("selectedName", selectedName);
        return new SPacketUpdateTileEntity(pos, 1, tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        bank = pkt.getNbtCompound().getInteger("bank");
        locked = pkt.getNbtCompound().getBoolean("locked");
        mode = pkt.getNbtCompound().getBoolean("mode");
        selectedSlot = pkt.getNbtCompound().getInteger("selectedSlot");
        selectedName = pkt.getNbtCompound().getString("selectedName");
    }

    public int getFieldCount(){
        return 4;
    }

    public void setField(int id, int value){
        switch(id){
            case 0: bank = value;
                break;
            case 1: locked = (value == 1);
                break;
            case 2: mode = (value == 1);
                break;
            case 3: selectedSlot = value;
                break;
        }
        
    }

    public int getField(int id){
        switch(id){
            case 0: return bank;
            case 1: return (locked) ? 1 : 0;
            case 2: return (mode) ? 1 : 0;
            case 3: return selectedSlot;
        }
        return -1;
    }
    
    public String getSelectedName(){
        return selectedName;
    }
    
    public void setSelectedName(String name){
        selectedName = name;
    }
    
    public int getItemCost(int index){
        return itemCosts[index - 37];       //Note if you are getting this with selected slot, make sure to subtract 37
    }
    
    public boolean isItemStackNull(int index){
        return itemStackHandler.getStackInSlot(index) == null;
    }
    
}