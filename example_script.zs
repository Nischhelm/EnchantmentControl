#loader contenttweaker

import mods.contenttweaker.enchantments.EnchantmentBuilder;
import crafttweaker.item.IItemStack;
import crafttweaker.enchantments.IEnchantmentDefinition;
import crafttweaker.damage.IDamageSource;
import crafttweaker.entity.IEntityLivingBase;
import crafttweaker.entity.IEntity;
import crafttweaker.entity.IEntityEquipmentSlot;
import crafttweaker.player.IPlayer;
import mods.enchantmentcontrol.EnchantmentHelper;

val builder as EnchantmentBuilder = EnchantmentBuilder.create("myenchant");

//builder.name = "myenchant"; //Already set through create(), changing it later is probably a bad idea. for remapping, check enchantmentcontrol config
builder.domain = "enchantmentcontrol"; //default "contenttweaker"

//builder.minLevel = 1; //usually not needed, 1 by default
builder.maxLevel = 5;

builder.curse = false;
builder.treasure = false;
//builder.allowedOnBooks = true; //usually not needed, true by default
//builder.doublePrice = false;  //usually not needed, copies from isTreasure if not set

builder.displayColor = "LIGHT_PURPLE"; // see https://minecraft.wiki/w/Formatting_codes#Color_codes

builder.setEnchantabilityCalc(5, 10, 50); //(minEnch, lvlSpan, range);
//builder.setEnchantabilityCalc(5, 10, 50, "CONST"); //(minEnch, lvlSpan, range, mode); with modes NORMAL, SUPER, CONST, not recommended

//these slots are searched in for vanilla system overrides, only keep fitting. empty by default
builder.applicableSlots = [
    IEntityEquipmentSlot.mainHand(),
    IEntityEquipmentSlot.offhand(),
    IEntityEquipmentSlot.head(),
    IEntityEquipmentSlot.chest(),
    IEntityEquipmentSlot.legs(),
    IEntityEquipmentSlot.feet()
] as IEntityEquipmentSlot[];

//Possible but not recommended: setRarityCommon() setRarityRare() setRarityUncommon() setRarityVeryRare()
//instead use
builder.rarity = "LEGENDARY";

//Possible but not recommended: setTypeAll() setTypeArmor() setTypeBow() setTypeBreakable() setTypeChest() setTypeDigger() setTypeFeed() setTypeFishingRod() setTypeHead() setTypeLegs() setTypeWeapon() setTypeWearable()
//instead use
builder.type = "ARMOR_FEET";
//and/or if using multiple types or custom anvil behavior, can also do this via config list so its all in one place
builder.setTypes(["ARMOR_FEET", "SWORD"] as string[]);
builder.setTypesForAnvil(["AXE"] as string[]);

//Not recommended, better to do that via the Incompatible Groups config list to have everything in one place
builder.setIncompatibleTo(["minecraft:mending", "minecraft:unbreaking"] as string[]);

//Sharpness/Smite/BoA behavior
//Only queried for enchants on the mainhand held item when hurting an entity. custom creatureattributes can be used
builder.calcDamageByCreature = function (ench as IEnchantmentDefinition, lvl as int, creature as string) as float {
    return creature == "ANIMAL" ? 2.5F * lvl : 0.0F; // return how much dmg to add
};

//Reduce dmg for sources (Protection behavior). Each point returned is 4% dmg reduction (basic prot returns lvl, so up to 4 points = 16% DR). max useful return value is 20 = 80% DR
//Only queried for enchants on items on worn armor pieces
builder.calcModifierDamage = function (ench as IEnchantmentDefinition, lvl as int, source as IDamageSource) as int {
    return source.isMagicDamage() ? 1 : 0; // return how many points (4% DR) to reduce the dmg by
};

