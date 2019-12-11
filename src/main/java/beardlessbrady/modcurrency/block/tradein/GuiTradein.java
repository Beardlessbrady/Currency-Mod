package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.vending.TileVending;
import beardlessbrady.modcurrency.network.PacketHandler;
import beardlessbrady.modcurrency.network.PacketSetFieldToServer;
import beardlessbrady.modcurrency.network.PacketSetItemToServer;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

import static beardlessbrady.modcurrency.block.TileEconomyBase.FIELD_MODE;
import static beardlessbrady.modcurrency.block.TileEconomyBase.FIELD_SELECTED;
import static beardlessbrady.modcurrency.block.vending.TileVending.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-07-31
 */

public class GuiTradein extends GuiContainer {
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/tradeingui.png");
    private static final ResourceLocation ASSET_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/guiassets.png");

    private TileTradein te;
    private GuiTextField fieldPrice, fieldAmnt, fieldItemMax, fieldTimeRestock;

    private static final int FIELDPRICE = 0;
    private static final int FIELDAMNT = 1;
    private static final int FIELDITEMMAX = 2;
    private static final int FIELDTIMERESTOCK = 3;

    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    public GuiTradein(EntityPlayer entityPlayer, TileTradein te) {
        super(new ContainerTradein(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttonList.add(new GuiButton(BUTTONCHANGE, i + 143, j + 39, 20, 20, "$"));

        String mode = (te.getField(FIELD_MODE) == 1) ? "STOCK" : "TRADE";
        buttonList.add(new GuiButton(BUTTONADMIN, i + 137, j - 42, 32, 20, mode));

        fieldPrice = new GuiTextField(FIELDPRICE, fontRenderer, 0, 0, 90, 8);        //Setting Costs
        fieldPrice.setTextColor(Integer.parseInt("204c96", 16));
        fieldPrice.setEnableBackgroundDrawing(false);
        fieldPrice.setMaxStringLength(7);
        fieldPrice.setEnabled(false);
        fieldPrice.setVisible(false);
        fieldPrice.setText("0.00");

        fieldAmnt = new GuiTextField(FIELDAMNT, fontRenderer, 0, 0, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt.setTextColor(Integer.parseInt("204c96", 16));
        fieldAmnt.setEnableBackgroundDrawing(false);
        fieldAmnt.setMaxStringLength(2);
        fieldAmnt.setEnabled(false);
        fieldAmnt.setVisible(false);
        fieldAmnt.setText("1");

        fieldItemMax = new GuiTextField(FIELDITEMMAX, fontRenderer, i - 66, j + 85, 90, 8);
        fieldItemMax.setTextColor(Integer.parseInt("BEA63D", 16));
        fieldItemMax.setEnableBackgroundDrawing(false);
        fieldItemMax.setMaxStringLength(3);
        fieldItemMax.setEnabled(false);
        fieldItemMax.setVisible(false);
        fieldItemMax.setText("1");

        fieldTimeRestock = new GuiTextField(FIELDTIMERESTOCK, fontRenderer, i - 63, j + 75, 90, 8);
        fieldTimeRestock.setTextColor(Integer.parseInt("BEA63D", 16));
        fieldTimeRestock.setEnableBackgroundDrawing(false);
        fieldTimeRestock.setMaxStringLength(4);
        fieldTimeRestock.setEnabled(false);
        fieldTimeRestock.setVisible(false);
        fieldTimeRestock.setText("1");

        GlStateManager.color(0xFF, 0xFF, 0xFF);

        updateTextField();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        fieldPrice.drawTextBox();
        fieldAmnt.drawTextBox();
        fieldItemMax.drawTextBox();
        fieldTimeRestock.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACK_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);

        //Admin Tag
        if(te.getField(FIELD_MODE) == 1) {

            //Tag
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
            drawTexturedModalRect(guiLeft - 107, guiTop + 5, 0, 208, 106, 48);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //Basics GUI labels
        fontRenderer.drawString(I18n.format("tile.modcurrency:blocktradein.name"), 8, -42, Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 87, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 18, 3, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 145, 3, Color.lightGray.getRGB());
        GlStateManager.color(0xFF, 0xFF, 0xFF);

        //Admin 'Price Tag' rendering
        drawAdminPanel();

        //Draws the red selection overlay when determining which slot is selected
        drawSelectionOverlay();
        
    }

    private void drawAdminPanel() {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        if (te.getField(TileVending.FIELD_MODE) == 1) {

            //NON BUNDLE PANEL
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), -74, 10, Integer.parseInt("ffffff", 16));

            String itemName = "[" + te.getSelectedName() + "]";
            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRenderer.drawString(I18n.format(itemName), -90 - (itemName.length() * 2), 28, Integer.parseInt("08285e", 16));
            GL11.glPopMatrix();

            fontRenderer.drawStringWithShadow(I18n.format("$"), -90, 30, Color.lightGray.getRGB());

            fieldPrice.x = i - 82;
            fieldPrice.y = j + 30;
            fieldPrice.setEnabled(true);
            fieldPrice.setVisible(true);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.amnt"), -90, 40, Color.lightGray.getRGB());
            fieldAmnt.x = i - 65;
            fieldAmnt.y = j + 40;
            fieldAmnt.setEnabled(true);
            fieldAmnt.setVisible(true);

            GlStateManager.color(0xFF, 0xFF, 0xFF);
        } else {
            fieldPrice.setEnabled(false);
            fieldPrice.setVisible(false);

            fieldAmnt.setEnabled(false);
            fieldAmnt.setVisible(false);

            fieldTimeRestock.setEnabled(false);
            fieldTimeRestock.setVisible(false);
            fieldItemMax.setEnabled(false);
            fieldItemMax.setVisible(false);
        }
    }

    private void drawSelectionOverlay() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        if (te.getField(TileTradein.FIELD_MODE) == 1) {
            int slotId = te.getField(FIELD_SELECTED);
            int slotColumn = 0, slotRow = 0;

            if (slotId >= 0 && slotId <= 4) {
                slotRow = slotId;
            } else if (slotId >= 5 && slotId <= 9) {
                slotColumn = 1;
                slotRow = (slotId) - 5;
            } else if (slotId >= 10 && slotId <= 14) {
                slotColumn = 2;
                slotRow = (slotId) - 10;
            } else if (slotId >= 15 && slotId <= 19) {
                slotColumn = 3;
                slotRow = (slotId) - 15;
            } else if (slotId >= 20 && slotId <= 24) {
                slotColumn = 4;
                slotRow = (slotId) - 20;
            }
            drawTexturedModalRect(42 + (18 * slotRow), -24 + (18 * slotColumn), 21, 172, 20, 20);
            drawTexturedModalRect(42 + (18 * slotRow) + 14, -24 + (18 * slotColumn) + 15, 83, 3, 16, 14);
        }
    }

