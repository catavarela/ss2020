import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static double final_t = 31556926 * 2.5; //s
    private static double delta_t = 100; //s

    public static void main(String[] args) {
        Universe universe = new Universe(Constants.G, Constants.sun_mass, Constants.x0_sun, Constants.y0_sun, Constants.vx0_sun, Constants.vy0_sun,
                Constants.earth_mass, Constants.x0_earth, Constants.y0_earth, Constants.vx0_earth, Constants.vy0_earth,
                Constants.mars_mass, Constants.x0_mars, Constants.y0_mars, Constants.vx0_mars, Constants.vy0_mars);
        writeFile(universe.calculate(final_t, delta_t, Metodo.BEEMAN), "test_run.tsv");

        System.out.println("Distancia mínima: " + universe.getMindistance() + ".");
        System.out.println("Tiempo mínimo: " + universe.getMintime() + ".");
    }

    public static void writeFile(List<String> output, String fileName) {
        Iterator<String> it = output.iterator();

        int i = -1;

        try {
            FileWriter writer = new FileWriter(fileName);

            while(it.hasNext()) {
                i++;

                if (i % 100 == 0){
                    writer.write(it.next() + '\n');
                    writer.write(it.next() + '\n');
                    writer.write(it.next() + '\n');
                    writer.write(it.next() + '\n');
                }

                else {
                    it.next();
                    it.next();
                    it.next();
                    it.next();
                }

            }
            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo" + fileName + '.');
            e.printStackTrace();
        }
    }
}
