import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static double final_t = 86400 * 687; //s
    private static double delta_t = 100; //s
    private static boolean hay_cohete = false;
    private static double crash_time;
    private static String output = "norocket.tsv";

    public static void main(String[] args) {
        double dia_de_despegue = 86400 * 275 + 15 * 3600;

            Body sol = new Body(0d, Constants.sun_mass, Constants.sun_radius, Constants.x0_sun, Constants.y0_sun, Constants.vx0_sun, Constants.vy0_sun, "Sol");
            Body tierra = new Body(0d, Constants.earth_mass, Constants.earth_radius, Constants.x0_earth, Constants.y0_earth, Constants.vx0_earth, Constants.vy0_earth, "Tierra");
            Body marte = new Body(0d, Constants.mars_mass, Constants.mars_radius, Constants.x0_mars, Constants.y0_mars, Constants.vx0_mars, Constants.vy0_mars, "Marte");

            ArrayList<Body> celestial_bodies = new ArrayList<Body>();
            celestial_bodies.add(sol);
            celestial_bodies.add(tierra);
            celestial_bodies.add(marte);

            //Universe universe = new Universe(Constants.G, celestial_bodies, hay_cohete, dia_de_despegue);

            //crash_time = universe.calculate(final_t, delta_t, Metodo.BEEMAN);

            writeFile(universe.getResults(), output);
            //writeFile(universe.getRocketSpeed(), "rocketSpeed.csv");
            writeXYZ();
            //System.out.println(getMinDistance());
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

    public static double getMinDistance() {
        double min_distance = Double.MAX_VALUE, current_distance = 0;
        double rocket_position[] = new double[2], Mars_position[] = new double[2];
        String aux[];

        try {
            Scanner lector = new Scanner(new File(output));

            while(lector.hasNext()) {
                Double.parseDouble(lector.nextLine()); // seconds
                lector.nextLine(); // Sun position
                lector.nextLine(); // Earth position

                aux = lector.nextLine().split("    "); // Mars position
                Mars_position[0] = Double.valueOf(aux[0]); Mars_position[1] = Double.valueOf(aux[1]);

                if (!lector.hasNextInt()) {
                    aux = lector.nextLine().split("    ", 2); // Rocket position
                    rocket_position[0] = Double.valueOf(aux[0]); rocket_position[1] = Double.valueOf(aux[1]);
                    current_distance = Math.hypot(rocket_position[0] - Mars_position[0], rocket_position[1] - Mars_position[1]);

                    if(current_distance < min_distance){
                        min_distance = current_distance;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return min_distance;
    }

    public static void writeXYZ(){
        List<String> XYZ_output = new ArrayList<String>();

        try {
            Scanner lector = new Scanner(new File(output));
            while(lector.hasNext()) {
                lector.nextLine(); // seconds

                XYZ_output.add("");
                XYZ_output.add(lector.nextLine() + "    9e+09    0.5    0.5    0"); //Sun position
                XYZ_output.add(lector.nextLine() + "    4e+09    0    0    1"); //Earth position
                XYZ_output.add(lector.nextLine() + "    4e+09    1    0    0"); //Mars position

                if (lector.hasNext() && !lector.hasNextInt()) {
                    XYZ_output.add(lector.nextLine() + "    2e+09    1    1    1"); //Rocket position
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

        writeFile(XYZ_output, "norocket.xyz");
    }
}
