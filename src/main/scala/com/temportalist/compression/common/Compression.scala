package com.temportalist.compression.common

import java.util

import com.temportalist.compression.common.entity.EntityItemCompressed
import com.temportalist.compression.common.init.{CBlocks, CEntity, CItems}
import com.temportalist.compression.common.item.ICompressed
import com.temportalist.compression.common.packets.{PacketDropFullStack, PacketUpdateHeldSize}
import com.temportalist.compression.common.recipe.{RecipeCompress, RecipeDeCompress, RecipeDynamic, RecipeRefill}
import com.temportalist.origin.api.common.lib.V3O
import com.temportalist.origin.api.common.proxy.IProxy
import com.temportalist.origin.api.common.resource.{EnumResource, IModDetails, IModResource}
import com.temportalist.origin.api.common.utility.{Scala, Stacks}
import com.temportalist.origin.foundation.common.IMod
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.{Mod, SidedProxy}
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.Entity
import net.minecraft.entity.item.EntityItem
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Items
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.util.AxisAlignedBB
import net.minecraft.world.World
import net.minecraftforge.event.entity.item.ItemTossEvent
import net.minecraftforge.event.entity.player.EntityItemPickupEvent
import net.minecraftforge.oredict.RecipeSorter
import net.minecraftforge.oredict.RecipeSorter.Category

/**
 *
 *
 * @author  TheTemportalist  6/18/15
 */
@Mod(modid = Compression.MODID, name = Compression.MODNAME, version = Compression.VERSION,
	modLanguage = "scala",
	//guiFactory = Compression.clientProxy,
	dependencies = "" //required-after:origin@[6,);"
)
object Compression extends IMod with IModResource {

	final val MODID = "compression"
	final val MODNAME = "Compression"
	final val VERSION = "@MOD_VERSION@"
	final val clientProxy = "com.temportalist.compression.client.ProxyClient"
	final val serverProxy = "com.temportalist.compression.server.ProxyServer"

	override def getDetails: IModDetails = this

	override def getModid: String = this.MODID

	override def getModName: String = this.MODNAME

	override def getModVersion: String = this.VERSION

	@SidedProxy(clientSide = this.clientProxy, serverSide = this.serverProxy)
	var proxy: IProxy = null

	/**
	 * The tab for all the Compressed blocks
	 * Entries are added based on a selector and fetches all
	 * possible entries from the registered blocks/items
	 */
	val tab = new CreativeTabs(Compression.MODID) {
		override def getTabIconItem: Item = Items.stick
	}

	@Mod.EventHandler
	def pre(event: FMLPreInitializationEvent): Unit = {
		super.preInitialize(this, event, this.proxy, Options, CBlocks, CItems, CEntity)

		this.registerPackets(classOf[PacketUpdateHeldSize], classOf[PacketDropFullStack])

		RecipeSorter.register("compress", classOf[RecipeCompress], Category.SHAPELESS, "")
		RecipeSorter.register("decompress", classOf[RecipeDeCompress], Category.SHAPELESS, "")
		RecipeSorter.register("dynamic", classOf[RecipeDynamic], Category.SHAPELESS, "")
		RecipeSorter.register("refill", classOf[RecipeRefill], Category.SHAPELESS, "")

	}

	@Mod.EventHandler
	def init(event: FMLInitializationEvent): Unit = super.initialize(event, this.proxy)

	@Mod.EventHandler
	def post(event: FMLPostInitializationEvent): Unit = {
		super.postInitialize(event, this.proxy)
		// construct blocks & items compressed
		CBlocks.constructCompressables(true)
		CBlocks.constructCompressables(false)
		// resources
		this.setResource("gui_creative", this.loadResource(EnumResource.GUI, "stack_creative.png"))
		this.setResource("gui_survival", this.loadResource(EnumResource.GUI, "stack_survival.png"))
	}

	@SubscribeEvent
	def onItemPickUp(event: EntityItemPickupEvent): Unit = {
		val tierMin = Options.poolPlayerTier
		if (tierMin < 1) return

		val player = event.entityPlayer
		val entStack = event.item.getEntityItem
		if (player == null || entStack == null) return

		Scala.foreach(player.inventory, (slot: Int, stack: ItemStack) => {
			if (stack != null) stack.getItem match {
				case compressed: ICompressed =>
					if (CBlocks.getStackTier(stack) >= tierMin) {
						if (this.shouldCompressedPickUpStack(stack, entStack)) {
							val amountToAdd = if (this.isCompressedStack(entStack))
								CBlocks.getInnerSize(entStack)
							else entStack.stackSize
							if (CBlocks.canAddToStack(stack, amountToAdd)) {
								val newStack = stack.copy()
								CBlocks.addToInnerSize(newStack, amountToAdd)
								player.inventory.setInventorySlotContents(slot, newStack)

								event.setCanceled(true)
								event.item.setDead()

								return
							}
						}
					}
				case _ =>
			}
		}: Unit)
	}

	def shouldCompressedPickUpStack(compressed: ItemStack, other: ItemStack): Boolean = {
		(Compression.isStackCompressedAndMatches(other, compressed) &&
				CBlocks.getStackTier(other) < CBlocks.getStackTier(compressed)) ||
				Stacks.doStacksMatch(CBlocks.getInnerStack(compressed), other)
	}

	def isStackCompressedAndMatches(thatStack: ItemStack, thisStack: ItemStack): Boolean = {
		if (thatStack == null && thisStack == null) true
		else if (thatStack == null || thisStack == null) false
		else if (this.isCompressedStack(thatStack)) {
			Stacks.doStacksMatch(CBlocks.getInnerStack(thisStack), CBlocks.getInnerStack(thatStack))
		}
		else false
	}

