package beardlessbrady.modcurrency2.block.economyblocks.vending;

import beardlessbrady.modcurrency2.ModCurrency;
import beardlessbrady.modcurrency2.network.*;
import beardlessbrady.modcurrency2.proxy.ClientProxy;
import beardlessbrady.modcurrency2.utilities.GuiButtonTextured;
import beardlessbrady.modcurrency2.utilities.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase.FIELD_MODE;
import static beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class GuiVending extends GuiContainer {
    private static final ResourceLocation ASSET_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/guiassets.png");
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/vendinggui.png");

    private GuiTextField fieldPrice, fieldAmnt, fieldAmnt2, fieldAmnt3, fieldAmnt4, fieldAmnt5, fieldItemMax, fieldTimeRestock;
    private TileVending te;

    private final KeyBinding[] keyBindings = ClientProxy.keyBindings.clone();

    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;
    private static final int BUTTONINFINITE = 2;

    private static final int FIELDPRICE = 0;
    private static final int FIELDAMNT = 1;
    private static final int FIELDAMNT2 = 2;
    private static final int FIELDAMNT3 = 3;
    private static final int FIELDAMNT4 = 4;
    private static final int FIELDAMNT5 = 5;
    private static final int FIELDITEMMAX = 6;
    private static final int FIELDTIMERESTOCK = 7;

    public GuiVending(EntityPlayer entityPlayer, TileVending te) {
        super(new ContainerVending(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttonList.add(new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, "$"));

        String mode = (te.getField(FIELD_MODE) == 1) ? I18n.format("guivending.stock") : I18n.format("guivending.trade");
        buttonList.add(new GuiButton(BUTTONADMIN, i + 137, j - 42, 32, 20, mode));

        buttonList.add(new GuiButtonTextured("infinite", BUTTONINFINITE, i -21, j + 55, 0, 82, 21, 17, 0, "", ASSET_TEXTURE));
        buttonList.get(BUTTONINFINITE).visible = false;

        fieldPrice = new GuiTextField(FIELDPRICE, fontRenderer, 0, 0, 90, 8);        //Setting Costs
        fieldPrice.setTextColor(Integer.parseInt("C35763", 16));
        fieldPrice.setEnableBackgroundDrawing(false);
        fieldPrice.setMaxStringLength(7);
        fieldPrice.setVisible(false);
        fieldPrice.setText("0.00");

        fieldAmnt = new GuiTextField(FIELDAMNT, fontRenderer, 0, 0, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt.setTextColor(Integer.parseInt("C35763", 16));
        fieldAmnt.setEnableBackgroundDrawing(false);
        fieldAmnt.setMaxStringLength(2);
        fieldAmnt.setVisible(false);
        fieldAmnt.setText("1");

        fieldAmnt2 = new GuiTextField(FIELDAMNT2, fontRenderer, i - 22, j + 54, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt2.setTextColor(Integer.parseInt("C35763", 16));
        fieldAmnt2.setEnableBackgroundDrawing(false);
        fieldAmnt2.setMaxStringLength(2);
        fieldAmnt2.setVisible(false);
        fieldAmnt2.setText("1");

        fieldAmnt3 = new GuiTextField(FIELDAMNT3, fontRenderer, i - 22, j + 73, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt3.setTextColor(Integer.parseInt("C35763", 16));
        fieldAmnt3.setEnableBackgroundDrawing(false);
        fieldAmnt3.setMaxStringLength(2);
        fieldAmnt3.setVisible(false);
        fieldAmnt3.setText("1");

        fieldAmnt4 = new GuiTextField(FIELDAMNT4, fontRenderer, i - 22, j + 94, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt4.setTextColor(Integer.parseInt("C35763", 16));
        fieldAmnt4.setEnableBackgroundDrawing(false);
        fieldAmnt4.setMaxStringLength(2);
        fieldAmnt4.setVisible(false);
        fieldAmnt4.setText("1");

        fieldAmnt5 = new GuiTextField(FIELDAMNT5, fontRenderer, i - 22, j + 111, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt5.setTextColor(Integer.parseInt("C35763", 16));
        fieldAmnt5.setEnableBackgroundDrawing(false);
        fieldAmnt5.setMaxStringLength(2);
        fieldAmnt5.setVisible(false);
        fieldAmnt5.setText("1");

        fieldItemMax = new GuiTextField(FIELDITEMMAX, fontRenderer, i - 66, j + 85, 90, 8);
        fieldItemMax.setTextColor(Integer.parseInt("BEA63D", 16));
        fieldItemMax.setEnableBackgroundDrawing(false);
        fieldItemMax.setMaxStringLength(3);
        fieldItemMax.setVisible(false);
        fieldItemMax.setText("1");

        fieldTimeRestock = new GuiTextField(FIELDTIMERESTOCK, fontRenderer, i - 63, j + 75, 90, 8);
        fieldTimeRestock.setTextColor(Integer.parseInt("BEA63D", 16));
        fieldTimeRestock.setEnableBackgroundDrawing(false);
        fieldTimeRestock.setMaxStringLength(4);
        fieldTimeRestock.setVisible(false);
        fieldTimeRestock.setText("1");

        GlStateManager.color(0xFF, 0xFF, 0xFF);

        updateTextField();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);

        fieldPrice.drawTextBox();
        fieldAmnt.drawTextBox();
        fieldAmnt2.drawTextBox();
        fieldAmnt3.drawTextBox();
        fieldAmnt4.drawTextBox();
        fieldAmnt5.drawTextBox();
        fieldItemMax.drawTextBox();
        fieldTimeRestock.drawTextBox();

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        guiColor(te.getColor());

        Minecraft.getMinecraft().getTextureManager().bindTexture(BACK_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 154);

        GlStateManager.color(1F, 1F, 1F, 1.0F);
        GlStateManager.popMatrix();

        drawTexturedModalRect(guiLeft, guiTop + 109, 0, 156, 176, 99);

        //Tag background
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);

        //Admin Tag
        if(te.getField(FIELD_MODE) == 1) {

            //If not bundles price normal tag background, IF bundles print bundle tag
            if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() != te.getField(FIELD_SELECTED)) {
                drawTexturedModalRect(guiLeft - 107, guiTop + 5, 150, 135, 106, 48);
            } else
                drawTexturedModalRect(guiLeft - 91, guiTop - 10, 166, 0, 90, 134);

            if (te.getField(FIELD_CREATIVE) == 1) {
                if (te.getField(FIELD_FINITE) == 1) {
                    //If bundled tag is open move everything down by y=bundleMod
                    int bundleMod = 0;
                    if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() == te.getField(FIELD_SELECTED)) {
                        bundleMod = 72;
                    }

                    //finite Tag
                    drawTexturedModalRect(guiLeft - 101, guiTop + 62 + bundleMod, 156, 184, 100, 36);
                    //Do not draw string on bundled tag mode
                    if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() != te.getField(FIELD_SELECTED))
                        drawTexturedModalRect(guiLeft - 14, guiTop + 27, 146, 184, 9, 56);
                }
            }
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawBundles();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        //If owner or creative make admin button visible
        buttonList.get(BUTTONADMIN).visible = te.isOwner() || Minecraft.getMinecraft().player.isCreative();

        //Basics GUI labels
        fontRenderer.drawString(I18n.format("tile.modcurrency2:blockvending.name"), 8, -42, Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 114, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 149, -8, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 80, 68, Color.lightGray.getRGB());

        //Money total and cashout button labels
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);
        fontRenderer.drawStringWithShadow(I18n.format("guivending.cash"), 7, -40, Integer.parseInt("2DB22F", 16));
        fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, -30, Integer.parseInt("ffffff", 16));

        //draws players cash in machine
        fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.LONG_FIELD_CASHRESERVE))), 15, -30, Integer.parseInt("ffffff", 16));
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        //Draws custom stack sizes
        drawItemStackSize();

        //Admin 'Price Tag' rendering
        drawAdminPanel();

        //STOCK MODE - SELL MODE
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            //Draws the red selection overlay when determining which slot is selected
            drawSelectionOverlay();

            //If a creative machine add an extra label saying so
            if (te.getField(TileVending.FIELD_CREATIVE) == 1)
                fontRenderer.drawString(I18n.format("guivending.creative"), 90, -42, Color.pink.getRGB());

            //Draws machines cash total and its labels
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 0.8F);
            fontRenderer.drawStringWithShadow(I18n.format("guivending.profit"), 7, -10, Integer.parseInt("3D78E0", 16));
            fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, 0, Integer.parseInt("ffffff", 16));
            fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.LONG_FIELD_CASHREGISTER))), 15, 0, Integer.parseInt("ffffff", 16));
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            //Changes '$' button to blue to signify clicking it cashes out Machines money
            buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, TextFormatting.BLUE + "$"));
        } else {
            //Changes '$' button to green to signify clicking it cashes out Players money
            buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, TextFormatting.GREEN + "$"));

            if (te.getField(TileVending.FIELD_CREATIVE) == 1)
                buttonList.get(BUTTONINFINITE).visible = false;
        }

        //Warning Message Rendering
        message();
    }

    private void drawItemStackSize() {
        if(te.getField(FIELD_FINITE) == 1) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 0.8F);

            String num;
            int startY = -29;
            int columnCount = 5;

            for (int j = 0; j < columnCount; j++) {
                for (int i = 0; i < 5; i++) {
                    int index = (i + (5 * j));

                    if (te.getItemVendor(i + (5 * j)).getSize() != 0 && te.getItemVendor(i + (5 * j)).getSize() > 0) {
                        num = Integer.toString(te.getItemVendor(i + (5 * j)).getSize());
                    } else if (!te.getItemVendor(index).getStack().isEmpty()) {
                        num = TextFormatting.RED + "Out";
                    } else {
                        num = " ";
                    }

                    if (num.length() == 1) num = "  " + num;
                    if (num.length() == 2) num = " " + num;

                    if (te.getField(FIELD_MODE) == 1) {
                        if (te.getItemVendor(i + (5 * j)).getSize() != 1)
                            fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
                    } else {
                        int startAmount = te.getItemVendor(index).getAmount();

                        if (te.getItemVendor(index).getBundleMainSlot() == -1) {
                            //If Sneak button held down, show a full stack (or as close to it)
                            //If Jump button held down, show half a stack (or as close to it)
                            if (Keyboard.isKeyDown(keyBindings[0].getKeyCode())) {
                                startAmount = te.sneakFullStack(index, startAmount);

                                if(!te.getKey(KEY_SHIFT)){
                                    te.setKey(KEY_SHIFT, true);

                                    PacketSendKeyToServer pack = new PacketSendKeyToServer();
                                    pack.setData(te.getPos(), KEY_SHIFT, true);
                                    PacketHandler.INSTANCE.sendToServer(pack);
                                }
                            }else{
                                if(te.getKey(KEY_SHIFT)) {
                                    te.setKey(KEY_SHIFT, false);

                                    PacketSendKeyToServer pack = new PacketSendKeyToServer();
                                    pack.setData(te.getPos(), KEY_SHIFT, false);
                                    PacketHandler.INSTANCE.sendToServer(pack);
                                }
                            }

                            if (Keyboard.isKeyDown(keyBindings[1].getKeyCode())) {
                                startAmount = te.jumpHalfStack(index, startAmount);

                                if (!te.getKey(KEY_CONTROL)) {
                                    te.setKey(KEY_CONTROL, true);

                                    PacketSendKeyToServer pack = new PacketSendKeyToServer();
                                    pack.setData(te.getPos(), KEY_CONTROL, true);
                                    PacketHandler.INSTANCE.sendToServer(pack);
                                }
                            }else{
                                if(te.getKey(KEY_CONTROL)){
                                    te.setKey(KEY_CONTROL, false);

                                    PacketSendKeyToServer pack = new PacketSendKeyToServer();
                                    pack.setData(te.getPos(), KEY_CONTROL, false);
                                    PacketHandler.INSTANCE.sendToServer(pack);
                                }
                            }
                        }

                        String amount = Integer.toString(startAmount);

                        if (amount.length() == 1) amount = "  " + amount;
                        if (amount.length() == 2) amount = " " + amount;

                        if (te.getItemVendor(index).getSize() >= 1 && te.getItemVendor(index).getSize() < te.getItemVendor(index).getAmount())
                            num = TextFormatting.RED + "Out";

                        if (num.equals(TextFormatting.RED + "Out")) {
                            fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
                        } else {
                            if (startAmount != 1 && !te.getItemVendor(index).getStack().isEmpty())
                                fontRenderer.drawStringWithShadow(amount, 66 + (i * 26), startY + (j * 26), -1);
                        }
                    }
                    GlStateManager.color(0xFF, 0xFF, 0xFF);
                }
            }

            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    private void drawAdminPanel() {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        if (te.getField(TileVending.FIELD_MODE) == 1) {

            //NON BUNDLE PANEL
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
            if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() != te.getField(FIELD_SELECTED)) {

                fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), -74, 10, Integer.parseInt("ffffff", 16));

                String itemName = "[" + te.getSelectedName() + "]";
                GL11.glPushMatrix();
                GL11.glScaled(0.7, 0.7, 0.7);
                fontRenderer.drawString(I18n.format(itemName), -90 - (itemName.length() * 2), 28, Integer.parseInt("7B232D", 16));
                GL11.glPopMatrix();

                fontRenderer.drawStringWithShadow(I18n.format("$"), -90, 30, Color.lightGray.getRGB());

                fieldPrice.x = i - 82;
                fieldPrice.y = j + 30;
                fieldPrice.setVisible(true);

                fontRenderer.drawStringWithShadow(I18n.format("guivending.amnt"), -90, 40, Color.lightGray.getRGB());
                fieldAmnt.x = i - 65;
                fieldAmnt.y = j + 40;
                fieldAmnt.setVisible(true);

                fieldAmnt2.setVisible(false);

                fieldAmnt3.setVisible(false);

                fieldAmnt4.setVisible(false);

                fieldAmnt5.setVisible(false);
            } else { //BUNDLE PANEL
                fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings_bundled"), -80, 6, Integer.parseInt("ffffff", 16));
                fontRenderer.drawStringWithShadow(I18n.format("$"), -70, 16, Color.lightGray.getRGB());

                fieldPrice.x = i - 62;
                fieldPrice.y = j + 16;
                fieldPrice.setVisible(true);

                fontRenderer.drawStringWithShadow(I18n.format("guivending.amnt_bundled"), -70, 26, Color.lightGray.getRGB());
                fieldAmnt.setVisible(true);

                int[] bundleSlots = te.getItemVendor(te.getField(FIELD_SELECTED)).getBundle();
                StringBuilder bundleName = new StringBuilder();
                for (int bundleSlot : bundleSlots) {
                    bundleName.append("\n•").append(te.getItemVendor(bundleSlot).getStack().getDisplayName()).append("\n");
                    if(te.getItemVendor(bundleSlot).getStack().getDisplayName().length() < 13)
                        bundleName.append("\n");
                }

                GL11.glPushMatrix();
                GL11.glScaled(0.7, 0.7, 0.7);
                fontRenderer.drawSplitString(bundleName.toString(), -120, 40, 90, Color.lightGray.getRGB());
                GL11.glPopMatrix();

                fontRenderer.drawStringWithShadow(I18n.format("x"), -30, 35, Color.lightGray.getRGB());

                fieldAmnt.x = i - 22;
                fieldAmnt.y = j + 36;

                fieldAmnt3.setVisible(false);
                fieldAmnt4.setVisible(false);
                fieldAmnt5.setVisible(false);

                switch (bundleSlots.length) {
                    case 5:
                        fontRenderer.drawStringWithShadow(I18n.format("x"), -30, 110, Color.lightGray.getRGB());
                        fieldAmnt5.setVisible(true);
                    case 4:
                        fontRenderer.drawStringWithShadow(I18n.format("x"), -30, 93, Color.lightGray.getRGB());
                        fieldAmnt4.setVisible(true);
                    case 3:
                        fontRenderer.drawStringWithShadow(I18n.format("x"), -30, 72, Color.lightGray.getRGB());
                        fieldAmnt3.setVisible(true);
                    case 2:
                        fontRenderer.drawStringWithShadow(I18n.format("x"), -30, 53, Color.lightGray.getRGB());
                        fieldAmnt2.setVisible(true);
                        break;
                }
            }
            GlStateManager.color(0xFF, 0xFF, 0xFF);

            //Finite Restock for Creative machine
            if (te.getField(FIELD_CREATIVE) == 1) {
                Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);

                //If bundled tag is open move everything down by y=bundleMod
                int bundleMod = 0;
                if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() == te.getField(FIELD_SELECTED)) {
                    bundleMod = 72;

                    fieldItemMax.y = j + 157;
                    fieldTimeRestock.y = j + 147;
                }else{
                    fieldItemMax.y = j + 85;
                    fieldTimeRestock.y = j + 75;
                }


                buttonList.get(BUTTONINFINITE).visible = true;
                buttonList.get(BUTTONINFINITE).y = j + 55 + bundleMod;

                //Draw slash through infinite button if finite
                if (te.getField(FIELD_FINITE) != 1)
                    drawTexturedModalRect(-20, 55 + bundleMod, 61, 1, 19, 17);

                    //Adds extra 'Restock' screen if inventory is 'finite'
                if (te.getField(FIELD_FINITE) == 1) {
                    fontRenderer.drawStringWithShadow(I18n.format("Restock"), -80, 65 + bundleMod, Color.WHITE.getRGB());
                    fontRenderer.drawStringWithShadow(I18n.format("every       secs"), -95, 74 + bundleMod, Color.WHITE.getRGB());
                    fontRenderer.drawStringWithShadow(I18n.format("up to      items"), -95, 84 + bundleMod, Color.WHITE.getRGB());

                    fieldTimeRestock.setVisible(true);
                    fieldItemMax.setVisible(true);
                } else {
                    fieldTimeRestock.setVisible(false);
                    fieldItemMax.setVisible(false);
                }
            }
        } else {
            fieldPrice.setVisible(false);

            fieldAmnt.setVisible(false);

            fieldAmnt2.setVisible(false);
            fieldAmnt3.setVisible(false);
            fieldAmnt4.setVisible(false);
            fieldAmnt5.setVisible(false);

            fieldTimeRestock.setVisible(false);
            fieldItemMax.setVisible(false);
        }
    }

    private void drawBundles() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        for (int slot = 0; slot < te.TE_INVENTORY_SLOT_COUNT; slot++) {
            if (te.getItemVendor(slot).hasBundle()) {
                if(te.getItemVendor(slot).hasBundle()) {
                    int[] bundle = te.getItemVendor(slot).getBundle();
                    if (bundle[0] == slot) { //is the main slot
                        int slotColumn = 0, slotRow;

                        if (slot <= 4) {
                            slotRow = slot;
                        } else if (slot <= 9) {
                            slotColumn = 1;
                            slotRow = (slot) - 5;
                        } else if (slot <= 14) {
                            slotColumn = 2;
                            slotRow = (slot) - 10;
                        } else if (slot <= 19) {
                            slotColumn = 3;
                            slotRow = (slot) - 15;
                        } else {
                            slotColumn = 4;
                            slotRow = (slot) - 20;
                        }

                        //Check which direction bundle is going
                        int direction = 0; //0=Left, 1=Right, 2=Down, 3=Up
                        int endDirection = 0;

                        if (te.getItemVendor(slot - 1).getBundleMainSlot() == slot) {
                            endDirection = 1;
                        } else if (te.getItemVendor(slot + 1).getBundleMainSlot() == slot) {
                            direction = 1;
                            endDirection = 0;
                        } else if (te.getItemVendor(slot + 5).getBundleMainSlot() == slot) {
                            direction = 2;
                            endDirection = 3;
                        } else if (te.getItemVendor(slot - 5).getBundleMainSlot() == slot) {
                            direction = 3;
                            endDirection = 2;
                        }

                        int yChange = 0;
                        if (te.getField(FIELD_SELECTED) == slot && te.getField(FIELD_MODE) == 1) { //Selected is on a bundle
                            yChange = 21;
                        }

                        //Starting
                        drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 21 * direction, 130 + yChange, 20, 20);

                        int forIncrement = 1;
                        switch (direction) {
                            case 0:
                                forIncrement = -1;
                                break;
                            case 2:
                                forIncrement = 5;
                                break;
                            case 3:
                                forIncrement = -5;
                                break;
                        }

                        for (int i = 1; i < bundle.length; i++) {
                            int slotColumn2 = 0;
                            int slotRow2 = 0;
                            int slot2 = slot + (forIncrement * i);
                            if (te.getItemVendor(slot2).getBundleMainSlot() == slot) {

                                if (slot2 >= 0 && slot2 <= 4) {
                                    slotRow2 = slot2;
                                } else if (slot2 >= 5 && slot2 <= 9) {
                                    slotColumn2 = 1;
                                    slotRow2 = (slot2) - 5;
                                } else if (slot2 >= 10 && slot2 <= 14) {
                                    slotColumn2 = 2;
                                    slotRow2 = (slot2) - 10;
                                } else if (slot2 >= 15 && slot2 <= 19) {
                                    slotColumn2 = 3;
                                    slotRow2 = (slot2) - 15;
                                } else if (slot2 >= 20 && slot2 <= 24) {
                                    slotColumn2 = 4;
                                    slotRow2 = (slot2) - 20;
                                }

                                if (te.getItemVendor(slot2 + (forIncrement)).getBundleMainSlot() == slot) { //This slot is NOT the end of the bundle
                                    if (direction == 0 || direction == 1) {
                                        drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 84, 130 + yChange, 20, 20);
                                    } else {
                                        drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 105, 130 + yChange, 20, 20);

                                    }
                                } else { //This slot IS the end of the bundle
                                    drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 21 * endDirection, 130 + yChange, 20, 20);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawSelectionOverlay() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        if (te.getField(TileVending.FIELD_MODE) == 1) {
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

            if (te.getItemVendor(slotId).getBundleMainSlot() != slotId)
                drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 0, 172, 20, 20);
            drawTexturedModalRect(42 + (18 * slotRow) + 14, -32 + (18 * slotColumn) + 15, 3, 3, 16, 14);
        }
    }

    private void message() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        FontRenderer fontRenderer = Minecraft.getMinecraft().ingameGUI.getFontRenderer();
        String message = te.getMessage();

        if (!message.equals("")) {
            //Draws beginning of back panel of message
            drawTexturedModalRect(70 - ((message.length()) * 5 / 2), 55, 0, 20, 21, 21);

            //Draws extentions of the back panel of message
            int panelAmounts = (message.length() * 5) / 15;
            for (int i = 0; i < panelAmounts; i++) {
                drawTexturedModalRect(91 - (message.length() * 5 / 2) + (i * 17), 55, 4, 20, 17, 21);
            }

            //Draws end of back panel of message
            drawTexturedModalRect(91 - (message.length() * 5 / 2) + (panelAmounts * 17), 55, 0, 42, 21, 21);

            //Draws Warning Symbol
            drawTexturedModalRect(74 - (message.length() * 5 / 2), 58, 41, 1, 19, 17);
        }
        fontRenderer.drawStringWithShadow(message, 94 - (message.length() * 5) / 2, 62, 0xDE3131);
        GlStateManager.color(0xFF, 0xFF, 0xFF);
    }

    private void guiColor(EnumDyeColor dyeColor) {
        switch (dyeColor) {
            case LIGHT_BLUE:
                GlStateManager.color(143F / 255F, 185F / 255F, 244F / 255F, 1.0F);
                break;
            case MAGENTA:
                GlStateManager.color(203F / 255F, 105F / 255F, 197F / 255F, 1.0F);
                break;
            case YELLOW:
                GlStateManager.color(231F / 255F, 231F / 255F, 42F / 255F, 1.0F);
                break;
            case SILVER:
                GlStateManager.color(196F / 255F, 196F / 255F, 203F / 255F, 1.0F);
                break;
            case PURPLE:
                GlStateManager.color(164F / 255F, 83F / 255F, 206F / 255F, 1.0F);
                break;
            case ORANGE:
                GlStateManager.color(230F / 255F, 158F / 255F, 52F / 255F, 1.0F);
                break;
            case WHITE:
                GlStateManager.color(234F / 255F, 234F / 255F, 234F / 255F, 1.0F);
                break;
            case GREEN:
                GlStateManager.color(74F / 255F, 107F / 255F, 24F / 255F, 1.0F);
                break;
            case BROWN:
                GlStateManager.color(112F / 255F, 68F / 255F, 37F / 255F, 1.0F);
                break;
            case BLACK:
                GlStateManager.color(31F / 255F, 31F / 255F, 36F / 255F, 1.0F);
                break;
            case PINK:
                GlStateManager.color(247F / 255F, 180F / 255F, 214F / 255F, 1.0F);
                break;
            case LIME:
                GlStateManager.color(135F / 255F, 202F / 255F, 49F / 255F, 1.0F);
                break;
            case GRAY:
                GlStateManager.color(160F / 255F, 160F / 255F, 160F / 255F, 1.0F);
                break;
            case CYAN:
                GlStateManager.color(60F / 255F, 142F / 255F, 176F / 255F, 1.0F);
                break;
            case BLUE:
                GlStateManager.color(45F / 255F, 93F / 255F, 167F / 255F, 1.0F);
                break;
            case RED:
                GlStateManager.color(211F / 255F, 90F / 255F, 86F / 255F, 1.0F);
                break;
        }
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (width - xSize) / 2);
        int j = (y - (height - ySize) / 2);

        if (j <= 58 && j >= -31 && i >= 43) {
            int startY = -31;
            int startX = 43;
            int row = ((j - startY) / 18);
            int column = ((i - startX) / 18);
            int slot = column + (row * 5);

            List<String> list = new ArrayList<>();
            List<String> ogTooltip = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int tooltipStart = 1;

            if (te.getItemVendor(slot).getBundleMainSlot() == -1) {
                //Adding name and subname of item before price and such
                if (ogTooltip.size() > 0) {
                    list.add(ogTooltip.get(0));
                    list.set(0, stack.getRarity().rarityColor + list.get(0));
                }
                if (ogTooltip.size() > 1) if (!ogTooltip.get(1).equals("")) {
                    list.add(TextFormatting.GRAY + ogTooltip.get(1));
                    tooltipStart = 2;
                }

                //Adding Vending Strings
                TextFormatting color = TextFormatting.RED;
                if (te.getField(FIELD_MODE) == 0) {
                    if (te.canAfford(slot, 1)) {
                        color = TextFormatting.GREEN;
                    }

                    int cost = te.getItemVendor(slot).getCost();
                    int amount = te.getItemVendor(slot).getAmount();


                    //If Sneak button held down, show a full stack (or as close to it)
                    //If Jump button held down, show half a stack (or as close to it)
                    if (Keyboard.isKeyDown(keyBindings[0].getKeyCode())) {
                        amount = te.sneakFullStack(slot, amount);
                        cost = cost * (amount / te.getItemVendor(slot).getAmount());
                    } else if (Keyboard.isKeyDown(keyBindings[1].getKeyCode())) {
                        amount = te.jumpHalfStack(slot, amount);
                        cost = cost * (amount / te.getItemVendor(slot).getAmount());
                    }

                    if (te.getItemVendor(slot).getAmount() == 1) {
                        list.add(color + "$" + UtilMethods.translateMoney(cost));
                    } else {
                        list.add(TextFormatting.BLUE + Integer.toString(amount) + TextFormatting.RESET + " for " + color + "$" + UtilMethods.translateMoney(cost));
                    }

                    if (te.getField(FIELD_FINITE) == 1) {
                        list.add("Stock: " + TextFormatting.BLUE + te.getItemVendor(slot).getSize());
                    } else {
                        list.add("Stock: " + TextFormatting.BLUE + "Infinite");
                    }
                }

                //adding original extra stuff AFTER price and such
                for (; tooltipStart < stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).size(); tooltipStart++) {
                    list.add(TextFormatting.GRAY + stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(tooltipStart));
                }

            } else { //Is a BUNDLE
                list.add("Bundle");

                //Adding Vending Strings
                TextFormatting color = TextFormatting.RED;
                if (te.getField(FIELD_MODE) == 0) {
                    if (te.canAfford(slot, 1)) {
                        color = TextFormatting.GREEN;
                    }

                    int mainSlot = te.getItemVendor(slot).getBundleMainSlot();
                    int cost = te.getItemVendor(mainSlot).getCost();

                    list.add(color + "$" + UtilMethods.translateMoney(cost));

                    list.add(" ");
                    list.add("Includes:");


                    int[] bundle = te.getItemVendor(mainSlot).getBundle();
                    for (int k : bundle) {
                        list.add("x" + TextFormatting.BLUE + te.getItemVendor(k).getAmount() + " " + TextFormatting.RESET + te.getItemVendor(k).getStack().getDisplayName());
                    }
                }


            }
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        } else {
            super.renderToolTip(stack, x, y);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        int numChar = Character.getNumericValue(typedChar);
        if ((te.getField(FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) { //Ensures keys input are only numbers or backspace type keys

            if ((!fieldPrice.getText().contains(".")) || keyCode != 52) {
                if (fieldPrice.textboxKeyTyped(typedChar, keyCode)) setCost();
            }

            if (fieldPrice.getText().length() > 0)
                if (fieldPrice.getText().substring(fieldPrice.getText().length() - 1).equals("."))
                    fieldPrice.setMaxStringLength(fieldPrice.getText().length() + 2);
            if (!fieldPrice.getText().contains(".")) fieldPrice.setMaxStringLength(7);

            if (fieldAmnt.textboxKeyTyped(typedChar, keyCode))
                setAmnt(te.getField(FIELD_SELECTED), fieldAmnt);

            if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() == te.getField(FIELD_SELECTED)) {
                int[] bundle = te.getItemVendor(te.getField(FIELD_SELECTED)).getBundle();

                switch (bundle.length) {
                    case 5:
                        if (fieldAmnt5.textboxKeyTyped(typedChar, keyCode)) setAmnt(bundle[4], fieldAmnt5);
                    case 4:
                        if (fieldAmnt4.textboxKeyTyped(typedChar, keyCode)) setAmnt(bundle[3], fieldAmnt4);
                    case 3:
                        if (fieldAmnt3.textboxKeyTyped(typedChar, keyCode)) setAmnt(bundle[2], fieldAmnt3);
                    case 2:
                        if (fieldAmnt2.textboxKeyTyped(typedChar, keyCode)) setAmnt(bundle[1], fieldAmnt2);
                }
            }

            if (te.getField(FIELD_CREATIVE) == 1 && te.getField(FIELD_FINITE) == 1) {
                if (fieldItemMax.textboxKeyTyped(typedChar, keyCode))
                    setItemMax(fieldItemMax);

                if (fieldTimeRestock.textboxKeyTyped(typedChar, keyCode))
                    setTimeRestock(fieldTimeRestock);
            }
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
            fieldAmnt2.mouseClicked(mouseX, mouseY, mouseButton);
            fieldAmnt3.mouseClicked(mouseX, mouseY, mouseButton);
            fieldAmnt4.mouseClicked(mouseX, mouseY, mouseButton);
            fieldAmnt5.mouseClicked(mouseX, mouseY, mouseButton);
            fieldItemMax.mouseClicked(mouseX, mouseY, mouseButton);
            fieldTimeRestock.mouseClicked(mouseX, mouseY, mouseButton);

            updateTextField();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private void setCost() {
        if (fieldPrice.getText().length() > 0) {
            int newCost = 0;

            if (fieldPrice.getText().contains(".")) {
                if (fieldPrice.getText().lastIndexOf(".") + 1 != fieldPrice.getText().length()) {
                    if (fieldPrice.getText().lastIndexOf(".") + 2 == fieldPrice.getText().length()) {
                        newCost = Integer.valueOf(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1) + "0");
                    } else {
                        newCost = Integer.valueOf(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1));
                    }
                }

                if (fieldPrice.getText().lastIndexOf(".") != 0)
                    newCost += Integer.valueOf(fieldPrice.getText().substring(0, fieldPrice.getText().lastIndexOf("."))) * 100;

            } else {
                newCost = Integer.valueOf(fieldPrice.getText()) * 100;
            }

            te.getItemVendor(te.getField(FIELD_SELECTED)).setCost(newCost);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), newCost, PacketSetItemToServer.FIELD_COST, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void setAmnt(int slot, GuiTextField guiTextField) {
        if (guiTextField.getText().length() > 0) {
            int amount = Integer.parseInt(guiTextField.getText());

            if (te.getItemVendor(slot).getStack().isEmpty()) {
                amount = 1;
            } else if (Integer.valueOf(guiTextField.getText()) > te.getItemVendor(slot).getStack().getMaxStackSize())
                amount = te.getItemVendor(slot).getStack().getMaxStackSize();

            if (amount == 0) amount = 1;

            te.getItemVendor(slot).setAmount(amount);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), amount, PacketSetItemToServer.FIELD_AMOUNT, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void setItemMax(GuiTextField guiTextField){
        if (guiTextField.getText().length() > 0) {
            int amount = Integer.parseInt(guiTextField.getText());

            te.getItemVendor(te.getField(FIELD_SELECTED)).setItemMax(amount);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), amount, PacketSetItemToServer.FIELD_ITEMMAX, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void setTimeRestock(GuiTextField guiTextField){
        if (guiTextField.getText().length() > 0) {
            int amount = Integer.parseInt(guiTextField.getText());

            te.getItemVendor(te.getField(FIELD_SELECTED)).setTimeRaise(amount);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), amount, PacketSetItemToServer.FIELD_TIMERAISE, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void updateTextField() {
        ItemVendor item = te.getItemVendor(te.getField(FIELD_SELECTED));
        boolean isItem = item.getStack().getItem() != Items.AIR;;

        fieldPrice.setEnabled(isItem);
        fieldPrice.setText(UtilMethods.translateMoney(te.getItemVendor(te.getField(FIELD_SELECTED)).getCost()));

        fieldAmnt.setEnabled(isItem);
        fieldAmnt.setText(Integer.toString(te.getItemVendor(te.getField(FIELD_SELECTED)).getAmount()));

        fieldItemMax.setEnabled(isItem);
        fieldItemMax.setText(Integer.toString(te.getItemVendor(te.getField(FIELD_SELECTED)).getItemMax()));

        fieldTimeRestock.setEnabled(isItem);
        fieldTimeRestock.setText(Integer.toString(te.getItemVendor(te.getField(FIELD_SELECTED)).getTimeRaise()));

        if (te.getItemVendor(te.getField(FIELD_SELECTED)).getBundleMainSlot() == te.getField(FIELD_SELECTED)) {
            int[] bundle = te.getItemVendor(te.getField(FIELD_SELECTED)).getBundle();

            switch (bundle.length) {
                case 5:
                    fieldAmnt5.setText(Integer.toString(te.getItemVendor(bundle[4]).getAmount()));
                case 4:
                    fieldAmnt4.setText(Integer.toString(te.getItemVendor(bundle[3]).getAmount()));
                case 3:
                    fieldAmnt3.setText(Integer.toString(te.getItemVendor(bundle[2]).getAmount()));
                case 2:
                    fieldAmnt2.setText(Integer.toString(te.getItemVendor(bundle[1]).getAmount()));
            }
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (te.getField(FIELD_MODE) == 1 && Keyboard.isKeyDown(keyBindings[0].getKeyCode())) {
            int i = (mouseX - (width - xSize) / 2);
            int j = (mouseY - (height - ySize) / 2);

            int startY = -31;
            int startX = 43;
            int row = ((j - startY) / 18);
            int column = ((i - startX) / 18);
            int slot = column + (row * 5);
            int selectedSlot = te.getField(FIELD_SELECTED);

            if (slot >= 0 && slot < 25) {

                if (te.getItemVendor(slot).getBundleMainSlot() == -1 && !te.getItemVendor(slot).getStack().isEmpty() && !te.getItemVendor(selectedSlot).getStack().isEmpty()) {

                    int displacement = selectedSlot - slot;
                    int[] movementArray = {-1, -2, -3, -4, 1, 2, 3, 4, -5, -10, -15, -20, 5, 10, 15, 20};
                    //-1,-2,-3,-4 Right
                    //1,2,3,4 Left
                    //-5,-10,-15,-20 Down
                    //5,10,15,20 Up

                    int direction = 0; //0 Right, 1 Left, 2 Down, 3 Up
                    switch (displacement) {
                        case -1:
                        case -2:
                        case -3:
                        case -4:
                            direction = 0;
                            break;
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                            direction = 1;
                            break;
                        case -5:
                        case -10:
                        case -15:
                        case -20:
                            direction = 2;
                            break;
                        case 5:
                        case 10:
                        case 15:
                        case 20:
                            direction = 3;
                            break;
                    }

                    boolean possibleValue = false;
                    for (int element : movementArray) {
                        if (element == displacement) {
                            possibleValue = true;
                            break;
                        }
                    }

                    if (possibleValue) {
                        if (Math.abs(displacement) == 1 || Math.abs(displacement) == 5) {
                            if (te.getItemVendor(selectedSlot).getBundleMainSlot() != selectedSlot) {
                                int[] bundle = {selectedSlot, slot};
                                te.getItemVendor(selectedSlot).setBundle(bundle);
                                PacketSetItemBundleToServer pack0 = new PacketSetItemBundleToServer();
                                pack0.setData(selectedSlot, bundle, te.getPos());
                                PacketHandler.INSTANCE.sendToServer(pack0);

                                te.getItemVendor(slot).setBundle(new int[]{selectedSlot});
                                PacketSetItemBundleToServer pack = new PacketSetItemBundleToServer();
                                pack.setData(slot, new int[]{selectedSlot}, te.getPos());
                                PacketHandler.INSTANCE.sendToServer(pack);
                            }
                        } else {
                            int previousSlot = slot;

                            switch (direction) {
                                case 0:
                                    previousSlot--;
                                    break;
                                case 1:
                                    previousSlot++;
                                    break;
                                case 2:
                                    previousSlot -= 5;
                                    break;
                                case 3:
                                    previousSlot += 5;
                                    break;
                            }

                            if(te.getItemVendor(previousSlot).hasBundle() && te.getItemVendor(selectedSlot).hasBundle() ) {
                                if (te.getItemVendor(previousSlot).getBundleMainSlot() == selectedSlot) {
                                    int[] oldBundle = te.getItemVendor(selectedSlot).getBundle().clone();
                                    int[] newBundle = new int[oldBundle.length + 1];

                                    System.arraycopy(oldBundle, 0, newBundle, 0, oldBundle.length);

                                    newBundle[newBundle.length - 1] = slot;
                                    te.getItemVendor(selectedSlot).setBundle(newBundle);
                                    PacketSetItemBundleToServer pack0 = new PacketSetItemBundleToServer();
                                    pack0.setData(selectedSlot, newBundle, te.getPos());
                                    PacketHandler.INSTANCE.sendToServer(pack0);

                                    te.getItemVendor(slot).setBundle(new int[]{selectedSlot});
                                    PacketSetItemBundleToServer pack = new PacketSetItemBundleToServer();
                                    pack.setData(slot, new int[]{selectedSlot}, te.getPos());
                                    PacketHandler.INSTANCE.sendToServer(pack);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getField(FIELD_MODE) == 1) ? 0 : 1, FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                buttonList.get(BUTTONADMIN).displayString = (te.getField(FIELD_MODE) == 0) ? I18n.format("guivending.stock") : I18n.format("guivending.trade");

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            case BUTTONCHANGE:
                PacketOutChangeToServer pack0 = new PacketOutChangeToServer();
                pack0.setData(te.getPos(), false);
                PacketHandler.INSTANCE.sendToServer(pack0);
                te.outChange(false);
                break;
            case BUTTONINFINITE:
                PacketSetFieldToServer pack1 = new PacketSetFieldToServer();
                pack1.setData((te.getField(FIELD_FINITE) == 1) ? 0 : 1, FIELD_FINITE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }

}