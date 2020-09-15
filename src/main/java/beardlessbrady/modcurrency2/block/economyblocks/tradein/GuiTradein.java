package beardlessbrady.modcurrency2.block.economyblocks.tradein;

import beardlessbrady.modcurrency2.ModCurrency;
import beardlessbrady.modcurrency2.block.economyblocks.vending.TileVending;
import beardlessbrady.modcurrency2.network.PacketHandler;
import beardlessbrady.modcurrency2.network.PacketOutChangeToServer;
import beardlessbrady.modcurrency2.network.PacketSetFieldToServer;
import beardlessbrady.modcurrency2.network.PacketSetItemToServer;
import beardlessbrady.modcurrency2.utilities.UtilMethods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static beardlessbrady.modcurrency2.block.economyblocks.TileEconomyBase.*;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-07-31
 */

public class GuiTradein extends GuiContainer {
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/tradeingui.png");
    private static final ResourceLocation ASSET_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/guiassets.png");
    private TileTradein te;
    private GuiTextField fieldPrice, fieldAmnt, fieldUntil, fieldItemMax, fieldTimeRestock;
    //Field ID's
    private static final int FIELDPRICE = 0;
    private static final int FIELDAMNT = 1;
    private static final int FIELDITEMMAX = 2;
    private static final int FIELDTIMERESTOCK = 3;
    private static final int FIELDUNTIL = 4;

    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    public GuiTradein(EntityPlayer entityPlayer, TileTradein te) {
        super(new ContainerTradein(entityPlayer, te));
        this.te = te;
    }

    /** Method runs when GUI is first initialized **/
    @Override
    public void initGui() {
        super.initGui();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttonList.add(new GuiButton(BUTTONCHANGE, i + 143, j - 5, 20, 20, "$"));

        String mode = (te.getField(FIELD_MODE) == 1) ? I18n.format("guivending.stock") : I18n.format("guivending.trade");
        buttonList.add(new GuiButton(BUTTONADMIN, i + 137, j - 42, 32, 20, mode));

        fieldPrice = new GuiTextField(FIELDPRICE, fontRenderer, 0, 0, 90, 8);        //Setting Costs
        fieldPrice.setTextColor(Integer.parseInt("3359d4", 16));
        fieldPrice.setEnableBackgroundDrawing(false);
        fieldPrice.setMaxStringLength(7);
        fieldPrice.setVisible(false);
        fieldPrice.setText("0.00");

        fieldAmnt = new GuiTextField(FIELDAMNT, fontRenderer, 0, 0, 90, 8);        //Setting Amount Sold in Bulk
        fieldAmnt.setTextColor(Integer.parseInt("3359d4", 16));
        fieldAmnt.setEnableBackgroundDrawing(false);
        fieldAmnt.setMaxStringLength(2);
        fieldAmnt.setVisible(false);
        fieldAmnt.setText("1");

        fieldUntil = new GuiTextField(FIELDUNTIL, fontRenderer, 0, 0, 90, 8);        //Setting Until value (Buy until)
        fieldUntil.setTextColor(Integer.parseInt("3359d4", 16));
        fieldUntil.setEnableBackgroundDrawing(false);
        fieldUntil.setMaxStringLength(3);
        fieldUntil.setVisible(false);
        fieldUntil.setText("0");

        fieldItemMax = new GuiTextField(FIELDITEMMAX, fontRenderer, i - 66, j + 85, 90, 8);
        fieldItemMax.setTextColor(Integer.parseInt("3359d4", 16));
        fieldItemMax.setEnableBackgroundDrawing(false);
        fieldItemMax.setMaxStringLength(3);
        fieldItemMax.setVisible(false);
        fieldItemMax.setText("1");

        fieldTimeRestock = new GuiTextField(FIELDTIMERESTOCK, fontRenderer, i - 63, j + 75, 90, 8);
        fieldTimeRestock.setTextColor(Integer.parseInt("3359d4", 16));
        fieldTimeRestock.setEnableBackgroundDrawing(false);
        fieldTimeRestock.setMaxStringLength(4);
        fieldTimeRestock.setVisible(false);
        fieldTimeRestock.setText("1");

        GlStateManager.color(0xFF, 0xFF, 0xFF); // Resets colors in GL so there are no colouring issues */

        updateTextField();
    }

