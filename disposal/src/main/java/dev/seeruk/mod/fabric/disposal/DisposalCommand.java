package dev.seeruk.mod.fabric.disposal;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class DisposalCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(CommandManager.literal("disposal").executes(context -> {
                ServerCommandSource source = context.getSource();

                if (source.getPlayer() != null) {
                    ServerPlayerEntity player = source.getPlayer();

                    source.sendFeedback(() -> Text.literal("Opening disposal..."), false);
                    player.openHandledScreen(new DisposalScreenHandlerFactory());
                }

                return 1;
            }));
        });
    }
}
