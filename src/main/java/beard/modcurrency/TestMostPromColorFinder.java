package beard.modcurrency;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2018-02-25
 */
public class TestMostPromColorFinder {

    public static void main(String[] args) {
        BufferedImage poop = null;
        try {
            poop = javax.imageio.ImageIO.read(new File("modcurrency:item/iron_ingot"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(poop.getRGB(5,6));
    }
}
