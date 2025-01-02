package dev.seeruk.mod.fabric.lore;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ItemNameCommand {
    public static final SimpleCommandExceptionType EMPTY_HAND_EXCEPTION =
        new SimpleCommandExceptionType(Text.of("Your main hand is empty"));

    private static final LegacyComponentSerializer serialiser = LegacyComponentSerializer.legacyAmpersand();

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(CommandManager.literal("itemname")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .executes(context -> 0) // TODO: Show help?
                .then(literal("reset")
                    .executes(context -> {
                        var source = context.getSource();
                        if (source.getPlayer() != null) {
                            var player = source.getPlayer();
                            var stack = player.getMainHandStack();

                            if (stack.isEmpty()) {
                                throw EMPTY_HAND_EXCEPTION.create();
                            }

                            stack.remove(DataComponentTypes.CUSTOM_NAME);

                            source.sendFeedback(() -> Text.literal("Item name reset!"), false);
                        }

                        return 1;
                    })
                )
                .then(literal("set")
                    .then(argument("name", StringArgumentType.greedyString())
                        .executes(context -> {
                            var source = context.getSource();
                            if (source.getPlayer() != null) {
                                var player = source.getPlayer();
                                var stack = player.getMainHandStack();

                                if (stack.isEmpty()) {
                                    throw EMPTY_HAND_EXCEPTION.create();
                                }

                                var adventure = LoreMod.getInstance().getAdventure();

                                stack.set(
                                    DataComponentTypes.CUSTOM_NAME,
                                    adventure.asNative(serialiser.deserialize(
                                        StringArgumentType.getString(context, "name")
                                    ))
                                );

                                source.sendFeedback(() -> Text.literal("Item name set!"), false);
                            }

                            return 1;
                        })
                    )
                )
            );
        });
    }
}
