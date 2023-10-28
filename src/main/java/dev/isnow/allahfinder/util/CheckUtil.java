package dev.isnow.allahfinder.util;

import dev.isnow.allahfinder.checker.protocol.MCPing;
import dev.isnow.allahfinder.checker.protocol.PingOptions;
import dev.isnow.allahfinder.checker.protocol.json.data.FinalResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
public class CheckUtil {
    public FinalResponse check(String ip, int port) {
        final MCPing mcPing = new MCPing();
        final PingOptions pingOptions = new PingOptions();
        pingOptions.setHostname(ip);
        pingOptions.setPort(port);
        pingOptions.setTimeout(5000);
        try {
            return mcPing.getPing(pingOptions);
        } catch (IOException ignored) {
            return null;
        }
    }


    public boolean valid(String joinResponse, FinalResponse response, int port) {
        final boolean validPort = !(port == 25565);

        final String version = response.getVersion().getName();
        final boolean validVersion = !version.contains("Elytrium") && !version.contains("Cord") && !version.contains("§") && !version.contains("&") && !version.contains("Waterfall") && !version.contains("Velocity") && !version.contains("BotFilter");

        final boolean validResponse = joinResponse.contains("SUCCESS") && !joinResponse.contains("1.19");

        return validPort && validVersion && validResponse;
    }
}
