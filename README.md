# CW2025 Developing Maintainable Software – Tetris

This project involves maintaining and extending a re-implementation of the classic retro game Tetris, developed for CW2025 Software Maintenance for the DMS module.

🔗 GitHub Repository:  
https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi

-------------------------------------------------------------------------------------------------

### Prerequisites:
* Apache Maven 3.9.6+
* Java Oracle JDK 23+
* Javafx SDK

### Recommended IDEs:
* IntelliJ IDEA 2025.2+
* IntelliJ IDEA Community Edition 2025.2+
* Eclipse 2025.1+
* Vs Code with Java Extension Pack

-------------------------------------------------------------------------------------------------

## 1.0 Environment Setup

### 1.1 Install Java Development Kit (JDK)
* Download and Install JDK 23 or above from Oracle or use OpenJDK:
* https://www.oracle.com/asean/java/technologies/downloads
* Set JAVA_HOME environment variable or add to system PATH
* Verify java Installation in terminal: (should show Java 23 or higher)
```bash
  java --version
```

### 1.2 Install Apache Maven 3.9.6+
* Download and Install Apache Maven 3.9.6+
* https://maven.apache.org/download.cgi
* Go to Environment variable and set the path to bin file to PATH.
* Verify Maven installation in terminal: (Should show Apache Maven 3.9.6 or higher)
```bash
    mvn --version
```

### 1.3 Javafx SDK
*

-----------------------------------------------------------------------------------------------------

## 2.0 Open Project Folder
Open the Project in any IDE.
* Download and Extract `ThashaDewiSureshKumar_IntelliJ_25.zip`
Or Clone repository
```bash
    git clone https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi Tetris
```
* Open the folder in IDE, Import as Maven Project.

-------------------------------------------------------------------------------------------------

## 3.0 IDE SET UP With Tetris Game.

### 3.1 IntelliJ and IntelliJ Community Edition
* SetUp Project SDK
  * Go to: File → Project Structure → Project → SDK  
  * Add the Oracle JDK (version) → Apply → OK
* Download and Extract `ThashaDewiSureshKumar_IntelliJ_25.zip` Or Clone repository
```bash
    git clone https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi Tetris
```
* Continue to 3.1 to Compile and Run using MAVEN.

### 3.2 Eclipse
* SetUp javafx:run 
* Download and Extract `ThashaDewiSureshKumar_IntelliJ_25.zip` Or Clone repository
```bash
    git clone https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi Tetris
```
* Import the project into Eclipse as Maven
* Continue to 3.2 to Compile and Run using MAVEN.

### 3.3 VsCode
* Install Java Extension Pack
* Install JavaFX Extension
* Continue to Compile and Run using MAVEN section

--------------------------------------------------------------------------------------------------

## 4.0 Compile and Run using MAVEN
Run all until each show "BUILD SUCCESS" in terminal

#### 4.1 Clean the Project
``` bash 
    mvn clean
```

#### 4.2 Compile the Project
```bash
    mvn compile
```

#### 4.3 Run the Javafx to start the game
```bash
    mvn javafx:run
```

------------------------------------------------------------------------------------------------

## 5.0 Features: Implemented and Working Properly

### 5.1 Gameplay Mechanics
* Hold / Swap Brick Feature
  * Allows players to hold bricks for swapping it with current brick to use.
  * Implemented keybindings in GuiController, ControlsController and KeyBindings.
  * Implemented its function in SimpleBoard, InputEventListener, GameController and GuiController.
  * Updated FXML to display Hold/Swap controls in game UI as well as added Held Brick display on the left of gameboard.
  

* Ghost Block Preview
  * Displays a ghost brick where the falling brick would be placed.
  * Implemented in ViewData, SimpleBoard and GuiController.
  * Updated FXML to have Ghost Brick.
  

* Hard Drop Brick
  * Added an instant place feature that places the brick instantly, adds +3 score if done.
  * Implemented keybindings in GuiController, ControlsController and KeyBindings.
  * Implemented its function in SimpleBoard, InputEventListener, GameController and GuiController.

    
