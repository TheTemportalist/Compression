package com.temportalist.compression.client;

import com.temportalist.compression.common.init.CBlocks;
import com.temportalist.origin.api.common.utility.WorldHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

/**
 * @author TheTemportalist  6/19/15
 */
public class RenderItemCompressed implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		if (WorldHelper.isBlock(CBlocks.getInnerStack(item).getItem()))
			return helper != ItemRendererHelper.INVENTORY_BLOCK;
		else {
			return type != ItemRenderType.INVENTORY && helper != ItemRendererHelper.BLOCK_3D
					&& helper != ItemRendererHelper.ENTITY_ROTATION
					&& helper != ItemRendererHelper.ENTITY_BOBBING;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();

		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON)
			GL11.glTranslated(0.5, 0.5, 0.5);
		else if (type == ItemRenderType.EQUIPPED)
			GL11.glTranslated(0.5, 0.5, 0.5);

		RenderBlockCompressed.renderItem(type, item, data);

		GL11.glPopMatrix();
	}

}
