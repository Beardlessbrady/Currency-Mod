package gunn.modcurrency.mod.client.gui;

import gunn.modcurrency.mod.block.ModBlocks;
import gunn.modcurrency.mod.client.gui.util.TabButton;
import gunn.modcurrency.mod.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
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
        recipe(new ItemStack(ModItems.itemWallet));
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
                text = TextFormatting.BOLD + "The Dollar Bill";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 50, guiTop + 21, 148, Color.BLACK.getRGB());


                text = "These " + TextFormatting.DARK_GREEN + "Coloured " + TextFormatting.BLACK + "pieces of paper are the most important part of your economy.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

                text = "Each has a higher value then the last. This way you won't have one-thousand one dollar bills filling your inventory. You can of course " + TextFormatting.BOLD + "convert "
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
                text = TextFormatting.BOLD + "The Wallet";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 50, guiTop + 21, 148, Color.BLACK.getRGB());


                text = " This revolutionary piece of leather is a convenient way to keep all your hard earned cash in one place.";
               fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 32, 112, Color.BLACK.getRGB());

               text = " Unfortunately it can" + TextFormatting.BOLD + " ONLY " + TextFormatting.RESET + "hold bills from the mod, preventing you from taking advantage of cheap portable storage.";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 75, 112, Color.BLACK.getRGB());

                text =  "There is also " + TextFormatting.RED + "special " + TextFormatting.BLACK + "interaction features with vending machines...";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 125, 112, Color.BLACK.getRGB());


                this.buttonList.get(0).visible = true;
                this.buttonList.get(2).visible = true;
                break;
            case "wallet1":
                GL11.glPushMatrix();
                GL11.glTranslatef(guiLeft + 0.5F, guiTop + 0.5F, 0.8F);
                GL11.glScalef(0.5F, 0.5F, 0.5F);
                Minecraft.getMinecraft().getTextureManager().bindTexture(VENDOR_TEXTURE);
                drawTexturedModalRect(184, 62, 198, 0, 16,16);
                GL11.glPopMatrix();

                Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
                drawTexturedModalRect(guiLeft + 46, guiTop + 84, 167, 0, 58,58);

                ItemStack[] ingredients = recipe(new ItemStack(ModItems.itemWallet));

                this.itemRender.renderItemIntoGUI(ingredients[0], guiLeft + 48, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredients[1], guiLeft + 67, guiTop + 86);
                this.itemRender.renderItemIntoGUI(ingredients[2], guiLeft + 86, guiTop + 86);

                this.itemRender.renderItemIntoGUI(ingredients[3], guiLeft + 48, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredients[4], guiLeft + 67, guiTop + 105);
                this.itemRender.renderItemIntoGUI(ingredients[5], guiLeft + 86, guiTop + 105);

                this.itemRender.renderItemIntoGUI(ingredients[6], guiLeft + 48, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredients[7], guiLeft + 67, guiTop + 124);
                this.itemRender.renderItemIntoGUI(ingredients[8], guiLeft + 86, guiTop + 124);

                text = "When placed in the vending machine input slot [   ] all purchases will go through the wallet, you will never need to collect your change again!";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 18, guiTop + 22, 112, Color.BLACK.getRGB());

                text = "Wallet Recipe";
                fontRenderer.drawSplitString(I18n.format(text), guiLeft + 49, guiTop + 75, 112, Color.BLACK.getRGB());



                this.buttonList.get(0).visible = true;
                this.buttonList.get(1).visible = true;
                //</editor-fold>
                break;

        }
        Minecraft.getMinecraft().fontRenderer.setUnicodeFlag(false);
        super.drawScreen(mouseX, mouseY, partialTicks);

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
                }
                break;
            case 2: //Next
                switch (page){
                    case "wallet": page = "wallet1";
                        break;
                }
                break;
            case 3: //Bill Button
                this.page ="bill";
                break;
            case 4: //Wallet Button
                this.page ="wallet";
        }
        clean();
    }
}
