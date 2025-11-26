package com.comp2042.Managers;

import com.comp2042.Event.GameMode;
import com.comp2042.Helper.PlayerUtils;

import java.io.*;
import java.util.*;

/** Utility for reading, writing and managing persistent high score lists per game mode. */
public class HighScoreManager {

    /** Maximum number of high score entries to retain. */
    private static final int MAX_ENTRIES = 10;

    /** Simple record storing a player name and score as a single entry. */
    public record ScoreEntry(String name, int score) {}

    /** Return the filename used to store high scores for the given game mode.
     * @param mode the GameMode whose file path is requested
     * @return the filename for the mode's high score storage */
    private static String getFilePath(GameMode mode) {
        return switch (mode) {
            case CLASSIC -> "highscores_classic.txt";
            case TIME_LIMIT -> "highscores_timelimit.txt";
            case ALL_SAME_BLOCK -> "highscores_sameshape.txt";
        };
    }

    /** Add a score for a player to the high score list for the given game mode and persist the result. only top 10
     * @param name player name
     * @param score the player's score
     * @param mode the game mode to which the score belongs*/
    public static void addScore(String name, int score, GameMode mode) {
        name = PlayerUtils.validatePlayerName(name);

        List<ScoreEntry> scores = getTopScores(mode);                                      // Get existing top scores
        scores.add(new ScoreEntry(name, score));                                           // Add the new score
        scores.sort((a, b) -> Integer.compare(b.score(), a.score())); // Sort descending
        if (scores.size() > MAX_ENTRIES) {                                                 // Keep only top 10
            scores = scores.subList(0, MAX_ENTRIES);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(getFilePath(mode))))  {      // Write updated scores back to file
            for (ScoreEntry entry : scores) {
                writer.println(entry.name() + "," + entry.score());                  // Save each entry
            }
        } catch (IOException e) {
            e.printStackTrace();                                                     // Print error if unable to save
        }
    }

    /** Read and return the top scores for a game mode from persistent storage.
     * @param mode the GameMode to read scores for
     * @return a List of ScoreEntry in descending order (highest first)*/
    public static List<ScoreEntry> getTopScores(GameMode mode) {
        String filePath = getFilePath(mode);
        List<ScoreEntry> scores = new ArrayList<>();                                     // List to hold scores read from file
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {     // Open file
            String line;
            while ((line = reader.readLine()) != null) {                                 // Read line by line
                String[] parts = line.split(",");                                  // Split line into name and score
                if (parts.length == 2) {
                    try {
                        String name = parts[0].trim();
                        int score = Integer.parseInt(parts[1].trim());
                        scores.add(new ScoreEntry(name, score));                          // Add new ScoreEntry to list
                    } catch (NumberFormatException ignored) { }
                }
            }
        }
        catch (IOException ignored) { } // Ignore if file not found or cannot be read

        // Sort descending
        scores.sort((a, b) -> Integer.compare(b.score(), a.score()));

        // Limit to top 10
        if (scores.size() > MAX_ENTRIES) { return scores.subList(0, MAX_ENTRIES);}
        return scores;
    }

    /** Return the highest recorded score for the specified game mode, or 0 if none exist.
     * @param mode the GameMode to query
     * @return the top score or 0 */
    public static int getHighScore(GameMode mode) {
        List<ScoreEntry> scores = getTopScores(mode);
        return scores.isEmpty() ? 0 : scores.get(0).score();
    }

    /** Return the player name associated with the highest recorded score for the specified mode, or "Unknown" if no scores are recorded.
     * @param mode the GameMode to query
     * @return player name of the top scorer or "Unknown" */
    public static String getHighScorePlayerName(GameMode mode) {
        List<ScoreEntry> scores = getTopScores(mode);
        return scores.isEmpty() ? "Unknown" : scores.get(0).name();
    }
}
