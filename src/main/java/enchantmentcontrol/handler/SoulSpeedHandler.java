package enchantmentcontrol.handler;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.enchantment.ModEnchantments;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Vanilla Soul Speed:
 * - while on soul speed blocks (and while jumping off them), adds 0.03 + 0.0105 * lvl movement speed
 * - removes the soul sand slowdown
 * - each block moved on soul speed blocks has a chance to damage the enchanted boots by 1
 */
public class SoulSpeedHandler {
    private static final UUID SPEED_UUID = UUID.fromString("87f46a96-686f-4796-b035-22e16ee9e038");
    private static final Map<EntityLivingBase, BlockPos> lastPositions = new WeakHashMap<>();

    private static Set<Block> soulSpeedBlocks = null;

    public static boolean isSoulSpeedBlock(Block block) {
        if (soulSpeedBlocks == null) {
            soulSpeedBlocks = Arrays.stream(ConfigHandler.enchantments.soulSpeed.blocks)
                    .map(String::trim)
                    .map(ResourceLocation::new)
                    .map(ForgeRegistries.BLOCKS::getValue)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
        }
        return soulSpeedBlocks.contains(block);
    }

    public static int getLevel(EntityLivingBase entity) {
        return EnchantmentHelper.getMaxEnchantmentLevel(ModEnchantments.SOUL_SPEED, entity);
    }

    private static boolean isOnSoulSpeedBlock(EntityLivingBase entity) {
        //1.12 soul sand is lower than a full block, so entities stand inside of it. Full-block soul blocks (modded soul soil) are checked below the feet
        BlockPos feet = new BlockPos(entity.posX, entity.posY, entity.posZ);
        if (isSoulSpeedBlock(entity.world.getBlockState(feet).getBlock())) return true;
        return isSoulSpeedBlock(entity.world.getBlockState(new BlockPos(entity.posX, entity.posY - 0.5, entity.posZ)).getBlock());
    }

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
        if (speed == null) return;
        AttributeModifier current = speed.getModifier(SPEED_UUID);

        int lvl = getLevel(entity);
        boolean canBeActive = lvl > 0
                && !entity.isRiding()
                && !entity.isElytraFlying()
                && !(entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isFlying);
        boolean onSoulBlock = canBeActive && isOnSoulSpeedBlock(entity);
        //like vanilla, stays active while in the air after jumping off soul blocks
        boolean active = onSoulBlock || (canBeActive && current != null && !entity.onGround);

        // Stop applying speed modifier
        if (!active) {
            if (current != null) speed.removeModifier(current);
            if (!entity.world.isRemote) lastPositions.remove(entity);
            return;
        }

        // Apply speed modifier
        double amount = 0.03D + 0.0105D * lvl;
        if (current == null || current.getAmount() != amount) {
            if (current != null) speed.removeModifier(current);
            speed.applyModifier(new AttributeModifier(SPEED_UUID, "Soul Speed", amount, 0).setSaved(false));
        }

        // Damage boots if moved
        if (entity.world.isRemote || !onSoulBlock || !entity.onGround) return;
        BlockPos pos = new BlockPos(entity);
        BlockPos lastPos = lastPositions.put(entity, pos);
        if (lastPos == null || lastPos.equals(pos)) return;

        if (entity.getRNG().nextFloat() >= ConfigHandler.enchantments.soulSpeed.durabilityChance) return;
        ItemStack stack = EnchantmentHelper.getEnchantedItem(ModEnchantments.SOUL_SPEED, entity);
        if (!stack.isEmpty()) stack.damageItem(1, entity);
    }
}
