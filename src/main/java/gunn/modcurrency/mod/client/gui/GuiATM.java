package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.tileentity.TileATM;
import gunn.modcurrency.mod.container.ContainerATM;
import gunn.modcurrency.mod.worldsaveddata.bank.BankAccount;
import gunn.modcurrency.mod.worldsaveddata.bank.BankAccountSavedData;
import gunn.modcurrency.mod.network.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
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
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortabtexture.png");
    private GuiTextField withdrawField, feeField;
    private TileATM te;
    private EntityPlayer player;

    public GuiATM(EntityPlayer entityPlayer, TileATM tile) {
        super(new ContainerATM(entityPlayer, tile));
        te = tile;
        player = entityPlayer;
    }

    private void setFee() {
        if (te.getField(0)== 1) {
            if (this.feeField.getText().length() > 0) {
                int newFee = Integer.valueOf(this.feeField.getText());

                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData(newFee, 2, te.getPos());

                PacketHandler.INSTANCE.sendToServer(pack);
                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.withdrawField = new GuiTextField(0, fontRendererObj, i + 65, j + 75, 46, 10);
        this.withdrawField.setMaxStringLength(7);

        this.feeField = new GuiTextField(0, fontRendererObj, i-55, j + 48, 46, 10);
        this.feeField.setEnableBackgroundDrawing(false);
        this.feeField.setTextColor((Integer.parseInt("0099ff", 16)));
        this.feeField.setText(Integer.toString(te.getField(2)));

      //  tabList = new TabButtonList(this.buttonList, i - 21, j + 20);
        this.buttonList.add(new GuiButton(0, i + 107, j + 51, 45, 20, "Deposit"));
        this.buttonList.add(new GuiButton(1, i + 21, j + 51, 48, 20, "Withdraw"));

     //   if (te.getField(0) == 1) {
     //       tabList.addTab("Gear", TAB_TEXTURE, 0, 0, 2);
      //      tabList.setOpenState("Gear", 26);
     //   }
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
        fontRendererObj.drawString(I18n.format("tile.modcurrency:blockatm.name"), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 7, 100, Color.darkGray.getRGB());

        BankAccountSavedData bankData = BankAccountSavedData.getData(te.getWorld());
        BankAccount account = bankData.getBankAccount(player.getUniqueID().toString());
        fontRendererObj.drawString(I18n.format("Balance: $" + account.getBalance()), 5,15, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("$"), 57,76, Color.darkGray.getRGB());

        String text = withdrawField.getText();
        if (withdrawField.getText().length() > 0) {
            int amount = Integer.parseInt(text);

            if (amount > account.getBalance() && amount <= 6400) {
                this.withdrawField.setTextColor(14826556);
            }else this.withdrawField.setTextColor(15066597);
        }

        if(te.getField(2) != 0) {
            fontRendererObj.drawString(I18n.format("Fee: $" + te.getField(2)), 68, 40, Integer.parseInt("540909", 16));
            fontRendererObj.drawString(I18n.format("Fee: $" + te.getField(2)), 67, 40, Integer.parseInt("9B1A1A", 16));
        }

        if (te.getField(0) == 1) {
            drawIcons();
            if (te.getField(1) == 1) {
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guiatm.feesettings"), -81, 27, Integer.parseInt("42401c", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guiatm.feesettings"), -80, 26, Integer.parseInt("fff200", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guiatm.fee"), -84, 48, Integer.parseInt("211d1b", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guiatm.fee"), -83, 47, Color.lightGray.getRGB());
                fontRendererObj.drawString(I18n.format("$"), -62, 48, Integer.parseInt("0099ff", 16));

                this.feeField.setMaxStringLength(6);
            }
        }
    }

    private void drawIcons() {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);

      //  for (int k = 0; k < tabList.getSize(); k++) {
      //      int tabLoc = 22 * (k + 1);
      //      int offSet2 = 0;
    //        if (te.getField(1) == 1) {
       //         offSet2 = 26;
     //       }

      //      switch (k) {
      //          case 0:
     //               if (te.getField(1) == 1) {
      //                  drawTexturedModalRect(-91, tabLoc - 2, 27, 0, 91, 47);
      //              }
      //              drawTexturedModalRect(-19, tabLoc, 236, 19, 19, 16); //Icon
      //              break;
      //      }
      //  }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        withdrawField.drawTextBox();
        if(te.getField(0) == 1 && te.getField(1) == 1) feeField.drawTextBox();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        withdrawField.mouseClicked(mouseX, mouseY, mouseButton);
        feeField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if (withdrawField.isFocused()) {
            if ((numChar >= 0 && numChar <= 9) || (keyCode == 203) || (keyCode == 205) || (keyCode == 14 && withdrawField.getText().length() > 0) || (keyCode == 211 && withdrawField.getText().length() > 0)) { //Ensures keys input are only numbers or backspace type keys
                if (this.withdrawField.textboxKeyTyped(typedChar, keyCode)) {
                }
            } else super.keyTyped(typedChar, keyCode);
        }

        if (feeField.isFocused()) {
            if (((numChar >= 0 && numChar <= 9) || (keyCode == 203) || (keyCode == 205) || (keyCode == 14) || (keyCode == 211))) { //Ensures keys input are only numbers or backspace type keys
                if (this.feeField.textboxKeyTyped(typedChar, keyCode)) setFee();
            } else super.keyTyped(typedChar, keyCode);
        }

        if(!feeField.isFocused() && !withdrawField.isFocused()) super.keyTyped(typedChar, keyCode);
    }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:         //Deposit Button
                PacketBankDepositToServer pack = new PacketBankDepositToServer();
                pack.setData(te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);
                break;
            case 1:         //Withdraw Button
                String text = withdrawField.getText();
                if (text.length() != 0) {
                    int amount = Integer.parseInt(text);

                    PacketBankWithdrawToServer pack1 = new PacketBankWithdrawToServer();
                    pack1.setData(te.getPos(), amount);
                    PacketHandler.INSTANCE.sendToServer(pack1);
                }
                break;
            case 2:
              //  tabList.checkOpenState("Gear", te.getField(1) == 0);
                int newGear = te.getField(1) == 1 ? 0 : 1;
                PacketSetFieldToServer pack2 = new PacketSetFieldToServer();
                pack2.setData(newGear, 1, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2);
                break;
        }
    }
}
