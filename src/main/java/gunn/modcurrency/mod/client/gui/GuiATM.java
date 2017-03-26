package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.client.container.ContainerATM;
import gunn.modcurrency.mod.core.data.BankAccount;
import gunn.modcurrency.mod.core.data.BankAccountSavedData;
import gunn.modcurrency.mod.core.network.PacketDepositToServer;
import gunn.modcurrency.mod.core.network.PacketHandler;
import gunn.modcurrency.mod.core.network.PacketItemSpawnToServer;
import gunn.modcurrency.mod.tile.TileATM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
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
    private TileATM te;
    private EntityPlayer player;

    public GuiATM(EntityPlayer player, TileATM te) {
        super(new ContainerATM(player, te));
        this.te = te;
        this.player = player;
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

        fontRendererObj.drawString(I18n.format("tile.modcurrency:blockatm.name"), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 7, 100, Color.darkGray.getRGB());


        fontRendererObj.drawString(I18n.format("Balance: $null"), 5,15, Color.darkGray.getRGB());

       //TODO fontRendererObj.drawString(I18n.format("Fee: $23"), 68, 40, Integer.parseInt("8c0000", 16));
       // fontRendererObj.drawString(I18n.format("Fee: $23"), 67, 40, Integer.parseInt("a71717", 16));
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
        if (((numChar >= 0 && numChar <= 9) || (keyCode == 203) || (keyCode == 205) ||
                (keyCode == 14 && withdrawField.getText().length() > 1) || (keyCode == 211 && withdrawField.getText().length() > 1))) { //Ensures keys input are only numbers or backspace type keys
            if (this.withdrawField.textboxKeyTyped(typedChar, keyCode)){

            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        BankAccount currAcc = BankAccountSavedData.getData(te.getWorld()).getBankAccount(player.getGameProfile().getId().toString());
        switch (button.id) {
            case 0:         //Deposit Button
                PacketDepositToServer pack0 = new PacketDepositToServer();
                pack0.setData(te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                System.out.println(BankAccountSavedData.getData(te.getWorld()).getBankAccount(player.getGameProfile().getId().toString()));
                break;
            case 1:         //Withdraw Button
                PacketDepositToServer pack1 = new PacketDepositToServer();
                pack1.setData(te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);

                BankAccountSavedData bankSaved = BankAccountSavedData.getData(te.getWorld());
                BankAccount bkk = bankSaved.getBankAccount(te.getPlayerUsing().getGameProfile().getId().toString());

                System.out.println(bkk);
                break;
        }
    }
}
