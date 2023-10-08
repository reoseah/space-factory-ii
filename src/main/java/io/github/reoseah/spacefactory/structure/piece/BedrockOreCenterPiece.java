package io.github.reoseah.spacefactory.structure.piece;

import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.structure.BedrockOreStructure;
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
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class BedrockOreCenterPiece extends StructurePiece {
    public static final StructurePieceType TYPE = BedrockOreCenterPiece::new;

    protected final BedrockOreStructure.Type oreType;

    public BedrockOreCenterPiece(BedrockOreStructure.Type type, ChunkPos pos, Random random, int bottomY) {
        this(type, 0, BlockBox.create(pos.getCenterAtY(bottomY).add(-1, 0, -1), pos.getCenterAtY(bottomY).add(1, 4, 1)));
    }

    public BedrockOreCenterPiece(BedrockOreStructure.Type type, int length, BlockBox boundingBox) {
        super(TYPE, length, boundingBox);
        this.oreType = type;
    }

    public BedrockOreCenterPiece(StructureContext ctx, NbtCompound nbt) {
        super(TYPE, nbt);
        this.oreType = BedrockOreStructure.Type.byId(nbt.getInt("ore_type"));
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {

    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos pos = this.boundingBox.getCenter().withY(this.boundingBox.getMinY());

        // this doesn't work for some reason, only Y > minY get placed
        // this.addBlock(world, SpaceFactory.BEDROCK_REDSTONE_ORE.getDefaultState(), pos.getX(), pos.getY(), pos.getZ(), chunkBox);

        world.setBlockState(pos, this.oreType.getBedrockOre(), 3);

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                this.addBlock(world, this.oreType.getDeepslateOre(), pos.getX() + dx, pos.getY() + 1, pos.getZ() + dz, chunkBox);
                this.addBlock(world, this.oreType.getDeepslateOre(), pos.getX() + dx, pos.getY() + 2, pos.getZ() + dz, chunkBox);
                this.addBlock(world, this.oreType.getDeepslateOre(), pos.getX() + dx, pos.getY() + 3, pos.getZ() + dz, chunkBox);
                if (Math.abs(dx) == 0 || Math.abs(dz) == 0) {
                    this.addBlock(world, this.oreType.getDeepslateOre(), pos.getX() + dx, pos.getY() + 4, pos.getZ() + dz, chunkBox);
                }
            }
        }
    }
}
