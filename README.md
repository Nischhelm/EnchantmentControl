Enchantment Control

A mod to control all vanilla behavior of all vanilla and modded enchantments such as 
- minlvl/maxlvl
- enchantability
- incompatibily
- rarity
- treasure
- curse
- name color
- applicability on items and books
- applicability via anvil vs only enchantment table
- etc

## Migrating from Enchantments Control

Enchantments Control (`enchcontrol`) keeps its per-enchantment settings in `/config/enchcontrol/enchantments.json`.
On the first launch with this mod installed, that file gets imported automatically. You don't need to move anything.
This is controlled by the `Import Enchantments Control Config` option in the "First Setup" section of `enchantmentcontrol.cfg` (default: on).

How the import works:
- Enchantments Control needs every field for every enchantment, and on its first launch it writes its bundled defaults for all vanilla enchantments.
  So this mod only imports the fields that **differ from the enchantment's actual values**. Enchantments you didn't change don't get a file.
- The changes are written to `/config/enchantmentcontrol/enchantments/<modid>/<enchid>.json` and apply on that same launch.
- Enchantments that already have a json there are skipped, so your existing configs are never overwritten.
- The original file is backed up as `enchantments.json.imported`, and `enchantments.json` is replaced with an empty list `[]`.
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
| `type` | `type` |
| `equipment_slots` | `slots` (`ALL` or empty = all slots) |
| `min_ench_eval` / `max_ench_eval` (only if `custom_evaluations` is true) | `enchantability` |

Enchantability formulas are translated if they match one of these patterns (spaces don't matter):
- `min_ench_eval`: `A`, `A+B*(LVL-1)`, `A+(LVL-1)*B`, `A+B*LVL`, `B*LVL`, `LVL*B`
- `max_ench_eval`: `(1+10*LVL)+R` (`SUPER`), `MIN+R` (`NORMAL`), a plain number (`CONST`)

Other formulas are skipped with a warning in the log.

Not imported (a warning is logged for each enchantment that uses them):
- `enabled: false`, `hide_on_item`, `hide_on_book`
- `incompatible` / `incompat_mode`: use the "Incompatible Enchantment Groups" config instead
- `items_list` / `applicability_mode`: use the "Item Types" configs instead
- `description`: use `/config/enchantmentcontrol/descriptions.txt` instead

Enchantments Control's own `config.json` (roman numerals, level caps, curse hiding, description tooltips, Thermal Expansion support) isn't touched.
If you keep Enchantments Control installed for those features, note that its level caps (default 10) also limit any max level you set in this mod.
