package dev.isnow.allahfinder.checker.protocol.json.data;

import com.google.gson.annotations.SerializedName;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Description;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Players;
import dev.isnow.allahfinder.checker.protocol.json.rawData.Version;

public class NewResponse extends MCResponse
{
    @SerializedName("description")
    private final Description description;
    
    public void setVersion(final String a) {
        this.version.setName(a);
    }
    
    public NewResponse() {
        this.description = new Description();
        this.players = new Players();
        this.version = new Version();
    }
    
    public Description getDescription() {
        return this.description;
    }
    
    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description.getText());
    }
}
