package dev.isnow.allahfinder.checker.protocol.json.data;

import com.google.gson.annotations.SerializedName;
import dev.isnow.allahfinder.checker.protocol.json.rawData.ForgeModInfo;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Players;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Version;

public class ForgeResponseOld
{
    @SerializedName("description")
    private String description;
    @SerializedName("players")
    private Players players;
    @SerializedName("version")
    private Version version;
    @SerializedName("modinfo")
    private ForgeModInfo modinfo;
    
    public FinalResponse toFinalResponse() {
        this.version.setName(this.version.getName() + " FML with " + this.modinfo.getNMods() + " mods");
        return new FinalResponse(this.players, this.version, "", this.description);
    }
}
