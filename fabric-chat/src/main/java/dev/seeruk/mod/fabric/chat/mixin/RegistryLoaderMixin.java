package dev.seeruk.mod.fabric.chat.mixin;

import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryLoader;
import net.minecraft.registry.RegistryOps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.Map;

@Mixin(RegistryLoader.class)
public class RegistryLoaderMixin {
    @Inject(
        method = "load(Lnet/minecraft/registry/RegistryLoader$RegistryLoadable;Lnet/minecraft/registry/DynamicRegistryManager;Ljava/util/List;)Lnet/minecraft/registry/DynamicRegistryManager$Immutable;",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V",
            ordinal = 0,
            shift = At.Shift.AFTER
        ),
        locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    private static void load(
        @Coerce Object loadable,
        DynamicRegistryManager baseRegistryManager,
        List<RegistryLoader.Entry<?>> entries,
        CallbackInfoReturnable<DynamicRegistryManager.Immutable> cir,
        Map map,
        List<RegistryLoader.Loader<?>> list,
        RegistryOps.RegistryInfoGetter registryInfoGetter
    ) {
        for (var entry : list) {
            var registry = entry.registry();
            if (registry.getKey().equals(RegistryKeys.MESSAGE_TYPE)) {
                // TODO: Inject
            }
        }
    }
}
