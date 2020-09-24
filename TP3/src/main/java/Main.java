import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {

    public static void main(String[] args) {

        if(args.length < 1) {
            if(args[0] == null)
                System.out.println("Falta archivo de data estática");

            exit(1);
        }

        int t_arbitrario = 1;
        int n = 0; double l = 0;
        double r = 0, mass = 0, vMax = 0;

        double R = 0, Mass = 0f, V = 0, X = 0, Y = 0;

        try {
            Scanner lector = new Scanner(new File(args[0]));

            n = Integer.valueOf(lector.nextLine());
            l = Double.valueOf(lector.nextLine());

            r = Double.valueOf(lector.nextLine());
            mass = Double.valueOf(lector.nextLine());
            vMax = Double.valueOf(lector.nextLine());

            R = Double.valueOf(lector.nextLine());
            Mass = Double.valueOf(lector.nextLine());
            V = Double.valueOf(lector.nextLine());
            X = Double.valueOf(lector.nextLine());
            Y = Double.valueOf(lector.nextLine());

            lector.close();

        } catch (FileNotFoundException e) {
            System.out.println("Ocurrió un error al leer el archivo de data estática" + ' ' + args[1] + '.');
            e.printStackTrace();
            exit(1);
        }

        ArrayList<Particula> particulas;

        GeneradorParticulas g = new GeneradorParticulas(n, l,  r, mass, vMax);
        particulas = g.generar(R, Mass,  V, X, Y);
        ArrayList<String> Sparticulas = g.toStringParticulas();

        Calculator calculador = new Calculator(l, particulas);

        writeXYZ(Sparticulas, "output.xyz", false);

        double delta_t_acum = 0;
        Choque prox_choque;

        boolean grandeNoChocoPared = true;

        while (grandeNoChocoPared){
            prox_choque = calculador.actualizacion();

            delta_t_acum = prox_choque.getTc() + delta_t_acum;

            if(prox_choque.getP1().getId() == 1 && prox_choque.getP2() == null)
                grandeNoChocoPared = false;

            //System.out.println("TC: " + delta_t_acum);

            if(delta_t_acum >= t_arbitrario) {
                Sparticulas = calculador.toStringParticulas();

                writeXYZ(Sparticulas, "output.xyz", true);
                t_arbitrario++;
            }

        }
    }

    public static void writeXYZ(ArrayList<String> output, String fileName, boolean append) {
        output.add(0, String.valueOf(output.size() + 4));
        output.add(1, "");

        output.add(2, "0.0 6.0 0.1 0.0 0.0 0.0\n" +
                "6.0 6.0 0.1 0.0 0.0 0.0\n" +
                "6.0 0.0 0.1 0.0 0.0 0.0\n" +
                "0.0 0.0 0.1 0.0 0.0 0.0");

        writeFile(output, fileName, append);
    }

    public static void writeFile(ArrayList<String> output, String fileName, boolean append){
        Iterator<String> it = output.iterator();

        try {
            FileWriter writer = new FileWriter(fileName, append);

            while(it.hasNext())
                writer.write(it.next() + '\n');

            writer.close();

        } catch (IOException e) {
            System.out.println("Ocurrio un error al querer crear/escribir el archivo" + fileName + '.');
            e.printStackTrace();
        }
    }
}
