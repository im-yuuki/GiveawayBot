package dev.yuuki.discord.giveawaybot.database.entity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;

public record GiveawayEntity(
    String uuid,
    String name,
    long guildId,
    long channelId,
    long hookId,
    long endAt,
    long hostUid,
    long requiredRoleId,
    Set<Long> participants
) {
    private static final Gson gson = new Gson();
    private static final Type PARTICIPANTS_TYPE = new TypeToken<Set<Long>>() {}.getType();

    public String getParticipantsJson() {
        return gson.toJson(participants);
    }

    public GiveawayEntity setParticipantsFromJson(String jsonString) {
        return new GiveawayEntity(
                uuid,
                name,
                guildId,
                channelId,
                hookId,
                endAt,
                hostUid,
                requiredRoleId,
                gson.fromJson(jsonString, PARTICIPANTS_TYPE)
        );
    }
}
