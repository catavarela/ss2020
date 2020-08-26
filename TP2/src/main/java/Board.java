import java.util.Random;

public class Board {
    private int[][] current_board;
    private int size;

    //TODO: reducir el espacio donde inician las celdas vivas
    //TODO: definir el porcentaje de celdas vivas
    Board(int size, int percentage) {
        this.size = size;
        current_board = new int[size][size];

        Random rand = new Random();

        for(int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                current_board[row][column] = rand.nextInt(2);
            }
        }
    }

    public void update() {
        int neighbours;
        int[][] next_board = new int[size][size];

        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                neighbours = countNeighbours(row,column);

                if(current_board[row][column] == 1 && !(neighbours == 2 || neighbours == 3)) {
                    next_board[row][column] = 0;
                } else if(current_board[row][column] == 0 && neighbours == 3) {
                    next_board[row][column] = 1;
                } else {
                    next_board[row][column] = current_board[row][column];
                }
            }
        }

        current_board = next_board;
    }

    private int countNeighbours(int row, int column) {
        int up, down, left, right;
        up = row == 0? size - 1 : row - 1;
        down = row == size - 1 ? 0 : row + 1;
        left = column == 0 ? size - 1 : column - 1;
        right =  column == size - 1 ? 0 : column + 1;

        return current_board[up][left] +
               current_board[up][column] +
               current_board[up][right] +
               current_board[row][left] +
               current_board[row][right] +
               current_board[down][left] +
               current_board[down][column] +
               current_board[down][right];
    }

    public void printBoard() {
        for(int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                System.out.print(current_board[row][column]);
            }
            System.out.print('\n');
        }
    }


}
