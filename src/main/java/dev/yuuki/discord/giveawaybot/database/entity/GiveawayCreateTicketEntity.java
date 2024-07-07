package dev.yuuki.discord.giveawaybot.database.entity;

public record GiveawayCreateTicketEntity(
        String uuid,
        String name,
        String description,

        long guildId,
        long channelId,
        long duration,
        long hostUid,
        long requiredRoleId
) {
}
