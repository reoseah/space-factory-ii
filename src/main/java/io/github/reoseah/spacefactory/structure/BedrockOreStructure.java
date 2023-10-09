package io.github.reoseah.spacefactory.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.structure.piece.BedrockOreCenterPiece;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.ChunkRandom;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.gen.structure.Structure;
import net.minecraft.world.gen.structure.StructureType;

import java.util.Optional;

public class BedrockOreStructure extends Structure {
    public static final Codec<BedrockOreStructure> CODEC = RecordCodecBuilder
            .create(instance -> instance
                    .group(BedrockOreStructure.configCodecBuilder(instance),
                            Type.CODEC.fieldOf("ore_type")
                                    .forGetter(structure -> structure.oreType))
                    .apply(instance, BedrockOreStructure::new));

    public static final StructureType<BedrockOreStructure> TYPE = () -> CODEC;

    public final Type oreType;

    public BedrockOreStructure(Structure.Config settings, Type oreType) {
        super(settings);
        this.oreType = oreType;
    }

    @Override
    public StructureType<?> getType() {
        return TYPE;
    }

    @Override
    protected Optional<StructurePosition> getStructurePosition(Context context) {
        ChunkPos chunkPos = context.chunkPos();
        ChunkRandom random = context.random();
        HeightLimitView world = context.world();

        return Optional.of(new StructurePosition(chunkPos.getCenterAtY(world.getBottomY()), Either.left(builder -> {
            BedrockOreCenterPiece piece = new BedrockOreCenterPiece(this.oreType, chunkPos, random.split(), world.getBottomY());
            builder.addPiece(piece);
            piece.fillOpenings(piece, builder, random);
        })));
    }

    public enum Type implements StringIdentifiable {
        IRON("iron", SpaceFactory.BEDROCK_IRON_ORE, Blocks.DEEPSLATE_IRON_ORE),
        COPPER("copper", SpaceFactory.BEDROCK_COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE),
        GOLD("gold", SpaceFactory.BEDROCK_GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE),
        REDSTONE("redstone", SpaceFactory.BEDROCK_REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE),
        EMERALD("emerald", SpaceFactory.BEDROCK_EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE);

        public static final com.mojang.serialization.Codec<Type> CODEC = StringIdentifiable.createCodec(Type::values);

        private final String name;
        @Getter
        private final BlockState bedrockOre;
        @Getter
        private final BlockState deepslateOre;

        Type(String name, Block bedrockOre, Block deepslateOre) {
            this.name = name;
            this.bedrockOre = bedrockOre.getDefaultState();
            this.deepslateOre = deepslateOre.getDefaultState();
        }

        public static Type byId(int id) {
            if (id < 0 || id >= values().length) {
                id = 0;
            }
            return values()[id];
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
