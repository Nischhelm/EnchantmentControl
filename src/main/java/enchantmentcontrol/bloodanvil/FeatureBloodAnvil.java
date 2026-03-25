package enchantmentcontrol.bloodanvil;

import enchantmentcontrol.EnchantmentControl;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Set;

public class FeatureBloodAnvil {

    public static final BlockBloodAnvil BLOOD_ANVIL = new BlockBloodAnvil();
    public static Set<String> blacklistedItems = null;

    public static void onPreInit(){
        NetworkRegistry.INSTANCE.registerGuiHandler(EnchantmentControl.MODID, new FeatureBloodAnvil.GuiHandlerBloodAnvil());
    }

    public static void resetConfigValues(){
        blacklistedItems = null;
    }

    @SubscribeEvent
    public static void onBlockRegistry(RegistryEvent.Register<Block> event) {
        event.getRegistry().register(BLOOD_ANVIL);
    }

    @SubscribeEvent
    public static void onItemRegistry(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(new ItemBlock(BLOOD_ANVIL).setRegistryName(EnchantmentControl.MODID, "blood_anvil").setTranslationKey("blood_anvil"));
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void onModelRegistry(ModelRegistryEvent event) {
        BLOOD_ANVIL.initModel();
    }

    public static class GuiHandlerBloodAnvil implements IGuiHandler {
        @Nullable
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return new ContainerBloodAnvil(player.inventory, world, new BlockPos(x, y, z));
        }

        @Nullable
        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
            return new GuiBloodAnvil(player.inventory, world);
        }
    }
}
