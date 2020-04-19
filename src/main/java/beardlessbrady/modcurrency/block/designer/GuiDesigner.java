package beardlessbrady.modcurrency.block.designer;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.network.PacketHandler;
import beardlessbrady.modcurrency.network.PacketSetFieldToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

import static beardlessbrady.modcurrency.block.economyblocks.TileEconomyBase.FIELD_MODE;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class GuiDesigner extends GuiContainer {
    private static final ResourceLocation BACK_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/currencydesignergui.png");
    private TileDesigner te;

    public GuiDesigner(EntityPlayer entityPlayer, TileDesigner te) {
        super(new ContainerDesigner(entityPlayer, te));
        this.te = te;
    }

    //Button ID's
    private static final int BUTTONADD = 0;

    @Override
    public void initGui() {
        super.initGui();
        int i = (width - xSize) / 2;
        int j = (height - ySize) / 2;

        buttonList.add(new GuiButton(BUTTONADD, i + 183, j - 33, 20, 20, "+"));
        buttonList.get(BUTTONADD).enabled = false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACK_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 15, 242, 240); // Background texture, TE inventory*/
        GlStateManager.popMatrix();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(te.getField(TileDesigner.FIELD_TEMPLATE) == 1){ // If template is in machine
            buttonList.get(BUTTONADD).enabled = true;
        } else {
            buttonList.get(BUTTONADD).enabled = false;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case BUTTONADD:

                break;
        }
    }
}
