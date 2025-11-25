## 🎮 CW2025 Software Maintenance – Tetris (Thasha Dewi)

This project involves **maintaining and extending a re-implementation of the classic retro game Tetris**, developed for the **CW2025 Software Maintenance** module.

🔗 **GitHub Repository:**  
https://github.com/MDThasha/CW2025-Tetris-Thasha-Dewi

---

## How to Compile & Run the Project

### 1. Install Java JDK 25
Download and install Oracle JDK 25:  
https://www.oracle.com/asean/java/technologies/downloads

### 2. Open the Project in IntelliJ IDE
1. Extract **`ThashaDewiSureshKumar_IntelliJ_25.zip`**  
2. Open the folder in **IntelliJ IDE**

### 3. Set the Project SDK
Go to: **File → Project Structure → Project → SDK**  
Add the Oracle JDK → **Apply → OK**

<img width="347" height="250" alt="image" src="https://github.com/user-attachments/assets/0195d3f5-38d2-4d05-9501-ea38991d8d6c" />
<img width="300" height="300" alt="image" src="https://github.com/user-attachments/assets/0f539422-9ccc-40c1-a51d-f77acfa0d160" />

### 4. Clean the Project
Open the Maven panel (right side of IntelliJ):  
**Plugins → clean → clean:clean** (double-click)

<img width="257" height="191" alt="image" src="https://github.com/user-attachments/assets/2ea0eb8a-799f-4283-a739-be96450c4c29" />

### 5. Compile the Project
After the clean finishes:  
**Plugins → compiler → compiler:compile** (double-click)

### 6. Run the Application
Then run:  
**Plugins → javafx → javafx:run** (double-click)

The game will launch shortly.  
To run again, just double-click **javafx:run**.

<img width="218" height="418" alt="image" src="https://github.com/user-attachments/assets/9c12a2bf-3768-459d-ac02-3219cb2508a3" />

---
## 🧩 Implemented Features That are Working

### 🎯 Gameplay Mechanics
- **Dynamic Brick Speed Increase**  
  - Brick fall speed increases by **25ms** every **500 points**.  
  - Starts at **400ms** and caps at **100ms**.
- **Higher Brick Spawn Point**  
  - Bricks now spawn at the **top of the board**, giving a more authentic Tetris feel.
- **Hold / Swap Feature**  
  - Allows players to hold a brick for later use.  
  - Preview shown on the left panel.
- **Ghost Block Preview**
  - Displays a ghost brick where the falling brick would be placed
- **Hard Drop**
  - Added an instant place feature that placed the brick even if brick hasnt touched the bottom.
  - adds score if done, similar to when player manually moves brick down. 
- **Level System**  
  - Levels increase automatically as speed and score increase.  
  - Level UI displayed under the timer.
- **Added Ability to Pause the Game**

---
<img alt="image" src="https://github.com/user-attachments/assets/aaa68cf2-54af-4d8a-9a64-0801654d4002" align="right" width="150"/>

### 🪟 UI & Visual Improvements
- **Next Brick Preview Panel**  
  - Displays the upcoming brick on the top-right of the game board.
- **Score Display Panel**  
  - Live-updating score below the Next Brick Preview.
- **Black Background Theme**
- **Centered Layout (Even in Fullscreen)**  
  - Game elements remain centered regardless of window size.
- **On-Screen Controls Display**  
  - Controls shown on the left side of the gameplay screen.
- **On-Screen Next Brick Preview**
- **On-Screen Held Brick Panel**
- **On-Screen Timer**
- **On-Screen Level and game speed display**
- **On-Screen Score display**

---

### 🏆 High Scores & Player Data
- **Highscore System**  
  - Saves the top 10 scores for each game mode in a `.txt` file.
  - Player name recorded (or marked as **UNKNOWN** if none provided).
- **Game Over Stats Display**  
  - Shows current score + highscore + player name at the end of the game.

---
<img alt="image" src="https://github.com/user-attachments/assets/08d580d4-2096-4109-aeae-9eec9af013a6" align="right" width="150"/>
<img alt="image" src="https://github.com/user-attachments/assets/691bd423-4d9c-446a-9bbb-f66c792e1ea9" align="right" width="150"/>
<img alt="image" src="https://github.com/user-attachments/assets/4ecde055-22c6-4688-a061-3440a4e944f0" align="right" width="150"/>
<img alt="image" src="https://github.com/user-attachments/assets/c6a739a8-12fb-4e1e-aa72-93ae888603b8" align="right" width="150"/>

### 🧭 Menus & Navigation
- **Main Menu Page**  
  Includes:
  - Game title  
  - Player name  
  - Start Game  
  - Controls  
  - Leaderboard
  - Quit  
  - Volume Slider
  - my name as credit
  
  <img alt="image" src="https://github.com/user-attachments/assets/f39e94ad-e0c5-4865-b86e-c6afb869db98" align="right" width="150"/>
  <img alt="image" src="https://github.com/user-attachments/assets/23a621c4-b249-4b92-b0d6-56948ff552d6" align="right" width="150"/>
  <img alt="image" src="https://github.com/user-attachments/assets/f23c5b40-718e-4387-8ce2-b0d7ba08207c" align="right" width="150"/>
- **Controls Page**  
  - Allows players to fully rebind and customize keybindings. Keybinging changes will reflect all UI.
- **Leaderboard Page**  
  - Displays the **top 10 scores** for each game mode.
- **Select Mode after clicking Start Game**
  - allows player to select a mode
- **Pause Menu**  
  - Pauses gameplay and allows resuming, quitting, etc.

---

### 🎮 Game Modes
Players can choose between three modes:
- **Classic Mode**  
  - Traditional endless gameplay.  
  - Timer counts up; speed & level increase with score.
- **Time Limit Mode**  
  - Classic gameplay but with a **2-minute timer**.
  - when player get 4 row clear +15s to timer
- **All Same Block Mode**  
  - All bricks are the **same shape**, based on the first generated brick.

---
<img alt="image" src="https://github.com/user-attachments/assets/8c466afa-a672-4bd0-9b91-cc739e8843f4" align="right" width="150"/>
<img alt="image" src="https://github.com/user-attachments/assets/13ba329f-7272-4d35-9df1-8602a324da25" align="right" width="150"/>
<img alt="image" src="https://github.com/user-attachments/assets/7a623823-c5f7-45bc-b31f-71679b8b8a5e" align="right" width="150"/>

### ⚡ Random Events (After 2 Minutes)
Every random event lasts **7 seconds**:
- **Blackout** – Bottom of the screen is hidden behind a dark overlay.
- **Temporary Speed Boost** – Brick fall speed temporarily set to **90ms**.
- **Reverse Controls** – All movement controls are inverted.



---

### 🔊 Audio and Sound effects
The Game has background music and sound effects:
- **place sound effect**
- **clear row sound effect**
- **game over Sount effect**

---

## 🧩 Implemented Features but Not Working
### NONE

---

## 🧩 Features Not Implemented
### NONE

---
New Java Classes:

Modified Java Classes:

Unexpected Problems:

