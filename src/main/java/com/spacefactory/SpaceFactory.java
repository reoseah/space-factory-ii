package com.spacefactory;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlazedTerracottaBlock;
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

        Registry.register(Registries.ITEM, "spacefactory:white_spaceglaze", new BlockItem(WHITE_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:white_spaceglaze_panel", new BlockItem(WHITE_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_gray_spaceglaze", new BlockItem(LIGHT_GRAY_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:light_gray_spaceglaze_panel", new BlockItem(LIGHT_GRAY_SPACEGLAZE_PANEL, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:gray_spaceglaze", new BlockItem(GRAY_SPACEGLAZE, new Item.Settings()));
        Registry.register(Registries.ITEM, "spacefactory:gray_spaceglaze_panel", new BlockItem(GRAY_SPACEGLAZE_PANEL, new Item.Settings()));

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