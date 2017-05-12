package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.client.gui.util.TabButtonList;
import gunn.modcurrency.mod.container.ContainerVending;
import gunn.modcurrency.mod.network.PacketHandler;
import gunn.modcurrency.mod.network.PacketItemSpawnToServer;
import gunn.modcurrency.mod.network.PacketSetFieldToServer;
import gunn.modcurrency.mod.network.PacketSetItemCostToServer;
import gunn.modcurrency.mod.tileentity.TileVending;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

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
    private GuiTextField nameField;
    private boolean creativeExtended;
    private TabButtonList tabList;

    public GuiVending(InventoryPlayer invPlayer, TileVending te) {
        super(new ContainerVending(invPlayer, te));
        tile = te;
        xSize = 176;
        ySize = 235;
        creativeExtended = false;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String ChangeButton = "Change";
        if (tile.getField(2) == 1) ChangeButton = "Profit";

        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, ChangeButton));
        this.buttonList.add(new GuiButton(1, i + 198, j + 85, 45, 20, "BORKED"));
        this.buttonList.get(1).visible = false;

        tabList = new TabButtonList(this.buttonList, i - 21, j + 20);
        tabList.addTab("Mode", TAB_TEXTURE, 0, 88, 2);
        tabList.addTab("Lock", TAB_TEXTURE, 0, 22, 3);
        tabList.addTab("Gear", TAB_TEXTURE, 0, 0, 4);
        tabList.addTab("Creative", TAB_TEXTURE, 0, 44, 5);
        this.buttonList.get(3).visible = false;
        this.buttonList.get(4).visible = false;
        this.buttonList.get(5).visible = false;

        this.nameField = new GuiTextField(0, fontRendererObj, i - 50, j + 88 - 28, 45, 10);        //Setting Costs
        this.nameField.setTextColor(Integer.parseInt("0099ff", 16));
        this.nameField.setEnableBackgroundDrawing(false);
        this.nameField.setMaxStringLength(7);
        this.nameField.setEnabled(false);

        //updateTextField();
    }

    private void setCost() {
        if (this.nameField.getText().length() > 0) {
            int newCost = Integer.valueOf(this.nameField.getText());

            PacketSetItemCostToServer pack = new PacketSetItemCostToServer();
            pack.setData(newCost, tile.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }
    }

    private void updateTextField() {
        this.nameField.setText(String.valueOf(tile.getItemCost(tile.getField(3) - 37)));
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
        if (tile.getField(8) == 1 && tile.getField(2) == 1) {
            nameField.drawTextBox();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        if(tile.isTwoBlock()){
            drawTexturedModalRect(guiLeft + 43, guiTop + 31, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 103, 7, 210, 90, 18);
            drawTexturedModalRect(guiLeft + 43, guiTop + 121, 7, 210, 90, 18);
        }
        drawTexturedModalRect(guiLeft + 43, guiTop + 49, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 67, 7, 210, 90, 18);
        drawTexturedModalRect(guiLeft + 43, guiTop + 85, 7, 210, 90, 18);

        //Draw Input Icons
        if (tile.getField(2) == 0) {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 0, 15, 15);
        } else {
            drawTexturedModalRect(guiLeft + 152, guiTop + 9, 215, 0, 15, 15);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int i = (mouseX - (this.width - this.xSize) / 2);
        int j = (mouseY - (this.height - this.ySize) / 2);

        if(tile.getField(2) == 1){
            this.buttonList.get(3).visible = true;
            this.buttonList.get(4).visible = true;
            this.buttonList.get(5).visible = true;
        }else{
            this.buttonList.get(3).visible = false;
            this.buttonList.get(4).visible = false;
            this.buttonList.get(5).visible = false;

        }



        fontRendererObj.drawString(I18n.format("tile.modcurrency:blockvending.name"), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 4, 142, Color.darkGray.getRGB());

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);

        drawIcons();
        if (tile.getField(2) == 0) {    //SELL MODE
            fontRendererObj.drawString(I18n.format("Cash") + ": $" + tile.getField(0), 5, 15, Color.darkGray.getRGB());

            if (tile.getField(9) == 1)
                fontRendererObj.drawString(I18n.format("Wallet") + ": $" + tile.getField(10), 5, 23, Integer.parseInt("3abd0c", 16));

        } else {    //EDIT MODE
            drawSelectionOverlay();
            drawTexturedModalRect(14, 31, 177, 21, 18, 108);

            String profitName = "WEEORK";
            String profitAmnt = Integer.toString(tile.getField(4));

            fontRendererObj.drawString(I18n.format(profitName) + ": $" + profitAmnt, 5, 16, Color.darkGray.getRGB());
            int yOffset = 0;

            if (tile.getField(8) == 1) {
                yOffset = 26;
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.slotsettings"), -81, 71 - 21, Integer.parseInt("42401c", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.slotsettings"), -80, 70 - 21, Integer.parseInt("fff200", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.cost"), -84, 88 - 28, Integer.parseInt("211d1b", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.cost"), -83, 87 - 28, Color.lightGray.getRGB());
                fontRendererObj.drawString(I18n.format("$"), -57, 88 - 28, Integer.parseInt("0099ff", 16));

                String selectedName = tile.getSelectedName();

                GL11.glPushMatrix();
                GL11.glScaled(0.7, 0.7, 0.7);
                fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), -117, 105, Integer.parseInt("001f33", 16));
                fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), -118, 104, Integer.parseInt("0099ff", 16));
                GL11.glPopMatrix();
            }

            if (creativeExtended) {
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.tabs.infinity.infinitestock"), -86, 115 + yOffset - 44, Integer.parseInt("42401c", 16));
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.tabs.infinity.infinitestock"), -85, 114 + yOffset - 44, Integer.parseInt("fff200", 16));
            }

            drawToolTips(i, j);
            //</editor-fold>
        }

    }

    private void drawIcons() {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);

        int size = 1;
        if(tile.getField(2) == 1) size = tabList.getSize();


        for (int k = 0; k < size; k++) {
            int tabLoc = 22 * (k + 1);

            int offSet2 = 0;
            if (tile.getField(8) == 1) {
                offSet2 = 26;
            }

            switch (k) {
                case 0:
                    drawTexturedModalRect(-19, tabLoc, 236, 73, 19, 16);
                    break;
                case 1:
                    if (tile.getField(1) == 0) {
                        drawTexturedModalRect(-19, tabLoc, 236, 1, 19, 16);
                    } else drawTexturedModalRect(-19, tabLoc, 216, 1, 19, 16);

                    break;
                case 2:
                    if (tile.getField(8) == 1) {
                        drawTexturedModalRect(-91, tabLoc - 2, 27, 0, 91, 47);
                    }
                    drawTexturedModalRect(-19, tabLoc, 236, 19, 19, 16); //Icon
                    break;
                case 3:
                    if (tile.getField(5) == 1) {
                        int yOffset = 0;
                        if (tile.getField(8) == 1) yOffset = 26;

                        if (creativeExtended) {
                            drawTexturedModalRect(-91, tabLoc + yOffset - 2, 27, 48, 91, 47);
                            this.buttonList.set(1, (new GuiButton(1, i - 69, j + tabLoc + 18 + yOffset, 45, 20, ((tile.getField(6) == 1) ? "Enabled" : "Disabled"))));
                        } else {
                            this.buttonList.get(1).visible = false;
                        }
                        drawTexturedModalRect(-19, tabLoc + offSet2, 236, 37, 19, 16);
                    }
                    break;
            }
        }
    }

    private void drawSelectionOverlay() {
        if (tile.getField(8) == 1) {
            int slotId = tile.getField(3) - 37;
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

            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect(24 + (18 * slotRow), 30 + (18 * slotColumn), 177, 0, 20, 20); //Selection Box
        }
    }

    private void drawToolTips(int i, int j) {
        int xMin = -21;
        int xMax = 0;
        int yMin, yMax;
        int yOffset = 0;

        for (int k = 0; k < tabList.getSize(); k++) {
            yMin = (22 * (k)) + 20;
            yMax = yMin + 21;

            if (((i >= xMin && i <= xMax) && (j >= yMin && j <= yMax) && ((tile.getField(8) == 0) || k <= 1)) || ((k > 1 && j >= yMin + 26 && j <= yMax + 26) && tile.getField(8) == 1)) {
                java.util.List<String> list = new ArrayList<>();
                switch (k) {
                    case 0:
                        if (tile.getField(8) == 1) yOffset = -5;
                        list.add("Lock Tab");
                        list.add("Enable/Disable");
                        list.add("pipe interaction");
                        this.drawHoveringText(list, -116, 24 * (k + 1) + yOffset, fontRendererObj);
                        break;
                    case 1:
                        if (tile.getField(8) == 1) yOffset = -29;
                        list.add("Settings Tab");
                        list.add("Set item costs");
                        list.add("and prices");
                        this.drawHoveringText(list, -114, 24 * (k + 1) + yOffset, fontRendererObj);
                        break;
                    case 2:
                        if(creativeExtended) yOffset= -21;
                        if(tile.getField(8) == 1){
                            yOffset= 34;
                        }
                        if(tile.getField(8) == 1 && creativeExtended){
                            yOffset= 82;
                        }

                        list.add("Creative Tab");
                        list.add("Infinite Stock");
                        this.drawHoveringText(list, -104, 24 * (k + 1) + yOffset, fontRendererObj);
                        break;
                }
            }
        }
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
            ItemStack currStack = tile.getStack(slot);

            List<String> list = new ArrayList<>();
            list.add(String.valueOf(currStack.getDisplayName()));

            if(tile.getField(2) == 0){
                if(!tile.canAfford(slot)){
                    list.add(TextFormatting.RED + "$" + (String.valueOf(tile.getItemCost(slot))));
                }else{
                    list.add("$" + (String.valueOf(tile.getItemCost(slot))));
                }
                if(tile.isGhostSlot(slot)) {
                    list.add(TextFormatting.RED + "OUT OF STOCK");
                }
            }else{
                list.add("$" + (String.valueOf(tile.getItemCost(slot))));
            }

            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
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
        if(tile.getField(2) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
//            nameField.mouseClicked(mouseX, mouseY, mouseButton);
            if (tile.getField(8) == 1 && mouseButton == 0) updateTextField();
        }else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((tile.getField(2) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205))) { //Ensures keys input are only numbers or backspace type keys
            if (this.nameField.textboxKeyTyped(typedChar, keyCode)) setCost();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    //Button Actions
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:  //Change Button
                PacketItemSpawnToServer pack0 = new PacketItemSpawnToServer();
                pack0.setBlockPos(tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1: //Infinite? Button
                PacketSetFieldToServer pack4 = new PacketSetFieldToServer();
                pack4.setData((tile.getField(6) == 1) ? 0 : 1, 6, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack4);
                break;
            case 2: //Mode Button
                PacketSetFieldToServer pack5 = new PacketSetFieldToServer();
                pack5.setData((tile.getField(2) == 1) ? 0 : 1, 2, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack5);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case 3: //Lock Button
                PacketSetFieldToServer pack1 = new PacketSetFieldToServer();
                pack1.setData((tile.getField(1) == 1) ? 0 : 1, 1, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case 4: //Gear Button
                tabList.checkOpenState("Gear", tile.getField(8) == 0);
                int newGear = tile.getField(8) == 1 ? 0 : 1;
                PacketSetFieldToServer pack2 = new PacketSetFieldToServer();
                pack2.setData(newGear, 8, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2);
                break;
            case 5: //Creative Button
                creativeExtended = !creativeExtended;
                break;
        }
    }
    //</editor-fold>





}
