package dev.seeruk.mod.fabric.propswap.mixin;

import dev.seeruk.mod.fabric.propswap.block.ToolableHorizontalConnectingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalConnectingBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(HorizontalConnectingBlock.class)
public class HorizontalConnectingBlockMixin implements ToolableHorizontalConnectingBlock {

    @Override
    public void seers_propswap$handleToolUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (!world.isClient && hand.equals(Hand.MAIN_HAND)) {
            if (player.isSneaking()) {
                this.facingDirectionProperty(player).ifPresent(direction -> {
                    world.setBlockState(pos, state.with(direction, !state.get(direction)), 18);
                });
            } else {
                world.setBlockState(
                    pos,
                    state.with(HorizontalConnectingBlock.NORTH, false)
                        .with(HorizontalConnectingBlock.EAST, false)
                        .with(HorizontalConnectingBlock.SOUTH, false)
                        .with(HorizontalConnectingBlock.WEST, false),
                    18
                );
            }
        }
    }

    @Unique
    private Optional<BooleanProperty> facingDirectionProperty(PlayerEntity player) {
        return switch (player.getHorizontalFacing()) {
            case NORTH -> Optional.of(HorizontalConnectingBlock.NORTH);
            case EAST -> Optional.of(HorizontalConnectingBlock.EAST);
            case SOUTH -> Optional.of(HorizontalConnectingBlock.SOUTH);
            case WEST -> Optional.of(HorizontalConnectingBlock.WEST);
            default -> Optional.empty();
        };
    }
}
