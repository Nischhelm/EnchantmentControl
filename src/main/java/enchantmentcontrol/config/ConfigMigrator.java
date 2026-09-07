package enchantmentcontrol.config;

import enchantmentcontrol.EnchantmentControl;
import meldexun.betterconfig.api.ConfigMigrationHelper;
import meldexun.betterconfig.api.tree.*;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.util.Map;

// Here is where I pay for my sins
public class ConfigMigrator {
	public static <T extends IConfigContext<T>> void handleMigration(IConfigCategory<T> general, T context, ArtifactVersion fileVersion) {
		if (fileVersion == null) migrateTo1_2_0(general, context); // migrate from pre 1.2.0 where there was no version yet
	}

	private static <T extends IConfigContext<T>> void migrateTo1_2_0(IConfigCategory<T> general, T context) {
		try {
			migrateFirstSetup(general);
			migrateIncompatibleGroups(general, context);
			migrateRarities(general, context);
			migrateCreatureAttributes(general, context);
			migrateItemTypes(general, context);
			IConfigCategory<T> anvilMechanics = migrateAnvilMechanics(general);
			migrateCompat(general);
			IConfigCategory<T> mixinToggles = migrateMixinToggles(general);

			ConfigMigrationHelper.moveCategory("(MixinToggle) Anvil Use Count UpgPot Compat (SoManyEnchantments)", anvilMechanics, mixinToggles);

			ConfigMigrationHelper.renameCategory(general, "blacklists", "Blacklists");
			ConfigMigrationHelper.renameCategory(general, "debug", "Debug");
			ConfigMigrationHelper.renameCategory(general, "enchantment table mechanics", "Enchantment Table Mechanics");
		} catch (Exception e) {
			EnchantmentControl.LOGGER.error("Config migration failed", e);
			throw new RuntimeException("Config migration failed", e);
		}
	}

	private static <T extends IConfigContext<T>> IConfigCategory<T> migrateMixinToggles(IConfigCategory<T> general) {
		IConfigCategory<T> cat = ConfigMigrationHelper.renameCategory(general, "mixin toggles", "Mixin Toggles");
		ConfigMigrationHelper.renameElement(cat, "(MixinToggle) Render First Enchant Bold", "(MixinToggle) Render First Enchant Underscored");
		return cat;
	}

	private static <T extends IConfigContext<T>> void migrateFirstSetup(IConfigCategory<T> general) {
		IConfigCategory<T> oldCat = ConfigMigrationHelper.renameCategory(general, "first setup", "First Setup");
		if (oldCat == null) return;

		ConfigMigrationHelper.renameCategory(oldCat, "enchantment id remaps", "Enchantment Id Remaps");
		ConfigMigrationHelper.renameCategory(oldCat, "enchantment numeric id remaps", "Enchantment Numeric Id Remaps");
	}

