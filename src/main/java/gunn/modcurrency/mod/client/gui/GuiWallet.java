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
    EntityPlayer player;

    public GuiWallet(EntityPlayer player, ItemStack wallet) {
        super(new ContainerWallet(player, wallet));

        this.player = player;

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

       // System.out.println(this.player.getHeldItemMainhand().getTagCompound().hasKey("total"));
        if(this.player.getHeldItemMainhand().hasTagCompound()){
            NBTTagCompound nbtTagCompound = this.player.getHeldItemMainhand().getTagCompound();
            if(nbtTagCompound.hasKey("total")){
                int totalCash = nbtTagCompound.getInteger("total");

                fontRenderer.drawSplitString(I18n.format("Total: $" + UtilMethods.translateMoney(totalCash)), guiLeft + 58 + 1, guiTop + 8 + 1, 112, Color.GRAY.getRGB());
                fontRenderer.drawSplitString(I18n.format("Total: $" + UtilMethods.translateMoney(totalCash)), guiLeft + 58, guiTop + 8, 112, Color.WHITE.getRGB());
            }
        }
    }
}
