package dev.seeruk.mod.fabric.nicks.command.suggestion;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
public class ColourSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    private final Set<String> colours;

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder) {
        for (String colour : colours) {
            builder.suggest(colour);
        }

        return builder.buildFuture();
    }
}
