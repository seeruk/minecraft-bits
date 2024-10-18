package dev.seeruk.mod.fabric.propswap.mixin;

import dev.seeruk.mod.fabric.propswap.block.PickaxedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.WallBlock;
import net.minecraft.block.enums.WallShape;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

@Mixin(WallBlock.class)
public class WallBlockMixin implements PickaxedBlock {
    @Override
    public void seers_propswap$onPickaxeUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if (!world.isClient && hand.equals(Hand.MAIN_HAND)) {
            if (player.isSneaking()) {
                var maybeDirection = this.facingDirectionProperty(player);
                if (maybeDirection.isPresent()) {
                    var direction = maybeDirection.get();

                    world.setBlockState(pos, state.with(direction, nextWallShape(state.get(direction))), 18);
                }
            } else {
                world.setBlockState(
                    pos,
                    state.with(WallBlock.NORTH_SHAPE, WallShape.NONE)
                        .with(WallBlock.EAST_SHAPE, WallShape.NONE)
                        .with(WallBlock.SOUTH_SHAPE, WallShape.NONE)
                        .with(WallBlock.WEST_SHAPE, WallShape.NONE),
                    18
                );
            }
        }
    }

    @Unique
    private Optional<EnumProperty<WallShape>> facingDirectionProperty(PlayerEntity player) {
        return switch (player.getHorizontalFacing()) {
            case NORTH -> Optional.of(WallBlock.NORTH_SHAPE);
            case EAST -> Optional.of(WallBlock.EAST_SHAPE);
            case SOUTH -> Optional.of(WallBlock.SOUTH_SHAPE);
            case WEST -> Optional.of(WallBlock.WEST_SHAPE);
            default -> Optional.empty();
        };
    }

    @Unique
    private WallShape nextWallShape(WallShape current) {
        return switch (current) {
            case NONE -> WallShape.LOW;
            case LOW -> WallShape.TALL;
            case TALL -> WallShape.NONE;
        };
    }
}
