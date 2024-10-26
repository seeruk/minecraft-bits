package dev.seeruk.mod.fabric.nicks.text;

import dev.seeruk.mod.fabric.nicks.NicksMod;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class PlayerNicknameText {

    private static final MiniMessage miniMessage =
        // We use a stripped down MiniMessage formatter, only supporting colours.
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

    public static Text get(PlayerEntity player) {
        var mod = NicksMod.getInstance();
        var adventure = mod.getAdventure();
        var store = mod.getStore();

        var username = player.getGameProfile().getName();
        var uuid = player.getUuid();

        // Get the customised values
        var colour = store.getColours().getOrDefault(uuid, "reset");
        var nick = store.getNicks().getOrDefault(uuid, username);

        // TODO: Configurable prefix
        if (!nick.equals(player.getName().getString())) {
            nick = "≈" + nick;
        }

        // Create the coloured component
        var component = miniMessage.deserialize(String.format("<%1$s>%2$s</%1$s>", colour, nick));

        return adventure.toNative(component);
    }

    public static Text getUnformatted(PlayerEntity player) {
        var mod = NicksMod.getInstance();
        var store = mod.getStore();

        var username = player.getGameProfile().getName();
        var uuid = player.getUuid();

        // Get the customised values
        var nick = store.getNicks().getOrDefault(uuid, username);

        // TODO: Configurable prefix
        if (!nick.equals(player.getName().getString())) {
            nick = "≈" + nick;
        }

        return Text.of(nick);
    }
}
