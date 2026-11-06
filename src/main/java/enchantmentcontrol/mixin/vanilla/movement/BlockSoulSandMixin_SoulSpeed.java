package enchantmentcontrol.mixin.vanilla.movement;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import enchantmentcontrol.handler.SoulSpeedHandler;
import net.minecraft.block.BlockSoulSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BlockSoulSand.class)
public abstract class BlockSoulSandMixin_SoulSpeed {
    @WrapMethod(method = "onEntityCollision")
    private void ec_soulSpeedNoSlowdown(World world, BlockPos pos, IBlockState state, Entity entity, Operation<Void> original) {
        if (entity instanceof EntityLivingBase && SoulSpeedHandler.getLevel((EntityLivingBase) entity) > 0) return;
        original.call(world, pos, state, entity); //slow down normally
    }
}
