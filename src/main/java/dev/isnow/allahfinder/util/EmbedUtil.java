package dev.isnow.allahfinder.util;

import dev.isnow.allahfinder.checker.protocol.json.data.FinalResponse;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Player;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.utils.TimeFormat;
import org.bson.Document;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@UtilityClass
public class EmbedUtil {

    public EmbedBuilder getEmbed(Document server) {
        final String serverVersion = server.getString("versionName");
        final int serverProtocol = server.getInteger("versionProtocol");
        final Date lastUpdate = server.getDate("lastUpdateDate");

        final int maxPlayerCount = server.getInteger("maxPlayerCount");
        final int playerCount = server.getInteger("playerCount");

        final String ip = server.getString("ip");
        final int port = server.getInteger("port");

        String description = server.getString("motd");
        if(description == null) {
            description = "DIDN'T QUERY - OLD PING";
        }

        final ArrayList<String> playerStrings = server.get("players", ArrayList.class);
        final StringBuilder players = new StringBuilder();
        for(final String player : playerStrings) {
            players.append(player).append("\n");
        }

        return build(ip, port, serverVersion, serverProtocol, maxPlayerCount, playerCount, description, players.toString(), lastUpdate);
    }

    public EmbedBuilder getEmbed(FinalResponse server, String ip, int port) {
        final String serverVersion = server.getVersion().getName();
        final int serverProtocol = server.getVersion().getProtocol();

        final int maxPlayerCount = server.getPlayers().getMax();
        final int playerCount = server.getPlayers().getOnline();

        String description = server.getDescription();

        final List<Player> playerStrings = server.getPlayers().getSample();
        final StringBuilder players = new StringBuilder();
        if(playerStrings != null) {
            for(final Player player : playerStrings) {
                players.append(player.getName()).append(":UUID:").append(player.getId()).append("\n");
            }
        }

        return build(ip, port, serverVersion, serverProtocol, maxPlayerCount, playerCount, description, players.toString(), new Date());
    }

    private EmbedBuilder build(String ip, int port, String serverVersion, int serverProtocol, int maxPlayerCount, int playerCount, String description, String players, Date lastUpdate) {
        final EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("SERVER:", null);
        eb.setColor(Color.red);
        eb.setDescription("Cum");
        eb.addField("IP: ", ip + ":" + port, false);
        eb.addField("Version: ", serverVersion, false);
        eb.addField("Protocol: ", String.valueOf(serverProtocol), false);
        eb.addField("MOTD: ", description, false);
        eb.addField("LastUpdate: ", TimeFormat.RELATIVE.format(lastUpdate.getTime()), false);
        eb.addField("PlayerCount: ", playerCount + "/" + maxPlayerCount , false);
        boolean firstField = true;
        if(players.length() > 1024) {
            for(final String playerSplit : StringUtil.split(players, 1000)) {
                eb.addField(firstField ? "Players" : " ", "\n" + playerSplit , !firstField);
                firstField = false;
            }
        } else {
            eb.addField("Players: ", "\n" + players , false);
        }

        return eb;
    }
}
