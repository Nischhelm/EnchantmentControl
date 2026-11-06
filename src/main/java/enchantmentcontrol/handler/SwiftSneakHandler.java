package enchantmentcontrol.handler;

import enchantmentcontrol.enchantment.ModEnchantments;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.MovementInput;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Swift Sneak: each level adds 15% of walking speed to sneaking speed (vanilla 30%), capped at 100%.
 */
public class SwiftSneakHandler {
    private static final float VANILLA_SNEAK_FACTOR = 0.3F;

    @SubscribeEvent
    public static void onInputUpdate(InputUpdateEvent event) {
        MovementInput input = event.getMovementInput();
        if (!input.sneak) return;

        int lvl = EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.SWIFT_SNEAK, event.getEntityPlayer());
        if (lvl <= 0) return;

        float sneakFactor = Math.min(1F, VANILLA_SNEAK_FACTOR + 0.15F * lvl);
        float scale = sneakFactor / VANILLA_SNEAK_FACTOR;
        input.moveStrafe *= scale;
        input.moveForward *= scale;
    }
}
