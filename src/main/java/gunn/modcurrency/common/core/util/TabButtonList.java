package gunn.modcurrency.common.core.util;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import scala.Int;

import java.util.ArrayList;
import java.util.List;

/**
 * Distributed with the Currency-Mod for Minecraft
 * Copyright (C) 2017  Brady Gunn
 *
 * File Created on 2017-02-12
 */
public class TabButtonList {
    int startX, startY;
    int nextY = 0;
    int tabAmnt = 0;
    List<GuiButton> buttonList;
    ArrayList<String> tabOrder = new ArrayList();   //Top to Bottom
    ArrayList<Integer> tabExt = new ArrayList();   //Top to Bottom
    ArrayList<Integer> tabDefaultY = new ArrayList();

    public TabButtonList(List<GuiButton> buttonList, int startX, int startY){
        this.startX = startX;
        this.startY = startY;
        this.buttonList = buttonList;
    }

    public void addTab(String name, ResourceLocation textureLoc, int textureX, int textureY, int buttonId){
        if(nextY == 0) nextY = startY;

        this.buttonList.add(new TabButton(name, buttonId, startX, nextY, textureX, textureY, 21, 22, "", textureLoc));
        tabDefaultY.add(nextY - startY);

        tabAmnt++;
        if(tabAmnt == 1){
            nextY = startY + 23;
        }else {
            nextY = nextY + 22;
        }

        this.tabOrder.add(name);
        this.tabExt.add(0);
    }

    public void checkOpenState(String buttonName, boolean booleanVar) {
        String nextTab;
        TabButton currentButton;

        for (int i = 0; i < tabOrder.size(); i++) {
            if (tabOrder.get(i) == buttonName) {
                for (int j = i + 1; j < tabOrder.size(); j++) {
                    nextTab = tabOrder.get(j);

                    for (int k = 0; k < buttonList.size(); k++) {
                        if (buttonList.get(k) instanceof TabButton) {
                            currentButton = ((TabButton) buttonList.get(k));

                            if (currentButton.name == nextTab) {
                                if (booleanVar) {
                                    buttonList.set(k, new TabButton(currentButton.name, currentButton.buttonid, currentButton.xPosition, tabDefaultY.get(j) + startY + tabExt.get(i), currentButton.minU, currentButton.minV, 21, 22, "", currentButton.textureLoc));
                                } else {
                                    buttonList.set(k, new TabButton(currentButton.name, currentButton.buttonid, currentButton.xPosition, tabDefaultY.get(j) + startY, currentButton.minU, currentButton.minV, 21, 22, "", currentButton.textureLoc));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void setOpenState(String buttonName, int height){
        for(int i = 0; i < tabOrder.size(); i ++){
            if(tabOrder.get(i) == buttonName) tabExt.set(i, height);
        }
    }
}
