package enchantmentcontrol.compat.somanyenchantments;

import com.shultrea.rin.config.ModConfig;
import com.shultrea.rin.enchantments.base.EnchantmentBase;
import com.shultrea.rin.registry.EnchantmentRegistry;
import enchantmentcontrol.EnchantmentControl;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.config.EarlyConfigReader;
import enchantmentcontrol.config.provider.ItemTypeConfigProvider;
import enchantmentcontrol.util.enchantmenttypes.CustomTypeMatcher;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class NewSMECompat {
    private static Configuration smeConfig = null;
    private static final Map<Enchantment, String[]> enchT = new HashMap<>();
    private static final Map<Enchantment, String[]> anvil = new HashMap<>();
    private static final Map<Enchantment, String> smeEnchNames = new HashMap<Enchantment, String>(){{ //Rip that this is needed but welp that happens when you dont use maps in configs
        put(EnchantmentRegistry.adept, "Adept");
        put(EnchantmentRegistry.ancientSealedCurses, "Ancient Sealed Curses");
        put(EnchantmentRegistry.ancientSwordMastery, "Ancient Sword Mastery");
        put(EnchantmentRegistry.arcSlash, "Arc Slash");
        put(EnchantmentRegistry.ashDestroyer, "Ash Destroyer");
        put(EnchantmentRegistry.atomicDeconstructor, "Atomic Deconstructor");
        put(EnchantmentRegistry.blessedEdge, "Blessed Edge");
        put(EnchantmentRegistry.brutality, "Brutality");
        put(EnchantmentRegistry.burningShield, "Burning Shield");
        put(EnchantmentRegistry.burningThorns, "Burning Thorns");
        put(EnchantmentRegistry.butchering, "Butchering");
        put(EnchantmentRegistry.clearskiesFavor, "Clearskies' Favor");
        put(EnchantmentRegistry.combatMedic, "Combat Medic");
        put(EnchantmentRegistry.counterAttack, "Counter Attack");
        put(EnchantmentRegistry.criticalStrike, "Critical Strike");
        put(EnchantmentRegistry.cryogenic, "Cryogenic");
        put(EnchantmentRegistry.culling, "Culling");
        put(EnchantmentRegistry.darkShadows, "Dark Shadows");
        put(EnchantmentRegistry.defusingEdge, "Defusing Edge");
        put(EnchantmentRegistry.desolator, "Desolator");
        put(EnchantmentRegistry.difficultysEndowment, "Difficulty's Endowment");
        put(EnchantmentRegistry.disarmament, "Disarmament");
        put(EnchantmentRegistry.disorientatingBlade, "Disorientating Blade");
        put(EnchantmentRegistry.empoweredDefence, "Empowered Defence");
        put(EnchantmentRegistry.envenomed, "Envenomed");
        put(EnchantmentRegistry.evasion, "Evasion");
        put(EnchantmentRegistry.fieryEdge, "Fiery Edge");
        put(EnchantmentRegistry.flinging, "Flinging");
        put(EnchantmentRegistry.horsDeCombat, "Hors De Combat");
        put(EnchantmentRegistry.inhumane, "Inhumane");
        put(EnchantmentRegistry.innerBerserk, "Inner Berserk");
        put(EnchantmentRegistry.jaggedRake, "Jagged Rake");
        put(EnchantmentRegistry.levitator, "Levitator");
        put(EnchantmentRegistry.lifesteal, "Lifesteal");
        put(EnchantmentRegistry.lightWeight, "Light Weight");
        put(EnchantmentRegistry.luckMagnification, "Luck Magnification");
        put(EnchantmentRegistry.lunasBlessing, "Lunas Blessing");
        put(EnchantmentRegistry.magicProtection, "Magic Protection");
        put(EnchantmentRegistry.magmaWalker, "Magma Walker");
        put(EnchantmentRegistry.moisturized, "Moisturized");
        put(EnchantmentRegistry.mortalitas, "Mortalitas");
        put(EnchantmentRegistry.naturalBlocking, "Natural Blocking");
        put(EnchantmentRegistry.parry, "Parry");
        put(EnchantmentRegistry.penetratingEdge, "Penetrating Edge");
        put(EnchantmentRegistry.physicalProtection, "Physical Protection");
        put(EnchantmentRegistry.purgingBlade, "Purging Blade");
        put(EnchantmentRegistry.purification, "Purification");
        put(EnchantmentRegistry.pushing, "Pushing");
        put(EnchantmentRegistry.rainsBestowment, "Rain's Bestowment");
        put(EnchantmentRegistry.reviledBlade, "Reviled Blade");
        put(EnchantmentRegistry.reinforcedsharpness, "Reinforced Sharpness");
        put(EnchantmentRegistry.smelter, "Smelter");
        put(EnchantmentRegistry.solsBlessing, "Sol's Blessing");
        put(EnchantmentRegistry.spellBreaker, "Spell Breaker");
        put(EnchantmentRegistry.splitShot, "Splitshot");
        put(EnchantmentRegistry.strafe, "Strafe");
        put(EnchantmentRegistry.strengthenedVitality, "Strengthened Vitality");
        put(EnchantmentRegistry.swifterSlashes, "Swifter Slashes");
        put(EnchantmentRegistry.thunderstormsBestowment, "Thunderstorm's Bestowment");
        put(EnchantmentRegistry.trueStrike, "True Strike");
        put(EnchantmentRegistry.swiftSwimming, "Swift Swimming");
        put(EnchantmentRegistry.unreasonable, "Unreasonable");
        put(EnchantmentRegistry.unsheathing, "Unsheathing");
        put(EnchantmentRegistry.upgradedPotentials, "Upgraded Potentials");
        put(EnchantmentRegistry.viper, "Viper");
        put(EnchantmentRegistry.waterAspect, "WaterAspect");
        put(EnchantmentRegistry.plowing, "Plowing");
        put(EnchantmentRegistry.wintersGrace, "Winter's Grace");
        put(EnchantmentRegistry.ascetic, "Ascetic");
        put(EnchantmentRegistry.bluntness, "Bluntness");
        put(EnchantmentRegistry.breachedPlating, "Breached Plating");
        put(EnchantmentRegistry.cursedEdge, "Cursed Edge");
        put(EnchantmentRegistry.curseOfDecay, "Curse of Decay");
        put(EnchantmentRegistry.curseOfHolding, "Curse of Holding");
        put(EnchantmentRegistry.curseOfInaccuracy, "Curse of Inaccuracy");
        put(EnchantmentRegistry.curseOfPossession, "Curse of Possession");
        put(EnchantmentRegistry.curseOfVulnerability, "Curse of Vulnerability");
        put(EnchantmentRegistry.dragging, "Dragging");
        put(EnchantmentRegistry.extinguish, "Extinguish");
        put(EnchantmentRegistry.heavyWeight, "Heavy Weight");
        put(EnchantmentRegistry.inefficient, "Inefficient");
        put(EnchantmentRegistry.instability, "Instability");
        put(EnchantmentRegistry.meltdown, "Meltdown");
        put(EnchantmentRegistry.pandorasCurse, "Pandora's Curse");
        put(EnchantmentRegistry.powerless, "Powerless");
        put(EnchantmentRegistry.rusted, "Rusted");
        put(EnchantmentRegistry.unpredictable, "Unpredictable");
        put(EnchantmentRegistry.runeArrowPiercing, "Rune: Arrow Piercing");
        put(EnchantmentRegistry.runeMagicalBlessing, "Rune: Magical Blessing");
        put(EnchantmentRegistry.runePiercingCapabilities, "Rune: Piercing Capabilities");
        put(EnchantmentRegistry.runeResurrection, "Rune: Resurrection");
        put(EnchantmentRegistry.runeRevival, "Rune: Revival");
        put(EnchantmentRegistry.subjectBiology, "Subject Biology");
        put(EnchantmentRegistry.subjectChemistry, "Subject Chemistry");
        put(EnchantmentRegistry.subjectEnglish, "Subject English");
        put(EnchantmentRegistry.subjectHistory, "Subject History");
        put(EnchantmentRegistry.subjectMathematics, "Subject Mathematics");
        put(EnchantmentRegistry.subjectPE, "Subject P.E.");
        put(EnchantmentRegistry.subjectPhysics, "Subject Physics");
        put(EnchantmentRegistry.subjectGeography, "Subject Geography");
        put(EnchantmentRegistry.lesserBaneOfArthropods, "Lesser Bane Of Arthropods");
        put(EnchantmentRegistry.lesserFireAspect, "Lesser Fire Aspect");
        put(EnchantmentRegistry.lesserFlame, "Lesser Flame");
        put(EnchantmentRegistry.lesserSharpness, "Lesser Sharpness");
        put(EnchantmentRegistry.lesserSmite, "Lesser Smite");
        put(EnchantmentRegistry.advancedBaneOfArthropods, "Advanced Bane Of Arthropods");
        put(EnchantmentRegistry.advancedBlastProtection, "Advanced Blast Protection");
        put(EnchantmentRegistry.advancedEfficiency, "Advanced Efficiency");
        put(EnchantmentRegistry.advancedFeatherFalling, "Advanced Feather Falling");
        put(EnchantmentRegistry.advancedFireAspect, "Advanced Fire Aspect");
        put(EnchantmentRegistry.advancedFireProtection, "Advanced Fire Protection");
        put(EnchantmentRegistry.advancedFlame, "Advanced Flame");
        put(EnchantmentRegistry.advancedKnockback, "Advanced Knockback");
        put(EnchantmentRegistry.advancedLooting, "Advanced Looting");
        put(EnchantmentRegistry.advancedLuckOfTheSea, "Advanced Luck Of The Sea");
        put(EnchantmentRegistry.advancedLure, "Advanced Lure");
        put(EnchantmentRegistry.advancedMending, "Advanced Mending");
        put(EnchantmentRegistry.advancedPower, "Advanced Power");
        put(EnchantmentRegistry.advancedProjectileProtection, "Advanced ProjectileProtection");
        put(EnchantmentRegistry.advancedProtection, "Advanced Protection");
        put(EnchantmentRegistry.advancedPunch, "Advanced Punch");
        put(EnchantmentRegistry.advancedSharpness, "Advanced Sharpness");
        put(EnchantmentRegistry.advancedSmite, "Advanced Smite");
        put(EnchantmentRegistry.advancedThorns, "Advanced Thorns");
        put(EnchantmentRegistry.supremeBaneOfArthropods, "Supreme Bane Of Arthropods");
        put(EnchantmentRegistry.supremeFireAspect, "Supreme Fire Aspect");
        put(EnchantmentRegistry.supremeFlame, "Supreme Flame");
        put(EnchantmentRegistry.supremeProtection, "Supreme Protection");
        put(EnchantmentRegistry.supremeSharpness, "Supreme Sharpness");
        put(EnchantmentRegistry.supremeSmite, "Supreme Smite");
    }};

    private static String[] getNewSMETypes(Enchantment ench, boolean forAnvil) {
        if(smeConfig == null){
            try {
                Field field = ConfigManager.class.getDeclaredField("CONFIGS");
                field.setAccessible(true);
                smeConfig = ((Map<String, Configuration>) field.get(null)).get(Loader.instance().getConfigDir().getAbsolutePath() + "/somanyenchantments.cfg");
            } catch (Exception e) {
                smeConfig = new Configuration(new File(Loader.instance().getConfigDir(), "somanyenchantments.cfg"));
            }
        }

        String enchName = smeEnchNames.get(ench);
        if(enchName == null){
            EnchantmentControl.LOGGER.warn("Didn't find new SME enchantment {} {}, skipping inferral", ench.getClass(), ench.getRegistryName()!=null ? ench.getRegistryName().toString() : "not registered");
        }

        if(!forAnvil)
            return enchT.computeIfAbsent(ench, k -> smeConfig.get("general.can apply on enchantment table and anvil", enchName, new String[0]).getStringList());
        else
            return anvil.computeIfAbsent(ench, k -> smeConfig.get("general.general.can apply additionally on anvil", enchName, new String[0]).getStringList());
    }

    public static void addNewSMETypes(Map<String, Set<Enchantment>> byName, Map<Enchantment, Set<String>> byEnchantment, Map<String, Set<Enchantment>> byNameAnvil, Map<Enchantment, Set<String>> byEnchantmentAnvil) {
        Set<EnchantmentBase> smeEnchants;
        try {
            Field field = EnchantmentRegistry.class.getDeclaredField("enchantmentSet");
            field.setAccessible(true);
            smeEnchants = (Set<EnchantmentBase>) field.get(null);
        } catch (Exception ignored) {
            return;
        }
        for (EnchantmentBase ench : smeEnchants) {
            if(!ench.isEnabled()) continue;
            if(ench.getRegistryName() == null && EarlyConfigReader.getRegistrationBlacklist().contains(ench.getRegistryName().toString())) continue;
            //Direct copy, not being extra intelligent
            for(String smeType : getNewSMETypes(ench, false)){
                byName.computeIfAbsent(smeType, k -> new LinkedHashSet<>()).add(ench);
                byEnchantment.computeIfAbsent(ench, k -> new HashSet<>()).add(smeType);
            }
            for(String smeType : getNewSMETypes(ench, true)){
                byNameAnvil.computeIfAbsent(smeType, k -> new LinkedHashSet<>()).add(ench);
                byEnchantmentAnvil.computeIfAbsent(ench, k -> new HashSet<>()).add(smeType);
            }
        }
    }

    public static void addNewSMECustomTypes(){
        List<String> existingCustomTypeCfgs = Arrays.stream(ConfigHandler.itemTypes.customTypes).collect(Collectors.toList());
        Set<String> existingCustomTypeNames = existingCustomTypeCfgs.stream().map(cfg -> cfg.split(";")[0].trim()).collect(Collectors.toSet());
        String[] smeCustomTypes = ModConfig.canApply.customTypes;
        if(smeCustomTypes.length == 0) return;

        boolean cfgChanged = false;
        for(String customType : smeCustomTypes){
            String[] split = customType.split(";");
            String name, regex;
            if(split.length==2) {
                name = split[0].trim();
                regex = split[1].trim();
            } else {
                name = "";
                regex = "";
            }
            //Add matcher internally
            ItemTypeConfigProvider.registerCustomTypeMatcher(new CustomTypeMatcher(name, regex));

            //Add matcher for config
            if(!existingCustomTypeNames.contains(name)) {
                cfgChanged = true;
                existingCustomTypeCfgs.add(name + EnchantmentControl.SEP + " regex" + EnchantmentControl.SEP + " " + regex);
            }
        }
        if(!cfgChanged) return; //Nothing added, no need to write cfg
        String[] enchCtrlCustomTypes = existingCustomTypeCfgs.toArray(new String[0]);

        //Write custom types config
        EnchantmentControl.CONFIG.get("general.item types","Custom Item Types", new String[0]).set(enchCtrlCustomTypes);
        ConfigHandler.itemTypes.customTypes = enchCtrlCustomTypes;
        EnchantmentControl.configNeedsSaving = true;
    }
}
