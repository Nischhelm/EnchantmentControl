package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import meldexun.betterconfig.Config;
import meldexun.betterconfig.ConfigCategory;
import meldexun.betterconfig.ConfigElement;
import meldexun.betterconfig.ConfigList;
import meldexun.betterconfig.ConfigValue;

import java.util.Map;

// Here is where i'm paying for my sins
public class ConfigMigrator {
	public static void handleMigration(Config config, String fileVersion, String classVersion) {
		// Migration is needed when fileVersion is empty (old config has no version)
		if (fileVersion == null || fileVersion.isEmpty()) {
			EnchantmentControl.LOGGER.info("Detected old config format (no version). Starting migration to version {}", classVersion);

			try {
				ConfigCategory general = config.getCategories().get("general");
				if (general == null) {
					EnchantmentControl.LOGGER.warn("No general category found, skipping migration");
					return;
				}

				migrateFirstSetup(general);
				migrateIncompatibleGroups(general);
				migrateRarities(general);
				migrateCreatureAttributes(general);
				migrateItemTypes(general);
				migrateAnvilMechanics(general);
				migrateCompat(general);
				migrateMixinToggles(general);

				renameSubCategory(general, "blacklists", "Blacklists");
				renameSubCategory(general, "debug", "Debug");
				renameSubCategory(general, "enchantment table mechanics", "Enchantment Table Mechanics");

				config.setVersion(ConfigHandler.class.getName(), "1.0.0");

				EnchantmentControl.LOGGER.info("Config migration completed successfully");
			} catch (Exception e) {
				EnchantmentControl.LOGGER.error("Config migration failed", e);
				throw new RuntimeException("Config migration failed", e);
			}
		}
	}

	private static void migrateMixinToggles(ConfigCategory general) {
		ConfigCategory cat = renameSubCategory(general, "mixin toggles", "Mixin Toggles");
		renameElement(cat, "(MixinToggle) Render First Enchant Bold", "(MixinToggle) Render First Enchant Underscored");
	}

	private static void migrateFirstSetup(ConfigCategory general) {
		ConfigCategory oldCat = renameSubCategory(general, "first setup", "First Setup");
		if (oldCat == null) return;

		renameSubCategory(oldCat, "enchantment id remaps", "Enchantment Id Remaps");
		renameSubCategory(oldCat, "enchantment numeric id remaps", "Enchantment Numeric Id Remaps");
	}

	private static void migrateIncompatibleGroups(ConfigCategory general) {
		ConfigElement oldElement = general.getElements().get("Incompatible Groups");
		if (!(oldElement instanceof ConfigList)) return;

		ConfigList oldList = (ConfigList) oldElement;

		// Create new category structure
		ConfigCategory incompatCat = general.getSubCategories()
			.computeIfAbsent("Incompatible Enchantments", k -> new ConfigCategory());
		ConfigCategory groupsCategory = new ConfigCategory();

		int groupIndex = 1;
		for (ConfigElement item : oldList.getList()) {
			if (item instanceof ConfigValue) {
				String line = ((ConfigValue) item).getValue();
				if (line == null || line.trim().isEmpty()) continue;

				// Parse CSV line and create ConfigList for the ArrayList
				String[] enchantments = line.split(",");
				if (enchantments.length == 0) continue;

				ConfigList groupList = new ConfigList();
				for (String ench : enchantments) {
					String enchName = ench.trim();
					if (!enchName.isEmpty()) {
						ConfigValue cv = new ConfigValue();
						cv.setValue(enchName);
						groupList.getList().add(cv);
					}
				}

				if (!groupList.getList().isEmpty()) {
					groupsCategory.getElements().put("Group " + groupIndex++, groupList);
				}
			}
		}

		incompatCat.getSubCategories().put("Incompatible Groups", groupsCategory);

		// Remove old element from general
		general.getElements().remove("Incompatible Groups");

		// Also migrate the enabled toggle
		moveElement(general, incompatCat, "Incompatible Groups Enabled");
	}

	private static void migrateRarities(ConfigCategory general) {
		// Create Advanced category
		ConfigCategory advanced = general.getSubCategories()
			.computeIfAbsent("Advanced", k -> new ConfigCategory());

		// Move rarities subcategory to advanced and rename
		moveSubCategory(general, advanced, "rarities");
		renameSubCategory(advanced, "rarities", "Rarities");
	}

