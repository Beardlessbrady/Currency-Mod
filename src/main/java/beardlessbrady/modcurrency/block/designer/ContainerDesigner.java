package beardlessbrady.modcurrency.block.designer;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class ContainerDesigner extends Container {
    private TileDesigner te;

    //0-35 = Players Inv
    public final int HOTBAR_SLOT_COUNT = 9;
    public final int PLAYER_INV_ROW_COUNT = 3;
    public final int PLAYER_INV_COLUMN_COUNT = 9;
    public final int PLAYER_INV_TOTAL_COUNT = PLAYER_INV_COLUMN_COUNT * PLAYER_INV_ROW_COUNT;
    public final int PLAYER_TOTAL_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INV_TOTAL_COUNT;

    public ContainerDesigner(EntityPlayer entityPlayer, TileDesigner te){
        this.te = te;

        setupPlayerInv(entityPlayer.inventory);
        setupTeInv();
    }

    /** Required to allow block to be interacted with **/
    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return true;
    }

    /** Setup Player's inventory in the UI **/
    private void setupPlayerInv(InventoryPlayer invPlayer) {
        // Size of SLOT box X and Y */
        final int SLOT_X_SPACING = 18;
        final int SLOT_Y_SPACING = 18;

        // Starting positions of both the hotbar and the players inventory
        final int HOTBAR_XPOS = 8;
        final int HOTBAR_YPOS = 169;
        final int PLAYER_INV_XPOS = 8;
        final int PLAYER_INV_YPOS = 111;

        for (int x = 0; x < HOTBAR_SLOT_COUNT; x++) // Loops through players HOTBAR and add it to UI */
            addSlotToContainer(new Slot(invPlayer, x, HOTBAR_XPOS + SLOT_X_SPACING * x, HOTBAR_YPOS));


        for (int y = 0; y < PLAYER_INV_ROW_COUNT; y++) { // Loops through players INVENTORY and add it to UI */
            for (int x = 0; x < PLAYER_INV_COLUMN_COUNT; x++) {
                int slotNum = HOTBAR_SLOT_COUNT + y * PLAYER_INV_COLUMN_COUNT + x; // Determines the current Slot Number by math */
                int xpos = PLAYER_INV_XPOS + x * SLOT_X_SPACING;
                int ypos = PLAYER_INV_YPOS + y * SLOT_Y_SPACING;
                addSlotToContainer(new Slot(invPlayer, slotNum, xpos, ypos));
            }
        }
    }

    /** Setup Tile's inventory in the UI **/
    private void setupTeInv(){
      //  addSlotToContainer(new Slot())
    }

    /** Slot clicked **/
    @Override
    public ItemStack slotClick(int slotId, int dragType, ClickType clickTypeIn, EntityPlayer player) {
        return super.slotClick(slotId, dragType, clickTypeIn, player);
    }

    /** Shift click **/
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        return super.transferStackInSlot(playerIn, index);
    }
}
