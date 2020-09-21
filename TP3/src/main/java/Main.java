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

import static java.lang.System.*;

public class Main {

    public static void main(String[] args) {

        if(args.length < 1) {
            if(args[0] == null)
                System.out.println("Falta archivo de data estática");

            exit(1);
        }

        int t_arbitrario;
        int n = 0, colisionesxS, corridas = 0, maxCorridas = 20; double l = 0;
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

        ArrayList<String> csv_1 = new ArrayList<>();
        csv_1.add("Corrida,Unidad_Tiempo,Colisiones");
        writeFile(csv_1, "CSVG1.csv", false);
        csv_1.clear();

        ArrayList<String> csv_2 = new ArrayList<>();
        csv_2.add("Corrida,Tiempo_Entre_Choques");
        writeFile(csv_2, "CSVG2.csv", false);
        csv_2.clear();

        ArrayList<String> csv_3 = new ArrayList<>();
        csv_3.add("Corrida,Tiempo_Acum,Modulo_V");
        writeFile(csv_3, "CSVG3.csv", false);
        csv_3.clear();

        while(maxCorridas-- > 0){
            corridas++;
            colisionesxS=0;
            t_arbitrario = 1;

            ArrayList<Particula> particulas;

            GeneradorParticulas g = new GeneradorParticulas(n, l,  r, mass, vMax);

            do{
                particulas = g.generar(R, Mass,  V, X, Y);

                System.out.println(particulas);
            }while (particulas==null);

            //ArrayList<String> Sparticulas = g.toStringParticulas();

            Calculator calculador = new Calculator(l, particulas);

            //writeXYZ(Sparticulas, "output.xyz", false);

            double delta_t_acum = 0;
            Choque prox_choque;

            boolean grandeNoChocoPared = true;


            if(corridas==1) {
                for (Particula p : particulas)
                    csv_3.add(corridas + "," + delta_t_acum + "," + Math.sqrt((p.getVX() * p.getVX()) + (p.getVY() * p.getVY())));

                writeFile(csv_3, "CSVG3.csv", true);
                csv_3.clear();
            }

            while (grandeNoChocoPared){
                prox_choque = calculador.actualizacion();

                colisionesxS++;

                delta_t_acum = prox_choque.getTc() + delta_t_acum;

                if(prox_choque.getP1().getId() == 1 && prox_choque.getP2() == null)
                    grandeNoChocoPared = false;

                //System.out.println("TC: " + delta_t_acum);

                csv_2.add(corridas + "," + prox_choque.getTc());


                if(corridas == 1) {
                    for (Particula p : particulas)
                        csv_3.add(corridas + "," + delta_t_acum + "," + Math.sqrt((p.getVX() * p.getVX()) + (p.getVY() * p.getVY())));
                }

                if(delta_t_acum >= t_arbitrario) {
                    csv_1.add(corridas + "," + t_arbitrario + "," + colisionesxS);

                    //Sparticulas = calculador.toStringParticulas();
                    //TODO: tratar de generar todo el output antes y escrbir una sola vez al final
                    //writeXYZ(Sparticulas, "output.xyz", true);
                    t_arbitrario++;colisionesxS=0;
                }

                if(colisionesxS%10 == 0){
                    writeFile(csv_1, "CSVG1.csv", true);
                    csv_1.clear();

                    writeFile(csv_2, "CSVG2.csv", true);
                    csv_2.clear();

                    writeFile(csv_3, "CSVG3.csv", true);
                    csv_3.clear();
                }

            }

            System.out.println("Corrida: " + corridas);

            writeFile(csv_1, "CSVG1.csv", true);
            csv_1.clear();

            writeFile(csv_2, "CSVG2.csv", true);
            csv_2.clear();

            if(corridas==1) {
                writeFile(csv_3, "CSVG3.csv", true);
                csv_3.clear();
            }
        }
    }

    public static void writeXYZ(ArrayList<String> output, String fileName, boolean append) {
        output.add(0, String.valueOf(output.size() + 4));
        output.add(1, "");

        output.add(2, "0.0 6.0 0.1 0.0\n" +
                "6.0 6.0 0.1 0.0\n" +
                "6.0 0.0 0.1 0.0\n" +
                "0.0 0.0 0.1 0.0");

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
