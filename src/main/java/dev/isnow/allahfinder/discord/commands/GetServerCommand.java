package dev.isnow.allahfinder.discord.commands;

import dev.isnow.allahfinder.AllahFinderImpl;
import dev.isnow.allahfinder.util.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bson.Document;

public class GetServerCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("getserver")) {
            event.deferReply().queue();

            final OptionMapping ip = event.getOption("ip");
            final OptionMapping port = event.getOption("port");

            if(ip == null) {
                event.getHook().sendMessage("Couldn't find it bro").queue();
                return;
            }

            Document server;
            if(port != null) {
                server = AllahFinderImpl.getInstance().getDatabase().getServer(ip.getAsString(), port.getAsInt());
            } else {
                String[] split = ip.getAsString().split(":");

                if(split.length < 2) {
                    final Document[] servers = AllahFinderImpl.getInstance().getDatabase().getServers(ip.getAsString());
                    if(servers == null || servers.length == 0) {
                        event.getHook().sendMessage("Couldn't find shit bro").queue();
                        return;
                    }
                    for(final Document foundServer : servers) {
                        event.getChannel().sendMessageEmbeds(EmbedUtil.getEmbed(foundServer).build()).complete();
                    }
                    return;
                }

                server = AllahFinderImpl.getInstance().getDatabase().getServer(split[0], split[1]);
            }

            if(server == null) {
                event.getHook().sendMessage("Couldn't find it bro").queue();
                return;
            }

            event.getChannel().sendMessageEmbeds(EmbedUtil.getEmbed(server).build()).complete();
            event.getHook().sendMessage("DONE!").queue();
        }
    }
}
