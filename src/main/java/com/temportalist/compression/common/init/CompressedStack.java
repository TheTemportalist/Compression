package com.temportalist.compression.common.init;

import com.google.common.collect.Lists;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.items.ICompressed;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

public class CompressedStack {

    /**
     * Returns if the stack is a Compressed Stack
     * @param itemStack the ItemStack to check
     * @return true if the stack is qualified as "Compressed"
     */
    public static boolean isCompressed(ItemStack itemStack) {
        return itemStack != null && itemStack != ItemStack.EMPTY && itemStack.getItem() instanceof ICompressed;
    }

    /**
     * Create a Compressed ItemStack (ItemStack containing an ICompressed Item) from a sample stack
     * @param itemStack The sample ItemStack containing the non/un-compressed item
     * @param tier The Enumtier of the compressed ItemStack
     * @return an ItemStack, null if the itemStack is a compressed stack
     */
    public static ItemStack create(ItemStack itemStack, EnumTier tier) {
        // Do not create if the stack is already compressed
        if (CompressedStack.isCompressed(itemStack)) return null;
        else if (tier == null) return CompressedStack.createSampleStack(itemStack);

        // Check if itemstack is an item or block
        boolean isBlock = itemStack.getItem() instanceof ItemBlock;

        // Create the compressed stack
        ItemStack compressed = new ItemStack(isBlock ? ModBlocks.compressed.item : ModItems.compressed, 1, 0);

        // Create the nbt data for the stack
        NBTTagCompound tagCom = new NBTTagCompound();
        // The registry name, formattted as modid:name
        tagCom.setString("name", CompressedStack.getNameOf(itemStack, true, true));
        if (itemStack.hasTagCompound()) tagCom.setTag("sampleTag", itemStack.getTagCompound());
        // The display name of the inner stack, used in rending the text on hover
        tagCom.setString("display", itemStack.getItem().getItemStackDisplayName(itemStack));
        // The tier/size of the stack
        tagCom.setInteger("tier", tier.ordinal());
        compressed.setTagCompound(tagCom);

        // Return
        return compressed;
    }

    /**
     * Get the fully qualified display name for a compressed stack
     * @param itemStack The ItemStack to translate
     * @return "<Tier Name> Compressed <Item Name>" if stack is a Compressed Stack,
     *      the {@link Item#getItemStackDisplayName(ItemStack)} otherwise
     */
    public static String getDisplayNameFor(ItemStack itemStack) {
        if (!CompressedStack.isCompressed(itemStack)) return itemStack.getItem().getItemStackDisplayName(itemStack);
        else {
            return CompressedStack.getTier(itemStack).getName() + " Compressed " + itemStack.getTagCompound().getString("display");
        }
    }

    /**
     * Get the name of the item in a Compressed ItemStack
     * @param itemStack The ItemStack
     * @return The name of the inner sample stack of the Compressed ItemStack, else the {@link ItemStack#getUnlocalizedName()}
     */
    public static String getStackName(ItemStack itemStack) {
        return CompressedStack.isCompressed(itemStack) ? itemStack.getTagCompound().getString("name") : itemStack.getUnlocalizedName();
    }

    /**
     * Get the tier of the Compressed ItemStack
     * @param itemStack The ItemStack
     * @return The {@link EnumTier} of the Compressed ItemStack, else null
     */
    public static EnumTier getTier(ItemStack itemStack) {
        return CompressedStack.isCompressed(itemStack) ? EnumTier.getTier(itemStack.getTagCompound().getInteger("tier")) : null;
    }

    /**
     * Returns the data name of an ItemStack
     * @param itemStack The ItemStack to stringify
     * @param withModid If the name should include the modid
     * @param withMeta If the name should include the metadata
     * @return A String representing the ItemStack in the form of: "<modid>:<name>:<meta>"
     */
    public static String getNameOf(ItemStack itemStack, boolean withModid, boolean withMeta) {
        // Get the registry name of the item
        ResourceLocation registry = itemStack.getItem().getRegistryName();
        // Create a name var
        String name = "";
        // Load in the modid
        if (withModid) {
            name += registry.getResourceDomain() + ":";
        }
        // Load in the name
        name += registry.getResourcePath();
        // Load in the metadata
        if (withMeta) {
            name += ":" + itemStack.getItemDamage();
        }
        // Return
        return name;
    }

