# Text Adventure Game

## About

This is a text-based adventure game written in Java. The player explores rooms, examines objects, and interacts with the world and other charecters by entering text commands. It's designed to be simple and easy to run on any machine with a Java SDK.

## What the game is

- Genre: Interactive fiction / text adventure
- Language: Java
- Files: source files are the `.java` files in the project root (for example `Game.java`, `Main.java`, `Room.java`, `Parser.java`, etc.).

The game world consists of rooms (Library, Kitchen, Bedroom, Foyer, Balcony, Outside, etc.). Each room may contain items and exits to other rooms. The `Game` class contains the main game loop and logic.

## How it works (brief)

- `Main.java` starts the game by instantiating `Game` and calling `play()`.
- `Game.java` manages the game loop: it prints the current room description, reads input from the player, parses it (via `Parser`/`Command`), and executes game actions.
- `Room` and related classes represent locations and their contents.

## How to run (player instructions)

Prerequisites:
- Java Development Kit (JDK) 8 or newer installed and `javac`/`java` available on your PATH.

To compile and run:

```bash
# compile all Java source files
javac *.java

# run the game
java Main
```

## Basic gameplay commands (not all included below)

- `look` — see the current room items
- `go <direction>` — move to a room in the given direction (e.g., `go north`)
- `back` - move to previous room
- `pickup <item>` — pick up an item (gain its weight)
- `drop <item>` - drop an item (lose its weight)
- `inventory` — show carried items
- `map` - shows the map image
- `quit` — end the game
- `help` - shows all commands including special ones (cut, rip, etc)


---

Enjoy playing and exploring the code!
