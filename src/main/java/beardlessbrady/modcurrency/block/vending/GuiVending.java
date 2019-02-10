package beardlessbrady.modcurrency.block.vending;

import net.minecraft.client.gui.inventory.GuiContainer;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class GuiVending extends GuiContainer {
    public GuiVending(TileVending tile) {
        super(new ContainerVending(tile));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

    }
}
