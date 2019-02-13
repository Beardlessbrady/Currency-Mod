package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

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

    public GuiVending(EntityPlayer entityPlayer, TileVending te){
        super(new ContainerVending(entityPlayer, te));
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 41, 0, 0, 176, 324);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }


}
