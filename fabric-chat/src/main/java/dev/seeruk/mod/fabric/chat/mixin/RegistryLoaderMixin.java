package dev.seeruk.mod.fabric.chat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.seeruk.mod.fabric.chat.ChatMod;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.*;
import net.minecraft.text.Decoration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @Inject(
        method = "load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Ljava/util/List;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        )
    )
    @SuppressWarnings("unchecked")
    private static void load(
        RegistryLoader.RegistryLoadable loadable,
        List<RegistryWrapper.Impl<?>> registries,
        List<RegistryLoader.Entry<?>> entries,
        CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir,
        @Local(ordinal = 2) List<RegistryLoader.Loader<?>> list
    ) {
        list.forEach((loader) -> {
            var registry = loader.registry();
            if (registry.getKey().equals(RegistryKeys.MESSAGE_TYPE)) {
                Registry.register(
                    (Registry<MessageType>) registry,
                    ChatMod.MESSAGE_TYPE_SEER,
                    new MessageType(
                        Decoration.ofChat("%2$s"),
                        Decoration.ofChat("%2$s")
                    )
                );
            }
        });
    }
}
