package com.temportalist.compression.common.threads;

import com.google.gson.Gson;
import com.temportalist.compression.common.Compression;
import net.minecraftforge.common.config.Property;
import org.apache.commons.io.IOUtils;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.function.Consumer;

public class Threads {

    public enum Config {

        BLACKLIST(
                "fetch-blacklist",
                "Set to true to recompile the blacklist of items/blocks",
                "https://raw.githubusercontent.com/TheTemportalist/Compression/1.12.1/STATIC/blacklist.json",
                Threads::loadBlacklist
        );

        private final String name, desc;
        private Property property;
        private final String url;
        private final Consumer<String> onLoad;

        Config(String name, String description, String url, Consumer<String> onLoad) {
            this.name = name;
            this.desc = description;
            this.url = url;
            this.onLoad = onLoad;
        }

        public void getConfig(com.temportalist.compression.common.config.Config config, String category) {
            this.property = config.get(category, this.name, this.desc, true);
        }

        public boolean shouldFetch() {
            return this.property.getBoolean();
        }

        public void tryFetch() {
            if (!this.shouldFetch()) return;
            this.property.set(false);

            new Thread( () -> {
                String content;
                try {
                    Reader fileIn = new InputStreamReader(new URL(this.url).openStream());
                    content = IOUtils.toString(new URL(this.url), Charset.defaultCharset());
                    fileIn.close();

                    if (!content.isEmpty()) {
                        this.onLoad.accept(content);
                    }
                }
                catch (Exception e) {
                    Compression.LOGGER.error(e);
                    Compression.LOGGER.error(e);
                }
            }).start();

        }

    }

    static final Gson GSON = new Gson();

    public static void fetch() {
        // Should occur AFTER configuration load

        for (Config thread : Config.values()) {
            thread.tryFetch();
        }

    }

    public static class GreyList
    {
        public String[] items;
        public String[] blocks;
    }

    static void loadBlacklist(String file) {
        try {
            Compression.main.config.setBlacklist(GSON.fromJson(file, GreyList.class));

            Compression.LOGGER.info("Loaded GreyList configuration");
        }
        catch (Exception e) {
            Compression.LOGGER.error("Encountered an error loading blacklist from remote");
            Compression.LOGGER.error(e);
            Compression.LOGGER.error(file);
        }
    }

}
