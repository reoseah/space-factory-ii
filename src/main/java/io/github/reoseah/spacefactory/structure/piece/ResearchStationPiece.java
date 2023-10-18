package io.github.reoseah.spacefactory.structure.piece;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.block.MachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class ResearchStationPiece extends StructurePiece {
    public static final StructurePieceType TYPE = ResearchStationPiece::new;

    public static final char[][][] LAYERS = {{ //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', '.', '#', '.', '#', '.', '#', ' ', ' '}, //
            {' ', '#', '.', '.', '.', '.', '.', '.', '.', '#', ' '}, //
            {'#', '.', '.', '.', '#', '.', '#', '.', '.', '.', '#'}, //
            {'#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#'}, //
            {'#', 'E', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', 'A', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', '#', '.', '.', '#', 'D', '#', '.', '.', '#', ' '}, //
            {' ', ' ', '#', '.', '$', '.', '$', '.', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', 'R', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', '.', '#', '.', '#', '.', '#', ' ', ' '}, //
            {' ', '#', '.', '.', '.', '.', '.', '.', '.', '#', ' '}, //
            {'#', '.', '.', '.', '#', '.', '#', '.', '.', '.', '#'}, //
            {'#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', '#', '.', '.', '#', 'd', '#', '.', '.', '#', ' '}, //
            {' ', ' ', '#', '.', '$', '.', 'G', '.', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', 'r', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', 'X', 'G', 'X', '#', ' ', ' ', ' '}, //
            {' ', ' ', 'X', '.', '#', '.', '#', '.', 'X', ' ', ' '}, //
            {' ', 'X', '.', '.', '#', '.', '#', '.', '.', 'X', ' '}, //
            {'#', '.', '.', '.', '#', '.', '#', '.', '.', '.', '#'}, //
            {'X', '#', 'X', 'X', '#', '#', '#', 'X', 'X', '#', 'X'}, //
            {'G', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'G'}, //
            {'X', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'X'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', 'X', '.', '.', '#', '#', '#', '.', '.', 'X', ' '}, //
            {' ', ' ', 'X', '.', '#', ' ', '#', '.', 'X', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', 'T', '#', 'T', '#', 'T', '#', ' ', ' '}, //
            {' ', '#', 'T', '.', '#', '.', '#', '.', 'T', '#', ' '}, //
            {'#', 'T', '.', '.', '#', '.', '#', '.', '.', 'T', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', 'T', '.', '.', '#', '.', '#', '.', '.', 'T', '#'}, //
            {'#', '#', '#', '#', '#', '#', '#', '#', '#', '#', '#'}, //
            {'#', 'T', '.', '.', '#', '.', '#', '.', '.', 'T', '#'}, //
            {' ', '#', 'T', '.', '#', '#', '#', '.', 'T', '#', ' '}, //
            {' ', ' ', '#', 'T', '#', 'X', '#', 'T', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', '#', '#', '#', '#', '#', '#', '#', '#', '#', ' '}, //
            {' ', ' ', '#', '#', '#', '#', '#', '#', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '}, //
    }};
    public static final BlockState STEEL_BLOCK = SpaceFactory.GRAPHENE_STEEL.getDefaultState();
    public static final BlockState STEEL_DOUBLE_SLAB = SpaceFactory.GRAPHENE_STEEL_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.DOUBLE);
    public static final BlockState EMBOSSED_STEEL = SpaceFactory.EMBOSSED_GRAPHENE_STEEL.getDefaultState();
    public static final BlockState STEEL_TOP_SLAB = SpaceFactory.GRAPHENE_STEEL_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP);
    public static final BlockState STEEL_DOOR = SpaceFactory.GRAPHENE_STEEL_DOOR.getDefaultState();

    public ResearchStationPiece(ChunkPos pos, Random random, int y) {
        this(0, BlockBox.create(pos.getStartPos().withY(y).add(-5, -1, -5), pos.getStartPos().withY(y).add(5, 4, 5)));
        this.setOrientation(Direction.fromHorizontal(random.nextInt(4)));
    }

    public ResearchStationPiece(int length, BlockBox bb) {
        super(TYPE, length, bb);
    }

    public ResearchStationPiece(StructureContext ctx, NbtCompound nbt) {
        super(TYPE, nbt);
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {

    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        for (int y = 0; y < LAYERS.length; y++) {
            for (int z = 0; z < LAYERS[0].length; z++) {
                for (int x = 0; x < LAYERS[0][0].length; x++) {
                    char ch = LAYERS[y][z][x];
                    if (ch == ' ') {
                        continue;
                    }
                    BlockState state = switch (ch) {
                        case '#' -> STEEL_BLOCK;
                        case '$' -> STEEL_DOUBLE_SLAB;
                        case 'X' -> EMBOSSED_STEEL;
                        case 'T' -> STEEL_TOP_SLAB;
                        case 'G' -> Blocks.TINTED_GLASS.getDefaultState();
                        case 'D' -> STEEL_DOOR.with(DoorBlock.HALF, DoubleBlockHalf.LOWER);
                        case 'd' -> STEEL_DOOR.with(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                        case 'R' -> STEEL_DOOR.with(DoorBlock.HALF, DoubleBlockHalf.LOWER) //
                                .with(DoorBlock.FACING, Direction.SOUTH);
                        case 'r' -> STEEL_DOOR.with(DoorBlock.HALF, DoubleBlockHalf.UPPER) //
                                .with(DoorBlock.FACING, Direction.SOUTH);
                        case 'E' -> SpaceFactory.EXTRACTOR.getDefaultState().with(MachineBlock.FACING, Direction.WEST);
                        case 'A' -> SpaceFactory.ASSEMBLER.getDefaultState().with(MachineBlock.FACING, Direction.WEST);
                        case '.' -> Blocks.CAVE_AIR.getDefaultState();
                        default -> throw new IllegalStateException();
                    };
                    this.addBlock(world, state, x, y, z, chunkBox);
                }
            }
        }
    }
}
