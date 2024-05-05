package com.Miglon.Blocks;

import com.Miglon.DeerMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {

    public static final Block REINDEER_MOSS = registerBlock("reindeer_moss", (Block) new FlowerbedBlock( FabricBlockSettings.copyOf(Blocks.PINK_PETALS).nonOpaque()));
    private static void  addItemsToNaturalTabItemGroup(FabricItemGroupEntries entries){
        entries.add(REINDEER_MOSS);
    }

    private static Block registerBlock(String name, Block block){
        registerBlockItem(name, block);
        return Registry.register(Registries.BLOCK, new Identifier(DeerMod.MODID, name), block);
    }

    private static Item registerBlockItem(String name, Block block){
        return Registry.register(Registries.ITEM, new Identifier(DeerMod.MODID, name), new BlockItem(block, new FabricItemSettings()));

    }

    public static void registerModBlocks(){
        DeerMod.LOGGER.info("register");

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(ModBlocks::addItemsToNaturalTabItemGroup);
    }
}
