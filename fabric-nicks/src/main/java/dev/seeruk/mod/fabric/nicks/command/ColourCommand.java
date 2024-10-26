package dev.seeruk.mod.fabric.nicks.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.seeruk.mod.fabric.nicks.NicksMod;
import dev.seeruk.mod.fabric.nicks.command.suggestion.ColourSuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Set;
import java.util.TreeSet;

import static net.minecraft.server.command.CommandManager.literal;

public class ColourCommand extends AbstractUpdateCommand {

    private static final Set<String> colours = prepareColours();

    public static void register() {
        registerAs("color");
        registerAs("colour");
    }

    private static void registerAs(String name) {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal(name)
                .then(CommandManager.argument("value", StringArgumentType.string())
                    .suggests(new ColourSuggestionProvider(colours))
                    .executes(ColourCommand::onNickCommand)
                )
            );
        }));
    }

    private static int onNickCommand(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        if (source.getPlayer() == null) {
            return 0;
        }

        if (detectSpam(source)) {
            return 0;
        }

        var mod = NicksMod.getInstance();
        var player = source.getPlayer();
        var adventure = mod.getAdventure();
        var audience = adventure.audience(source).audience();

        var colour = StringArgumentType.getString(context, "value");

        if (!colours.contains(colour)) {
            // TODO: Configurable message?
            audience.sendMessage(miniMessage.deserialize(String.format(
                "<red>Invalid colour. Available colours are: %s</red>",
                String.join(", ", colours)
            )));
            return -1;
        }

        // Update the player's colour
        mod.getStore().setColour(player.getUuid(), colour);

        return finalise(source);
    }

    private static Set<String> prepareColours() {
        var colours = NamedTextColor.NAMES.keys().stream().sorted().toList();
        var result = new TreeSet<>(colours);
        // Special case for reset at the end...
        result.add("reset");
        // TODO: Configurable deny list for colours?
        return result;
    }
}
