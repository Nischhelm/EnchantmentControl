package enchantmentcontrol.mixin.vanilla.anvil;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import enchantmentcontrol.config.ConfigHandler;
import enchantmentcontrol.util.AnvilCostUtil;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeHooks;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import shadows.ench.asm.EnchHooks;

import java.util.Map;

@Mixin(ContainerRepair.class)
public abstract class ContainerRepairMixin_RepairCostScaling {//extends Container {
//    @Shadow @Final private IInventory inputSlots;
//    @Shadow @Final private IInventory outputSlot;
//    @Shadow @Final private EntityPlayer player;
//    @Shadow public int maximumCost;
//    @Shadow public int materialCost;
//    @Shadow private String repairedItemName;

    @WrapOperation(
            method = "updateRepairOutput",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;setRepairCost(I)V")
    )
    private void ec_updateRepairOutput_changeRepairCostScaling(
            ItemStack stackLeftCopy, int cost,
            Operation<Void> original,
            @Local(name = "itemstack2") ItemStack stackRight,
            @Local(name = "k") int renameCost,
            @Local(name = "i") int totalCost
    ) {
        int repairCost, useCount;
        int repairCostLeft = stackLeftCopy.getRepairCost();
        int repairCostRight = stackRight.getRepairCost();
        int useCountLeft = AnvilCostUtil.getAnvilCount(stackLeftCopy);
        int useCountRight = AnvilCostUtil.getAnvilCount(stackRight);

        //items from before this mixin got enabled
        if(useCountLeft == 0 && repairCostLeft > 0) {
            useCountLeft = MathHelper.floor(Math.log(repairCostLeft + 1) / Math.log(2));
            AnvilCostUtil.setAnvilCount(stackLeftCopy, useCountLeft);
        }
        if(useCountRight == 0 && repairCostRight > 0) {
            useCountRight = MathHelper.floor(Math.log(repairCostRight + 1) / Math.log(2));
            AnvilCostUtil.setAnvilCount(stackRight, useCountRight);
        }

        repairCost = AnvilCostUtil.combineCosts(repairCostLeft, repairCostRight);
        useCount = AnvilCostUtil.combineCounts(useCountLeft, useCountRight);

        float multi = ConfigHandler.anvil.repairCostScalingFactor;

        if (renameCost != totalCost || renameCost == 0) {
            switch(ConfigHandler.anvil.repairCostScalingType) {
                case CONST: // a
                    repairCost = (int) multi; break;
                case LINEAR: // a * useCount
                    repairCost = (int) (multi * useCount); break;
                case QUADRATIC: // a * useCount^2
                    repairCost = (int) (multi * useCount * useCount); break;
                case EXPONENTIAL: default: // (a ^ useCount) - 1
                    repairCost = MathHelper.ceil(repairCost * multi) + 1; break;
            }
        }

        AnvilCostUtil.setAnvilCount(stackLeftCopy, useCount + 1);
        original.call(stackLeftCopy, repairCost);
    }


