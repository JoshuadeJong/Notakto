import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class UserInterface {

    private Scanner reader; // YES
    private Random rand;    // YES

    // Board Variables
    private Board board;
    private int adjacentLength;
    private ArrayList<String> moves;

    // AI Variables
    private ArrayList<String> aiNames;
    private int level;

    // Game Variables
    private int gameType;
    private ArrayList<Integer> score;


    // Constructor
    public UserInterface(Scanner sc, Random rand) {
        this.reader = sc;
        this.rand = rand;

        this.aiNames = new ArrayList<String>();
        aiNames.add("Ava");
        aiNames.add("GLaDOS");
        aiNames.add("HAL 9000");
    }

    // Main
    public void start() {
        title();
        rules();

        int replay;

        do {
            gameType();     // Ask if they want to play PvP or PvAI

            if (this.gameType == 1) {  // If it is PvAI ask for the AI level
                aiLevel();
            }

            setAdjacentLength();    // Ask how many Xs in a row result in a loss
            setBoard();     // Ask which board and set it up
            setMoves();     // Setup the possible moves

            setScore(); // Set the score

            do {
                int playerTurn = coinFlip();   // Flip a coin to figure out who starts

                if (0 == this.gameType) { // PvP
                    while (this.board.getState()) {
                        turn(playerTurn);
                        playerTurn = (playerTurn + 1) % 2;
                    }
                } else if (1 == this.gameType) { //PvAI
                    ArrayList<Integer> move = new ArrayList<>();
                    AI bot = new AI(this.rand, this.board, this.adjacentLength);

                    while (this.board.getState()) {
                        if (playerTurn == 0) {
                            move = turn(playerTurn);
                        } else {
                            move = bot.turn(this.level, this.board, move);
                            aiThinking(move);
                        }

                        playerTurn = (playerTurn + 1) % 2;
                    }
                }

                updateScore(playerTurn);   // Update winner's score
                winner(playerTurn);     // Display winner
                scoreBoard();      // Display Scoreboard

                replay = replay();  // Does the player wish to replay?
                this.board.reset(); // Reset the board to zeros
            } while (0 == replay); // (0) Player wants to replay with the same settings
        } while (2 != replay); // (1) Player wants to changes settings and play again

        quit(); // (2) Player wants to quit
    }

    //// Game States
    private void gameType() {
        // Question
        String options = "\n" +
                "Who you want to play against?\n" +
                "   (1) Another Player\n" +
                "   (2) AI";

        System.out.println(options);

        // Variables
        ArrayList<String> choices = new ArrayList<>();
        choices.add("1");
        choices.add("2");

        String message = "\n" +
                "Type " + choices.get(0) + " to play against another player.\n" +
                "Type " + choices.get(1) + " to player against an AI.\n" +
                "Type help to see commands.";

        // Interpret Choice
        this.gameType = interpret(choices, options, message);
    }

    private void aiLevel() {
        // Question
        String options = "\n" +
                "Which AI do you want to play against?\n" +
                "   (1) " + aiNames.get(0) + " (Easy)\n" +
                "   (2) " + aiNames.get(1) + " (Medium)\n" +
                "   (3) " + aiNames.get(2) + " (Hard)";

        System.out.println(options);

        // Variables
        ArrayList<String> choices = numberChoices(3);

        String message = "\n" +
                "Type " + choices.get(0) + " to for an easy game.\n" +
                "Type " + choices.get(1) + " to have a struggle.\n" +
                "Type " + choices.get(2) + " to suffer.\n" +
                "Type help to see commands.";

        // Interpret Choice
        this.level = interpret(choices, options, message);
    }

    private ArrayList<Integer> turn(int playerTurn) {

        // Question
        System.out.println("\nPlayers " + (playerTurn + 1) + "'s Turn");
        boardPrint();
        System.out.println("Place a X on the board.");

        // Variables
        String options = "\nPlayer " + (playerTurn + 1) + " can place a X on the board.";
        String message = "\n" +
                "Place a X on the board using the coordinate system. Example: C3\n" +
                "Type help to see commands.";

        // Interpret Choice
        ArrayList<Integer> move = new ArrayList<>();

        int choice;
        while (true) {
            choice = interpret(this.moves, options, message) / 2;
            move.add(0, choice / this.board.getColumn());
            move.add(1, choice % this.board.getColumn());


            if (this.board.add(move.get(0), move.get(1), 1)) {
                break;
            } else {
                System.out.println("You can not play a X in occupied square.");
            }
        }

        return move;
    }

    private void setAdjacentLength() {
        // Put code for optional adjacent lengths
        this.adjacentLength = 3;
    }

    private void setBoard() {
        // Question
        String options = "\n" +
                "Which board do you wish to play on.\n" +
                "   (1) 3x3\n" +
                "   (2) 4x4\n" +
                "   (3) 5x5\n" +
                "   (4) 6x6\n" +
                "   (5) Random";

        System.out.println(options);

        // Variables
        ArrayList<String> choices = new ArrayList<>();

        for (int i = 1; i < 6; i++) {
            choices.add("" + i);
        }

        String message = "\n" +
                "Type 1, 2, 3, 4, or 5 to select the board size.\n" +
                "Type help to see commands.";

        // Interpret Choice
        int choice = interpret(choices, options, message);

        if(4 == choice){ //The board is never square on random
                int row;
                int column;

                do {
                    row = rand.nextInt(3) + 3;
                    column = rand.nextInt(3) + 3;
                }while(row == column);

                this.board = new Board(row, column, this.adjacentLength);
        } else {
            choice += 3;
            this.board = new Board(choice, choice, this.adjacentLength);
        }

    }

    private int replay() {
        // Question
        String options = "\n" +
                "Do you want to play again?\n" +
                "   (1) Yes.\n" +
                "   (2) Yes, but change settings.\n" +
                "   (3) Quit the game.";

        System.out.println(options);

        // Variables
        ArrayList<String> choices = numberChoices(3);

        String message = "\n" +
                "Type " + choices.get(0) + " to replay\n" +
                "Type " + choices.get(1) + " to change the settings and play again.\n" +
                "Type " + choices.get(2) + " to quit the game.\n" +
                "Type help to see commands.";

        // Interpret Choice
        return interpret(choices, options, message);
    }

    //// Interpreter
    private int interpret(ArrayList<String> choices, String options, String message) {
        int count = 0;
        int tries = 3;

        while (count < 100) {
            String choice = reader.nextLine().toLowerCase();

            // Default Options
            switch(choice){
                case "help":
                    help();
                    break;
                case "rules":
                    rules();
                    break;
                case "board":
                    try {
                        this.board.getState();
                        System.out.println();
                        boardPrint();
                    } catch (Exception e) {
                        System.out.println("\nThe board has not be initialized.");
                    }
                    break;
                case "score":
                    try {
                        scoreBoard();
                    } catch (Exception e) {
                        System.out.println("\nThere is no score yet.");
                    }
                    break;
                case "options":
                    System.out.println(options);
                    break;
                case "contact":
                    contact();
                    break;
                case "source":
                    source();
                    break;
                case "quit":
                    quit();
                    break;
            }

            // Options
            if (choices.contains(choice)) {
                return choices.indexOf(choice);
            }

            // Number of entry tries
            if (tries - 1 == count % tries) {
                System.out.println(message);
            }
            count++;
        }

        quit();
        return -1;
    }

    // Method
    private void setMoves() {
        this.moves = new ArrayList<String>();

        for (int i = 0; i < board.getRow(); i++) {
            for (int j = 0; j < board.getColumn(); j++) {
                this.moves.add("" + (i + 1) + (char) ('a' + j));
                this.moves.add("" + (char) ('a' + j) + (i + 1));
            }
        }
    }

    private void setScore() {

        this.score = new ArrayList<>();
        this.score.add(0, 0);
        this.score.add(1, 0);
    }

    private void updateScore(int playerIndex) {
        this.score.set(playerIndex, this.score.get(playerIndex) + 1);
    }

    private ArrayList<String> numberChoices(int amount){

        ArrayList<String> choices = new ArrayList<>();

        for(int i = 1; i <= amount; i++){
            choices.add("" + i);
        }

        return choices;
    }

    // Messages
    private void title() {
        System.out.println(
                "  _   _         _          _     _                 \n" +
                        " | \\ | |       | |        | |   | |               \n" +
                        " |  \\| |  ___  | |_  __ _ | | __| |_  ___         \n" +
                        " | . ` | / _ \\ | __|/ _` || |/ /| __|/ _ \\       \n" +
                        " | |\\  || (_) || |_| (_| ||   < | |_| (_) |       \n" +
                        " |_| \\_| \\___/  \\__|\\__,_||_|\\_\\ \\__|\\___/ \n" +
                        "                                  By Joshua de Jong");
    }

    private void boardPrint() {

        // Print Letters
        for (int j = 0; j < board.getColumn(); j++) {
            System.out.print("   " + (char) ('a' + j) + "  ");
        }
        System.out.println();

        for (int i = 0; i < board.getRow(); i++) {
            for (int j = 0; j < board.getColumn(); j++) {
                if (0 == j) {
                    System.out.print("      |");
                } else if (board.getColumn() - 1 == j) {
                    System.out.print("     \n");
                } else {
                    System.out.print("     |");
                }
            }

            for (int j = 0; j < board.getColumn(); j++) {
                if (0 == j) {
                    System.out.print("" + (i + 1) + "  ");
                    board.printChar(i,j);
                    System.out.print("  |");
                } else if (board.getColumn() - 1 == j) {
                    System.out.print("  ");
                    board.printChar(i,j);
                    System.out.print("  \n");
                } else {
                    System.out.print("  ");
                    board.printChar(i,j);
                    System.out.print("  |");
                }
            }

            if (i != board.getRow() - 1) {
                for (int j = 0; j < board.getColumn(); j++) {
                    if (0 == j) {
                        System.out.print(" _____|");
                    } else if (board.getColumn() - 1 == j) {
                        System.out.print("_____\n");
                    } else {
                        System.out.print("_____|");
                    }
                }
            } else {
                for (int j = 0; j < board.getColumn(); j++) {
                    if (0 == j) {
                        System.out.print("      |");
                    } else if (board.getColumn() - 1 == j) {
                        System.out.print("     \n");
                    } else {
                        System.out.print("     |");
                    }
                }
            }
        }
        System.out.println();

    }

    private int coinFlip() {

        //Message
        System.out.print("\nFlipping the coin");

        dots(350);

        Random rand = new Random();
        int startPlayer = rand.nextInt(100) % 2;

        if (0 == startPlayer) {
            System.out.println("  HEADS");
            System.out.println("Player 1 Starts!");
        } else {
            System.out.println("  TAILS");
            if (0 == this.gameType) {
                System.out.println("Player 2 Starts!");
            } else {
                System.out.println("" + this.aiNames.get(this.level) + " Starts!");
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return startPlayer;
    }

    private void aiThinking(ArrayList<Integer> move) {
        System.out.println();
        System.out.print("" + aiNames.get(this.level) + " is thinking");
        dots(300);
        System.out.printf("  and played %s%d", (char) ('a' + move.get(1)), (move.get(0) + 1));
        System.out.println();
    }

    private void winner(int player) {

        System.out.println();
        boardPrint();

        if (0 == this.gameType) {
            System.out.println("Player " + (player + 1) + " Wins!");
        } else {
            if (0 == player) {
                System.out.println("Player 1 Wins!");
            } else {
                System.out.println("" + this.aiNames.get(this.level) + "Wins!");
            }
        }
    }

    private void scoreBoard() {

        System.out.print("Score Player 1: " + this.score.get(0));

        System.out.print("    ");

        if (0 == this.gameType) {
            System.out.print("Player 2: " + this.score.get(1));
        } else {
            System.out.print("" + this.aiNames.get(this.level) + ": " + this.score.get(1));
        }

        System.out.println();

    }

    private void help() {
        System.out.println("\n" +
                "Help\n" +
                "   rules   - Prints the rules for the game of Notakto.\n" +
                "   options - Prints the current input options.\n" +
                "   board   - Reprint the board.\n" +
                "   score   - Prints the score between both parties.\n" +
                "   contact - How to reach the creator of this game.\n" +
                "   source  - Where the source code for this game can be obtained.\n" +
                "   quit    - Leave the game.");
    }

    private void rules() {
        System.out.println("\n" +
                "Rules\n" +
                " 1) Both players take turns placing a X in a unoccupied square.\n" +
                " 2) Win by forcing the other player make a three-in-a-row of Xs.");
    }

    private void contact() {
        System.out.println("\n" +
                "Contact\n" +
                " Name: Joshua de Jong\n" +
                " Email: JoshuaKdeJong@gmail.com\n" +
                " LinkedIn: https://www.linkedin.com/in/joshua-de-jong/" +
                " GitHub: https://github.com/ManVanMaan");
    }

    private void source() {
        System.out.println("\n" +
                "Source\n" +
                " Dropbox: https://www.dropbox.com/sh/d8s8v0kpt793l2j/AABNeCBSAZX86c6q5n_HwBw1a?dl=0\n" +
                " Github: https://github.com/ManVanMaan/Notakto");
    }

    private void dots(int speed) {
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(speed);
                System.out.print("  .");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // State Changer

    private void quit() {
        System.out.print("\n" + "Quitting");
        dots(350);
        System.out.println();
        System.exit(0);
    }


}