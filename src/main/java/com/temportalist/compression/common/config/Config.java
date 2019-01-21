package com.temportalist.compression.common.config;

import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.effects.EnumEffect;
import com.temportalist.compression.common.init.CompressedStack;
import com.temportalist.compression.common.lib.EnumTier;
import com.temportalist.compression.common.threads.Threads;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.*;
import java.util.*;

@Mod.EventBusSubscriber
public class Config {

    static final String CATEGORY_THREADS = "threads";
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class GreyList
    {

        private File file;
        private Set<String> items, blocks;

        GreyList(File file) {
            this.file = file;
            this.items = new HashSet<>();
            this.blocks = new HashSet<>();
        }

        void merge(Threads.GreyList blacklist) {
            this.items.addAll(Arrays.asList(blacklist.items));
            this.blocks.addAll(Arrays.asList(blacklist.blocks));
            Compression.main.config.save();
        }

        public boolean containsAny(boolean isBlock, String... names) {
            Set<String> registry = isBlock ? this.blocks : this.items;
            for (String name : names) {
                if (registry.contains(name)) return true;
            }
            return false;
        }

        public boolean containsAny(String... names) {
            return this.containsAny(false, names) || this.containsAny(true, names);
        }

        public void clear()
        {
            this.items.clear();
            this.blocks.clear();
        }

        void read() {
            try {
                Reader fileIn = new FileReader(this.file);
                Threads.GreyList list = GSON.fromJson(fileIn, Threads.GreyList.class);
                fileIn.close();
                this.merge(list);
            }
            catch (Exception e) {
                //e.printStackTrace();
            }

        }

        void save() {
            try {
                Threads.GreyList list = new Threads.GreyList();
                list.items = this.items.toArray(new String[]{});
                list.blocks = this.blocks.toArray(new String[]{});
                String text = GSON.toJson(list, Threads.GreyList.class);
                Writer fileOut = new FileWriter(this.file);
                fileOut.write(text);
                fileOut.close();
            }
            catch (Exception e) {
                Compression.LOGGER.error("Could not write to greylist file");
                Compression.LOGGER.error(e);
            }
        }

        public void addAll(Collection<? extends String> items, Collection<? extends String> blocks)
        {
            this.items.addAll(items);
            this.blocks.addAll(blocks);
        }

    }

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Compression.main.config.onChanged(event);
    }

    public static String CATEGORY_TIERS = "tiers";

    private Configuration mcConfig;
    public GreyList blacklist, whitelist;
    public boolean blacklistEnabled, whitelistEnabled;

    public NonNullList<ItemStack> compressableItems, compressableBlocks;

    public GreyList compressables;

    public Config(File directory) {
        this.mcConfig = new Configuration(new File(directory.getPath(), "compression.cfg"));
        this.blacklist = new GreyList(new File(directory.getPath(), "compression-blacklist.json"));
        this.whitelist = new GreyList(new File(directory.getPath(), "compression-whitelist.json"));
        this.compressableItems = NonNullList.create();
        this.compressableBlocks = NonNullList.create();
        this.compressables = new GreyList(new File(directory.getPath(), "compression-valid.json"));
        this.blacklistEnabled = true;
        this.whitelistEnabled = false;
    }

    public void onChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Compression.MOD_ID)) {
            this.syncConfig();
            this.save();
        }
    }

    public void initPre() {
        this.syncConfig();
    }

    public void syncConfig() {

        this.blacklistEnabled = this.get(Configuration.CATEGORY_GENERAL, "blacklistEnabled",
                "Enable the blacklsit (compression-blacklist.json)",
                this.blacklistEnabled
        ).getBoolean(true);

        this.whitelistEnabled = this.get(Configuration.CATEGORY_GENERAL, "whitelistEnabled",
                "Enable the whitelist (compression-whitelist.json)",
                this.whitelistEnabled
        ).getBoolean(false);

        for (EnumTier tier : EnumTier.values())
        {
            tier.getConfig(this, CATEGORY_TIERS);
        }

        for (EnumEffect effect : EnumEffect.values()) {
            effect.getConfig(this, Configuration.CATEGORY_GENERAL);
        }

        for (Threads.Config threadConfig : Threads.Config.values()) {
            threadConfig.getConfig(this, Config.CATEGORY_THREADS);
        }

        this.blacklist.read();
        this.whitelist.read();

        Compression.LOGGER.info("[Compression] whitelist");
        for (String str : this.whitelist.blocks)
        {
            Compression.LOGGER.info(str);
        }
        for (String str : this.whitelist.items)
        {
            Compression.LOGGER.info(str);
        }

        Compression.LOGGER.info("[Compression] blacklist");
        for (String str : this.blacklist.blocks)
        {
            Compression.LOGGER.info(str);
        }
        for (String str : this.blacklist.items)
        {
            Compression.LOGGER.info(str);
        }

        Threads.fetch();

    }

    public void save() {
        if (this.mcConfig.hasChanged()) {
            this.mcConfig.save();
        }
        if (!this.blacklist.file.exists())
            this.blacklist.save();
        if (!this.whitelist.file.exists())
            this.whitelist.save();

        if (this.compressableItems != null && this.compressableBlocks != null)
        {
            this.compressables.clear();

            NonNullList<String> itemNames = NonNullList.create();
            for (ItemStack stack : this.compressableItems)
            {
                String name = CompressedStack.getNameOf(stack, true, true);
                if (name != null) itemNames.add(name);
            }
            NonNullList<String> blockNames = NonNullList.create();
            for (ItemStack stack : this.compressableBlocks)
            {
                String name = CompressedStack.getNameOf(stack, true, true);
                if (name != null) blockNames.add(name);
            }
            this.compressables.addAll(itemNames, blockNames);

            this.compressables.save();
        }
    }

    public void setBlacklist(Threads.GreyList blacklist) {
        this.blacklist.merge(blacklist);
    }

    public <T> Property get(String category, String name, String comment, T value) {
        if (value instanceof Boolean) {
            return this.mcConfig.get(category, name, (Boolean) value, comment);
        } else if (value instanceof Integer) {
            return this.mcConfig.get(category, name, (Integer) value, comment);
        } else if (value instanceof Double) {
            return this.mcConfig.get(category, name, (Double) value, comment);
        } else if (value instanceof String) {
            return this.mcConfig.get(category, name, (String) value, comment);
        } else {
            return null;
        }
    }

    public <T> Property get(String category, String name, String comment, T[] value) {
        if (value instanceof Boolean[]) {
            return this.mcConfig.get(category, name, Booleans.toArray(Lists.newArrayList((Boolean[]) value)), comment);
        } else if (value instanceof Integer[]) {
            return this.mcConfig.get(category, name, Ints.toArray(Lists.newArrayList((Integer[]) value)), comment);
        } else if (value instanceof Double[]) {
            return this.mcConfig.get(category, name, Doubles.toArray(Lists.newArrayList((Double[]) value)), comment);
        } else if (value instanceof String[]) {
            return this.mcConfig.get(category, name, (String[]) value, comment);
        } else {
            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    public void populateConfigElements(List<IConfigElement> elements) {
        elements.add(new ConfigElement(this.mcConfig.getCategory(
                Configuration.CATEGORY_GENERAL
        )));
        elements.add(new ConfigElement(this.mcConfig.getCategory(
                Config.CATEGORY_THREADS
        )));
    }

}
