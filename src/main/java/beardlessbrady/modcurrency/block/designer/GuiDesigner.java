package beardlessbrady.modcurrency.block.designer;

import beardlessbrady.modcurrency.block.economyblocks.tradein.ContainerTradein;
import beardlessbrady.modcurrency.block.economyblocks.tradein.TileTradein;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2020-03-20
 */
public class GuiDesigner extends GuiContainer {
    private TileDesigner te;

    public GuiDesigner(EntityPlayer entityPlayer, TileDesigner te) {
        super(new ContainerDesigner(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
}
