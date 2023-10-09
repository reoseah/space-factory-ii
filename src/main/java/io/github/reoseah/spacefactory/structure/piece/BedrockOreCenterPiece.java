package io.github.reoseah.spacefactory.structure.piece;

import io.github.reoseah.spacefactory.structure.BedrockOreStructure;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.structure.StructureContext;
import net.minecraft.structure.StructurePiece;
import net.minecraft.structure.StructurePieceType;
import net.minecraft.structure.StructurePiecesHolder;
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
        this(type, 0, BlockBox.create(pos.getStartPos().withY(bottomY).add(-1, 0, -1), pos.getStartPos().withY(bottomY).add(1, 4, 1)));
    }

    public BedrockOreCenterPiece(BedrockOreStructure.Type type, int length, BlockBox bb) {
        super(TYPE, length, bb);
        this.oreType = type;
    }

    public BedrockOreCenterPiece(StructureContext ctx, NbtCompound nbt) {
        super(TYPE, nbt);
        this.oreType = BedrockOreStructure.Type.byId(nbt.getInt("ore_type"));
    }

    @Override
    protected void writeNbt(StructureContext context, NbtCompound nbt) {
        nbt.putInt("ore_type", this.oreType.ordinal());
    }

    @Override
    public void fillOpenings(StructurePiece start, StructurePiecesHolder holder, Random random) {
        super.fillOpenings(start, holder, random);

        BlockPos bedrockOre = this.boundingBox.getCenter().withY(this.boundingBox.getMinY());

        for (int i = 0; i < 8; i++) {
            int dx = random.nextInt(16) - random.nextInt(16);
            int dy = 1 + random.nextInt(8) + random.nextInt(8);
            int dz = random.nextInt(16) - random.nextInt(16);

            StructurePiece piece = new SmallOrePiece(this.oreType, this.chainLength + 1, bedrockOre.add(dx, dy, dz));
            if (holder.getIntersecting(piece.getBoundingBox()) == null) {
                holder.addPiece(piece);
            }
        }

        for (int i = 0; i < 16; i++) {
            int dx = random.nextInt(24) - random.nextInt(24);
            int dy = 1 + random.nextInt(12) + random.nextInt(12);
            int dz = random.nextInt(24) - random.nextInt(24);

            StructurePiece piece = new TinyOrePiece(this.oreType, this.chainLength + 1, bedrockOre.add(dx, dy, dz));
            if (holder.getIntersecting(piece.getBoundingBox()) == null) {
                holder.addPiece(piece);
            }
        }
    }

    @Override
    public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        BlockPos pos = this.boundingBox.getCenter().withY(this.boundingBox.getMinY());

        //  this.addBlock doesn't work for some reason, only Y > minY get placed
        world.setBlockState(pos, this.oreType.getBedrockOre(), 3);

        for (int dz = -1; dz <= 1; dz++) {
            for (int dx = -1; dx <= 1; dx++) {
                this.addOre(world, pos.getX() + dx, pos.getY() + 1, pos.getZ() + dz, chunkBox, random);
                this.addOre(world, pos.getX() + dx, pos.getY() + 2, pos.getZ() + dz, chunkBox, random);
                this.addOre(world, pos.getX() + dx, pos.getY() + 3, pos.getZ() + dz, chunkBox, random);
                if (Math.abs(dx) == 0 || Math.abs(dz) == 0) {
                    this.addOre(world, pos.getX() + dx, pos.getY() + 4, pos.getZ() + dz, chunkBox, random);
                }
            }
        }
    }

    protected void addOre(StructureWorldAccess world, int x, int y, int z, BlockBox chunkBox, Random random) {
        if (this.oreType == BedrockOreStructure.Type.EMERALD && random.nextBoolean()) {
            this.addBlock(world, Blocks.DEEPSLATE.getDefaultState(), x, y, z, chunkBox);
        } else {
            this.addBlock(world, this.oreType.getDeepslateOre(), x, y, z, chunkBox);
        }
    }

}
