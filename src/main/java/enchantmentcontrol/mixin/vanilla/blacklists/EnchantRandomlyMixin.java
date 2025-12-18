package enchantmentcontrol.mixin.vanilla.blacklists;

import com.llamalad7.mixinextras.sugar.Local;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.provider.BlacklistConfigProvider;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.EnchantRandomly;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Mixin(EnchantRandomly.class)
public abstract class EnchantRandomlyMixin {
    @Shadow @Final private List<Enchantment> enchantments;

    @Inject(
            method = "<init>",
            at = @At(value = "TAIL")
    )
    private void soManyEnchantments_vanillaEnchantRandomly_init(LootCondition[] conditionsIn, List<Enchantment> enchantmentsIn, CallbackInfo ci){
        //This is for predefined enchantments in the loot function
        List<Enchantment> filteredEnchants = enchantments.stream()
                .filter(e -> BlacklistConfigProvider.getRandomEnchantsBlacklist().contains(e) == ConfigHandler.blacklists.blacklistedRandomEnchantsIsWhitelist)
                .collect(Collectors.toList());
        this.enchantments.clear();
        this.enchantments.addAll(filteredEnchants);
    }

    @Inject(
            method = "apply",
            at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 1)
    )
    private void soManyEnchantments_vanillaEnchantRandomly_apply(ItemStack stack, Random rand, LootContext context, CallbackInfoReturnable<ItemStack> cir, @Local List<Enchantment> enchantments) {
        //This is for the full list from the forge registry
        List<Enchantment> filteredEnchants = enchantments.stream()
                .filter(e -> BlacklistConfigProvider.getRandomEnchantsBlacklist().contains(e) == ConfigHandler.blacklists.blacklistedRandomEnchantsIsWhitelist)
                .collect(Collectors.toList());
        enchantments.clear();
        enchantments.addAll(filteredEnchants);
    }
}