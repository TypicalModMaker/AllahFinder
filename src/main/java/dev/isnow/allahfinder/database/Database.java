package dev.isnow.allahfinder.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import dev.isnow.allahfinder.checker.protocol.json.data.FinalResponse;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Player;
import dev.isnow.allahfinder.util.MessageUtil;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

public class Database {

    private final MongoCollection<Document> collection;

    public Database(MongoCredential credentials, String ip, String database) {
        MessageUtil.debug("Connecting to mongodb...");
        MongoClient server;
        if(credentials != null) {
            server = new MongoClient(new ServerAddress(ip), credentials, MongoClientOptions.builder().build());
        } else {
            server = new MongoClient(new ServerAddress(ip), MongoClientOptions.builder().build());
        }
        MessageUtil.debug("Getting database..");
        MongoDatabase db = server.getDatabase(database);

        MessageUtil.debug("Getting collection..");

        collection = db.getCollection("servers");
        MessageUtil.debug("Connected to mongodb!");
    }

    public void addHit(String ip, short port, FinalResponse response, String result) {
        final List<Player> players = response.getPlayers().getSample();
        final List<String> playerStringList = new ArrayList<>();

        if(players != null) {
            for(Player p : players) {
                playerStringList.add(p.getName() + ":UUID:" + p.getId());
            }
        }

        // Update player list
        final Document oldServer = getServer(ip, port);
        if(oldServer != null) {
            for(Object playerObj : oldServer.get("players", ArrayList.class)) {
                final String playerString = (String) playerObj;

                final String[] player = playerString.split(":UUID:");
                playerStringList.removeIf(s -> s.contains(player[0]));

                playerStringList.add(playerString);
            }
        }

        final Document serverInfo = new Document("$set", new Document()
                .append("ip", ip)
                .append("port", port)
                .append("playerCount", response.getPlayers().getOnline())
                .append("maxPlayerCount", response.getPlayers().getMax())
                .append("players", playerStringList)
                .append("versionName", response.getVersion().getName())
                .append("versionProtocol", response.getVersion().getProtocol())
                .append("motd", response.getDescription())
                .append("lastUpdateDate", new Date())
                .append("joinResult", result)
        );

        final UpdateOptions updateOptions = new UpdateOptions().upsert(true);
        final Bson filter = Filters.and(
                Filters.eq("ip", ip),
                Filters.eq("port", port)
        );

        collection.updateOne(filter, serverInfo, updateOptions);
    }

    public long getServerCount() {
        return collection.countDocuments();
    }

    public Document[] getPlayerServers(OptionMapping playerQuery) {
        if(playerQuery == null) {
            return null;
        }

        final Document query = new Document("players", new Document("$elemMatch", new Document("$regex", playerQuery.getAsString()).append("$options", "i")));
        final Document sort = new Document("lastUpdateDate", -1);

        final List<Document> documents = new ArrayList<>();

        for (Document document : collection.find(query).sort(sort).limit(30)) {
            MessageUtil.debug(document.values());
            documents.add(document);
        }

        if(documents.isEmpty()) {
            return null;
        }
        return documents.toArray(new Document[0]);
    }

    public Document getServer(String ip, short port) {
        final Document query = new Document("port", port).append("ip", ip);
        return collection.find(query).limit(1).first();
    }

    public Document getServer(String ip, int port) {
        return getServer(ip, (short) port);
    }

    public Document getServer(String ip, String port) {
        return getServer(ip, Short.parseShort(port));
    }

    public Document[] getServers(String ip) {
        try {
            final Document query = new Document("ip", Pattern.compile(Pattern.quote(ip)));
            final Document sortDoc = new Document("playerCount", -1);

            final List<Document> documents = new ArrayList<>();
            for (Document document : collection.find(query).sort(sortDoc).limit(30)) {
                documents.add(document);
            }

            if(documents.isEmpty()) {
                return null;
            }
            return documents.toArray(new Document[30]);
        } catch (Exception e) {
            MessageUtil.error("FAILED TO FETCH SERVERS!", e.getMessage());
        }
        return null;
    }

    public Document[] getServers(OptionMapping minPlayers, OptionMapping version, OptionMapping sort, OptionMapping motd) {
        try {
            final Document query = new Document();
            if(minPlayers != null) {
                query.append("playerCount", new Document("$gt", minPlayers.getAsInt()));
            }
            if(version != null) {
                query.append("versionName", Pattern.compile(Pattern.quote(version.getAsString())));
            }
            if(motd != null) {
                query.append("motd", Pattern.compile(Pattern.quote(motd.getAsString())));
            }

            Document sortDoc = new Document();
            if(sort != null && sort.getAsBoolean()) {
                sortDoc = new Document("playerCount", -1);
            }

            final List<Document> documents = new ArrayList<>();
            for (Document document : collection.find(query).sort(sortDoc).limit(30)) {
                documents.add(document);
            }

            if(documents.isEmpty()) {
                return null;
            }
            return documents.toArray(new Document[30]);
        } catch (Exception e) {
            MessageUtil.error("FAILED TO FETCH SERVERS!", e.getMessage());
        }
        return null;
    }
}
