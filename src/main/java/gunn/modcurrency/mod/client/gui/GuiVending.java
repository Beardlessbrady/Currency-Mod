package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.client.gui.util.TabButton;
import gunn.modcurrency.mod.container.ContainerVending;
import gunn.modcurrency.mod.item.ModItems;
import gunn.modcurrency.mod.network.*;
import gunn.modcurrency.mod.tileentity.TileVending;
import gunn.modcurrency.mod.utils.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import javax.xml.soap.Text;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-09
 */
public class GuiVending extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortabtexture.png");
    private TileVending tile;
    private boolean creativeExtended;
    private EntityPlayer player;
    private GuiTextField priceField,multiPriceField1a, multiPriceField1b, multiPriceField2a, multiPriceField2b, multiPriceField3a, multiPriceField3b;

    private static final int CHANGEBUTTON_ID = 0;
    private static final int INFINITEBUTTON_ID = 1;
    private static final int OUTPUTBUTTON_ID = 2;
    private static final int MODE_ID = 3;
    private static final int LOCK_ID = 4;
    private static final int GEAR_ID = 5;
    private static final int CREATIVE_ID = 6;
    private static final int ADD1_ID = 7;
    private static final int ADD2_ID = 8;

    private final int yShift = 8;

    public GuiVending(EntityPlayer entityPlayer, TileVending te) {
        super(new ContainerVending(entityPlayer.inventory, te));
        tile = te;
        xSize = 176;
        ySize = 235;
        creativeExtended = false;
        player = entityPlayer;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        String ChangeButton = "Change";
        if(tile.getField(tile.FIELD_MODE) == 1) ChangeButton = "Profit";

        this.buttonList.add(new GuiButton(CHANGEBUTTON_ID, i + 103, j + 7, 45, 20, ChangeButton));
        this.buttonList.add(new GuiButton(INFINITEBUTTON_ID, i + 198, j + 85, 45, 20, "BORKED"));
        this.buttonList.add(new TabButton("Output", OUTPUTBUTTON_ID, i + 10, j + 116 + (yShift * tile.getField(tile.FIELD_TWOBLOCK)), 202, 34, 22,12,23,"", BACKGROUND_TEXTURE));
        this.buttonList.add(new TabButton("Mode", MODE_ID, i - 20, j + 20, 0, 88, 20, 21, 0,"", TAB_TEXTURE));
        this.buttonList.add(new TabButton("Lock", LOCK_ID, i - 20, 22 + ((TabButton) this.buttonList.get(MODE_ID)).getButtonY(), 0, 22, 20, 21, 0,"", TAB_TEXTURE));
        this.buttonList.add(new TabButton("Gear", GEAR_ID, i - 20, 22 + ((TabButton) this.buttonList.get(LOCK_ID)).getButtonY(), 0, 0, 20, 21, 0,"", TAB_TEXTURE));
        this.buttonList.add(new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton) this.buttonList.get(GEAR_ID)).getButtonY(), 0, 44, 20, 21, 0,"", TAB_TEXTURE));
        this.buttonList.add(new GuiButton(ADD1_ID, i - 100, j + 110, 10, 10, "+"));
        this.buttonList.add(new GuiButton(ADD2_ID, i - 100, j + 120, 10, 10, "+"));

        priceField = new GuiTextField(0, fontRenderer, i - 48, j + 91, 45 + 40, 10);        //Setting Costs
        priceField.setTextColor(Integer.parseInt("0099ff", 16));
        priceField.setEnableBackgroundDrawing(false);
        priceField.setMaxStringLength(7);
        priceField.setEnabled(false);

        this.buttonList.get(MODE_ID).visible = false;
        this.buttonList.get(LOCK_ID).visible = false;
        this.buttonList.get(GEAR_ID).visible = false;
        this.buttonList.get(CREATIVE_ID).visible = false;
        this.buttonList.get(INFINITEBUTTON_ID).visible = false;
        this.buttonList.get(OUTPUTBUTTON_ID).visible = false;

        this.buttonList.get(ADD1_ID).visible = false;
        this.buttonList.get(ADD2_ID).visible = false;


        if(tile.getField(tile.FIELD_UPGRADEMULTI) == 1){
            multiPriceField1a = new GuiTextField(0, fontRenderer, i - 78, j + 101, 26, 10);
            multiPriceField1a.setMaxStringLength(3);
            multiPriceField1a.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField1a.setEnableBackgroundDrawing(false);
            multiPriceField1a.setEnabled(false);

            multiPriceField1b = new GuiTextField(0, fontRenderer, i - 48, j + 101, 50, 10);
            multiPriceField1b.setMaxStringLength(7);
            multiPriceField1b.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField1b.setEnableBackgroundDrawing(false);
            multiPriceField1b.setEnabled(false);

            multiPriceField2a = new GuiTextField(0, fontRenderer, i - 78, j + 111, 26, 10);
            multiPriceField2a.setMaxStringLength(3);
            multiPriceField2a.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField2a.setEnableBackgroundDrawing(false);
            multiPriceField2a.setEnabled(false);

            multiPriceField2b = new GuiTextField(0, fontRenderer, i - 48, j + 111, 50, 10);
            multiPriceField2b.setMaxStringLength(7);
            multiPriceField2b.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField2b.setEnableBackgroundDrawing(false);
            multiPriceField2b.setEnabled(false);

            multiPriceField3a = new GuiTextField(0, fontRenderer, i - 78, j + 121, 26, 10);
            multiPriceField3a.setMaxStringLength(3);
            multiPriceField3a.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField3a.setEnableBackgroundDrawing(false);
            multiPriceField3a.setEnabled(false);

            multiPriceField3b = new GuiTextField(0, fontRenderer, i - 48, j + 121, 50, 10);
            multiPriceField3b.setMaxStringLength(7);
            multiPriceField3b.setTextColor(Integer.parseInt("0099ff", 16));
            multiPriceField3b.setEnableBackgroundDrawing(false);
            multiPriceField3b.setEnabled(false);
        }
    }

    private void setCost(int number) {
        if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) { //If normal prices
            if (this.priceField.getText().length() > 0) {
                int newCost = 0;

                if (priceField.getText().contains(".")) {
                    if (priceField.getText().lastIndexOf(".") + 1 != priceField.getText().length()) {
                        if (priceField.getText().lastIndexOf(".") + 2 == priceField.getText().length()) {
                            newCost = Integer.valueOf(this.priceField.getText().substring(priceField.getText().lastIndexOf(".") + 1) + "0");
                        } else {
                            newCost = Integer.valueOf(this.priceField.getText().substring(priceField.getText().lastIndexOf(".") + 1));
                        }
                    }

                    if (priceField.getText().lastIndexOf(".") != 0)
                        newCost += Integer.valueOf(this.priceField.getText().substring(0, priceField.getText().lastIndexOf("."))) * 100;

                } else {
                    newCost = Integer.valueOf(this.priceField.getText()) * 100;
                }

                tile.setItemCost(newCost);
                PacketSetItemCostToServer pack = new PacketSetItemCostToServer();
                pack.setData(newCost, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
            }
        }else{ //If MULTI PRICES enabled
            GuiTextField textField = multiPriceField1b;
            int slot = 3;

            switch(number){
                case 2:
                    textField = multiPriceField2b;
                    slot = 4;
                    break;
                case 3:
                    textField = multiPriceField3b;
                    slot = 5;
                    break;
            }

            if (textField.getText().length() > 0) {
                int newCost = 0;

                if (textField.getText().contains(".")) {
                    if (textField.getText().lastIndexOf(".") + 1 != textField.getText().length()) {
                        if (textField.getText().lastIndexOf(".") + 2 == textField.getText().length()) {
                            newCost = Integer.valueOf(textField.getText().substring(textField.getText().lastIndexOf(".") + 1) + "0");
                        } else {
                            newCost = Integer.valueOf(textField.getText().substring(textField.getText().lastIndexOf(".") + 1));
                        }
                    }

                    if (textField.getText().lastIndexOf(".") != 0)
                        newCost += Integer.valueOf(textField.getText().substring(0, textField.getText().lastIndexOf("."))) * 100;

                } else {
                    newCost = Integer.valueOf(textField.getText()) * 100;
                }
                int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37).clone();
                prices[slot] = newCost;

                tile.setMultiPrices(prices.clone());
                PacketSetItemMultiPricesToServer pack = new PacketSetItemMultiPricesToServer();
                pack.setData(prices.clone(), tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
            }
        }
    }

    private void setAmnt(int number){
        GuiTextField textField = multiPriceField1a;
        int slot = 0;

        switch(number) {
            case 2:
                slot = 1;
                textField = multiPriceField2a;
                break;
            case 3:
                 slot = 2;
                textField = multiPriceField3a;
                break;
        }

        if (textField.getText().length() > 0) {
            int newAmnt = Integer.valueOf(textField.getText());
            if (newAmnt > tile.getField(tile.FIELD_LIMIT)) newAmnt = tile.getField(tile.FIELD_LIMIT);

            int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37).clone();
            prices[slot] = newAmnt;

            tile.setMultiPrices(prices.clone());
            PacketSetItemMultiPricesToServer pack = new PacketSetItemMultiPricesToServer();
            pack.setData(prices.clone(), tile.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    private void updateTextField() {
        if (tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
            priceField.setText(UtilMethods.translateMoney(tile.getItemCost(tile.getField(tile.FIELD_SELECTSLOT) - 37)));
        } else {
            String multiPrice1a = String.valueOf(tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37)[0]);
            if (multiPrice1a.length() > 0) {
                this.multiPriceField1a.setText(multiPrice1a);
            }

            int[] multiPriceIndex = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37).clone();
            multiPriceField1b.setText(UtilMethods.translateMoney(multiPriceIndex[3]));

            if(multiPriceIndex[1] != -1){
                String multiPrice2a = String.valueOf(tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37)[1]);
                if (multiPrice2a.length() > 0) {
                    this.multiPriceField2a.setText(multiPrice2a);
                }

                multiPriceField2b.setText(UtilMethods.translateMoney(multiPriceIndex[4]));
            }

            if(multiPriceIndex[2] != -1){
                String multiPrice3a = String.valueOf(tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37)[2]);
                if (multiPrice3a.length() > 0) {
                    this.multiPriceField3a.setText(multiPrice3a);
                }

                multiPriceField3b.setText(UtilMethods.translateMoney(multiPriceIndex[5]));
            }
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        PacketSetFieldToServer pack = new PacketSetFieldToServer();
        pack.setData(0, 8, tile.getPos());
        PacketHandler.INSTANCE.sendToServer(pack);

        creativeExtended = false;
    }

    //<editor-fold desc="Draw and Renders">
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        this.renderHoveredToolTip(mouseX,mouseY);
        if (tile.getField(tile.FIELD_GEAREXT) == 1 && tile.getField(tile.FIELD_MODE) == 1){
            if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
                priceField.drawTextBox();
            }else{
                multiPriceField1a.drawTextBox();
                multiPriceField1b.drawTextBox();

                int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);


                if(prices[1] != -1) {
                    multiPriceField2a.drawTextBox();
                    multiPriceField2b.drawTextBox();
                }
                if(prices[2] != -1) {
                    multiPriceField3a.drawTextBox();
                    multiPriceField3b.drawTextBox();
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if(tile.getField(tile.FIELD_TWOBLOCK) == 1){
            drawTexturedModalRect(guiLeft + 43, guiTop + 31, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 103, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 121, 7, 210, 90, 18);
        }
        drawTexturedModalRect(guiLeft + 43, guiTop + 49, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 67, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 85, 7, 210, 90, 18);

        //Draw Input Icons
        if (tile.getField(tile.FIELD_MODE) == 0) {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 0, 15, 15);
        } else {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 215, 0, 15, 15);
            //Draw Buffer Slot Backgrounds
            int y = 33;
            if(tile.getField(tile.FIELD_TWOBLOCK) == 1) y = 41;
            drawTexturedModalRect(guiLeft + 10, guiTop + y, 178, 34, 22, 82);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;


        if (tile.getOwner().equals(player.getUniqueID().toString()) || player.isCreative()) {  //If owner or creative
            this.buttonList.get(MODE_ID).visible = true;

            if(tile.getField(tile.FIELD_MODE) == 1) { //If in EDIT mode
                this.buttonList.get(LOCK_ID).visible = true;
                this.buttonList.get(GEAR_ID).visible = true;
                this.buttonList.get(OUTPUTBUTTON_ID).visible = true;

                //Hiding/Revealing slots in EDIT mode
                this.inventorySlots.getSlot(((ContainerVending)this.inventorySlots).TE_MONEY_FIRST_SLOT_INDEX).xPos= -1000;
                for (int k = 0; k < ((ContainerVending) this.inventorySlots).TE_BUFFER_COUNT; k++){
                    this.inventorySlots.getSlot(((ContainerVending)this.inventorySlots).TE_BUFFER_FIRST_SLOT_INDEX+ k).xPos= 13;
                }

                if(tile.getField(tile.FIELD_UPGRADEMULTI) == 1){
                    ((TabButton) buttonList.get(GEAR_ID)).setOpenState(tile.getField(tile.FIELD_GEAREXT) == 1, 49);   //Set Gear Tab Open state
                }else {
                    ((TabButton) buttonList.get(GEAR_ID)).setOpenState(tile.getField(tile.FIELD_GEAREXT) == 1, 26);   //Set Gear Tab Open state
                }

                if(((TabButton)buttonList.get(GEAR_ID)).openState()){   //If Gear Tab Opened
                    if(tile.getField(tile.FIELD_UPGRADEMULTI) == 1){
                        drawTexturedModalRect(-91, 64, 27, 0, 91, 43);
                        drawTexturedModalRect(-91, 92, 27, 5, 91, 43);

                        int[] prices1 = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);

                        if(prices1[1] == -1){
                            this.buttonList.get(ADD1_ID).visible = true;
                            this.buttonList.get(ADD1_ID).displayString = "+";

                            this.buttonList.get(ADD2_ID).visible = false;
                        }else{
                            if(prices1[2] == -1){
                                this.buttonList.get(ADD2_ID).visible = true;
                                this.buttonList.get(ADD2_ID).displayString = "+";

                                this.buttonList.get(ADD1_ID).visible = true;
                                this.buttonList.get(ADD1_ID).displayString = "-";
                            }else{
                                this.buttonList.get(ADD2_ID).visible = true;
                                this.buttonList.get(ADD2_ID).displayString = "-";

                                this.buttonList.get(ADD1_ID).visible = false;
                            }
                        }

                    }else {
                        drawTexturedModalRect(-91, 64, 27, 0, 91, 47);
                    }



                    drawSelectionOverlay();

                    if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
                        this.priceField.setEnabled(true);
                    }else{
                        int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);

                        multiPriceField1a.setVisible(true);
                        multiPriceField1a.setEnabled(true);
                        multiPriceField1b.setEnabled(true);
                        multiPriceField1b.setVisible(true);

                        if(prices[1] != -1) {
                            multiPriceField2a.setEnabled(true);
                            multiPriceField2a.setVisible(true);
                            multiPriceField2b.setEnabled(true);
                            multiPriceField2b.setVisible(true);

                            this.buttonList.get(ADD2_ID).visible = true;
                        }else{
                            this.buttonList.get(ADD2_ID).visible = false;
                        }

                        if(prices[2] != -1) {
                            multiPriceField3a.setEnabled(true);
                            multiPriceField3a.setVisible(true);
                            multiPriceField3b.setEnabled(true);
                            multiPriceField3b.setVisible(true);
                        }
                    }



                    Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                    this.buttonList.set(CREATIVE_ID, new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton)this.buttonList.get(GEAR_ID)).getButtonY(),0, 44, 20, 21, 0,"", TAB_TEXTURE));
                }else{  //If Gear Tab Closed
                    this.buttonList.get(ADD1_ID).visible = false;
                    this.buttonList.get(ADD2_ID).visible = false;
                    if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
                        this.priceField.setEnabled(false);
                    }
                    this.buttonList.set(CREATIVE_ID, new TabButton("Creative", CREATIVE_ID, i - 20, 22 + ((TabButton)this.buttonList.get(GEAR_ID)).getButtonY(),0, 44, 20, 21, 0,"", TAB_TEXTURE));
                }

                if(player.isCreative()){    //If in Creative
                    this.buttonList.get(CREATIVE_ID).visible = true;

                    ((TabButton)buttonList.get(CREATIVE_ID)).setOpenState(creativeExtended, 26 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY());
                    if(((TabButton)buttonList.get(CREATIVE_ID)).openState()){
                        this.buttonList.set(INFINITEBUTTON_ID, (new GuiButton(INFINITEBUTTON_ID, i - 69, j + 106 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), 45, 20, ((tile.getField(tile.FIELD_INFINITE) == 1) ? "Enabled" : "Disabled"))));
                        drawTexturedModalRect(-91, 86 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), 27, 48, 91, 47);
                    }else  this.buttonList.get(INFINITEBUTTON_ID).visible = false;
                }else{  //If in Survival
                    this.buttonList.get(CREATIVE_ID).visible = false;
                    this.buttonList.get(INFINITEBUTTON_ID).visible = false;

                }

                //Render Output Button Icon
                if(tile.getField(tile.FIELD_OUTPUTBILL) <= 5){
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemCoin,1, tile.getField(tile.FIELD_OUTPUTBILL)), 13, 114 + (yShift * tile.getField(tile.FIELD_TWOBLOCK)));
                }else{
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, tile.getField(tile.FIELD_OUTPUTBILL) - 5), 13, 115 + (yShift * tile.getField(tile.FIELD_TWOBLOCK)));
                }

            }else{  //In SELL mode
                this.buttonList.get(LOCK_ID).visible = false;
                this.buttonList.get(GEAR_ID).visible = false;
                this.buttonList.get(CREATIVE_ID).visible = false;
                this.buttonList.get(INFINITEBUTTON_ID).visible = false;
                this.buttonList.get(OUTPUTBUTTON_ID).visible = false;

                //Hiding/Revealing slots in SELL mode
                this.inventorySlots.getSlot(((ContainerVending)this.inventorySlots).TE_MONEY_FIRST_SLOT_INDEX).xPos= 152;
                for (int k = 0; k < ((ContainerVending) this.inventorySlots).TE_BUFFER_COUNT; k++){
                    this.inventorySlots.getSlot(((ContainerVending)this.inventorySlots).TE_BUFFER_FIRST_SLOT_INDEX + k).xPos= -1000;
                }
            }
            Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
            drawIcons();
        }
        warnings();
        drawText();
        //drawItemStackSize(); zLevels are the devil
    }

    private void drawText(){
        fontRenderer.drawString(I18n.format("tile.modcurrency:blockvending.name"), 5, 6, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 4, 142, Color.darkGray.getRGB());
        if (tile.getField(tile.FIELD_MODE) == 1){
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.profit") + ": $" + UtilMethods.translateMoney(tile.getLong(tile.LONG_PROFIT)), 5, 15, Color.darkGray.getRGB());
        }else{
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.cash") + ": $" + UtilMethods.translateMoney(tile.getLong(tile.LONG_BANK)), 5, 15, Color.darkGray.getRGB());
        }

        if (tile.getField(tile.FIELD_GEAREXT) == 1) {
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.slotsettings"), -81, 71, Integer.parseInt("42401c", 16));
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.slotsettings"), -80, 70, Integer.parseInt("fff200", 16));

            if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
                fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.cost"), -84, 92, Integer.parseInt("211d1b", 16));
                fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.cost"), -83, 91, Color.lightGray.getRGB());
                fontRenderer.drawString(I18n.format("$"), -55, 91, Integer.parseInt("0099ff", 16));
            }else{
                fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.costs"), -84, 92, Integer.parseInt("211d1b", 16));
                fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.costs"), -83, 91, Color.lightGray.getRGB());

                fontRenderer.drawString(I18n.format("$"), -54, 101, Integer.parseInt("0099ff", 16));
                fontRenderer.drawString(I18n.format("x"), -84, 101, Integer.parseInt("0099ff", 16));

                int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);

                if(prices[1] != -1){
                    fontRenderer.drawString(I18n.format("$"), -54, 111, Integer.parseInt("0099ff", 16));
                    fontRenderer.drawString(I18n.format("x"), -84, 111, Integer.parseInt("0099ff", 16));
                }
                if(prices[2] != -1){
                    fontRenderer.drawString(I18n.format("$"), -54, 121, Integer.parseInt("0099ff", 16));
                    fontRenderer.drawString(I18n.format("x"), -84, 121, Integer.parseInt("0099ff", 16));
                }
            }

            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRenderer.drawString(I18n.format("[" + tile.getSelectedName() + "]"), -117, 115, Integer.parseInt("001f33", 16));
            fontRenderer.drawString(I18n.format("[" + tile.getSelectedName() + "]"), -118, 114, Integer.parseInt("0099ff", 16));
            GL11.glPopMatrix();
        }
        if (creativeExtended) {
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.infinitestock"), -86, 93 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), Integer.parseInt("42401c", 16));
            fontRenderer.drawString(I18n.format("tile.modcurrency:guivending.infinitestock"), -85, 92 + ((TabButton)this.buttonList.get(GEAR_ID)).openExtY(), Integer.parseInt("fff200", 16));
        }

        if (tile.getField(tile.FIELD_WALLETIN) == 1) fontRenderer.drawString(I18n.format("Wallet") + ": $" + UtilMethods.translateMoney(tile.getLong(tile.LONG_WALLETTOTAL)), 5, 23, Integer.parseInt("3abd0c", 16));
    }

    private void drawIcons() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        int size = 1;
        if(tile.getField(tile.FIELD_MODE) == 1){
            size = 3;
            if(tile.getField(tile.FIELD_CREATIVE) == 1) size = 4;
        }

        for (int k = 0; k < size; k++) {
            int tabLoc = 22 * (k + 1);
            int offSet2 = 0;
            if (tile.getField(tile.FIELD_GEAREXT) == 1){
                if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0){
                    offSet2 = 26;
                }else{
                    offSet2 = 49;
                }
            }

            switch (k) {
                case 0:
                    drawTexturedModalRect(-19, tabLoc, 236, 73, 19, 16);
                    break;
                case 1:
                    if (tile.getField(tile.FIELD_LOCKED) == 0) {
                        drawTexturedModalRect(-19, tabLoc, 236, 1, 19, 16);
                    }else drawTexturedModalRect(-19, tabLoc, 216, 1, 19, 16);
                    break;
                case 2:
                    drawTexturedModalRect(-19, tabLoc, 236, 19, 19, 16); //Icon
                    break;
                case 3:
                    drawTexturedModalRect(-19, tabLoc + offSet2, 236, 37, 19, 16);
                    break;
            }
        }
    }

    private void warnings(){
        if(tile.getField(tile.FIELD_LOCKED) == 1){
            if(tile.getWorld().getBlockState(tile.getPos().down()).getBlock() == Blocks.HOPPER){
            //todo

            }
        }

       // if(!this.tile.getBufferStackHandler().getStackInSlot(0).isEmpty() )
        //    drawTexturedModalRect(151, 30, 236, 109, 19, 16);

    }

    private void drawSelectionOverlay() {
        if (tile.getField(tile.FIELD_GEAREXT) == 1) {
            int slotId = tile.getField(tile.FIELD_SELECTSLOT) -37;
            int slotColumn, slotRow;

            if (slotId >= 0 && slotId <= 4) {
                slotColumn = 0;
                slotRow = slotId + 1;
            } else if (slotId >= 5 && slotId <= 9) {
                slotColumn = 1;
                slotRow = (slotId + 1) - 5;
            } else if (slotId >= 10 && slotId <= 14) {
                slotColumn = 2;
                slotRow = (slotId + 1) - 10;
            } else if (slotId >= 15 && slotId <= 19) {
                slotColumn = 3;
                slotRow = (slotId + 1) - 15;
            } else if (slotId >= 20 && slotId <= 24) {
                slotColumn = 4;
                slotRow = (slotId + 1) - 20;
            } else {
                slotColumn = 5;
                slotRow = (slotId + 1) - 25;
            }

            if(tile.getField(tile.FIELD_TWOBLOCK) != 1) slotColumn++;

            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect(24 + (18 * slotRow), 30 + (18 * slotColumn), 177, 0, 20, 20); //Selection Box
        }
    }

    private void drawItemStackSize(){
        GlStateManager.enableDepth();
        this.zLevel = 300.0F;

        this.fontRenderer.drawStringWithShadow("Test", 50, 50, -1);
        this.zLevel=0.0F;
        GlStateManager.disableDepth();

    }


    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (this.width - this.xSize) / 2);
        int j = (y - (this.height - this.ySize) / 2);

        if(j < 140 && j > 30 && i >= 43) {
            int startX = 43;
            int startY = 31;
            int row = ((i - startX) / 18);
            int column = ((j - startY) / 18);
            int slot = row + (column * 5);
            if(tile.getField(tile.FIELD_TWOBLOCK) != 1)slot = slot -5;
            List<String> list = new ArrayList<>();
            List<String> ogTooltip = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int tooltipStart = 1;


            //Adding name and subname of item before price and such
            if(ogTooltip.size()>0){
                list.add(ogTooltip.get(0));
                list.set(0, stack.getRarity().rarityColor + (String)list.get(0));
            }
            if(ogTooltip.size()>1) if(ogTooltip.get(1) != ""){
                list.add(TextFormatting.GRAY + ogTooltip.get(1));
                tooltipStart = 2;
            }

            //Adding Vending Strings
            TextFormatting color = TextFormatting.YELLOW;
            if(tile.getField(tile.FIELD_MODE) == 0) {
                if (!tile.canAfford(slot)) {
                    color = TextFormatting.RED;
                } else {
                    color = TextFormatting.GREEN;
                }
            }

            if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {
                list.add(color + "Price: $" + UtilMethods.translateMoney(tile.getItemCost(slot)));
            }else {
                int[] prices = tile.getMultiPrices(slot);

                list.add(TextFormatting.GRAY + "Prices:");
                list.add(color + "x" + prices[0] + " for " + "$" + UtilMethods.translateMoney(prices[3]));
                if(prices[1] > 0) list.add(color + "x" + prices[1] + " for " + "$" + UtilMethods.translateMoney(prices[4]));
                if(prices[2] > 0) list.add(color + "x" + prices[2] + " for " + "$" + UtilMethods.translateMoney(prices[5]));
            }




            if(tile.getItemSize(slot) > 0){
                list.add(TextFormatting.BLUE + "Stock: " + Integer.toString(tile.getItemSize(slot)));
            }else{
                list.add(TextFormatting.RED + "OUT OF STOCK");
            }


            //adding original extra stuff AFTER price and such
            for(; tooltipStart < stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).size(); tooltipStart++) {
                list.add(TextFormatting.GRAY + stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(tooltipStart));
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }else{
            super.renderToolTip(stack, x, y);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Keyboard and Mouse Inputs">
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(tile.getField(tile.FIELD_MODE) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            priceField.mouseClicked(mouseX, mouseY, mouseButton);


            if(tile.getField(tile.FIELD_UPGRADEMULTI) == 1) {
                int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);

                multiPriceField1a.mouseClicked(mouseX, mouseY, mouseButton);
                multiPriceField1b.mouseClicked(mouseX, mouseY, mouseButton);
                if(prices[1] != -1) {
                    multiPriceField2a.mouseClicked(mouseX, mouseY, mouseButton);
                    multiPriceField2b.mouseClicked(mouseX, mouseY, mouseButton);
                }
                if(prices[2] != -1) {
                    multiPriceField3a.mouseClicked(mouseX, mouseY, mouseButton);
                    multiPriceField3b.mouseClicked(mouseX, mouseY, mouseButton);
                }
            }

            if (tile.getField(tile.FIELD_GEAREXT) == 1 && mouseButton == 0) updateTextField();
        }else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((tile.getField(tile.FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) { //Ensures keys input are only numbers or backspace type keys
         if(tile.getField(tile.FIELD_UPGRADEMULTI) == 0) {

             if ((keyCode == 52 && !priceField.getText().contains(".")) || keyCode != 52) {
                 if (this.priceField.textboxKeyTyped(typedChar, keyCode)) setCost(0);
             }

             if (priceField.getText().length() > 0) if (priceField.getText().substring(priceField.getText().length() - 1).equals(".")) priceField.setMaxStringLength(priceField.getText().length() + 2);
             if (!priceField.getText().contains(".")) priceField.setMaxStringLength(7);
         }else {

             if ((keyCode == 52 && !multiPriceField1b.getText().contains(".")) || keyCode != 52) {
                 if (this.multiPriceField1b.textboxKeyTyped(typedChar, keyCode)) setCost(1);
             }

             if (this.multiPriceField1a.textboxKeyTyped(typedChar, keyCode)) setAmnt(1);

             if (multiPriceField1b.getText().length() > 0) if (multiPriceField1b.getText().substring(multiPriceField1b.getText().length() - 1).equals(".")) multiPriceField1b.setMaxStringLength(multiPriceField1b.getText().length() + 2);
             if (!multiPriceField1b.getText().contains(".")) multiPriceField1b.setMaxStringLength(7);

             if(tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37)[1] != -1){ //If MultPrices price 2 is active
                 if ((keyCode == 52 && !multiPriceField2b.getText().contains(".")) || keyCode != 52) {
                    if (this.multiPriceField2b.textboxKeyTyped(typedChar, keyCode)) setCost(2);
                 }

                 if (this.multiPriceField2a.textboxKeyTyped(typedChar, keyCode)) setAmnt(2);

                 if (multiPriceField2b.getText().length() > 0) if (multiPriceField2b.getText().substring(multiPriceField2b.getText().length() - 1).equals(".")) multiPriceField2b.setMaxStringLength(multiPriceField2b.getText().length() + 2);
                 if (!multiPriceField2b.getText().contains(".")) multiPriceField2b.setMaxStringLength(7);
             }

             if(tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37)[2] != -1){ //If MultiPrices price 3 is active
                 if ((keyCode == 52 && !multiPriceField3b.getText().contains(".")) || keyCode != 52) {
                     if (this.multiPriceField3b.textboxKeyTyped(typedChar, keyCode)) setCost(3);
                 }

                   if (this.multiPriceField3a.textboxKeyTyped(typedChar, keyCode)) setAmnt(3);

                 if (multiPriceField3b.getText().length() > 0) if (multiPriceField3b.getText().substring(multiPriceField3b.getText().length() - 1).equals(".")) multiPriceField3b.setMaxStringLength(multiPriceField3b.getText().length() + 2);
                 if (!multiPriceField3b.getText().contains(".")) multiPriceField3b.setMaxStringLength(7);
             }
         }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    //Button Actions
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case CHANGEBUTTON_ID:  //Change Button
                PacketItemSpawnToServer pack0 = new PacketItemSpawnToServer();
                pack0.setBlockPos(tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case INFINITEBUTTON_ID: //Infinite? Button
                PacketSetFieldToServer pack1 = new PacketSetFieldToServer();
                pack1.setData((tile.getField(tile.FIELD_INFINITE) == 1) ? 0 : 1, tile.FIELD_INFINITE, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);
                break;
            case MODE_ID: //Mode Button
                PacketSetFieldToServer pack2 = new PacketSetFieldToServer();
                pack2.setData((tile.getField(tile.FIELD_MODE) == 1) ? 0 : 1, tile.FIELD_MODE, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2);

                creativeExtended = false;

                tile.setField(tile.FIELD_GEAREXT, 0);
                PacketSetFieldToServer pack2b = new PacketSetFieldToServer();
                pack2b.setData(0, tile.FIELD_GEAREXT, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2b);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case LOCK_ID: //Lock Button
                PacketSetFieldToServer pack3 = new PacketSetFieldToServer();
                pack3.setData((tile.getField(tile.FIELD_LOCKED) == 1) ? 0 : 1, tile.FIELD_LOCKED, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack3);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case GEAR_ID: //Gear Button
                int newGear = tile.getField(tile.FIELD_GEAREXT) == 1 ? 0 : 1;
                tile.setField(tile.FIELD_GEAREXT, newGear);
                PacketSetFieldToServer pack4 = new PacketSetFieldToServer();
                pack4.setData(newGear, tile.FIELD_GEAREXT, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack4);
                break;
            case CREATIVE_ID: //Creative Button
                creativeExtended = !creativeExtended;
                break;
            case OUTPUTBUTTON_ID: //Output Button
                int out = tile.getField(tile.FIELD_OUTPUTBILL);
                if(out < 10) {
                    out++;
                }else out = 0;

                tile.setField(tile.FIELD_OUTPUTBILL, out);
                PacketSetFieldToServer packOut = new PacketSetFieldToServer();
                packOut.setData(out, tile.FIELD_OUTPUTBILL, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(packOut);
                break;
            case ADD1_ID: //Add Button for Multi Prices 2
                int[] prices = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);
                if(this.buttonList.get(ADD1_ID).displayString.equals("+")) {
                    prices[1] = 0;
                    this.buttonList.get(ADD1_ID).displayString = "-";
                }else{
                    prices[1] = -1;
                    prices[4] = 0;
                    this.buttonList.get(ADD1_ID).displayString = "+";
                }
                tile.setMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37, prices.clone());

                PacketSetItemMultiPricesToServer pack = new PacketSetItemMultiPricesToServer();
                pack.setData(prices, this.tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case ADD2_ID: //Add Button for Multi Prices 3
                int[] prices1 = tile.getMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37);
                if(this.buttonList.get(ADD2_ID).displayString.equals("+")) {
                    prices1[2] = 0;
                    this.buttonList.get(ADD2_ID).displayString = "-";
                }else{
                    prices1[2] = -1;
                    prices1[5] = 0;
                    this.buttonList.get(ADD2_ID).displayString = "+";
                }
                tile.setMultiPrices(tile.getField(tile.FIELD_SELECTSLOT) - 37, prices1.clone());

                PacketSetItemMultiPricesToServer pack5 = new PacketSetItemMultiPricesToServer();
                pack5.setData(prices1, this.tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack5);

                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
        }
    }
    //</editor-fold>
}