    /** Draws the screen and all the components in it **/
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        fieldPrice.drawTextBox();
        fieldAmnt.drawTextBox();
        fieldUntil.drawTextBox();
        fieldItemMax.drawTextBox();
        fieldTimeRestock.drawTextBox();
    }

    /** Draws background **/
    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        guiColor(te.getColor()); //Colored Background
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACK_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 128); // Background texture, TE inventory*/
        GlStateManager.color(1F, 1F, 1F, 1.0F); //Resets color after background to ensure there are no visual glitches
        GlStateManager.popMatrix();

        drawTexturedModalRect(guiLeft, guiTop + 82, 0, 129, 176, 99); //Draws Player Inventory Background texture

        if(te.getField(FIELD_MODE) == 1) { // STOCK MODE */
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
            drawTexturedModalRect(guiLeft - 107, guiTop + 5, 0, 194, 106, 62); // Info Tag background */
        }
    }

    /** Draws foreground **/
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        //If owner or creative make admin button visible
        buttonList.get(BUTTONADMIN).visible = te.isOwner() || Minecraft.getMinecraft().player.isCreative();

        // Barebone GUI text */
        fontRenderer.drawString(I18n.format("tile.modcurrency2:blocktradein.name"), 8, -42, Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 87, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 18, 20, Color.lightGray.getRGB());
        GlStateManager.color(0xFF, 0xFF, 0xFF); // Resets colors in GL to prevent visual glitches */

        drawAdminPanel(); // STOCK 'Info Tag' Rendering */

        drawItemStackSize(); // Custom Stack size Rendering */

        drawSelectionOverlay(); // 'Selection Tag' Rendering */

        // Draws the money labels and amounts and shrinks the text size */
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);
        // Players Money text rendering */
        fontRenderer.drawStringWithShadow(I18n.format("guivending.profit"), 7, -40, Integer.parseInt("2DB22F", 16));
        fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, -30, Integer.parseInt("ffffff", 16));
        fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.LONG_FIELD_CASHRESERVE))), 15, -30, Integer.parseInt("ffffff", 16));
        // Machines Money text rendering in STOCK MODE
        if (te.getField(TileVending.FIELD_MODE) == 1) {
            //If a creative machine add an extra label saying so
            if (te.getField(TileTradein.FIELD_CREATIVE) == 1)
                fontRenderer.drawString(I18n.format("guivending.creative"), 140, -58, Color.pink.getRGB());

            fontRenderer.drawStringWithShadow(I18n.format("guivending.cash"), 7, -10, Integer.parseInt("3D78E0", 16));
            fontRenderer.drawStringWithShadow(I18n.format("guivending.moneysign"), 7, 0, Integer.parseInt("ffffff", 16));
            fontRenderer.drawStringWithShadow(I18n.format(UtilMethods.translateMoney(te.getLongField(TileVending.LONG_FIELD_CASHREGISTER))), 15, 0, Integer.parseInt("ffffff", 16));

            buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j - 5, 20, 20, TextFormatting.BLUE + "$"));  //Changes '$' to blue to signify clicking it cashes out Machines money
        } else { //TRADE MODE
            buttonList.set(BUTTONCHANGE, new GuiButton(BUTTONCHANGE, i + 143, j - 5, 20, 20, TextFormatting.GREEN + "$"));   //Changes '$' to green to signify clicking it cashes out Players money
        }
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        message(); // Warning Message Rendering */
    }

    /** Custom Item Stack size Rendering **/
    private void drawItemStackSize() {
        //TODO Dont render if creative and infinite and don't want items collected
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);

        String num; // Used to calculate the size of each itemStack */
        int startY = -19; // Starting Y value */
        int columnCount = 5;

        // Loops through each slot to calculate and render its stack size */
        for (int j = 0; j < columnCount; j++) {
            for (int i = 0; i < 5; i++) {
                num = " ";
                int index = (i + (5 * j));
                ItemTradein item = te.getItemTradein(index);

                if (!item.getStack().isEmpty()) // If ItemStack is Empty don't render a number, otherwise render itemStack size*/

                    if (te.getField(FIELD_MODE) == 1) { // STOCK MODE
                        num = Integer.toString(item.getSize());
                    } else { // TRADE MODE
                        if(item.getUntil() - item.getSize() > 0) {
                            int amount = item.getAmount();
                            if (amount > 1)
                                num = Integer.toString(amount);
                        } else if (item.getSize() >= te.getItemTradeinMax()){
                            num = TextFormatting.RED + "FULL";
                        }
                    }

                if (num.equals("0"))
                    num = TextFormatting.RED + "  0"; // If itemStack size is 0 then color is red */
                if (num.length() == 1) num = "  " + num; // Spacing to center numbers correctly */
                if (num.length() == 2) num = " " + num; // Spacing to center numbers correctly */

                if(num.equals(TextFormatting.RED + "FULL")){ // Centers text better for 'FULL' Tag
                    fontRenderer.drawStringWithShadow(num, 63 + (i * 26), startY + (j * 26), -1); // Renders text */
                } else {
                    fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1); // Renders text */
                }

                GlStateManager.color(0xFF, 0xFF, 0xFF); // Resets color in GL to prevent visual glitches */
            }
        }

        // Resets gl */
        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    /** 'Info Tag' Rendering */
    private void drawAdminPanel() {
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;
        if (te.getField(TileVending.FIELD_MODE) == 1) { // STOCK MODE */
            Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), -74, 10, Integer.parseInt("ffffff", 16)); // Title Text */

            // Renders currently selected ItemStacks display name and shrinks it */
            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            String itemName = "[" + te.getSelectedName() + "]";
            fontRenderer.drawString(I18n.format(itemName), -90 - (itemName.length() * 2), 28, Integer.parseInt("08285e", 16));
            GL11.glPopMatrix();

            // Sets Price textfield's position and enables it */
            fontRenderer.drawStringWithShadow(I18n.format("$"), -90, 30, Color.lightGray.getRGB());
            fieldPrice.x = i - 82;
            fieldPrice.y = j + 30;
            fieldPrice.setVisible(true);

            // Sets Amount textfield's position and enables it */
            fontRenderer.drawStringWithShadow(I18n.format("guivending.amnt"), -90, 40, Color.lightGray.getRGB());
            fieldAmnt.x = i - 65;
            fieldAmnt.y = j + 40;
            fieldAmnt.setVisible(true);

            // Sets Until textfield's position and enables it */
            fontRenderer.drawStringWithShadow(I18n.format("guitradein.until"), -90, 50, Color.lightGray.getRGB());
            fieldUntil.x = i - 65;
            fieldUntil.y = j + 50;
            fieldUntil.setVisible(true);

            GlStateManager.color(0xFF, 0xFF, 0xFF); // Resets GL color to prevent visual bugs */
        } else { // TRADE MODE, disables anything that shouldn't enabled*/
            fieldPrice.setVisible(false);
            fieldAmnt.setVisible(false);
            fieldUntil.setVisible(false);
        }
    }

    /** 'Selection Tag' Rendering **/
    private void drawSelectionOverlay() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);

        if (te.getField(TileTradein.FIELD_MODE) == 1) { // STOCK MODE */
            int slotId = te.getField(FIELD_SELECTED);
            int slotColumn = 0, slotRow = 0;

            // Calculating the right slot Row and Column positions*/
            if (slotId >= 0 && slotId <= 4) {
                slotRow = slotId;
            } else if (slotId >= 5 && slotId <= 9) {
                slotColumn = 1;
                slotRow = (slotId) - 5;
            } else if (slotId >= 10 && slotId <= 14) {
                slotColumn = 2;
                slotRow = (slotId) - 10;
            } else if (slotId >= 15 && slotId <= 19) {
                slotColumn = 3;
                slotRow = (slotId) - 15;
            } else if (slotId >= 20 && slotId <= 24) {
                slotColumn = 4;
                slotRow = (slotId) - 20;
            }

            // Drawing the selection tag and selection box */
            drawTexturedModalRect(42 + (18 * slotRow), -24 + (18 * slotColumn), 21, 172, 20, 20);
            drawTexturedModalRect(42 + (18 * slotRow) + 14, -24 + (18 * slotColumn) + 15, 83, 3, 16, 14);
        }
    }

    /** Updated all Text fields**/
    private void updateTextField(){
        ItemTradein item = te.getItemTradein(te.getField(FIELD_SELECTED));
        boolean isItem = item.getStack().getItem() != Items.AIR;;

        fieldPrice.setEnabled(isItem);
        fieldPrice.setText(UtilMethods.translateMoney(item.getCost()));

        fieldAmnt.setEnabled(isItem);
        fieldAmnt.setText(Integer.toString(item.getAmount()));

        fieldUntil.setEnabled(isItem);
        fieldUntil.setText(Integer.toString(item.getUntil()));

        fieldItemMax.setEnabled(isItem);
        fieldItemMax.setText(Integer.toString(item.getItemMax()));

        fieldTimeRestock.setEnabled(isItem);
        fieldTimeRestock.setText(Integer.toString(item.getTimeRaise()));
    }

    /** Colors background texture based on block color **/
    private void guiColor(EnumDyeColor dyeColor) {
        switch (dyeColor) {
            case LIGHT_BLUE:
                GlStateManager.color(143F / 255F, 185F / 255F, 244F / 255F, 1.0F);
                break;
            case MAGENTA:
                GlStateManager.color(203F / 255F, 105F / 255F, 197F / 255F, 1.0F);
                break;
            case YELLOW:
                GlStateManager.color(231F / 255F, 231F / 255F, 42F / 255F, 1.0F);
                break;
            case SILVER:
                GlStateManager.color(196F / 255F, 196F / 255F, 203F / 255F, 1.0F);
                break;
            case PURPLE:
                GlStateManager.color(164F / 255F, 83F / 255F, 206F / 255F, 1.0F);
                break;
            case ORANGE:
                GlStateManager.color(230F / 255F, 158F / 255F, 52F / 255F, 1.0F);
                break;
            case WHITE:
                GlStateManager.color(234F / 255F, 234F / 255F, 234F / 255F, 1.0F);
                break;
            case GREEN:
                GlStateManager.color(74F / 255F, 107F / 255F, 24F / 255F, 1.0F);
                break;
            case BROWN:
                GlStateManager.color(112F / 255F, 68F / 255F, 37F / 255F, 1.0F);
                break;
            case BLACK:
                GlStateManager.color(31F / 255F, 31F / 255F, 36F / 255F, 1.0F);
                break;
            case PINK:
                GlStateManager.color(247F / 255F, 180F / 255F, 214F / 255F, 1.0F);
                break;
            case LIME:
                GlStateManager.color(135F / 255F, 202F / 255F, 49F / 255F, 1.0F);
                break;
            case GRAY:
                GlStateManager.color(160F / 255F, 160F / 255F, 160F / 255F, 1.0F);
                break;
            case CYAN:
                GlStateManager.color(60F / 255F, 142F / 255F, 176F / 255F, 1.0F);
                break;
            case BLUE:
                GlStateManager.color(45F / 255F, 93F / 255F, 167F / 255F, 1.0F);
                break;
            case RED:
                GlStateManager.color(211F / 255F, 90F / 255F, 86F / 255F, 1.0F);
                break;
        }
    }

    /** Render Hover over tool tips **/
    @Override
    protected void renderToolTip(ItemStack stack, int x, int y) {
        int i = (x - (width - xSize) / 2);
        int j = (y - (height - ySize) / 2);

        // If mouse is hovering over a slot */
        if (j <= 66 && j >= -23 && i >= 43) {
            // Initial slot X and Y starting positions */
            int startY = -23;
            int startX = 43;

            // Calculating which row and column then through that which slot is being hovered over */
            int row = ((j - startY) / 18);
            int column = ((i - startX) / 18);
            int slot = column + (row * 5);

            //Collects the vanilla tooltip for the item
            java.util.List<String> list = new ArrayList<>();
            List<String> ogTooltip = stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
            int tooltipStart = 1;

            if (ogTooltip.size() > 0) { // If the vanilla tooltip has anything in it add its 0 index (most likely item name) and set its rarity colour if it has any */
                list.add(ogTooltip.get(0));
                list.set(0, stack.getItem().getForgeRarity(stack).getColor() + list.get(0));
            }

            if (ogTooltip.size() > 1) // If vanilla tooltip has any more information add it AFTER our added information */
                if (!ogTooltip.get(1).equals("")) {
                list.add(TextFormatting.GRAY + ogTooltip.get(1));
                tooltipStart = 2;
            }

            ItemTradein item = te.getItemTradein(slot);
            int cost = item.getCost(); // Get Items cost */
            int amount = item.getAmount(); // Get Item Bulk Amount

            // Adding items price to tooltip: What is written depends on if machine has enough funds to buy the item */
            if (te.getField(FIELD_MODE) == 0) { // TRADE MODE */
                if (item.getCost() <= te.getLongField(LONG_FIELD_CASHREGISTER)) {
                    // Changes text if slot is paying on bulk amount or not
                    if (item.getAmount() == 1) {
                        list.add("Payout of " + TextFormatting.GREEN + "$" + UtilMethods.translateMoney(cost));
                    } else {
                        list.add("Payout of " + TextFormatting.GREEN + "$" + UtilMethods.translateMoney(cost) +
                                TextFormatting.RESET + " per " + TextFormatting.BLUE + amount);
                    }

                    // Changes text if slot wants more of said item or not
                    if(item.getUntil() > 0){
                        if(item.getUntil() - item.getSize() <= 0){
                            list.add(TextFormatting.RED + "Capacity Full");
                        } else {
                            list.add(TextFormatting.DARK_GREEN + "Wants " + (item.getUntil() - item.getSize()) + " more");
                        }
                    }

                }else{
                    list.add(TextFormatting.RED + "Machine cannot afford $" + UtilMethods.translateMoney(cost));
                }
            }

            // Actually adding the vanilla extra tooltip info back if there is any */
            for (; tooltipStart < stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).size(); tooltipStart++) {
                list.add(TextFormatting.GRAY + stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL).get(tooltipStart));
            }

            // Adding the tooltip info to the tooltip */
            FontRenderer font = stack.getItem().getFontRenderer(stack);
            net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
            drawHoveringText(list, x, y, (font == null ? fontRenderer : font));
            net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        } else {
            super.renderToolTip(stack, x, y);
        }
    }

    /** Method activates if a key is typed **/
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        int numChar = Character.getNumericValue(typedChar); // Collects keys input */

        // STOCK MODE: Ensures keys input are only numbers or backspace type keys */
        if ((te.getField(FIELD_MODE) == 1) && ((numChar >= 0 && numChar <= 9) || (keyCode == 14) || keyCode == 211 || (keyCode == 203) || (keyCode == 205) || (keyCode == 52))) {

            if ((!fieldPrice.getText().contains(".")) || keyCode != 52) {
                if (fieldPrice.textboxKeyTyped(typedChar, keyCode)) setPayout();
            }

            if (fieldPrice.getText().length() > 0)
                if (fieldPrice.getText().substring(fieldPrice.getText().length() - 1).equals("."))
                    fieldPrice.setMaxStringLength(fieldPrice.getText().length() + 2);
            if (!fieldPrice.getText().contains(".")) fieldPrice.setMaxStringLength(7);

            if (fieldAmnt.textboxKeyTyped(typedChar, keyCode))
                setAmnt(te.getField(FIELD_SELECTED), fieldAmnt);

            if (fieldUntil.textboxKeyTyped(typedChar, keyCode))
                setUntil(te.getField(FIELD_SELECTED),fieldUntil);

            //TODO when we implement this shit
            /*if (te.getField(FIELD_CREATIVE) == 1 && te.getField(FIELD_FINITE) == 1) {
                if (fieldItemMax.textboxKeyTyped(typedChar, keyCode))
                    setItemMax(fieldItemMax);

                if (fieldTimeRestock.textboxKeyTyped(typedChar, keyCode))
                    setTimeRestock(fieldTimeRestock);
            }*/
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    /** Method activates if mouse is clicked **/
    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (te.getField(TileVending.FIELD_MODE) == 1) { // STOCK MODE */
            super.mouseClicked(mouseX, mouseY, mouseButton);
            fieldPrice.mouseClicked(mouseX, mouseY, mouseButton);
            fieldAmnt.mouseClicked(mouseX, mouseY, mouseButton);
            fieldUntil.mouseClicked(mouseX, mouseY, mouseButton);
            fieldItemMax.mouseClicked(mouseX, mouseY, mouseButton);
            fieldTimeRestock.mouseClicked(mouseX, mouseY, mouseButton);
            updateTextField();
        } else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    /** Actions performed by various buttons **/
    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getField(FIELD_MODE) == 1) ? 0 : 1, FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                buttonList.get(BUTTONADMIN).displayString = (te.getField(FIELD_MODE) == 0) ? I18n.format("guivending.stock") : I18n.format("guivending.trade");
                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            case BUTTONCHANGE:
                PacketOutChangeToServer pack0 = new PacketOutChangeToServer();
                pack0.setData(te.getPos(), false);
                PacketHandler.INSTANCE.sendToServer(pack0);
                te.outChange(false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }

    /** Sets the 'cost' of the selected item from the text field**/
    private void setPayout() {
        if (fieldPrice.getText().length() > 0) {
            int newCost = 0;

            // Converts string to money system. EX: $5 = 500, $12.69 = 1269 */

            //If there is a '.' collect and convert the amount after the '.' to money system */
            if (fieldPrice.getText().contains(".")) { // lastIndexOf() outputs how many digits are before the specified string */
                if (fieldPrice.getText().lastIndexOf(".") + 1 != fieldPrice.getText().length()) { // If there are more digits past the '.' */
                    if (fieldPrice.getText().lastIndexOf(".") + 2 == fieldPrice.getText().length()) { // If there is only one digit after the '.' add a '0' to the end*/
                        newCost = Integer.parseInt(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1) + "0");
                    } else { // If there are 2 digits after the '.' collect number*/
                        newCost = Integer.parseInt(fieldPrice.getText().substring(fieldPrice.getText().lastIndexOf(".") + 1));
                    }
                }

                // If '.' collect first half of number, multiply it by 100 to convert it to money system and add to newCost*/
                if (fieldPrice.getText().lastIndexOf(".") != 0)
                    newCost += Integer.parseInt(fieldPrice.getText().substring(0, fieldPrice.getText().lastIndexOf("."))) * 100;

            } else { // If no '.' just multiply number by 100 to convert it to number system */
                newCost = Integer.parseInt(fieldPrice.getText()) * 100;
            }

            // Send new cost to itemTradeIn */
            te.getItemTradein(te.getField(FIELD_SELECTED)).setCost(newCost);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), newCost, PacketSetItemToServer.FIELD_COST, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    /** Sets the 'amount' of the selected item from the text field **/
    private void setAmnt(int slot, GuiTextField guiTextField) {
        if (guiTextField.getText().length() > 0) {
            int amount = Integer.parseInt(guiTextField.getText()); // Set amount as per text field */

            // If slot is empty set amount textfield to 1, or if textfield is higher then maxStackSize set to maxStackSize */
            if (te.getItemTradein(slot).getStack().isEmpty()) {
                amount = 1;
            } else if (Integer.parseInt(guiTextField.getText()) > te.getItemTradein(slot).getStack().getMaxStackSize())
                amount = te.getItemTradein(slot).getStack().getMaxStackSize();

            if (amount == 0) amount = 1; // If text field is 0 set to 1, 0 would break things */

            // Set new amount to ItemTradeIn */
            te.getItemTradein(slot).setAmount(amount);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), amount, PacketSetItemToServer.FIELD_AMOUNT, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    /** Sets the 'Buy Until' of the selected item from the text field **/
    private void setUntil(int slot, GuiTextField guiTextField){
        if (guiTextField.getText().length() > 0) {
            int until = Integer.parseInt(guiTextField.getText()); // Set until as per text field */

            // If slot is empty set until textfield to 0, or if textfield is higher then maxStackSize set to maxStackSize */
            if (te.getItemTradein(slot).getStack().isEmpty()) {
                until = 0;
            } else if (Integer.parseInt(guiTextField.getText()) > te.getItemTradeinMax())
                until = te.getItemTradeinMax();

            // Set new until to ItemTradeIn */
            te.getItemTradein(slot).setUntil(until);
            PacketSetItemToServer pack = new PacketSetItemToServer();
            pack.setData(te.getField(FIELD_SELECTED), until, PacketSetItemToServer.FIELD_UNTIL, te.getPos());
            PacketHandler.INSTANCE.sendToServer(pack);

            te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
        }
    }

    /** Error Message Rendering **/
    private void message() {
        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        FontRenderer fontRenderer = Minecraft.getMinecraft().ingameGUI.getFontRenderer();
        String message = te.getMessage();

        if (!message.equals("")) { // If message is not empty */
            drawTexturedModalRect(70 - ((message.length()) * 5 / 2), 55, 0, 20, 21, 21); // Draws Back panel */

            // Extends the Back panel to fit message */
            int panelAmounts = (message.length() * 5) / 15;
            for (int i = 0; i < panelAmounts; i++)
                drawTexturedModalRect(91 - (message.length() * 5 / 2) + (i * 17), 55, 4, 20, 17, 21);

            drawTexturedModalRect(91 - (message.length() * 5 / 2) + (panelAmounts * 17), 55, 0, 42, 21, 21); // Ends Back panel of message */

            drawTexturedModalRect(74 - (message.length() * 5 / 2), 58, 41, 1, 19, 17); // Warning Symbol */
        }
        fontRenderer.drawStringWithShadow(message, 94 - (message.length() * 5) / 2, 62, 0xDE3131); // Message Text */
        GlStateManager.color(0xFF, 0xFF, 0xFF); // Reset GL color to prevent visual bugs */
    }
}
