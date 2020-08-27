import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if(args.length < 3) {
            if(args[0] == null)
                System.out.println("Falta especificar el tamaño del tablero");

            if(args[1] == null)
                System.out.println("Falta especificar el porcentaje de celdas vivas");

            if(args[2] == null)
                System.out.println("Falta especificar el número de iteraciones");

            exit(1);
        }

        int size = Integer.parseInt(args[0]);
        double percentage = Double.parseDouble(args[1]);
        int iterations = Integer.parseInt(args[2]);
        Board board = new Board(size,percentage);
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
