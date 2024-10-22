package dev.seeruk.mod.fabric.chat.mixin;

import dev.seeruk.mod.fabric.chat.event.AdvancementMessageSendCallback;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerAdvancementTracker.class)
public class PlayerAdvancementTrackerMixin {
    @Shadow private ServerPlayerEntity owner;

    @ModifyArg(
        method = "method_53637",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V"
        )
    )
    private Text onAdvancement(Text message) {
        AdvancementMessageSendCallback.EVENT.invoker()
            .interact(this.owner, message);

        return message;
    }
}
