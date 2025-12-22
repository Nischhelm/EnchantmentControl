package enchantmentcontrol.util;

public enum VanillaSystem {
    SWEEPING("sweepingStrength"),
    KNOCKBACK("knockbackStrength"),
    FIRE_ASPECT("fireAspectStrength"),
    RESPIRATION("respirationStrength"),
    DEPTH_STRIDER("depthStriderStrength"),
    EFFICIENCY("efficiencyStrength"),
    LUCK_OF_THE_SEA("luckOfTheSeaStrength"),
    LURE("lureStrength"),
    LOOTING("lootingStrength"),
    AQUA_AFFINITY("aquaAffinityStrength"),
    FROST_WALKER("frostWalkerStrength"),
    BINDING_CURSE("bindingCurseStrength"),
    VANISHING_CURSE("vanishingCurseStrength");

    public final String cfgName;
    VanillaSystem(String cfgName) {
        this.cfgName = cfgName;
    }

    @Override public String toString() {
        return cfgName;
    }
}