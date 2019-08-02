import java.util.ArrayList;
import java.util.Random;

public class AI {

    private Random rand;
    private Matrix curBoard;    // The Current Board State
    private Matrix noMove;  // Where the AI Can't Place
    private int adjacentLength; //

    // Constructor
    public AI(Random rand, Board board, int adjacentLength) {
        this.rand = rand;
        this.curBoard = new Matrix(board.getRow(), board.getColumn());
        this.adjacentLength = adjacentLength;
        this.noMove = new Matrix(curBoard.getRowSize(), curBoard.getColumnSize());
    }

    // Turn Logic
    public ArrayList<Integer> turn(int level, Board board, ArrayList<Integer> lastMove) {

        // Update Current Matrix and Previous Matrix
        this.curBoard = board.getBoard();

        // Set noMove Matrix
        if (lastMove.size() != 0) {
            setNoMove(lastMove, this.noMove);
        }

        // Initialize Move
        ArrayList<Integer> move = new ArrayList<>();
        move.add(0);
        move.add(0);

        // Does a move exist?
        if (curBoard.getRowSize() * curBoard.getColumnSize() == noMove.sum()) {
            move = forcedMove(move);
        } else {
            switch (level) {
                case 0: // Easy
                    move = turnEasy(move);
                    break;
                case 1: // Medium
                    move = turnMedium(move);
                    break;
                case 2: // Hard
                    move = turnHard(move);
                    break;
            }
        }

        // Update current matrix and noMove matrix
        board.add(move.get(0), move.get(1), 1);
        setNoMove(move, this.noMove);

        return move;
    }

    private ArrayList<Integer> turnEasy(ArrayList<Integer> nextMove) {

        // Pick Any Empty Square
        do {
            nextMove.set(0, rand.nextInt(100000) % curBoard.getRowSize());
            nextMove.set(1, rand.nextInt(100000) % curBoard.getColumnSize());
        } while (!curBoard.get(nextMove.get(0), nextMove.get(1)).equals(0));

        return nextMove;
    }

    private ArrayList<Integer> turnMedium(ArrayList<Integer> nextMove) {

        // Pick a square which won't kill us
        do {
            nextMove.set(0, rand.nextInt(100000) % curBoard.getRowSize());
            nextMove.set(1, rand.nextInt(100000) % curBoard.getColumnSize());
        } while (noMove.get(nextMove.get(0), nextMove.get(1)).equals(1));

        return nextMove;
    }

    private ArrayList<Integer> turnHard(ArrayList<Integer> nextMove) {

        // If AI has first turn
        if (this.curBoard.sum() == 0) {
            return turnEasy(nextMove);
        }

        // Find scores for next move
        Matrix scores = scoreMatrix(this.noMove);

        // Pick the lowest even score position
        boolean moveFound = false;
        int minScore = curBoard.getRowSize() * curBoard.getColumnSize() + 1;

        // Create possible move list
        ArrayList<Integer> moveList = new ArrayList<>();

        for (int i = 0; i < scores.getRowSize(); i++) {
            for (int j = 0; j < scores.getColumnSize(); j++) {
                if (0 == scores.get(i, j) % 2 && scores.get(i, j) <= minScore) {

                    if (scores.get(i, j) < minScore) {
                        minScore = scores.get(i, j);
                        moveList.clear();
                    }

                    moveList.add(i);
                    moveList.add(j);

                    moveFound = true;
                }
            }
        }

        // If no optimal move was found pick a random one which won't kill us.
        if (!moveFound) {
            nextMove = turnMedium(nextMove);
        } else {
            // Select a random square with the min score
            int r = 2 * rand.nextInt(moveList.size() / 2);
            nextMove.set(0, moveList.get(r));
            nextMove.set(1, moveList.get(r + 1));
        }

        return nextMove;
    }

    // Methods
    private void setNoMove(ArrayList<Integer> lastMove, Matrix noMove) {

        // Set the last move to be a No Move Location
        noMove.set(lastMove.get(0), lastMove.get(1), 1);

        // Search area
        ArrayList<Integer> lowerBound = new ArrayList<>();
        ArrayList<Integer> upperBound = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            lowerBound.add(lastMove.get(i) - this.adjacentLength);
            upperBound.add(lastMove.get(i) + this.adjacentLength);

            if (lowerBound.get(i) < 0) {
                lowerBound.set(i, 0);
            }

            if (upperBound.get(i) > this.curBoard.getSize().get(i)) {
                upperBound.set(i, curBoard.getSize().get(i));
            }
        }

