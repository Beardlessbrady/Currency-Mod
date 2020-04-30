package beardlessbrady.modcurrency2.utilities;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-06-06
 */

public class GuiButtonTextured extends GuiButton {
  protected ResourceLocation CUSTOM_TEXTURES;
    int minU, minV, maxU, maxV, openY, buttonid, hoverXShift;
    String name;
    ResourceLocation textureLoc;
    boolean openState;

    //Allows a button with a custom texture, yes it is very butchered together
    public GuiButtonTextured(String name, int buttonId, int x, int y, int minU, int minV, int maxU, int maxV, int hoverXShift, String buttonText, ResourceLocation texture) {
        super(buttonId, x, y, minU - maxU, minV - maxV, buttonText);
        this.CUSTOM_TEXTURES = texture;
        this.width = maxU;
        this.height = maxV;
        this.minU = minU;
        this.minV = minV;
        this.maxU = maxU;
        this.maxV = maxV;
        this.hoverXShift = hoverXShift;

        this.name = name;
        this.buttonid = buttonId;
        this.textureLoc = texture;
        this.openState = false;


    }

    @Override
    public void drawButton(Minecraft mc , int mouseX, int mouseY, float p_191745_4_){
        if (this.visible) {
            mc.getTextureManager().bindTexture(CUSTOM_TEXTURES);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = (this.hovered == true) ? 1 : 0;
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.drawTexturedModalRect(this.x, this.y, minU + (i * hoverXShift), minV, maxU, maxV);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }

    public int getButtonY(){
        if(openState) return y + openY;
        return y;
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