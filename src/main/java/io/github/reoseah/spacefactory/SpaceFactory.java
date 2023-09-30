package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.block.AssemblerBlock;
import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.block.ExtractorBlock;
import io.github.reoseah.spacefactory.block.ExtractorBlockEntity;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import io.github.reoseah.spacefactory.screen.ExtractorScreenHandler;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

import java.nio.file.Path;

public class SpaceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger("spacefactory");

    public static SpaceFactoryConfig config;

    public static final Block BEDROCK_IRON_ORE = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(Float.MAX_VALUE, Float.MAX_VALUE));
    public static final Block BEDROCK_COPPER_ORE = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(Float.MAX_VALUE, Float.MAX_VALUE));
    public static final Block BEDROCK_GOLD_ORE = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(Float.MAX_VALUE, Float.MAX_VALUE));
    public static final Block BEDROCK_REDSTONE_ORE = new RedstoneOreBlock(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(Float.MAX_VALUE, Float.MAX_VALUE) //
            .luminance(state -> state.get(Properties.LIT) ? 9 : 0));
    public static final Block BEDROCK_EMERALD_ORE = new Block(AbstractBlock.Settings.create().mapColor(MapColor.GRAY).strength(Float.MAX_VALUE, Float.MAX_VALUE));
    public static final Block ULTRAPURE_IRON_BLOCK = new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(SpaceFactory::none));
    public static final Block ULTRAPURE_COPPER_BLOCK = new Block(AbstractBlock.Settings.create().mapColor(MapColor.DULL_RED).strength(3F, 15F).allowsSpawning(SpaceFactory::none));
    public static final Block STEEL_SHEET_BLOCK = new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(SpaceFactory::none));
    public static final Block EXTRACTOR = new ExtractorBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(SpaceFactory::none));
    public static final Block ASSEMBLER = new AssemblerBlock(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(SpaceFactory::none));

    public static final Item ULTRAPURE_IRON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_COPPER = new Item(new Item.Settings());
    public static final Item ULTRAPURE_GOLD = new Item(new Item.Settings());
    public static final Item ULTRAPURE_SILICON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_CARBON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_IRON_NUGGET = new Item(new Item.Settings());
    public static final Item ULTRAPURE_COPPER_NUGGET = new Item(new Item.Settings());
    public static final Item STEEL_SHEET = new Item(new Item.Settings());
    public static final Item MOTOR = new Item(new Item.Settings());
    public static final Item TRANSFORMER = new Item(new Item.Settings());
    public static final Item SUPERCAPACITOR = new Item(new Item.Settings());
    public static final Item MOLECULAR_TRANSFORMER = new Item(new Item.Settings());
    public static final Item RFLUX_LASER = new Item(new Item.Settings());
    public static final Item QUANTUM_COMPUTER = new Item(new Item.Settings());
    public static final Item DRILL_SUPPLIES = new Item(new Item.Settings());

    public static void initialize() throws Exception {
        LOGGER.info("Reading config...");
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");
        config = SpaceFactoryConfig.loadOrCreate(configPath);

        LOGGER.info("Initializing...");

        Registry.register(Registries.BLOCK, "spacefactory:bedrock_iron_ore", BEDROCK_IRON_ORE);
        Registry.register(Registries.BLOCK, "spacefactory:bedrock_copper_ore", BEDROCK_COPPER_ORE);
        Registry.register(Registries.BLOCK, "spacefactory:bedrock_gold_ore", BEDROCK_GOLD_ORE);
        Registry.register(Registries.BLOCK, "spacefactory:bedrock_redstone_ore", BEDROCK_REDSTONE_ORE);
        Registry.register(Registries.BLOCK, "spacefactory:bedrock_emerald_ore", BEDROCK_EMERALD_ORE);
        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_iron_block", ULTRAPURE_IRON_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_copper_block", ULTRAPURE_COPPER_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:steel_sheet_block", STEEL_SHEET_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:extractor", EXTRACTOR);
        Registry.register(Registries.BLOCK, "spacefactory:assembler", ASSEMBLER);

        Registry.register(Registries.ITEM, "spacefactory:bedrock_iron_ore", new BlockItem(BEDROCK_IRON_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:bedrock_copper_ore", new BlockItem(BEDROCK_COPPER_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:bedrock_gold_ore", new BlockItem(BEDROCK_GOLD_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:bedrock_redstone_ore", new BlockItem(BEDROCK_REDSTONE_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:bedrock_emerald_ore", new BlockItem(BEDROCK_EMERALD_ORE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron_block", new BlockItem(ULTRAPURE_IRON_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper_block", new BlockItem(ULTRAPURE_COPPER_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:steel_sheet_block", new BlockItem(STEEL_SHEET_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:extractor", new BlockItem(EXTRACTOR, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:assembler", new BlockItem(ASSEMBLER, new Item.Settings()));

        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron", ULTRAPURE_IRON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper", ULTRAPURE_COPPER);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_gold", ULTRAPURE_GOLD);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_silicon", ULTRAPURE_SILICON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_carbon", ULTRAPURE_CARBON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron_nugget", ULTRAPURE_IRON_NUGGET);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper_nugget", ULTRAPURE_COPPER_NUGGET);
        Registry.register(Registries.ITEM, "spacefactory:steel_sheet", STEEL_SHEET);
        Registry.register(Registries.ITEM, "spacefactory:motor", MOTOR);
        Registry.register(Registries.ITEM, "spacefactory:transformer", TRANSFORMER);
        Registry.register(Registries.ITEM, "spacefactory:supercapacitor", SUPERCAPACITOR);
        Registry.register(Registries.ITEM, "spacefactory:rflux_laser", RFLUX_LASER);
        Registry.register(Registries.ITEM, "spacefactory:quantum_computer", QUANTUM_COMPUTER);
        Registry.register(Registries.ITEM, "spacefactory:molecular_transformer", MOLECULAR_TRANSFORMER);
        Registry.register(Registries.ITEM, "spacefactory:drill_supplies", DRILL_SUPPLIES);

        FuelRegistry.INSTANCE.add(ULTRAPURE_CARBON, 8 * 200);

        ItemGroup itemGroup = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.spacefactory"))
                .icon(() -> new ItemStack(SpaceFactory.QUANTUM_COMPUTER))
                .entries((displayContext, entries) -> {
                    entries.add(BEDROCK_IRON_ORE);
                    entries.add(BEDROCK_COPPER_ORE);
                    entries.add(BEDROCK_GOLD_ORE);
                    entries.add(BEDROCK_REDSTONE_ORE);
                    entries.add(BEDROCK_EMERALD_ORE);
                    entries.add(ULTRAPURE_IRON_BLOCK);
                    entries.add(ULTRAPURE_COPPER_BLOCK);
                    entries.add(STEEL_SHEET_BLOCK);
                    entries.add(EXTRACTOR);
                    entries.add(ASSEMBLER);

                    entries.add(ULTRAPURE_IRON);
                    entries.add(ULTRAPURE_COPPER);
                    entries.add(ULTRAPURE_GOLD);
                    entries.add(ULTRAPURE_SILICON);
                    entries.add(ULTRAPURE_CARBON);
                    entries.add(ULTRAPURE_IRON_NUGGET);
                    entries.add(ULTRAPURE_COPPER_NUGGET);
                    entries.add(STEEL_SHEET);
                    entries.add(MOTOR);
                    entries.add(TRANSFORMER);
                    entries.add(SUPERCAPACITOR);
                    entries.add(RFLUX_LASER);
                    entries.add(QUANTUM_COMPUTER);
                    entries.add(MOLECULAR_TRANSFORMER);
                    entries.add(DRILL_SUPPLIES);
                })
                .build();
        Registry.register(Registries.ITEM_GROUP, "spacefactory:main", itemGroup);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, "spacefactory:assembler", AssemblerBlockEntity.TYPE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, "spacefactory:extractor", ExtractorBlockEntity.TYPE);

        EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.createEnergyStorage(), AssemblerBlockEntity.TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.createEnergyStorage(), ExtractorBlockEntity.TYPE);

        Registry.register(Registries.SCREEN_HANDLER, "spacefactory:assembler", AssemblerScreenHandler.TYPE);
        Registry.register(Registries.SCREEN_HANDLER, "spacefactory:extractor", ExtractorScreenHandler.TYPE);

        Registry.register(Registries.RECIPE_TYPE, "spacefactory:assembly", AssemblerRecipe.TYPE);
        Registry.register(Registries.RECIPE_TYPE, "spacefactory:extraction", ExtractorRecipe.TYPE);

        Registry.register(Registries.RECIPE_SERIALIZER, "spacefactory:assembly", AssemblerRecipe.SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, "spacefactory:extraction", ExtractorRecipe.SERIALIZER);

        LOGGER.info("Done!");
    }

    @SuppressWarnings("unused")
    private static boolean none(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }
}