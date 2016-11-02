package gunn.modcurrency.client.containers;

import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency Mod for Minecraft.
 *
 * The Currency Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-02.
 */
public class ContainerVendor extends Container{
    private TileVendor tilevendor;

    //0-8 = Player Hot bar
    //9-35 = Player Inventory's
    //36-67 = Tile Slots
    private final int HOTBAR_SLOT_COUNT = 9;
    private final int PLAYER_INV_ROW_COUNT = 3;
    private final int PLAYER_INV_COLUMN_COUNT = 9;
    private final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT + PLAYER_INV_ROW_COUNT;
    private final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    private final int PLAYER_FIRST_SLOT_INDEX = 0;
    private final int TE_MONEY_FIRST_SLOT_INDEX = PLAYER_FIRST_SLOT_INDEX + PLAYER_TOTAL_COUNT;
    private final int TE_VEND_FIRST_SLOT_INDEX = TE_MONEY_FIRST_SLOT_INDEX + 1;
    private final int TE_VEND_TOTAL_SLOT_COUNT = TE_VEND_FIRST_SLOT_INDEX + 30;

    public ContainerVendor(InventoryPlayer invPlayer, TileVendor tilevendor){
        this.tilevendor = tilevendor;

        //Players Inv Specs
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

        //TE Inv Specs


    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tilevendor.isUseableByPlayer(playerIn);
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        return null;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        super.onContainerClosed(player);
        this.tilevendor.closeInventory(player);
    }
}
