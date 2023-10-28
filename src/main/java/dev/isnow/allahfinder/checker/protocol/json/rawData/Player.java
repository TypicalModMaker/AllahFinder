package dev.isnow.allahfinder.checker.protocol.json.rawData;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Player
{
    @SerializedName("name")
    private final String name;
    @SerializedName("id")
    private final String id;
    
    public String getName() {
        return this.name;
    }
    
    public String getId() {
        return this.id;
    }
}
