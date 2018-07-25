package com.temportalist.compression.client;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.ContainerCompressor;
import com.temportalist.compression.common.blocks.TileCompressor;
import com.temportalist.compression.common.network.MessageCompressor;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCompressor extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation(Compression.MOD_ID,"textures/gui/compressor.png");
    /** The player inventory bound to this GUI. */
    private final InventoryPlayer playerInventory;
    private final TileCompressor tileCompressor;
    private GuiButton btnMode;

    public GuiCompressor(InventoryPlayer playerInv, TileCompressor furnaceInv)
    {
        super(new ContainerCompressor(playerInv, furnaceInv));
        this.playerInventory = playerInv;
        this.tileCompressor = furnaceInv;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(this.btnMode = new GuiButton(1, 190, 95, 85, 20, "Compress"));
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if(button.id == 1)
        {
            Compression.main.Network.NETWORK.sendToServer(new MessageCompressor(this.tileCompressor.getPos()));
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.btnMode.displayString = (this.tileCompressor.isDecompressing ? "Dec" : "C") + "ompressing";

        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = this.tileCompressor.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    /**
     * Draws the background layer of this container (behind the items).
     */
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        int l = this.getCookProgressScaled(24);
        this.drawTexturedModalRect(i + 79, j + 34, 176, 14, l + 1, 16);
    }

    private int getCookProgressScaled(int pixels)
    {
        int i = this.tileCompressor.getField(0);
        int j = this.tileCompressor.getField(1);
        //Compression.LOGGER.info("Gui: " + i + "/" + j);
        return j != 0 && i != 0 ? i * pixels / j : 0;
    }

}