package com.temportalist.compression.common.config;

import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.effects.EnumEffect;
import com.temportalist.compression.common.threads.Threads;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber
public class Config {

    static final String CATEGORY_THREADS = "threads";
    static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static class Blacklist {

        private File file;
        private Set<String> items, blocks;

        Blacklist(File file) {
            this.file = file;
            this.items = new HashSet<>();
            this.blocks = new HashSet<>();
        }

        void merge(Threads.Blacklist blacklist) {
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

        void read() {
            this.items.clear();
            this.blocks.clear();

            try {
                Reader fileIn = new FileReader(this.file);
                Threads.Blacklist blacklist = GSON.fromJson(fileIn, Threads.Blacklist.class);
                fileIn.close();
                this.merge(blacklist);
            }
            catch (Exception e) {
                //e.printStackTrace();
            }

        }

        void save() {
            try {
                Threads.Blacklist list = new Threads.Blacklist();
                list.items = this.items.toArray(new String[]{});
                list.blocks = this.blocks.toArray(new String[]{});
                String text = GSON.toJson(list, Threads.Blacklist.class);
                Writer fileOut = new FileWriter(this.file);
                fileOut.write(text);
                fileOut.close();
            }
            catch (Exception e) {
                Compression.LOGGER.error("Could not write to blacklist file");
                Compression.LOGGER.error(e);
            }
        }

    }

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Compression.main.config.onChanged(event);
    }

    private Configuration mcConfig;
    public Blacklist blacklist;

    public Config(File directory) {
        this.mcConfig = new Configuration(new File(directory.getPath(), "compression.cfg"));
        this.blacklist = new Blacklist(new File(directory.getPath(), "compression-blacklist.json"));

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

        for (EnumEffect effect : EnumEffect.values()) {
            effect.getConfig(this, Configuration.CATEGORY_GENERAL);
        }

        for (Threads.Config threadConfig : Threads.Config.values()) {
            threadConfig.getConfig(this, Config.CATEGORY_THREADS);
        }

        this.blacklist.read();
        Threads.fetch();

    }

    public void save() {
        if (this.mcConfig.hasChanged()) {
            this.mcConfig.save();
        }
        this.blacklist.save();
    }

    public void setBlacklist(Threads.Blacklist blacklist) {
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
