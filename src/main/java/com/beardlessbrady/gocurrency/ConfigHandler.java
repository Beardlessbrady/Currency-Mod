package com.beardlessbrady.gocurrency;


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
    public static final CommonConfig COMMON;
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        COMMON_SPEC = specPair.getValue();
        COMMON = specPair.getKey();
    }

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> configCurrency;

    public static void bakeConfig() {
        configCurrency = COMMON.getConfigCurrency();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == ConfigHandler.COMMON_SPEC) {
            bakeConfig();
        }
    }

    public static class CommonConfig {
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> configCurrency;
        Predicate<Object> currencyValidator = new Predicate<Object>() {
            @Override
            public boolean test(Object o) {
                String s = ((String) o);

                if (s.contains(":")){
                    String[] values = s.split(":");

                    if (values.length == 2) {
                        if(values[1].contains(".")) {
                            String[] currency = values[1].split("[.]");
                            if (currency.length == 2){
                                currency[0] = currency[0].replaceAll("[^0-9]", "");
                                currency[1] = currency[1].replaceAll("[^0-9]", "");


                                System.out.println("HEEE");
                                System.out.println(currency[0]);

                                if(Long.parseLong(currency[0]) <= (long)Integer.MAX_VALUE) {

                                    int d = Integer.parseInt(currency[0]);
                                    int c = Integer.parseInt(currency[1]);

                                    return (d >= 0 && c <= 99 && c >= 0);
                                }
                            }
                        }
                    }
                }
                return false;
            }
        };

        public ForgeConfigSpec.ConfigValue<List<? extends String>> getConfigCurrency() {
            return configCurrency;
        }
        public CommonConfig (ForgeConfigSpec.Builder builder) {
            builder.push("Currency Configuration");

            configCurrency = builder
                    .comment("Currency - defining the names and values of each currency item in the mod. NAME:VALUE, Value must be within 0.01 to " + Integer.MAX_VALUE + ".99")
                    .translation(GOCurrency.MODID + ".config." + "configCurrency")
                    .defineList("currency", Arrays.asList("One Dollar:1.00", "Five Dollars:5.00", "Ten Dollars:10.00", "Twenty Dollars:20.00", "Fifty Dollars:50.00", "One Hundred Dollars:100.00"), currencyValidator);

            builder.pop();
        }
    }
}
