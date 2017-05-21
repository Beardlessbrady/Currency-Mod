package gunn.modcurrency.mod.client.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-05-21
 */
public class TabButton extends GuiButton{
    protected ResourceLocation CUSTOM_TEXTURES;
    int minU, minV, maxU, maxV, openY, buttonid;
    String name;
    ResourceLocation textureLoc;
    boolean openState;

    //Allows a button with a custom texture, yes it is very butchered together
    public TabButton(String name, int buttonId, int x, int y, int minU, int minV, int maxU, int maxV, String buttonText, ResourceLocation texture) {
        super(buttonId, x, y, minU - maxU, minV - maxV, buttonText);
        this.CUSTOM_TEXTURES = texture;
        this.width = maxU;
        this.height = maxV;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;

        this.name = name;
        this.buttonid = buttonId;
        this.textureLoc = texture;
        this.openState = false;


    }

    /**
     * Draws this button to the screen.
     */
    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(CUSTOM_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.xPosition, this.yPosition, minU, minV, maxU, maxV);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public int getButtonY(){
        if(openState) return yPosition + openY;
        return yPosition;
    }

    public boolean openState(){
        return this.openState;
    }

    public void setOpenState(boolean op, int opY){
        this.openState = op;
        this.openY = opY;
    }

    public int openExtY(){
        if(openState) return openY;
        return 0;
    }


}
