package dev.seeruk.mod.fabric.chat.mixin;

import dev.seeruk.mod.fabric.chat.ChatMod;
import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.PlaceholderContext;
import eu.pb4.placeholders.api.Placeholders;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Unique
    private static final NodeParser nameParser = ParserBuilder.of()
        .legacyAll()
        .add(TagParser.DEFAULT_SAFE)
        .build();

    @ModifyArg(
        method = "getDisplayName",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/scoreboard/Team;decorateName(Lnet/minecraft/scoreboard/AbstractTeam;Lnet/minecraft/text/Text;)Lnet/minecraft/text/MutableText;"
        )
    )
    public Text formatName(Text original) {
        var config = ChatMod.getInstance().getConfig();

        return Placeholders.parseText(
            nameParser.parseText(config.displayNameFormat, ParserContext.of()),
            PlaceholderContext.of((PlayerEntity)(Object)this)
        );
    }
}
