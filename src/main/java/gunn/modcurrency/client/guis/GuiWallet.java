package gunn.modcurrency.client.guis;

import gunn.modcurrency.common.containers.ContainerWallet;
import gunn.modcurrency.common.items.ItemWallet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
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
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiwallettexture.png");
    private static final ResourceLocation EXTRA_TEXTURE = new ResourceLocation("modcurrency", "textures/gui/guiwalletextratexture.png");
    public static final int GUI_XPOS_OFFPUT = -19;

    int fullness = 0;

    public GuiWallet(EntityPlayer player, ItemStack wallet) {
        super(new ContainerWallet(player, wallet));

        NBTTagCompound compound = wallet.getTagCompound();
        fullness = compound.getInteger("full");
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        //Background
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT, guiTop,0 ,0 , 213, 201);

        //Background Extras TODO
        Minecraft.getMinecraft().getTextureManager().bindTexture(EXTRA_TEXTURE);
        switch(fullness){
            default: break;
            case 3:
            case 2:
            case 1:
                drawTexturedModalRect(guiLeft + GUI_XPOS_OFFPUT + 1, guiTop - 5,0 ,0 , 213, 14);
        }

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
