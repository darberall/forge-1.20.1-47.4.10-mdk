package net.loadingsignal.algaemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;

public class AlgaeBlock extends WaterlilyBlock implements BonemealableBlock {
    public static final int MAX_AGE = 4;
    //public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public AlgaeBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)));
       }


    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return MAX_AGE;
    }

    public int getAge(BlockState pState) {
        return pState.getValue(this.getAgeProperty());
    }



    public BlockState getStateForAge(int pAge) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(pAge));
    }

    public final boolean isMaxAge(BlockState pState) {
        return this.getAge(pState) >= this.getMaxAge();
    }



    /*
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            int currentLevel = state.getValue(AGE);

            // Cycle the value (0-5), then loop back to 0
            int nextLevel = (currentLevel >= MAX_AGE) ? 0 : currentLevel + 1;

            // Update the block state in the world
            level.setBlock(pos, state.setValue(AGE, nextLevel), 3);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }
*/


    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos blockPos, BlockState blockState, boolean b) {
        return !this.isMaxAge(blockState);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos blockPos, BlockState blockState) {
        int currentLevel = this.getAge(blockState);

        // Cycle the value (0-5), then loop back to 0
        int nextLevel = (currentLevel >= MAX_AGE) ? 0 : currentLevel + 1;

        // Update the block state in the world
        serverLevel.setBlock(blockPos, blockState.setValue(AGE, nextLevel), 3);
    }
}
