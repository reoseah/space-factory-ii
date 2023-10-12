package io.github.reoseah.spacefactory.structure.piece;

import io.github.reoseah.spacefactory.SpaceFactory;
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
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

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
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', '#', '.', '.', '#', 'R', '#', '.', '.', '#', ' '}, //
            {' ', ' ', '#', '.', '#', '.', '#', '.', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', 'D', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', '#', '#', '#', '#', ' ', ' ', ' '}, //
            {' ', ' ', '#', '.', '#', '.', '#', '.', '#', ' ', ' '}, //
            {' ', '#', '.', '.', '.', '.', '.', '.', '.', '#', ' '}, //
            {'#', '.', '.', '.', '#', '.', '#', '.', '.', '.', '#'}, //
            {'#', '#', '#', '#', '#', '.', '#', '#', '#', '#', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', '#', '.', '.', '#', 'r', '#', '.', '.', '#', ' '}, //
            {' ', ' ', '#', '.', '$', '.', 'G', '.', '#', ' ', ' '}, //
            {' ', ' ', ' ', '#', '#', 'd', '#', '#', ' ', ' ', ' '}, //
    }, { //
            {' ', ' ', ' ', '#', '$', 'G', '$', '#', ' ', ' ', ' '}, //
            {' ', ' ', '$', '.', '#', '.', '#', '.', '$', ' ', ' '}, //
            {' ', '$', '.', '.', '#', '.', '#', '.', '.', '$', ' '}, //
            {'#', '.', '.', '.', '#', '.', '#', '.', '.', '.', '#'}, //
            {'$', '#', '$', '$', '#', '#', '#', '$', '$', '#', '$'}, //
            {'G', '.', '.', '.', '.', '.', '.', '.', '.', '.', 'G'}, //
            {'$', '.', '.', '.', '.', '.', '.', '.', '.', '.', '$'}, //
            {'#', '.', '.', '.', '.', '.', '.', '.', '.', '.', '#'}, //
            {' ', '$', '.', '.', '#', '#', '#', '.', '.', '$', ' '}, //
            {' ', ' ', '$', '.', '$', '.', '$', '.', '$', ' ', ' '}, //
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
            {' ', ' ', '#', 'T', '#', '#', '#', 'T', '#', ' ', ' '}, //
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
    public static final BlockState STEEL_TOP_SLAB = SpaceFactory.GRAPHENE_STEEL_SLAB.getDefaultState().with(SlabBlock.TYPE, SlabType.TOP);

    public ResearchStationPiece(ChunkPos pos, Random random, int y) {
        this(0, BlockBox.create(pos.getStartPos().withY(y).add(-5, -1, -5), pos.getStartPos().withY(y).add(5, 4, 5)));
        this.setOrientation(Direction.NORTH);
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
                    switch (ch) {
                        case '#' -> this.addBlock(world, STEEL_BLOCK, x, y, z, chunkBox);
                        case '$' -> this.addBlock(world, STEEL_DOUBLE_SLAB, x, y, z, chunkBox);
                        case 'T' -> this.addBlock(world, STEEL_TOP_SLAB, x, y, z, chunkBox);
                        case 'G' -> this.addBlock(world, Blocks.TINTED_GLASS.getDefaultState(), x, y, z, chunkBox);
                        case 'D' ->
                                this.addBlock(world, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.LOWER), x, y, z, chunkBox);
                        case 'd' ->
                                this.addBlock(world, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER), x, y, z, chunkBox);
                        case 'R' ->
                                this.addBlock(world, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.LOWER).with(DoorBlock.FACING, Direction.SOUTH), x, y, z, chunkBox);
                        case 'r' ->
                                this.addBlock(world, Blocks.IRON_DOOR.getDefaultState().with(DoorBlock.HALF, DoubleBlockHalf.UPPER).with(DoorBlock.FACING, Direction.SOUTH), x, y, z, chunkBox);
                        case '.' -> this.addBlock(world, Blocks.CAVE_AIR.getDefaultState(), x, y, z, chunkBox);
                    }
                }
            }
        }
    }
}