	private static void migrateCreatureAttributes(ConfigCategory general) {
		ConfigCategory oldCreatureAttr = general.getSubCategories().remove("creature attributes");
		if (oldCreatureAttr == null) return;

		ConfigCategory advanced = general.getSubCategories().computeIfAbsent("Advanced", k -> new ConfigCategory());
		ConfigCategory newCreatureAttr = new ConfigCategory();

		// Parse each entry: "ATTR_NAME" -> "type, val1, val2, ..."
		for (Map.Entry<String, ConfigElement> entry : oldCreatureAttr.getElements().entrySet()) {
			String attrName = entry.getKey();
			if (!(entry.getValue() instanceof ConfigValue)) continue;

			String csvValue = ((ConfigValue) entry.getValue()).getValue();
			String[] parts = csvValue.split(",");

			if (parts.length < 2) continue;

			// Create subcategory for this attribute
			ConfigCategory attrCat = new ConfigCategory();

			// Map type: modid/mob/class → MODID/EXACT/CLASS
			String typeStr = parts[0].trim();
			String enumType = mapCreatureAttributeTypeToEnum(typeStr);
			ConfigValue typeValue = new ConfigValue();
			typeValue.setValue(enumType);
			attrCat.getElements().put("type", typeValue);

			// Create values list (LinkedHashSet<String>)
			ConfigList valuesList = new ConfigList();
			for (int i = 1; i < parts.length; i++) {
				String val = parts[i].trim();
				if (!val.isEmpty()) {
					ConfigValue cv = new ConfigValue();
					cv.setValue(val);
					valuesList.getList().add(cv);
				}
			}
			attrCat.getElements().put("values", valuesList);

			newCreatureAttr.getSubCategories().put(attrName, attrCat);
		}

		advanced.getSubCategories().put("Creature Attributes", newCreatureAttr);
	}

	private static void migrateItemTypes(ConfigCategory general) {
		ConfigCategory itemTypes = renameSubCategory(general, "item types", "Item Types");
		if (itemTypes == null) return;

		// Migrate custom types
		migrateCustomItemTypes(itemTypes);

		// Migrate general item types
		migrateGeneralSection(itemTypes);

		// Migrate anvil item types
		migrateAnvilSection(itemTypes);

		// Migrate top-level item types settings (rename some fields)
		renameElement(itemTypes, "Item Blacklist", "Allow Modded Item Blacklist");
		renameElement(itemTypes, "Modification Enabled", "Section Enabled");
	}

	private static void migrateAnvilMechanics(ConfigCategory general) {
		ConfigCategory cat = renameSubCategory(general, "anvil mechanics", "Anvil Mechanics");
		renameSubCategory(cat, "blood anvil", "Blood Anvil");
		cat.getElements().remove("(MixinToggle) Anvil Use Count UpgPot Compat (SoManyEnchantments)");
	}

	private static void migrateCompat(ConfigCategory general) {
		ConfigCategory cat = renameSubCategory(general, "compat", "Compat");
		renameSubCategory(cat, "newsme", "newSME");
	}

	private static void migrateCustomItemTypes(ConfigCategory itemTypes) {
		ConfigElement oldCustomTypesElement = itemTypes.getElements().get("Custom Item Types");
		if (!(oldCustomTypesElement instanceof ConfigList)) return;

		ConfigList oldList = (ConfigList) oldCustomTypesElement;
		ConfigCategory newCustomTypes = new ConfigCategory();

		// Parse CSV: "MatcherName, type, value1, value2, ..." OR "MatcherName, value1, value2, ..." (defaults to REGEX)
		for (ConfigElement item : oldList.getList()) {
			if (!(item instanceof ConfigValue)) continue;

			String csvLine = ((ConfigValue) item).getValue();
			String[] parts = csvLine.split(",");

			if (parts.length < 2) continue; // Need at least name and one value

			String matcherName = parts[0].trim();
			String typeStr = parts[1].trim();

			// Check if parts[1] is a recognized type
			String enumType;
			int valueStartIndex;
			if (isRecognizedItemType(typeStr)) {
				// Format: "MatcherName, type, value1, value2, ..."
				enumType = mapItemTypeToEnum(typeStr);
				valueStartIndex = 2;
				if (parts.length < 3) continue; // Need at least one value after type
			} else {
				// Format: "MatcherName, value1, value2, ..." - default to REGEX
				enumType = "REGEX";
				valueStartIndex = 1;
			}

			// Create subcategory for this matcher
			ConfigCategory matcherCat = new ConfigCategory();

			ConfigValue typeValue = new ConfigValue();
			typeValue.setValue(enumType);
			matcherCat.getElements().put("type", typeValue);

			// Create values list (Set<String>)
			ConfigList valuesList = new ConfigList();
			for (int i = valueStartIndex; i < parts.length; i++) {
				String val = parts[i].trim();
				if (!val.isEmpty()) {
					ConfigValue cv = new ConfigValue();
					cv.setValue(val);
					valuesList.getList().add(cv);
				}
			}
			matcherCat.getElements().put("values", valuesList);

			newCustomTypes.getSubCategories().put(matcherName, matcherCat);
		}

		itemTypes.getSubCategories().put("Custom Item Types", newCustomTypes);
		itemTypes.getElements().remove("Custom Item Types");
	}

