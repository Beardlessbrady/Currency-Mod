package gunn.modcurrency.client.guis;

import gunn.modcurrency.common.containers.ContainerBuySell;
import gunn.modcurrency.common.core.handler.PacketHandler;
import gunn.modcurrency.common.core.network.PacketSendIntData;
import gunn.modcurrency.common.core.network.PacketSendItemToServer;
import gunn.modcurrency.api.ModTile;
import gunn.modcurrency.common.tiles.TileSeller;
import gunn.modcurrency.common.tiles.TileVendor;
import gunn.modcurrency.common.core.util.CustomButton;
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
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2016-11-02.
 */
public class GuiBuySell extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTabTexture.png");
    private ModTile tile;
    private GuiTextField nameField;
    private boolean gearExtended, creativeExtended;
    
    private String header;

    public GuiBuySell(InventoryPlayer invPlayer, ModTile te) {
        super(new ContainerBuySell(invPlayer, te));
        tile = te;
        xSize = 176;
        ySize = 235;
        gearExtended = false;
        creativeExtended = false;

        if(tile instanceof TileVendor) header = "Vending Machine";
        if(tile instanceof TileSeller) header = "Exchange Machine";
    }
    
    //Sends packet of new cost to server
    private void setCost() {
        if (this.nameField.getText().length() > 0) {
            int newCost = Integer.valueOf(this.nameField.getText());

            PacketSendIntData pack = new PacketSendIntData();
            pack.setData(newCost, tile.getPos(), 1);

            PacketHandler.INSTANCE.sendToServer(pack);
            tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
        }
    }

    //Updates Cost text field
    private void updateTextField() {
        this.nameField.setText(String.valueOf(tile.getItemCost(tile.getField(3) -37)));
    }

    //<editor-fold desc="Drawing Gui Assets--------------------------------------------------------------------------------------------------">
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (gearExtended) nameField.drawTextBox();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(I18n.format(header), 5, 6, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("Inventory"), 4, 142, Color.darkGray.getRGB());
        if(tile.getField(2) == 0){
            fontRendererObj.drawString(I18n.format("Cash") + ": $" + tile.getField(0), 5, 15, Color.darkGray.getRGB());

            String fundAmount = Integer.toString(tile.getField(4));
            if(tile.getField(6) == 1) fundAmount = "Infinite";
            if(tile instanceof TileSeller) fontRendererObj.drawString(I18n.format("Funds") + ": $" + fundAmount, 5, 23, Color.darkGray.getRGB());
        }

        if (tile.getField(2) == 1) {
            drawIcons();

            String profitName = "Profit";
            String profitAmnt = Integer.toString(tile.getField(4));
            if(tile instanceof TileSeller){
                profitName = "Funds";
                if(tile.getField(6)==1) profitAmnt = "Infinite";
            }
            fontRendererObj.drawString(I18n.format(profitName) + ": $" + profitAmnt, 5, 16, Color.darkGray.getRGB());

            if (gearExtended) {
                fontRendererObj.drawString(I18n.format("Slot Settings"), 197, 51, Integer.parseInt("42401c", 16));
                fontRendererObj.drawString(I18n.format("Slot Settings"), 196, 50, Integer.parseInt("fff200", 16));
                fontRendererObj.drawString(I18n.format("Cost:"), 183, 73, Integer.parseInt("211d1b", 16));
                fontRendererObj.drawString(I18n.format("Cost:"), 184, 72, Color.lightGray.getRGB());
                fontRendererObj.drawString(I18n.format("$"), 210, 72, Integer.parseInt("0099ff", 16));
                
                String selectedName = tile.getSelectedName();
                
                GL11.glPushMatrix();
                    GL11.glScaled(0.7, 0.7, 0.7);
                    fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), 257, 91, Integer.parseInt("001f33", 16));
                    fontRendererObj.drawString(I18n.format("[" + selectedName + "]"), 258, 90, Integer.parseInt("0099ff", 16));
                GL11.glPopMatrix();
            }
            if(creativeExtended){
                if(!gearExtended) {
                    fontRendererObj.drawString(I18n.format("Infinite Stock"), 197, 73, Integer.parseInt("42401c", 16));
                    fontRendererObj.drawString(I18n.format("Infinite Stock"), 196, 72, Integer.parseInt("fff200", 16));
                }else{
                    fontRendererObj.drawString(I18n.format("Infinite Stock"), 197, 99, Integer.parseInt("42401c", 16));
                    fontRendererObj.drawString(I18n.format("Infinite Stock"), 196, 98, Integer.parseInt("fff200", 16));
                }
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String ChangeButton = "Change";

        if(tile instanceof TileVendor) if(tile.getField(2) == 1) ChangeButton = "Profit";
        if(tile instanceof TileSeller) ChangeButton = "Cash";
        
        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, ChangeButton));

        if (tile.getField(2) == 1) {
            this.buttonList.add(new CustomButton(1, i + 176, j + 20, 0, 21, 21, 22, "", TAB_TEXTURE));   //Lock Tab
            this.buttonList.add(new CustomButton(2, i + 176, j + 43, 0, 0, 21, 21, "", TAB_TEXTURE));   //Gear Tab
            if(tile.getField(5) == 1) {
                this.buttonList.add(new CustomButton(3, i + 176, j + 65, 0, 44, 21, 21, "", TAB_TEXTURE));   //Creative Tab
                this.buttonList.add(new GuiButton(4, i + 198, j + 85, 45, 20, "BORKED"));
                this.buttonList.get(4).visible = false;
            }
            this.nameField = new GuiTextField(0, fontRendererObj, i + 217, j + 72, 45, 10);        //Setting Costs
            this.nameField.setTextColor(Integer.parseInt("0099ff", 16));
            this.nameField.setEnableBackgroundDrawing(false);
            this.nameField.setMaxStringLength(7);
            this.nameField.setEnabled(true);
            updateTextField();
        }
    }

    private void drawIcons() {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);

        //Draw Lock Icon
        if (tile.getField(1) == 1) {
            drawTexturedModalRect(180, 23, 245, 15, 11, 16);
        } else {
            drawTexturedModalRect(180, 25, 245, 0, 11, 14);
        }
        
        //Draw Gear Icon and Extended Background
        if (gearExtended) drawTexturedModalRect(176, 43, 27, 0, 91, 47);
        drawTexturedModalRect(174, 46, 237, 32, 19, 15);

        //Draw Creative Icon
        if(tile.getField(5) == 1) {
            if(!gearExtended) {
                this.buttonList.set(3,(new CustomButton(3, i + 176, j + 65, 0, 44, 21, 21, "", TAB_TEXTURE)));   //Creative Tab
                if(creativeExtended && tile.getField(5) == 1) {
                    this.buttonList.set(4,(new GuiButton(4, i + 198, j + 85, 45, 20, ((tile.getField(6) == 1) ? "Enabled" : "Disabled"))));
                    drawTexturedModalRect(176, 65, 27, 48, 91, 47);
                }else if(!creativeExtended && tile.getField(5) == 1) this.buttonList.get(4).visible = false;
                drawTexturedModalRect(175, 71, 237, 48, 19, 9);
            }else{
                this.buttonList.set(3,(new CustomButton(3, i + 176, j + 91, 0, 44, 21, 21, "", TAB_TEXTURE)));   //Creative Tab
                 if(creativeExtended  && tile.getField(5) == 1) {
                     this.buttonList.set(4,(new GuiButton(4, i + 198, j + 111, 45, 20, ((tile.getField(6) == 1) ? "Enabled" : "Disabled"))));
                     drawTexturedModalRect(176, 91, 27, 48, 91, 47);
                 }else if (!creativeExtended && tile.getField(5) == 1) this.buttonList.get(4).visible = false;
                 drawTexturedModalRect(175, 97, 237, 48, 19, 9);
            }
        }

        if(gearExtended) {
            //Draw Selected Slot Overlay
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
            list.add("$" + (String.valueOf(tile.getItemCost(slot))));

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

    //<editor-fold desc="Keyboard and Mouse Inputs-------------------------------------------------------------------------------------------">
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
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(tile.getField(2) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            nameField.mouseClicked(mouseX, mouseY, mouseButton);
            if (gearExtended && mouseButton == 0) updateTextField();
        }else{
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }
    //</editor-fold>

    @Override
    //Button Actions
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:         //Change Button
                PacketSendItemToServer pack0 = new PacketSendItemToServer();
                pack0.setBlockPos(tile.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1: //Lock Button
                PacketSendIntData pack1 = new PacketSendIntData();
                pack1.setData((tile.getField(1) == 1) ? 0 : 1, tile.getPos(), 0);
                PacketHandler.INSTANCE.sendToServer(pack1);

                tile.getWorld().notifyBlockUpdate(tile.getPos(), tile.getBlockType().getDefaultState(), tile.getBlockType().getDefaultState(), 3);
                break;
            case 2:
                gearExtended = !gearExtended;
                PacketSendIntData pack2 = new PacketSendIntData();
                pack2.setData(gearExtended ? 1 : 0, tile.getPos(), 4);
                PacketHandler.INSTANCE.sendToServer(pack2);
                break;
            case 3:
                creativeExtended = !creativeExtended;
                break;
            case 4:
                PacketSendIntData pack4 = new PacketSendIntData();
                pack4.setData((tile.getField(6) == 1) ? 0 : 1, tile.getPos(), 3);
                PacketHandler.INSTANCE.sendToServer(pack4);
                break;
        }
    }
}