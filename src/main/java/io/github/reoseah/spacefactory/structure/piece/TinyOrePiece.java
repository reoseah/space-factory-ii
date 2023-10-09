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

public class TinyOrePiece extends StructurePiece {
    public static final StructurePieceType TYPE = TinyOrePiece::new;

    protected final BedrockOreStructure.Type oreType;

    public TinyOrePiece(BedrockOreStructure.Type type, int length, BlockPos pos) {
        super(TYPE, length, new BlockBox(pos));
        this.oreType = type;
    }

    public TinyOrePiece(StructureContext ctx, NbtCompound nbt) {
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

        this.addBlock(world, this.oreType.getDeepslateOre(), pos.getX(), pos.getY(), pos.getZ(), boundingBox);
    }

    @Override
    protected boolean canAddBlock(WorldView world, int x, int y, int z, BlockBox box) {
        Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return block == Blocks.BEDROCK || block == Blocks.DEEPSLATE;
    }
}