    /**
     * Returns a set of qualifiers (modid, name, and metadata) of an Item name
     * (usually generated with {@link CompressedStack#getNameOf(ItemStack, boolean, boolean)})
     * @param name The name to destringify
     * @return A {@link Tuple} of {@link ResourceLocation} and an integer (representing modid:name and metadata respectively)
     */
    private static Tuple<ResourceLocation, Integer> getQualifiers(String name) {

        String modid = "", nameOut;
        // Cache the default metadata value
        int meta = OreDictionary.WILDCARD_VALUE;

        // Check to see if the name has the specified format, else return no modid no metadata
        if (!name.matches("(.*):(.*)")) nameOut = name;
        else {
            // Cache the length of the stringified name
            int endNameIndex = name.length();
            // Check to see if the name matches the full format modid:name:meta
            if (name.matches("(.*):(.*):(.*)")) {
                // Save the end of the modid:name portion
                endNameIndex = name.lastIndexOf(':');
                // Get the metadata from the name
                meta = Integer.parseInt(name.substring(endNameIndex + 1, name.length()));
            }

            // Get the modid portion (the string from start till first ':'
            modid = name.substring(0, name.indexOf(':'));
            // Get the name portion, the area in the middle
            nameOut = name.substring(name.indexOf(':') + 1, endNameIndex);
        }

        // Return
        return new Tuple<>(new ResourceLocation(modid, nameOut), meta);
    }

    /**
     * Constructs an ItemStack from the specified name, generated by {@link CompressedStack#getNameOf(ItemStack, boolean, boolean)}
     * @param name The qualified name in the format of "[<modid>:]<name>[:<meta>]"
     * @return The ItemStack containing 1 item of the specified type, or null if the modid:name was not found
     */
    public static ItemStack createItemStack(String name) {
        return CompressedStack.createItemStack(name, null);
    }

    public static ItemStack createItemStack(String name, NBTTagCompound tagCom) {
        // Gets the qualifying members of the name
        Tuple<ResourceLocation, Integer> qualifiers = CompressedStack.getQualifiers(name);

        // Search for the registry name of the item
        Block block = Block.REGISTRY.getObject(qualifiers.getFirst());
        Item item = Item.REGISTRY.getObject(qualifiers.getFirst());

        ItemStack out = null;

        // If valid block and block can be in an inventory
        if (block != Blocks.AIR && Item.getItemFromBlock(block) != null) {
            out = new ItemStack(block, 1, qualifiers.getSecond());
        }
        // If valid item
        else if (item != null) {
            out = new ItemStack(item, 1, qualifiers.getSecond());
        }
        // Not a valid name

        if (out != null) {
            out.setTagCompound(tagCom);
        }

        return out;
    }

    /**
     * Constructs an IBlockState from the specified name, generated by {@link CompressedStack#getNameOf(ItemStack, boolean, boolean)}
     * @param name The qualified name in the format of "[<modid>:]<name>[:<meta>]"
     * @return The IBlockState of the specified type, or null if the modid:name was not found
     */
    public static IBlockState createState(String name) {
        // Gets the qualifying members of the name
        Tuple<ResourceLocation, Integer> qualifiers = CompressedStack.getQualifiers(name);

        // Search for the registry name of the block
        Block block = Block.REGISTRY.getObject(qualifiers.getFirst());
        // If valid block
        if (block != null) {
            return block.getStateFromMeta(qualifiers.getSecond());
        }
        // Not a valid name
        else {
            return null;
        }
    }

    /**
     * Create a sample of the inner stack of a Compressed ItemStack
     * @param itemStack The Compressed ItemStack
     * @return an ItemStack containing 1 item of the specified type, null if itemStack is not compressed
     */
    public static ItemStack createSampleStack(ItemStack itemStack) {
        return CompressedStack.isCompressed(itemStack) ?
                CompressedStack.createItemStack(CompressedStack.getStackName(itemStack),
                        itemStack.getTagCompound().hasKey("sampleTag") ? itemStack.getTagCompound().getCompoundTag("sampleTag") : null) :
                itemStack.copy();
    }

    /**
     * Create a sample of the inner block of a Compressed ItemStack
     * @param itemStack The Compressed ItemStack
     * @return an IBlockState of the specified type, null if itemStack is not compressed
     */
    public static IBlockState createSampleState(ItemStack itemStack) {
        return CompressedStack.isCompressed(itemStack) ?
                CompressedStack.createState(CompressedStack.getStackName(itemStack)) :
                null;
    }

