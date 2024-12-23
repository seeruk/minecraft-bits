package dev.seeruk.mod.fabric.resourceworlds.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherPortalBlock.class)
public class NetherPortalBlockMixin {

    @Inject(
        method = "onEntityCollision",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void disablePortalCollision(BlockState state, World world, BlockPos pos, Entity entity, CallbackInfo ci) {
        // Disable nether portal ignition in resource worlds
        if (world.getRegistryKey().getValue().toString().startsWith("resource:")) {
            ci.cancel();
        }
    }
}
