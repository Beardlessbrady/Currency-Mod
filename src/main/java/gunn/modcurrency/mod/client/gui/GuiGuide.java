package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.client.gui.util.TabButton;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
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
    private static final ResourceLocation VENDOR_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortexture.png");
    private static final ResourceLocation TAB_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guivendortabtexture.png");

    private int guiTop, guiLeft;
    protected final int xSize = 146;
    protected final int ySize = 180;


    private ItemStack item;
    private String page;
    private final int buttonStart=3;
    private final int buttonTotal=7;

    public GuiGuide(ItemStack item){
        this.item = item;
        this.page = "root";
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;


        this.buttonList.add(0, new TabButton("Back", 0, guiLeft + 14, guiTop + 10, 1, 219, 21, 11, "", BACKGROUND_TEXTURE));
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
            case "root":
                //<editor-fold desc="Root Page">
                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 33, guiTop + 10, 45, 192, 77, 23);    //Logo

                text = "Starting or trying to get involved in an economy? This is your guide to the items and blocks that will help you survive and thrive in this economical era.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                for (int k = buttonStart; k < buttonTotal; k++) {
                    this.buttonList.set(k, new TabButton(Integer.toString(k), k, guiLeft + 26 + (25 * (k - 3)), guiTop + 90, 0, 250, 16, 16, "", BACKGROUND_TEXTURE));
                }

                if ((i >= 26 && i <= 26 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 14, 57);
                    GL11.glPopMatrix();
                } else
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), guiLeft + 26, guiTop + 90);

                if ((i >= 51 && i <= 51 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet, 1, 1), 30, 57);
                    GL11.glPopMatrix();
                } else
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet, 1, 1), guiLeft + 51, guiTop + 90);

                if ((i >= 76 && i <= 76 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), 47, 57);
                    GL11.glPopMatrix();
                } else
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending), guiLeft + 76, guiTop + 90);

                if ((i >= 101 && i <= 101 + 16) && (j >= 90 && j <= 90 + 16)) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(guiLeft + 1.5F, guiTop + 1.5F, 0.8F);
                    GL11.glScalef(1.5F, 1.5F, 1.5F);
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), 64, 57);
                    GL11.glPopMatrix();
                } else
                    this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger), guiLeft + 101, guiTop + 90);
                //this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet), guiLeft + 35, guiTop + 110);
                //</editor-fold>
                break;
            case "bill":
                //<editor-fold desc="Dollar Bill Page">
                //Item Icon
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 1.5F, guiTop + 1.5F, 0.8F);
                GL11.glScalef(1.5F, 1.5F, 1.5F);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote), 16, 8);
                GL11.glPopMatrix();

                //Chapter Title
                text = TextFormatting.BOLD + "Dollar Bill";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 52, guiTop + 21, 148, Color.BLACK.getRGB());


                text = "These " + TextFormatting.DARK_GREEN + "Coloured " + TextFormatting.BLACK + "pieces of paper are the most important part of your economy.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                text = "Each has a higher value then the last. This way you won't have a million one dollar bills filling your inventory. You can of course " + TextFormatting.BOLD + "convert "
                        + TextFormatting.RESET + "between each bill in any crafting table.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 85, 112, Color.BLACK.getRGB());

                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 0), guiLeft + 35, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 1), guiLeft + 55, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 2), guiLeft + 75, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 3), guiLeft + 96, guiTop + 60);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 4), guiLeft + 45, guiTop + 70);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemBanknote, 1, 5), guiLeft + 85, guiTop + 70);


                this.buttonList.get(0).visible = true;
                //</editor-fold>
                break;
            case "wallet":
                //<editor-fold desc="Wallet Page">
                //Item Icon
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 1.2F, guiTop + 1.2F, 0.8F);
                GL11.glScalef(1.2F, 1.2F, 1.2F);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModItems.itemWallet, 1,1), 23, 11);
                GL11.glPopMatrix();

                //Chapter Title
                text = TextFormatting.BOLD + "Wallet";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 50, guiTop + 21, 148, Color.BLACK.getRGB());


                text = " This revolutionary piece of leather is a convenient way to keep all your hard earned cash in one place.";
               fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

               text = " Unfortunately it can" + TextFormatting.BOLD + " ONLY " + TextFormatting.RESET + "hold bills from the mod, preventing you from taking advantage of cheap portable storage.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 72, 112, Color.BLACK.getRGB());

                text =  "There is also " + TextFormatting.RED + "special " + TextFormatting.BLACK + "interaction features with vending machines...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 122, 112, Color.BLACK.getRGB());


                this.buttonList.get(0).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "wallet1":
                //<editor-fold desc="Wallet Page 01">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.5F, guiTop + 0.5F, 0.8F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(VENDOR_TEXTURE);
                drawTexturedModalRect(184, 62, 198, 0, 16,16);
                GL11.glPopMatrix();

                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 44, guiTop + 84, 167, 0, 58,58);

                ItemStack[] ingredients = recipe(new ItemStack(ModItems.itemWallet));

                this.itemRender.renderItemIntoGUI(ingredients[0], guiLeft + 46, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredients[1], guiLeft + 65, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredients[2], guiLeft + 84, guiTop + 86);

                this.itemRender.renderItemIntoGUI(ingredients[3], guiLeft + 46, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredients[4], guiLeft + 65, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredients[5], guiLeft + 84, guiTop + 105);

                this.itemRender.renderItemIntoGUI(ingredients[6], guiLeft + 46, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredients[7], guiLeft + 65, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredients[8], guiLeft + 84, guiTop + 124);

                text = "When placed in the vending machine input slot [   ] all purchases will go through the wallet, you will never need to collect your change again!";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = "Wallet Recipe";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 49, guiTop + 75, 112, Color.BLACK.getRGB());



                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                //</editor-fold>
                break;
            case "vending":
                //<editor-fold desc="Vending Machine Page">
                //Item Icon
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 1.2F, guiTop + 1.2F, 0.8F);
                GL11.glScalef(1.2F, 1.2F, 1.2F);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockVending, 1, 0), 23, 11);
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(96, 124, 236, 73, 19,17);
                GL11.glPopMatrix();

                //Chapter Title
                text = TextFormatting.BOLD + "Vending Machine";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 47, guiTop + 21, 148, Color.BLACK.getRGB());


                text = " Why have a villager sell your goods when you can have a machine do it?";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                text = " Once your vending machine is placed it will instantly recognize you as its owner. Clicking the [    ] icon in the gui will change it between " + TextFormatting.BOLD + "'Stock'" +
                TextFormatting.RESET + " and " + TextFormatting.BOLD + "'Sell'" + TextFormatting.RESET + " mode.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 62, 112, Color.BLACK.getRGB());

                text = "You can also create a " + TextFormatting.BOLD + "double" + TextFormatting.RESET + " vending machine...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 120, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "vending1":
                //<editor-fold desc="Vending Machine Page 01">
                text = "...by simply placing one on top of another one. Besides the " + TextFormatting.BOLD + "aesthetic" + TextFormatting.RESET +
                        " change the machine also gets " + TextFormatting.BOLD + "double the inventory space.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = TextFormatting.UNDERLINE + "Sell Mode" + TextFormatting.RESET + ". This is where players can" + TextFormatting.BOLD +
                        " buy " + TextFormatting.RESET + "items. When hovering over items a " + TextFormatting.ITALIC + "price " +
                        TextFormatting.RESET + "will be shown. If it is " + TextFormatting.RED + "RED" + TextFormatting.BLACK +
                        " it means the player didn't put enough cash in the machine to afford it. If it is " + TextFormatting.GREEN + "GREEN" + TextFormatting.BLACK +
                        " it means the player has...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 72, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "vending2":
                //<editor-fold desc="Vending Machine Page 02">
            GL11.glPushMatrix();
            GL11.glTranslatef(guiLeft + 0.5F, guiTop + 0.5F, 0.8F);
            GL11.glScalef(0.5F, 0.5F, 0.5F);
            Minecraft.getMinecraft().getTextureManager().bindTexture(VENDOR_TEXTURE);
            drawTexturedModalRect(73, 98, 198, 0, 16,16);
            GL11.glPopMatrix();

            text = "...enough money to buy " + TextFormatting.BOLD + "at least one " + TextFormatting.RESET + "of the items. Insert cash by placing it in the [   ] slot or by shift " +
                    "clicking it. You can also click the [" + TextFormatting.BOLD + "Change" + TextFormatting.RESET + "] button to get your remaining cash back. You can get different amounts of items by:" +
                    "";
            fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

            text = "\u2022 Right click = 1 item";
            fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 105, 112, Color.BLACK.getRGB());
            text = "\u2022 Left click = 10 items";
            fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 115, 112, Color.BLACK.getRGB());
            text = "\u2022 Shift click = Whole stack";
            fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 125, 112, Color.BLACK.getRGB());

            this.buttonList.get(0).visible = true;
            this.buttonList.get(1).visible = true;
            this.buttonList.get(2).visible = true;
            //</editor-fold>
                break;
            case "vending3":
                //<editor-fold desc="Vending Machine Page 03">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 122, 236, 1, 19,17);
                GL11.glPopMatrix();

                text = TextFormatting.UNDERLINE + "Stock Mode" + TextFormatting.RESET + ". This is where owners can set their prices and stock the items they want to sell." +
                        " You can click the [" + TextFormatting.BOLD + "Profit" + TextFormatting.RESET + "] button to receive all the money you've made so far.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = " The [   ] tab enables and disables the automation feature of the machine. Once enabled the machine profit will be transformed into 20 dollar bills in a hidden slot and will sit there...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 88, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "vending4":
                //<editor-fold desc="Vending Machine Page 04">
                this.itemRender.renderItemIntoGUI(new ItemStack(Blocks.CHEST, 1, 0), guiLeft + 41, guiTop + 103);

                text = "...until the owner either disables automation (in which money will go back into the machines profit) or places a hopper under it" +
                        " in which the money will be siphoned out.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = " You can setup a " + TextFormatting.BOLD + "quicker" + TextFormatting.RESET + " way to pull out of the machine. Placing a vanilla chest [   ] " +
                        TextFormatting.BOLD + "one " + TextFormatting.RESET + "or " + TextFormatting.BOLD + "two blocks directly under " +
                        TextFormatting.RESET + "the machine will causes all outputs to " + TextFormatting.BOLD +
                        "quickly...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 80, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "vending5":
                //<editor-fold desc="Vending Machine Page 05">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 115, 236, 19, 19,17);
                GL11.glPopMatrix();

                text = "...move into the chest, much faster than a hopper.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = " You can also pump items into the machine by hooking up a hopper to the " + TextFormatting.BOLD + "side " + TextFormatting.RESET +
                    "of the machine.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 43, 112, Color.BLACK.getRGB());

                text = " The [   ] tab is used to edit the prices of the items sold. Change which item you are editing by simply " + TextFormatting.BOLD + "right clicking.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 83, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "vending6":
                //<editor-fold desc="Vending Machine Page 06">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 28, 236, 55, 19,17);
                GL11.glPopMatrix();

                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 44, guiTop + 84, 167, 0, 58,58);

                text = " The [   ] tab can only be accessed if the owner is in " + TextFormatting.BOLD + "creative " + TextFormatting.RESET + "It allows the owner to make stock infinite.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                ItemStack[] ingredientsv = recipe(new ItemStack(ModBlocks.blockVending));

                this.itemRender.renderItemIntoGUI(ingredientsv[0], guiLeft + 46, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredientsv[1], guiLeft + 65, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredientsv[2], guiLeft + 84, guiTop + 86);

                this.itemRender.renderItemIntoGUI(ingredientsv[3], guiLeft + 46, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredientsv[4], guiLeft + 65, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredientsv[5], guiLeft + 84, guiTop + 105);

                this.itemRender.renderItemIntoGUI(ingredientsv[6], guiLeft + 46, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredientsv[7], guiLeft + 65, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredientsv[8], guiLeft + 84, guiTop + 124);

                text = "Vending Machine Recipe";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 75, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                //</editor-fold>
                break;
            case "exchange":
                //<editor-fold desc="Exchange Machine Page">
                //Item Icon
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 1.2F, guiTop + 1.2F, 0.8F);
                GL11.glScalef(1.2F, 1.2F, 1.2F);
                this.itemRender.renderItemIntoGUI(new ItemStack(ModBlocks.blockExchanger, 1, 0), 23, 11);
                GL11.glPopMatrix();

                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(96, 164, 236, 73, 19,17);
                GL11.glPopMatrix();

                //Chapter Title
                text = TextFormatting.BOLD + "Exchange Machine";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 47, guiTop + 21, 148, Color.BLACK.getRGB());

                text = " Too lazy to go mining? Have tons of money? Setup an exchange machine and have people come to YOU and bring you everything you ever wanted.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                text = " Once your exchange machine is placed it will instantly recognize you as its owner. Clicking the [    ] icon in the gui will change it between " + TextFormatting.BOLD + "'Stock'" +
                        TextFormatting.RESET + " and " + TextFormatting.BOLD + "'Sell'" + TextFormatting.RESET + " mode.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 90, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "exchange1":
                //<editor-fold desc="Exchange Machine Page 01">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.5F, guiTop + 0.5F, 0.8F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(VENDOR_TEXTURE);
                drawTexturedModalRect(133, 238, 198, 17, 16,16);
                GL11.glPopMatrix();

                text = "You can also create a " + TextFormatting.BOLD + "double" + TextFormatting.RESET + " exchange machine by simply placing one on top of another one. Besides the " +
                        TextFormatting.BOLD + "aesthetic" + TextFormatting.RESET + " change the machine also gets " + TextFormatting.BOLD + "double the inventory space.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = TextFormatting.UNDERLINE + "Sell Mode" + TextFormatting.RESET + ". This is where players can" + TextFormatting.BOLD +
                        " sell " + TextFormatting.RESET + "items for cash. Insert items by placing them in the [   ] slot or by shift " +
                        "clicking it. You can also click the...";//
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 92, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "exchange2":
                //<editor-fold desc="Exchange Machine Page 02">

                text = "[" + TextFormatting.BOLD + "Cash" + TextFormatting.RESET + "] button to receive your newly earned cash. You can get different amounts of items by:";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = "\u2022 Right click = 1 item";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 55, 112, Color.BLACK.getRGB());
                text = "\u2022 Left click = 10 items";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 65, 112, Color.BLACK.getRGB());
                text = "\u2022 Shift click = Whole stack";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 75, 112, Color.BLACK.getRGB());

                text = TextFormatting.UNDERLINE + "Stock Mode" + TextFormatting.RESET + ". This is where owners can set their prices and stock the items they want to sell." +
                        " You can click the [" + TextFormatting.BOLD + "Profit" + TextFormatting.RESET + "] button to receive all the money you've made so far.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 90, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "exchange3":
                //<editor-fold desc="Exchange Machine Page 03">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 27, 236, 1, 19, 17);
                GL11.glPopMatrix();

                text = " The [   ] tab enables and disables the automation feature of the machine. Whenever an item is sold to the machine it is moved to the hidden output slot" +
                        ". If there is no automation enabled your machine will become backed up quickly. Once enabled the owner must attach a hopper to the bottom of the machine" +
                        " in which sold items will be siphoned out.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "exchange4":
                //<editor-fold desc="Exchange Machine Page 04">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 151, 236, 19, 19,17);
                GL11.glPopMatrix();

                this.itemRender.renderItemIntoGUI(new ItemStack(Blocks.CHEST, 1, 0), guiLeft + 41, guiTop + 45);

                text = " You can setup a " + TextFormatting.BOLD + "quicker" + TextFormatting.RESET + " way to pull out of the machine. Placing a vanilla chest [   ] " +
                        TextFormatting.BOLD + "one " + TextFormatting.RESET + "or " + TextFormatting.BOLD + "two blocks directly under " +
                        TextFormatting.RESET + "the machine will causes all outputs to " + TextFormatting.BOLD +
                        "quickly " + TextFormatting.RESET + "move into the chest, much faster than a hopper.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = " The [   ] tab is used to edit the amount of cash you are willing to give per item. Change which item you are editing by simply...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 108, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                this.buttonList.get(2).visible = true;
                //</editor-fold>
                break;
            case "exchange5":
                //<editor-fold desc="Exchange Machine Page 05">
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.8F, guiTop + 0.8F, 0.8F);
                GL11.glScalef(0.7F, 0.7F, 0.7F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(TAB_TEXTURE);
                drawTexturedModalRect(55, 51, 236, 55, 19,17);
                GL11.glPopMatrix();

                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 44, guiTop + 94, 167, 0, 58,58);

                text = "..." + TextFormatting.BOLD + "right clicking.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = " The [   ] tab can only be accessed if the owner is in " + TextFormatting.BOLD + "creative " + TextFormatting.RESET + "It allows the owner to make stock infinite.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 38, 112, Color.BLACK.getRGB());

                ItemStack[] ingredientse = recipe(new ItemStack(ModBlocks.blockExchanger));

                this.itemRender.renderItemIntoGUI(ingredientse[0], guiLeft + 46, guiTop + 96);
                this.itemRender.renderItemIntoGUI(ingredientse[1], guiLeft + 65, guiTop + 96);
                this.itemRender.renderItemIntoGUI(ingredientse[2], guiLeft + 84, guiTop + 96);

                this.itemRender.renderItemIntoGUI(ingredientse[3], guiLeft + 46, guiTop + 115);
                this.itemRender.renderItemIntoGUI(ingredientse[4], guiLeft + 65, guiTop + 115);
                this.itemRender.renderItemIntoGUI(ingredientse[5], guiLeft + 84, guiTop + 115);

                this.itemRender.renderItemIntoGUI(ingredientse[6], guiLeft + 46, guiTop + 134);
                this.itemRender.renderItemIntoGUI(ingredientse[7], guiLeft + 65, guiTop + 134);
                this.itemRender.renderItemIntoGUI(ingredientse[8], guiLeft + 84, guiTop + 134);

                text = "Exchange Machine Recipe";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 30, guiTop + 85, 112, Color.BLACK.getRGB());

                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                //</editor-fold>
                break;
        }
        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(false);
        super.drawScreen(mouseX, mouseY, partialTicks);

        //<editor-fold desc="Usual Buttons">
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        //Back
        if (this.buttonList.get(0).visible == true) {
            if ((i >= 14 && i <= 14 + 20) && (j >= 10 && j <= 10 + 10)) {
                drawTexturedModalRect(guiLeft + 14, guiTop + 10, 23, 219, 21, 11);
            }
        }

        //Prev
        if (this.buttonList.get(1).visible == true) {
            if ((i >= 20 && i <= 20 + 17) && (j >= 156 && j <= 156 + 9)) {
                drawTexturedModalRect(guiLeft + 20, guiTop + 154, 26, 206, 18,11);
            }
        }

        //Next
        if (this.buttonList.get(2).visible == true) {
            if ((i >= 100 && i <= 100 + 17) && (j >= 156 && j <= 156 + 9)) {
                drawTexturedModalRect(guiLeft + 100, guiTop + 155, 26, 194, 18,11);
            }
        }
        //</editor-fold>
    }

    public void clean(){
        for(int i = 0; i < buttonTotal; i++){
            this.buttonList.get(i).visible = false;
        }
    }

    public ItemStack[] recipe(ItemStack out){
        for (IRecipe recipe : ForgeRegistries.RECIPES.getValues()) {
            ItemStack output = null;
            try {
                output = recipe.getRecipeOutput();
            } catch (Exception e) {
            }
            if (output != null && output.getItem() == out.getItem()) {
                ItemStack[] items = new ItemStack[9];
                NonNullList<Ingredient> ingredients = recipe.getIngredients();

                for(int i = 0; i < ingredients.size(); i++) {
                    if (ingredients.get(i) != Ingredient.EMPTY) {
                        items[i] = ingredients.get(i).getMatchingStacks()[0];
                    }else items[i] = ItemStack.EMPTY;
                }
                return items;
            }
        }
        return null;
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
                switch (page){
                    default: page = "root";
                        break;
                }
                break;
            case 1: //Prev
                switch (page){
                    case "wallet1": page = "wallet";
                        break;
                    case "vending1": page = "vending";
                        break;
                    case "vending2": page = "vending1";
                        break;
                    case "vending3": page = "vending2";
                        break;
                    case "vending4": page = "vending3";
                        break;
                    case "vending5": page = "vending4";
                        break;
                    case "vending6": page = "vending5";
                        break;
                    case "exchange1": page = "exchange";
                        break;
                    case "exchange2": page = "exchange1";
                        break;
                    case "exchange3": page = "exchange2";
                        break;
                    case "exchange4": page = "exchange3";
                        break;
                    case "exchange5": page = "exchange4";
                }
                break;
            case 2: //Next
                switch (page){
                    case "wallet": page = "wallet1";
                        break;
                    case "vending": page = "vending1";
                        break;
                    case "vending1": page = "vending2";
                        break;
                    case "vending2": page = "vending3";
                        break;
                    case "vending3": page = "vending4";
                        break;
                    case "vending4": page = "vending5";
                        break;
                    case "vending5": page = "vending6";
                        break;
                    case "exchange": page = "exchange1";
                        break;
                    case "exchange1": page = "exchange2";
                        break;
                    case "exchange2": page = "exchange3";
                        break;
                    case "exchange3": page = "exchange4";
                        break;
                    case "exchange4": page = "exchange5";
                        break;
            }
                break;
            case 3: //Bill Button
                this.page ="bill";
                break;
            case 4: //Wallet Button
                this.page ="wallet";
                break;
            case 5: //Vending Button
                this.page ="vending";
                break;
            case 6: //Exchange Button
                this.page ="exchange";
        }
        clean();
    }
}
