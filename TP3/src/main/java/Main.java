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

        int n = 0; float l = 0f; float t_terminal = 0f;
        float r = 0f, mass = 0f, vMax = 0f;

        float R = 0f, Mass = 0f, V = 0f, X = 0f, Y = 0f;

        try {
            Scanner lector = new Scanner(new File(args[0]));

            n = Integer.valueOf(lector.nextLine());
            l = Float.valueOf(lector.nextLine());
            t_terminal = Float.valueOf(lector.nextLine());

            r = Float.valueOf(lector.nextLine());
            mass = Float.valueOf(lector.nextLine());
            vMax = Float.valueOf(lector.nextLine());

            R = Float.valueOf(lector.nextLine());
            Mass = Float.valueOf(lector.nextLine());
            V = Float.valueOf(lector.nextLine());
            X = Float.valueOf(lector.nextLine());
            Y = Float.valueOf(lector.nextLine());

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

        Calculator calculador = new Calculator(n, l, particulas);

        writeXYZ(Sparticulas, "output.xyz");

        float tc = 0f;

        while (t_terminal > tc){
            tc = calculador.actualizacion() + tc;

            Sparticulas = calculador.toStringParticulas();
            //TODO: tratar de generar todo el output antes y escrbir una sola vez al final
            writeXYZ(Sparticulas, "output.xyz");
        }
    }

    public static void writeXYZ(ArrayList<String> output, String fileName) {
        output.add(0, String.valueOf(output.size()));
        output.add(1, "");

        writeFile(output, fileName);
    }

    public static void writeFile(ArrayList<String> output, String fileName){
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
