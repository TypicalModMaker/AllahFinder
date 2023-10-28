package dev.isnow.allahfinder.discord;

import dev.isnow.allahfinder.AllahFinderImpl;
import dev.isnow.allahfinder.checker.protocol.json.data.FinalResponse;
import dev.isnow.allahfinder.discord.commands.FindNiggerCommand;
import dev.isnow.allahfinder.discord.commands.GetServerCommand;
import dev.isnow.allahfinder.discord.commands.ServersCommand;
import dev.isnow.allahfinder.util.EmbedUtil;
import dev.isnow.allahfinder.util.MessageUtil;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.TimeFormat;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

@RequiredArgsConstructor
public class DiscordIntegration implements EventListener {
    public final String statusMessageID, channelID, hitChannelID, guildID, botToken;
    public Guild mainServer;
    public JDA bot;

    public HashMap<String, ListenerAdapter> commands = new HashMap<>();
    public void runBot() {
        JDABuilder builder = JDABuilder.createDefault(botToken);

        MessageUtil.debug("Bulding bot...");
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        builder.setBulkDeleteSplittingEnabled(false);
        builder.setActivity(Activity.watching(AllahFinderImpl.getInstance().getDatabase().getServerCount() + " Servers"));
        builder.addEventListeners(this);
        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
        bot = builder.build();
        commands.put("Servers", new ServersCommand());
        commands.put("FindNigger", new FindNiggerCommand());
        commands.put("GetServer", new GetServerCommand());
        MessageUtil.debug("Bot built!");
    }
    @Override
    public void onEvent(@NotNull final GenericEvent genericEvent) {
        if(genericEvent instanceof ReadyEvent) {
            mainServer = bot.getGuildById(guildID);
            if(mainServer == null) {
                MessageUtil.error("Failed to find the guild with ID", guildID + "!");
                System.exit(0);
            }
            mainServer.updateCommands().addCommands(
                    Commands.slash("servers", "Get servers.")
                            .addOption(OptionType.INTEGER, "minplayercount", "Minimum player count")
                            .addOption(OptionType.STRING, "serverversion", "Server version")
                            .addOption(OptionType.STRING, "motd", "Server MOTD")
                            .addOption(OptionType.BOOLEAN, "sort", "Sort from Maximum players - lowest [FOR RANDOM SET TO FALSE]"),
                    Commands.slash("findnigger", "Find a black person by their nick/uuid")
                            .addOption(OptionType.STRING, "searchquery", "Nick/UUID", true),
                    Commands.slash("getserver", "Get a server from database by ip and port (optional)")
                            .addOption(OptionType.STRING, "ip", "IP or IP:PORT", true)
                            .addOption(OptionType.INTEGER, "port", "PORT", false)
            ).queue();
            editStatus();
            final Thread t = new Thread(() -> {
                while (true) {
                    MessageUtil.debug("Updating status...");
                    editStatus();
                    bot.getPresence().setActivity(Activity.watching(AllahFinderImpl.getInstance().getDatabase().getServerCount() + " Servers"));
                    try {
                        Thread.sleep(500000);
                    } catch (InterruptedException ignored) {
                    }
                }
            });
            t.start();
            MessageUtil.debug("Bot is ready!");
        } else if(genericEvent instanceof MessageReceivedEvent) {
            final MessageReceivedEvent event = (MessageReceivedEvent) genericEvent;
            MessageUtil.debug("Recieved a message: " + event.getMessage().getContentRaw());
            if(event.getMessage().getContentRaw().startsWith("!cock")) {
                event.getChannel().sendMessage("Cocked").complete();
            }
        } else if(genericEvent instanceof SlashCommandInteractionEvent) {
            for(EventListener listener : commands.values()) {
                listener.onEvent(genericEvent);
            }
        }
    }

    public void editStatus() {
        final TextChannel channel = mainServer.getChannelById(TextChannel.class, channelID);
        if(channel == null) {
            MessageUtil.error("Couldn't find the channel with id:", channelID + "!");
            System.exit(0);
        }


        try {
            final Message message = channel.retrieveMessageById(statusMessageID).complete();
            message.editMessage("ONLINE, Last Edit: " + TimeFormat.RELATIVE.now()).complete();
        } catch (Exception e) {
            channel.sendMessage("ONLINE, Last Edit: " + TimeFormat.RELATIVE.now()).complete();
        }
    }

    public void broadcastHit(FinalResponse server, String ip, int port, boolean ping) {
        mainServer.getChannelById(TextChannel.class, hitChannelID).sendMessage(ping ? "@everyone" : "").setEmbeds(EmbedUtil.getEmbed(server, ip, port).build()).complete();
    }
}
