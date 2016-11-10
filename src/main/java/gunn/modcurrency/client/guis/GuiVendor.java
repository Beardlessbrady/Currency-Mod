package gunn.modcurrency.client.guis;

import gunn.modcurrency.client.containers.ContainerVendor;
import gunn.modcurrency.handler.PacketHandler;
import gunn.modcurrency.network.PacketSendData;
import gunn.modcurrency.network.PacketSendItem;
import gunn.modcurrency.tiles.TileVendor;
import gunn.modcurrency.util.CustomButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

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
public class GuiVendor extends GuiContainer{

    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTexture.png");
    protected static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiVendorTabTexture.png");
    private TileVendor tilevendor;
    private boolean gearExtended = false;

    public GuiVendor(InventoryPlayer invPlayer, TileVendor tilevendor){
        super(new ContainerVendor(invPlayer, tilevendor));
        this.tilevendor = tilevendor;
        
        xSize = 176;
        ySize = 235;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        fontRendererObj.drawString(I18n.format("container.vendor.name"),5,7, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("container.vendor_dollarAmount.name") + ": $" + tilevendor.getField(0),5,16, Color.darkGray.getRGB());
        fontRendererObj.drawString(I18n.format("container.vendor_playerInv.name"),4,142, Color.darkGray.getRGB());
        
        drawLockIcon();
        drawGearIcon();
    }

    @Override
    public void initGui() {
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        super.initGui();
        
        this.buttonList.add(new GuiButton(0, i + 103, j + 7, 45, 20, "Change"));
        this.buttonList.add(new CustomButton( 1, i+ 176, j+ 20, 0, 50, 26, 32, "", TAB_TEXTURE));
        this.buttonList.add(new CustomButton(2, i + 176, j + 54, 0, 0, 26, 32, "", TAB_TEXTURE));
    }
    
    public void drawLockIcon(){
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        if (tilevendor.getField(1) == 1) {
            //Lock [Locked] 
            drawTexturedModalRect(180 , 25, 240, 0, 16, 23);
        } else {
            //Lock [UnLocked]
            drawTexturedModalRect(180 , 22, 240, 24, 16, 26);
        }
    }
    
    public void drawGearIcon(){
        Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        //0
        drawTexturedModalRect(176 , 58, 233, 51, 23, 24);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id) {
            case 0:         //Change Button
                PacketSendItem pack0 = new PacketSendItem();
                pack0.setBlockPos(tilevendor.getPos());
                PacketHandler.INSTANCE.sendToServer(pack0);
                break;
            case 1:         //Lock Button
                PacketSendData pack1 = new PacketSendData();
                if (tilevendor.getField(1) == 1) { //is True
                    pack1.setData(0,tilevendor.getPos(),0);
                } else { // is False
                    pack1.setData(1,tilevendor.getPos(),0);
                }
                PacketHandler.INSTANCE.sendToServer(pack1);
                tilevendor.getWorld().notifyBlockUpdate(tilevendor.getPos(), tilevendor.getBlockType().getDefaultState(), tilevendor.getBlockType().getDefaultState(), 3);
                break;
            case 2:
                if(gearExtended == true){
                    gearExtended = false;
                }else{
                    gearExtended = true;
                }
                break;
        }
    }
}