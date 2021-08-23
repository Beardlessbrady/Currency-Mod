package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.CustomButton;
import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.network.MessageVendingStateData;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import org.lwjgl.opengl.GL12;

import java.util.List;
import java.util.Objects;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContainerScreen extends ContainerScreen<VendingContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("gocurrency", "textures/gui/vending.png");
    private static final ResourceLocation TEXTURE2 = new ResourceLocation("gocurrency", "textures/gui/vending2.png");

    final static int FONT_Y_SPACING = 10;
    final static int PLAYER_INV_LABEL_XPOS = VendingContainer.PLAYER_INVENTORY_XPOS;
    final static int PLAYER_INV_LABEL_YPOS = VendingContainer.PLAYER_INVENTORY_YPOS - FONT_Y_SPACING;

    final static byte BUTTONID_MODE = 0;
    final static byte BUTTONID_PRICE = 1;
    final static byte BUTTONID_CASH = 2;

    public VendingContainerScreen(VendingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttons.clear();
        // 0
        this.buttons.add(new Button(i + 115, j - 30, 20, 20,
                new TranslationTextComponent(""), (button) -> {handle(VendingStateData.MODE_INDEX);}));
        this.children.add(this.buttons.get(BUTTONID_MODE));

        this.buttons.add(new CustomButton(i - 1000, j - 1000 , 21,23, 232, 21, 232, 21,
                new TranslationTextComponent(""), (button) -> {handle(VendingStateData.EDITPRICE_INDEX);}));

        this.buttons.add(new CustomButton(i + 116, j + 29 , 20,13, 177, 218, 177, 231,
                new TranslationTextComponent(""), (button) -> {handle(VendingStateData.EDITPRICE_INDEX);})); // TODO
      //  this.children.add(this.buttons.get(BUTTONID_CASH));
    }

    private void handle(int k) {
        container.updateModeSlots();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        switch (k) {
            case VendingStateData.MODE_INDEX:
                if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) { // MODE SELL (Reverse since hase not changed yet)
                    // Hide Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 1000, j + 1000, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(VendingStateData.EDITPRICE_INDEX);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29 , 20,13, 177, 218, 177, 231,
                            new TranslationTextComponent(""), (button) -> {handle(VendingStateData.EDITPRICE_INDEX);})));
                } else { // Mode STOCK
                    // Show Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 11, j - 33, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(VendingStateData.EDITPRICE_INDEX);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29 , 20,13, 197, 218, 197, 231,
                            new TranslationTextComponent(""), (button) -> {handle(VendingStateData.EDITPRICE_INDEX);})));
                }
                break;
            case VendingStateData.EDITPRICE_INDEX:
                if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON (Reverse since hase not changed yet)
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 11, j - 33, 21, 23, 232, 21, 232, 21,
                            new TranslationTextComponent(""), (button) -> {
                        handle(VendingStateData.EDITPRICE_INDEX);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                } else {
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i - 73, j - 33, 21, 23, 126, 0, 126, 0,
                            new TranslationTextComponent(""), (button) -> {
                        handle(VendingStateData.EDITPRICE_INDEX);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                }
                break;
        }

        GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), k));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
        if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null) {
            int slot = this.hoveredSlot.slotNumber;
            System.out.println(slot);

            if(slot == ((VendingContainer)container).getFirstInputSlotIndex()) { // INPUT SLOT
                List<ITextComponent> list = Lists.newArrayList();
                IFormattableTextComponent test = (new StringTextComponent("$1,000,000,000.00"));
                list.add(test);

                FontRenderer font = getMinecraft().fontRenderer;
                this.renderWrappedToolTip(matrixStack, list, x, y, (font == null ? this.font : font));
                net.minecraftforge.fml.client.gui.GuiUtils.postItemToolTip();
            }
        }
        super.renderHoveredTooltip(matrixStack, x, y);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);

        // Draw Background
        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;

        //Draw Player Inventory background
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY + 111, 0, 157, 176, 99);



        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            //Draw closed machine background
            this.blit(matrixStack, edgeSpacingX + 32, edgeSpacingY - 47, 0, 0, 125, 157);
        } else {
            this.minecraft.getTextureManager().bindTexture(TEXTURE2);
            //Draw closed machine background
            this.blit(matrixStack, edgeSpacingX - 78, edgeSpacingY - 47, 0, 0, 235, 157);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
        }



    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        drawBufferSize(matrixStack);

        this.font.func_243248_b(matrixStack, this.title, 40, -41, 4210752); //Block Title
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, 117, 4210752); //Inventory Title

      /*  GL12.glDisable(GL12.GL_DEPTH_TEST);
        GL12.glPushMatrix();
        GL12.glScalef(0.7F, 0.7F, 0.8F);
        this.font.drawStringWithShadow(matrixStack, "$1'000'000.00", 0, 0, Color.fromHex("#73AF52").getColor());
        GL12.glPopMatrix();
        GL12.glDisable(GL12.GL_DEPTH_TEST);*/


        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            // Draw Top tips of outer machine to cover items inside
            this.blit(matrixStack, 98, -31, 176, 245, 11, 11);
            this.blit(matrixStack, 39, -31, 187, 245, 11, 11);

            // Sell Mode Button icon
            this.blit(matrixStack, 117, -28, 144, 67, 16, 16);
        } else {
            // Stock Mode Button icon
            this.blit(matrixStack, 117, -28, 127, 67, 16, 16);

            // Price Tag Button Icon
            this.blit(matrixStack, 7, 2, 178, 67, 16, 16);

            if(container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON
                this.blit(matrixStack, -73, -33, 126, 0, 106, 48); // Big Tag

                this.font.drawStringWithShadow(matrixStack, "Slot Pricing", -45, -27, Objects.requireNonNull(Color.fromHex("#cbd11d")).getColor()); //Inventory Title
            }
        }


    }

    private void drawBufferSize(MatrixStack matrixStack) {
        GL12.glDisable(GL12.GL_DEPTH_TEST);
        GL12.glPushMatrix();
        GL12.glScalef(0.7F, 0.7F, 0.8F);

        matrixStack.push();
        matrixStack.translate(0, 0, 350);

        String num;
        int startY = -30;
        int startX = 59;
        int columnCount = 4;
        int rowCount = 4;

        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < rowCount; i++) {
                int index = (i + (rowCount * j));
                int count = container.getStockContents().getStackSize(index);

                if (count > 0) {
                    num = TextFormatting.WHITE + Integer.toString(count);
                } else if (count == 0) {
                    num = TextFormatting.RED + "Out";
                } else {
                    num = " ";
                }

                if (count < 10 && count > 0) num = "  " + num;
                if (count >= 10 && count < 100) num = " " + num;

                if (count != 1 && !container.getStockContents().getStackInSlot(index).isEmpty())
                    this.font.drawStringWithShadow(matrixStack, num, startX + (i * 26), startY + (j * 31), 1); //Inventory Title
            }
        }
        matrixStack.pop();
        GL12.glPopMatrix();
        GL12.glDisable(GL12.GL_DEPTH_TEST);
    }

}