* Levels 
  * Player goes up a level when they gain 500 score, speed will increase 25ms each level.
  * start speed is 400ms at Level 1, caps at 100ms.
  * Implemented in GuiController
  * Updated FXML to display current level and speed of falling bricks on the right of game board.


* Added Ability to Pause the Game
  * Implemented keybindings in GuiController, ControlsController and KeyBindings.
  * Implemented its function in GuiController.
  * Updated FXML to display pause screen in game when paused.


* HighScore
  * Saves the top 10 scores for each game mode in a `.txt` file. Player name recorded (or marked as UNKNOWN if none provided).
  * Implemented in HighScoreManger and GuiController.
  * Updated FXML to display High score and player name of it in game over panel.


### 5.2 UI and Visuals
* Next Brick Preview
  * Displays the upcoming brick in the game.
  * Implemented using RandomBrickGenerator in SimpleBoard, GameController and GuiController.
  * Updated FXML to show Next Brick panel on the right of game board.


* Score Display
  * Live-updating score.
  * Updated FXML to display Score on the right of game panel.


* Centered Layout in Fullscreen window
  * Game elements remain centered regardless of window size.
  * Implemented in Main.


* Controls preview
  * Displays current controls next to game board on left.


* Timer
  * Implemented in GuiController
  * Updated FXML to display Timer next to game board on right.

    
### 5.3 Menus & Navigation FXML updates
* Main Menu Page
  * MenuController
    * Includes:
      * Game title
      * Player name input
      * Start Game
      * Controls
      * Leaderboard
      * Quit
      * Volume Slider
      * my name as credit
  
* Controls Page  
  * Allows players to fully rebind and customize keybindings.
  * Implemented in ControlsController, GuiController, ControlsController and KeyBindings.
  * Update FXML to reflect the control changes. 


* Leaderboard Page
  * Displays the top 10 scores for each game mode.
  * Implemented in LeaderBoardController using HighScoreManager.
  * Updates FXML to display high scores.


* Select Mode after clicking Start Game
  * Allows player to select a mode before starting the game.
  * Implemented in ModeSelectController using GameMode.

### 5.4 Game Modes
* Implemented in GameMode, GuiController, and GameController.

Players can choose between three modes:
* Classic Mode
  * Traditional endless gameplay.  
  * Timer counts up; speed & level increase with score.


* Time Limit Mode  
  * Classic game but with a 2-minute timer.
  * when player get 4 row clear +15s to timer


* All Same Block Mode
  * Classic game but all bricks are the same shape, based on the first generated brick.

### 5.5 Random Events (After 2 Minutes)
* Implemented in RandomEventManager, GuiController and GameController.

Every random event lasts 7 seconds with a min 10s in between events and max 30s:
* Blackout – Bottom of the screen is hidden behind a dark overlay.
* Temporary Speed Boost – Brick fall speed temporarily set to 90ms.
* Reverse Controls – All movement controls are inverted.

### 5.6 Audio and Sound effects
* Implemented in AudioManger, GameController, MenuController and Main.

The Game has background music and sound effects:
* place sound effect
* clear row sound effect
* game over Sound effect
* Bg Music (starts soft to avoid loud music blast when game first runs)

----------------------------------------------------------------------------------------------------

## 6.0 Features: Not Implemented
* Animations for better UI
* Multiplayer Mode
* Save Current Game when Quit, To allow players to continue where they left off.

Reasons: 
* Time constraints and complexity of implementation. 

-----------------------------------------------------------------------------------------------------

## 7.0 Refactoring Process

### 7.1 New Java Classes
#### 7.1.1 Controllers
| Class Name             | Description                                                                                                                                           |
|------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------|
| ControlsController     | Used to connect the controlsLayout.fxml, to handle displaying the menu showing the current controllers and allowing users to alter and save keybinds. |
| LeaderBoardController  | Used to connect the leaderBoard.fxml, to handle displaying the leaderboards of the top 10 highscores of all different game modes.                     |
| MenuController         | Used to connect the mainMenuLayout.fxml, to handle displaying the main menu.                                                                          |
| ModeSelectorController | Used to connect the selectModeLayout.fxml, to handle allowing users to pick a game mode to play in when users click start game in main menu.          |

