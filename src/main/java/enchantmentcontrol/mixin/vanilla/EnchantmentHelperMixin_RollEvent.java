package enchantmentcontrol.mixin.vanilla;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Cancellable;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import enchantmentcontrol.event.EnchantmentOverride;
import enchantmentcontrol.event.RollEnchantmentsEvent;
import enchantmentcontrol.util.EnchantmentContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(value = EnchantmentHelper.class, priority = 2000)
public abstract class EnchantmentHelperMixin_RollEvent {

    @Unique private static final Map<Enchantment, EnchantmentOverride> ec$overrides = new HashMap<>();

    @WrapOperation(
            method = "buildEnchantmentList",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getEnchantmentDatas(ILnet/minecraft/item/ItemStack;Z)Ljava/util/List;")
    )
    private static List<EnchantmentData> ec_wrapGetEnchantmentDatas(
            int adjustedLevel, ItemStack stack, boolean allowTreasure, // wrapped method params
            Operation<List<EnchantmentData>> original,
            Random randomIn, // first target method param
            @Cancellable CallbackInfoReturnable<List<EnchantmentData>> cir,
            @Share("ec_minEnchCount") LocalRef<Integer> minEnchCount,
            @Share("ec_maxEnchCount") LocalRef<Integer> maxEnchCount
    ) {
        // Get context (potentially set by ContainerEnchantmentThreadLocalMixin)
        EnchantmentContext context = EnchantmentContext.getContextAndRemove();

        RollEnchantmentsEvent.Pre preEvent = new RollEnchantmentsEvent.Pre(context, stack, adjustedLevel, allowTreasure);
        if (MinecraftForge.EVENT_BUS.post(preEvent)) {
            cir.setReturnValue(new ArrayList<>());
            return new ArrayList<>();
        }

        ec$overrides.clear();
        ec$overrides.putAll(preEvent.getOverrides());

        // Call original getEnchantmentDatas
        List<EnchantmentData> list = original.call(preEvent.getLevel(), stack, preEvent.allowsTreasure());

        RollEnchantmentsEvent.Post postEvent = new RollEnchantmentsEvent.Post(context, stack, adjustedLevel, allowTreasure, list);
        if(MinecraftForge.EVENT_BUS.post(postEvent)){
            cir.setReturnValue(new ArrayList<>());
            return new ArrayList<>();
        }

        minEnchCount.set(postEvent.getMinEnchCount());
        maxEnchCount.set(postEvent.getMaxEnchCount());

        return list;
    }

    @WrapOperation(
            method = "getEnchantmentDatas",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMinEnchantability(I)I")
    )
    private static int ec_overrideMinLvl(Enchantment enchantment, int enchLevel, Operation<Integer> original) {
        EnchantmentOverride override = ec$overrides.get(enchantment);
        if (override != null && override.getMinLvl() != null)
            return override.getMinLvl();
        return original.call(enchantment, enchLevel);
    }

    @WrapOperation(
            method = "getEnchantmentDatas",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getMaxEnchantability(I)I")
    )
    private static int ec_overrideMaxLvl(Enchantment enchantment, int enchLevel, Operation<Integer> original) {
        EnchantmentOverride override = ec$overrides.get(enchantment);
        if (override != null && override.getMaxLvl() != null)
            return override.getMaxLvl();
        return original.call(enchantment, enchLevel);
    }

    @WrapOperation(
            method = "getEnchantmentDatas",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;canApplyAtEnchantingTable(Lnet/minecraft/item/ItemStack;)Z")
    )
    private static boolean ec_overrideCanApply(Enchantment enchantment, ItemStack stack, Operation<Boolean> original) {
        EnchantmentOverride override = ec$overrides.get(enchantment);
        if (override != null && Boolean.TRUE.equals(override.getSkipCanApply()))
            return true;
        return original.call(enchantment, stack);
    }

    // Modify while(nextInt(50) <= level) to respect potentially set min and max counts of enchantments to be picked
    @Definition(id = "nextInt", method = "Ljava/util/Random;nextInt(I)I")
    @Definition(id = "level", local = @Local(type = int.class, argsOnly = true))
    @Definition(id = "randomIn", local = @Local(type = Random.class, argsOnly = true))
    @Expression("randomIn.nextInt(50) <= level")
    @ModifyExpressionValue(method = "buildEnchantmentList", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
    private static boolean ec_pickEnchantmentMinMaxCount(
            boolean original,
            @Local(ordinal = 0) List<EnchantmentData> list,
            @Share("ec_minEnchCount") LocalRef<Integer> minEnchCount,
            @Share("ec_maxEnchCount") LocalRef<Integer> maxEnchCount
    ) {
        if (maxEnchCount.get() != null && list.size() >= maxEnchCount.get()) return false;
        if (minEnchCount.get() != null && list.size() < minEnchCount.get()) return true;
        return original;
    }
}
