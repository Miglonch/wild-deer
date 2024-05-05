package com.Miglon;

import com.Miglon.Blocks.ModBlocks;
import com.Miglon.Entities.DeerEntity;
import com.Miglon.Items.ModItems;
import com.Miglon.gen.ModWorldGeneration;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeerMod implements ModInitializer {
    public static final String MODID = "deerid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);


	public static final EntityType<DeerEntity> DEER_ENTITY = Registry.register(
			Registries.ENTITY_TYPE,
			new Identifier(DeerMod.MODID, "deer"),
			FabricEntityTypeBuilder.<DeerEntity>create(SpawnGroup.CREATURE, DeerEntity::new)
					.dimensions(EntityDimensions.fixed(1.25F, 1.5F))
					.build()
	);

	public static final Item DEER_SPAWN_EGG = registerItem("deer_spawn_egg", new SpawnEggItem(DeerMod.DEER_ENTITY, 0xc4c4c4, 0xadadad, new FabricItemSettings()));

	private static Item registerItem(String name, Item item){
		return Registry.register(Registries.ITEM, new Identifier(DeerMod.MODID, name), item);
	}

	private static void  addItemsToSpawnEggsTabItemGroup(FabricItemGroupEntries entries){
		entries.add(DEER_SPAWN_EGG);
	}
	@Override
	public void onInitialize() {
		FabricDefaultAttributeRegistry.register(DEER_ENTITY, DeerEntity.createDeerAttributes());
		ModWorldGeneration.generateModWorldGen();
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.SPAWN_EGGS).register(DeerMod::addItemsToSpawnEggsTabItemGroup);
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
	}
}