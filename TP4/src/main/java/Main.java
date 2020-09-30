import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static double final_t = 5; //s
    private static double delta_t = 1/60f; //s
    public static void main(String[] args){
        double current_t = 0;
        Oscilator oscilator = new Oscilator();

        List<String> analyticResults = new ArrayList<String>();
        analyticResults.add("Time,Position");

        while(current_t < final_t) {
            analyticResults.add(current_t + "," + oscilator.analyticSolution(current_t));
            current_t += delta_t;
        }

        writeFile(analyticResults, "results.csv");
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
