import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.System.exit;

public class Main {
    public static void main(String[] args) {
        Track track = new Track();
        writeFile(track.run(), "output100.xyz", false);

        //variateQuantity(5, 145, 10, "outputA150OnlyMeans.csv", Exercise.A);

        //variateQuantityAndTrackWidth(6.0, 6.0, 0.5, 0.5, 4.0, 0.5, "outputB150.csv");

    }

    public static void writeFile(List<String> output, String fileName, boolean append) {
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName, append);

            while (it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo " + fileName + '.');
            e.printStackTrace();
        }
    }

    /* <---------
    public static void variateQuantityAndTrackWidth(double min_ext_radius, double max_ext_radius, double step, int min, int max, int step_q,String csvName){
        double current_radius = min_ext_radius;

        //List<String> title = new ArrayList<String>();
        //title.add("Quantity,Density,Velocity,Time,Track_Width");

        //writeFile(title, csvName, false);

        while (current_radius <= max_ext_radius){
            Constants.extTrackRadius = current_radius;

            variateQuantity(min, max, step_q, csvName, Exercise.B);

            current_radius += step;
        }

    }

    public static void variateQuantity (int min, int max, int step, String csvName, Exercise exercise){
        int current_quantity = min;
        Track track;

        if(exercise == Exercise.A){
            List<String> title = new ArrayList<String>();
            title.add("Quantity,Density,Velocity,Time");

            writeFile(title, csvName, false);
        }

        while (current_quantity <= max){
            //Constants.quantity = (int)Math.floor(current_quantity * Math.PI * (Math.pow(Constants.extTrackRadius, 2) - Math.pow(Constants.intTrackRadius, 2)));

            Constants.quantity = current_quantity;

            track = new Track();

            if(track.createParticles() != Constants.maxTries) {
                System.out.println("Could not create track using " + Constants.quantity + " particles. Tried " + Constants.maxTries + " different times before exiting.");

                exit(1);
            }

            track.run(/*current_quantity);*/
/*
            writeExerciseOutput(exercise, track, csvName);

            current_quantity += step;
        }
    }

    private static void writeExerciseOutput(Exercise exercise, Track track, String csvName){
        switch (exercise){
            case A:
                writeFile(track.getOutputA(), csvName, true);
                break;

            case B:
                writeFile(track.getOutputB(), csvName, true);
                break;
        }
    }

     ---------> */
}