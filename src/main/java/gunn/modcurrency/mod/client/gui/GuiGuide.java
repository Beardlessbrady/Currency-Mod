package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-06-10
 */
public class GuiGuide extends GuiScreen{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiguide.png");

    private int guiTop, guiLeft;
    protected final int xSize = 146;
    protected final int ySize = 180;
    private final int wrapX = 19;
    private final int wrapY = 46;
    private final int wrapWidth = 148;
    private final float wrapScale = 0.8F;


    private ItemStack item;
    private int page;

    public GuiGuide(ItemStack item){
        this.item = item;
        this.page = 0; //Todo save and pull last page to NBT
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int i = (mouseX - (this.width - this.xSize) / 2);
        int j = (mouseY - (this.height - this.ySize) / 2);

        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 20, 1, xSize, ySize);
        RenderHelper.enableGUIStandardItemLighting();

        switch (page) {
            case 0:     //Front Page
                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 33, guiTop + 10, 45, 192, 77, 23);    //Logo

                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft - wrapScale, guiTop - wrapScale, wrapScale);
                GL11.glScalef(wrapScale, wrapScale, wrapScale);
                String introText = "So you want to start an economy? Sick and tired of trading items with estimated value ehh? This book will be your guide to learning how it all works, good luck.";
                fontRendererObj.drawSplitString(I18n.format(introText), wrapX, wrapY, wrapWidth, Color.BLACK.getRGB());
                GL11.glPopMatrix();


                if ((i >= 35 && i <= 35 + 15) && (j >= 90 && j <= 90 + 15)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 20, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), guiLeft + 35, guiTop + 90);

                if ((i >= 55 && i <= 55 + 15) && (j >= 90 && j <= 90 + 15)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), 33, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), guiLeft + 55, guiTop + 90);

                if ((i >= 75 && i <= 75 + 15) && (j >= 90 && j <= 90 + 15)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), 47, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), guiLeft + 75, guiTop + 90);

                if ((i >= 95 && i <= 95 + 15) && (j >= 90 && j <= 90 + 15)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), 60, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), guiLeft + 95, guiTop + 90);
                //this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet), guiLeft + 35, guiTop + 110);
            break;
            case 1: //
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }






    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() == keyCode) this.mc.displayGuiScreen(null);  //Closes gui when inventory button is clicked
        super.keyTyped(typedChar, keyCode);
    }
}
