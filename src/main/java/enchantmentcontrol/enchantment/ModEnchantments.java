package enchantmentcontrol.enchantment;

import enchantmentcontrol.Tags;
import enchantmentcontrol.config.ConfigHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Backports of 1.16+ Soul Speed and 1.19+ Swift Sneak, both disabled by default.
 * Stats mirror 1.21: very rare, treasure, max lvl 3. All of it can be changed via the usual jsons of this mod.
 */
public class ModEnchantments {
    public static final Enchantment SWIFT_SNEAK = new BackportedEnchantment("swift_sneak", EnumEnchantmentType.ARMOR_LEGS, EntityEquipmentSlot.LEGS, 25, 25, 50);
    public static final Enchantment SOUL_SPEED = new BackportedEnchantment("soul_speed", EnumEnchantmentType.ARMOR_FEET, EntityEquipmentSlot.FEET, 10, 10, 15);

    @SubscribeEvent
    public static void onEnchantmentRegistry(RegistryEvent.Register<Enchantment> event) {
        if (ConfigHandler.enchantments.swiftSneak.enabled) event.getRegistry().register(SWIFT_SNEAK);
        if (ConfigHandler.enchantments.soulSpeed.enabled) event.getRegistry().register(SOUL_SPEED);
    }

    private static class BackportedEnchantment extends Enchantment {
        private final int minEnch;
        private final int lvlSpan;
        private final int range;

        private BackportedEnchantment(String name, EnumEnchantmentType type, EntityEquipmentSlot slot, int minEnch, int lvlSpan, int range) {
            super(Rarity.VERY_RARE, type, new EntityEquipmentSlot[]{slot});
            this.minEnch = minEnch;
            this.lvlSpan = lvlSpan;
            this.range = range;
            this.setName(Tags.MODID + "." + name);
            this.setRegistryName(Tags.MODID, name);
        }

        @Override public int getMinEnchantability(int lvl) { return minEnch + lvlSpan * (lvl - 1); }
        @Override public int getMaxEnchantability(int lvl) { return getMinEnchantability(lvl) + range; }
        @Override public int getMaxLevel() { return 3; }
        @Override public boolean isTreasureEnchantment() { return true; }
    }
}
