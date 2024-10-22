package dev.seeruk.mod.fabric.chat.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.brigadier.context.CommandContext;
import dev.seeruk.mod.fabric.chat.ChatMod;
import dev.seeruk.mod.fabric.chat.event.EmoteMessageSendCallback;
import dev.seeruk.mod.fabric.chat.text.Formatter;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.command.MeCommand;
import net.minecraft.server.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MeCommand.class)
public class MeCommandMixin {
    @ModifyArg(
        method = "method_43645",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/MessageType$Parameters;)V"
        )
    )
    private static SignedMessage onEmoteMessage(SignedMessage message, @Local(argsOnly = true) CommandContext<ServerCommandSource> commandContext) {
        if (commandContext.getSource() instanceof ServerCommandSource commandSource) {
            if (!commandSource.isExecutedByPlayer() || commandSource.getPlayer() == null) {
                return message;
            }

            var config = ChatMod.getInstance().getConfig();
            var player = commandSource.getPlayer();

            var messagePlainText = message.getContent().getString();

            var decorated = Formatter.decoratePlainText(player, messagePlainText, Formatter.canPlayerUseGlobalPlaceholders(player));
            var result = Formatter.applyFormat(config.emoteFormat, player, decorated);

            EmoteMessageSendCallback.EVENT.invoker().interact(player, result, decorated);

            return message.withUnsignedContent(result);
        }
        return message;
    }

    @ModifyArg(
        method = "method_43645",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/network/message/SignedMessage;Lnet/minecraft/server/command/ServerCommandSource;Lnet/minecraft/network/message/MessageType$Parameters;)V"
        ),
        index = 2
    )
    private static MessageType.Parameters modifyMessageType(MessageType.Parameters params, @Local ServerCommandSource commandSource) {
        return MessageType.params(ChatMod.MESSAGE_TYPE_SEER, commandSource);
    }
}
