# Mythos TCG Counter

A lightweight match counter fspecifically tailored for Mythos TCG, built natively for Android using **Jetpack Compose**

This app is designed for two players sharing a single device at a game table.

## Features

* **Dual Player Layout:** Two identical control sections, with Player 2's side rotated by 180 degrees for comfortable face-to-face tabletop gaming.
* **Chakra & Mission Points Trackers:** Intuitive plus/minus buttons to quickly update game resources. Includes a quick-reset shortcut for Chakra (defaults to 5).
* **The Edge:** A dedicated toggle to instantly pass and track who currently holds the game's Edge. The active player gets a bright yellow highlighted border.
* **Dynamic Match Timer:** A 30-minute countdown timer with smart visual cues
* **Wake Lock Integration:** The screen is automatically forced to stay turned on (`keepScreenOn`) while the match is active, so you don't have to worry about your phone locking mid-turn.
* **Immersive Mode:** System bars (status and navigation) are completely hidden during gameplay to prevent accidental misclicks.