    public static boolean canCompressItem(ItemStack itemStack) {
        try {
            if (CompressedStack.isCompressed(itemStack) || itemStack == ItemStack.EMPTY) {
                return false;
            }
            else if (itemStack.getItem() instanceof ItemBlock) {
                if (CompressedStack.isInBlackList(itemStack)) return false;
                Block block = Block.getBlockFromItem(itemStack.getItem());
                IBlockState state = block.getStateFromMeta(itemStack.getItemDamage());
                return state.getMaterial().isOpaque() && state.isFullCube() && !state.canProvidePower() &&
                        !block.hasTileEntity(state) && itemStack.getItem().getItemStackLimit(itemStack) > 1;
            }
            else {
                if (CompressedStack.isInBlackList(itemStack)) return false;
                return itemStack.getItem().getItemStackLimit(itemStack) > 1 && !itemStack.hasTagCompound();
            }
        }
        catch (Exception e) {
            Compression.LOGGER.error(e);
            return false;
        }
    }

    public static boolean isInBlackList(ItemStack itemStack) {
        return Compression.main.config.blacklist.containsAny(
                itemStack.getItem() instanceof ItemBlock,
                getNameOf(itemStack, true, true),
                getNameOf(itemStack, true, false),
                getNameOf(itemStack, false, true),
                getNameOf(itemStack, false, false)
        );
    }

    public static List<ItemStack> createStackList(ItemStack sample, long count) {

        // Determine the starting tier - the max tier that can contain the count, but must reach criteria
        EnumTier startTier = EnumTier.getHead();
        // ex: count = 592, startTier = SINGLE(9)
        // ex: 0) SINGLE != DUODEVDECUPLE = true, fits = true
        // ex: 1) DOUBLE != DUODEVDECUPLE = true, fits = true
        // ex: 2) TRIPLE != DUODEVDECUPLE = true, fits = false
        boolean fits = true;
        while (startTier != EnumTier.getTail() && fits) {
            // ex: 0) next = DOUBLE
            // ex: 1) next = TRIPLE
            EnumTier next = startTier.getNext();
            // ex: 0) 81 < 592 = true
            // ex: 1) 729 < 592 = false
            fits = next.getSizeMax() < count;
            if (fits) {
                // 0) startTier = next = DOUBLE
                startTier = next;
            }
        }
        // ex: so singleTier = DOUBLE, which is the largest the tier can get without going over count

        EnumMap<EnumTier, Integer> counts = new EnumMap<>(EnumTier.class);

        // compile the list of tier stacks, according to a starting tier determined
        // ex: count = 7470, startTier = QUADRUPLE
        // ex: 0) QUADRUPLE != null
        // ex: 1) TRIPLE != null
        // ex: 2) DOUBLE != null
        // ex: 3) SINGLE != null
        // ex: 4) null == null
        while (startTier != null) {
            // ex: 0) quantity = 7470 / 6561 = 1, mod = 7470 / 6561 = 909
            // ex: 1) quantity = 909 / 729 = 1, mod = 909 / 729 = 180
            // ex: 2) quantity = 180 / 81 = 2, 180 = 909 / 81 = 18
            // ex: 3) quantity = 18 / 9 = 2, 180 = 18 / 9 = 0
            long quantity = count / startTier.getSizeMax();
            long mod = count % startTier.getSizeMax();
            // Save the quantity for this tier to the map
            // ex: 0) QUADRUPLE => 1
            // ex: 1) TRIPLE => 1
            // ex: 2) DOUBLE => 2
            // ex: 3) SINGLE => 2
            counts.put(startTier, (int) quantity);
            // Prep the next iteration with the remaining amount
            // ex: 0) count = 909
            // ex: 1) count = 180
            // ex: 2) count = 18
            // ex: 3) count = 0
            count = mod;
            // Decrement the tier
            // ex: 0) startTier = TRIPLE
            // ex: 1) startTier = DOUBLE
            // ex: 2) startTier = SINGLE
            // ex: 3) startTier = null
            startTier = EnumTier.getTier(startTier.ordinal() - 1);
        }
        // so counts: QUADRUPLE => 1, TRIPLE => 1, DOUBLE => 2, SINGLE => 2

        List<ItemStack> stacks = new ArrayList<>();

        counts.forEach((EnumTier tier, Integer quantity) -> {
            ItemStack stack = CompressedStack.create(sample, tier);
            stack.setCount(quantity);
            stacks.add(stack);
        });
        if (count > 0) {
            ItemStack left = sample.copy();
            left.setCount((int)count);
            stacks.add(left);
        }

        return stacks;
    }

}
