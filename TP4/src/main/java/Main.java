import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main {
    private static double final_t = 31556926; //s
    private static double delta_t = 120; //s
    private static boolean hay_cohete = true;
    private static double dia_de_despegue = 8.5;
    private static double crash_time;

    public static void main(String[] args) {

        Body sol = new Body(0d, Constants.sun_mass, Constants.sun_radius, Constants.x0_sun, Constants.y0_sun, Constants.vx0_sun, Constants.vy0_sun, "Sol");
        Body tierra = new Body(0d, Constants.earth_mass, Constants.earth_radius, Constants.x0_earth, Constants.y0_earth, Constants.vx0_earth, Constants.vy0_earth, "Tierra");
        Body marte = new Body(0d, Constants.mars_mass, Constants.mars_radius, Constants.x0_mars, Constants.y0_mars, Constants.vx0_mars, Constants.vy0_mars, "Marte");

        ArrayList<Body> celestial_bodies = new ArrayList<Body>();

        celestial_bodies.add(sol);
        celestial_bodies.add(tierra);
        celestial_bodies.add(marte);

        Universe universe = new Universe(Constants.G, celestial_bodies, hay_cohete, dia_de_despegue);

        crash_time = universe.calculate(final_t, delta_t, Metodo.BEEMAN);

        System.out.println(crash_time/86400);

        writeFile(universe.getResults(), "test_run.tsv");
    }

    public static void writeFile(List<String> output, String fileName) {
        Iterator<String> it = output.iterator();
        double time;
        boolean cohete_viajando = false;

        int i = -1;

        try {
            FileWriter writer = new FileWriter(fileName);

            while(it.hasNext()) {
                i++;

                if (i % 100 == 0){
                    time = Double.valueOf(it.next());

                    writer.write(Double.toString(time) + '\n');
                    writer.write(it.next() + '\n');
                    writer.write(it.next() + '\n');
                    writer.write(it.next() + '\n');

                    if(time/86400 >= dia_de_despegue) {
                        if(crash_time == 0d || time < crash_time) {
                            cohete_viajando = true;
                            writer.write(it.next() + '\n');
                        }else
                            cohete_viajando = false;
                    }
                }

                else {
                    it.next();
                    it.next();
                    it.next();
                    it.next();

                    if(cohete_viajando)
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
