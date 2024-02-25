package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.AssemblerRecipe;
import io.github.reoseah.spacefactory.recipe.ProcessingRecipeType;
import io.github.reoseah.spacefactory.screen.ProcessingMachineScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class AssemblerBlockEntity extends ProcessingMachineBlockEntity<AssemblerRecipe> {
    public static final BlockEntityType<AssemblerBlockEntity> TYPE = new BlockEntityType<>(AssemblerBlockEntity::new, ImmutableSet.of(SpaceFactory.ASSEMBLER), null);

    public AssemblerBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public ProcessingRecipeType<AssemblerRecipe> getRecipeType() {
        return ProcessingRecipeType.ASSEMBLER;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return ProcessingMachineScreenHandler.createAssembler(syncId, this, playerInventory);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getAssemblerEnergyCapacity();
    }

    @Override
    public int getEnergyConsumption() {
        return SpaceFactory.config.getAssemblerEnergyConsumption();
    }
}
