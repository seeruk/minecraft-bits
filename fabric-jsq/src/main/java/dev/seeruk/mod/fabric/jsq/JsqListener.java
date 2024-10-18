package dev.seeruk.mod.fabric.jsq;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.seeruk.common.jsq.JsqEvent;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;

/**
 * JsqListener is a Redis pub/sub adapter used to listen for join/switch/quit events on a Redis
 * pub/sub channel. Messages are formatted using the given component serializer.
 */
public class JsqListener extends RedisPubSubAdapter<String, byte[]> {
    private final Logger logger;
    private final MinecraftServer server;
    private final ComponentSerializer<Component, Component, String> serializer;

    public JsqListener(
        Logger logger,
        MinecraftServer server,
        ComponentSerializer<Component, Component, String> serializer
    ) {
        this.logger = logger;
        this.server = server;
        this.serializer = serializer;
    }

    /**
     * Handle an incoming message. We expect this to be a join/switch/quit message, ready to be
     * deserialized into a component that can be sent to the whole server.
     */
    @Override
    public void message(String channel, byte[] message) {
        server.executeSync(() -> {
            try {
                var event = JsqEvent.parseFrom(message);

                server.getPlayerManager().getPlayerList().forEach(player ->
                    player.sendMessage(serializer.deserialize(event.getMessage()))
                );
            } catch (InvalidProtocolBufferException e) {
                logger.warn("failed to parse JSQ event proto message", e);
            }
        });
    }
}
