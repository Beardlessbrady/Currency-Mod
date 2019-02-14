package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class GuiVending extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/vendingmachinegui.png");

    //Button ID's
    private static final int BUTTONCHANGE = 0;

    public GuiVending(EntityPlayer entityPlayer, TileVending te){
        super(new ContainerVending(entityPlayer, te));
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(BUTTONCHANGE,  i + 143 , j + 27, 20, 20, "$"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        fontRenderer.drawString(I18n.format("tile.modcurrency:blockvending.name"), 8, -42,  Color.darkGray.getRGB());
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, 114,  Color.darkGray.getRGB());
        this.fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.in"), 149, -8, Color.lightGray.getRGB());
        this.fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.out"), 80, 68, Color.lightGray.getRGB());
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }


}
