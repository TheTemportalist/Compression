package temportalist.compression.main.common.init;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.gen.structure.StructureStrongholdPieces;

/**
 * Created by TheTemportalist on 5/19/2016.
 *
 * @author TheTemportalist
 */
public class EnumHelperTemp {

	static Class<?>[][] commonTypes = {
		{EnumAction.class},
		{ItemArmor.ArmorMaterial.class, String.class, int.class, int[].class, int.class, SoundEvent.class, float.class},
		{EntityPainting.EnumArt.class, String.class, int.class, int.class, int.class, int.class},
		{EnumCreatureAttribute.class},
		{EnumCreatureType.class, Class.class, int.class, Material.class, boolean.class, boolean.class},
		{StructureStrongholdPieces.Stronghold.Door.class},
		{EnumEnchantmentType.class},
		{BlockPressurePlate.Sensitivity.class},
		{RayTraceResult.Type.class},
		{EnumSkyBlock.class, int.class},
		{EntityPlayer.SleepResult.class},
		{Item.ToolMaterial.class, int.class, int.class, float.class, float.class, int.class},
		{EnumRarity.class, TextFormatting.class, String.class}
	};

}
