package gunn.modcurrency.client.guis;

import gunn.modcurrency.tiles.TileVendor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

/**
 * This class was created by <Brady Gunn>.
 * Distributed with the Currency-Mod for Minecraft.
 *
 * The Currency-Mod is open source and distributed
 * under the General Public License
 *
 * File Created on 2016-11-07.
 */
public class GuiTab_Lock extends Gui{
    protected int xSize,ySize,xPos,yPos;
    protected static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/tab_gui.png");
    private TileVendor tilevendor;

    public GuiTab_Lock(TileVendor tilevendor){
        this.tilevendor = tilevendor;
        xSize = 26;
        ySize = 32;
        xPos = 328;
        yPos = 40;
    }
    
    protected void drawBackgroundLayer() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexturedModalRect(xPos ,yPos ,0 ,50 , xSize, ySize);
        
        System.out.println("LOCKED =" + tilevendor.getField(1));
        if(tilevendor.getField(1) == 1) {
            //Lock [Locked]
            drawTexturedModalRect(xPos + 4, yPos + 7, 241, 1, 14, 19);
        }else {
            //Lock [UnLocked]
            drawTexturedModalRect(xPos + 4, yPos + 2, 241, 24, 14, 24);
        }
    }
}
