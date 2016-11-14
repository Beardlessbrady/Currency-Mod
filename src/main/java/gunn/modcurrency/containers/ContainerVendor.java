package gunn.modcurrency.containers;

import gunn.modcurrency.items.ModItems;
import gunn.modcurrency.tiles.TileVendor;
import gunn.modcurrency.util.SlotBank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.lwjgl.opencl.CL;

import javax.annotation.Nullable;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-02.
 */
public class ContainerVendor extends Container{
    private TileVendor tilevendor;
    
    //Slot Index's
    //0-35 = Player Inventory's
    //36 = Money Slot
    //37-67 = Vend Slots
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    private final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;
    
    private final int TE_VEND_COLUMN_COUNT = 6;
    private final int TE_VEND_ROW_COUNT = 5;
    private final int TE_VEND_TOTAL_COUNT = TE_VEND_COLUMN_COUNT * TE_VEND_ROW_COUNT;
    private final int TE_TOTAL= 31;

    private int[] cachedFields;

    public ContainerVendor(InventoryPlayer invPlayer, TileVendor tilevendor){
        this.tilevendor = tilevendor;

        setupPlayerInv(invPlayer);
        setupTeInv();
    }

    public void setupPlayerInv(InventoryPlayer invPlayer){
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 211;

        for(int x = 0; x < HOTBAR_SLOT_COUNT; x++){
            int slotNum = x;
            addSlotToContainer(new Slot(invPlayer, slotNum, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));
        }

        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 153;
        for(int y = 0; y < PLAYER_INV_ROW_COUNT; y++){
            for(int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++){
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x;
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    public void setupTeInv(){
        IItemHandler itemHandler = this.tilevendor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,null);
        final int TE_MONEY_XPOS = 152;
        final int TE_MONEY_YPOS = 9;
        addSlotToContainer(new SlotBank(itemHandler, 0,TE_MONEY_XPOS,TE_MONEY_YPOS));

        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;
        final int TE_INV_XPOS = 44;
        final int TE_INV_YPOS = 32;

        for(int y = 0; y < TE_VEND_COLUMN_COUNT; y++){
            for(int x = 0; x < TE_VEND_ROW_COUNT; x++){
                int slotNum = 1 + y * TE_VEND_ROW_COUNT + x;
                int xpos = TE_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = TE_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new SlotItemHandler(itemHandler,slotNum,xpos,ypos));
            }
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tilevendor.canInteractWith(playerIn);
    }

    @Nullable
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        if(slotId >= 0 && slotId <= 36) {           //Is Players Inv or Money Slot
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }else if(slotId >= 37 && slotId <= 67){     //Is Tile Inv (and not money)
            
            if(clickTypeIn == ClickType.CLONE) tilevendor.setField(3, slotId);
            
            if((clickTypeIn == ClickType.CLONE) || (clickTypeIn == ClickType.PICKUP && slotId == tilevendor.getField(3))){
                if(getSlot(slotId).getHasStack()) {
                    tilevendor.setSelectedName(getSlot(slotId).getStack().getDisplayName());
                }else{
                    tilevendor.setSelectedName("No Item");
                }
                tilevendor.getWorld().notifyBlockUpdate(tilevendor.getPos(), tilevendor.getBlockType().getDefaultState(), tilevendor.getBlockType().getDefaultState(), 3);
                return null;
            }

            
            return super.slotClick(slotId, dragType, clickTypeIn, player);
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack sourceStack = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack copyStack = slot.getStack();
            sourceStack = copyStack.copy();

            if (index < PLAYER_TOTAL_COUNT){        //Player Inventory Slots
                if(inventorySlots.get(index).getStack().getItem() == ModItems.itembanknote){
                    if (!this.mergeItemStack(copyStack, TE_MONEY_FIRST_SLOT_INDEX,TE_MONEY_FIRST_SLOT_INDEX+1, false)) {
                        return null;
                    }
                }else {
                    if (!this.mergeItemStack(copyStack, TE_VEND_FIRST_SLOT_INDEX, TE_VEND_FIRST_SLOT_INDEX + TE_VEND_TOTAL_COUNT, false)) {
                        return null;
                    }
                }
            }else if (index >= TE_VEND_FIRST_SLOT_INDEX && index < TE_VEND_FIRST_SLOT_INDEX + TE_VEND_TOTAL_COUNT){  //TE Inventory
                if (!this.mergeItemStack(copyStack, 0, PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT, false)) {
                    return null;
                }
            }

            if (copyStack.stackSize == 0) {
                slot.putStack(null);
            } else {
                slot.onSlotChanged();
            }
        }
        return sourceStack;
    }


    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        //this.tilevendor.closeInventory(player);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        boolean fieldChanged[] = new boolean[tilevendor.getFieldCount()];

        if (cachedFields == null) {
            cachedFields = new int[tilevendor.getFieldCount()];
        }
        for (int i = 0; i < cachedFields.length; i++) {
            if (cachedFields[i] != tilevendor.getField(i)) {
                cachedFields[i] = tilevendor.getField(i);
                fieldChanged[i] = true;
            }
        }

        for (IContainerListener listener : this.listeners) {
            for (int field = 0; field < tilevendor.getFieldCount(); ++field) {
                if (fieldChanged[field]) {
                    listener.sendProgressBarUpdate(this, field, cachedFields[field]);
                }
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tilevendor.setField(id, data);
    }
}

