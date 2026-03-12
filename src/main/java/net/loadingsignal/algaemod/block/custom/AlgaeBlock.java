package net.loadingsignal.algaemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class AlgaeBlock extends WaterlilyBlock implements BonemealableBlock {


    //setting up Age property and get methods
    public static final int MAX_AGE = 4;

    public static final IntegerProperty AGE = BlockStateProperties.AGE_4;
    //public static final BooleanProperty NORTH = BooleanProperty.create("north");
    //public static final BooleanProperty EAST = BooleanProperty.create("east");
    //public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    //public static final BooleanProperty WEST = BooleanProperty.create("west");
    //public static final BooleanProperty UP = BooleanProperty.create("up");

    protected IntegerProperty getAgeProperty() {
        return AGE;
    }

   //protected BooleanProperty getNorthProperty(){
    //    return NORTH;
   // }


    public int getMaxAge() {
        return MAX_AGE;
    }

    public int getAge(BlockState state) {
        return state.getValue(this.getAgeProperty());
    }


    public BlockState getStateForAge(int age) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(age));

    }

   //public BlockState getStateForAge(boolean north) {
    //    return this.defaultBlockState().setValue(this.getNorthProperty(), Boolean.valueOf(true));

   // }

    public final boolean isMaxAge(BlockState state) {
        return this.getAge(state) >= this.getMaxAge();
    }

    //public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    //@Nullable
   // @Override
   // public BlockState getStateForPlacement(BlockPlaceContext context) {
       // return this.defaultBlockState().setValue(FACING, Direction.NORTH);
  //  }

    public AlgaeBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)));
       // this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));

       }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    //    builder.add(FACING);
    }



    //voxel shape and collision shape
    private static final VoxelShape SHAPE = Block.box(0,0,0,16,1,16);

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
    protected static final VoxelShape COLLISION_SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.4D, 15.0D);

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return COLLISION_SHAPE;
    }

    //block rules-- placing on water, surviving on water with light, boats break it.
            //may place on water with air above it
    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        FluidState block = level.getFluidState(pos);
        FluidState blockAbove = level.getFluidState(pos.above());
        return (block.getType() == Fluids.WATER) && blockAbove.getType() == Fluids.EMPTY;
    }
            //can survive if water is source block and can see sky or has light.
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        FluidState blockBelow = level.getFluidState(pos.below());
        boolean hasLight = level.getRawBrightness(pos, 0) >= 8 || level.canSeeSky(pos);
        boolean onWater = blockBelow.getType() == Fluids.WATER && blockBelow.isSource();
        return hasLight && onWater;
    }

                //boats break algae when they run into it. (voxel collision shape has to be like it is now so that boats can enter the block).
    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);

        if (level instanceof ServerLevel && entity instanceof Boat) {
            level.destroyBlock(pos, true);
        }
    }

    //bonemeal functionality

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos bpos, BlockState state, boolean b) {
        return !this.isMaxAge(state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource randomSource, BlockPos pos, BlockState sState) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel serverLevel, RandomSource randomSource, BlockPos pos, BlockState state) {

        this.growCrops(serverLevel, pos, state);
        /*int currentLevel = this.getAge(blockState);

        // Cycle the value (0-5), then loop back to 0
        int nextLevel = (currentLevel >= MAX_AGE) ? 0 : currentLevel + 1;

        // Update the block state in the world
        serverLevel.setBlock(blockPos, blockState.setValue(AGE, nextLevel), 3);
        */

    }

    protected int getBonemealAgeIncrease(Level pLevel) {
        return Mth.nextInt(pLevel.random, 2, 5);
    }


    //growing on random ticks

     public boolean isRandomlyTicking(BlockState state) {
              return !this.isMaxAge(state);
          }
      public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
              if (!level.isAreaLoaded(pos, 1))
                  return; // Forge: prevent loading unloaded chunks when checking neighbor's light
              if (level.getRawBrightness(pos, 0) >= 9) {
                  int i = this.getAge(state);

                  if (i < this.getMaxAge()) {
                      if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(level, pos, state, random.nextInt((int) (25.0F / 10) + 1) == 0)) {
                          level.setBlock(pos, this.getStateForAge(i + 1), 2);
                          net.minecraftforge.common.ForgeHooks.onCropsGrowPost(level, pos, state);
                      }
                 }
              }
          }


    public void growCrops(Level level, BlockPos pos, BlockState state) {
        int i = this.getAge(state) + this.getBonemealAgeIncrease(level);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        level.setBlock(pos, this.getStateForAge(i), 2);
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




}