	private static void migrateGeneralSection(ConfigCategory itemTypes) {
		ConfigCategory general = renameSubCategory(itemTypes, "general", "General");
		if (general == null) return;

		// Migrate Item Types list
		ConfigElement oldTypesElement = general.getElements().remove("Item Types");
		if (oldTypesElement instanceof ConfigList) {
			ConfigCategory newTypesMap = parseItemTypesListToMap((ConfigList) oldTypesElement);
			general.getSubCategories().put("Item Types", newTypesMap);
		}

		// Migrate settings with name changes
		renameElement(general, "Blacklist", "Allow Modded Enchantment Blacklist");
		renameElement(general, "Modification Enabled", "Section Enabled");
	}

	private static void migrateAnvilSection(ConfigCategory itemTypes) {
		ConfigCategory anvil = renameSubCategory(itemTypes, "anvil", "Anvil");
		if (anvil == null) return;

		// Migrate Item Types list
		ConfigElement oldTypesElement = anvil.getElements().remove("Item Types");
		if (oldTypesElement instanceof ConfigList) {
			ConfigCategory newTypesMap = parseItemTypesListToMap((ConfigList) oldTypesElement);
			anvil.getSubCategories().put("Item Types", newTypesMap);
		}

		// Migrate settings with name changes
		renameElement(anvil, "Blacklist", "Allow Modded Enchantment Blacklist");
		renameElement(anvil, "Modification Enabled", "Section Enabled");
	}

	private static ConfigCategory parseItemTypesListToMap(ConfigList oldList) {
		ConfigCategory typesMap = new ConfigCategory();

		// Parse lines like "TYPE = ench1, ench2, ench3"
		for (ConfigElement item : oldList.getList()) {
			if (!(item instanceof ConfigValue)) continue;

			String line = ((ConfigValue) item).getValue();
			String[] parts = line.split("=", 2);

			if (parts.length != 2) continue;

			String typeName = parts[0].trim();
			String[] enchantments = parts[1].split(",");

			// Create ConfigList for this type (Map<String, ArrayList<String>>)
			ConfigList enchList = new ConfigList();
			for (String ench : enchantments) {
				String enchName = ench.trim();
				if (!enchName.isEmpty()) {
					ConfigValue cv = new ConfigValue();
					cv.setValue(enchName);
					enchList.getList().add(cv);
				}
			}

			if (!enchList.getList().isEmpty()) {
				typesMap.getElements().put(typeName, enchList);
			}
		}

		return typesMap;
	}

	// Helper methods

	/**
	 * Move a subcategory from one category to another (keeps the same name)
	 * @param from Source category
	 * @param to Destination category
	 * @param name Name of the subcategory
	 */
	private static void moveSubCategory(ConfigCategory from, ConfigCategory to, String name) {
		ConfigCategory subCat = from.getSubCategories().remove(name);
		if (subCat != null) {
			to.getSubCategories().put(name, subCat);
		}
	}

	/**
	 * Rename a subcategory within the same parent category
	 * @param category Parent category
	 * @param oldName Current name
	 * @param newName New name
	 */
	private static ConfigCategory renameSubCategory(ConfigCategory category, String oldName, String newName) {
		ConfigCategory subCat = category.getSubCategories().remove(oldName);
		if (subCat != null) {
			category.getSubCategories().put(newName, subCat);
		}
		return subCat;
	}

	/**
	 * Move a config element from one category to another (keeps the same name)
	 * @param from Source category
	 * @param to Destination category
	 * @param name Name of the element
	 */
	private static void moveElement(ConfigCategory from, ConfigCategory to, String name) {
		ConfigElement element = from.getElements().remove(name);
		if (element != null) {
			to.getElements().put(name, element);
		}
	}

	/**
	 * Rename a config element within the same category
	 * @param category Parent category
	 * @param oldName Current name
	 * @param newName New name
	 */
	private static void renameElement(ConfigCategory category, String oldName, String newName) {
		ConfigElement element = category.getElements().remove(oldName);
		if (element != null) {
			category.getElements().put(newName, element);
		}
	}

	private static boolean isRecognizedItemType(String type) {
		return type.equals("modid") || type.equals("regex") || type.equals("items") || type.equals("class");
	}

	private static String mapItemTypeToEnum(String oldType) {
		switch (oldType) {
			case "modid": return "MODID";
			case "regex": return "REGEX";
			case "items": return "EXACT"; // IMPORTANT: "items" maps to EXACT enum
			case "class": return "CLASS";
			default: return "EXACT"; // this won't happen as we check recognized before
		}
	}

	private static String mapCreatureAttributeTypeToEnum(String oldType) {
		switch (oldType.toLowerCase()) {
			case "modid": return "MODID";
			case "mob": return "EXACT"; // IMPORTANT: "mob" maps to EXACT enum (exact entity registry names)
			case "class": return "CLASS";
			default:
				EnchantmentControl.LOGGER.warn("Unknown creature attribute type: {}, defaulting to EXACT", oldType);
				return "EXACT";
		}
	}
}
