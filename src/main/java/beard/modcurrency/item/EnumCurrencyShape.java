package beard.modcurrency.item;

import net.minecraft.util.IStringSerializable;

public enum EnumCurrencyShape implements IStringSerializable {
    CIRCLE,
    CIRCLE_BIG,
    CIRCLE_HOLE,
    SQUARE,
    SQUARE_HOLE;

    @Override
    public String getName() {
        return this.name().toLowerCase();
    }
}
