package io.github.reoseah.spacefactory.block;

import com.google.common.collect.ImmutableSet;
import io.github.reoseah.spacefactory.SpaceFactory;
import io.github.reoseah.spacefactory.recipe.ExtractorRecipe;
import io.github.reoseah.spacefactory.recipe.ProcessingRecipeType;
import io.github.reoseah.spacefactory.screen.ProcessingMachineScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;

public class ExtractorBlockEntity extends ProcessingMachineBlockEntity<ExtractorRecipe> {
    public static final BlockEntityType<ExtractorBlockEntity> TYPE = new BlockEntityType<>(ExtractorBlockEntity::new, ImmutableSet.of(SpaceFactory.EXTRACTOR), null);

    public ExtractorBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    @Override
    public ProcessingRecipeType<ExtractorRecipe> getRecipeType() {
        return ProcessingRecipeType.EXTRACTOR;
    }

    @Override
    protected ScreenHandler createScreenHandler(int syncId, PlayerInventory playerInventory) {
        return ProcessingMachineScreenHandler.createExtractor(syncId, this, playerInventory);
    }

    @Override
    public int getEnergyCapacity() {
        return SpaceFactory.config.getExtractorEnergyCapacity();
    }

    @Override
    public int getEnergyConsumption() {
        return SpaceFactory.config.getExtractorEnergyConsumption();
    }
}