    private void updateTextField(){
            fieldPrice.setText(UtilMethods.translateMoney(te.getItemTradein(te.getField(FIELD_SELECTED)).getCost()));
            fieldAmnt.setText(Integer.toString(te.getItemTradein(te.getField(FIELD_SELECTED)).getAmount()));
            fieldItemMax.setText(Integer.toString(te.getItemTradein(te.getField(FIELD_SELECTED)).getItemMax()));
            fieldTimeRestock.setText(Integer.toString(te.getItemTradein(te.getField(FIELD_SELECTED)).getTimeRaise()));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((te.getField(FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) { //Ensures keys input are only numbers or backspace type keys

            if ((!fieldPrice.getText().contains(".")) || keyCode != 52) {
                if (fieldPrice.textboxKeyTyped(typedChar, keyCode)) setPayout();
            }

            if (fieldPrice.getText().length() > 0)
                if (fieldPrice.getText().substring(fieldPrice.getText().length() - 1).equals("."))
                    fieldPrice.setMaxStringLength(fieldPrice.getText().length() + 2);
            if (!fieldPrice.getText().contains(".")) fieldPrice.setMaxStringLength(7);

            if (fieldAmnt.textboxKeyTyped(typedChar, keyCode))
                setAmnt(te.getField(FIELD_SELECTED), fieldAmnt);

            /*if (te.getField(FIELD_CREATIVE) == 1 && te.getField(FIELD_FINITE) == 1) {
                if (fieldItemMax.textboxKeyTyped(typedChar, keyCode))
                    setItemMax(fieldItemMax);

                if (fieldTimeRestock.textboxKeyTyped(typedChar, keyCode))
                    setTimeRestock(fieldTimeRestock);
            }*/
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            fieldPrice.mouseClicked(mouseX, mouseY, mouseButton);
            fieldAmnt.mouseClicked(mouseX, mouseY, mouseButton);
            fieldItemMax.mouseClicked(mouseX, mouseY, mouseButton);
            fieldTimeRestock.mouseClicked(mouseX, mouseY, mouseButton);

            updateTextField();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getField(FIELD_MODE) == 1) ? 0 : 1, FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                buttonList.get(BUTTONADMIN).displayString = (te.getField(FIELD_MODE) == 0) ? "STOCK" : "TRADE";

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            case BUTTONCHANGE:
             ///   PacketOutChangeToServer pack0 = new PacketOutChangeToServer();
             //   pack0.setData(te.getPos(), false);
             //   PacketHandler.INSTANCE.sendToServer(pack0);
              //  te.outChange(false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }

    private void setPayout() {
        if (fieldPrice.getText().length() > 0) {
            int newCost = 0;

            if (fieldPrice.getText().contains(".")) {
                if (fieldPrice.getText().lastIndexOf(".") + 1 != fieldPrice.getText().length()) {
                    if (fieldPrice.getText().lastIndexOf(".") + 2 == fieldPrice.getText().length()) {
                        newCost = Integer.parseInt(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1) + "0");
                    } else {
                        newCost = Integer.parseInt(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1));
                    }
                }

                if (fieldPrice.getText().lastIndexOf(".") != 0)
                    newCost += Integer.parseInt(fieldPrice.getText().substring(0, fieldPrice.getText().lastIndexOf("."))) * 100;

            } else {
                newCost = Integer.parseInt(fieldPrice.getText()) * 100;
            }

            te.getItemTradein(te.getField(FIELD_SELECTED)).setCost(newCost);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), newCost, PacketSetItemToServer.FIELD_COST, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void setAmnt(int slot, GuiTextField guiTextField) {
        if (guiTextField.getText().length() > 0) {
            int amount = Integer.parseInt(guiTextField.getText());

            if (te.getItemTradein(slot).getStack().isEmpty()) {
                amount = 1;
            } else if (Integer.parseInt(guiTextField.getText()) > te.getItemTradein(slot).getStack().getMaxStackSize())
                amount = te.getItemTradein(slot).getStack().getMaxStackSize();

            if (amount == 0) amount = 1;

            te.getItemTradein(slot).setAmount(amount);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), amount, PacketSetItemToServer.FIELD_AMOUNT, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }
}
