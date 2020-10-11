import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static double final_t = 31556926; //s
    private static double delta_t = 120; //s
    private static boolean hay_cohete = true;
    private static double dia_de_despegue = 8.5;
    private static double crash_time;
    private static String output = "output.tsv";

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
        System.out.println(getDayWithMinDistance());

        writeFile(universe.getResults(), output);
        writeXYZ();
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

    public static double getDayWithMinDistance() {
        double min_distance = Double.MAX_VALUE, current_distance;
        double day = 0, seconds, current_day = 0;
        double Earth_position[] = new double[2], Mars_position[] = new double[2];
        String aux[];
        List<String> results = new ArrayList<String>();

        try {
            Scanner lector = new Scanner(new File(output));

            while(lector.hasNext()) {
                seconds = Double.parseDouble(lector.nextLine());
                lector.nextLine(); // Sun position

                aux = lector.nextLine().split("    ", 2); // Earth position
                Earth_position[0] = Double.valueOf(aux[0]); Earth_position[1] = Double.valueOf(aux[1]);

                aux = lector.nextLine().split("    "); // Mars position
                Mars_position[0] = Double.valueOf(aux[0]); Mars_position[1] = Double.valueOf(aux[1]);

                current_distance = Math.hypot(Earth_position[0] - Mars_position[0], Earth_position[1] - Mars_position[1]);

                if (!lector.hasNextInt())
                    lector.nextLine(); // rocket position

                if(Math.floor(seconds / 86400) == current_day){
                    results.add(Math.floor(seconds / 86400) + "," + current_distance);
                    current_day++;
                }

                if(current_distance < min_distance){
                    min_distance = current_distance;
                    day = seconds / 86400;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writeFile(results, "distanceToMars.csv");
        return day;
    }

    public static void writeXYZ(){
        List<String> XYZ_output = new ArrayList<String>();

        try {
            Scanner lector = new Scanner(new File(output));

            while(lector.hasNext()) {
                lector.nextLine(); // seconds

                XYZ_output.add("");
                XYZ_output.add(lector.nextLine() + "    3e+10    0.5    0.5    0"); //Sun position
                XYZ_output.add(lector.nextLine() + "    1e+10    0    0    1"); //Earth position
                XYZ_output.add(lector.nextLine() + "    1e+10    1    0    0"); //Mars position

                if (!lector.hasNextInt()) {
                    XYZ_output.add(lector.nextLine() + "    0.75e+10    1    1    1"); //Rocket position //TODO: CAMBIAR EL TAMAÑO!
                    XYZ_output.add(XYZ_output.size() - 5,"8");
                } else {
                    XYZ_output.add(XYZ_output.size() - 4,"7");
                }

                XYZ_output.add("-3e+11    -3e+11    1    0    0    0");
                XYZ_output.add("-3e+11    3e+11    1    0    0    0");
                XYZ_output.add("3e+11    -3e+11    1    0    0    0");
                XYZ_output.add("3e+11    3e+11    1    0    0    0");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        writeFile(XYZ_output, "output.xyz");
    }
}
