package gunn.modcurrency.mod.guis;

import gunn.modcurrency.mod.containers.ContainerATM;
import gunn.modcurrency.mod.tiles.TileATM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-03-16
 */
public class GuiATM extends GuiContainer{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiatmtexture.png");
    private GuiTextField withdrawField;

    public GuiATM(InventoryPlayer invPlayer, TileATM te) {
        super(new ContainerATM());
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(0, i + 68, j + 74, 45, 20, "Deposit"));
        this.buttonList.add(new GuiButton(1, i + 65, j + 32, 48, 20, "Withdraw"));

        this.withdrawField = new GuiTextField(0, fontRendererObj, i + 60, j + 39, 45, 10);
        this.withdrawField.setEnableBackgroundDrawing(false);
        this.withdrawField.setTextColor(Integer.parseInt("0099ff", 16));
        this.withdrawField.setEnabled(true);
        this.withdrawField.setText("$100");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Background
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop,0 ,0 , 176, 192);

    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int i = (mouseX - (this.width - this.xSize) / 2);
        int j = (mouseY - (this.height - this.ySize) / 2);

        fontRendererObj.drawString(I18n.format("tile.modcurrency:guiatm.name"), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 7, 100, Color.darkGray.getRGB());

        fontRendererObj.drawString(I18n.format("Balance: $10000"), 5,15, Color.darkGray.getRGB());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        withdrawField.drawTextBox();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }
}
