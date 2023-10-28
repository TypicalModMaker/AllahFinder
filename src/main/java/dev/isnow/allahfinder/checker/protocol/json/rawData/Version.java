package dev.isnow.allahfinder.checker.protocol.json.rawData;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Version
{
    @SerializedName("name")
    private String name;
    @SerializedName("protocol")
    private int protocol;
    
    public void setName(final String a) {
        this.name = a;
    }

    public void setProtocol(final int a) {
        this.protocol = a;
    }

    public String getName() {
        return this.name;
    }
}
