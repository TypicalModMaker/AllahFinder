package dev.isnow.allahfinder.util;

import dev.isnow.allahfinder.AllahFinderImpl;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtil {

    public void info(Object... message) {
        System.out.println("[INFO] " + convert(message));
    }

    public void debug(Object... message) {
        if(AllahFinderImpl.DEBUG) {
            System.out.println("[DEBUG] " + convert(message));
        }
    }

    public void error(Object... message) {
        System.out.println("[ERROR] " + convert(message));
    }

    private String convert(Object... array) {
        StringBuilder builder = new StringBuilder();
        for (Object value : array) {
            builder.append(value).append(" ");
        }
        return builder.toString();
    }
}
