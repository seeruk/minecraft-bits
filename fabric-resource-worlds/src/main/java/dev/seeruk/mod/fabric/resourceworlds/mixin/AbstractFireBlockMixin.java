package dev.seeruk.mod.fabric.resourceworlds.mixin;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {
    @Inject(
        method = "onBlockAdded",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/dimension/NetherPortal;getNewPortal(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction$Axis;)Ljava/util/Optional;",
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    protected void disableNetherPortalLighting(
        BlockState state,
        World world,
        BlockPos pos,
        BlockState oldState,
        boolean notify,
        CallbackInfo ci
    ) {
        // Disable nether portal ignition in resource worlds
        if (world.getRegistryKey().getValue().toString().startsWith("resource:")) {
            ci.cancel();
        }
    }
}
