package temportalist.compression.main.common.init

import net.minecraft.init.Items
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.item.ItemStack
import net.minecraft.util.{ResourceLocation, SoundEvent}
import net.minecraftforge.common.util.EnumHelper
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.item.{ItemCompressed, ItemDenseArmor}
import temportalist.compression.main.common.lib.EnumTier
import temportalist.origin.foundation.common.registers.ItemRegister

/**
  *
  * Created by TheTemportalist on 4/14/2016.
  *
  * @author TheTemportalist
  */
object ModItems extends ItemRegister {

	var item: ItemCompressed = _

	var armorTypes: Array[EntityEquipmentSlot] = _
	var leatherDense: ArmorMaterial = _
	var leatherDenseArmor: Array[ItemDenseArmor] = _

	override def register(): Unit = {
		this.item = new ItemCompressed()

		this.armorTypes = Array[EntityEquipmentSlot](
			EntityEquipmentSlot.FEET, EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.CHEST, EntityEquipmentSlot.HEAD)

		val leatherEquipRL = new ResourceLocation("item.armor.equip_leather")
		this.leatherDense = EnumHelper.addArmorMaterial(
			"DENSELEATHER",
			Compression.getModId + ":denseleather",
			50,
			Array[Int](2, 8, 10, 0),
			0,
			SoundEvent.REGISTRY.getObject(leatherEquipRL),
			0
		)

		this.leatherDenseArmor = new Array[ItemDenseArmor](4)
		for (slot <- this.armorTypes)
			this.leatherDenseArmor(slot.getIndex) = new ItemDenseArmor(slot, this.leatherDense)

	}

	override def registerCrafting(): Unit = {

		/*
		Compression.log("Loading compressed recipes for Items...")

		for (any <- JavaConversions.asScalaIterator(Item.REGISTRY.iterator())) {
			if (!any.isInstanceOf[ItemBlock]) Recipes.tryAddRecipes(new ItemStack(any))
		}
		*/

		// ~~~~~ Armor

		val leather = new ItemStack(Items.LEATHER)
		val armorComponent = Compressed.create(leather, tier = EnumTier.NONUPLE)
		Map[EntityEquipmentSlot, (String, String, String)] (
			EntityEquipmentSlot.HEAD    -> ("iii", "i i", "   "),
			EntityEquipmentSlot.CHEST   -> ("i i", "iii", "iii"),
			EntityEquipmentSlot.LEGS    -> ("iii", "i i", "i i"),
			EntityEquipmentSlot.FEET    -> ("   ", "i i", "i i")
		).foreach(set => {
			GameRegistry.addRecipe(new ItemStack(this.getArmorDense(set._1)),
				set._2._1, set._2._2, set._2._3,
				Character.valueOf('i'), armorComponent)
		})

	}

	def getArmorDense(slot: EntityEquipmentSlot): ItemDenseArmor = this.leatherDenseArmor(slot.getIndex)

}
