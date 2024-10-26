package dev.seeruk.mod.fabric.nicks.mixin;

import dev.seeruk.mod.fabric.nicks.NicksMod;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagParser;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {

    // TODO: Not here
    private static final Map<UUID, String> playerColours = new HashMap<>();
    private static final Map<UUID, String> playerNicks = new HashMap<>();

    // We use a stripped down MiniMessage formatter, only supporting colours.
    private static final MiniMessage miniMessage =
        MiniMessage.builder()
            .tags(TagResolver.builder()
                .resolver(StandardTags.color())
                .resolver(StandardTags.reset())
                // TODO: Maybe in the future?
                //.resolver(StandardTags.decorations())
                //.resolver(StandardTags.rainbow())
                //.resolver(StandardTags.gradient())
                .build()
            )
            .build();

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
        if (!((PlayerEntity)(Object)this instanceof ServerPlayerEntity)) {
            return original;
        }

        // TODO: All of this stuff needs to be somewhere else really, to handle the placeholder

        // We need a string to use MiniMessage on it sadly
        var stringName = original.getString();
        // Get the player by their UUID, so their nicks persist across name changes
        var uuid = ((PlayerEntity)(Object)this).getUuid();
        // Get the customised values
        var colour = playerColours.getOrDefault(uuid, "reset");
        var nick = playerNicks.getOrDefault(uuid, stringName);
        // Create the coloured component
        var component = miniMessage.deserialize(String.format("<%1$s>%2$s</%1$s>", colour, nick));

        return NicksMod.getInstance().getAdventure().toNative(component);
    }
}
