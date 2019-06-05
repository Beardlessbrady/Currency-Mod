package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.UtilMethods;
import beardlessbrady.modcurrency.block.TileEconomyBase;
import beardlessbrady.modcurrency.network.PacketHandler;
import beardlessbrady.modcurrency.network.PacketOutChangeToServer;
import beardlessbrady.modcurrency.network.PacketSetFieldToServer;
import beardlessbrady.modcurrency.network.PacketSetItemCostToServer;
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
import net.minecraft.world.World;
import org.lwjgl.input.Mouse;
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

    GuiTextField fieldPrice;

    TileVending te;


    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    private static final int FIELDPRICE = 0;

    public GuiVending(EntityPlayer entityPlayer, TileVending te){
        super(new ContainerVending(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(BUTTONCHANGE,  i + 143 , j + 27, 20, 20, "$"));

        String mode = (te.getIntField(te.FIELD_MODE) == 1)? "STOCK" : "SELL";
        this.buttonList.add(new GuiButton(BUTTONADMIN,  i + 137 , j - 42, 32, 20, mode));

        this.fieldPrice = new GuiTextField(FIELDPRICE, fontRenderer, i + 217, j + 30, 50, 8);        //Setting Costs
        this.fieldPrice.setTextColor(Integer.parseInt("C35763", 16));
        this.fieldPrice.setEnableBackgroundDrawing(false);
        this.fieldPrice.setMaxStringLength(7);
        this.fieldPrice.setEnabled(false);
        this.fieldPrice.setVisible(false);
        this.fieldPrice.setText("0.00");

        updateTextField();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX,mouseY);
        this.fieldPrice.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        if(te.isOwner()){
            buttonList.get(BUTTONADMIN).visible = true;
        }else{
            buttonList.get(BUTTONADMIN).visible = false;
        }

        fontRenderer.drawString(I18n.format("tile.modcurrency:blockvending.name"), 8, -42,  Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 114,  Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 149, -8, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 80, 68, Color.lightGray.getRGB());

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);
        fontRenderer.drawStringWithShadow(I18n.format("guivending.cash"), 7, -40, Integer.parseInt("2DB22F", 16));
        fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, -30, Integer.parseInt("ffffff", 16));
        fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.FIELD_LONG_CASHRESERVE))), 15, -30, Integer.parseInt("ffffff", 16));
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        drawItemStackSize();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        //STOCK MODE, SELL MODE
        if(te.getIntField(TileVending.FIELD_MODE) == 1) {
            drawSelectionOverlay();
            drawAdminPanel();

            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            GL11.glScalef(0.7F, 0.7F, 0.8F);
            fontRenderer.drawStringWithShadow(I18n.format("guivending.profit"), 7, -10, Integer.parseInt("3D78E0", 16));
            fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, 0, Integer.parseInt("ffffff", 16));
            fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.FIELD_LONG_CASHREGISTER))), 15, 0, Integer.parseInt("ffffff", 16));
            GL11.glPopMatrix();
            GL11.glDisable(GL11.GL_DEPTH_TEST);

            this.fieldPrice.setEnabled(true);
            this.fieldPrice.setVisible(true);

            this.buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE,  i + 143 , j + 27, 20, 20, TextFormatting.BLUE + "$"));
        }else{

            this.fieldPrice.setEnabled(false);
            this.fieldPrice.setVisible(false);

            this.buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE,  i + 143 , j + 27, 20, 20, TextFormatting.GREEN + "$"));
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id){
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getIntField(te.FIELD_MODE) == 1) ? 0 : 1, te.FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                String mode = (te.getIntField(te.FIELD_MODE) == 0)? "STOCK" : "SELL";
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

        for(int j=0; j<columnCount; j++) {
            for (int i = 0; i < 5; i++) {
                if (te.getItemSize(i + (5 * j)) != 0 && te.getItemSize(i + (5 * j)) > 0) {
                    num = Integer.toString(te.getItemSize(i + (5 * j)));
                } else if (!te.getInvItemStack(i + (5*j)).isEmpty()){
                    num = TextFormatting.RED + "Out";
                } else {
                    num = " ";
                }

                if(num.length() == 1) num = "  " + num;
                if(num.length() == 2) num = " " + num;

                this.fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
            }
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void drawAdminPanel(){
        if(te.getIntField(TileVending.FIELD_MODE) == 1){
            drawTexturedModalRect(177, 0, 0, 202, 106, 54);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), 216, 10, Integer.parseInt("ffffff", 16));

            String itemName = "[" + te.getSelectedName() + "]";
            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRenderer.drawString(I18n.format(itemName), 322 - (itemName.length() *2 ), 28,  Integer.parseInt("7B232D", 16));
            GL11.glPopMatrix();

            fontRenderer.drawStringWithShadow(I18n.format("$"), 210, 30, Color.lightGray.getRGB());
        }
    }

    private void drawSelectionOverlay() {
        if(te.getIntField(TileVending.FIELD_MODE) == 1) {
            int slotId = te.getIntField(TileVending.FIELD_SELECTED);
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

            drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 0, 172, 29, 29);
        }
    }

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (this.width - this.xSize) / 2);
        int j = (y - (this.height - this.ySize) / 2);

        if(j <= 58 && j >= -31 && i >= 43) {
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

            //Adding Vending Strings
            TextFormatting color = TextFormatting.YELLOW;
            if (te.getIntField(TileEconomyBase.FIELD_MODE) == 0) {
                if (te.canAfford(slot, 1)) {
                     color = TextFormatting.GREEN;
                 } else {
                     color = TextFormatting.RED;
                }

                //list.add("x"  + " for " + color + "$"  + UtilMethods.translateMoney(te.getItemCost(slot)));
                list.add(color + "$"  + UtilMethods.translateMoney(te.getItemCost(slot)));
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
    public void handleMouseInput() throws IOException {
        if(te.getIntField(TileVending.FIELD_MODE) == 1) {
            int i = Integer.signum(Mouse.getEventDWheel());
            if (i == 1) {
                if (te.getIntField(TileVending.FIELD_SELECTED) == 24) {
                    te.setIntField(TileVending.FIELD_SELECTED, 0);
                    te.setSelectedName(te.getInvItemStack(0).getDisplayName());

                    updateTextField();
                } else {
                    te.setIntField(TileVending.FIELD_SELECTED, te.getIntField(TileVending.FIELD_SELECTED) + 1);
                    te.setSelectedName(te.getInvItemStack(te.getIntField(TileVending.FIELD_SELECTED)).getDisplayName());

                    updateTextField();
                }
            } else if (i == -1) {
                if (te.getIntField(TileVending.FIELD_SELECTED) == 0) {
                    te.setIntField(TileVending.FIELD_SELECTED, 24);
                    te.setSelectedName(te.getInvItemStack(24).getDisplayName());

                    updateTextField();
                } else {
                    te.setIntField(TileVending.FIELD_SELECTED, te.getIntField(TileVending.FIELD_SELECTED) - 1);
                    te.setSelectedName(te.getInvItemStack(te.getIntField(TileVending.FIELD_SELECTED)).getDisplayName());

                    updateTextField();
                }
            }
        }
        super.handleMouseInput();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((te.getIntField(te.FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) { //Ensures keys input are only numbers or backspace type keys

            if ((keyCode == 52 && !fieldPrice.getText().contains(".")) || keyCode != 52) {
                if (this.fieldPrice.textboxKeyTyped(typedChar, keyCode)) setCost();
            }

            if (fieldPrice.getText().length() > 0)
                if (fieldPrice.getText().substring(fieldPrice.getText().length() - 1).equals("."))
                    fieldPrice.setMaxStringLength(fieldPrice.getText().length() + 2);
            if (!fieldPrice.getText().contains(".")) fieldPrice.setMaxStringLength(7);

        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(te.getIntField(TileVending.FIELD_MODE) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            fieldPrice.mouseClicked(mouseX, mouseY, mouseButton);

            updateTextField();
        }else {
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

    private void updateTextField() {
        fieldPrice.setText(UtilMethods.translateMoney(te.getItemCost()));
    }
}
