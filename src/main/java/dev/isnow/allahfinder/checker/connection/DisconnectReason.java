package dev.isnow.allahfinder.checker.connection;

import dev.isnow.allahfinder.util.StringUtil;
import java.util.Arrays;
import lombok.Getter;

public enum DisconnectReason {
    IPWL("You have to join through the proxy", "You must join thru"),
    BungeeGuard("Unable to authenticate", "BungeeGuard", "https://www.nickuc.com/docs/bungeeguard"),
    Protected("Please join to the server via"),
    VANILLA("multiplayer.disconnect.slow_login"),
    VELOCITY("This server is only compatible with Minecraft 1.13 and above"),
    DATA_HOSTNAME("Unknown data in login hostname"),
    WHITELIST("You are not white-listed on this server!", "whitelist"),
    MODDED("FML/Forge", "This server has mods that require Forge"),
    VERSION("Incompatible client", "Outdated client!"),
    IP_FORWARDING("IP forwarding"),
    THROTTLE("Connection throttled!"),
    BYTE_BUF("PooledUnsafeDirectByteBuf"),
    KEY_PACKET("Unexpected key packet"),
    VPN("VPN"),
    UNKNOWN("");

    @Getter
    private final String[] reasons;

    DisconnectReason(String... reason) {
        reasons = reason;
    }

    public static DisconnectReason getReason(String input) {
        if(StringUtil.containsIgnoreCase(input, "VPN")) {
            return VPN;
        }
        if(StringUtil.containsIgnoreCase(input, "whitelist")) {
            return WHITELIST;
        }
        return Arrays.stream(values()).filter(disconnectReason -> Arrays.stream(disconnectReason.getReasons()).anyMatch(reason -> !reason.equals("") && input.contains(reason))).findFirst().orElse(DisconnectReason.UNKNOWN);
    }
}
