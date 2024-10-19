package dev.seeruk.mod.fabric.chat.message;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.seeruk.common.chat.ChatEvent;
import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.text.TextUtils;
import eu.pb4.placeholders.api.node.TextNode;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagParser;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/**
 * JsqListener is a Redis pub/sub adapter used to listen for join/switch/quit events on a Redis
 * pub/sub channel. Messages are formatted using the given component serializer.
 */
public class ChatListener extends RedisPubSubAdapter<String, byte[]> {
    private static final NodeParser chatParser = ParserBuilder.of()
        .markdown()
        .legacyAll()
        .add(TagParser.DEFAULT_SAFE)
        .build();

    private final Config config;
    private final Logger logger;
    private final MinecraftServer server;

    public ChatListener(Config config, Logger logger, MinecraftServer server) {
        this.config = config;
        this.logger = logger;
        this.server = server;
    }

    /**
     * Handle an incoming message. We expect this to be a join/switch/quit message, ready to be
     * deserialized into a component that can be sent to the whole server.
     */
    @Override
    public void message(String channel, byte[] proto) {
        ChatEvent event;

        try {
            event = ChatEvent.parseFrom(proto);
        } catch (InvalidProtocolBufferException e) {
            logger.warn("failed to parse chat event proto message", e);
            return;
        }

        if (config.server.equals(event.getServer())) {
            return; // Skip events that came from this server
        }

        server.executeSync(() -> {
            var formatted = !event.getFormatted().isEmpty() ? TextUtils.deserialize(event.getFormatted()) : Text.empty();

            server.getPlayerManager().getPlayerList().forEach(player ->
                player.sendMessage(chatParser.parseNode(TextNode.convert(formatted)).toText())
            );
        });
    }
}
