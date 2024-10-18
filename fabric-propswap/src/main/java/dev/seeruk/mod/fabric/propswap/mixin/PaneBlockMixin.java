package dev.seeruk.mod.fabric.propswap.mixin;

import dev.seeruk.mod.fabric.propswap.block.PickaxedBlock;
import dev.seeruk.mod.fabric.propswap.block.ToolableHorizontalConnectingBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PaneBlock.class)
public class PaneBlockMixin implements PickaxedBlock {
    @Override
    public void seers_propswap$onPickaxeUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand) {
        if ((Object)this instanceof ToolableHorizontalConnectingBlock toolableBlock) {
            toolableBlock.seers_propswap$handleToolUse(state, world, pos, player, hand);
        }
    }
}
