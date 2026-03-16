package enchantmentcontrol.bloodanvil;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiBloodAnvil extends GuiContainer {

    private static final ResourceLocation BLOOD_ANVIL_RESOURCE = new ResourceLocation("enchantmentcontrol:textures/gui/container/blood_anvil.png");
    private final ContainerBloodAnvil bloodAnvil;
    private final InventoryPlayer playerInventory;

    public GuiBloodAnvil(InventoryPlayer inventory, World world) {
        super(new ContainerBloodAnvil(inventory, world));
        this.playerInventory = inventory;
        this.bloodAnvil = (ContainerBloodAnvil) this.inventorySlots;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.fontRenderer.drawString(I18n.format("gui.blood_anvil.name"), 60, 6, 0x00404040);

        if (this.bloodAnvil.maximumCost > 0) {
            int textColor = 0x0080FF20;
            boolean hasValidOutput = true;
            String s = I18n.format("gui.blood_anvil.cost", this.bloodAnvil.maximumCost);

            if (!this.bloodAnvil.getSlot(2).getHasStack())
                hasValidOutput = false;
            else if (!this.bloodAnvil.getSlot(2).canTakeStack(this.playerInventory.player))
                textColor = 0x00FF6060;

            if (hasValidOutput) {
                int shadowColor = 0xFF000000 | (textColor & 0x00FCFCFC) >> 2 | textColor & 0xFF000000;
                int k = this.xSize - 8 - this.fontRenderer.getStringWidth(s);

                if (this.fontRenderer.getUnicodeFlag()) {
                    drawRect(k - 3, 65, this.xSize - 7, 77, 0xFF000000);
                    drawRect(k - 2, 66, this.xSize - 8, 76, 0xFF3B3B3B);
                } else {
                    this.fontRenderer.drawString(s, k, 68, shadowColor);
                    this.fontRenderer.drawString(s, k + 1, 67, shadowColor);
                    this.fontRenderer.drawString(s, k + 1, 68, shadowColor);
                }

                this.fontRenderer.drawString(s, k, 67, textColor);
            }
        }

        GlStateManager.enableLighting();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(BLOOD_ANVIL_RESOURCE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(i + 59, j + 20, 0, this.ySize + (this.bloodAnvil.getSlot(0).getHasStack() ? 0 : 16), 110, 16);

        if ((this.bloodAnvil.getSlot(0).getHasStack() || this.bloodAnvil.getSlot(1).getHasStack()) &&
                !this.bloodAnvil.getSlot(2).getHasStack())
            this.drawTexturedModalRect(i + 99, j + 45, this.xSize, 0, 28, 21);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}
