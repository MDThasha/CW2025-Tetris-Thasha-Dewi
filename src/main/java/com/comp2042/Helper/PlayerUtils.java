package com.comp2042.Helper;

/** Helper for taking in player name*/
public class PlayerUtils {
    private PlayerUtils() { }

    /** Validates and returns a player name, Returns "Unknown" if the name is null or empty.
     * @param name
     * @return name if name is in field, unknown if its null */
    public static String validatePlayerName(String name) {
        return (name == null || name.trim().isEmpty()) ? "Unknown" : name.trim();
    }
}