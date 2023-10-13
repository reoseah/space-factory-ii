package io.github.reoseah.spacefactory.block;

import net.minecraft.block.*;
import net.minecraft.block.enums.DoorHinge;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class ThickDoorBlock extends DoorBlock {
    protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 4);
    protected static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 12, 16, 16, 16);
    protected static final VoxelShape EAST_SHAPE = Block.createCuboidShape(12, 0, 0, 16, 16, 16);
    protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 0, 0, 4, 16, 16);

    public ThickDoorBlock(Settings settings, BlockSetType blockSetType) {
        super(settings, blockSetType);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        boolean closed = !state.get(OPEN);
        boolean right = state.get(HINGE) == DoorHinge.RIGHT;
        return switch (direction) {
            case EAST -> closed ? WEST_SHAPE : (right ? SOUTH_SHAPE : NORTH_SHAPE);
            case SOUTH -> closed ? NORTH_SHAPE : (right ? WEST_SHAPE : EAST_SHAPE);
            case WEST -> closed ? EAST_SHAPE : (right ? NORTH_SHAPE : SOUTH_SHAPE);
            case NORTH -> closed ? SOUTH_SHAPE : (right ? EAST_SHAPE : WEST_SHAPE);
            default -> throw new IllegalStateException();
        };
    }
}
