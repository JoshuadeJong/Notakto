import java.util.ArrayList;

public class Matrix {

    private int rowSize;
    private int columnSize;
    private ArrayList<Integer> matrix;

    //// Constructors
    public Matrix(int row, int column) {
        this.rowSize = row;
        this.columnSize = column;
        this.matrix = new ArrayList<Integer>();

        for (int i = 0; i < this.rowSize * this.columnSize; i++) {
            this.matrix.add(0);
        }
    }

    // Access Method
    private int pos2Index(int row, int column) {
        return row * this.columnSize + column;
    }

    // Create Methods
    private void setMatrix(int value) {
        for (int i = 0; i < this.rowSize * this.columnSize; i++) {
            this.matrix.set(i, value);
        }
    }

    public void ones() {
        setMatrix(1);
    }

    public void zeros() {
        setMatrix(0);
    }

    public void value(int value) {
        setMatrix(value);
    }

    public void diag(ArrayList<Integer> list) {

        int counter = 0;
        for (int i = 0; i < this.rowSize * this.columnSize; i++) {
            if (0 == i % (this.columnSize + 1)) {
                this.matrix.set(i, list.get(counter));
                counter++;
            } else {
                this.matrix.set(i, 0);
            }
        }
    }

    public void eyes() {

        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < this.rowSize && i < this.columnSize; i++) {
            list.add(1);
        }

        diag(list);
    }

    // Operations
    public int sum() {
        int sum = 0;

        for (int i = 0; i < rowSize * columnSize; i++) {
            sum += matrix.get(i);
        }

        return sum;
    }

    public Matrix scale(int n) {

        Matrix output;
        output = this;

        for (int i = 0; i < rowSize * columnSize; i++) {
            output.matrix.set(i, matrix.get(i) * n);
        }

        return output;
    }

    // Set
    public void set(int row, int column, int value) {
        this.matrix.set(pos2Index(row, column), value);
    }

    public void copy(Matrix A) {
        this.rowSize = A.rowSize;
        this.columnSize = A.columnSize;

        for (int i = 0; i < this.rowSize * this.columnSize; i++) {
            this.matrix.set(i, A.matrix.get(i));
        }
    }

    // Gets
    public Integer get(int row, int column) {

        int pos = pos2Index(row, column);

        if (pos > this.rowSize * this.columnSize) {
            System.out.println("The requested element does not exists.");
            return 0;
        }

        return this.matrix.get(pos2Index(row, column));
    }

    public ArrayList<Integer> getDiag() {

        ArrayList<Integer> list = new ArrayList<Integer>();

        for (int i = 0; i < this.rowSize * this.columnSize; i += this.columnSize + 1) {
            list.add(this.matrix.get(i));
        }

        return list;
    }

    public ArrayList<Integer> getSize() {
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add(this.rowSize);
        list.add(this.columnSize);

        return list;
    }

    public int getRowSize() {
        return rowSize;
    }

    public int getColumnSize() {
        return columnSize;
    }

    //// Print
    public void print() {
        for (int i = 0; i < this.rowSize; i++) {
            for (int j = 0; j < this.columnSize; j++) {
                System.out.print(matrix.get(pos2Index(i, j)) + " ");
            }
            System.out.print("\n");
        }
    }

    public void printRow(int row) {
        for (int j = 0; j < this.columnSize; j++) {
            System.out.print(matrix.get(pos2Index(row, j)) + " ");
        }
    }

    public void printColumn(int column) {
        for (int i = 0; i < this.columnSize; i++) {
            System.out.print(matrix.get(pos2Index(i, column)) + " ");
        }
    }
}