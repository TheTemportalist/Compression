package com.temportalist.compression.common.effects;

import com.temportalist.compression.common.Config;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import net.minecraft.item.ItemStack;

public enum EnumEffect {

    // compress like-items on player inventory entry
    COMPRESSOR("Compressor", "Compress like-items as they enter the player's inventory when in the player's inventory", EnumTier.DOUBLE),
    // attract like-items when in player inventory
    MAGNET_I("Magnet I", "Attract like-items to the player when in the player's inventory", EnumTier.TRIPLE),
    // attract all items when in player inventory
    MAGNET_II("Magnet II", "Attract all items to the player when in the player's inventory", EnumTier.QUADRUPLE),
    // attract like-items in world
    ATTRACTION_I("Attraction I", "Attract like-items to the entity when in the world as an entity", EnumTier.TRIPLE),
    // attract all items in world
    ATTRACTION_II("Attraction II", "Attract all items to the entity when in the world as an entity", EnumTier.QUADRUPLE),
    // attract all entities when in world as entity
    ATTRACTION_III("Attraction III", "Attract all entities to the entity when in the world as an entity", EnumTier.HEXTUPLE),
    // eat world when in world as block
    BLACK_HOLE("Black Hole", "Destroy blocks and attract items/entities when in the world as a block", EnumTier.DUODEVDECUPLE),
    // enable potential hearts from consumed energy
    POTENTIAL_ENERGY("Potential Energy", "When a black-hole tier block consumes matter, create potential energy for extra health", true)
    ;

    private String configName, description;
    private EnumTier defaultTier;
    private boolean defaultEnabled;

    private EnumTier value;
    private boolean enabled;

    EnumEffect(String name, String desc, EnumTier tier) {
        this.configName = name;
        this.description = desc;
        this.defaultTier = tier;
        this.defaultEnabled = true;
    }

    EnumEffect(String name, String desc, boolean enabled) {
        this.configName = name;
        this.description = desc;
        this.defaultTier = null;
        this.defaultEnabled = enabled;
    }

    public void getConfig(Config config, String category) {
        this.value = null;
        this.enabled = true;

        if (this.defaultTier != null) {
            int tier = config.get(category, this.configName, this.description,
                    this.defaultTier.ordinal() + 1
            ).getInt();
            this.value = EnumTier.getTier(tier - 1);
        }
        else {
            this.enabled = config.get(category, this.configName, this.description, this.defaultEnabled).getBoolean();
        }
    }

    public EnumTier getTier() {
        return value;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean canDoEffect(ItemStack stack) {
        return this.canDoEffect(CompressedStack.getTier(stack));
    }

    public boolean canDoEffect(EnumTier tier) {
        if (this.getTier() == null) return this.enabled;
        else return this.getTier().lte(tier);
    }

    public static EnumEffect getLowestTier(EnumEffect... effects) {
        EnumEffect effect = null;
        for (EnumEffect effectIter : effects) {
            if (effect == null || effectIter.getTier().lt(effect.getTier()))
                effect = effectIter;
        }
        return effect;
    }

}
