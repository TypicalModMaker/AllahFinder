package dev.isnow.allahfinder.discord.commands;

import dev.isnow.allahfinder.AllahFinderImpl;
import dev.isnow.allahfinder.util.EmbedUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bson.Document;

public class FindNiggerCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equalsIgnoreCase("findnigger")) {
            event.deferReply().queue();

            final OptionMapping searchQuery = event.getOption("searchquery");

            if(searchQuery != null && (searchQuery.getAsString().contains("UUID") || searchQuery.getAsString().contains(":"))) {
                event.getHook().sendMessage("You're so fucking funny bro").queue();
                return;
            }
            final Document[] servers = AllahFinderImpl.getInstance().getDatabase().getPlayerServers(searchQuery);

            if(searchQuery == null || servers == null || servers.length == 0) {
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
