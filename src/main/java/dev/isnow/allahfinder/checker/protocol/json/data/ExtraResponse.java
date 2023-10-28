package dev.isnow.allahfinder.checker.protocol.json.data;

import com.google.gson.annotations.SerializedName;
import dev.isnow.allahfinder.checker.protocol.json.rawData.ExtraDescription;

public class ExtraResponse extends MCResponse
{
    @SerializedName("description")
    private ExtraDescription description;
    
    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description.getText());
    }
}
