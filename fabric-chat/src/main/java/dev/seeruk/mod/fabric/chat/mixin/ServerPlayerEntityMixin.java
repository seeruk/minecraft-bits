package dev.seeruk.mod.fabric.chat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.seeruk.mod.fabric.chat.event.DeathMessageSendCallback;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {
    @Inject(
        method = "onDeath",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;send(Lnet/minecraft/network/packet/Packet;Lnet/minecraft/network/PacketCallbacks;)V"
        )
    )
    private void onDeath(DamageSource damageSource, CallbackInfo ci, @Local Text text) {
        DeathMessageSendCallback.EVENT.invoker()
            .interact((ServerPlayerEntity) (Object)this, text);
    }
}
