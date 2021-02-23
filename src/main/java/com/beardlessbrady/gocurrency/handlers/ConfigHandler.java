package com.beardlessbrady.gocurrency.handlers;


import com.beardlessbrady.gocurrency.GOCurrency;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by BeardlessBrady on 2021-02-23 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
@Mod.EventBusSubscriber(modid = "gocurrency", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigHandler {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getValue();
        CLIENT = specPair.getKey();
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends Double>> configCurrencyValue;

    public static void bakeConfig() {
        configCurrencyValue = CLIENT.getConfigCurrencyValue();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigHandler.CLIENT_SPEC) {
            bakeConfig();
        }
    }






    public static class ClientConfig {
        public final ForgeConfigSpec.ConfigValue<List<? extends Double>> configCurrencyValue;
        Predicate<Object> validator = n -> ((Double)n > 0.0 && (Double)n < 999999.999);

      //   defineList(String path, List<? extends T> defaultValue, Predicate<Object> elementValidator) {


        public ForgeConfigSpec.ConfigValue<List<? extends Double>> getConfigCurrencyValue() {
            return configCurrencyValue;
        }

        public ClientConfig (ForgeConfigSpec.Builder builder) {
            configCurrencyValue = builder
                    .comment("aBoolean usage description")
                    .translation(GOCurrency.MODID + ".config." + "aBoolean")
                    .defineList("configCurrencyValue", Arrays.asList(1.0, 2.0, 3.0), validator);

            builder.push("Category");
            builder.pop();
        }
    }
}
