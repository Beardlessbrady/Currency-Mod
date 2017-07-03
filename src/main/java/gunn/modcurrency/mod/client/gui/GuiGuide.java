package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.client.gui.util.TabButton;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
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

        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(true);


        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 20, 1, xSize, ySize);
        RenderHelper.enableGUIStandardItemLighting();

        String text = "";

        switch (page) {
            case 0:
                //<editor-fold desc="Root Page">
                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 33, guiTop + 10, 45, 192, 77, 23);    //Logo

                text = "Starting or trying to get involved in an economy? This is your guide to the items and blocks that will help you survive and thrive in this economical era.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                for(int k = buttonStart; k < buttonTotal; k++) {
                    this.buttonList.set(k,new TabButton(Integer.toString(k), k, guiLeft + 26 + (25 * (k-3)), guiTop + 90, 0, 250, 16, 16, "", BACKGROUND_TEXTURE));
               }

                if ((i >= 26 && i <= 26 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 14, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), guiLeft + 26, guiTop + 90);

                if ((i >= 51 && i <= 51 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), 30, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet,1,1), guiLeft + 51, guiTop + 90);

                if ((i >= 76 && i <= 76 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), 47, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), guiLeft + 76, guiTop + 90);

                if ((i >= 101 && i <= 101 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), 64, 57);
                    GL11.glPopMatrix();
                }else this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), guiLeft + 101, guiTop + 90);
                //this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet), guiLeft + 35, guiTop + 110);
                //</editor-fold>
            break;
            case 1:
                //<editor-fold desc="Dollar Bill Page">
                //Item Icon
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 1.5F, guiTop  + 1.5F, 0.8F);
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 16, 8);
                GL11.glPopMatrix();

                //Chapter Title
                text = TextFormatting.BOLD + "The Dollar Bill";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 50, guiTop + 21, 148, Color.BLACK.getRGB());


                text = "These " + TextFormatting.DARK_GREEN + "Coloured " + TextFormatting.BLACK + "pieces of paper are the most important part of your economy.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                text = "Each has a higher value then the last. This way you won't have one-thousand one dollar bills filling your inventory. You can of course " + TextFormatting.BOLD + "convert "
                        + TextFormatting.RESET + "between each bill in any crafting table.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 85, 112, Color.BLACK.getRGB());

                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 0), guiLeft + 35, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 1), guiLeft + 55, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 2), guiLeft + 75, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 3), guiLeft + 96, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 4), guiLeft + 45, guiTop + 70);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote,1, 5), guiLeft + 85, guiTop + 70);


                //TODO Implement back,next,prev buttons. Save last page before going to next, save current page for when reopening book after closing
                //</editor-fold>
            break;
        }

        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(false);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void clean(){
        for(int i = 0; i < buttonTotal; i++){
            this.buttonList.get(i).visible = false;
        }
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode() == keyCode) this.mc.displayGuiScreen(null);  //Closes gui when inventory button is clicked
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0: //Back
                break;
            case 1: //Next
                break;
            case 2: //Prev
                break;
            case 3:
                clean();
                this.page =1;
                break;
        }
    }
}
