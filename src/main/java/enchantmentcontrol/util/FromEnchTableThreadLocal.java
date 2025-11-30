package enchantmentcontrol.util;

public class FromEnchTableThreadLocal {
    //Used to tell EnchantmentHelperEnchantBlacklistMixin that the call to EnchantmentHelper.buildEnchantmentList comes from Enchanting table
    private static final ThreadLocal<Boolean> fromEnchTable = ThreadLocal.withInitial(() -> false);

    public static boolean getAndRemove(){
        boolean value = fromEnchTable.get();
        fromEnchTable.remove();
        return value;
    }

    public static void set(boolean value){
        fromEnchTable.set(value);
    }
}
