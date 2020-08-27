import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Board {
    private int[][] current_2D_board;
    private int[][][] current_3D_board;
    private int size;
    private boolean three_dimensional;


    Board(int size, double percentage, boolean three_dimensional) {
        this.size = size;
        this.three_dimensional = three_dimensional;

        Random rand = new Random();
        int min_bound = (int) ((size - 0.2 * size) / 2);
        int max_bound = size - (int) ((size - 0.2 * size) / 2);

        if(three_dimensional) {
            current_3D_board = new int[size][size][size];
            for(int row = min_bound; row < max_bound; row++) {
                for (int column = min_bound; column < max_bound; column++) {
                    for(int depth = min_bound; depth < max_bound; depth++) {
                        current_3D_board[row][column][depth] = rand.nextDouble() < percentage ? 1 : 0;
                    }
                }
            }
        } else {
            current_2D_board = new int[size][size];
            for(int row = min_bound; row < max_bound; row++) {
                for (int column = min_bound; column < max_bound; column++) {
                    current_2D_board[row][column] = rand.nextDouble() < percentage ? 1 : 0;
                }
            }
        }
    }

    public void update() {
        if(three_dimensional) update3D();
        else update2D();
    }

    private void update3D() {
        int neighbours;
        int[][][] next_board = new int[size][size][size];

        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                for(int depth = 0; depth < size; depth++) {
                    neighbours = countNeighbours3D(row,column, depth);

                    if(current_3D_board[row][column][depth] == 1 && !(neighbours == 5 || neighbours == 6 || neighbours == 7)) {
                        next_board[row][column][depth] = 0;
                    } else if(current_3D_board[row][column][depth] == 0 && neighbours == 6) {
                        next_board[row][column][depth] = 1;
                    } else {
                        next_board[row][column][depth] = current_3D_board[row][column][depth];
                    }
                }
            }
        }

        current_3D_board = next_board;
    }

    //TODO: agregar reglas custom
    private void update2D() {
        int neighbours;
        int[][] next_board = new int[size][size];

        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                neighbours = countNeighbours2D(row,column);

                if(current_2D_board[row][column] == 1 && !(neighbours == 2 || neighbours == 3)) {
                    next_board[row][column] = 0;
                } else if(current_2D_board[row][column] == 0 && neighbours == 3) {
                    next_board[row][column] = 1;
                } else {
                    next_board[row][column] = current_2D_board[row][column];
                }
            }
        }

        current_2D_board = next_board;
    }

    private int countNeighbours2D(int row, int column) {
        int up, down, left, right;
        up = row == 0? size - 1 : row - 1;
        down = row == size - 1 ? 0 : row + 1;
        left = column == 0 ? size - 1 : column - 1;
        right =  column == size - 1 ? 0 : column + 1;

        return current_2D_board[up][left] +
               current_2D_board[up][column] +
               current_2D_board[up][right] +
               current_2D_board[row][left] +
               current_2D_board[row][right] +
               current_2D_board[down][left] +
               current_2D_board[down][column] +
               current_2D_board[down][right];
    }

    private int countNeighbours3D(int row, int column, int depth) {
        int up, down, left, right, forward, backward;
        up = row == 0 ? size - 1 : row - 1;
        down = row == size - 1 ? 0 : row + 1;
        left = column == 0 ? size - 1 : column - 1;
        right =  column == size - 1 ? 0 : column + 1;
        backward = depth == 0 ? size - 1 : depth - 1;
        forward = depth == size - 1 ? 0 : depth + 1;

        return current_3D_board[up][left][depth] + current_3D_board[up][left][backward] + current_3D_board[up][left][forward] +
                current_3D_board[up][column][depth] + current_3D_board[up][column][backward] + current_3D_board[up][column][forward] +
                current_3D_board[up][right][depth] + current_3D_board[up][right][backward] + current_3D_board[up][right][forward] +
                current_3D_board[row][left][depth] + current_3D_board[row][left][backward] + current_3D_board[row][left][forward] +
                current_3D_board[row][right][depth] + current_3D_board[row][right][backward] + current_3D_board[row][right][forward] +
                current_3D_board[down][left][depth] + current_3D_board[down][left][backward] + current_3D_board[down][left][forward] +
                current_3D_board[down][column][depth] + current_3D_board[down][column][backward] + current_3D_board[down][column][forward] +
                current_3D_board[down][right][depth] + current_3D_board[down][right][backward] + current_3D_board[down][right][forward] +
                current_3D_board[row][column][backward] + current_3D_board[row][column][forward];
    }

    public List<String> printBoard() {
        if(three_dimensional) return print3DBoard();
        else return print2DBoard();
    }

    private List<String> print2DBoard() {
        int total = 0;
        List<String> output = new ArrayList<String>();

        for(int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(current_2D_board[row][column] == 1) {
                    int dist = (int) Math.hypot(Math.abs(column - size / 2), Math.abs(row - size / 2));
                    output.add(column + " " + row + " " + (1 - dist / (size/2.0)) + " " + dist / (size/2.0));
                    total++;
                }
            }
        }

        if(current_2D_board[0][0] == 0) { output.add("0 0 0 0"); total++; }
        if(current_2D_board[0][size - 1] == 0) { output.add("0 " + (size - 1) + " 0 0"); total++; }
        if(current_2D_board[size - 1][0] == 0) { output.add((size - 1) + " 0 0 0"); total++; }
        if(current_2D_board[size - 1][size - 1] == 0) { output.add((size - 1) + " " + (size - 1) + " 0 0"); total++; }

        output.add(0, String.valueOf(total));
        output.add(1, "");
        return output;
    }

    private List<String> print3DBoard() {
        int total = 0;
        List<String> output = new ArrayList<String>();

        for(int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                for(int depth = 0; depth < size; depth++) {
                    if(current_3D_board[row][column][depth] == 1) {
                        int dist = (int) Math.sqrt(Math.pow(column - size / 2, 2) + Math.pow(row - size / 2, 2) + Math.pow(depth - size / 2, 2));
                        output.add(column + " " + row + " " + depth + " " + (1 - dist / (size/2.0)) + " " + dist / (size/2.0));
                        total++;
                    }
                }
            }
        }

        if(current_3D_board[0][0][0] == 0) { output.add("0 0 0 0 0"); total++; }
        if(current_3D_board[0][0][size - 1] == 0) { output.add("0 0 " + (size - 1) + " 0 0"); total++; }
        if(current_3D_board[0][size - 1][0] == 0) { output.add("0 " + (size - 1) + " 0 0 0"); total++; }
        if(current_3D_board[0][size - 1][size - 1] == 0) { output.add("0 " + (size - 1) + " " + (size - 1) + " 0 0"); total++; }
        if(current_3D_board[size - 1][0][0] == 0) { output.add((size - 1) + " 0 0 0 0"); total++; }
        if(current_3D_board[size - 1][0][size - 1] == 0) { output.add((size - 1) + " 0 " + (size - 1) + " 0 0"); total++; }
        if(current_3D_board[size - 1][size - 1][0] == 0) { output.add((size - 1) + " " + (size - 1) + " 0 0 0"); total++; }
        if(current_3D_board[size - 1][size - 1][size - 1] == 0) { output.add((size - 1) + " " + (size - 1) + " " + (size - 1) + " 0 0"); total++; }

        output.add(0, String.valueOf(total));
        output.add(1, "");
        return output;
    }


}
