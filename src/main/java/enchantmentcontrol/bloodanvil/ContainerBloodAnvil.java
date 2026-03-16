package enchantmentcontrol.bloodanvil;

import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.AnvilCostUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ContainerBloodAnvil extends Container {

    private final IInventory outputSlot;
    private final IInventory inputSlots;
    private final World world;
    private final BlockPos pos;
    public int maximumCost;

    @SideOnly(Side.CLIENT)
    public ContainerBloodAnvil(InventoryPlayer playerInventory, World worldIn) {
        this(playerInventory, worldIn, BlockPos.ORIGIN);
    }

    public ContainerBloodAnvil(InventoryPlayer inventoryPlayer, World world, BlockPos blockPosIn) {
        this.outputSlot = new InventoryCraftResult();
        this.inputSlots = new InventoryBasic("Blood Anvil", true, 2) {
            public void markDirty() {
                super.markDirty();
                ContainerBloodAnvil.this.onCraftMatrixChanged(this);
            }
        };
        this.pos = blockPosIn;
        this.world = world;
        this.addSlotToContainer(new Slot(this.inputSlots, 0, 27, 47));
        this.addSlotToContainer(new Slot(this.inputSlots, 1, 76, 47));
        this.addSlotToContainer(new Slot(this.outputSlot, 2, 134, 47) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }

            @Override
            public boolean canTakeStack(@Nonnull EntityPlayer player) {
                return (player.capabilities.isCreativeMode || player.experienceLevel >= ContainerBloodAnvil.this.maximumCost) && ContainerBloodAnvil.this.maximumCost > 0 && this.getHasStack();
            }

            @Override
            @Nonnull
            public ItemStack onTake(@Nonnull EntityPlayer player, @Nonnull ItemStack stack) {
                //Pay XP Cost
                if (!player.capabilities.isCreativeMode)
                    player.addExperienceLevel(-ContainerBloodAnvil.this.maximumCost);

                //Clear input 1&2
                ContainerBloodAnvil.this.inputSlots.setInventorySlotContents(0, ItemStack.EMPTY);
                ContainerBloodAnvil.this.inputSlots.setInventorySlotContents(1, ItemStack.EMPTY);

                //Reset cost
                ContainerBloodAnvil.this.maximumCost = 0;

                //Break anvil
                IBlockState anvilState = world.getBlockState(blockPosIn);
                if (!player.capabilities.isCreativeMode && !world.isRemote && anvilState.getBlock() == FeatureBloodAnvil.BLOOD_ANVIL) {
                    world.setBlockToAir(blockPosIn);
                    world.playEvent(1029, blockPosIn, 0);
                } else if (!world.isRemote)
                    world.playEvent(1030, blockPosIn, 0);

                return stack;
            }
        });

        //Player inventory
        for (int i = 0; i < 3; ++i)
            for (int j = 0; j < 9; ++j)
                this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));

        //Player hotbar
        for (int k = 0; k < 9; ++k)
            this.addSlotToContainer(new Slot(inventoryPlayer, k, 8 + k * 18, 142));
    }

    @Override
    public void onCraftMatrixChanged(@Nonnull IInventory inventory) { // happens on client+server, can desync if options are different
        super.onCraftMatrixChanged(inventory);

        //Reset state
        this.maximumCost = 0;
        this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);

        if (inventory != this.inputSlots) return;

        ItemStack leftInput = this.inputSlots.getStackInSlot(0);
        ItemStack rightInput = this.inputSlots.getStackInSlot(1);
        if(leftInput.isEmpty() || rightInput.isEmpty()) return;

        boolean isBook = rightInput.getItem() == Items.BOOK || rightInput.getItem() == Items.ENCHANTED_BOOK;
        if (!ConfigHandler.anvil.bloodAnvil.allowBooks && isBook) return;

        // Blacklisted items
        ResourceLocation loc = rightInput.getItem().getRegistryName();
        if(loc != null) {
            String itemId = loc.toString();
            if (FeatureBloodAnvil.blacklistedItems == null)
               FeatureBloodAnvil.blacklistedItems = Stream.of(ConfigHandler.anvil.bloodAnvil.blacklist).collect(Collectors.toSet());

            boolean isInList = FeatureBloodAnvil.blacklistedItems.contains(itemId);
            if(isInList != ConfigHandler.anvil.bloodAnvil.asWhitelist) return;
        }

        Map<Enchantment, Integer> enchsLeft = EnchantmentHelper.getEnchantments(leftInput);
        if (enchsLeft.isEmpty()) return; // nothing to move

        Map<Enchantment, Integer> enchsRight = EnchantmentHelper.getEnchantments(rightInput);
        Map<Enchantment, Integer> enchsOut = new LinkedHashMap<>(enchsRight);
        if (!ConfigHandler.anvil.bloodAnvil.allowEnchanted && !enchsOut.isEmpty()) return;

        boolean changed = false;
        int totalCost = 0;

        // reverse so disallowing illegal mutually exclusive enchants on the original item will pick the topmost one to allow to move
        ArrayList<Map.Entry<Enchantment, Integer>> revertedEntryList = new ArrayList<>(enchsLeft.entrySet());
        Collections.reverse(revertedEntryList);

        for (Map.Entry<Enchantment, Integer> entry : revertedEntryList) {
            Enchantment enchantment = entry.getKey();
            int level = entry.getValue();

            // can this enchant go on this item
            if (!ConfigHandler.anvil.bloodAnvil.allowUnapplicable && !enchantment.canApply(rightInput)) continue;

            if (!ConfigHandler.anvil.bloodAnvil.allowMutuallyExclusiveTarget) {
                // is the currently moved enchant incompatible with any of the targets existing enchants ?
                boolean allCompatible = areAllEnchantmentsCompatible(enchantment, enchsRight.keySet());
                if (!allCompatible) continue;
            }
            if (!ConfigHandler.anvil.bloodAnvil.allowMutuallyExclusiveOriginal) {
                // is the currently moved enchant incompatible with any of the targets own enchants ?
                boolean allCompatible = areAllEnchantmentsCompatible(enchantment, enchsLeft.keySet());
                if (!allCompatible) {
                    enchsLeft.remove(enchantment); // one of the mutually exclusive ones is allowed to move.
                    continue;
                }
            }

            // no hax
            if(ConfigHandler.anvil.bloodAnvil.allowIllegalLevels)
                level = MathHelper.clamp(level, enchantment.getMinLevel(), enchantment.getMaxLevel());

            // take the bigger one if already on it
            if (enchsOut.containsKey(enchantment))
                level = Math.max(level, enchsOut.get(enchantment));

            totalCost += level * AnvilCostUtil.getRarityMultiplier(enchantment.getRarity(), isBook);

            enchsOut.put(enchantment, level);
            changed = true;
        }
        // if we don't move any enchants we shouldn't be allowed to do the move at all
        if (!changed) return;

        ItemStack outStack = rightInput.copy();
        EnchantmentHelper.setEnchantments(enchsOut, outStack);

        // Move anvil use cost + counts
        if (ConfigHandler.anvil.bloodAnvil.moveAnvilCost) {
            int costLeft = leftInput.getRepairCost();
            int costRight = rightInput.getRepairCost();
            int costOut = AnvilCostUtil.combineCosts(costLeft, costRight);
            outStack.setRepairCost(costOut);

            int countLeft = AnvilCostUtil.getAnvilCount(leftInput);
            int countRight = AnvilCostUtil.getAnvilCount(rightInput);
            int countOut = AnvilCostUtil.combineCounts(countLeft, countRight);
            AnvilCostUtil.setAnvilCount(outStack, countOut);
        }

        this.outputSlot.setInventorySlotContents(0, outStack);

        this.maximumCost = ConfigHandler.anvil.bloodAnvil.costIsDynamic ? totalCost : ConfigHandler.anvil.bloodAnvil.cost;
    }

    @Override
    public void onContainerClosed(@Nonnull EntityPlayer player) {
        super.onContainerClosed(player);

        if (!this.world.isRemote)
            this.clearContainer(player, this.world, this.inputSlots);
    }

    @Override
    public void addListener(@Nonnull IContainerListener listener) {
        super.addListener(listener);
        listener.sendWindowProperty(this, 0, this.maximumCost);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void updateProgressBar(int id, int data) {
        if (id == 0) this.maximumCost = data;
    }

    @Override
    public boolean canInteractWith(@Nonnull EntityPlayer player) {
        if (this.world.getBlockState(this.pos).getBlock() != FeatureBloodAnvil.BLOOD_ANVIL) return false;
        return player.getDistanceSq(this.pos.add(0.5, 0.5, 0.5)) <= 64;
    }

    @Override @Nonnull
    public ItemStack transferStackInSlot(EntityPlayer player, int index) {
        ItemStack stackCopy = ItemStack.EMPTY;
        Slot clickedSlot = this.inventorySlots.get(index);

        if (clickedSlot != null && clickedSlot.getHasStack()) {
            ItemStack stackOriginal = clickedSlot.getStack();
            stackCopy = stackOriginal.copy();

            if (index == 2) { //Clicked on Output
                //Can't move to inventory
                if (!this.mergeItemStack(stackOriginal, 3, 39, true)) return ItemStack.EMPTY;

                //Useless
                clickedSlot.onSlotChange(stackOriginal, stackCopy);
            } else if (index > 2) { // Clicked on inventory/hotbar
                //cant move to inputs
                if (index < 39 && !this.mergeItemStack(stackOriginal, 0, 2, false)) return ItemStack.EMPTY;
            } else if (!this.mergeItemStack(stackOriginal, 3, 39, false)) return ItemStack.EMPTY; // can't move inputs to inventory

            if (stackOriginal.isEmpty()) clickedSlot.putStack(ItemStack.EMPTY); //moved everything?
            else clickedSlot.onSlotChanged();

            if (stackOriginal.getCount() == stackCopy.getCount()) return ItemStack.EMPTY; //Didn't move anything?

            clickedSlot.onTake(player, stackOriginal);
        }

        return stackCopy;
    }

    private static boolean areAllEnchantmentsCompatible(Enchantment ench, Set<Enchantment> toCompare) {
        return toCompare
                .stream()
                .filter(((Predicate<Enchantment>) ench::equals).negate())
                .allMatch(ench::isCompatibleWith);
    }
}
