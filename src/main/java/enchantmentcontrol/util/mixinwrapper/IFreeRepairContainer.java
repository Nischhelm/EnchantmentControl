package enchantmentcontrol.util.mixinwrapper;

/**
 * Implemented by ContainerRepair if "Free Repairs" is enabled.
 * Separate interface per toggle since each toggle is its own mixin config and either can be loaded without the other.
 */
public interface IFreeRepairContainer {
    /** true if the current output only costs 0 lvls because of free renames/repairs. Vanilla wouldn't allow taking a 0 cost output */
    boolean ec$isFreeRepairOutput();
}
