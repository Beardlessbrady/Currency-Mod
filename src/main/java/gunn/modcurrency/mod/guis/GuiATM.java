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
import net.minecraft.util.text.TextFormatting;

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

        this.buttonList.add(new GuiButton(0, i + 107, j + 51, 45, 20, "Deposit"));
        this.buttonList.add(new GuiButton(1, i + 21, j + 51, 48, 20, "Withdraw"));

        this.withdrawField = new GuiTextField(0, fontRendererObj, i + 65, j + 75, 46, 10);
        this.withdrawField.setMaxStringLength(6);
        this.withdrawField.setText("$");
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

        fontRendererObj.drawString(I18n.format("Balance: $0"), 5,15, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("Fee: $23"), 68, 40, Integer.parseInt("8c0000", 16));
        fontRendererObj.drawString(I18n.format("Fee: $23"), 67, 40, Integer.parseInt("a71717", 16));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        withdrawField.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        withdrawField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        System.out.println(withdrawField.getText().length());
        System.out.println(keyCode);
        if (((numChar >= 0 && numChar <= 9) || (keyCode == 203) || (keyCode == 205) ||
                (keyCode == 14 && withdrawField.getText().length() > 1) || (keyCode == 211 && withdrawField.getText().length() > 1))) { //Ensures keys input are only numbers or backspace type keys
            if (this.withdrawField.textboxKeyTyped(typedChar, keyCode)){

            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
}
