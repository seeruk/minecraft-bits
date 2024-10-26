package dev.seeruk.mod.fabric.nicks;

import com.google.protobuf.InvalidProtocolBufferException;
import dev.seeruk.mod.fabric.nicks.database.RefreshableStore;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;

import java.util.UUID;

/**
 * NicksListener is a Redis pub/sub adapter used to listen for nickname / colour update events on a
 * Redis pub/sub channel. This is used to refresh our in-memory store.
 */
@RequiredArgsConstructor
public class NicksListener extends RedisPubSubAdapter<String, byte[]> {

    private final Logger logger;
    private final RefreshableStore store;
    private final UUID server;

    @Override
    public void message(String channel, byte[] message) {
        try {
            var event = NicksEvent.parseFrom(message);
            var sender = UUID.fromString(event.getServer());

            if (sender.equals(server)) {
                return;
            }

            store.refresh();
        } catch (InvalidProtocolBufferException e) {
            logger.warn("failed to parse nicks event proto message", e);
        }
    }
}
