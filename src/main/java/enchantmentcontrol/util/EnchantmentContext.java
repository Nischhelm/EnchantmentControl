 package enchantmentcontrol.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EnchantmentContext {
    private static final ThreadLocal<EnchantmentContext> context = new ThreadLocal<>();

    public static EnchantmentContext fromEnchantingTable(World world, @Nullable EntityPlayer player, int slot, @Nullable BlockPos pos) {
        return new EnchantmentContext(world, player, true, slot, pos);
    }

    public static EnchantmentContext notFromEnchantingTable() {
        return new EnchantmentContext(null, null, false, -1, null);
    }

    public static EnchantmentContext notFromEnchantingTable(@Nullable World world, @Nullable EntityPlayer player) {
        return new EnchantmentContext(world, player, false, -1, null);
    }

    @Nullable
    private final World world;
    @Nullable
    private final EntityPlayer player;
    private final boolean fromEnchantingTable;
    private final int enchantingTableSlot; // 1 - 3
    private final BlockPos pos;

    private EnchantmentContext(@Nullable World world, @Nullable EntityPlayer player, boolean fromEnchantingTable, int slot, @Nullable BlockPos pos) {
        this.world = world;
        this.player = player;
        this.fromEnchantingTable = fromEnchantingTable;
        this.enchantingTableSlot = fromEnchantingTable ? slot : -1;
        this.pos = pos;
    }

    /**
     * Gets the current context and removes it from the thread local.
     * @return The context, or null if not set
     */
    @Nullable
    public static EnchantmentContext getContextAndRemove(){
        EnchantmentContext ctx = context.get();
        context.remove();
        return ctx;
    }

    /**
     * Sets the enchantment context for the current thread.
     */
    public static void setContext(EnchantmentContext ctx){
        context.set(ctx);
    }

    @Nullable
    public World getWorld() {
        return this.world;
    }

    @Nullable
    public EntityPlayer getPlayer() {
        return this.player;
    }

    public boolean isFromEnchantingTable() {
        return this.fromEnchantingTable;
    }

    public int getEnchantingTableSlot() {
        return this.enchantingTableSlot;
    }

    public BlockPos getPosition() {
        return this.pos;
    }
}
