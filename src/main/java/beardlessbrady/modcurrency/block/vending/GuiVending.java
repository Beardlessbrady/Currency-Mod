package beardlessbrady.modcurrency.block.vending;

import beardlessbrady.modcurrency.ModCurrency;
import beardlessbrady.modcurrency.network.PacketHandler;
import beardlessbrady.modcurrency.network.PacketSetFieldToServer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-02-10
 */

public class GuiVending extends GuiContainer {
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/vendingmachinegui.png");
    private static final ResourceLocation ASSET_TEXTURE = new ResourceLocation(ModCurrency.MODID, "textures/gui/guiassets.png");

    TileVending te;


    //Button ID's
    private static final int BUTTONCHANGE = 0;
    private static final int BUTTONADMIN = 1;

    public GuiVending(EntityPlayer entityPlayer, TileVending te){
        super(new ContainerVending(entityPlayer, te));
        this.te = te;
    }

    @Override
    public void initGui() {
        super.initGui();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;

        this.buttonList.add(new GuiButton(BUTTONCHANGE,  i + 143 , j + 27, 20, 20, "$"));

        String mode = (te.getIntField(te.FIELD_MODE) == 1)? "STOCK" : "SELL";
        this.buttonList.add(new GuiButton(BUTTONADMIN,  i + 137 , j - 42, 32, 20, mode));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(BACKGROUND_TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop - 47, 0, 0, 176, 254);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        if(te.getPlayerUsing().equals(te.getOwner())){
            buttonList.get(BUTTONADMIN).visible = true;
        }else{
            buttonList.get(BUTTONADMIN).visible = false;
        }

        fontRenderer.drawString(I18n.format("tile.modcurrency:blockvending.name"), 8, -42,  Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("container.inventory"), 8, 114,  Color.darkGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.in"), 149, -8, Color.lightGray.getRGB());
        fontRenderer.drawString(I18n.format("guivending.out"), 80, 68, Color.lightGray.getRGB());

        drawItemStackSize();

        Minecraft.getMinecraft().getTextureManager().bindTexture(ASSET_TEXTURE);
        drawSelectionOverlay();
        drawAdminPanel();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch(button.id){
            case BUTTONADMIN:
                PacketSetFieldToServer pack = new PacketSetFieldToServer();
                pack.setData((te.getIntField(te.FIELD_MODE) == 1) ? 0 : 1, te.FIELD_MODE, te.getPos());
                PacketHandler.INSTANCE.sendToServer(pack);

                String mode = (te.getIntField(te.FIELD_MODE) == 0)? "STOCK" : "SELL";
                buttonList.get(BUTTONADMIN).displayString = mode;

                te.getWorld().notifyBlockUpdate(te.getPos(), te.getBlockType().getDefaultState(), te.getBlockType().getDefaultState(), 3);
                break;
        }
    }

    private void drawItemStackSize() {
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glPushMatrix();
        GL11.glScalef(0.7F, 0.7F, 0.8F);

        String num = " ";
        int startY = -29;
        int columnCount = 5;

        for(int j=0; j<columnCount; j++) {
            for (int i = 0; i < 5; i++) {
                if (te.getItemSize(i + (5 * j)) != 0 && te.getItemSize(i + (5 * j)) > 0) {
                    num = Integer.toString(te.getItemSize(i + (5 * j)));
                } else if (!te.getItemStack(i + (5*j)).isEmpty()){
                    num = "Out";
                } else {
                    num = " ";
                }

                if(num.length() == 1) num = "  " + num;
                if(num.length() == 2) num = " " + num;

                this.fontRenderer.drawStringWithShadow(num, 66 + (i * 26), startY + (j * 26), -1);
            }
        }

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private void drawAdminPanel(){
        if(te.getIntField(TileVending.FIELD_MODE) == 1){
            drawTexturedModalRect(177, 0, 0, 215, 106, 41);

            fontRenderer.drawStringWithShadow(I18n.format("guivending.slotsettings"), 206, 4, Integer.parseInt("ffffff", 16));

            GL11.glPushMatrix();
            GL11.glScaled(0.7, 0.7, 0.7);
            fontRenderer.drawStringWithShadow(I18n.format("[" + te.getSelectedName() + "]"), 279, 20, Integer.parseInt("ffffff", 16));
            GL11.glPopMatrix();
        }
    }

    private void drawSelectionOverlay() {
        if(te.getIntField(TileVending.FIELD_MODE) == 1) {
            int slotId = te.getIntField(TileVending.FIELD_SELECTED);
            int slotColumn = 0, slotRow = 0;

            if (slotId >= 37 && slotId <= 41) {
                slotColumn = 0;
                slotRow = slotId - 37;
            } else if (slotId >= 42 && slotId <= 46) {
                slotColumn = 1;
                slotRow = (slotId - 37) - 5;
            } else if (slotId >= 47 && slotId <= 51) {
                slotColumn = 2;
                slotRow = (slotId - 37) - 10;
            } else if (slotId >= 52 && slotId <= 56) {
                slotColumn = 3;
                slotRow = (slotId - 37) - 15;
            } else if (slotId >= 57 && slotId <= 61) {
                slotColumn = 4;
                slotRow = (slotId - 37) - 20;
            }

            drawTexturedModalRect(42 + (18 * slotRow), -32 + (18 * slotColumn), 0, 185, 29, 29);
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        int i = Integer.signum(Mouse.getEventDWheel());
        if(i==1){
            if(te.getIntField(TileVending.FIELD_SELECTED) == 61){
                te.setIntField(TileVending.FIELD_SELECTED, 37);
                te.setSelectedName(te.getItemStack(0).getDisplayName());

            }else {
                te.setIntField(TileVending.FIELD_SELECTED, te.getIntField(TileVending.FIELD_SELECTED) + 1);
                te.setSelectedName(te.getItemStack(te.getIntField(TileVending.FIELD_SELECTED) -37).getDisplayName());
            }
        }else if(i==-1) {
            if (te.getIntField(TileVending.FIELD_SELECTED) == 37) {
                te.setIntField(TileVending.FIELD_SELECTED, 61);
                te.setSelectedName(te.getItemStack(24).getDisplayName());
            } else {
                te.setIntField(TileVending.FIELD_SELECTED, te.getIntField(TileVending.FIELD_SELECTED) - 1);
                te.setSelectedName(te.getItemStack(te.getIntField(TileVending.FIELD_SELECTED) -37).getDisplayName());
            }
        }

        super.handleMouseInput();
    }
}
