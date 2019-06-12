package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.network.*;
import beardlessbrady.modcurrency.utilities.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private static final ResourceLocation ASSET_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/guiassets.png");

    GuiTextField fieldPrice, fieldAmnt;

    TileVending te;


    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    private static final int FIELDPRICE = 0;
    private static final int FIELDAMNT = 1;

    public GuiVending(EntityPlayer entityPlayer, TileVending te) {
        super(new ContainerVending(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, "$"));

        String mode = (te.getField(te.FIELD_MODE) == 1) ? "STOCK" : "TRADE";
        this.buttonList.add(new GuiButton(BUTTONADMIN, i + 137, j - 42, 32, 20, mode));

        this.fieldPrice = new GuiTextField(FIELDPRICE, fontRenderer, i + 213, j + 30, 90, 8);        //Setting Costs
        this.fieldPrice.setTextColor(Integer.parseInt("C35763", 16));
        this.fieldPrice.setEnableBackgroundDrawing(false);
        this.fieldPrice.setMaxStringLength(7);
        this.fieldPrice.setEnabled(false);
        this.fieldPrice.setVisible(false);
        this.fieldPrice.setText("0.00");

        this.fieldAmnt = new GuiTextField(FIELDAMNT, fontRenderer, i + 233, j + 40, 90, 8);        //Setting Amount Sold in Bulk
        this.fieldAmnt.setTextColor(Integer.parseInt("C35763", 16));
        this.fieldAmnt.setEnableBackgroundDrawing(false);
        this.fieldAmnt.setMaxStringLength(2);
        this.fieldAmnt.setEnabled(false);
        this.fieldAmnt.setVisible(false);
        this.fieldAmnt.setText("1");

        updateTextField();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        this.fieldPrice.drawTextBox();
        this.fieldAmnt.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        drawBundles();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if (te.isOwner() || Minecraft.getMinecraft().player.isCreative()) {
            buttonList.get(BUTTONADMIN).visible = true;
        } else {
            buttonList.get(BUTTONADMIN).visible = false;
        }

        fontRenderer.drawString(I18n.format("tile.modcurrency:blockvending.name"), 8, -42, Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 114, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 149, -8, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 80, 68, Color.lightGray.getRGB());

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);
        fontRenderer.drawStringWithShadow(I18n.format("guivending.cash"), 7, -40, Integer.parseInt("2DB22F", 16));
        fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, -30, Integer.parseInt("ffffff", 16));

        fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getField(TileVending.FIELD_CASHRESERVE))), 15, -30, Integer.parseInt("ffffff", 16));
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawItemStackSize();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        //STOCK MODE, SELL MODE
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            drawSelectionOverlay();

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 0.8F);
            fontRenderer.drawStringWithShadow(I18n.format("guivending.profit"), 7, -10, Integer.parseInt("3D78E0", 16));
            fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, 0, Integer.parseInt("ffffff", 16));

            fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getField(TileVending.FIELD_CASHREGISTER))), 15, 0, Integer.parseInt("ffffff", 16));
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            this.buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, TextFormatting.BLUE + "$"));
        } else {
            this.buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j + 27, 20, 20, TextFormatting.GREEN + "$"));
        }
        drawAdminPanel();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getField(te.FIELD_MODE) == 1) ? 0 : 1, te.FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                String mode = (te.getField(te.FIELD_MODE) == 0) ? "STOCK" : "TRADE";
                buttonList.get(BUTTONADMIN).displayString = mode;

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            case BUTTONCHANGE:
                PacketOutChangeToServer pack0 = new PacketOutChangeToServer();
                pack0.setData(te.getPos(), false);
                PacketHandler.INSTANCE.sendToServer(pack0);
                te.outChange(false);
        }
    }

    private void drawItemStackSize() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);

        String num = " ";
        int startY = -29;
        int columnCount = 5;

        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < 5; i++) {
                int index = (i + (5 * j));

                if (te.getItemSize(i + (5 * j)) != 0 && te.getItemSize(i + (5 * j)) > 0) {
                    num = Integer.toString(te.getItemSize(i + (5 * j)));
                } else if (!te.getInvItemStack(index).isEmpty()) {
                    num = TextFormatting.RED + "Out";
                } else {
                    num = " ";
                }

                if (num.length() == 1) num = "  " + num;
                if (num.length() == 2) num = " " + num;

                if (te.getField(TileEconomyBase.FIELD_MODE) == 1) {
                    if(te.getItemSize(i + (5 * j)) != 1)
                        this.fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
                } else {
                    int startAmount = te.getItemAmnt(index);


                    //If Sneak button held down, show a full stack (or as close to it)
                    //If Jump button held down, show half a stack (or as close to it)
                    if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
                        startAmount = te.sneakFullStack(index, startAmount);
                    } else if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode())) {
                        startAmount = te.jumpHalfStack(index, startAmount);
                    }

                    String amount = Integer.toString(startAmount);

                    if (amount.length() == 1) amount = "  " + amount;
                    if (amount.length() == 2) amount = " " + amount;

                    if (te.getItemSize(index) >= 1 && te.getItemSize(index) < te.getItemAmnt(index))
                        num = TextFormatting.RED + "Out";

                    if (num.equals(TextFormatting.RED + "Out")) {
                        this.fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
                    } else {
                        if (startAmount != 1 && !te.getInvItemStack(index).isEmpty())
                            this.fontRenderer.drawStringWithShadow(amount, 66 + (i * 26), startY + (j * 26), -1);
                    }
                }
            }
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void drawAdminPanel() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            drawTexturedModalRect(177, 0, 0, 202, 106, 54);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), 216, 10, Integer.parseInt("ffffff", 16));

            String itemName = "[" + te.getSelectedName() + "]";
            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRenderer.drawString(I18n.format(itemName), 322 - (itemName.length() * 2), 28, Integer.parseInt("7B232D", 16));
            GL11.glPopMatrix();

            fontRenderer.drawStringWithShadow(I18n.format("$"), 206, 30, Color.lightGray.getRGB());
            this.fieldPrice.setEnabled(true);
            this.fieldPrice.setVisible(true);

            fontRenderer.drawStringWithShadow(I18n.format("Amnt:"), 206, 40, Color.lightGray.getRGB());
            this.fieldAmnt.setEnabled(true);
            this.fieldAmnt.setVisible(true);
        } else {
            this.fieldPrice.setEnabled(false);
            this.fieldPrice.setVisible(false);

            this.fieldAmnt.setEnabled(false);
            this.fieldAmnt.setVisible(false);
        }
    }

    private void drawBundles() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        for (int slot = 0; slot < te.TE_INVENTORY_SLOT_COUNT; slot++) {
            if (te.getSlotBundle(slot) != -1) {
                if (te.getSlotBundle(slot) == slot) { //is the main slot
                    int slotColumn = 0, slotRow = 0;

                    if (slot >= 0 && slot <= 4) {
                        slotColumn = 0;
                        slotRow = slot;
                    } else if (slot >= 5 && slot <= 9) {
                        slotColumn = 1;
                        slotRow = (slot) - 5;
                    } else if (slot >= 10 && slot <= 14) {
                        slotColumn = 2;
                        slotRow = (slot) - 10;
                    } else if (slot >= 15 && slot <= 19) {
                        slotColumn = 3;
                        slotRow = (slot) - 15;
                    } else if (slot >= 20 && slot <= 24) {
                        slotColumn = 4;
                        slotRow = (slot) - 20;
                    }

                    //Check which direction bundle is going
                    int direction = 0; //0=Left, 1=Right, 2=Down, 3=Up
                    int endDirection = 0;
                    if (te.getSlotBundle(slot - 1) == slot) {
                        direction = 0;
                        endDirection = 1;
                    } else if (te.getSlotBundle(slot + 1) == slot) {
                        direction = 1;
                        endDirection = 0;
                    } else if (te.getSlotBundle(slot + 5) == slot) {
                        direction = 2;
                        endDirection = 3;
                    } else if (te.getSlotBundle(slot - 5) == slot) {
                        direction = 3;
                        endDirection = 2;
                    }

                    //Starting
                    drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 21 * direction, 130, 20, 20);

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

                    for (int i = 1; i < 5; i++) {
                        int slotColumn2 = 0;
                        int slotRow2 = 0;
                        int slot2 = slot + (forIncrement * i);
                        if (te.getSlotBundle(slot2) == slot) {

                            if (slot2 >= 0 && slot2 <= 4) {
                                slotColumn2 = 0;
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

                            if (te.getSlotBundle(slot2 + (forIncrement)) == slot) { //This slot is NOT the end of the bundle
                                if(direction == 0 || direction == 1){
                                    drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 84, 130, 20, 20);
                                }else{
                                    drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 105, 130, 20, 20);

                                }
                            } else { //This slot IS the end of the bundle
                                drawTexturedModalRect(42 + (18 * slotRow2), -32 + (18 * slotColumn2), 21 * endDirection, 130, 20, 20);
                            }
                        }
                    }
                }
            }
        }
    }

    private void drawSelectionOverlay() {
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            int slotId = te.getField(TileVending.FIELD_SELECTED);
            int slotColumn = 0, slotRow = 0;

            if (slotId >= 0 && slotId <= 4) {
                slotColumn = 0;
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

            drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 0, 172, 20, 20);
            drawTexturedModalRect(42 + (18 * slotRow) + 14, -32 + (18 * slotColumn) + 15, 21, 187, 16, 14);
        }
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (this.width - this.xSize) / 2);
        int j = (y - (this.height - this.ySize) / 2);

        if (j <= 58 && j >= -31 && i >= 43) {
            int startY = -31;
            int startX = 43;
            int row = ((j - startY) / 18);
            int column = ((i - startX) / 18);
            int slot = column + (row * 5);

            List<String> list = new ArrayList<>();
            List<String> ogTooltip = stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int tooltipStart = 1;

            //Adding name and subname of item before price and such
            if (ogTooltip.size() > 0) {
                list.add(ogTooltip.get(0));
                list.set(0, stack.getRarity().rarityColor + (String) list.get(0));
            }
            if (ogTooltip.size() > 1) if (ogTooltip.get(1) != "") {
                list.add(TextFormatting.GRAY + ogTooltip.get(1));
                tooltipStart = 2;
            }

            list.add("slot: " + te.getSlotBundle(slot));

            //Adding Vending Strings
            TextFormatting color = TextFormatting.YELLOW;
            if (te.getField(TileEconomyBase.FIELD_MODE) == 0) {
                if (te.canAfford(slot, 1)) {
                    color = TextFormatting.GREEN;
                } else {
                    color = TextFormatting.RED;
                }

                int cost = te.getItemCost(slot);
                int amount = te.getItemAmnt(slot);

                //If Sneak button held down, show a full stack (or as close to it)
                //If Jump button held down, show half a stack (or as close to it)
                if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSneak.getKeyCode())) {
                    amount = te.sneakFullStack(slot, amount);
                    cost = cost * (amount / te.getItemAmnt(slot));
                } else if (Keyboard.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode())) {
                    amount = te.jumpHalfStack(slot, amount);
                    cost = cost * (amount / te.getItemAmnt(slot));
                }

                if (te.getItemAmnt(slot) == 1) {
                    list.add(color + "$" + UtilMethods.translateMoney(cost));
                } else {
                    list.add(TextFormatting.BLUE + Integer.toString(amount) + TextFormatting.RESET + " for " + color + "$" + UtilMethods.translateMoney(cost));
                }

                list.add("Stock: " + TextFormatting.BLUE + te.getItemSize(slot));
            }

            //adding original extra stuff AFTER price and such
            for (; tooltipStart < stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).size(); tooltipStart++) {
                list.add(TextFormatting.GRAY + stack.getTooltip(this.mc.player, this.mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(tooltipStart));
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        } else {
            super.renderToolTip(stack, x, y);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {

        int numChar = Character.getNumericValue(typedChar);
        if ((te.getField(TileEconomyBase.FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) { //Ensures keys input are only numbers or backspace type keys

            if ((!fieldPrice.getText().contains(".")) || keyCode != 52) {
                if (this.fieldPrice.textboxKeyTyped(typedChar, keyCode)) setCost();
            }

            if (fieldPrice.getText().length() > 0)
                if (fieldPrice.getText().substring(fieldPrice.getText().length() - 1).equals("."))
                    fieldPrice.setMaxStringLength(fieldPrice.getText().length() + 2);
            if (!fieldPrice.getText().contains(".")) fieldPrice.setMaxStringLength(7);

            if (this.fieldAmnt.textboxKeyTyped(typedChar, keyCode)) setAmnt();

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

            updateTextField();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    private void setCost() {
        if (this.fieldPrice.getText().length() > 0) {
            int newCost = 0;

            if (fieldPrice.getText().contains(".")) {
                if (fieldPrice.getText().lastIndexOf(".") + 1 != fieldPrice.getText().length()) {
                    if (fieldPrice.getText().lastIndexOf(".") + 2 == fieldPrice.getText().length()) {
                        newCost = Integer.valueOf(this.fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1) + "0");
                    } else {
                        newCost = Integer.valueOf(this.fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1));
                    }
                }

                if (fieldPrice.getText().lastIndexOf(".") != 0)
                    newCost += Integer.valueOf(this.fieldPrice.getText().substring(0, fieldPrice.getText().lastIndexOf("."))) * 100;

            } else {
                newCost = Integer.valueOf(this.fieldPrice.getText()) * 100;
            }

            te.setItemCost(newCost);
            PacketSetItemCostToServer pack = new PacketSetItemCostToServer();
            pack.setData(newCost, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void setAmnt() {
        if (this.fieldAmnt.getText().length() > 0) {
            int amount = Integer.valueOf(fieldAmnt.getText());

            if (te.isSlotEmpty()) {
                amount = 1;
            } else if (Integer.valueOf(fieldAmnt.getText()) > te.getInvItemStack().getMaxStackSize())
                amount = te.getInvItemStack().getMaxStackSize();

            if (amount == 0) amount = 1;

            te.setItemAmnt(amount);

            te.setItemAmnt(amount);
            PacketSetItemAmntToServer pack = new PacketSetItemAmntToServer();
            pack.setData(amount, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    private void updateTextField() {
        fieldPrice.setText(UtilMethods.translateMoney(te.getItemCost()));
        fieldAmnt.setText(Integer.toString(te.getItemAmnt()));
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (te.getField(TileEconomyBase.FIELD_MODE) == 1) {
            int i = (mouseX - (this.width - this.xSize) / 2);
            int j = (mouseY - (this.height - this.ySize) / 2);

            int startY = -31;
            int startX = 43;
            int row = ((j - startY) / 18);
            int column = ((i - startX) / 18);
            int slot = column + (row * 5);
            int selectedSlot = te.getField(TileVending.FIELD_SELECTED);

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
            Loop: for (int element : movementArray) {
                if (element == displacement) {
                    possibleValue = true;
                    break Loop;
                }
            }

            if (possibleValue) {
                if (Math.abs(displacement) == 1 || Math.abs(displacement) == 5) {
                    if(te.getSlotBundle(selectedSlot) != selectedSlot) {
                        te.setSlotBundle(slot, selectedSlot);
                        PacketSetItemBundleToServer pack = new PacketSetItemBundleToServer();
                        pack.setData(slot, selectedSlot, te.getPos());
                        PacketHandler.INSTANCE.sendToServer(pack);
                    }
                } else {
                    int previousSlot = slot;

                    switch (direction) {
                        case 0: previousSlot--;
                            break;
                        case 1: previousSlot++;
                            break;
                        case 2: previousSlot -= 5;
                            break;
                        case 3: previousSlot += 5;
                            break;
                    }

                    if(te.getSlotBundle(previousSlot) == selectedSlot) {
                        te.setSlotBundle(slot, selectedSlot);
                        PacketSetItemBundleToServer pack = new PacketSetItemBundleToServer();
                        pack.setData(slot, selectedSlot, te.getPos());
                        PacketHandler.INSTANCE.sendToServer(pack);
                    }
                }
            }
        }
    }
}
