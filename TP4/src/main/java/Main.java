import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static double final_t = 5; //s

    public static void main(String[] args) {
        Oscilator oscilator = new Oscilator();
        int order = -1;
        double delta_t;

        while(order >= -6) {
            delta_t = Math.pow(10, order);
            writeFile(oscilator.calculate(final_t, delta_t, Metodo.VERLET), "results.csv");
            System.out.println(order);
            order--;
        }

        writeFile(oscilator.getError(), "error.csv");
    }

    public static void writeFile(List<String> output, String fileName) {
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName);

            while (it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo" + fileName + '.');
            e.printStackTrace();
        }
    }
}
