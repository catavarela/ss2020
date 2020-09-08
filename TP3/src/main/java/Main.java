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

public class Main {

    public static void main(String[] args) {

        if(args.length < 2) {
            if(args[1] == null)
                System.out.println("Falta archivo de data estática");

            if(args[0] == null)
                System.out.println("Falta especificar el número de partícula a estudiar");

            exit(1);
        }

        int n = 0, m = 0; float l = 0f; long t_terminal = 0;
        float r = 0f, mass = 0f, vMax = 0f;

        float R = 0f, Mass = 0f, V = 0f, X = 0f, Y = 0f;

        File src;

        try {
            Scanner lector = new Scanner(new File(args[1]));

            n = Integer.valueOf(lector.nextLine());
            l = Float.valueOf(lector.nextLine());
            m = Integer.valueOf(lector.nextLine());
            t_terminal = Long.valueOf(lector.nextLine());

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

        if(m == 0)
            m = Calculator.mCalculator(l, 0f, R);

        Particula [][] heads = null;

        GeneradorParticulas g = new GeneradorParticulas(n, l,  r, m, mass, vMax);
        heads = g.generar(R, Mass,  V, X, Y);
        ArrayList<String> particulas = g.toStringParticulas();

        Calculator calculador = new Calculator(n, l, m, heads);

        writeFile(particulas, "particulas.txt");
        src = new File("particulas.txt");

        //escribir 'particulas' en archivo output.xyz

        long t_init = System.currentTimeMillis();
        float tc = 0f;

        while (t_terminal > System.currentTimeMillis() - t_init){
            tc = calculador.actualizacion() + tc;

            particulas = calculador.toStringParticulas();
            //escribir 'particulas' a archivo output.xyz + tc (tiempo de choque)
        }
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
