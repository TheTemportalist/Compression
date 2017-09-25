package com.temportalist.compression.common.config;

import com.google.common.collect.Lists;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;
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

import java.io.File;
import java.util.List;

@Mod.EventBusSubscriber
public class Config {

    public static final String CATEGORY_THREADS = "threads";

    @SubscribeEvent
    public static void onConfigurationChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        Compression.main.config.onChanged(event);
    }

    private Configuration mcConfig;
    public Threads.Blacklist blacklist;

    public Config(File directory) {
        this.mcConfig = new Configuration(new File(directory.getPath(), "compression.cfg"));
    }

    public void onChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID() == Compression.MOD_ID ) {
            this.syncConfig();
            if (this.mcConfig.hasChanged()) {
                this.mcConfig.save();
            }
        }
    }

    public void initPre() {
        this.blacklist = new Threads.Blacklist();
        this.syncConfig();
    }

    public void syncConfig() {

        for (EnumEffect effect : EnumEffect.values()) {
            effect.getConfig(this, Configuration.CATEGORY_GENERAL);
        }

        for (Threads.Config threadConfig : Threads.Config.values()) {
            threadConfig.getConfig(this, Config.CATEGORY_THREADS);
        }

        Threads.fetch();

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
    }

}
