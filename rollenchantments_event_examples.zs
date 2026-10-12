// ============================================================
// RollEnchantments Event Examples - Testing All Features
// This script demonstrates all CraftTweaker integration features
// for the RollEnchantmentsEvent.Pre and RollEnchantmentsEvent.Post
//
// written by Claude Code, reviewed & tested by Nischhelm
// ============================================================

import mods.enchantmentcontrol.RollEnchantmentsEvent_Pre;
import mods.enchantmentcontrol.RollEnchantmentsEvent_Post;
import mods.enchantmentcontrol.EnchantmentOverride;

import crafttweaker.enchantments.IEnchantment;


// ============================================================
// PRE EVENT EXAMPLES - Modify enchanting parameters before rolling
// ============================================================

// Example 1: Log all context information
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    print("=== Pre Event Debug Info ===");
    print("Level: " + event.level);
    print("Allow Treasure: " + event.allowTreasure);
    print("From Enchanting Table: " + event.fromEnchantingTable);
    print("Enchanting Table Slot: " + event.enchantingTableSlot);
    print("Item: " + event.itemStack.displayName);

    if (!isNull(event.player)) {
        print("Player: " + event.player.name);
    }

    if (!isNull(event.position)) {
        print("Position: " + event.position.x + ", " + event.position.y + ", " + event.position.z);
    }
});


// Example 2: Increase level for diamond items from enchanting table
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    // Only modify enchanting table enchantments
    if (event.fromEnchantingTable) {
        val item = event.itemStack;

        // Boost level for diamond items
        if (item.definition.id has "diamond") {
            print("Boosting enchanting level for diamond item from " + event.level + " to " + (event.level + 10));
            event.level = event.level + 10;
        }
    }
});


// Example 3: Always allow treasure enchantments for specific items
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    val item = event.itemStack;

    // Allow treasure enchantments on golden items
    if (item.definition.id has "gold") {
        print("Enabling treasure enchantments for golden item");
        event.allowTreasure = true;
    }
});


// Example 4: Per-enchantment overrides - Force Sharpness levels
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    if (event.fromEnchantingTable && event.enchantingTableSlot == 3 && event.itemStack.definition.id == "minecraft:diamond_sword") {
        // Get or create override for Sharpness
        val sharpnessOverride = event.getOverride(<enchantment:minecraft:sharpness>);

        // Force Sharpness to always be at least level 3 and at most level 5
        sharpnessOverride.minLvl = 3;
        sharpnessOverride.maxLvl = 5;

        print("Set Sharpness override: minLvl=3, maxLvl=5 for diamond sword on slot 3 of enchanting table");
    }
});


// Example 5: Skip canApply check to allow "incompatible" enchantments
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    // Allow Silk Touch on shears by skipping the applicability check
    if (event.itemStack.definition.id == "minecraft:shears") {
        val silkTouchOverride = event.getOverride(<enchantment:minecraft:silk_touch>);
        silkTouchOverride.skipCanApply = true;

        print("Allowing Silk Touch on shears by skipping canApply check");
    }
});


// Example 6: Use chained methods for EnchantmentOverride
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    if (event.itemStack.definition.id == "minecraft:bow") {
        // Chain multiple override settings
        event.getOverride(<enchantment:minecraft:power>)
            .minLvl(2)
            .maxLvl(4);

        event.getOverride(<enchantment:minecraft:flame>)
            .minLvl(1)
            .maxLvl(1)
            .skipCanApply(true);

        print("Set chained overrides for bow enchantments");
    }
});


// Example 7: Remove an override another event handler might have added
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    if (event.itemStack.definition.id == "minecraft:fishing_rod") {
        if (event.enchantingTableSlot == 1) {
            event.removeOverride(<enchantment:minecraft:luck_of_the_sea>);
            print("Removed Luck of the Sea override for slot 1");
        }
    }
});


