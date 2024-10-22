package dev.seeruk.mod.fabric.chat.event;

import dev.seeruk.common.chat.ChatEvent;
import dev.seeruk.common.chat.ChatEventType;
import dev.seeruk.mod.fabric.chat.config.Config;
import dev.seeruk.mod.fabric.chat.text.TextUtils;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.network.ServerPlayerEntity;

@RequiredArgsConstructor
public class RedisPublishListener {
    private final Config config;
    private final StatefulRedisPubSubConnection<String, byte[]> redisConn;

    public void register() {
        AdvancementMessageSendCallback.EVENT.register((player, message) -> {
            var proto = chatEventBuilder(player)
                .setType(ChatEventType.Advancement)
                .setFormatted(TextUtils.serialize(message))
                .setMessage(message.getString())
                .build();

            sendChatEvent(proto);
        });

        ChatMessageSendCallback.EVENT.register((player, formatted, message) -> {
            var proto = chatEventBuilder(player)
                .setType(ChatEventType.Chat)
                .setFormatted(TextUtils.serialize(formatted))
                .setMessage(message.getString())
                .build();

            sendChatEvent(proto);
        });

        DeathMessageSendCallback.EVENT.register((player, message) -> {
            var proto = chatEventBuilder(player)
                .setType(ChatEventType.Death)
                .setFormatted(TextUtils.serialize(message))
                .setMessage(message.getString())
                .build();

            sendChatEvent(proto);
        });

        EmoteMessageSendCallback.EVENT.register((player, formatted, message) -> {
            var proto = chatEventBuilder(player)
                .setType(ChatEventType.Emote)
                .setFormatted(TextUtils.serialize(formatted))
                .setMessage(message.getString())
                .build();

            sendChatEvent(proto);
        });
    }

    private void sendChatEvent(ChatEvent event) {
        redisConn.async().publish(config.redisChannel, event.toByteArray());
    }

    private ChatEvent.Builder chatEventBuilder() {
        return ChatEvent.newBuilder().setServer(config.server);
    }

    private ChatEvent.Builder chatEventBuilder(ServerPlayerEntity player) {
        return chatEventBuilder()
            .setPlayerUuid(player.getUuidAsString())
            .setPlayerName(player.getName().getString());
    }
}
