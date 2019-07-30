package beardlessbrady.modcurrency.block.tradein;

import beardlessbrady.modcurrency.block.EconomyBlockBase;
import beardlessbrady.modcurrency.block.TileEconomyBase;

/**
 * This class was created by BeardlessBrady. It is distributed as
 * part of The Currency-Mod. Source Code located on github:
 * https://github.com/BeardlessBrady/Currency-Mod
 * -
 * Copyright (C) All Rights Reserved
 * File Created 2019-07-09
 */

public class BlockTradein extends EconomyBlockBase {

    public BlockTradein(String name, Class<? extends TileEconomyBase> tileClass) {
        super("blocktradein", TileTradein.class);
    }


}
