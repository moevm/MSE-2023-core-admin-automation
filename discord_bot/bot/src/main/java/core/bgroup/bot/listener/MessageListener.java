package core.bgroup.bot.listener;

import core.bgroup.bot.service.MessageHandlingService;
import core.bgroup.bot.service.MessageReadingService;
import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.MessageData;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@EnableScheduling
@Component
public class MessageListener {
    private final MessageReadingService readingService;
    private final MessageHandlingService handlingService;
    private Snowflake lastMessageSnowflake;

    public MessageListener(MessageReadingService readingService, MessageHandlingService handlingService) {
        this.readingService = readingService;
        this.handlingService = handlingService;
        this.lastMessageSnowflake = Snowflake.of(Instant.now());
    }

    @Scheduled(fixedRateString = "${discord.listen-rate}")
    public void listen() {
        List<MessageData> messages = readingService.readMessagesAfter(lastMessageSnowflake);
        lastMessageSnowflake = Snowflake.of(Instant.now());
        if (!messages.isEmpty()) {
            messages.forEach(handlingService::handleMessage);
        }
    }
}
