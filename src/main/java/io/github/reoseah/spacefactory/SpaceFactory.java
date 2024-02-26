package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.block.ExtractorBlockEntity;
import io.github.reoseah.spacefactory.block.MachineBlock;
import io.github.reoseah.spacefactory.block.SteelDoorBlock;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.screen.ProcessingMachineScreenHandler;
import io.github.reoseah.spacefactory.structure.ResearchStationPiece;
import io.github.reoseah.spacefactory.structure.ResearchStationStructure;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import team.reborn.energy.api.EnergyStorage;

import java.nio.file.Path;

public class SpaceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger("spacefactory");

    public static SpaceFactoryConfig config;

    public static final Block ULTRAPURE_IRON_BLOCK = new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(Blocks::never));
    public static final Block ULTRAPURE_COPPER_BLOCK = new Block(AbstractBlock.Settings.copy(ULTRAPURE_IRON_BLOCK).mapColor(MapColor.DULL_RED));
    public static final Block ULTRAPURE_GOLD_BLOCK = new Block(AbstractBlock.Settings.copy(ULTRAPURE_IRON_BLOCK).mapColor(MapColor.GOLD));
    public static final Block GRAPHENE_STEEL = new Block(AbstractBlock.Settings.copy(ULTRAPURE_IRON_BLOCK));
    public static final Block GRAPHENE_STEEL_SLAB = new SlabBlock(AbstractBlock.Settings.copy(GRAPHENE_STEEL));
    public static final Block EMBOSSED_GRAPHENE_STEEL = new Block(AbstractBlock.Settings.copy(GRAPHENE_STEEL));
    public static final Block GRAPHENE_STEEL_DOOR = new SteelDoorBlock(AbstractBlock.Settings.copy(GRAPHENE_STEEL), new BlockSetType("iron", false, BlockSoundGroup.METAL, SoundEvents.BLOCK_IRON_DOOR_CLOSE, SoundEvents.BLOCK_IRON_DOOR_OPEN, SoundEvents.BLOCK_IRON_TRAPDOOR_CLOSE, SoundEvents.BLOCK_IRON_TRAPDOOR_OPEN, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_OFF, SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, SoundEvents.BLOCK_STONE_BUTTON_CLICK_OFF, SoundEvents.BLOCK_STONE_BUTTON_CLICK_ON));
    public static final Block EXTRACTOR = new MachineBlock(ExtractorBlockEntity::new, //
            AbstractBlock.Settings.copy(ULTRAPURE_IRON_BLOCK).luminance(state -> state.get(Properties.LIT) ? 9 : 0));
    public static final Block ASSEMBLER = new MachineBlock(AssemblerBlockEntity::new, //
            AbstractBlock.Settings.copy(ULTRAPURE_IRON_BLOCK).luminance(state -> state.get(Properties.LIT) ? 9 : 0));

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
    public static final Item METASTABLE_TIME_CRYSTAL = new Item(new Item.Settings());

    public static void initialize() throws Exception {
        LOGGER.info("Reading config...");
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("spacefactory.json");
        config = SpaceFactoryConfig.loadOrCreate(configPath);

        LOGGER.info("Initializing...");

        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_iron_block", ULTRAPURE_IRON_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_copper_block", ULTRAPURE_COPPER_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_gold_block", ULTRAPURE_GOLD_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:extractor", EXTRACTOR);
        Registry.register(Registries.BLOCK, "spacefactory:assembler", ASSEMBLER);
        Registry.register(Registries.BLOCK, "spacefactory:graphene_steel", GRAPHENE_STEEL);
        Registry.register(Registries.BLOCK, "spacefactory:graphene_steel_slab", GRAPHENE_STEEL_SLAB);
        Registry.register(Registries.BLOCK, "spacefactory:embossed_graphene_steel", EMBOSSED_GRAPHENE_STEEL);
        Registry.register(Registries.BLOCK, "spacefactory:graphene_steel_door", GRAPHENE_STEEL_DOOR);

        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron_block", new BlockItem(ULTRAPURE_IRON_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper_block", new BlockItem(ULTRAPURE_COPPER_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_gold_block", new BlockItem(ULTRAPURE_GOLD_BLOCK, new Item.Settings()));
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
        Registry.register(Registries.ITEM, "spacefactory:lapotrogenic_lattice", METASTABLE_TIME_CRYSTAL);
        Registry.register(Registries.ITEM, "spacefactory:molecular_transformer", MOLECULAR_TRANSFORMER);
        Registry.register(Registries.ITEM, "spacefactory:graphene_steel", new BlockItem(GRAPHENE_STEEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:graphene_steel_slab", new BlockItem(GRAPHENE_STEEL_SLAB, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:embossed_graphene_steel", new BlockItem(EMBOSSED_GRAPHENE_STEEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:graphene_steel_door", new BlockItem(GRAPHENE_STEEL_DOOR, new Item.Settings()));

        FuelRegistry.INSTANCE.add(ULTRAPURE_CARBON, 8 * 200);

        ItemGroup itemGroup = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.spacefactory"))
                .icon(() -> new ItemStack(SpaceFactory.QUANTUM_COMPUTER))
                .entries((displayContext, entries) -> {
                    entries.add(ULTRAPURE_IRON_BLOCK);
                    entries.add(ULTRAPURE_COPPER_BLOCK);
                    entries.add(ULTRAPURE_GOLD_BLOCK);
                    entries.add(EXTRACTOR);
                    entries.add(ASSEMBLER);
                    entries.add(GRAPHENE_STEEL);
                    entries.add(GRAPHENE_STEEL_SLAB);
                    entries.add(EMBOSSED_GRAPHENE_STEEL);
                    entries.add(GRAPHENE_STEEL_DOOR);

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
                    entries.add(METASTABLE_TIME_CRYSTAL);
                    entries.add(MOLECULAR_TRANSFORMER);
                })
                .build();
        Registry.register(Registries.ITEM_GROUP, "spacefactory:main", itemGroup);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, "spacefactory:assembler", AssemblerBlockEntity.TYPE);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, "spacefactory:extractor", ExtractorBlockEntity.TYPE);

        EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.createEnergyStorage(), AssemblerBlockEntity.TYPE);
        EnergyStorage.SIDED.registerForBlockEntity((be, side) -> be.createEnergyStorage(), ExtractorBlockEntity.TYPE);

        Registry.register(Registries.SCREEN_HANDLER, "spacefactory:assembler", ProcessingMachineScreenHandler.ASSEMBLER_TYPE);
        Registry.register(Registries.SCREEN_HANDLER, "spacefactory:extractor", ProcessingMachineScreenHandler.EXTRACTOR_TYPE);

        Registry.register(Registries.RECIPE_TYPE, "spacefactory:assembly", AssemblerRecipe.TYPE);
        Registry.register(Registries.RECIPE_TYPE, "spacefactory:extraction", ExtractorRecipe.TYPE);

        Registry.register(Registries.RECIPE_SERIALIZER, "spacefactory:assembly", AssemblerRecipe.SERIALIZER);
        Registry.register(Registries.RECIPE_SERIALIZER, "spacefactory:extraction", ExtractorRecipe.SERIALIZER);

        Registry.register(Registries.STRUCTURE_TYPE, "spacefactory:research_station", ResearchStationStructure.TYPE);

        Registry.register(Registries.STRUCTURE_PIECE, "spacefactory:research_station", ResearchStationPiece.TYPE);

        LOGGER.info("Done!");
    }
}