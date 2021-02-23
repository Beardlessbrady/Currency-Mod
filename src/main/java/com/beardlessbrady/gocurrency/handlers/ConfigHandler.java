package com.beardlessbrady.gocurrency.handlers;


import com.beardlessbrady.gocurrency.GOCurrency;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by BeardlessBrady on 2021-02-23 for Currency-Mod
 * All Rights Reserved
 * https://github.com/Beardlessbrady/Currency-Mod
 */
public class ConfigHandler {
    public static final ClientConfig CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        CLIENT_SPEC = specPair.getValue();
        CLIENT = specPair.getKey();
    }

    public static class ClientConfig {
        public final ForgeConfigSpec.DoubleValue configCurrencyValue;
        List<Double> list = new ArrayList<Double>(Arrays.asList(1.0, 2.0, 3.0, 10.0));

        Predicate<Double> validator = n -> (n > 0.0 && n < 999999.999);

        public ClientConfig (ForgeConfigSpec.Builder builder) {
            configCurrencyValue = builder
                    .comment("aBoolean usage description")
                    .translation(GOCurrency.MODID + ".config." + "aBoolean")
                    .defineList("configCurrencyValue", list, validator);
        }
    }
}
