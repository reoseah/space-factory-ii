package com.spacefactory;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.*;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger("spacefactory");

    public static final Block WHITE_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block WHITE_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.WHITE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIGHT_GRAY_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIGHT_GRAY_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_GRAY).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block GRAY_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block GRAY_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.GRAY).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BLACK_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BLACK_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BLACK).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BROWN_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BROWN_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BROWN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block RED_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.RED).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block RED_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.RED).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block ORANGE_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block ORANGE_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.ORANGE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block YELLOW_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block YELLOW_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.YELLOW).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIME_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.LIME).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIME_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIME).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block GREEN_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block GREEN_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block CYAN_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block CYAN_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.CYAN).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIGHT_BLUE_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block LIGHT_BLUE_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.LIGHT_BLUE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BLUE_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block BLUE_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block PURPLE_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block PURPLE_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.PURPLE).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block PINK_SPACEGLAZE = new GlazedTerracottaBlock(AbstractBlock.Settings.create().mapColor(DyeColor.PINK).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block PINK_SPACEGLAZE_PANEL = new Block(AbstractBlock.Settings.create().mapColor(DyeColor.PINK).strength(5F, 15F).allowsSpawning(SpaceFactory::none).pistonBehavior(PistonBehavior.PUSH_ONLY));
    public static final Block RED_FORCEFIELD = new TransparentBlock(AbstractBlock.Settings.create().mapColor(DyeColor.RED).strength(5F).luminance(state -> 13).nonOpaque());
    public static final Block GREEN_FORCEFIELD = new TransparentBlock(AbstractBlock.Settings.create().mapColor(DyeColor.GREEN).strength(5F).luminance(state -> 13).nonOpaque());
    public static final Block BLUE_FORCEFIELD = new TransparentBlock(AbstractBlock.Settings.create().mapColor(DyeColor.BLUE).strength(5F).luminance(state -> 13).nonOpaque());

    public static final Item ULTRAPURE_IRON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_COPPER = new Item(new Item.Settings());
    public static final Item ULTRAPURE_GOLD = new Item(new Item.Settings());
    public static final Item ULTRAPURE_SILICON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_CARBON = new Item(new Item.Settings());
    public static final Item ULTRAPURE_IRON_NUGGET = new Item(new Item.Settings());
    public static final Item ULTRAPURE_COPPER_NUGGET = new Item(new Item.Settings());
    public static final Item STEEL_SHEET = new Item(new Item.Settings());
    public static final Item CIRCUIT = new Item(new Item.Settings());
    public static final Item ELECTRIC_MOTOR = new Item(new Item.Settings());
    public static final Item TRANSFORMER = new Item(new Item.Settings());
    public static final Item SUPERCAPACITOR = new Item(new Item.Settings());
    public static final Item RESONANT_LASER = new Item(new Item.Settings());
    public static final Item WARP_CRYSTAL = new Item(new Item.Settings());

    public static void initialize() {
        LOGGER.info("Initializing...");

        Registry.register(Registries.BLOCK, "spacefactory:white_spaceglaze", WHITE_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:white_spaceglaze_panel", WHITE_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:light_gray_spaceglaze", LIGHT_GRAY_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:light_gray_spaceglaze_panel", LIGHT_GRAY_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:gray_spaceglaze", GRAY_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:gray_spaceglaze_panel", GRAY_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:black_spaceglaze", BLACK_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:black_spaceglaze_panel", BLACK_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:brown_spaceglaze", BROWN_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:brown_spaceglaze_panel", BROWN_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:red_spaceglaze", RED_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:red_spaceglaze_panel", RED_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:orange_spaceglaze", ORANGE_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:orange_spaceglaze_panel", ORANGE_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:yellow_spaceglaze", YELLOW_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:yellow_spaceglaze_panel", YELLOW_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:lime_spaceglaze", LIME_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:lime_spaceglaze_panel", LIME_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:green_spaceglaze", GREEN_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:green_spaceglaze_panel", GREEN_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:cyan_spaceglaze", CYAN_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:cyan_spaceglaze_panel", CYAN_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:light_blue_spaceglaze", LIGHT_BLUE_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:light_blue_spaceglaze_panel", LIGHT_BLUE_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:blue_spaceglaze", BLUE_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:blue_spaceglaze_panel", BLUE_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:purple_spaceglaze", PURPLE_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:purple_spaceglaze_panel", PURPLE_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:pink_spaceglaze", PINK_SPACEGLAZE);
        Registry.register(Registries.BLOCK, "spacefactory:pink_spaceglaze_panel", PINK_SPACEGLAZE_PANEL);
        Registry.register(Registries.BLOCK, "spacefactory:red_forcefield", RED_FORCEFIELD);
        Registry.register(Registries.BLOCK, "spacefactory:green_forcefield", GREEN_FORCEFIELD);
        Registry.register(Registries.BLOCK, "spacefactory:blue_forcefield", BLUE_FORCEFIELD);

        Registry.register(Registries.ITEM, "spacefactory:white_spaceglaze", new BlockItem(WHITE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:white_spaceglaze_panel", new BlockItem(WHITE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_gray_spaceglaze", new BlockItem(LIGHT_GRAY_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_gray_spaceglaze_panel", new BlockItem(LIGHT_GRAY_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:gray_spaceglaze", new BlockItem(GRAY_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:gray_spaceglaze_panel", new BlockItem(GRAY_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:black_spaceglaze", new BlockItem(BLACK_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:black_spaceglaze_panel", new BlockItem(BLACK_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:brown_spaceglaze", new BlockItem(BROWN_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:brown_spaceglaze_panel", new BlockItem(BROWN_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:red_spaceglaze", new BlockItem(RED_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:red_spaceglaze_panel", new BlockItem(RED_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:orange_spaceglaze", new BlockItem(ORANGE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:orange_spaceglaze_panel", new BlockItem(ORANGE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:yellow_spaceglaze", new BlockItem(YELLOW_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:yellow_spaceglaze_panel", new BlockItem(YELLOW_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:lime_spaceglaze", new BlockItem(LIME_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:lime_spaceglaze_panel", new BlockItem(LIME_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:green_spaceglaze", new BlockItem(GREEN_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:green_spaceglaze_panel", new BlockItem(GREEN_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:cyan_spaceglaze", new BlockItem(CYAN_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:cyan_spaceglaze_panel", new BlockItem(CYAN_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_blue_spaceglaze", new BlockItem(LIGHT_BLUE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_blue_spaceglaze_panel", new BlockItem(LIGHT_BLUE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:blue_spaceglaze", new BlockItem(BLUE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:blue_spaceglaze_panel", new BlockItem(BLUE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:purple_spaceglaze", new BlockItem(PURPLE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:purple_spaceglaze_panel", new BlockItem(PURPLE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:pink_spaceglaze", new BlockItem(PINK_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:pink_spaceglaze_panel", new BlockItem(PINK_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:red_forcefield", new BlockItem(RED_FORCEFIELD, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:green_forcefield", new BlockItem(GREEN_FORCEFIELD, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:blue_forcefield", new BlockItem(BLUE_FORCEFIELD, new Item.Settings()));

        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron", ULTRAPURE_IRON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper", ULTRAPURE_COPPER);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_gold", ULTRAPURE_GOLD);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_silicon", ULTRAPURE_SILICON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_carbon", ULTRAPURE_CARBON);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron_nugget", ULTRAPURE_IRON_NUGGET);
        Registry.register(Registries.ITEM, "spacefactory:ultrapure_copper_nugget", ULTRAPURE_COPPER_NUGGET);
        Registry.register(Registries.ITEM, "spacefactory:steel_sheet", STEEL_SHEET);
        Registry.register(Registries.ITEM, "spacefactory:circuit", CIRCUIT);
        Registry.register(Registries.ITEM, "spacefactory:electric_motor", ELECTRIC_MOTOR);
        Registry.register(Registries.ITEM, "spacefactory:transformer", TRANSFORMER);
        Registry.register(Registries.ITEM, "spacefactory:supercapacitor", SUPERCAPACITOR);
        Registry.register(Registries.ITEM, "spacefactory:resonant_laser", RESONANT_LASER);
        Registry.register(Registries.ITEM, "spacefactory:warp_crystal", WARP_CRYSTAL);

        ItemGroup itemGroup = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.spacefactory"))
                .icon(() -> new ItemStack(SpaceFactory.ULTRAPURE_IRON))
                .entries((displayContext, entries) -> {
                    entries.add(WHITE_SPACEGLAZE);
                    entries.add(WHITE_SPACEGLAZE_PANEL);
                    entries.add(LIGHT_GRAY_SPACEGLAZE);
                    entries.add(LIGHT_GRAY_SPACEGLAZE_PANEL);
                    entries.add(GRAY_SPACEGLAZE);
                    entries.add(GRAY_SPACEGLAZE_PANEL);
                    entries.add(BLACK_SPACEGLAZE);
                    entries.add(BLACK_SPACEGLAZE_PANEL);
                    entries.add(BROWN_SPACEGLAZE);
                    entries.add(BROWN_SPACEGLAZE_PANEL);
                    entries.add(RED_SPACEGLAZE);
                    entries.add(RED_SPACEGLAZE_PANEL);
                    entries.add(ORANGE_SPACEGLAZE);
                    entries.add(ORANGE_SPACEGLAZE_PANEL);
                    entries.add(YELLOW_SPACEGLAZE);
                    entries.add(YELLOW_SPACEGLAZE_PANEL);
                    entries.add(LIME_SPACEGLAZE);
                    entries.add(LIME_SPACEGLAZE_PANEL);
                    entries.add(GREEN_SPACEGLAZE);
                    entries.add(GREEN_SPACEGLAZE_PANEL);
                    entries.add(CYAN_SPACEGLAZE);
                    entries.add(CYAN_SPACEGLAZE_PANEL);
                    entries.add(LIGHT_BLUE_SPACEGLAZE);
                    entries.add(LIGHT_BLUE_SPACEGLAZE_PANEL);
                    entries.add(BLUE_SPACEGLAZE);
                    entries.add(BLUE_SPACEGLAZE_PANEL);
                    entries.add(PURPLE_SPACEGLAZE);
                    entries.add(PURPLE_SPACEGLAZE_PANEL);
                    entries.add(PINK_SPACEGLAZE);
                    entries.add(PINK_SPACEGLAZE_PANEL);
                    entries.add(RED_FORCEFIELD);
                    entries.add(GREEN_FORCEFIELD);
                    entries.add(BLUE_FORCEFIELD);

                    entries.add(ULTRAPURE_IRON);
                    entries.add(ULTRAPURE_COPPER);
                    entries.add(ULTRAPURE_GOLD);
                    entries.add(ULTRAPURE_SILICON);
                    entries.add(ULTRAPURE_CARBON);
                    entries.add(ULTRAPURE_IRON_NUGGET);
                    entries.add(ULTRAPURE_COPPER_NUGGET);
                    entries.add(STEEL_SHEET);
                    entries.add(CIRCUIT);
                    entries.add(ELECTRIC_MOTOR);
                    entries.add(TRANSFORMER);
                    entries.add(SUPERCAPACITOR);
                    entries.add(RESONANT_LASER);
                    entries.add(WARP_CRYSTAL);
                })
                .build();
        Registry.register(Registries.ITEM_GROUP, "spacefactory:main", itemGroup);

        FuelRegistry.INSTANCE.add(ULTRAPURE_CARBON, 8 * 200);
    }

    private static boolean none(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }
}