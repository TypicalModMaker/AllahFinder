package dev.isnow.allahfinder.checker.protocol;

import com.google.gson.Gson;
import dev.isnow.allahfinder.checker.protocol.json.data.*;
import java.io.IOException;
import java.net.InetSocketAddress;

public class MCPing
{
    public FinalResponse getPing(final PingOptions options) throws IOException {
        final Gson gson = new Gson();
        final Pinger a = new Pinger();
        a.setAddress(new InetSocketAddress(options.getHostname(), options.getPort()));
        a.setTimeout(options.getTimeout());
        final String json = a.fetchData();
        try {
            if (json == null || !json.contains("{")) {
                return null;
            }
            if (json.contains("\"modid\"") && json.contains("\"translate\"")) {
                return gson.fromJson(json, ForgeResponseTranslate.class).toFinalResponse();
            }
            if (json.contains("\"modid\"") && json.contains("\"text\"")) {
                return gson.fromJson(json, ForgeResponse.class).toFinalResponse();
            }
            if (json.contains("\"modid\"")) {
                return gson.fromJson(json, ForgeResponseOld.class).toFinalResponse();
            }
            if (json.contains("\"extra\"")) {
                return gson.fromJson(json, ExtraResponse.class).toFinalResponse();
            }
            if (json.contains("\"text\"")) {
                return gson.fromJson(json, NewResponse.class).toFinalResponse();
            }
            if(json.contains("\"description\"")) {
                return gson.fromJson(json, OldResponse.class).toFinalResponse();
            }
        } catch (Exception ignored) {}
        return null;
    }
}
