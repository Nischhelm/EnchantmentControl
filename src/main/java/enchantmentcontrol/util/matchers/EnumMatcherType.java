package enchantmentcontrol.util.matchers;

public enum EnumMatcherType {
    EXACT, //String List contains ResourceLocation::toString
    MODID, //String List contains ResourceLocation::getNameSpace (modid)
    CLASS, //Any of the classes is in inheritance chain of Object
    REGEX //Any of the regexes matches ResourceLocation::toString
}