#### 7.1.2 Event
| Class Name | Description                               |
|------------|-------------------------------------------|
| GameMode   | Has the different game modes in the game. |

#### 7.1.3 Helper
| Class Name  | Description                                                                                        |
|-------------|----------------------------------------------------------------------------------------------------|
| PlayerUtils | A helper that Validates and returns a player name, Returns "Unknown" if the name is null or empty. |

#### 7.1.4 Managers
| Class Name         | Description                                       |
|--------------------|---------------------------------------------------|
| AudioManager       | Manages the Audio for the whole game              |
| RandomEventManager | Manages random events for the game                |
| HighScoreManager   | Manages top 10 high scores for all the game modes |

-------------------------------------------------------------------------------------------------------------------------------------------------
# 7.2 Modified Java Classes
## 7.2.1 Controllers
### GameController
| Change                     | Detailed Description                                                                                                                                                                                                                                                                                         |
|----------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Added Game Mode Handling   | Introduced `private final GameMode gameMode;` to bind the selected mode (Classic, All Same Block, Time Limit) directly to controller logic. This ensures all gameplay flow (scoring, spawning, event processing) becomes mode-dependent and consistent across the application.                               |
| Time Limit Mode            | Added `TimeLimitDuration = 120` seconds and implemented an internal countdown timer. The timer updates using a JavaFX `Timeline`, synchronizing UI time display and ending the game when time reaches zero.                                                                                                  |
| Bonus Time System          | Implemented `checkAndApplyBonusTime()` to award +15 seconds when the player clears exactly four rows (Tetris). Integrates into the row-clearing pipeline so bonus time triggers only after brick locking.                                                                                                    |
| Mode-Specific Behavior     | Modified `GameController` now adapts scoring, spawning rules, and preview display depending on mode. - *All Same Block Mode* forces brick generation to always be identical. - *Time Limit Mode* modifies end-game conditions and introduces bonus time.                                                     |
| Score & Next Brick Binding | Modified `GameController` for Score, time, level, and next-brick preview are now bound to GUI elements through listener-based updates, ensuring instantaneous UI feedback after every board action.                                                                                                          |
| Player Name Support        | Modified `GameController` for Controller stores, retrieves, and forwards the player name to the GameOver panel and high-score system. Ensures persistent identity across sessions.                                                                                                                           |
| Hard Drop Event Handling   | Added `onHardDropEvent()` which: <br> 1) performs model-level `hardDrop()`, <br> 2) adds +3 points, <br> 3) processes row clearing and bonus time, <br> 4) updates GUI background + previews, <br> 5) checks for game-over. This strictly separates model drop logic from event-driven scoring + UI refresh. |
| Hold Event                 | Added `onHoldEvent()` which triggers model-level `holdBrick()` and updates the held-brick preview. Enforces the “one hold per turn” rule through model validation.                                                                                                                                           |
| Swap Event                 | Added `onSwapEvent()` which triggers `swapBrick()` and refreshes the held preview. Distinguishes explicitly between first-time hold and subsequent swaps.                                                                                                                                                    |