// Example 8: Cancel event to prevent any enchantments
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    // Prevent enchanting wooden tools
    if (event.itemStack.definition.id has "wooden") {
        print("Canceling enchantment for wooden item");
        event.canceled = true;
    }
});


// ============================================================
// POST EVENT EXAMPLES - Modify the rolled enchantment list
// ============================================================

// Example 9: Log all rolled enchantments
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    print("=== Post Event - Rolled Enchantments ===");

    for enchDef in event.enchantments {
        print("  - " + enchDef.name);
    }

    print("Total enchantments: " + event.enchantments.length);
});


// Example 10: Remove specific enchantments from the list
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Remove Curse of Vanishing from enchanting table results
    if (event.fromEnchantingTable) {
        event.removeEnchantment(<enchantment:minecraft:vanishing_curse>);
        print("Removed Curse of Vanishing from enchanting table results");
    }
});


// Example 11: Add custom enchantments to the list
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Add Mending II to all book enchantments
    if (event.itemStack.definition.id == "minecraft:book") {
        event.addEnchantment(<enchantment:minecraft:mending>.makeEnchantment(2));
        print("Added Mending II to book enchantments");
    }
});


// Example 12: Force minimum number of enchantments
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Ensure at least 2 enchantments for slot 3 (highest level)
    if (event.fromEnchantingTable && event.enchantingTableSlot == 3) {
        event.minEnchCount = 2;
        print("Set minimum enchantment count to 2 for slot 3");
    }
});


// Example 13: Limit maximum number of enchantments
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Limit iron tools to max 2 enchantments
    if (event.itemStack.definition.id has "iron") {
        event.maxEnchCount = 2;
        print("Limited iron tools to max 2 enchantments");
    }
});

// Example 14: Clear all enchantments and add custom set
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Custom enchantment set for emerald items (if they exist)
    if (event.itemStack.definition.id has "emerald") {
        event.clearEnchantments();

        // Add custom enchantment set
        event.addEnchantment(<enchantment:minecraft:unbreaking>.makeEnchantment(3));
        event.addEnchantment(<enchantment:minecraft:mending>.makeEnchantment(1));

        print("Replaced enchantments with custom set for emerald item");
    }
});


// Example 15: Conditional enchantment manipulation based on player
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    if (!isNull(event.player)) {
        // Give bonus enchantments to specific players
        if (event.player.name == "Dev" || event.player.name == "Admin") {
            event.minEnchCount = 3;
            print("Bonus enchantments for player: " + event.player.name);
        }
    }
});


// Example 16: Position-based enchantment modification
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    if (!isNull(event.position)) {
        // Special enchantments in specific coordinates (e.g., spawn area)
        val pos = event.position;

        if (pos.x >= -100 && pos.x <= 100 && pos.z >= -100 && pos.z <= 100) {
            event.addEnchantment(<enchantment:minecraft:unbreaking>.makeEnchantment(2));
            print("Added bonus Unbreaking in spawn area");
        }
    }
});


// Example 17: Cancel post event to prevent enchantments
events.onRollEnchantmentsPost(function(event as RollEnchantmentsEvent_Post) {
    // Prevent enchanting leather armor
    if (event.itemStack.definition.id has "leather") {
        print("Canceling enchantments for leather armor");
        event.canceled = true;
    }
});


// Example 18: Reading override values (can be null = not set)
events.onRollEnchantmentsPre(function(event as RollEnchantmentsEvent_Pre) {
    if (event.itemStack.definition.id == "minecraft:diamond_pickaxe") {
        val efficiencyOverride = event.getOverride(<enchantment:minecraft:efficiency>);
        efficiencyOverride.minLvl = 4;

        // Read back the value
        if (!isNull(efficiencyOverride.minLvl)) {
            print("Efficiency minLvl is now: " + efficiencyOverride.minLvl);
        }
    }
});


print("RollEnchantments example script loaded successfully!");