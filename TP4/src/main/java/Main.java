import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static double final_t = 10; //s
    private static double delta_t = 1/1000d; //s

    public static void main(String[] args){
        Oscilator oscilator = new Oscilator();

        writeFile(oscilator.calculate(final_t,delta_t, Metodo.GEAR), "results.csv");
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
