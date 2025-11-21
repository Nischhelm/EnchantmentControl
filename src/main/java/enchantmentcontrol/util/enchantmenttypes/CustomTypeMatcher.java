package enchantmentcontrol.util.enchantmenttypes;

import com.shultrea.rin.enchantments.base.EnchantmentBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CustomTypeMatcher implements ITypeMatcher {
    private final String name;
    private final String regex;

    public CustomTypeMatcher(String in){
        String[] split = in.split(";");
        if(split.length==2) {
            this.name = split[0].trim();
            this.regex = split[1].trim();
        } else {
            this.name = "";
            this.regex = "";
        }
    }

    public boolean isValid(){
        return !this.name.isEmpty() && !this.regex.isEmpty();
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean matches(EnchantmentBase enchantment, ItemStack stack, Item item, String itemName){
        return itemName.matches(this.regex);
    }
}
