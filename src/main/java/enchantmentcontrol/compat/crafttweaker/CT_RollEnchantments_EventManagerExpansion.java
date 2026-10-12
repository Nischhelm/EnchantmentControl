package enchantmentcontrol.compat.crafttweaker;

import crafttweaker.annotations.ZenDoc;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.event.IEventHandle;
import crafttweaker.api.event.IEventManager;
import crafttweaker.util.EventList;
import crafttweaker.util.IEventHandler;
import enchantmentcontrol.event.RollEnchantmentsEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import stanhebben.zenscript.annotations.ZenExpansion;
import stanhebben.zenscript.annotations.ZenMethod;

@SuppressWarnings("unused")
@ZenExpansion("crafttweaker.events.IEventManager")
@ZenRegister
public class CT_RollEnchantments_EventManagerExpansion {
    private static final EventList<CT_RollEnchantmentsEvent.Pre> HANDLERS_PRE = new EventList<>();
    private static final EventList<CT_RollEnchantmentsEvent.Post> HANDLERS_POST = new EventList<>();

    @ZenDoc("Called before collecting the list of applicable enchantments to select from. Modify level, allowTreasure and enchantment specifics like minLvl, maxLvl and whether to skip the canApply check.")
    @ZenMethod
    public static IEventHandle onRollEnchantmentsPre(IEventManager manager, IEventHandler<CT_RollEnchantmentsEvent.Pre> handler) {
        return HANDLERS_PRE.add(handler);
    }

    @ZenDoc("Called after collecting the list of applicable enchantments to select from. Modify that list and how many to pick (minimally/maximally) from it.")
    @ZenMethod
    public static IEventHandle onRollEnchantmentsPost(IEventManager manager, IEventHandler<CT_RollEnchantmentsEvent.Post> handler) {
        return HANDLERS_POST.add(handler);
    }

    @ZenMethod
    public static void clearRollEnchantmentPreHandlers(IEventManager manager) {
        HANDLERS_PRE.clear();
    }

    @ZenMethod
    public static void clearRollEnchantmentPostHandlers(IEventManager manager) {
        HANDLERS_POST.clear();
    }

    @SubscribeEvent
    public static void onRollEnchantmentsPre(RollEnchantmentsEvent.Pre event) {
        if (HANDLERS_PRE.hasHandlers()) {
            CT_RollEnchantmentsEvent.Pre ctEvent = new CT_RollEnchantmentsEvent.Pre(event);
            HANDLERS_PRE.publish(ctEvent);
        }
    }

    @SubscribeEvent
    public static void onRollEnchantmentsPre(RollEnchantmentsEvent.Post event) {
        if (HANDLERS_POST.hasHandlers()) {
            CT_RollEnchantmentsEvent.Post ctEvent = new CT_RollEnchantmentsEvent.Post(event);
            HANDLERS_POST.publish(ctEvent);
        }
    }
}
