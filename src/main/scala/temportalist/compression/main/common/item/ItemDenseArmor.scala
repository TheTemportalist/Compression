package temportalist.compression.main.common.item

import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.EntityEquipmentSlot
import net.minecraft.item.{ItemArmor, ItemStack}
import net.minecraft.item.ItemArmor.ArmorMaterial
import net.minecraft.potion.{Potion, PotionEffect}
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import temportalist.compression.main.common.Compression
import temportalist.compression.main.common.init.ModItems

/**
  *
  * Created by TheTemportalist on 4/16/2016.
  *
  * @author TheTemportalist
  */
class ItemDenseArmor(slot: EntityEquipmentSlot, material: ArmorMaterial)
		extends ItemArmor(material, slot.getIndex, slot) {

	private val name = this.getClass.getSimpleName + "_" + slot.getName
	private val mod = Compression
	this.setRegistryName(this.mod.getModId, this.name)
	this.setUnlocalizedName(this.mod.getModId + ":" + this.name)
	GameRegistry.register(this)
	val slowness = Potion.getPotionFromResourceLocation("slowness")

	override def onArmorTick(world: World, player: EntityPlayer, itemStack: ItemStack): Unit = {
		if (itemStack.getItem == ModItems.getArmorDense(EntityEquipmentSlot.CHEST)) {
			val head = ItemDenseArmor.isClothed(player, EntityEquipmentSlot.HEAD)
			val legs = ItemDenseArmor.isClothed(player, EntityEquipmentSlot.LEGS)
			val feet = ItemDenseArmor.isClothed(player, EntityEquipmentSlot.FEET)
			if (!(head && legs && feet)) return
			player.addPotionEffect(new PotionEffect(
				slowness, 10, 4 // jabba has 4
			))
		}
	}

	override def getColor(stack: ItemStack): Int = 10511680

}
object ItemDenseArmor {

	def isClothed(player: EntityPlayer, slot: EntityEquipmentSlot): Boolean = {
		val stack = player.getItemStackFromSlot(slot)
		stack != null && stack.getItem == ModItems.getArmorDense(slot)
	}

	def getClothedCount(entity: EntityPlayer): Int = {
		var count = 0
		for (armorSlot <- ModItems.armorTypes)
			entity.getItemStackFromSlot(armorSlot) match {
				case stack: ItemStack =>
					if (stack.getItem == ModItems.getArmorDense(armorSlot)) count += 1
				case _ =>
			}
		count
	}

}
