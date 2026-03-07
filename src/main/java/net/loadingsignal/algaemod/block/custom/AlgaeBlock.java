package net.loadingsignal.algaemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.IceBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AlgaeBlock extends WaterlilyBlock implements BonemealableBlock {
    public static final int MAX_AGE = 4;
    //public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;

    public AlgaeBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)));
       }

    @Override
    protected boolean mayPlaceOn(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        FluidState $$3 = pLevel.getFluidState(pPos);
        FluidState $$4 = pLevel.getFluidState(pPos.above());
        return ($$3.getType() == Fluids.WATER) && $$4.getType() == Fluids.EMPTY;
    }
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        FluidState blockBelow = level.getFluidState(pos.below());
        boolean hasLight = level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
        boolean onWater = blockBelow.getType() == Fluids.WATER && blockBelow.isSource();
        return hasLight && onWater;
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


    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private static final VoxelShape SHAPE = Block.box(0,0,0,16,1,16);

    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.4D, 15.0D);

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
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

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        // Check if the entity is a boat and if we are on the server
        if (level instanceof ServerLevel && entity instanceof Boat) {
            level.destroyBlock(pos, true);
        }
    }

}