### GuiController – Major Changes
| Area                                | Detailed Description                                                                                                                                                                                                                                                                                                                                                               |
|-------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| FXML Bindings Expansion             | Added all required FXML links `pauseOverlay`, `nextBrickContainer`, `holdBrickContainer`, `scoreLabel`, `timerLabel`, `pauseKeybindLabel`, `restartLabel`, `rotateLabel`, `moveLeftLabel`, `moveRightLabel`, `moveDownLabel`, `hardDropLabel`, `mainMenuLabel`, `pauseLabel`, `holdLabel`, `swapLabel`, `speedLabel`                                                               |
| Constants & Configurable Parameters | Introduced unified constants for `currentSpeed`, `currentLevel`, `brickPanelYOffset`, ghost offsets, blackout opacity, event durations, speed increments, and animation timings. Removes magic numbers and centralizes gameplay tuning.                                                                                                                                            |
| Helper & Utility Enhancements       | Added helper methods such as `getGameController()`, layout calculators (`calculateBrickLayoutX/Y`), score pop-up generator `showScoreNotification()`, and key-formatting utilities (`formatKeyName`, `formatKeys`). These improve readability and modularity across input/UI logic.                                                                                                |
| Keyboard Input Pipeline             | Completely refactored key handling: <br>• integrated `handleKeyPress()` into the controller, <br>• moved listeners out of `initialize()`, <br>• added full support for reverse-controls events, <br>• added hold, swap, hard drop, soft drop, pause, restart, and menu transitions.                                                                                                |
| Dynamic Keybinding Display          | Implemented and Added `updateControlLabels()` to automatically reflect changes to user keybindings. Keys are formatted cleanly with `formatKeyName`. Ensures UI always matches active controls.                                                                                                                                                                                    |
| Brick Rendering Overhaul            | Rendering logic now includes: <br>• falling brick rectangles, <br>• ghost brick preview (auto-positioned), <br>• updated `refreshBrick()` to support ghost updates, `showNextBrick` and `showHeldBrick` displays the respective bricks in the panels, <br>• unified color lookup via the shared `Color[] COLORS`, <br>• smoother previews for next/hold with `showBrickPreview()`. |
| Set mode and player name to game    | Added `setPlayerName`, `setGameMode`, that sets the player name to the current game and game mode.                                                                                                                                                                                                                                                                                 |
| Score, Timer & Bonus Display        | Added real-time UI binding through `bindScore()`, updated timer system (`startTimer`, `startCountDownTimer`), and implemented `showBonusTimeLabel()` + score bonus popups. Timer now color-transitions when time is low.                                                                                                                                                           |
| Speed & Level Behavior              | Added logic to update and restore game speed: <br>• persistent level tracking, <br>• speed decay per level, <br>• temporary speed events (saved base speed), <br>• UI synchronization through `updateSpeedDisplay()`.                                                                                                                                                              |
| Random Event System Integration     | Connected GUI to `RandomEventManager`: <br>• blackout overlays (`activateBlackout`, `deactivateBlackout`), <br>• temporary speed boosts/debuffs (`setTemporarySpeed`, `restoreNormalSpeed`), <br>• reverse controls support, <br>• event notifications (`showEventNotification`, `showSimplePopup`).                                                                               |
| Game Flow & State Management        | Extended `newGame()`, `pauseGame()`, `stopTimeline()` and `gameOver()` to handle: <br>• event manager resets, <br>• visual cleanup for overlays/ghosts, <br>• timer resets, <br>• saving final scores and high score entries, <br>• lockouts for paused/over states.                                                                                                               |

 
## 7.2.2 Event
### EventType

New event types added to support advanced interaction:
* `HOLD` – Triggers brick holding and preview update
* `DROP` – Triggers hard drop & scoring pipeline
* `SWAP` – Performs held-brick swap

Allow the controller and input listeners to differentiate distinct user actions instead of overloading movement events.


### InputEventListener
Extended to detect and dispatch:
* Swap key → sends `SWAP` event
* Hold key → sends `HOLD` event
* Hard drop key → sends `DROP` event


Listener ensures input is ignored when paused or during events.


## 7.2.3 GameBoard
### Board
| Enhancement                | Detailed Explanation                                                                                                                                     |
|----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------|
| Next & Held Brick Support  | Added `getNextShapeInfo` and `getHeldBrickInfo` so the GUI can render upcoming and held bricks without modifying board state.                            |
| Gameplay Interactions      | Added model-level `hardDrop()`, `holdBrick()`, and `swapBrick()`. These manipulate brick position and state *only*—no scoring or GUI updates occur here. |
| Hold/Swap Validation       | Added `canHoldOrSwap()` to enforce “one hold/swap per falling cycle”. Prevents players from repeatedly swapping to abuse brick sequencing.               |


### MatrixOperations
* Added `SCORE_PER_LINE_BASE = 50`
  Used in the row-clearing algorithm to compute final score:
  `score = SCORE_PER_LINE_BASE * numberOfLinesCleared^2`