//What to do when being hurt by an enemy
//This happens very late in the dmg calc after the attacked entity already was hurt
//happens right before onEntityDamaged for equipment on the defending entity
//Vanilla uses this to apply thorns
builder.onUserHurt = function (ench as IEnchantmentDefinition, user as IEntityLivingBase, attacker as IEntity, lvl as int) {
    //do smth with the user or its attacker
    if(user instanceof IPlayer) user.sendMessage("Applying Thorns behavior!");
    if(attacker instanceof IPlayer) attacker.sendMessage("Getting Thorns behavior applied!");

    //Sample code translated from Thorns
    var rngSuccess = false;
    //dmg entity
    if (lvl > 0 && user.world.random.nextFloat() < 0.15F * lvl){
        rngSuccess = true;
        if (!isNull(attacker))
            attacker.attackEntityFrom(IDamageSource.createThornsDamage(user), lvl > 10 ? lvl - 10 : 1 + user.world.random.nextInt(4));
    }

    val stack as IItemStack = EnchantmentHelper.getRandomEnchantedItem(<enchantment:minecraft:thorns>, user);
    if (!isNull(stack) && !stack.isEmpty) stack.damageItem(rngSuccess ? 3 : 1, user); //dura dmg
};

//What to do after hurting an enemy
//Queried for all equipped gear! Mainhand of players twice for some reason
//happens right after onUserHurt for equipment on the attacking entity
//Vanilla uses this to apply slowness to arthropods when using BoA.
builder.onEntityDamaged = function (ench as IEnchantmentDefinition, user as IEntityLivingBase, target as IEntity, lvl as int) {
    //do smth with the user or its target
    if(user instanceof IPlayer) user.sendMessage("Applying BoA behavior!");
    if(target instanceof IPlayer) target.sendMessage("Getting BoA behavior applied!");

    //Sample code translated from Bane of Arthropods
    if (target instanceof IEntityLivingBase){
        val targetbase as IEntityLivingBase = target;
        if (targetbase.creatureAttribute == "ARTHROPOD")
            targetbase.addPotionEffect(<potion:minecraft:slowness>.makePotionEffect(20 + target.world.random.nextInt(10 * lvl), 3));
    }
};

//Vanilla System Override
/*
        Available systems:
    SWEEPING: keep low, a return value of 1.00 means 100% dmg on bystanding mobs. sweeping edge gives I: 50%, II: 67%, III: 75%
    KNOCKBACK:       one lvl = one lvl of the original enchant
    FIRE_ASPECT:     same (1 point = 4s fire, shorter windows not possible)
    RESPIRATION:     same
    DEPTH_STRIDER:   same
    EFFICIENCY:      same
    LUCK_OF_THE_SEA: same
    LURE:            same
    LOOTING:         same
    AQUA_AFFINITY   no scaling. value>=1 means does have the ability, <1 means doesn't have the ability
    FROST_WALKER    same (only magma dmg ignore part, not freezing ice)
    BINDING_CURSE   same
    VANISHING_CURSE same
*/
builder.setVanillaOverride("EFFICIENCY", 0.5F); // return currentSum + 0.5F * enchLvl
builder.setVanillaOverride("RESPIRATION", function(currentSum as float, enchLvl as int) as float {
    return currentSum + 0.5F * enchLvl; //how many system points per lvl of the enchant to give, modifying the current point sum, so returning 0 would cancel other enchants input
});

// ---- Not recommended but possible ----
//prob use displayColor instead
/*builder.calcTranslatedName = function(ench as IEnchantmentDefinition, lvl as int) as string{
    return "";
};*/

//use setEnchantabilityCalc instead
/*builder.calcEnchantabilityMax = function (ench as IEnchantmentDefinition, lvl as int) as int{
    return 0;
} as CalcEnchantability;
builder.calcEnchantabilityMin = function (ench as IEnchantmentDefinition, lvl as int) as int{
    return 0;
};*/

//use item type (general+anvil) config lists instead
/*builder.canApply = function (ench as IEnchantmentDefinition, stack as IItemStack) as bool{
    return false;
} as CalcCanApply;
builder.canApplyAtEnchantmentTable = function (ench as IEnchantmentDefinition, stack as IItemStack) as bool{
    return false;
};*/

//use incompatible groups config lists instead
/*builder.canApplyTogether = function (enchThis as IEnchantmentDefinition, enchOther as IEnchantmentDefinition) as bool{
    return false;
};*/

builder.register();
