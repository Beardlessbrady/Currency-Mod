package beard.modcurrency.item;

import net.minecraft.util.IStringSerializable;

public enum EnumCurrencyPrime implements IStringSerializable {
    BEAR,
    CHICKEN,
    CREEPER,
    FISH,
    LEAF,
    VILLAGER;

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }
}
