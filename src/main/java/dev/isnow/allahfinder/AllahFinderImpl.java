package dev.isnow.allahfinder;

import com.mongodb.MongoCredential;
import dev.isnow.allahfinder.config.Config;
import dev.isnow.allahfinder.database.Database;
import dev.isnow.allahfinder.discord.DiscordIntegration;
import dev.isnow.allahfinder.finder.MasscanIntegration;
import dev.isnow.allahfinder.util.FileUtil;
import dev.isnow.allahfinder.util.MessageUtil;
import lombok.Getter;

import java.io.File;

@Getter
public class AllahFinderImpl {

    public static boolean DEBUG = System.getenv().containsKey("ALLAHDEBUG");

    private final Config configuration;
    private final Database database;
    private final DiscordIntegration bot;
    private MasscanIntegration process;

    @Getter
    private static AllahFinderImpl instance;

    public AllahFinderImpl(String[] args) {
        instance = this;
        final float startTime = System.currentTimeMillis();
        System.out.println("\n  ___   _  _         _     ______  _             _             \n" +
                " / _ \\ | || |       | |    |  ___|(_)           | |            \n" +
                "/ /_\\ \\| || |  __ _ | |__  | |_    _  _ __    __| |  ___  _ __ \n" +
                "|  _  || || | / _` || '_ \\ |  _|  | || '_ \\  / _` | / _ \\| '__|\n" +
                "| | | || || || (_| || | | || |    | || | | || (_| ||  __/| |   \n" +
                "\\_| |_/|_||_| \\__,_||_| |_|\\_|    |_||_| |_| \\__,_| \\___||_|   \n");

        if(DEBUG) {
            MessageUtil.info("Running in debugging mode");
        }

        MessageUtil.info("Loading config...");
        final File configFile = new File("config.yml");
        if(!configFile.exists()) {
            MessageUtil.debug("Creating new config...");
            final boolean copied = FileUtil.copyResource("/config.yml", configFile);
            if(!copied) {
                MessageUtil.error("Failed to create default config!");
            } else {
                MessageUtil.info("Created new config! please edit it and restart the app.");
            }
            System.exit(0);;
        }
        configuration = new Config(new File("config.yml"));
        MessageUtil.info("Config loaded!");

        MessageUtil.info("Loading database...");
        final MongoCredential credential = MongoCredential.createCredential(configuration.getUsername(), configuration.getAuthDatabase(), configuration.getPassword().toCharArray());
        database = new Database(credential, configuration.getIp(), configuration.getServersDatabase());
        MessageUtil.info("Database loaded!");

        MessageUtil.info("Loading discord bot...");
        bot = new DiscordIntegration(configuration.getMessageID(), configuration.getChannelID(), configuration.getHitChannelID(), configuration.getGuildID(), configuration.getBotToken());
        bot.runBot();
        MessageUtil.info("Discord bot loaded!");

        MessageUtil.info("Starting masscan...");
        Thread masscanThread = new Thread(() -> {
            process = new MasscanIntegration(configuration.getIpsToScan(), configuration.getPortsToScan(), configuration.getExtraParams());
        });
        masscanThread.start();

        MessageUtil.info("Finished loading in " + (System.currentTimeMillis() - (startTime) / 1000 + " Seconds!"));
    }
}
