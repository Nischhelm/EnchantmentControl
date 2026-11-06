package enchantmentcontrol.config.folders;

import enchantmentcontrol.Tags;
import fermiumbooter.annotations.MixinConfig;
import meldexun.betterconfig.api.Order;
import net.minecraftforge.common.config.Config;

public class EnchantmentsConfig {
    @Config.Comment("Backport of 1.16+ Soul Speed for boots")
    @Config.Name("Soul Speed")
    @Order(0)
    public SoulSpeedConfig soulSpeed = new SoulSpeedConfig();

    @Config.Comment("Backport of 1.19+ Swift Sneak for leggings")
    @Config.Name("Swift Sneak")
    @Order(1)
    public SwiftSneakConfig swiftSneak = new SwiftSneakConfig();

    @MixinConfig(name = Tags.MODID)
    public static class SoulSpeedConfig {
        @Config.Comment({
                "Registers enchantmentcontrol:soul_speed. While on soul speed blocks, removes the soul sand slowdown",
                "  and adds 0.03 + 0.0105 per level movement speed (the player's base is 0.1, so lvl 1 = +40.5%). Each block moved has a chance to damage the boots.",
                "Like in vanilla it's a very rare treasure enchant with max lvl 3.",
                "Note: by default it still appears in librarian trades and other enchantment pools, use the blacklists if you don't want that."
        })
        @Config.Name("(MixinToggle) Soul Speed Enabled")
        @Config.RequiresMcRestart
        @MixinConfig.MixinToggle(mixinJson = "mixins.enchantmentcontrol.vanilla.soulspeed.json", defaultValue = false)
        @Order(0)
        public boolean enabled = false;

        @Config.Comment({
                "Blocks that count as soul speed blocks.",
                "Note: only the soul sand slowdown is automatically removed, other blocks keep whatever slowdown they have."
        })
        @Config.Name("Soul Speed Blocks")
        @Config.RequiresMcRestart
        @Order(1)
        public String[] blocks = {"minecraft:soul_sand"};

        @Config.Comment("Chance per block moved on soul speed blocks to damage the boots by 1 durability. Vanilla: 0.04=4%")
        @Config.Name("Durability Chance")
        @Config.RangeDouble(min = 0, max = 1)
        @Config.SlidingOption
        @Order(2)
        public float durabilityChance = 0.04F;
    }

    public static class SwiftSneakConfig {
        @Config.Comment({
                "Registers enchantmentcontrol:swift_sneak. Each level adds 15% of walking speed to sneaking speed (vanilla 30%), capped at 100%.",
                "Like in vanilla it's a very rare treasure enchant with max lvl 3.",
                "Note: by default it still appears in librarian trades and other enchantment pools, use the blacklists if you don't want that."
        })
        @Config.Name("Swift Sneak Enabled")
        @Config.RequiresMcRestart
        public boolean enabled = false;
    }
}
