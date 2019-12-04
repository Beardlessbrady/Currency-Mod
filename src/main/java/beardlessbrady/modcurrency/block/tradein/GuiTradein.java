package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.network.PacketHandler;
import beardlessbrady.modcurrency.network.PacketOutChangeToServer;
import beardlessbrady.modcurrency.network.PacketSetFieldToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

import static beardlessbrady.modcurrency.block.TileEconomyBase.FIELD_MODE;
import static beardlessbrady.modcurrency.block.vending.TileVending.FIELD_FINITE;

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

    private TileTradein te;

    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    public GuiTradein(EntityPlayer entityPlayer, TileTradein te) {
        super(new ContainerTradein(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttonList.add(new GuiButton(BUTTONCHANGE, i + 143, j + 39, 20, 20, "$"));

        String mode = (te.getField(FIELD_MODE) == 1) ? "STOCK" : "TRADE";
        buttonList.add(new GuiButton(BUTTONADMIN, i + 137, j - 42, 32, 20, mode));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACK_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        //Basics GUI labels
        fontRenderer.drawString(I18n.format("tile.modcurrency:blocktradein.name"), 8, -42, Integer.parseInt("ffffff", 16));
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 87, Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 18, 3, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 145, 3, Color.lightGray.getRGB());
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getField(FIELD_MODE) == 1) ? 0 : 1, FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                buttonList.get(BUTTONADMIN).displayString = (te.getField(FIELD_MODE) == 0) ? "STOCK" : "TRADE";

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
            case BUTTONCHANGE:
             ///   PacketOutChangeToServer pack0 = new PacketOutChangeToServer();
             //   pack0.setData(te.getPos(), false);
             //   PacketHandler.INSTANCE.sendToServer(pack0);
              //  te.outChange(false);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + button.id);
        }
    }
}
