public class Board {

    private int adjacentLength;
    private Matrix board;
    private boolean state; // true = board is still playable

    // Constructor
    public Board(int row, int column, int adjacentLength) {
        this.adjacentLength = adjacentLength;
        this.board = new Matrix(row, column);
        this.board.zeros();
        this.state = true;
    }

    // Methods
    public void reset() {
        this.board.zeros();
        this.state = true;
    }

    public boolean add(int row, int column, int playerValue) {

        // Is the board still alive?
        if (this.state) {

            // Check if we can place the piece
            if (this.board.get(row, column) == 0) {
                this.board.set(row, column, playerValue);
                this.check(row, column, playerValue);
                return true;
            }
        }

        return false;
    }

    private void check(int row, int column, int playerValue) {

        // Check Column
        int iStart = row - this.adjacentLength;
        Integer counter = 0;

        if (iStart < 0) {
            iStart = 0;
        }

        for (int i = iStart; i < getRow() && i < row + this.adjacentLength; i++) {
            counter = progressCounter(i, column, playerValue, counter);
            if (counterCheck(counter)) {
                return;
            }
        }

        // Check Row
        int jStart = column - this.adjacentLength;
        counter = 0;

        if (jStart < 0) {
            jStart = 0;
        }

        for (int j = jStart; j < getColumn() && j < column + this.adjacentLength; j++) {
            counter = progressCounter(row, j, playerValue, counter);
            if (counterCheck(counter)) {
                return;
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

        for (int i = iStart, j = jStart; (i < getRow() && i < row + this.adjacentLength) && (j < getColumn() && j < column + this.adjacentLength); i++, j++) {
            counter = progressCounter(i, j, playerValue, counter);
            if (counterCheck(counter)) {
                return;
            }
        }

        // Check anti-Diagonal
        iStart = row + column;
        jStart = row + column - (getRow() - 1);
        counter = 0;


        if (iStart >= getRow()) {
            iStart = getRow() - 1;
        }

        if (jStart < 0) {
            jStart = 0;
        }

        for (int i = iStart, j = jStart; (i >= 0 && i >= row - this.adjacentLength) && (j < getColumn() && j < column + this.adjacentLength); i--, j++) {
            counter = progressCounter(i, j, playerValue, counter);
            if (counterCheck(counter)) {
                return;
            }
        }
    }

    private int progressCounter(int i, int j, int playerValue, int counter){
        if(this.board.get(i,j).equals(playerValue)){
            counter++;
        } else {
            counter = 0;
        }

        return counter;
    }

    private boolean counterCheck(int counter) {

        if (counter == this.adjacentLength) {
            this.state = false;
            return true;
        }

        return false;
    }

    // Gets

    public int getRow() {
        return this.board.getRowSize();
    }

    public int getColumn() {
        return this.board.getColumnSize();
    }

    public Matrix getBoard() {
        return this.board;
    }

    public boolean getState() {
        return this.state;
    }

    // Print
    public void print() {
        this.board.print();
    }

    public void printChar(int row, int column) {


        switch (this.board.get(row, column)) {
            case 0:
                System.out.print('-');
                break;
            case 1:
                System.out.print("\u001B[31m" + 'x' + "\u001B[0m");
                break;
            case 2:
                System.out.print("\u001B[32m" + 'o' + "\u001B[0m");
                break;
            case 3:
                System.out.print("\u001B[33m" + '%' + "\u001B[0m");
                break;
            case 4:
                System.out.print("\u001B[34m" + '?' + "\u001B[0m");
                break;
            default:
                System.out.print("\u001B[35m" + ' ' + "\u001B[0m");
                break;
        }

    }
}