	private static <T extends IConfigContext<T>> void migrateIncompatibleGroups(IConfigCategory<T> general, T context) {
		IConfigElement<T> oldElement = general.getElements().get("Incompatible Groups");
		if (!(oldElement instanceof IConfigList)) return;

		IConfigList<T> oldList = (IConfigList<T>) oldElement;

		// Create new category structure
		IConfigCategory<T> incompatCat = general.getSubCategories()
			.computeIfAbsent("Incompatible Enchantments", k -> context.createCategory());
		IConfigCategory<T> groupsCategory = context.createCategory();

		int groupIndex = 1;
		for (IConfigElement<T> item : oldList.getList()) {
			if (item instanceof IConfigValue) {
				String line = ((IConfigValue<T>) item).getValue();
				if (line == null || line.trim().isEmpty()) continue;

				// Parse CSV line and create IConfigList<T> for the ArrayList
				String[] enchantments = line.split(",");
				if (enchantments.length == 0) continue;

				IConfigList<T> groupList = context.createList();
				for (String ench : enchantments) {
					String enchName = ench.trim();
					if (!enchName.isEmpty()) {
						IConfigValue<T> cv = context.createValue();
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
		ConfigMigrationHelper.moveElement("Incompatible Groups Enabled", general, incompatCat);
	}

	private static <T extends IConfigContext<T>> void migrateRarities(IConfigCategory<T> general, T context) {
		// Create Advanced category
		IConfigCategory<T> advanced = general.getSubCategories()
			.computeIfAbsent("Advanced", k -> context.createCategory());

		// Move rarities subcategory to advanced and rename
		ConfigMigrationHelper.moveCategory("rarities", general, advanced);
		ConfigMigrationHelper.renameCategory(advanced, "rarities", "Rarities");
	}

	private static <T extends IConfigContext<T>> void migrateCreatureAttributes(IConfigCategory<T> general, T context) {
		IConfigCategory<T> oldCreatureAttr = general.getSubCategories().remove("creature attributes");
		if (oldCreatureAttr == null) return;

		IConfigCategory<T> advanced = general.getSubCategories().computeIfAbsent("Advanced", k -> context.createCategory());
		IConfigCategory<T> newCreatureAttr = context.createCategory();

		// Parse each entry: "ATTR_NAME" -> "type, val1, val2, ..."
		for (Map.Entry<String, IConfigElement<T>> entry : oldCreatureAttr.getElements().entrySet()) {
			String attrName = entry.getKey();
			if (!(entry.getValue() instanceof IConfigValue)) continue;

			String csvValue = ((IConfigValue<T>) entry.getValue()).getValue();
			String[] parts = csvValue.split(",");

			if (parts.length < 2) continue;

			// Create subcategory for this attribute
			IConfigCategory<T> attrCat = context.createCategory();

			// Map type: modid/mob/class → MODID/EXACT/CLASS
			String typeStr = parts[0].trim();
			String enumType = mapCreatureAttributeTypeToEnum(typeStr);
			IConfigValue<T> typeValue = context.createValue();
			typeValue.setValue(enumType);
			attrCat.getElements().put("type", typeValue);

			// Create values list (LinkedHashSet<String>)
			IConfigList<T> valuesList = context.createList();
			for (int i = 1; i < parts.length; i++) {
				String val = parts[i].trim();
				if (!val.isEmpty()) {
					IConfigValue<T> cv = context.createValue();
					cv.setValue(val);
					valuesList.getList().add(cv);
				}
			}
			attrCat.getElements().put("values", valuesList);

			newCreatureAttr.getSubCategories().put(attrName, attrCat);
		}

		advanced.getSubCategories().put("Creature Attributes", newCreatureAttr);
	}

	private static <T extends IConfigContext<T>> void migrateItemTypes(IConfigCategory<T> general, T context) {
		IConfigCategory<T> itemTypes = ConfigMigrationHelper.renameCategory(general, "item types", "Item Types");
		if (itemTypes == null) return;

		// Migrate custom types
		migrateCustomItemTypes(itemTypes, context);

		// Migrate general item types
		migrateGeneralSection(itemTypes, context);

		// Migrate anvil item types
		migrateAnvilSection(itemTypes, context);

		// Migrate top-level item types settings (rename some fields)
		ConfigMigrationHelper.renameElement(itemTypes, "Item Blacklist", "Allow Modded Item Blacklist");
		ConfigMigrationHelper.renameElement(itemTypes, "Modification Enabled", "Section Enabled");
	}

	private static <T extends IConfigContext<T>> IConfigCategory<T> migrateAnvilMechanics(IConfigCategory<T> general) {
		IConfigCategory<T> cat = ConfigMigrationHelper.renameCategory(general, "anvil mechanics", "Anvil Mechanics");
		ConfigMigrationHelper.renameCategory(cat, "blood anvil", "Blood Anvil");
		return cat;
	}

	private static <T extends IConfigContext<T>> void migrateCompat(IConfigCategory<T> general) {
		IConfigCategory<T> cat = ConfigMigrationHelper.renameCategory(general, "compat", "Compat");
		ConfigMigrationHelper.renameCategory(cat, "newsme", "newSME");
	}

	private static <T extends IConfigContext<T>> void migrateCustomItemTypes(IConfigCategory<T> itemTypes, T context) {
		IConfigElement<T> oldCustomTypesElement = itemTypes.getElements().get("Custom Item Types");
		if (!(oldCustomTypesElement instanceof IConfigList)) return;

		IConfigList<T> oldList = (IConfigList<T>) oldCustomTypesElement;
		IConfigCategory<T> newCustomTypes = context.createCategory();

		// Parse CSV: "MatcherName, type, value1, value2, ..." OR "MatcherName, value1, value2, ..." (defaults to REGEX)
		for (IConfigElement<T> item : oldList.getList()) {
			if (!(item instanceof IConfigValue)) continue;

			String csvLine = ((IConfigValue<T>) item).getValue();
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
			IConfigCategory<T> matcherCat = context.createCategory();

			IConfigValue<T> typeValue = context.createValue();
			typeValue.setValue(enumType);
			matcherCat.getElements().put("type", typeValue);

			// Create values list (Set<String>)
			IConfigList<T> valuesList = context.createList();
			for (int i = valueStartIndex; i < parts.length; i++) {
				String val = parts[i].trim();
				if (!val.isEmpty()) {
					IConfigValue<T> cv = context.createValue();
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

	private static <T extends IConfigContext<T>> void migrateGeneralSection(IConfigCategory<T> itemTypes, T context) {
		IConfigCategory<T> general = ConfigMigrationHelper.renameCategory(itemTypes, "general", "General");
		if (general == null) return;

		// Migrate Item Types list
		IConfigElement<T> oldTypesElement = general.getElements().remove("Item Types");
		if (oldTypesElement instanceof IConfigList) {
			IConfigCategory<T> newTypesMap = parseItemTypesListToMap((IConfigList<T>) oldTypesElement, context);
			general.getSubCategories().put("Item Types", newTypesMap);
		}

		// Migrate settings with name changes
		ConfigMigrationHelper.renameElement(general, "Blacklist", "Allow Modded Enchantment Blacklist");
		ConfigMigrationHelper.renameElement(general, "Modification Enabled", "Section Enabled");
	}

	private static <T extends IConfigContext<T>> void migrateAnvilSection(IConfigCategory<T> itemTypes, T context) {
		IConfigCategory<T> anvil = ConfigMigrationHelper.renameCategory(itemTypes, "anvil", "Anvil");
		if (anvil == null) return;

		// Migrate Item Types list
		IConfigElement<T> oldTypesElement = anvil.getElements().remove("Item Types");
		if (oldTypesElement instanceof IConfigList) {
			IConfigCategory<T> newTypesMap = parseItemTypesListToMap((IConfigList<T>) oldTypesElement, context);
			anvil.getSubCategories().put("Item Types", newTypesMap);
		}

		// Migrate settings with name changes
		ConfigMigrationHelper.renameElement(anvil, "Blacklist", "Allow Modded Enchantment Blacklist");
		ConfigMigrationHelper.renameElement(anvil, "Modification Enabled", "Section Enabled");
	}

	private static <T extends IConfigContext<T>> IConfigCategory<T> parseItemTypesListToMap(IConfigList<T> oldList, T context) {
		IConfigCategory<T> typesMap = context.createCategory();

		// Parse lines like "TYPE = ench1, ench2, ench3"
		for (IConfigElement<T> item : oldList.getList()) {
			if (!(item instanceof IConfigValue)) continue;

			String line = ((IConfigValue<T>) item).getValue();
			String[] parts = line.split("=", 2);

			if (parts.length != 2) continue;

			String typeName = parts[0].trim();
			String[] enchantments = parts[1].split(",");

			// Create ConfigList for this type (Map<String, ArrayList<String>>)
			IConfigList<T> enchList = context.createList();
			for (String ench : enchantments) {
				String enchName = ench.trim();
				if (!enchName.isEmpty()) {
					IConfigValue<T> cv = context.createValue();
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
