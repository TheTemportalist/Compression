package temportalist.compression.main.common.lib;

import net.minecraft.block.properties.PropertyBool;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.property.IUnlistedProperty;
import temportalist.origin.api.common.helper.Names;

/**
 * Created by TheTemportalist on 4/15/2016.
 *
 * @author TheTemportalist
 */
public class BlockProperties {

	public static final IUnlistedProperty<Long> LONG_UN = new IUnlistedProperty<Long>() {

		@Override
		public String getName() {
			return "Long";
		}

		@Override
		public boolean isValid(Long value) {
			return true;
		}

		@Override
		public Class<Long> getType() {
			return Long.class;
		}

		@Override
		public String valueToString(Long value) {
			return value.toString();
		}

	};

	public static final IUnlistedProperty<ItemStack> ITEMSTACK_UN = new IUnlistedProperty<ItemStack>() {

		@Override
		public String getName() {
			return "ItemStack";
		}

		@Override
		public boolean isValid(ItemStack value) {
			return true;
		}

		@Override
		public Class<ItemStack> getType() {
			return ItemStack.class;
		}

		@Override
		public String valueToString(ItemStack value) {
			return Names.getName(value);
		}

	};

}
