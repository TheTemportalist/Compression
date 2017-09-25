package com.temportalist.compression.common.threads;

import com.google.gson.Gson;
import com.temportalist.compression.common.Compression;

import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
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
        private boolean shouldFetch;
        private final String url;
        private final Consumer<Reader> onLoad;

        Config(String name, String description, String url, Consumer<Reader> onLoad) {
            this.name = name;
            this.desc = description;
            this.shouldFetch = false;
            this.url = url;
            this.onLoad = onLoad;
        }

        public void getConfig(com.temportalist.compression.common.config.Config config, String category) {
            this.shouldFetch = config.get(category, this.name, this.desc, true).getBoolean();
        }

        public void tryFetch() {
            if (!this.shouldFetch) return;
            this.shouldFetch = false;

            new Thread( () -> {
                try {
                    Reader fileIn = new InputStreamReader(new URL(this.url).openStream());
                    this.onLoad.accept(fileIn);
                    fileIn.close();
                }
                catch (Exception e) {
                    Compression.LOGGER.error(e);
                }
            }).start();

        }

    }

    public static final Gson GSON = new Gson();

    public static void fetch() {
        // Should occur AFTER configuration load

        for (Config thread : Config.values()) {
            thread.tryFetch();
        }

    }

    public static class Blacklist {
        private String[] items;
        private String[] blocks;
    }

    public static void loadBlacklist(Reader file) {
        Compression.main.config.blacklist = GSON.fromJson(file, Blacklist.class);
        Compression.LOGGER.info("Loaded Blacklist configuration");
    }

}
