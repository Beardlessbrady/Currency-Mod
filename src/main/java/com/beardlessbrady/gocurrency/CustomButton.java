package com.beardlessbrady.gocurrency;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by BeardlessBrady on 2021-08-22 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class CustomButton extends Button {
    public static final Button.ITooltip field_238486_s_ = (button, matrixStack, mouseX, mouseY) -> {
    };
    protected final Button.IPressable onPress;
    protected final Button.ITooltip onTooltip;
    protected final int uOffset;
    protected final int vOffset;
    protected final int h_uOffset;
    protected final int h_vOffset;


    public CustomButton(int x, int y, int width, int height, int uOffset, int vOffset, int h_uOffset, int h_vOffset,
                        ITextComponent title, Button.IPressable pressedAction) {
        this(x, y, width, height, uOffset, vOffset, h_uOffset, h_vOffset, title, pressedAction, field_238486_s_);
    }

    public CustomButton(int x, int y, int width, int height, int uOffset, int vOffset, int h_uOffset, int h_vOffset,
                        ITextComponent title, Button.IPressable pressedAction, Button.ITooltip onTooltip) {
        super(x, y, width, height, title, pressedAction, onTooltip);
        this.onPress = pressedAction;
        this.onTooltip = onTooltip;
        this.uOffset = uOffset;
        this.vOffset = vOffset;
        this.h_uOffset = h_uOffset;
        this.h_vOffset = h_vOffset;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Add to be custom later on
        ResourceLocation TEXTURE = new ResourceLocation("gocurrency", "textures/gui/vending.png");

        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(TEXTURE);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        if (this.isHovered()) {
            this.blit(matrixStack, this.x, this.y, this.h_uOffset, this.h_vOffset, this.width, this.height);
        } else {
            this.blit(matrixStack, this.x, this.y, this.uOffset, this.vOffset, this.width, this.height);
        }
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);
    }
}