        // Search
        for (int i = lowerBound.get(0); i < upperBound.get(0); i++) {
            for (int j = lowerBound.get(1); j < upperBound.get(1); j++) {
                if (noMove.get(i, j).equals(0)) {
                    this.curBoard.set(i, j, 1);

                    if (check(i, j)) {
                        noMove.set(i, j, 1);
                    }

                    this.curBoard.set(i, j, 0);
                }
            }
        }
    }

    private boolean check(int row, int column) {

        // Check Column
        int iStart = row - this.adjacentLength;
        int counter = 0;

        if (iStart < 0) {
            iStart = 0;
        }

        for (int i = iStart; i < this.curBoard.getRowSize() && i < row + this.adjacentLength; i++) {
            counter = progressCounter(i, column, counter);
            if (this.adjacentLength == counter) {
                return true;
            }
        }

        // Check Row
        int jStart = column - this.adjacentLength;
        counter = 0;

        if (jStart < 0) {
            jStart = 0;
        }

        for (int j = jStart; j < this.curBoard.getColumnSize() && j < column + this.adjacentLength; j++) {
            counter = progressCounter(row, j, counter);
            if (this.adjacentLength == counter) {
                return true;
            }
        }

        // Check Diagonal
        iStart = row - column;
        jStart = 0;
        counter = 0;

        if (row < column) {
            iStart = 0;
            jStart = column - row;
        }

        for (int i = iStart, j = jStart; (i < this.curBoard.getRowSize() && i < row + this.adjacentLength) && (j < this.curBoard.getColumnSize() && j < column + this.adjacentLength); i++, j++) {
            counter = progressCounter(i, j, counter);
            if (this.adjacentLength == counter) {
                return true;
            }
        }

        // Check Anti-Diagonal
        iStart = row + column;
        jStart = row + column - (this.curBoard.getRowSize() - 1);
        counter = 0;

        if (iStart >= this.curBoard.getRowSize()) {
            iStart = this.curBoard.getRowSize() - 1;
        }

        if (jStart < 0) {
            jStart = 0;
        }

        for (int i = iStart, j = jStart; (i >= 0 && i >= row - this.adjacentLength) && (j < this.curBoard.getColumnSize() && j < column + this.adjacentLength); i--, j++) {
            counter = progressCounter(i, j, counter);
            if (this.adjacentLength == counter) {
                return true;
            }
        }

        return false;
    }

    private int progressCounter(int row, int column, int counter) {
        if (this.curBoard.get(row, column) == 1) {
            counter++;
        } else {
            counter = 0;
        }

        return counter;
    }

    private ArrayList<Integer> forcedMove(ArrayList<Integer> nextMove) {

        search_loop:
        for (int i = 0; i < this.curBoard.getRowSize(); i++) {
            for (int j = 0; j < this.curBoard.getColumnSize(); j++) {
                if (this.curBoard.get(i, j) == 0) {
                    nextMove.set(0, i);
                    nextMove.set(1, j);
                    break search_loop;
                }
            }
        }

        return nextMove;
    }

    private Matrix scoreMatrix(Matrix noMove) {

        Matrix score = new Matrix(noMove.getRowSize(), noMove.getColumnSize());
        score.value(-1);

        ArrayList<Integer> move = new ArrayList<>();

        Matrix noMoveClone = new Matrix(noMove.getRowSize(), noMove.getColumnSize());

        for (int i = 0; i < noMove.getRowSize(); i++) {
            for (int j = 0; j < noMove.getColumnSize(); j++) {
                if (noMove.get(i, j) != 1) {

                    // Set Move
                    move.add(0, i);
                    move.add(1, j);

                    // Update curBoard and noMove
                    this.curBoard.set(i, j, 1);
                    noMoveClone.copy(noMove);
                    setNoMove(move, noMoveClone);

                    // Calculate the score
                    score.set(i, j, this.curBoard.getRowSize() * this.curBoard.getColumnSize() - noMoveClone.sum());

                    // Retrogress curBoard
                    this.curBoard.set(i, j, 0);
                }
            }
        }

        return score;
    }

    // Gets
    public void getCurBoard() {
        curBoard.print();
    }

    public void getNoMove() {
        noMove.print();
    }

}