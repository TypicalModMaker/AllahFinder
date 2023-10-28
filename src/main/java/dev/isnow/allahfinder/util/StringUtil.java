package dev.isnow.allahfinder.util;

import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class StringUtil {
    public boolean containsIgnoreCase(String str, String searchStr) {
        final int length = searchStr.length();
        final char firstCharUpper = Character.toUpperCase(searchStr.charAt(0));
        final char firstCharLower = Character.toLowerCase(searchStr.charAt(0));
        for (int i = str.length() - length; i >= 0; i--) {
            final char charAt = str.charAt(i);
            if (charAt == firstCharUpper || charAt == firstCharLower) {
                if (str.regionMatches(true, i, searchStr, 0, length)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<String> split(String input, int characterCount) {
        final List<String> substrings = new ArrayList<>();

        if (input == null || input.isEmpty() || characterCount <= 0) {
            return substrings;
        }

        int length = input.length();
        int startIndex = 0;

        while (startIndex < length) {
            int endIndex = Math.min(startIndex + characterCount, length);
            String substring = input.substring(startIndex, endIndex);
            substrings.add(substring);
            startIndex = endIndex;
        }

        return substrings;
    }

    public String clean(String line) {
        if (line == null) {
            return "";
        }
        line = line.replace("§0", "");
        line = line.replace("§1", "");
        line = line.replace("§2", "");
        line = line.replace("§3", "");
        line = line.replace("§4", "");
        line = line.replace("§5", "");
        line = line.replace("§6", "");
        line = line.replace("§7", "");
        line = line.replace("§8", "");
        line = line.replace("§9", "");
        line = line.replace("§a", "");
        line = line.replace("§b", "");
        line = line.replace("§c", "");
        line = line.replace("§d", "");
        line = line.replace("§e", "");
        line = line.replace("§f", "");
        line = line.replace("§k", "");
        line = line.replace("§l", "");
        line = line.replace("§m", "");
        line = line.replace("§n", "");
        line = line.replace("§r", "");
        line = line.replace("§o", "");
        line = line.replace("§A", "");
        line = line.replace("§B", "");
        line = line.replace("§C", "");
        line = line.replace("§D", "");
        line = line.replace("§E", "");
        line = line.replace("§F", "");
        line = line.replace("§K", "");
        line = line.replace("§L", "");
        line = line.replace("§M", "");
        line = line.replace("§N", "");
        line = line.replace("§R", "");
        line = line.replace("§O", "");
        line = line.replace("\n", "");
        line = line.trim().replaceAll(" +", " ");
        return line;
    }
}
