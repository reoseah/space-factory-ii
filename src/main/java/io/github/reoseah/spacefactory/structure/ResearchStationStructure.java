package io.github.reoseah.spacefactory.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.VerticalBlockSample;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.List;
import java.util.Optional;

public class ResearchStationStructure extends Structure {
    public static final Codec<ResearchStationStructure> CODEC = ResearchStationStructure.createCodec(ResearchStationStructure::new);
    public static final StructureType<ResearchStationStructure> TYPE = () -> CODEC;

    public ResearchStationStructure(Config config) {
        super(config);
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom random = context.random();
        ChunkGenerator chunkGenerator = context.chunkGenerator();
        HeightLimitView world = context.world();

//        int y = context.chunkGenerator().getHeight(chunkPos.getStartX(), chunkPos.getStartZ(), Heightmap.Type.WORLD_SURFACE_WG, world, context.noiseConfig()) - 1;
        int minY = world.getBottomY() + 15;
        int maxY = MathHelper.nextBetween(random, 32, 100);
        BlockPos pos = chunkPos.getCenterAtY(0);

        BlockBox box = BlockBox.create(pos.add(-5, 0, -5), pos.add(4, 5, 4));
        ImmutableList<BlockPos> corners = ImmutableList.of(new BlockPos(box.getMinX(), 0, box.getMinZ()), new BlockPos(box.getMaxX(), 0, box.getMinZ()), new BlockPos(box.getMinX(), 0, box.getMaxZ()), new BlockPos(box.getMaxX(), 0, box.getMaxZ()));
        List<VerticalBlockSample> columns = corners.stream().map(p -> chunkGenerator.getColumnSample(p.getX(), p.getZ(), world, context.noiseConfig())).toList();

        Heightmap.Type type = Heightmap.Type.WORLD_SURFACE_WG;
        int[] yWrapper = new int[1];
        outer:
        for (int y = maxY; y > minY; --y) {
            int airBlocks = 0;
            for (VerticalBlockSample column : columns) {
                BlockState state = column.getState(y);
                if (!type.getBlockPredicate().test(state) || ++airBlocks < 3) {
                    continue;
                }
                yWrapper[0] = y;
                break outer;
            }
        }

        return Optional.of(new StructurePosition(chunkPos.getCenterAtY(yWrapper[0]), Either.left(builder -> {
            ResearchStationPiece piece = new ResearchStationPiece(chunkPos, random.split(), yWrapper[0]);
            builder.addPiece(piece);
            piece.fillOpenings(piece, builder, random);
        })));
    }

    @Override
    public StructureType<?> getType() {
        return TYPE;
    }
}
