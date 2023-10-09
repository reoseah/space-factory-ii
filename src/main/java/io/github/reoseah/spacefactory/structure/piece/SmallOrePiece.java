package io.github.reoseah.spacefactory.structure.piece;

import io.github.reoseah.spacefactory.structure.BedrockOreStructure;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class SmallOrePiece extends StructurePiece {
    public static final StructurePieceType TYPE = TinyOrePiece::new;

    protected final BedrockOreStructure.Type oreType;

    public SmallOrePiece(BedrockOreStructure.Type type, int length, BlockPos pos) {
        super(TYPE, length, BlockBox.create(pos, pos.add(1, 1, 1)));
        this.oreType = type;
    }

    public SmallOrePiece(StructureContext ctx, NbtCompound nbt) {
        super(TYPE, nbt);
        this.oreType = BedrockOreStructure.Type.byId(nbt.getInt("ore_type"));
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("ore_type", this.oreType.ordinal());
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos pos = this.boundingBox.getCenter();

        for (int dx = 0; dx <= 1; dx++) {
            for (int dy = 0; dy <= 1; dy++) {
                for (int dz = 0; dz <= 1; dz++) {
                    this.addOre(world, pos.getX() + dx, pos.getY() + dy, pos.getZ() + dz, chunkBox, random);
                }
            }
        }

    }

    @Override
    protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return block == Blocks.BEDROCK || block == Blocks.DEEPSLATE;
    }

    protected void addOre(StructureWorldAccess world, int x, int y, int z, BlockBox chunkBox, Random random) {
        if (this.oreType == BedrockOreStructure.Type.EMERALD && random.nextBoolean()) {
            this.addBlock(world, Blocks.DEEPSLATE.getDefaultState(), x, y, z, chunkBox);
        } else {
            this.addBlock(world, this.oreType.getDeepslateOre(), x, y, z, chunkBox);
        }
    }
}
