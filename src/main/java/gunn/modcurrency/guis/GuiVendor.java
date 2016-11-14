package gunn.modcurrency.guis;

import gunn.modcurrency.containers.ContainerVendor;
import gunn.modcurrency.handler.PacketHandler;
import gunn.modcurrency.network.PacketSendIntData;
import gunn.modcurrency.network.PacketSendItemToServer;
import gunn.modcurrency.tiles.TileVendor;
import gunn.modcurrency.util.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-02.
 */
public class GuiVendor extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTabTexture.png");
    private TileVendor tilevendor;
    private GuiTextField nameField;
    private boolean gearExtended;

    public GuiVendor(InventoryPlayer invPlayer, TileVendor tile) {
        super(new ContainerVendor(invPlayer, tile));
        tilevendor = tile;
        xSize = 176;
        ySize = 235;
        gearExtended = false;
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
        this.nameField.setText(String.valueOf(tilevendor.getItemCost()));
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
        fontRendererObj.drawString(I18n.format("container.vendor.name"), 5, 7, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("container.vendor_dollarAmount.name") + ": $" + tilevendor.getField(0), 5, 16, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("container.vendor_playerInv.name"), 4, 142, Color.darkGray.getRGB());

        if (tilevendor.getField(2) == 1) {
            drawIcons();

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
        
        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, "Change"));        

        if (tilevendor.getField(2) == 1) {
            this.buttonList.add(new CustomButton(1, i + 176, j + 20, 0, 21, 21, 22, "", TAB_TEXTURE));   //Lock Tab
            this.buttonList.add(new CustomButton(2, i + 176, j + 43, 0, 0, 21, 22, "", TAB_TEXTURE));   //Gear Tab
            this.nameField = new GuiTextField(0, fontRendererObj, i + 217, j + 72, 45, 10);        //Setting Costs
            this.nameField.setTextColor(Integer.parseInt("0099ff", 16));
            this.nameField.setEnableBackgroundDrawing(false);
            this.nameField.setMaxStringLength(7);
            this.nameField.setEnabled(true);
            updateTextField();
        }
    }

    private void drawIcons() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        
        //Draw Lock Icon
        if (tilevendor.getField(1) == 1) {
            drawTexturedModalRect(180, 23, 245, 15, 11, 16);    
        } else {
            drawTexturedModalRect(180, 25, 245, 0, 11, 14); 
        }
        
        //Draw Gear Icon and Extended Background
        if (gearExtended) drawTexturedModalRect(176, 43, 27, 0, 91, 54);
        drawTexturedModalRect(174, 46, 237, 32, 19, 15);
        
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
        drawTexturedModalRect(24 + (18 * slotRow), 30 + (18 * slotColumn), 177, 0, 20, 20);
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
        super.mouseClicked(mouseX, mouseY, mouseButton);
        nameField.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 2) updateTextField();
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
        }
    }
}