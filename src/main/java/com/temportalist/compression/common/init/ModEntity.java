package com.temportalist.compression.common.init;

import com.temportalist.compression.common.Compression;
import com.temportalist.compression.common.entity.EntityCompressed;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntity {

    public void initPre() {
        EntityRegistry.registerModEntity(new ResourceLocation(Compression.MOD_ID, "compressedEntity"),
                EntityCompressed.class, "Compressed", 0, Compression.main,
                64, 20, true
        );
    }

}
