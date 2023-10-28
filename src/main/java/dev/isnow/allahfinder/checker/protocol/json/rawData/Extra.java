package dev.isnow.allahfinder.checker.protocol.json.rawData;

import com.google.gson.annotations.SerializedName;

class Extra
{
    @SerializedName("color")
    private String color;
    @SerializedName("bold")
    private boolean bold;
    @SerializedName("text")
    private String text;
    
    public String getText() {
        return this.text;
    }
}
