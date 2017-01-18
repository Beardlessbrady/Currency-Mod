package gunn.modcurrency.client.guis;

import gunn.modcurrency.common.containers.ContainerWallet;
import gunn.modcurrency.common.items.ItemWallet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * File Created on 2017-01-17
 */
public class GuiWallet extends GuiContainer{
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiWalletTexture.png");
    private static final ResourceLocation EXTRA_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/GuiWalletExtraTexture.png");
    public static final int GUI_XPOS_OFFPUT = -19;

    public GuiWallet(InventoryPlayer invPlayer, ItemStack wallet) {
        super(new ContainerWallet(invPlayer, wallet));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        //Background
        drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT, guiTop,0 ,0 , 213, 201);

        Minecraft.getMinecraft().getTextureManager().bindTexture(EXTRA_TEXTURE);
        switch(ItemWallet.WALLET_ROW_COUNT){
            default:
            case 4:     //4 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 70,0 ,152 ,162 , 18);
            case 3:     //3 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 16,0 ,98 ,162 , 18);
            case 2:     //2 Rows
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 52,0 ,134 ,162 , 18);
            case 0:
            case 1:     //1 Row
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 25, guiTop + 34,0 ,116 ,162 , 18);
        }

    }


}
