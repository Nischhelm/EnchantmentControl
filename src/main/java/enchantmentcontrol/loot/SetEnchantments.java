package enchantmentcontrol.loot;

import com.google.gson.*;
import enchantmentcontrol.EnchantmentControl;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SetEnchantments extends LootFunction {

    private final List<Enchantment> enchantments;
    private final List<RandomValueRange> lvlRanges;

    public SetEnchantments(LootCondition[] conditions, @Nullable List<Enchantment> enchantments, @Nullable List<RandomValueRange> lvlRanges) {
        super(conditions);
        this.enchantments = enchantments == null ? Collections.emptyList() : enchantments;
        this.lvlRanges = lvlRanges == null ? Collections.emptyList() : lvlRanges;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {
        if(this.enchantments.isEmpty() || this.lvlRanges.isEmpty()) return stack;

        if (stack.getItem() == Items.BOOK)
            stack = new ItemStack(Items.ENCHANTED_BOOK);

        for (int i=0; i<enchantments.size(); i++) {
            Enchantment enchantment = this.enchantments.get(i);
            int lvl = this.lvlRanges.get(i).generateInt(rand);

            if (stack.getItem() == Items.BOOK)
                ItemEnchantedBook.addEnchantment(stack, new EnchantmentData(enchantment, lvl));
            else
                stack.addEnchantment(enchantment, lvl);
        }

        return stack;
    }

    public static class Serializer extends LootFunction.Serializer<SetEnchantments> {
        public Serializer() {
            super(new ResourceLocation("set_enchantments"), SetEnchantments.class);
        }

        public void serialize(JsonObject obj, SetEnchantments instance, JsonSerializationContext context) {
            if (!instance.enchantments.isEmpty()) {
                JsonArray jsonarray = new JsonArray();

                for (int i = 0; i < instance.enchantments.size(); i++) {
                    Enchantment enchantment = instance.enchantments.get(i);
                    RandomValueRange randomvaluerange = instance.lvlRanges.get(i);

                    ResourceLocation loc = Enchantment.REGISTRY.getNameForObject(enchantment);
                    if (loc == null) {
                        EnchantmentControl.LOGGER.warn("Could not find enchantment of class {} when parsing SetEnchantments loot function, skipping", enchantment.getClass().getName());
                        continue;
                    }

                    JsonObject obj2 = new JsonObject();
                    obj2.addProperty("enchantment", loc.toString());
                    int min = (int) randomvaluerange.getMin();
                    int max = (int) randomvaluerange.getMax();
                    if(min == max)
                        obj2.addProperty("lvl", min);
                    else {
                        obj2.addProperty("lvlMin", randomvaluerange.getMin());
                        obj2.addProperty("lvlMax", randomvaluerange.getMax());
                    }

                    jsonarray.add(obj2);
                }

                obj.add("enchantments", jsonarray);
            }
        }

        public SetEnchantments deserialize(JsonObject obj, JsonDeserializationContext context, LootCondition[] conditions) {
            List<Enchantment> enchs = new ArrayList<>();
            List<RandomValueRange> lvlRanges = new ArrayList<>();

            if (obj.has("enchantments")) {
                for (JsonElement el : JsonUtils.getJsonArray(obj, "enchantments")) {
                    JsonObject obj2 = el.getAsJsonObject();

                    String s = JsonUtils.getString(obj2, "enchantment");
                    Enchantment enchantment = Enchantment.REGISTRY.getObject(new ResourceLocation(s));
                    if(enchantment == null) {
                        EnchantmentControl.LOGGER.warn("Could not find Enchantment {} when parsing SetEnchantments loot function, skipping", s);
                        continue;
                    }

                    int min;
                    int max;
                    if(obj2.has("lvl")) {
                        min = JsonUtils.getInt(obj2, "lvl");
                        max = min;
                    } else {
                        min = JsonUtils.getInt(obj2, "lvlMin");
                        max = JsonUtils.getInt(obj2, "lvlMax");
                    }

                    enchs.add(enchantment);
                    lvlRanges.add(new RandomValueRange(min, max));
                }
            }

            return new SetEnchantments(conditions, enchs, lvlRanges);
        }
    }
}