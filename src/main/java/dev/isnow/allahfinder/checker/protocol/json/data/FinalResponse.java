package dev.isnow.allahfinder.checker.protocol.json.data;

import dev.isnow.allahfinder.checker.protocol.json.rawData.Players;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Version;

public class FinalResponse extends MCResponse
{
    private final String description;
    
    public FinalResponse(final Players players, Version version, final String favicon, final String description) {
        this.description = description;
        this.favicon = favicon;
        this.players = players;
        this.version = version;

    }
    
    public Players getPlayers() {
        return this.players;
    }
    
    public Version getVersion() {
        return this.version;
    }
    
    public String getDescription() {
        return this.description;
    }
}
