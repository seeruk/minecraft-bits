package dev.seeruk.mod.fabric.chat.mixin;

import dev.seeruk.mod.fabric.chat.ChatMod;
import dev.seeruk.mod.fabric.chat.event.ChatMessageSendCallback;
import dev.seeruk.mod.fabric.chat.text.Formatter;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.network.message.MessageType;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {
    @Redirect(
        method = "method_44900",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/message/MessageDecorator;decorate(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"
        )
    )
    private Text decorate(MessageDecorator instance, ServerPlayerEntity player, Text text) {
        // TODO: Do we need to call the original decorator still?
        var config = ChatMod.getInstance().getConfig();
        var message = Formatter.decorateText(player, text, Formatter.canPlayerUseGlobalPlaceholders(player));
        var result = Formatter.applyFormat(config.chatFormat, player, message);

        ChatMessageSendCallback.EVENT.invoker().interact(player, result, message);

        return result;
    }

    /**
     * Override the chat message type to be our own format.
     * See {@link dev.seeruk.mod.fabric.chat.mixin.RegistryLoaderMixin#load} for format.
     */
    @ModifyArg(
        method = "handleDecoratedMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageType$Parameters;)V"
        ),
        index = 2
    )
    private MessageType.Parameters handleDecoratedMessage(MessageType.Parameters params) {
        return MessageType.params(
            ChatMod.MESSAGE_TYPE_SEER,
            ((ServerPlayNetworkHandler)(Object)this).player
        );
    }
}
