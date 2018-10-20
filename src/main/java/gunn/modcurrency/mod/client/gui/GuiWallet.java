package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.container.ContainerWallet;
import gunn.modcurrency.mod.item.ItemWallet;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.utils.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemStackHandler;

import java.awt.*;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-01-17
 */
public class GuiWallet extends GuiContainer{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiwallettexture.png");
    private static final ResourceLocation EXTRA_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiwalletextratexture.png");
    public static final int GUI_XPOS_OFFPUT = -19;

    int fullness = 0;
    ItemStack wallet;

    public GuiWallet(EntityPlayer player, ItemStack wallet) {
        super(new ContainerWallet(player, wallet));

        this.wallet = wallet;

        NBTTagCompound compound = wallet.getTagCompound();
        fullness = compound.getInteger("full");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Background
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT, guiTop,0 ,0 , 213, 201);

        //Background Extras TODO
        Minecraft.getMinecraft().getTextureManager().bindTexture(EXTRA_TEXTURE);
        switch(fullness){
            default: break;
            case 3:
            case 2:
            case 1:
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 1, guiTop - 5,0 ,0 , 213, 14);
        }

        Minecraft.getMinecraft().getTextureManager().bindTexture(EXTRA_TEXTURE);
        switch(ItemWallet.WALLET_ROW_COUNT){
            default:
            case 4:     //4 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 70,0 ,152 ,162 , 18);
            case 3:     //3 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 16,0 ,98 ,162 , 18);
            case 2:     //2 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 52,0 ,134 ,162 , 18);
            case 0:
            case 1:     //1 Row
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 34,0 ,116 ,162 , 18);
        }

        if(this.wallet.hasTagCompound()){
            NBTTagCompound nbtTagCompound = this.wallet.getTagCompound();
            if(nbtTagCompound.hasKey("inventory")){
                ItemStackHandler itemStackHandler = new ItemStackHandler(ItemWallet.WALLET_TOTAL_COUNT);
                itemStackHandler.deserializeNBT((NBTTagCompound) nbtTagCompound.getTag("inventory"));

                int totalCash = getTotalCash(itemStackHandler);

                fontRenderer.drawSplitString(I18n.format("Total: $" + UtilMethods.translateMoney(totalCash)), guiLeft + 58 + 1, guiTop + 8 + 1, 112, Color.GRAY.getRGB());
                fontRenderer.drawSplitString(I18n.format("Total: $" + UtilMethods.translateMoney(totalCash)), guiLeft + 58, guiTop + 8, 112, Color.WHITE.getRGB());
            }
        }
    }


    public int getCashConversion(int meta) {
        switch (meta) {
            case 0:
                return 1;
            case 1:
                return 5;
            case 2:
                return 10;
            case 3:
                return 25;
            case 4:
                return 100;
            case 5:
                return 200;
            case 6:
                return 500;
            case 7:
                return 1000;
            case 8:
                return 2000;
            case 9:
                return 5000;
            case 10:
                return 10000;
        }
        return -1;
    }

    private int getTotalCash(ItemStackHandler itemStackHandler) {
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
            } else if (itemStackHandler.getStackInSlot(i).getItem().equals(ModItems.itemBanknote)) {
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
}
