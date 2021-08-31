package com.beardlessbrady.gocurrency.blocks.vending;

import com.beardlessbrady.gocurrency.CustomButton;
import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.init.ClientRegistry;
import com.beardlessbrady.gocurrency.network.MessageSetPrice;
import com.beardlessbrady.gocurrency.network.MessageVendingCashButton;
import com.beardlessbrady.gocurrency.network.MessageVendingStateData;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL12;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by BeardlessBrady on 2021-03-01 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class VendingContainerScreen extends ContainerScreen<VendingContainer> {
    private static final ResourceLocation PLAYER = new ResourceLocation("gocurrency", "textures/gui/player.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("gocurrency", "textures/gui/vending.png");
    private static final ResourceLocation TEXTURE2 = new ResourceLocation("gocurrency", "textures/gui/vending2.png");

    final static int FONT_Y_SPACING = 10;

    private TextFieldWidget fieldPrice;

    final static byte BUTTONID_MODE = 0;
    final static byte BUTTONID_PRICE = 1;
    final static byte BUTTONID_CASH = 2;
    final static byte BUTTONID_GUISWITCH = 3;

    public VendingContainerScreen(VendingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    /**
     * Initialize GUI
     */
    @Override
    protected void init() {
        super.init();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttons.clear();
        // 0
        this.buttons.add(new Button(i + 115, j - 30, 20, 20,
                new TranslationTextComponent(""), (button) -> {
            handle(HANDLE_MODE);
        }));
        this.children.add(this.buttons.get(BUTTONID_MODE));

        this.buttons.add(new CustomButton(i - 1000, j - 1000, 21, 23, 232, 21, 232, 21,
                new TranslationTextComponent(""), (button) -> {
            handle(HANDLE_EDITPRICE);
        }));

        this.buttons.add(new CustomButton(i + 116, j + 29, 20, 13, 177, 218, 177, 231,
                new TranslationTextComponent(""), (button) -> {
            handle(HANDLE_CASH);
        })); //
        this.children.add(this.buttons.get(BUTTONID_CASH));

        this.buttons.add(new CustomButton(i - 1000, j - 1000, 10, 10, 217, 218, 217, 228,
                new TranslationTextComponent(""), (button) -> {
        })); //TODO

        this.fieldPrice = new TextFieldWidget(this.font, guiLeft - 65, guiTop - 8, 94, 12, ITextComponent.getTextComponentOrEmpty("Price"));
        this.fieldPrice.setText("0.00");
        this.fieldPrice.setTextColor(Integer.parseInt("80C45C", 16));
        this.fieldPrice.setFocused2(true);
        this.fieldPrice.setVisible(false);
        this.fieldPrice.setMaxStringLength(13);
    }

    @Override
    public void resize(Minecraft minecraft, int width, int height) {
        super.resize(minecraft, width, height);

        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        // For some reason on resize these buttons mess up, redraw them on RESIZE!
        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) { // Mode STOCK
            // Price Settings Tab
            if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 0) { // PRICE EDIT ON
                this.children.remove(this.buttons.get(BUTTONID_PRICE));
                this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 14, j - 33, 18, 23, 176, 24, 176, 24,
                        new TranslationTextComponent(""), (button) -> {
                    handle(HANDLE_EDITPRICE);
                }));
                this.children.add(this.buttons.get(BUTTONID_PRICE));
            } else {
                this.children.remove(this.buttons.get(BUTTONID_PRICE));
                this.buttons.set(BUTTONID_PRICE, new CustomButton(i - 53, j - 33, 18, 23, 176, 24, 176, 24,
                        new TranslationTextComponent(""), (button) -> {
                    handle(HANDLE_EDITPRICE);
                }));
                this.children.add(this.buttons.get(BUTTONID_PRICE));
            }

            // Show GUI_SWITCH Button
            this.buttons.set(BUTTONID_GUISWITCH, new CustomButton(i + 125, j - 48, 10, 10, 217, 218, 217, 228,
                    new TranslationTextComponent(""), (button) -> {
            })); //TODO
            this.children.add(this.buttons.get(BUTTONID_GUISWITCH));
        }
    }

    /**
     * Handleing Button Clicks
     */
    private final static byte HANDLE_MODE = 0;
    private final static byte HANDLE_EDITPRICE = 1;
    private final static byte HANDLE_CASH = 2;
    private void handle(int k) {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        switch (k) {
            case HANDLE_MODE: //VendingStateData.MODE_INDEX:
                if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) { // MODE SELL (Reverse since hase not changed yet)
                    // Hide Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 1000, j + 1000, 18, 23, 176, 24, 176, 24,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    // Hide GUI_SWITCH BUTTON
                    this.children.remove(this.buttons.get(BUTTONID_GUISWITCH));
                    this.buttons.set(BUTTONID_GUISWITCH, new CustomButton(i + 1000, j + 1000, 10, 10, 217, 218, 217, 228,
                            new TranslationTextComponent(""), (button) -> {
                    }));
                    this.children.add(this.buttons.get(BUTTONID_GUISWITCH));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29, 20, 13, 177, 218, 177, 231,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    })));
                } else { // Mode STOCK
                    // Show Price Settings Tab
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 14, j - 33, 18, 23, 176, 24, 176, 24,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));

                    // Show GUI_SWITCH Button
                    this.buttons.set(BUTTONID_GUISWITCH, new CustomButton(i + 125, j - 48, 10, 10, 217, 218, 217, 228,
                            new TranslationTextComponent(""), (button) -> {
                    })); //TODO
                    this.children.add(this.buttons.get(BUTTONID_GUISWITCH));

                    this.buttons.set(BUTTONID_CASH, (new CustomButton(i + 116, j + 29, 20, 13, 197, 218, 197, 231,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    })));
                }
                container.updateModeSlots();
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.MODE_INDEX,
                        container.getVendingStateData(VendingStateData.MODE_INDEX) == 0? 1 : 0));
                break;
            case HANDLE_EDITPRICE: //VendingStateData.EDITPRICE_INDEX:
                if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON (Reverse since has not changed yet)
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i + 14, j - 33, 18, 23, 176, 24, 176, 24,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                } else {
                    this.children.remove(this.buttons.get(BUTTONID_PRICE));
                    this.buttons.set(BUTTONID_PRICE, new CustomButton(i - 78, j - 33, 18, 23, 176, 24, 176, 24,
                            new TranslationTextComponent(""), (button) -> {
                        handle(HANDLE_EDITPRICE);
                    }));
                    this.children.add(this.buttons.get(BUTTONID_PRICE));
                }
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.EDITPRICE_INDEX,
                        container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 0? 1 : 0));
                break;
            case HANDLE_CASH: //CASH BUTTON
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingCashButton(container.getTile().getPos(), container.getVendingStateData(VendingStateData.MODE_INDEX)));
                break;
        }
    }

    // ------------ INPUTS ----------------------
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.fieldPrice.mouseClicked(mouseX, mouseY, button);

        if (this.fieldPrice.isFocused()) {
            this.fieldPrice.setMaxStringLength(this.fieldPrice.getText().length());
        }

        boolean ret = super.mouseClicked(mouseX, mouseY, button);

        updateTextField();
        return ret;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(container.getVendingStateData(VendingStateData.MODE_INDEX) == 0 ) { // SELL
            if (keyCode == ClientRegistry.keyBindings[0].getKey().getKeyCode()) { // FULL STACK
                if (container.getVendingStateData(VendingStateData.BUYMODE_INDEX) != 2) {
                    GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.BUYMODE_INDEX, 2));
                }
            } else if (keyCode == ClientRegistry.keyBindings[1].getKey().getKeyCode()) { // HALF STACK
                if (container.getVendingStateData(VendingStateData.BUYMODE_INDEX) != 1) {
                    GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.BUYMODE_INDEX, 1));
                }
            }
        }

        if(fieldPrice.isFocused()) {
            char typedChar = (char)keyCode;
            int numChar = Character.getNumericValue(typedChar);

            //key inputs are only numbers, ., or backspace
            if ((container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) && (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) &&
                    ((numChar >= 0 && numChar <= 9) || (keyCode == 46 && !this.fieldPrice.getText().contains(".")) ||  (keyCode == 259))) {

                // Not backspace and less than 12 chars OR if more than 12 MUST BE A '.'
                if(keyCode != 259) {
                    if (this.fieldPrice.getText().length() != 12 || this.fieldPrice.getText().charAt(this.fieldPrice.getText().length() -1) == '.' || keyCode == 46) {
                        this.fieldPrice.charTyped(typedChar, keyCode); // Type
                    }
                } else {
                    this.fieldPrice.keyPressed(keyCode, scanCode, modifiers); // Type
                }

                // Set max length based on if and where the period is
                String priceText = this.fieldPrice.getText();
                if (priceText.length() >= 1) {
                    if (priceText.charAt(priceText.length()-1) == '.') { // Last char is '.'
                        if (priceText.charAt(0) == '.') {
                            this.fieldPrice.setMaxStringLength(3);
                        } else {
                            this.fieldPrice.setMaxStringLength(this.fieldPrice.getText().length() + 2);
                        }
                    } else if (!priceText.contains(".")){
                        this.fieldPrice.setMaxStringLength(13);
                    }
                } else {
                  this.fieldPrice.setMaxStringLength(13);
                }

                // Get Out text
                String outText = fieldPrice.getText();
                if (this.fieldPrice.getText().length() == 0) {
                    outText = "0.00";
                } else if (!this.fieldPrice.getText().contains(".")) {
                    outText += ".00";
                } else {
                    if (fieldPrice.getText().length() >= 2) {
                        if (outText.charAt(outText.length() - 2) == '.') {
                            outText += "0";
                        } else if (fieldPrice.getText().length() >= 1) {
                            if (outText.charAt(outText.length() - 1) == '.')
                                outText += "00";
                        }
                    } else if (fieldPrice.getText().length() >= 1) {
                        if (outText.charAt(outText.length() - 1) == '.')
                            outText += "00";
                    }
                }
                if (outText.charAt(0) == '.') {
                    outText = "0" + outText;
                    this.fieldPrice.setMaxStringLength(outText.length());
                }

                if (!outText.equals(container.getTile().getPrice(container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX)))) {
                    container.getTile().setPrice(container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX), outText);
                    GOCurrency.NETWORK_HANDLER.sendToServer(new MessageSetPrice(container.getTile().getPos(), container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX), outText));
                }
                return true;
            } else {
                return false;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == ClientRegistry.keyBindings[0].getKey().getKeyCode()) { // FULL STACK
            if (container.getVendingStateData(VendingStateData.BUYMODE_INDEX) == 2) {
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.BUYMODE_INDEX, 0));
            }
        } else if (keyCode == ClientRegistry.keyBindings[1].getKey().getKeyCode()) { // HALF STACK
            if (container.getVendingStateData(VendingStateData.BUYMODE_INDEX) == 1) {
                GOCurrency.NETWORK_HANDLER.sendToServer(new MessageVendingStateData(container.getTile().getPos(), VendingStateData.BUYMODE_INDEX, 0));
            }
        }

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private void updateTextField() {
        boolean isEmpty = !container.getStockContents().getStackInSlot(container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX)).isEmpty();

        fieldPrice.setEnabled(isEmpty);
        this.fieldPrice.setText(container.getTile().getPrice(container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX)));
    }

    // ------------- RENDERS --------------------

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(!container.getTile().isOwner()) {
            buttons.get(BUTTONID_MODE).active = false;
            buttons.get(BUTTONID_MODE).visible = false;
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        this.drawCustomTooltips(matrixStack, mouseX, mouseY);

        this.fieldPrice.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        // Draw Background
        int edgeSpacingX = (this.width - this.xSize) / 2;
        int edgeSpacingY = (this.height - this.ySize) / 2;

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        //Player Inventory
        this.minecraft.getTextureManager().bindTexture(PLAYER);
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY + 111, 0, 157, 176, 99);

        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            // CLOSED Vending Machine
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            this.blit(matrixStack, edgeSpacingX + 32, edgeSpacingY - 53, 0, 0, 125, 163);
        } else {
            // OPEN Vending Machine
            this.minecraft.getTextureManager().bindTexture(TEXTURE2);
            this.blit(matrixStack, edgeSpacingX - 78, edgeSpacingY - 53, 0, 0, 235, 163);
            this.minecraft.getTextureManager().bindTexture(TEXTURE);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        drawBufferSize(matrixStack);

        this.font.func_243248_b(matrixStack, this.title, 40, -48, 4210752); //Block Title
        this.font.func_243248_b(matrixStack, this.playerInventory.getDisplayName(), (float) this.playerInventoryTitleX, 117, 4210752); //Inventory Title

        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // Sell
            this.fieldPrice.setVisible(false);
            this.fieldPrice.setFocused2(false);

            GL12.glDisable(GL12.GL_DEPTH_TEST);
            GL12.glPushMatrix();
            GL12.glScalef(0.7F, 0.7F, 0.8F);
            this.font.drawStringWithShadow(matrixStack, TextFormatting.WHITE + I18n.format("block.gocurrency.vending.cash"), 56, -55, 0);
            this.font.drawString(matrixStack, TextFormatting.DARK_GREEN + container.currencyToString(container.getVendingStateData(VendingStateData.MODE_INDEX)), 94, -55, 0);
            GL12.glPopMatrix();
            GL12.glDisable(GL12.GL_DEPTH_TEST);

            this.minecraft.getTextureManager().bindTexture(TEXTURE);

            if(container.getTile().isOwner()) {
                // Sell Mode Button icon
                this.blit(matrixStack, 117, -28, 144, 67, 16, 16);
            }

            // Draw Top tips of outer machine to cover items inside
            this.blit(matrixStack, 98, -31, 176, 245, 11, 11);
            this.blit(matrixStack, 39, -31, 187, 245, 11, 11);
        } else {
            GL12.glDisable(GL12.GL_DEPTH_TEST);
            GL12.glPushMatrix();
            GL12.glScalef(0.7F, 0.7F, 0.8F);
            this.font.drawStringWithShadow(matrixStack, TextFormatting.WHITE + I18n.format("block.gocurrency.vending.income"), 56, -55, 0);
            this.font.drawString(matrixStack, TextFormatting.AQUA + container.currencyToString(container.getVendingStateData(VendingStateData.MODE_INDEX)), 104, -55, 0);
            GL12.glPopMatrix();
            GL12.glDisable(GL12.GL_DEPTH_TEST);

            this.minecraft.getTextureManager().bindTexture(TEXTURE);
            // Stock Mode Button icon
            this.blit(matrixStack, 117, -28, 127, 67, 16, 16);

            // Price Editing
            this.drawItemSelector(matrixStack, x, y);
        }
    }

    @Override
    protected void renderTooltip(MatrixStack matrixStack, ItemStack itemStack, int mouseX, int mouseY) {
        int x = (mouseX - (width - xSize) / 2);
        int y = (mouseY - (height - ySize) / 2);
        if (x >= 38 && y >= -32 && x <= 109 && y <= 51) {
            int startX = 38;
            int startY = -32;
            int row = ((x - startX) / 18);
            int column = ((y - startY) / 22);
            int slot = row + (column * 4);

            List<ITextComponent> list = new ArrayList<>();
            List<ITextComponent> ogTooltip = itemStack.getTooltip(Minecraft.getInstance().player,
                    Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

            int tooltipStart = 1;
            if (ogTooltip.size() > 0) {
                list.add(ogTooltip.get(0));
                list.set(0, ITextComponent.getTextComponentOrEmpty(itemStack.getRarity().color + list.get(0).getString()));
            }
            if (ogTooltip.size() > 1) if (!ogTooltip.get(1).equals("")) {
                list.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.GRAY + ogTooltip.get(1).getString()));
                tooltipStart = 2;
            }

            //Adding Vending Strings
            TextFormatting color = TextFormatting.RED;
            if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // SELL MODE
                if (container.canAfford(slot)) {
                    color = TextFormatting.GREEN;
                }

                int stock = container.getStockContents().getStackSize(slot);
                int[] price = container.priceFromBuyMode(slot);
                String cost = price[0] + "." + price[1];
                if (price[1] < 10) {
                    cost = price[0] + "." + price[1] + '0';
                }

                list.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.GOLD +
                        I18n.format("block.gocurrency.vending.buy.price") + color +
                        I18n.format("block.gocurrency.vending.slotpricing.$") + cost));

                if (stock > 0) {
                    list.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.GOLD +
                            I18n.format("block.gocurrency.vending.buy.stock") + TextFormatting.BLUE + stock));
                } else {
                    list.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.RED + I18n.format("block.gocurrency.vending.buy.out")));
                }

                //adding original extra stuff AFTER price and such
                for (; tooltipStart < ogTooltip.size(); tooltipStart++) {
                    list.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.GRAY + ogTooltip.get(tooltipStart).getString()));
                }
            }

            if (this.minecraft.player.inventory.getItemStack().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
                renderWrappedToolTip(matrixStack, list, mouseX, mouseY, this.font);
            }
        } else {
            super.renderTooltip(matrixStack, itemStack, mouseX, mouseY);
        }
    }

    /**
     * Draw Tooltips for buttons
     */
    private void drawCustomTooltips(MatrixStack matrixStack, int x, int y) {
        for (int i = 0; i < buttons.size(); i++) {
            Widget butt = buttons.get(i);
            if (x >= butt.x && x <= butt.x + butt.getWidth() && y >= butt.y && y <= butt.y + butt.getHeightRealms()) {
                String text = "NULL";
                int mode = container.getVendingStateData(VendingStateData.MODE_INDEX);
                List<ITextComponent> listText = new ArrayList<>();

                switch (i) {
                    case BUTTONID_MODE:
                        if(container.getTile().isOwner()) {
                            listText.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.DARK_PURPLE + I18n.format("block.gocurrency.vending.tooltip.mode" + mode + "_0")));
                            listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.mode" + mode + "_1", TextFormatting.GRAY)));
                        }
                        break;
                    case BUTTONID_CASH:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.cash" + mode, mode == 0 ? TextFormatting.GREEN : TextFormatting.AQUA)));
                        break;
                    case BUTTONID_GUISWITCH:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.guiswitch")));
                        break;
                    case BUTTONID_PRICE:
                        listText.add(ITextComponent.getTextComponentOrEmpty(I18n.format("block.gocurrency.vending.tooltip.pricesetting_0")));
                        if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1)
                            listText.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.GRAY + I18n.format("block.gocurrency.vending.tooltip.pricesetting_1") +
                                    TextFormatting.YELLOW + I18n.format(Minecraft.getInstance().gameSettings.keyBindAttack.getTranslationKey())));
                        break;
                }

                renderWrappedToolTip(matrixStack, listText, x, y, this.font);
            }
        }
    }

    /**
     * Draw Edit Price Mode (Opened Tab, selector over slot)
     */
    private void drawItemSelector(MatrixStack matrixStack, int x, int y) {
        if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 1) { // STOCK
            if (container.getVendingStateData(VendingStateData.EDITPRICE_INDEX) == 1) { // PRICE EDIT ON
                this.blit(matrixStack, -76, -33, 126, 0, 40, 48); // Big Tag
                this.blit(matrixStack, -37, -33, 127, 0, 40, 48); // Big Tag 2
                this.blit(matrixStack, 1, -33, 135, 0, 31, 48); // Big Tag 3
                this.blit(matrixStack, -80, -33, 167, 0, 5, 48); // Cap of Tag
                this.blit(matrixStack, -76, -30, 178, 67, 16, 16); // Price Tag Button Icon

                int selectedSlot = container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX);
                this.blit(matrixStack, 37 + ((selectedSlot % 4) * 18), -33 + ((selectedSlot / 4) * 22),
                        185, 0, 20, 20); // Price Selector Square
                this.blit(matrixStack, 50 + ((selectedSlot % 4) * 18), -19 + ((selectedSlot / 4) * 22),
                        161, 67, 16, 16); // Price Selector Tag

                this.font.drawStringWithShadow(matrixStack, I18n.format("block.gocurrency.vending.slotpricing"),
                        -57, -27, Objects.requireNonNull(Color.fromHex("#cbd11d")).getColor()); //Tab Title

                GL12.glDisable(GL12.GL_DEPTH_TEST);
                GL12.glPushMatrix();
                GL12.glScalef(0.5F, 0.5F, 0.8F);
                String selectedBlock = I18n.format(container.getStockContents().getStackInSlot(container.getVendingStateData(VendingStateData.SELECTEDSLOT_INDEX)).getItem().getTranslationKey());
                if (selectedBlock.equals("Air"))
                    selectedBlock = "None Selected";
                this.font.drawString(matrixStack, "[ " + selectedBlock + " ]",
                        -114, -34, Objects.requireNonNull(Color.fromHex("#FFFFFF")).getColor()); // Selected Block
                GL12.glPopMatrix();
                GL12.glDisable(GL12.GL_DEPTH_TEST);

                this.font.drawStringWithShadow(matrixStack, I18n.format("block.gocurrency.vending.slotpricing.$"),
                        -74, -6, Objects.requireNonNull(Color.fromHex("#FFFFFF")).getColor()); // &
                this.fieldPrice.setVisible(true);
            } else { // PRICE EDIT OFF
                this.blit(matrixStack, 11, -33, 176, 0, 4, 23); // Cap of Price Tab
                this.blit(matrixStack, 14, -30, 178, 67, 16, 16); // Price Tag Button Icon

                this.fieldPrice.setVisible(false);
                this.fieldPrice.setFocused2(false);
            }
        } else { // SELL MODE
            this.fieldPrice.setVisible(false);
            this.fieldPrice.setFocused2(false);
        }
    }

    /**
     * Draw item slot stack size
     */
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

                int stackMax = container.getStockContents().getStackInSlot(index).getMaxStackSize();
                int count = container.getStockContents().getStackSize(index);
                TextFormatting color = TextFormatting.WHITE;
                if (container.getVendingStateData(VendingStateData.MODE_INDEX) == 0) { // SELL
                    if (count == 0) {
                        count = 0;
                        color = TextFormatting.DARK_GREEN;
                    } else {
                        switch(container.getVendingStateData(VendingStateData.BUYMODE_INDEX)) {
                            case 0: // ONE
                                count = 1;
                                break;
                            case 1: // HALF
                                int half = (int)Math.ceil(stackMax / 2);
                                if (half == 0) half = 1;
                                if (count > half) {
                                    count = half;
                                }
                                color = TextFormatting.YELLOW;
                                break;
                            case 2: // FULL
                                if (count > stackMax) {
                                    count = stackMax;
                                }
                                color = TextFormatting.GOLD;
                                break;
                        }
                    }

                    if (!container.canAfford(index)) {
                        color = TextFormatting.RED;
                    }
                }

                if (count > 1) {
                    num = color + Integer.toString(count);
                } else if (count == 0) {
                    num = TextFormatting.RED + I18n.format("block.gocurrency.vending.buy.out");
                } else {
                    num = " ";
                }

                if (count < 10 && count > 0) num = "  " + num;
                if (count >= 10 && count < 100) num = " " + num;

                if (!container.getStockContents().getStackInSlot(index).isEmpty())
                    this.font.drawStringWithShadow(matrixStack, num, startX + (i * 26), startY + (j * 31), 1); //Inventory Title
            }
        }
        matrixStack.pop();
        GL12.glPopMatrix();
        GL12.glDisable(GL12.GL_DEPTH_TEST);
    }
}
