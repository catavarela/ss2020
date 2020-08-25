public class Board {
    Boolean current_board[][] = null;
    Boolean last_board[][] = null;
    int size;

    //TODO: constructor
    public Board(int size, int percentage) {
        this.size = size;
    }

    public void update() {
        int neighbours;

        for(int row = 0; row < size; row++) {
            for(int column = 0; column < size; column++) {
                neighbours = countNeighbours(row,column);

                if(last_board[row][column] && !(neighbours == 2 || neighbours == 3)) {
                    current_board[row][column] = false;
                } else if(!last_board[row][column] && neighbours == 3) {
                    current_board[row][column] = true;
                } else {
                    current_board[row][column] = last_board[row][column];
                }
            }
        }

        last_board = current_board;
    }

    private int countNeighbours(int row, int column) {
        int up, down, left, right;
        up = row == 0? size - 1 : row - 1;
        down = row == size - 1 ? 0 : row + 1;
        left = column == 0 ? size - 1 : column - 1;
        right =  column == size - 1 ? 0 : column + 1;

        int count = 0;
        if(last_board[up][left]) count++;
        if(last_board[up][column]) count++;
        if(last_board[up][right]) count++;
        if(last_board[row][left]) count++;
        if(last_board[row][right]) count++;
        if(last_board[down][left]) count++;
        if(last_board[down][column]) count++;
        if(last_board[down][right]) count++;

        return count;
    }


}