	def isCompressedStack(stack: ItemStack): Boolean = {
		stack != null &&
				(stack.getItem == CBlocks.compressedItem || stack.getItem == CItems.compressed)
	}

	def pullEntityTowards(entityBeingPulled: Entity, pos: V3O, motion: V3O): Unit = {
		var distX = pos.x - entityBeingPulled.posX
		var distY = pos.y - entityBeingPulled.posY
		var distZ = pos.z - entityBeingPulled.posZ

		val distance = Math.sqrt(distX * distX + distY * distY + distZ * distZ) * 2

		distX = distX / distance + motion.x / 2
		distY = distY / distance + motion.y / 2
		distZ = distZ / distance + motion.z / 2

		entityBeingPulled.motionX = distX
		entityBeingPulled.motionY = distY
		entityBeingPulled.motionZ = distZ
		entityBeingPulled.isAirBorne = true

		if (entityBeingPulled.isCollidedHorizontally) {
			entityBeingPulled.motionY += 1
		}

		if (entityBeingPulled.worldObj.rand.nextInt(20) == 0) {
			val pitch = 0.85f - entityBeingPulled.worldObj.rand.nextFloat() * 3f / 10f
			entityBeingPulled.worldObj.playSoundEffect(
				entityBeingPulled.posX, entityBeingPulled.posY, entityBeingPulled.posZ,
				"mob.endermen.portal", 0.6f, pitch)
		}
	}

	def onCompressedEntityUpdate(entity: EntityItemCompressed, compressed: ItemStack): Unit = {
		val closeBounds: AxisAlignedBB = entity.boundingBox.expand(0.25, 0.25, 0.25)
		this.tryToPullItemsCloser(Options.blackHoleTier, entity, entity.getEntityItem,
			entity.boundingBox, entity.worldObj, new V3O(entity),
			new V3O(entity.motionX, entity.motionY, entity.motionZ),
			(e: EntityItem, stack: ItemStack) => {
				if (e.boundingBox.intersectsWith(closeBounds)) {
					val amountToAdd: Long = if (Compression.isCompressedStack(stack))
						CBlocks.getInnerSize(stack)
					else stack.stackSize
					if (CBlocks.canAddToStack(compressed, amountToAdd)) {
						val newThisStack: ItemStack = compressed.copy
						CBlocks.addToInnerSize(newThisStack, amountToAdd)
						entity.setEntityItemStack(newThisStack)
						e.setDead()
						true
					}
					else false
				}
				else false
			}: Boolean)
	}

	/**
	 * Loops through surrounding EntityItem's
	 * @param stack The compressed stack
	 * @param boundingBox The bounding box to be expanded upon
	 * @param world The world
	 * @param pos A vector at the main point (the one things will be pulled towards)
	 * @param motion A vector showing where the pos vector is moving
	 * @param loopCall If function null or returns false,
	 *                 then items are pulled closer to the pos vec
	 */
	def tryToPullItemsCloser(requiredTier: Int, entity: Entity, stack: ItemStack,
			boundingBox: AxisAlignedBB,
			world: World, pos: V3O, motion: V3O,
			loopCall: (EntityItem, ItemStack) => Boolean): Unit = {
		val thisTier: Int = CBlocks.getStackTier(stack)
		if (thisTier >= requiredTier) {
			val radius = thisTier - requiredTier + 1.5D
			val radiusBounds: AxisAlignedBB = boundingBox.expand(radius, radius, radius)
			world.getEntitiesWithinAABBExcludingEntity(entity, radiusBounds) match {
				case list: util.List[_] =>
					for (i <- 0 until list.size()) {
						list.get(i) match {
							case e: EntityItem =>
								if (e.age >= 10) {
									val entStack = e.getEntityItem
									if (loopCall == null || !loopCall(e, entStack)) {
										if (Compression
												.shouldCompressedPickUpStack(stack, entStack)) {
											Compression.pullEntityTowards(e, pos, motion)
										}
									}
								}
							case _ =>
						}
					}
				case _ =>
			}
		}
	}

	@SubscribeEvent
	def tossEvent(event: ItemTossEvent): Unit = {
		val stack = event.entityItem.getEntityItem
		if (!this.isCompressedStack(stack)) return

		// player has pressed the drop key and is sneaking
		if (event.player.inventory.getItemStack == null && event.player.isSneaking) {
			this.splitAndDropCompressedStack(event.player, stack)
			event.setCanceled(true) // remove the entityitem sent from being dropped
		}

	}

	def splitAndDropCompressedStack(player: EntityPlayer, heldStack: ItemStack,
			dropMaxStack: Boolean = false): Unit = {
		if (!this.isCompressedStack(heldStack)) return

		var newInvStack = heldStack.copy()
		val dropStack = CBlocks.getInnerStack(newInvStack)

		// if we have the potential to drop up to 64 (maxstacksize), then drop as many as possible
		var sizeToDrop: Int = 1
		if (dropMaxStack)
			sizeToDrop = Math.min(dropStack.getMaxStackSize,
				CBlocks.getInnerSize(newInvStack)).toInt
		dropStack.stackSize = sizeToDrop
		CBlocks.decrStackSize(newInvStack, sizeToDrop)

		if (CBlocks.getInnerSize(newInvStack) > 0) {
			if (CBlocks.getInnerSize(newInvStack) == 1) newInvStack = dropStack.copy()
		}
		else newInvStack = null
		// set the player's inv slot to the newInvStack
		player.inventory.setInventorySlotContents(player.inventory.currentItem, newInvStack)
		Stacks.tossItem(dropStack, player)

	}

}
