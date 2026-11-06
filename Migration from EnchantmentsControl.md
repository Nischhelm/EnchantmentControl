## Migrating from Enchantments Control

Enchantments Control (`enchcontrol`) keeps its per-enchantment settings in `/config/enchcontrol/enchantments.json`.
On the first launch with this mod installed, that file gets imported automatically. You don't need to move anything.
This is controlled by the `Import Enchantments Control Config` option in the "First Setup" section of `enchantmentcontrol.cfg` (default: on).

How the import works:
- Enchantments Control needs every field for every enchantment, and on its first launch it writes its bundled defaults for all vanilla enchantments.
  So this mod only imports the fields that **differ from the enchantment's actual values**. Enchantments you didn't change don't get a file.
- The changes are written to `/config/enchantmentcontrol/enchantments/<modid>/<enchid>.json` and apply on that same launch.
- Enchantments that already have a json there are skipped, so your existing configs are never overwritten.
- The original file is backed up as `enchantments.json.imported` (`.imported2`, `.imported3`, ... if that already exists), and `enchantments.json` is replaced with an empty list `[]`.
  So it's only imported once, and Enchantments Control (if still installed) doesn't apply it on top of this mod's jsons.
  The file is emptied rather than deleted because Enchantments Control fails to load its `config.json` without it.
  It only writes a new one if `config.json` is missing too.
- To import again, restore the backup and delete the affected jsons in `/enchantments/`.

| Enchantments Control | Enchantment Control |
|---|---|
| `rarity` | `rarity` |
| `treasure` | `isTreasure` |
| `curse` | `isCurse` |
| `allowed_on_books` | `isAllowedOnBooks` |
| `min_lvl` / `max_lvl` | `minLvl` / `maxLvl` |
| `double_price` | `doublePrice` |
| `hide_on_item` / `hide_on_book` | `hideOnItem` / `hideOnBook` |
| `type` | `type` |
| `equipment_slots` | `slots` (`ALL` or empty = all slots) |
| `min_ench_eval` / `max_ench_eval` (only if `custom_evaluations` is true) | `enchantability` |
| `incompatible` / `incompat_mode` | "Incompatible Groups" config, see below |
| `enabled: false` | "Registered Enchantment Blacklist", see below |
| `items_list` / `items_list_mode` / `applicability_mode` | "Custom Item Types" + "Anvil.Item Types", see below |
| `description` | `/config/enchantmentcontrol/descriptions.txt`, only for enchantments that don't have a description there yet. Lines are joined with `\n`, lang keys get translated to the current language |

Incompatibilities don't go into the per-enchant jsons:
- On first setup ("Print Default Incompatibilities" enabled), they're merged into the inferred groups. `incompat_mode: 1` (only the list counts) removes the enchant's own incompatibilities there.
- If the groups are already set up, each imported pair is added as its own group named `Enchantments Control: a + b`.
  `incompat_mode: 1` can't be applied then and gets logged instead.
- Enchantments Control only checks them from the listing enchant's side. Groups always apply both ways.

Disabled enchantments (`enabled: false`) are fully unregistered instead.
Enchantments Control only kept them out of the enchanting table, librarians and random loot, so you could still get them with `/enchant`, from the creative tab or on items that already had them.
They're added to the "Registered Enchantment Blacklist", which applies from the next startup on, because registration has already happened by the time the import runs.
Items in existing worlds lose those enchantments.

Enchantability formulas are translated if they match one of these patterns (spaces don't matter):
- `min_ench_eval`: `A`, `A+B*(LVL-1)`, `A+(LVL-1)*B`, `A+B*LVL`, `B*LVL`, `LVL*B`
- `max_ench_eval`: `(1+10*LVL)+R` (`SUPER`), `MIN+R` (`NORMAL`), a plain number (`CONST`)

Other formulas are skipped with a warning in the log.

Item lists only applied to the anvil (and `/enchant`, random loot) in Enchantments Control, so they're translated into "Anvil.Item Types".
For each enchantment with a list, two custom item types are created: `EnchantmentsControl_<modid>_<enchid>` (the listed items) and `EnchantmentsControl_<modid>_<enchid>_Others` (every other item).
Inverted (`!`) anvil types disallow first, so:

| Enchantments Control | Anvil Item Types for that enchantment |
|---|---|
| blacklist (`items_list_mode: 0`) | `!EnchantmentsControl_<...>` |
| whitelist (`items_list_mode: 1`) | `!EnchantmentsControl_<...>_Others` |
| whitelist, only the list decides (`applicability_mode: 1`) | `EnchantmentsControl_<...>` + `!EnchantmentsControl_<...>_Others` |
| blacklist, only the list decides (`applicability_mode: 1`) | `ANY` + `!EnchantmentsControl_<...>` |

Like incompatibilities, these get merged into the inferred types on first setup ("Print Default Item Types" enabled), otherwise they're added to the existing config.

If Enchantments Control stays installed, this mod blocks its patches to the enchanting table, anvil, librarians, `enchant_randomly` loot and `/enchant`.
Their patch system replaces those methods entirely, which would silently disable this mod's logic.
Its cosmetic features from its `config.json` keep working: roman numerals, curse hiding, description tooltips, custom localization and Thermal Expansion enchanter support.
Its level caps feature is disabled entirely.