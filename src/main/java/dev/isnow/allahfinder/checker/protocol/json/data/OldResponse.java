package dev.isnow.allahfinder.checker.protocol.json.data;

import com.google.gson.annotations.SerializedName;

public class OldResponse extends MCResponse
{
    @SerializedName("description")
    private String description;
    
    public FinalResponse toFinalResponse() {
        return new FinalResponse(this.players, this.version, this.favicon, this.description);
    }
}
