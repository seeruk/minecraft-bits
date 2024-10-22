package dev.seeruk.mod.fabric.chat.message;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.seeruk.common.chat.ChatEvent;
import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.text.TextUtils;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.slf4j.Logger;

/**
 * JsqListener is a Redis pub/sub adapter used to listen for join/switch/quit events on a Redis
 * pub/sub channel. Messages are formatted using the given component serializer.
 */
public class ChatListener extends RedisPubSubAdapter<String, byte[]> {

    private final FabricServerAudiences adventure;
    private final Config config;
    private final Logger logger;
    private final MinecraftServer server;

    public ChatListener(
        FabricServerAudiences adventure,
        Config config,
        Logger logger,
        MinecraftServer server
    ) {
        this.adventure = adventure;
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
            var formatted = !event.getFormatted().isEmpty()
                ? TextUtils.deserialize(event.getFormatted())
                : Text.of(event.getMessage());

            server.getPlayerManager().getPlayerList().forEach(player -> {
                var audience = adventure.audience(player);
                audience.sendMessage(formatted);
            });
        });
    }
}
