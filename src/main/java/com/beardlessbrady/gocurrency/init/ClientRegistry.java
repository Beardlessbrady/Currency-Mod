package com.beardlessbrady.gocurrency.init;

import com.beardlessbrady.gocurrency.GOCurrency;
import com.beardlessbrady.gocurrency.items.CurrencyItem;
import net.java.games.input.Keyboard;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.awt.event.KeyEvent;
import java.security.Key;

/**
 * Created by BeardlessBrady on 2021-02-27 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class ClientRegistry {
    private final IEventBus eventBus;
    public static KeyBinding[] keyBindings;

    public ClientRegistry(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public static void doClientStuff() {
        ItemModelsProperties.registerProperty(CommonRegistry.ITEM_CURRENCY.get(), new ResourceLocation("currency"), CurrencyItem::getPropertyOverride);
        registerKeyBindings();
    }

    public void registerClientOnlyEvents() {
        eventBus.register(ClientEventSubscriber.class);
    }

    public static void registerKeyBindings() {
        keyBindings = new KeyBinding[2];

        keyBindings[0] = new KeyBinding("key.gocurrency.full", GLFW.GLFW_KEY_LEFT_SHIFT, "key.gocurrency.category");
        keyBindings[1] = new KeyBinding("key.gocurrency.half", GLFW.GLFW_KEY_LEFT_ALT, "key.gocurrency.category");

        for (KeyBinding key: keyBindings) {
            net.minecraftforge.fml.client.registry.ClientRegistry.registerKeyBinding(key);
        }
    }
}
