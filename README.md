# Gopher Hunting - Multi-threading using Handlers in Android 

The project aims at demonstarting the usage of the concepts of Handlers to perform multithreading in Android. Gopher Hunting is a two player game in which players makes guesses to find the location of a gopher placed in a grid.

## About the game
The layout of the game comprises of a 10x10 grid. The computer places a gopher in a random cell within this grid. Two players compete against each other to determine the location of the gopher within the cell. The first player to successfully find the location of the gopher wins the game.
The game can be played in two modes:
+ Continuous Mode
+ Guess by Guess Mode

In the continuous mode, both the players make a move continuously to find the location of the gopher, whereas, in the guess by guess mode one player makes a move and then wait for the other payer to make a move. The user interface comprises of option to change the mode of the game in between. 
During the execution of the game, there are four possible outcomes to the moves made by each player:
+ SUCCESS - When a player successfully discovers the location of Gopher.
+ NEAR MISS - When a player guesses a cell within the 9 cells surrounding the gopher cell.
+ CLOSE GUESS - When a player guesses a cell which is at a distance of 2 cells from the gopher cell.
+ COMPLETE MISS - When a player guesses a cell which doesn't meet the afore-mentioned criterias.
+ DISASTER - When a player guesses a cell which has been already occupied by itself or other player.

## Implementation
The parallel execution of moves made by the players has been achieved by multi-threading using Handlers in Android. The main (UI) thread manages the task of updating the UI based on its interaction with the Player threads. The players threads ***Player1Runnable*** and  ***Player2Runnable*** interact with the UI thread using the UI thread's Handler and send its corresponding position. The UI thread also co-ordinates the moves of the player threads in the Guess by Guess play mode.
Both the player threads exercise different heuristics to guess the next move. Player 1 thread makes random moves by utilizing the ```Math.random()``` method of android. The player 2 thread on the other hand makes sequential moves.
After determining the position of its next move, the player threads inform the UI thread about the next position based on which the UI thread provides feedback i.e. whether outcome of the move is ***SUCCESS, NEAR MISS, CLOSE GUESS, COMPLETE MISS OR DISASTER.*** The UI thread then updates the UI and places the corresponding player at the mentioned position in the grid. The Player 1 thread posts a runnable instance to the UI thread using the ```Handler.post()``` method. This runnable instance executes automatically on reaching the UI thread. The Player 2 threadon the other hand posts messages to the UI thread using ```Handler.sendMessage()``` method. The UI thread then handles the message using the ```handleMessage()``` method.
The game terminates when either of the player threads determines correctly the location of the gopher.

## Setting up the environment
After cloning the repository, import the project into Android Studio. Setup an emulator using AVD manager. The application supports API level 28 (Pie) and above. The application can then be launched directly and does not require any permission.
