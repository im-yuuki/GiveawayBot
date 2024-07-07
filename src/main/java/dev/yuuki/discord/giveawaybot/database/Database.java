package dev.yuuki.discord.giveawaybot.database;

import dev.yuuki.discord.giveawaybot.database.entity.GiveawayCreateTicketEntity;
import dev.yuuki.discord.giveawaybot.database.entity.GiveawayEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class Database {
    private static long getUTCNowTimestamp() {
        return LocalDateTime.now(ZoneOffset.UTC).toEpochSecond(ZoneOffset.UTC);
    }

    private static final Logger logger = LoggerFactory.getLogger(Database.class);
    
    private static final String INIT_SCRIPT =
              "CREATE TABLE IF NOT EXISTS giveaways ("
            + "    uuid         TEXT    NOT NULL UNIQUE,"
            + "    name         TEXT    NOT NULL,"
            + "    guild_id     INTEGER NOT NULL,"
            + "    channel_id   INTEGER NOT NULL,"
            + "    hook_id      INTEGER NOT NULL,"
            + "    created_at   TEXT    NOT NULL,"
            + "    duration     TEXT    NOT NULL,"
            + "    ended        INTEGER NOT NULL DEFAULT 0,"
            + "    host_uid     INTEGER NOT NULL,"
            + "    participants TEXT,"
            + "    PRIMARY KEY(uuid)    "
            + ");"
            + "CREATE TABLE IF NOT EXISTS guilds ("
            + "    guild_id        INTEGER NOT NULL UNIQUE,"
            + "    embed_color     INTEGER NOT NULL DEFAULT 16749009,"
            + "    enroll_emoji    TEXT    NOT NULL,"
            + "    banner_url      TEXT    DEFAULT NULL,"
            + "    thumbnail_url   TEXT    DEFAULT NULL,"
            + "    manager_role_id INTEGER DEFAULT NULL,"
            + "    PRIMARY KEY(guild_id),"
            + "    FOREIGN KEY (guild_id) REFERENCES giveaways(guild_id)"
            + "    ON UPDATE NO ACTION ON DELETE NO ACTION"
            + ");";

    private final Connection connection;
    private final HashMap<String, GiveawayEntity> storage = new HashMap<>();
    private final LRUCache<String, GiveawayCreateTicketEntity> createTicketStorage = new LRUCache<>(1000, 300);

    public Database() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:giveaways.sqlite");
        logger.info("Connected to database");
        connection.createStatement().executeUpdate(INIT_SCRIPT);
        logger.info("Initalize script executed");
    }

    public GiveawayCreateTicketEntity createGiveaway(String name, long guildId, long channelId, long duration, long hostUid) {
        GiveawayCreateTicketEntity entity = new GiveawayCreateTicketEntity(
                UUID.randomUUID().toString(),
                name, "",
                guildId, channelId, duration, hostUid, 0
        );
        createTicketStorage.setCache(entity.uuid(), entity);
        return entity;
    }


}
