package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.client.container.ContainerVendExchange;
import gunn.modcurrency.mod.tile.TileVendExchange;
import gunn.modcurrency.mod.core.network.*;
import gunn.modcurrency.mod.client.util.TabButtonList;
import gunn.modcurrency.mod.tile.TileSeller;
import gunn.modcurrency.mod.tile.TileVendor;
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
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * File Created on 2016-11-02.
 */
public class GuiVendExchange extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortabtexture.png");
    private TileVendExchange tile;
    private GuiTextField nameField, amountField;
    private boolean creativeExtended, nameExtended;
    private TabButtonList tabList;
    int yOffset = 0;

    private String header;

    public GuiVendExchange(InventoryPlayer invPlayer, TileVendExchange te) {
        super(new ContainerVendExchange(invPlayer, te));
        tile = te;
        xSize = 176;
        ySize = 235;
        creativeExtended = false;
        nameExtended = false;

        if (tile instanceof TileVendor) header = "tile.modcurrency:blockvendor.name";
        if (tile instanceof TileSeller) {
            header = "tile.modcurrency:blockseller.name";
        }

    }

    //Sends packet of new cost to server
    private void setCost() {
        if (this.nameField.getText().length() > 0) {
            int newCost = Integer.valueOf(this.nameField.getText());

            PacketSetItemCostToServer pack = new PacketSetItemCostToServer();
            pack.setData(newCost, tile.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }
    }

    private void setAmount() {
        if (tile.getField(2)== 1) {
            if (this.amountField.getText().length() > 0) {
                int newAmount = Integer.valueOf(this.amountField.getText());
                if (Integer.valueOf(this.amountField.getText()) == 0) newAmount = -1;

                PacketSetItemAmountToServer pack = new PacketSetItemAmountToServer();
                pack.setData(newAmount, tile.getPos(), tile.getField(3) - 37);

                PacketHandler.INSTANCE.sendToServer(pack);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
            }
        }
    }

    //Updates Cost text field
    private void updateTextField() {
        this.nameField.setText(String.valueOf(tile.getItemCost(tile.getField(3) - 37)));
        if (tile instanceof TileSeller) {
            if (((TileSeller) tile).getItemAmount(tile.getField(3) - 37) == -1) {
                this.amountField.setText("0");
            } else {
                this.amountField.setText(String.valueOf(((TileSeller) tile).getItemAmount(tile.getField(3) - 37)));
            }
        }
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        super.onResize(mcIn, w, h);
        PacketSetGearTabStateToServer pack = new PacketSetGearTabStateToServer();
        pack.setData(0, tile.getPos());
        PacketHandler.INSTANCE.sendToServer(pack);

        creativeExtended = false;
        nameExtended = false;
    }

    //<editor-fold desc="Drawing Gui Assets--------------------------------------------------------------------------------------------------">
    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String ChangeButton = "Change";

        if(tile instanceof TileVendor) if(tile.getField(2) == 1) ChangeButton = "Profit";
        if(tile instanceof TileSeller) ChangeButton = "Cash";


        tabList = new TabButtonList(this.buttonList, i - 21, j + 20);
        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, ChangeButton));

        if (tile.getField(2) == 1) {
            //tabList.addTab("Name", TAB_TEXTURE, 0, 88, 6);
            tabList.addTab("Lock", TAB_TEXTURE, 0, 22, 1);
            tabList.addTab("Gear", TAB_TEXTURE, 0, 0, 2);
            //tabList.addTab("Fuzzy", TAB_TEXTURE, 0, 66, 5);
            tabList.setOpenState("Gear", 26);
            if(tile.getField(5) == 1) {
                tabList.addTab("Creative", TAB_TEXTURE, 0, 44, 3);
                tabList.setOpenState("Creative", 26);
                this.buttonList.add(new GuiButton(4, i + 198, j + 85, 45, 20, "BORKED"));
                this.buttonList.get(4).visible = false;
            }
            this.nameField = new GuiTextField(0, fontRendererObj, i -50, j + 88 - 28, 45, 10);        //Setting Costs
            this.nameField.setTextColor(Integer.parseInt("0099ff", 16));
            this.nameField.setEnableBackgroundDrawing(false);
            this.nameField.setMaxStringLength(7);
            this.nameField.setEnabled(true);

            if(tile instanceof TileSeller){
                this.amountField = new GuiTextField(0, fontRendererObj, i -45, j + 97 - 28, 45, 10);        //Setting Costs
                this.amountField.setTextColor(Integer.parseInt("0099ff", 16));
                this.amountField.setEnableBackgroundDrawing(false);
                this.amountField.setMaxStringLength(7);
                this.amountField.setEnabled(true);
            }
            updateTextField();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (tile.getField(8) == 1 && tile.getField(2) == 1) {
            nameField.drawTextBox();
            if (tile instanceof TileSeller) amountField.drawTextBox();
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        //Draw Input Icons
        if (tile instanceof TileVendor) {
            if (tile.getField(2) == 0) drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 0, 15, 15);
            if (tile.getField(2) == 1) drawTexturedModalRect(guiLeft + 152, guiTop + 9, 215, 0, 15, 15);
        }
        if (tile instanceof TileSeller) {
            if (tile.getField(2) == 0) drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 17, 15, 15);
            if (tile.getField(2) == 1) drawTexturedModalRect(guiLeft + 152, guiTop + 9, 198, 0, 15, 15);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int i = (mouseX - (this.width - this.xSize) / 2);
        int j = (mouseY - (this.height - this.ySize) / 2);

        fontRendererObj.drawString(I18n.format(header), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("tile.modcurrency:gui.playerinventory"), 4, 142, Color.darkGray.getRGB());

        if (tile.getField(2) == 0) {
            //<editor-fold desc="Sell Mode">
            fontRendererObj.drawString(I18n.format("Cash") + ": $" + tile.getField(0), 5, 15, Color.darkGray.getRGB());

            if (tile instanceof TileVendor) if (tile.getField(9) == 1)
                fontRendererObj.drawString(I18n.format("Wallet") + ": $" + tile.getField(10), 5, 23, Integer.parseInt("3abd0c", 16));

            String fundAmount = Integer.toString(tile.getField(4));
            if (tile.getField(6) == 1) fundAmount = "Infinite";
            if (tile instanceof TileSeller)
                fontRendererObj.drawString(I18n.format("tile.modcurrency:guisell.funds") + ": " + "$" + fundAmount, 5, 23, Color.darkGray.getRGB());

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
            drawTexturedModalRect(14, 31, 177, 21, 18, 108);
            //</editor-fold>
        } else {
            //<editor-fold desc="Edit Mode">
            drawIcons();
            drawSelectionOverlay();

            String profitName = "tile.modcurrency:guisell.profit";
            String profitAmnt = Integer.toString(tile.getField(4));

            if (tile instanceof TileSeller) {
                profitName = "tile.modcurrency:guisell.funds";
                if (tile.getField(6) == 1) profitAmnt = "Infinite";
                if (tile.getField(8) == 1){
                    fontRendererObj.drawString(I18n.format("Amount:"), -84, 98 - 28, Integer.parseInt("211d1b", 16));
                    fontRendererObj.drawString(I18n.format("Amount:"), -83, 97 - 28, Color.lightGray.getRGB());
                }
            }

            int vendOffset = 0;
            if (tile instanceof TileVendor) vendOffset = -10;

            fontRendererObj.drawString(I18n.format(profitName) + ": $" + profitAmnt, 5, 16, Color.darkGray.getRGB());

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
                fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), -117, 115 + vendOffset, Integer.parseInt("001f33", 16));
                fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), -118, 114 + vendOffset, Integer.parseInt("0099ff", 16));
                GL11.glPopMatrix();
            }else yOffset = 0;
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

        for (int k = 0; k < tabList.getSize(); k++) {
            int tabLoc = 22 * (k + 1);

            int offSet2 = 0;
            if (tile.getField(8) == 1){
                offSet2 = 26;
            }

            switch (k) {
                case 0:
                    if (tile.getField(1) == 0) {
                        drawTexturedModalRect(-19, tabLoc, 236, 1, 19, 16);
                    } else drawTexturedModalRect(-19, tabLoc, 216, 1, 19, 16);
                    break;
                case 1:
                    if (tile.getField(8) == 1) {
                        drawTexturedModalRect(-91, tabLoc - 2, 27, 0, 91, 47);
                    }
                    drawTexturedModalRect(-19, tabLoc, 236, 19, 19, 16); //Icon
                    break;
                case 2:
                    if (tile.getField(5) == 1) {
                        if(tile.getField(8) == 1){
                            yOffset = 26;
                        }else yOffset = 0;
                        if (creativeExtended) {
                            drawTexturedModalRect(-91, tabLoc + yOffset -2, 27, 48, 91, 47);
                            this.buttonList.set(4, (new GuiButton(4, i - 69, j + tabLoc + 18 + yOffset, 45, 20, ((tile.getField(6) == 1) ? "Enabled" : "Disabled"))));
                        } else {
                            this.buttonList.get(4).visible = false;
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
        int xOffset = 0;

        for (int k = 0; k < tabList.getSize(); k++) {
            yMin = (22 * (k)) + 20;
            yMax = yMin + 21;

            if (((i >= xMin && i <= xMax) && (j >= yMin && j <= yMax) && ((tile.getField(8) == 0) || k <= 1)) || ((k > 1 && j >= yMin + 26 && j <= yMax + 26) && tile.getField(8) == 1)) {
                List<String> list = new ArrayList<>();
                switch (k) {
                 //   case 0: //Todo: Hiding Name Tab as its not implemented yet
                        //if (nameExtended) yOffset = -27;
                        /*
                        list.add("Title Tab");
                        list.add("Name shows up");
                        list.add("above store");
                        */

                        //this.drawHoveringText(list, -111, 24 * (k + 1) + yOffset, fontRendererObj);
                  //  break;
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
                  //  case :
                       // if (tile.getField(8) == 1) yOffset = 41; //Todo
                        /*
                        list.add("NBT Tab");
                        list.add("Set Fuzzy/Specific");
                        list.add("for slots");
                        */
                      //  this.drawHoveringText(list, /*-133*/ -111, 23 * (k + 1) + yOffset + vendOffset, fontRendererObj);
                 //   break;
                    case 2:
                        if(creativeExtended) yOffset= -21;
                        if(tile.getField(8) == 1){
                            yOffset= 34;
                        }
                        if(tile.getField(8) == 1 && creativeExtended){
                            yOffset= 82;
                        }
                        if(tile instanceof TileVendor) xOffset= 3;

                        list.add("Creative Tab");
                        if (tile instanceof TileVendor) {
                            list.add("Infinite Stock");
                        } else list.add("Infinite Funds");
                        this.drawHoveringText(list, -107 + xOffset, 24 * (k + 1) + yOffset, fontRendererObj);
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

            if(tile instanceof TileVendor && tile.getField(2) == 0){
                if(!((TileVendor) tile).canAfford(slot)){
                    list.add(TextFormatting.RED + "$" + (String.valueOf(tile.getItemCost(slot))));
                }else{
                    list.add("$" + (String.valueOf(tile.getItemCost(slot))));
                }
                if(((TileVendor) tile).isGhostSlot(slot)) {
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

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(tile.getField(2) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            nameField.mouseClicked(mouseX, mouseY, mouseButton);
            if(tile instanceof TileSeller) amountField.mouseClicked(mouseX, mouseY, mouseButton);
            if (tile.getField(8) == 1 && mouseButton == 0) updateTextField();
        }else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Keyboard and Mouse Inputs-------------------------------------------------------------------------------------------">
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((tile.getField(2) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205))) { //Ensures keys input are only numbers or backspace type keys
            if (this.nameField.textboxKeyTyped(typedChar, keyCode)) setCost();
            if(tile instanceof TileSeller) if (this.amountField.textboxKeyTyped(typedChar, keyCode)) setAmount();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    //</editor-fold>

    @Override
    //Button Actions
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:         //Change Button
                PacketItemSpawnToServer pack0 = new PacketItemSpawnToServer();
                pack0.setBlockPos(tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1: //Lock Button
                PacketSetLockTabToServer pack1 = new PacketSetLockTabToServer();
                pack1.setData((tile.getField(1) == 1) ? 0 : 1, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack1);
                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case 2: //Gear Button
                tabList.checkOpenState("Gear", tile.getField(8) == 0);
                int newGear = tile.getField(8) == 1 ? 0 : 1;
                PacketSetGearTabStateToServer pack2 = new PacketSetGearTabStateToServer();
                pack2.setData(newGear, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack2);
                break;
            case 3:
                creativeExtended = !creativeExtended;
                break;
            case 4:
                PacketSetInfiniteToServer pack4 = new PacketSetInfiniteToServer();
                pack4.setData((tile.getField(6) == 1) ? 0 : 1, tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack4);
                break;
         //   case 5:
        //        PacketSetFuzzyToServer pack5 = new PacketSetFuzzyToServer();
         //       pack5.setData((tile.getField(11) == 1) ? 0 : 1, tile.getPos());
         //       PacketHandler.INSTANCE.sendToServer(pack5);
           //     break;
            case 6:
                nameExtended = !nameExtended;
        }
    }
}