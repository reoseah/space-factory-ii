package io.github.reoseah.spacefactory;

import io.github.reoseah.spacefactory.block.AssemblerBlock;
import io.github.reoseah.spacefactory.block.AssemblerBlockEntity;
import io.github.reoseah.spacefactory.screen.AssemblerScreenHandler;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SpaceFactory {
    public static final Logger LOGGER = LoggerFactory.getLogger("spacefactory");

    public static final Block ULTRAPURE_IRON_BLOCK = new Block(AbstractBlock.Settings.create().mapColor(MapColor.WHITE).strength(3F, 15F).allowsSpawning(SpaceFactory::none));
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

    public static void initialize() {
        LOGGER.info("Initializing...");

        Registry.register(Registries.BLOCK, "spacefactory:ultrapure_iron_block", ULTRAPURE_IRON_BLOCK);
        Registry.register(Registries.BLOCK, "spacefactory:assembler", ASSEMBLER);

        Registry.register(Registries.ITEM, "spacefactory:ultrapure_iron_block", new BlockItem(ULTRAPURE_IRON_BLOCK, new Item.Settings()));
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

        ItemGroup itemGroup = FabricItemGroup.builder()
                .displayName(Text.translatable("itemGroup.spacefactory"))
                .icon(() -> new ItemStack(SpaceFactory.ULTRAPURE_IRON))
                .entries((displayContext, entries) -> {
                    entries.add(ULTRAPURE_IRON_BLOCK);
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
                })
                .build();
        Registry.register(Registries.ITEM_GROUP, "spacefactory:main", itemGroup);

        Registry.register(Registries.BLOCK_ENTITY_TYPE, "spacefactory:assembler", AssemblerBlockEntity.TYPE);

        Registry.register(Registries.SCREEN_HANDLER, "spacefactory:assembler", AssemblerScreenHandler.TYPE);

        FuelRegistry.INSTANCE.add(ULTRAPURE_CARBON, 8 * 200);
    }

    private static boolean none(BlockState state, BlockView world, BlockPos pos, EntityType<?> type) {
        return false;
    }
}