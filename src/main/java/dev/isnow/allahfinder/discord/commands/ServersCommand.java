package dev.isnow.allahfinder.discord.commands;

import dev.isnow.allahfinder.AllahFinderImpl;
import dev.isnow.allahfinder.util.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bson.Document;

public class ServersCommand extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("servers")) {
            event.deferReply().queue();

            final OptionMapping minPlayerCount = event.getOption("minplayercount");
            final OptionMapping serverVersionArg = event.getOption("serverversion");
            final OptionMapping motd = event.getOption("motd");
            final OptionMapping sort = event.getOption("sort");

            final Document[] servers = AllahFinderImpl.getInstance().getDatabase().getServers(minPlayerCount, serverVersionArg, sort, motd);

            if(servers == null || servers.length == 0) {
                event.getHook().sendMessage("Couldn't find shit bro").queue();
                return;
            }

            for(final Document server : servers) {
                event.getChannel().sendMessageEmbeds(EmbedUtil.getEmbed(server).build()).complete();
            }
            event.getHook().sendMessage("DONE!").queue();
        }
    }
}
