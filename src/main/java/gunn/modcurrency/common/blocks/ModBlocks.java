package gunn.modcurrency.common.blocks;

/**
 * Distributed with the Currency-Mod for Minecraft.
 * Copyright (C) 2016  Brady Gunn
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
 * File Created on 2016-10-30.
 */
public class ModBlocks {
    public static BlockTop blockTop;
    public static BlockVendor blockVendor;
    public static BlockSeller blockSeller;

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        blockTop = new BlockTop();
        blockVendor = new BlockVendor();
        blockSeller = new BlockSeller();
    }

    public static void ItemModels(){
        blockVendor.initModel();
        blockSeller.initModel();
    }
    
    public static void addRecipes(){
        blockVendor.recipe();
        blockSeller.recipe();
    }
}
