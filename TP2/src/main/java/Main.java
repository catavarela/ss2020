import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        if(args.length < 6) {
            if(args[0] == null)
                System.out.println("Falta especificar el tamaño del tablero");

            if(args[1] == null)
                System.out.println("Falta especificar el porcentaje de celdas vivas");

            if(args[2] == null)
                System.out.println("Falta especificar el número de iteraciones");

            if(args[3] == null)
                System.out.println("Falta especificar si es en tres dimensiones");

            if(args[4] == null)
                System.out.println("Falta especificar el número de regla a utilizar");

            if(args[5] == null)
                System.out.println("Falta especificar la cantidad de corridas");

            exit(1);
        }

        int size = Integer.parseInt(args[0]);
        double percentage = Double.parseDouble(args[1]);
        int iterations = Integer.parseInt(args[2]);
        boolean three_dimensional = Boolean.parseBoolean(args[3]);
        int rule = Integer.parseInt(args[4]);
        int corridas = Integer.parseInt(args[5]);

        int aux_corridas = corridas;
        List<String> aliveCells = new ArrayList<String>();
        List<String> furthestCells = new ArrayList<String>();
        List<String> output = null;

        while (corridas-- > 0)
            if(corridas == 0)
                output = correr(true,aux_corridas-corridas,iterations,size, percentage, three_dimensional, rule, aliveCells, furthestCells);
            else
                output = correr(false,aux_corridas-corridas,iterations,size, percentage, three_dimensional, rule, aliveCells, furthestCells);

        //por ahora el output es de la ultima corrida
        writeFile(output, "output.xyz");

        writeFileCSV(aliveCells, "aliveCells.csv", "Corrida,Regla,Porcentaje,Iteracion,Vivas");
        writeFileCSV(furthestCells, "furthestCells.csv", "Corrida,Regla,Porcentaje,Iteracion,Lejania");
    }

    public static List<String> correr(boolean ultima_corrida, int corrida, int iterations, int size, double percentage, boolean three_dimensional, int rule, List<String> aliveCells, List<String> furthestCells){
        Board board = new Board(size, percentage, three_dimensional, rule);
        List<String> output = null;

        if(ultima_corrida)
            output = board.printBoard();

        aliveCells.add(String.valueOf(corrida) + "," + rule + "," + percentage + "," + "0" + "," + String.valueOf(board.getAliveCells()));
        furthestCells.add(String.valueOf(corrida) + "," + rule + "," + percentage + "," + "0" + String.valueOf(board.getFurthestCell()));

        int aux_iterations = iterations;

        while(iterations-- > 0) {
            board.update();

            if(ultima_corrida)
                output.addAll(board.printBoard());

            aliveCells.add(String.valueOf(corrida) + "," + rule + "," + percentage + "," + String.valueOf(aux_iterations-iterations) + "," + String.valueOf(board.getAliveCells()));
            furthestCells.add(String.valueOf(corrida) + "," + rule + "," + percentage + "," + String.valueOf(aux_iterations-iterations) + "," + String.valueOf(board.getFurthestCell()));
        }

        return output;
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

    public static void writeFileCSV(List<String> output, String fileName, String firstRow){
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName);

            writer.write(firstRow + '\n');

            while(it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo" + fileName + '.');
            e.printStackTrace();
        }
    }
}
