public class Main {
    public static void main(String[] args) {
        //TODO: recibir por input el porcentaje de celdas vivas y el tamaño

        Board board = new Board(50,0.5);
        board.printBoard();
        System.out.println("=========================");
        board.update();
        board.printBoard();
        return;
    }

}