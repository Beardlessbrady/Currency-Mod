package gunn.modcurrency.common.blocks;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
    public static BlockVendor blockvendor;
    public static BlockTopVendor blocktopvendor;

    public static void preInit(){
        setupBlocks();
    }

    private static void setupBlocks(){
        blockvendor = new BlockVendor();
        blocktopvendor = new BlockTopVendor();
        
    }

    public static void initInitModels(){
        blockvendor.initModel();
    }
    
    public static void addRecipes(){
        blockvendor.recipe();
    }
}
