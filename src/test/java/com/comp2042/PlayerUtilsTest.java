package com.comp2042;

import com.comp2042.Helper.PlayerUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerUtilsTest {
    @Test
    void testValidPlayerName() {
        String name = "Player1";
        assertEquals("Player1", PlayerUtils.validatePlayerName(name),
                "Valid player name should be returned unchanged");
    }

    @Test
    void testNullPlayerName() {
        assertEquals("Unknown", PlayerUtils.validatePlayerName(null),
                "Null player name should return 'Unknown'");
    }

    @Test
    void testEmptyPlayerName() {
        assertEquals("Unknown", PlayerUtils.validatePlayerName(""),
                "Empty player name should return 'Unknown'");
    }

    @Test
    void testWhitespacePlayerName() {
        assertEquals("Unknown", PlayerUtils.validatePlayerName("   "),
                "Whitespace-only player name should return 'Unknown'");
    }

    @Test
    void testPlayerNameWithSpaces() {
        String name = "XX XX";
        assertEquals("XX XX", PlayerUtils.validatePlayerName(name),
                "Player name with spaces should be valid");
    }

    @Test
    void testPlayerNameWithSpecialCharacters() {
        String name = "Player_123!";
        assertEquals("Player_123!", PlayerUtils.validatePlayerName(name),
                "Player name with special characters should be valid");
    }

    @Test
    void testSingleCharacterPlayerName() {
        assertEquals("A", PlayerUtils.validatePlayerName("A"),
                "Single character player name should be valid");
    }

    @Test
    void testVeryLongPlayerName() {
        String longName = "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX";
        assertEquals(longName, PlayerUtils.validatePlayerName(longName),
                "Very long player name should be valid");
    }
}