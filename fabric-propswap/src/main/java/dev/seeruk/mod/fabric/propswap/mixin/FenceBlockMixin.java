package dev.seeruk.mod.fabric.propswap.mixin;

import dev.seeruk.mod.fabric.propswap.block.AxedBlock;
import dev.seeruk.mod.fabric.propswap.block.ToolableHorizontalConnectingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.FenceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FenceBlock.class)
public class FenceBlockMixin implements AxedBlock {
    public void seers_propswap$onAxeUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if ((Object)this instanceof ToolableHorizontalConnectingBlock toolableBlock) {
            toolableBlock.seers_propswap$handleToolUse(state, world, pos, player, hand);
        }
    }
}
