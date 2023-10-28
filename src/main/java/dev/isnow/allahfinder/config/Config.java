package dev.isnow.allahfinder.config;

import dev.isnow.allahfinder.util.MessageUtil;
import lombok.Getter;
import org.bspfsystems.yamlconfiguration.configuration.ConfigurationSection;
import org.bspfsystems.yamlconfiguration.configuration.InvalidConfigurationException;
import org.bspfsystems.yamlconfiguration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Config {

    private final YamlConfiguration config = new YamlConfiguration();

    @Getter
    private final String channelID, hitChannelID, guildID, messageID, botToken;
    @Getter
    private final String serversDatabase, authDatabase, username, password, ip;
    @Getter
    private final String ipsToScan, portsToScan, extraParams;
    public Config(File file) {
        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            MessageUtil.error("FAILED TO LOAD CONFIG");
            throw new RuntimeException(e);
        }

        // DISCORD
        MessageUtil.debug("Loading discord configuration...");
        final ConfigurationSection discord = loadSection("discord");
        final ConfigurationSection discordStatus = loadSection("discord.status");
        botToken = discord.getString("token");
        guildID = discord.getString("guild-id");
        hitChannelID = discord.getString("hit-channel-id");
        messageID = discordStatus.getString("status-message-id");
        channelID = discordStatus.getString("status-message-channel-id");
        MessageUtil.debug("Loaded discord configuration!");

        // DATABASE
        MessageUtil.debug("Loading database configuration...");
        final ConfigurationSection database = loadSection("database");
        final ConfigurationSection databaseAuth = loadSection("database.auth");
        serversDatabase = database.getString("database");
        ip = database.getString("ip");
        authDatabase = databaseAuth.getString("auth-database");
        username = databaseAuth.getString("username");
        password = databaseAuth.getString("password");
        MessageUtil.debug("Loaded database configuration!");

        MessageUtil.debug("Loading scanning configuration...");
        final ConfigurationSection scanning = loadSection("scanning");
        ipsToScan = scanning.getString("ip-range");
        portsToScan = scanning.getString("port-range");
        extraParams = scanning.getString("masscan-options");
    }
    private ConfigurationSection loadSection(String sectionName) {
        ConfigurationSection section = config.getConfigurationSection(sectionName);
        if(section == null) {
            MessageUtil.error("FAILED TO LOAD CONFIG | Section:", sectionName);
            throw new RuntimeException();
        }
        return section;
    }
}
