package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.client.gui.util.TabButton;
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
import java.util.*;

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
    private final int buttonStart=3;
    private final int buttonTotal=7;

    public GuiGuide(ItemStack item){
        this.item = item;
        this.page = 0; //Todo save and pull last page to NBT
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


        this.buttonList.add(0, new TabButton("Back", 0, guiLeft + 14, guiTop + 13, 1, 219, 21, 11, "", BACKGROUND_TEXTURE));
        this.buttonList.add(1, new TabButton("Prev", 1, guiLeft + 20, guiTop + 155, 3, 207, 18, 10, "", BACKGROUND_TEXTURE));
        this.buttonList.add(2, new TabButton("Next", 2, guiLeft + 100, guiTop + 155, 3, 194, 18, 10, "", BACKGROUND_TEXTURE));
        this.buttonList.get(0).visible = false;
        this.buttonList.get(1).visible = false;
        this.buttonList.get(2).visible = false;

        for(int i = buttonStart; i < buttonTotal; i++) {
            this.buttonList.add(i,new TabButton(Integer.toString(i), i, guiLeft + 35 + (20 * i), guiTop + 90, 0, 220, 16, 16, "", BACKGROUND_TEXTURE));
            this.buttonList.get(i).visible = false;
        }
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
                String introText = "Starting or trying to get involved in an economy? This is your guide to the items and blocks that will help you survive and thrive in this economical era.";
                fontRendererObj.drawSplitString(I18n.format(introText), wrapX, wrapY, wrapWidth, Color.BLACK.getRGB());
                GL11.glPopMatrix();

                for(int k = buttonStart; k < buttonTotal; k++) {
                    this.buttonList.set(k,new TabButton(Integer.toString(k), k, guiLeft + 26 + (25 * (k-3)), guiTop + 90, 0, 250, 16, 16, "", BACKGROUND_TEXTURE));
               }

                if ((i >= 26 && i <= 26 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 14, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), guiLeft + 26, guiTop + 90);

                if ((i >= 51 && i <= 51 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), 30, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), guiLeft + 51, guiTop + 90);

                if ((i >= 76 && i <= 76 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), 47, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), guiLeft + 76, guiTop + 90);

                if ((i >= 101 && i <= 101 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, wrapScale);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), 64, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), guiLeft + 101, guiTop + 90);
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

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {

    }
}
