package dev.seeruk.mod.fabric.nicks.mixin;

import dev.seeruk.mod.fabric.nicks.text.PlayerNicknameText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @ModifyArg(
        method = "getDisplayName",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"
        )
    )
    public Text formatName(Text original) {
        return PlayerNicknameText.get((PlayerEntity)(Object)this);
    }
}
