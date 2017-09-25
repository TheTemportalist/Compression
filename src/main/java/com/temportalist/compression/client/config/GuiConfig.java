package com.temportalist.compression.client.config;

import com.temportalist.compression.common.Compression;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class GuiConfig extends net.minecraftforge.fml.client.config.GuiConfig {

    public GuiConfig(GuiScreen parentScreen) {
        super(parentScreen, generateConfigElements(),
                Compression.MOD_ID,
                false, false,
                Compression.MOD_NAME
        );
    }

    public static List<IConfigElement> generateConfigElements() {
        List<IConfigElement> elements = new ArrayList<>();
        Compression.main.config.populateConfigElements(elements);
        return elements;
    }

}
