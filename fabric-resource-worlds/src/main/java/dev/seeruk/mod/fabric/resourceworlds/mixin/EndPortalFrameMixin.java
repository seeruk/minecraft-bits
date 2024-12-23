package dev.seeruk.mod.fabric.resourceworlds.mixin;

import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderEyeItem.class)
public class EndPortalFrameMixin {

    @Inject(
        method = "useOnBlock",
        at = @At("HEAD"),
        cancellable = true
    )
    protected void disableEndPortalCompletion(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        var world = context.getWorld();
        // Disable nether portal ignition in resource worlds
        if (world.getRegistryKey().getValue().toString().startsWith("resource:")) {
            cir.setReturnValue(ActionResult.PASS);
        }
    }
}
