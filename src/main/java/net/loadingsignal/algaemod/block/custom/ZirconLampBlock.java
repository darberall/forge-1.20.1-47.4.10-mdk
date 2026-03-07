package net.loadingsignal.algaemod.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.BlockHitResult;

public class ZirconLampBlock extends Block {
    public static final IntegerProperty LIT = IntegerProperty.create("lit", 0, 1);
    public ZirconLampBlock(BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, 0));
       }

    /*
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos,
                                 Player player, InteractionHand hand, BlockHitResult result) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            level.setBlock(blockPos, state.setValue(LIT), 3);
        }
        return super.use(state, level, blockPos, player, hand, result);
    }
*/
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            int currentLevel = state.getValue(LIT);

            // Cycle the value (0-5), then loop back to 0
            int nextLevel = (currentLevel >= 1) ? 0 : currentLevel + 1;

            // Update the block state in the world
            level.setBlock(pos, state.setValue(LIT, nextLevel), 3);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.CONSUME;
    }



    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }

}
