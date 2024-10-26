package dev.seeruk.mod.fabric.nicks.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.seeruk.mod.fabric.nicks.NicksMod;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.regex.Pattern;

import static net.minecraft.server.command.CommandManager.literal;

public class NickCommand extends AbstractUpdateCommand {

    private static final Pattern nickPattern = Pattern.compile("^[A-z0-9_]{3,15}$");

    public static void register() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            dispatcher.register(literal("nick")
                .then(CommandManager.argument("nickname", StringArgumentType.string())
                    .executes(NickCommand::onNickCommand)
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

        var nick = StringArgumentType.getString(context, "nickname");
        var nickMatcher = nickPattern.matcher(nick);

        if (!nickMatcher.find()) {
            // TODO: Configurable message?
            audience.sendMessage(miniMessage.deserialize("""
                <red>Invalid nickname. Valid nicknames are:<br>
                * Only made up of letters, numbers, and underscores
                * Must not start or end with underscores
                * Must be between 3 and 15 characters in length</red>"""));
            return -1;
        }

        // Update the player's nick
        mod.getStore().setNick(player.getUuid(), nick);

        return finalise(source);
    }
}
