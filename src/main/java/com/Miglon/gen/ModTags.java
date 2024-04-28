package com.Miglon.gen;

import com.Miglon.DeerMod;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;

import static net.minecraft.registry.tag.TagEntry.createTag;

public class ModTags {
    public static class Biomes {
        public static final TagKey<Biome> SPAWNS_ICE_VARIANT_DEERS =
                createTag("spawns_ice_variant_deers");
        public static final TagKey<Biome> SPAWNS_COLD_VARIANT_DEERS =
                createTag("spawns_cold_variant_deers");

        private static TagKey<Biome> createTag(String name) {
            return TagKey.of(RegistryKeys.BIOME, new Identifier(DeerMod.MODID, name));
        }
    }
}
