package com.comp2042;

import java.io.*;
import java.util.*;

public class HighScoreManager {

    private static final String FILE_PATH = "highscores.txt";   // File to store persistent high scores
    private static final int MAX_ENTRIES = 10;                  // Maximum number of top scores to keep
    public record ScoreEntry(String name, int score) {}         // Record to store player's name and score as a single entry

    // Returns a list of top scores with names in descending order
    public static List<ScoreEntry> getTopScores() {
        List<ScoreEntry> scores = new ArrayList<>();                                     // List to hold scores read from file
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {    // Open file
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

    // Add a new score and save top 10
    public static void addScore(String name, int score) {
        List<ScoreEntry> scores = getTopScores();                                          // Get existing top scores
        scores.add(new ScoreEntry(name, score));                                           // Add the new score
        scores.sort((a, b) -> Integer.compare(b.score(), a.score())); // Sort descending
        if (scores.size() > MAX_ENTRIES) {                                                 // Keep only top 10
            scores = scores.subList(0, MAX_ENTRIES);
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_PATH))) {      // Write updated scores back to file
            for (ScoreEntry entry : scores) {
                writer.println(entry.name() + "," + entry.score());                  // Save each entry
            }
        } catch (IOException e) {
            e.printStackTrace();                                                     // Print error if unable to save
        }
    }

    // Return the highest score from the top scores, or 0 if none exist
    public static int getHighScore() {
        List<ScoreEntry> scores = getTopScores();
        return scores.isEmpty() ? 0 : scores.get(0).score();
    }
}
