import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //TODO: recibir por input el porcentaje de celdas vivas, el tamaño y el número de iteraciones

        int iterations = 1000;
        Board board = new Board(100,0.5);
        List<String> output = board.printBoard();

        while(iterations-- > 0) {
            board.update();
            output.addAll(board.printBoard());
        }

        writeFile(output, "output.xyz");
    }

    public static void writeFile(List<String> output, String fileName){
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName);

            while(it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo" + fileName + '.');
            e.printStackTrace();
        }
    }
}
