package com.comp2042.Helper;

public class PlayerUtils {

    private PlayerUtils() { }

    /** Validates and returns a player name, Returns "Unknown" if the name is null or empty. */
    public static String validatePlayerName(String name) {
        return (name == null || name.isEmpty()) ? "Unknown" : name;
    }
}