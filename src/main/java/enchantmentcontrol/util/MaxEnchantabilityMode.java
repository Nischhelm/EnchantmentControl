package enchantmentcontrol.util;

public enum MaxEnchantabilityMode {
    NORMAL {
        @Override
        public int getMaxEnch(int enchLvl, int min, int range) {
            return min + range;
        }
    },
    SUPER {
        @Override
        public int getMaxEnch(int enchLvl, int min, int range) {
            return 1 + enchLvl * 10 + range;
        }
    },
    CONST {
        @Override
        public int getMaxEnch(int enchLvl, int min, int range) {
            return range;
        }
    };

    public abstract int getMaxEnch(int enchLvl, int min, int range);
}
