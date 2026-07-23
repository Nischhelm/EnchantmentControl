package enchantmentcontrol.mixin.vanilla;

import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.EarlyConfigReader;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;

import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = ForgeRegistry.class, remap = false)
public class ForgeRegistryCustomIDMixin<V extends IForgeRegistryEntry<V>> {

    @Shadow @Final private BitSet availabilityMap;

    @Unique private static boolean ec$enchantmentIdsReserved = false;
    @Unique private static final Set<Integer> ec$reservedIds = new HashSet<>();

    // modifying idToUse before if (idToUse > max) {...}
    @ModifyVariable(
            method = "add(ILnet/minecraftforge/registries/IForgeRegistryEntry;Ljava/lang/String;)I",
            at = @At(value = "LOAD"),
            slice = @Slice(
                    from = @At(value = "FIELD", target = "Lnet/minecraftforge/registries/ForgeRegistry;min:I", ordinal = 0, opcode = Opcodes.GETFIELD),
                    to = @At(value = "FIELD", target = "Lnet/minecraftforge/registries/ForgeRegistry;max:I", ordinal = 0, opcode = Opcodes.GETFIELD)
            ), // this could also just be ordinal = 2 but its more exact if anyone else thinks to add ASM in this crucial piece of code before me
            name = "idToUse",
            allow = 1
    )
    private int ec_injectCustomNumericId(int idToUse, int id, V value, String owner) {
        if (!(value instanceof Enchantment)) return idToUse; // Only enchs

        // Reserve all custom IDs on first enchantment registration
        if (!ec$enchantmentIdsReserved) {
            ec$reserveCustomIds();
            ec$enchantmentIdsReserved = true;
        }

        if (EarlyConfigReader.getCustomNumericIdsConfig().isEmpty()) return idToUse;

        ResourceLocation loc = value.getRegistryName();
        if (loc == null) return idToUse;
        String enchantmentName = loc.toString();

        Integer customId = EarlyConfigReader.getCustomNumericId(enchantmentName);
        if (customId == null) return idToUse;

        // Check if ID is actually occupied, not just reserved
        if (availabilityMap.get(customId) && !ec$reservedIds.contains(customId)) {
            EnchantmentControl.LOGGER.warn("Id {} for enchantment {} is already occupied. Now auto-assigning to {}.", customId, enchantmentName, idToUse);
            return idToUse;
        }

        EnchantmentControl.LOGGER.info("Assigning id {} to enchantment {}", customId, enchantmentName);
        availabilityMap.clear(customId); //unset so it can set again
        return customId;
    }

    @Unique
    private void ec$reserveCustomIds() {
        for (Integer customId : EarlyConfigReader.getCustomNumericIdsConfig().values()) {
            if (customId < 0) continue;

            if (!availabilityMap.get(customId)) {
                // reserve
                availabilityMap.set(customId);
                ec$reservedIds.add(customId);
            } else
                EnchantmentControl.LOGGER.warn("Cannot reserve id {} - already occupied", customId);
        }
    }
}
