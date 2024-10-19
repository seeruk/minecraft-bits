package dev.seeruk.mod.fabric.chat.mixin;

import eu.pb4.placeholders.api.parsers.LegacyFormattingParser;
import eu.pb4.placeholders.api.parsers.MarkdownLiteParserV1;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @ModifyArg(
        method = "getDisplayName",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"
        )
    )
    public Text formatName(Text original) {
        var builder = ParserBuilder.of()
            .add(TagParser.DEFAULT_SAFE)
            .add(MarkdownLiteParserV1.ALL)
            .add(LegacyFormattingParser.ALL)
            .build();

        return builder.parseNode("≈Seer").toText();
    }
}
