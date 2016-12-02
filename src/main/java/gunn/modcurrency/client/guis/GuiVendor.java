package gunn.modcurrency.client.guis;

import gunn.modcurrency.common.blocks.containers.ContainerVendor;
import gunn.modcurrency.common.core.handler.PacketHandler;
import gunn.modcurrency.common.core.network.PacketSendIntData;
import gunn.modcurrency.common.core.network.PacketSendItemToServer;
import gunn.modcurrency.common.blocks.tiles.TileVendor;
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
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed under a
 * Custom License: https://github.com/BeardlessBrady/Currency-Mod/blob/master/LICENSE
 *
 * File Created on 2016-11-02.
 */
public class GuiVendor extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTabTexture.png");
    private TileVendor tilevendor;
    private GuiTextField nameField;
    private boolean gearExtended, creativeExtended;

    public GuiVendor(InventoryPlayer invPlayer, TileVendor tile) {
        super(new ContainerVendor(invPlayer, tile));
        tilevendor = tile;
        xSize = 176;
        ySize = 235;
        gearExtended = false;
        creativeExtended = false;
    }

    //Sends packet of new cost to server
    private void setCost() {
        if (this.nameField.getText().length() > 0) {
            int newCost = Integer.valueOf(this.nameField.getText());

            PacketSendIntData pack = new PacketSendIntData();
            pack.setData(newCost, tilevendor.getPos(), 1);

            PacketHandler.INSTANCE.sendToServer(pack);
            tilevendor.getWorld().notifyBlockUpdate(tilevendor.getPos(), tilevendor.getBlockType().getDefaultState(), tilevendor.getBlockType().getDefaultState(), 3);
        }
    }

    //Updates Cost text field
    private void updateTextField() {
        this.nameField.setText(String.valueOf(tilevendor.getItemCost(tilevendor.getField(3) - 37)));
    }

    //<editor-fold desc="Drawing Gui Assets--------------------------------------------------------------------------------------------------">
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (gearExtended) nameField.drawTextBox();
        //if (creativeExtended) System.out.println("OPEN");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        fontRendererObj.drawString(I18n.format("Vending Machine"), 5, 7, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("Inventory"), 4, 142, Color.darkGray.getRGB());
        if(tilevendor.getField(2) == 0) fontRendererObj.drawString(I18n.format("Cash") + ": $" + tilevendor.getField(0), 5, 16, Color.darkGray.getRGB());
        
        if (tilevendor.getField(2) == 1) {
            drawIcons();
            fontRendererObj.drawString(I18n.format("Profit") + ": $" + tilevendor.getField(4), 5, 16, Color.darkGray.getRGB());

            if (gearExtended) {
                fontRendererObj.drawString(I18n.format("Slot Settings"), 197, 51, Integer.parseInt("42401c", 16));
                fontRendererObj.drawString(I18n.format("Slot Settings"), 196, 50, Integer.parseInt("fff200", 16));
                fontRendererObj.drawString(I18n.format("Cost:"), 183, 73, Integer.parseInt("211d1b", 16));
                fontRendererObj.drawString(I18n.format("Cost:"), 184, 72, Color.lightGray.getRGB());
                fontRendererObj.drawString(I18n.format("$"), 210, 72, Integer.parseInt("0099ff", 16));
                GL11.glPushMatrix();
                    GL11.glScaled(0.7, 0.7, 0.7);
                    fontRendererObj.drawString(I18n.format("[" + tilevendor.getSelectedName() + "]"), 257, 91, Integer.parseInt("001f33", 16));
                    fontRendererObj.drawString(I18n.format("[" + tilevendor.getSelectedName() + "]"), 258, 90, Integer.parseInt("0099ff", 16));
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        String ChangeButton = "Change";
        
        if(tilevendor.getField(2) == 1) ChangeButton = "Profit";
        
        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, ChangeButton));        

        if (tilevendor.getField(2) == 1) {
            this.buttonList.add(new CustomButton(1, i + 176, j + 20, 0, 21, 21, 22, "", TAB_TEXTURE));   //Lock Tab
            this.buttonList.add(new CustomButton(2, i + 176, j + 43, 0, 0, 21, 21, "", TAB_TEXTURE));   //Gear Tab
            if(tilevendor.getField(5) == 1)this.buttonList.add(new CustomButton(3, i + 176, j + 65, 0, 44, 21, 21, "", TAB_TEXTURE));   //Creative Tab

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
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        //Draw Lock Icon
        if (tilevendor.getField(1) == 1) {
            drawTexturedModalRect(180, 23, 245, 15, 11, 16);
        } else {
            drawTexturedModalRect(180, 25, 245, 0, 11, 14);
        }
        
        //Draw Gear Icon and Extended Background
        if (gearExtended) drawTexturedModalRect(176, 43, 27, 0, 91, 47);
        drawTexturedModalRect(174, 46, 237, 32, 19, 15);

        //Draw Creative Icon
        if(tilevendor.getField(5) == 1) {
            if(!gearExtended) {
                this.buttonList.remove(3);
                this.buttonList.add(new CustomButton(3, i + 176, j + 65, 0, 44, 21, 21, "", TAB_TEXTURE));   //Creative Tab
                if(creativeExtended) drawTexturedModalRect(176, 65, 27, 48, 91, 47);
                drawTexturedModalRect(175, 71, 237, 48, 19, 9);
            }else{
                this.buttonList.remove(3);
                this.buttonList.add(new CustomButton(3, i + 176, j + 91, 0, 44, 21, 21, "", TAB_TEXTURE));   //Creative Tab
                if(creativeExtended) drawTexturedModalRect(176, 91, 27, 48, 91, 47);
                drawTexturedModalRect(175, 97, 237, 48, 19, 9);
            }
        }



        //Draw Selected Slot Overlay
        int slotId = tilevendor.getField(3) - 37;
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

    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (this.width - this.xSize) / 2);
        int j = (y - (this.height - this.ySize) / 2);

        if(j < 140 && j > 30) {
            IItemHandler itemHandler = this.tilevendor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            int startX = 43;
            int startY = 31;

            int row = ((i - startX) / 18);
            int column = ((j - startY) / 18);

            int slot = row + (column * 5);

            ItemStack currStack = tilevendor.getStack(slot + 1);

            List<String> list = new ArrayList<>();
            list.add(String.valueOf(currStack.getDisplayName()));
            list.add("$" + (String.valueOf(tilevendor.getItemCost(slot))));


            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            this.drawHoveringText(list, x, y, (font == null ? fontRendererObj : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            tilevendor.getWorld().notifyBlockUpdate(tilevendor.getPos(), tilevendor.getBlockType().getDefaultState(), tilevendor.getBlockType().getDefaultState(), 3);
        }else{
            super.renderToolTip(stack, x, y);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Keyboard and Mouse Inputs-------------------------------------------------------------------------------------------">
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar);
        if ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205)) { //Ensures keys input are only numbers or backspace type keys
            if (this.nameField.textboxKeyTyped(typedChar, keyCode)) setCost();
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }
    
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if(tilevendor.getField(2) == 1) {
            super.mouseClicked(mouseX, mouseY, mouseButton);
            nameField.mouseClicked(mouseX, mouseY, mouseButton);
            if (mouseButton == 2) updateTextField();
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
                pack0.setBlockPos(tilevendor.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1:         //Lock Button
                PacketSendIntData pack1 = new PacketSendIntData();
                if (tilevendor.getField(1) == 1) { 
                    pack1.setData(0, tilevendor.getPos(), 0);
                } else { 
                    pack1.setData(1, tilevendor.getPos(), 0);
                }
                PacketHandler.INSTANCE.sendToServer(pack1);
                tilevendor.getWorld().notifyBlockUpdate(tilevendor.getPos(), tilevendor.getBlockType().getDefaultState(), tilevendor.getBlockType().getDefaultState(), 3);
                break;
            case 2:
                gearExtended = !gearExtended;
                break;
            case 3:
                creativeExtended = !creativeExtended;
        }
    }
}