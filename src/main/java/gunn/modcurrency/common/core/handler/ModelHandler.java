package gunn.modcurrency.common.core.handler;

import gunn.modcurrency.client.model.BakedModelVendor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
 * File Created on 2016-12-28
 */
public class ModelHandler {
    public static final ModelHandler instance = new ModelHandler();

    private  ModelHandler(){};

    @SubscribeEvent
    public void onModelBakeEvent(ModelBakeEvent e){
        Object object = e.getModelRegistry().getObject(BakedModelVendor.variantTag);
        if(object instanceof IBakedModel){
            IBakedModel existingModel = (IBakedModel)object;
            BakedModelVendor customModel = new BakedModelVendor(existingModel);
            e.getModelRegistry().putObject(BakedModelVendor.variantTag, customModel);
        }
    }
}
