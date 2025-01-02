package dev.seeruk.mod.fabric.lore;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ItemLoreCommand {
    public static final SimpleCommandExceptionType EMPTY_HAND_EXCEPTION =
        new SimpleCommandExceptionType(Text.of("Your main hand is empty"));

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, env) -> {
            dispatcher.register(CommandManager.literal("itemlore")
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

                            stack.set(DataComponentTypes.LORE, stack.getDefaultComponents().get(DataComponentTypes.LORE));

                            source.sendFeedback(() -> Text.literal("Item lore reset!"), false);
                        }

                        return 1;
                    })
                )
                .then(literal("add")
                    .then(argument("lore", StringArgumentType.greedyString())
                        .executes(context -> {
                            var source = context.getSource();
                            if (source.getPlayer() != null) {
                                var player = source.getPlayer();
                                var stack = player.getMainHandStack();

                                if (stack.isEmpty()) {
                                    throw EMPTY_HAND_EXCEPTION.create();
                                }

                                LoreEditor.addLoreLine(stack, StringArgumentType.getString(context, "lore"));

                                source.sendFeedback(() -> Text.literal("Item lore added!"), false);
                            }

                            return 1;
                        })
                    )
                )
                .then(literal("insert")
                    .then(argument("line", IntegerArgumentType.integer(1, LoreEditor.MAX_LINES))
                        .then(argument("lore", StringArgumentType.greedyString())
                            .executes(context -> {
                                var source = context.getSource();
                                if (source.getPlayer() != null) {
                                    var player = source.getPlayer();
                                    var stack = player.getMainHandStack();

                                    if (stack.isEmpty()) {
                                        throw EMPTY_HAND_EXCEPTION.create();
                                    }

                                    LoreEditor.insertLoreLine(
                                        stack,
                                        IntegerArgumentType.getInteger(context, "line"),
                                        StringArgumentType.getString(context, "lore")
                                    );

                                    source.sendFeedback(() -> Text.literal("Item lore inserted!"), false);
                                }

                                return 1;
                            })
                        )
                    )
                )
                .then(literal("set")
                    .then(argument("line", IntegerArgumentType.integer(1, LoreEditor.MAX_LINES))
                        .then(argument("lore", StringArgumentType.greedyString())
                            .executes(context -> {
                                var source = context.getSource();
                                if (source.getPlayer() != null) {
                                    var player = source.getPlayer();
                                    var stack = player.getMainHandStack();

                                    if (stack.isEmpty()) {
                                        throw EMPTY_HAND_EXCEPTION.create();
                                    }

                                    LoreEditor.setLoreLine(
                                        stack,
                                        IntegerArgumentType.getInteger(context, "line"),
                                        StringArgumentType.getString(context, "lore")
                                    );

                                    source.sendFeedback(() -> Text.literal("Item lore set!"), false);
                                }

                                return 1;
                            })
                        )
                    )
                )
                .then(literal("remove")
                    .then(argument("line", IntegerArgumentType.integer(1, LoreEditor.MAX_LINES))
                        .executes(context -> {
                            var source = context.getSource();
                            if (source.getPlayer() != null) {
                                var player = source.getPlayer();
                                var stack = player.getMainHandStack();

                                if (stack.isEmpty()) {
                                    throw EMPTY_HAND_EXCEPTION.create();
                                }

                                LoreEditor.removeLoreLine(stack, IntegerArgumentType.getInteger(context, "line"));

                                source.sendFeedback(() -> Text.literal("Item lore set!"), false);
                            }

                            return 1;
                        })
                    )
                )
            );
        });
    }
}
