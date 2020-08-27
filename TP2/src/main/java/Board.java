import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private int[][] current_board;
    private int size;

    Board(int size, double percentage) {
        this.size = size;
        current_board = new int[size][size];

        Random rand = new Random();
        int min_bound = (int) ((size - 0.2 * size) / 2);
        int max_bound = size - (int) ((size - 0.2 * size) / 2);

        for(int row = min_bound; row < max_bound; row++) {
            for (int column = min_bound; column < max_bound; column++) {
                current_board[row][column] = rand.nextDouble() < percentage ? 1 : 0;
            }
        }
    }

    //TODO: agregar reglas custom
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

    public List<String> printBoard() {
        int total = 0;
        List<String> output = new ArrayList<String>();

        for(int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(current_board[row][column] == 1) {
                    int dist = (int) Math.hypot( Math.abs(column - size / 2), Math.abs(row - size / 2));
                    output.add(column + " " + row + " " + (1 - dist / (size/2.0)) + " " + dist / (size/2.0));
                    total++;
                }
            }
        }

        if(current_board[0][0] == 0) { output.add(0 + " " + 0 + " 0 0"); total++; }
        if(current_board[0][size - 1] == 0) { output.add(0 + " " + (size - 1) + " 0 0"); total++; }
        if(current_board[size - 1][0] == 0) { output.add((size - 1) + " " + 0 + " 0 0"); total++; }
        if(current_board[size - 1][size - 1] == 0) { output.add((size - 1) + " " + (size - 1) + " 0 0"); total++; }

        output.add(0, String.valueOf(total));
        output.add(1, "");
        return output;
    }


}
