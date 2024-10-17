package dev.seeruk.mod.fabric.propswap.mixin;

import dev.seeruk.mod.fabric.propswap.block.PickaxedBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.PickaxeItem;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PickaxeItemMixin {
    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    public void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!((Object)this instanceof PickaxeItem)) {
            return;
        }

        var world = context.getWorld();
        var pos = context.getBlockPos();
        var state = world.getBlockState(pos);

        if (state.getBlock() instanceof PickaxedBlock pickaxedBlock) {
            pickaxedBlock.seers_propswap$onPickaxeUse(state, world, pos, context.getPlayer(), context.getHand());
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