            Map<Enchantment, Integer> map = EnchantmentHelper.getEnchantments(itemstack1);
//    public void updateRepairOutput() {
//        LocalBooleanRef sharedRef19 = false;
//        ItemStack itemstack = this.inputSlots.getStackInSlot(0);
//        this.maximumCost = 1;
//        int i = 0;
//        int j = 0;
//        int k = 0;
//
//        if (itemstack.isEmpty()) {
//            this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
//            this.maximumCost = 0;
//        } else {
//            ItemStack itemstack1 = itemstack.copy();
//            ItemStack itemstack2 = this.inputSlots.getStackInSlot(1);
//            //EC ec_updateRepairOutput_saveOriginalMap the result of this call:
//            Map<Enchantment, Integer> map = this.modifyExpressionValue$zzb000$ec_updateRepairOutput_saveOriginalMap(EnchantmentHelper.getEnchantments(itemstack1), sharedRef19);
//            j = j + itemstack.getRepairCost() + (itemstack2.isEmpty() ? 0 : itemstack2.getRepairCost());
//            this.materialCost = 0;
//            boolean flag = false;
//
//            if (!itemstack2.isEmpty()) {
//                if (!ForgeHooks.onAnvilChange(this, itemstack, itemstack2, this.outputSlot, this.repairedItemName, j)) {
//                    return;
//                flag = itemstack2.getItem() == Items.ENCHANTED_BOOK && !ItemEnchantedBook.getEnchantments(itemstack2).isEmpty();
//
//                if (itemstack1.isItemStackDamageable() && itemstack1.getItem().getIsRepairable(itemstack, itemstack2)) {
//                    int l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
//
//                    if (l2 <= 0) {
//                        this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
//                        this.maximumCost = 0;
//                        return;
//                    }
//
//                    int i3;
//
//                    for (i3 = 0; l2 > 0 && i3 < itemstack2.getCount(); ++i3) {
//                        int j3 = itemstack1.getItemDamage() - l2;
//                        itemstack1.setItemDamage(j3);
//                        ++i;
//                        l2 = Math.min(itemstack1.getItemDamage(), itemstack1.getMaxDamage() / 4);
//                    }
//
//                    this.materialCost = i3;
//                } else {
//                    if (!flag && (itemstack1.getItem() != itemstack2.getItem() || !itemstack1.isItemStackDamageable())) {
//                        this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
//                        this.maximumCost = 0;
//                        return;
//                    }
//
//                    if (itemstack1.isItemStackDamageable() && !flag) {
//                        int l = itemstack.getMaxDamage() - itemstack.getItemDamage();
//                        int i1 = itemstack2.getMaxDamage() - itemstack2.getItemDamage();
//                        int j1 = i1 + itemstack1.getMaxDamage() * 12 / 100;
//                        int k1 = l + j1;
//                        int l1 = itemstack1.getMaxDamage() - k1;
//
//                        if (l1 < 0) {
//                            l1 = 0;
//                        }
//
//                        if (l1 < itemstack1.getItemDamage()) // vanilla uses metadata here instead of damage.
//                        {
//                            itemstack1.setItemDamage(l1);
//                            i += 2;
//                        }
//                    }
//
//                    Map<Enchantment, Integer> map1 = EnchantmentHelper.getEnchantments(itemstack2);
//                    boolean flag2 = false;
//                    boolean flag3 = false;
//
//                    for (Enchantment enchantment1 : map1.keySet()) {
//                        if (enchantment1 != null) {
//                            int i2 = map.containsKey(enchantment1) ? ((Integer) map.get(enchantment1)).intValue() : 0;
//                            int j2 = ((Integer) map1.get(enchantment1)).intValue();
//                            j2 = i2 == j2 ? j2 + 1 : Math.max(j2, i2);
//                            boolean flag1 = enchantment1.canApply(itemstack);
//                            //EC
//                            if (this.modifyExpressionValue$zzm000$ec_overrideCanApply(this.player.capabilities.isCreativeMode) || itemstack.getItem() == Items.ENCHANTED_BOOK) {
//                                flag1 = true;
//                            }
//
//                            for (Enchantment enchantment : map.keySet()) {
//                                if (enchantment != enchantment1 && !enchantment1.isCompatibleWith(enchantment)) {
//                                    flag1 = false;
//                                    ++i;
//                                }
//                            }
//
//                            if (!flag1) {
//                                flag3 = true;
//                            } else {
//                                flag2 = true;
//                                //APOTHEOSIS
//                                if (j2 > EnchHooks.getMaxLevel(enchantment1)) {
//                                    j2 = EnchHooks.getMaxLevel(enchantment1);
//                                }
//
//                                map.put(enchantment1, Integer.valueOf(j2));
//                                int k3 = 0;
//
//                                switch (enchantment1.getRarity()) {
//                                    case COMMON:
//                                        k3 = 1;
//                                        break;
//                                    case UNCOMMON:
//                                        k3 = 2;
//                                        break;
//                                    case RARE:
//                                        k3 = 4;
//                                        break;
//                                    case VERY_RARE:
//                                        k3 = 8;
//                                }
//
//                                if (flag) {
//                                    k3 = Math.max(1, k3 / 2);
//                                }
//
//                                i += k3 * j2;
//
//                                if (itemstack.getCount() > 1) {
//                                    i = this.modifyExpressionValue$zzi000$ec_updateRepairOutput_removeCap(40);
//                                }
//                            }
//                        }
//                    }
//
//                    if (flag3 && !flag2) {
//                        this.outputSlot.setInventorySlotContents(0, ItemStack.EMPTY);
//                        this.maximumCost = 0;
//                        return;
//                    }
//                }
//            }
//
//            if (StringUtils.isBlank(this.repairedItemName)) {
//                if (itemstack.hasDisplayName()) {
//                    k = 1;
//                    i += k;
//                    itemstack1.clearCustomName();
//                }
//            } else if (!this.repairedItemName.equals(itemstack.getDisplayName())) {
//                k = 1;
//                i += k;
//                itemstack1.setStackDisplayName(this.repairedItemName);
//            }
//            if (flag && !itemstack1.getItem().isBookEnchantable(itemstack1, itemstack2)) itemstack1 = ItemStack.EMPTY;
//
//            this.maximumCost = j + i;
//
//            if (i <= 0) {
//                itemstack1 = ItemStack.EMPTY;
//            }
//
//            //EC modifies the 40 here
//            if (k == i && k > 0 && this.maximumCost >= this.modifyExpressionValue$zzi000$ec_updateRepairOutput_removeCap(40)) {
//                this.maximumCost = 39;
//            }
//
//                //EC modifies the 40 here
//                if (this.maximumCost >= this.modifyExpressionValue$zzi000$ec_updateRepairOutput_removeCap(40) && !this.player.capabilities.isCreativeMode) {
//                //APOTHEOSIS
//                itemstack1 = itemstack1;
//            }
//
//            if (!itemstack1.isEmpty()) {
//                int k2 = itemstack1.getRepairCost();
//                if (!itemstack2.isEmpty() && k2 < itemstack2.getRepairCost()) {
//                    k2 = itemstack2.getRepairCost();
//                }
//
//                if (k != i || k == 0) {
//                    k2 = k2 * 2 + 1;
//                }
//
//                if (this.wrapWithCondition$zzb000$ec_updateRepairOutput_noAnvilCostIncrease(itemstack1, k2, map, sharedRef19)) {
//                    Operation originalOperation = (var0) -> {
//                        ((ItemStack) var0[0]).setRepairCost((Integer) var0[1]);
//                        return (Void) null;
//                    };
//                    LocalRef<ItemStack> ref22 = itemstack2;
//                    LocalIntRef ref23 = k;
//                    LocalIntRef ref24 = i;
//                    this.wrapOperation$zzk000$ec_updateRepairOutput_changeRepairCostScaling$mixinextras$bridge$17(itemstack1, k2, originalOperation, ref22, ref23, ref24);
//                    i = ref24.dispose();
//                    k = ref23.dispose();
//                    itemstack2 = (ItemStack) ref22.dispose();
//                }
//
//                EnchantmentHelper.setEnchantments(map, itemstack1);
//            }
//
//            this.outputSlot.setInventorySlotContents(0, itemstack1);
//            this.detectAndSendChanges();
//        }
//    }
}
