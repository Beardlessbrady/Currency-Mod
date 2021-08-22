package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.network.MessageVendingStateData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL12;

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

    public VendingContainerScreen(VendingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    protected void init() {
        super.init();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttons.clear();
        addButton(new Button(50, 50, 10, 10,
                new TranslationTextComponent("gui.gocurrency.vending.buttonmode" + container.getVendingStateData(VendingStateData.MODE_INDEX)), (button) -> {
            handle(VendingStateData.MODE_INDEX);
        }));
    }

    private void handle(int i) {
        GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), i));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);


    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int x, int y) {
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
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY + 111, 0, 157, 175, 99);



        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            //Draw closed machine background
            this.blit(matrixStack, edgeSpacingX + 32, edgeSpacingY - 47, 0, 0, 124, 157);
        } else {
            this.minecraft.getTextureManager().bindTexture(TEXTURE2);
            //Draw closed machine background
            this.blit(matrixStack, edgeSpacingX - 78, edgeSpacingY - 47, 0, 0, 234, 157);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
        }



    }

    // Returns true if the given x,y coordinates are within the given rectangle
    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        drawBufferSize(matrixStack);

        this.font.func_243248_b(matrixStack, this.title, 40, -41, 4210752); //Block Title
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, 117, 4210752); //Inventory Title
        buttons.get(BUTTONID_MODE).setMessage(new TranslationTextComponent("gui.gocurrency.vending.buttonmode" + container.getVendingStateData(VendingStateData.MODE_INDEX)));

        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            // Draw Top tips of outer machine to cover items inside
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(matrixStack, 98, -31, 176, 245, 11, 11);
            this.blit(matrixStack, 39, -31, 187, 245, 11, 11);
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
