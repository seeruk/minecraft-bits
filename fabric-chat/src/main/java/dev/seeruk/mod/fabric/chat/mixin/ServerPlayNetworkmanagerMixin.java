package dev.seeruk.mod.fabric.chat.mixin;

import eu.pb4.placeholders.api.parsers.LegacyFormattingParser;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.network.message.MessageDecorator;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkmanagerMixin {
    @Redirect(
        method = "method_44900",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/network/message/MessageDecorator;decorate(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/text/Text;)Lnet/minecraft/text/Text;"
        )
    )
    private Text decorate(MessageDecorator instance, ServerPlayerEntity serverPlayerEntity, Text text) {
        var builder = ParserBuilder.of()
            .add(MarkdownLiteParserV1.ALL)
            .add(LegacyFormattingParser.ALL);

        if (serverPlayerEntity.hasPermissionLevel(serverPlayerEntity.getServer().getOpPermissionLevel())) {
            builder.add(TagParser.DEFAULT);
        } else {
            builder.add(TagParser.DEFAULT_SAFE);
        }

        return builder.build().parseNode(text.getString()).toText();
    }
}