## SimpleBoard – Major Changes
| Feature                     | Detailed Technical Description                                                                                                                                                                                            |
|-----------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Next Brick Generation       | Added `generateNextBrick()` which always prepares one upcoming brick ahead of time, separating generation from immediate spawning. This allows consistent next-brick previews and eliminates the old generator function.  |
| Spawn Position Upgrade      | Introduced `SPAWN_POSITION` constant to elevate brick spawn height. This increases fairness and prevents early collisions when using taller brick shadows (ghosts).                                                       |
| Next Brick Info             | `getNextShapeInfo()` returns clone-safe metadata so the GUI can render the preview without altering game state.                                                                                                           |
| Hard Drop Implementation    | `hardDrop()` moves the active brick to the lowest unobstructed Y-position and locks it instantly. `getHardDropPosition()` calculates the ghost landing point without modifying any state, used exclusively for rendering. |
| Hold System                 | `holdBrick()` saves the current brick on first use, or swaps on later uses. Enforces `hasSwappedThisTurn` flag to prevent multiple swaps.                                                                                 |
| Swap System                 | `swapBrick()` explicitly swaps the held and active bricks. Behaves identically to hold on first use but semantically distinct for controller event handling.                                                              |
| Held Brick UI Support       | Added `getHeldBrickInfo()` to supply shape and rotation data for the GUI preview panel.                                                                                                                                   |


### ViewData
* Added `getGhostOffset()`
  Returns how far the brick can fall from its current position, used to render ghost bricks accurately.


## 7.2.4 Logic
### Brick Classes (I, J, L, O, S, T, Z)
Updates:
* Declared `public final class` → ensures immutability and safe reuse for previews.
* Prevents subclassing issues and unintended state sharing when the same brick reference is shown in both active play and preview panels.


### BrickGenerator & RandomBrickGenerator
* Removed `getNextBrick()`
* Brick sequencing moved fully into `SimpleBoard` so that:
    * Previews, spawning, swapping, and mode-specific rules all use one unified source of truth.
    * To prevents desynchronization between GUI preview and actual brick generation.


## 7.2.3 Panels
### GameOverPanel
| Change            | Detailed Description                                                                    |
|-------------------|-----------------------------------------------------------------------------------------|
| Score Display     | Shows final score, high score, and player name. Applies fade-in animation for emphasis. |
| Keybinding Text   | Added dynamic control display reflecting current key configuration.                     |
| Animation System  | Added `showGameOver()` animation for a smoother transition.                             |
| Reset Logic       | Added `reset()` to fully reset panel state, clear animations, and prepare for new game. |


### Main
| Change             | Detailed Description                                                                   |
|--------------------|----------------------------------------------------------------------------------------|
| Start Method       | Loads audio resources, initializes root scene, and opens main menu using `loadMenu()`. |
| Layout Centering   | Ensures the game window consistently centers itself across all screen resolutions.     |
| Menu Logic         | `loadMenu()` handles switching between the menu, game scene, and settings screens.     |

--------------------------------------------------------------------------------------------------------------------------------------

### 5.3 Unexpected Problems:
| Issue                               | Description                                                                                                                                  | Solution                                                                                                                             |
|-------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Ghost Brick Misalignment            | During early testing, the ghost brick sometimes appeared shifted some pixels below the correct landing position, especially after rotations. | Adjusted the Y-offset calculation and ensured the ghost position uses the same board collision logic as the falling brick.           |
| Reverse Controls Not Reverting      | During random events, reverse-control mode sometimes persisted after the timer ended, causing permanent inverted movement.                   | Ensured `restoreNormalSpeed()` and `eventManager.stopReverseControls()` are always called through a unified event-cleanup function.  |
| Brick Preview Not Centering         | On some screen resolutions, the Next/Hold preview bricks loaded slightly off-center.                                                         | Reworked preview placement to recalculate container dimensions using `Platform.runLater()` to ensure layout measurements were valid. |
| High Score Not Saving on Quick Exit | If the player exited immediately after game over, the high score sometimes failed to save.                                                   | Moved high-score saving into `gameOver()` before the UI transition starts, ensuring the write operation always completes.            |
| UI smooth animations not animating  | for all the buttons the smooth hover animations were not working                                                                             | ensured the style class is working `ensureStyleClass()`, then tweeked the animations                                                 |

-----------------------------------------------------------------------------------------------------

