import java.io.*;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.exit;
import static java.lang.System.out;

class Pair{
    public Double first;
    public Double second;

    Pair(Double first, Double second) {
        this.first = first;
        this.second = second;
    }
}

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
        ArrayList<Pair> curva = new ArrayList<>();

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

            String row;
            BufferedReader csvReader = new BufferedReader(new FileReader("CorreccionDCM.csv"));
            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                curva.add(new Pair(Double.valueOf(data[0]), Double.valueOf(data[11])));
            }
            csvReader.close();

        } catch (IOException e) {
            System.out.println("Ocurrió un error al leer el archivo de data estática" + ' ' + args[1] + '.');
            e.printStackTrace();
            exit(1);
        }

        double y;
        double error;
        ArrayList<String> sError = new ArrayList<>();
        sError.add("Pendiente,Error");
        for(double m = 0; m < 0.1; m += 0.001) {
            error = 0;
            for(Pair pair : curva) {
                y = m * pair.first + 1.5;
                error += Math.pow((pair.second - y), 2);
            }
            sError.add(m + "," + error);
        }


        writeFile(sError, "Error.csv", false);

        ArrayList<Particula> particulas;

        GeneradorParticulas g = new GeneradorParticulas(n, l,  r, mass, vMax);
        particulas = g.generar(R, Mass,  V, X, Y);
        ArrayList<String> Sparticulas = g.toStringParticulas();
        ArrayList<String> STrayPartGrande = new ArrayList<>();
        ArrayList<String> SDCM = new ArrayList<>();
        ArrayList<Pair> sPosicion = new ArrayList<>();

        Calculator calculador = new Calculator(l, particulas, g.getParticulaGrande());

        writeXYZ(Sparticulas, "output.xyz", false);

        double delta_t_acum = 0;
        Choque prox_choque;

        boolean grandeNoChocoPared = true;

        while (grandeNoChocoPared){
            prox_choque = calculador.actualizacion();

            delta_t_acum = prox_choque.getTc() + delta_t_acum;

            if(prox_choque.getP1().getId() == 1 && prox_choque.getP2() == null)
                grandeNoChocoPared = false;

            System.out.println("TC: " + delta_t_acum);

            STrayPartGrande.add(calculador.getPosicionParticulaGrande());

            if(delta_t_acum >= t_arbitrario || !grandeNoChocoPared) {
                Sparticulas = calculador.toStringParticulas();
                writeXYZ(Sparticulas, "output.xyz", true);

                calculador.addsPosicion();
                //SDCM.add(Math.floor(delta_t_acum) + ";" + calculador.getDCM(true) + ";" + calculador.getDCM(false));
                t_arbitrario++;
            }

        }
        System.out.println("Temperatura: " + calculador.getTemperatura() + " K");

        STrayPartGrande.add(0,"X;Y");
        writeFile(STrayPartGrande, "Trayectoria.csv", false);

        sPosicion = calculador.getsPosicion();
        int aux = sPosicion.size();
        for(int i = 0; i < aux / 2; i++) {
            sPosicion.remove(0);
        }

        calcularDCM(sPosicion);
    }

    private static void calcularDCM(ArrayList<Pair> sPosicion) {
        Pair posicionInicial = sPosicion.get(0);
        double dist_x, dist_y, DCM;
        ArrayList<String> sDCM = new ArrayList<>();
        sDCM.add("Tiempo,DCM");
        int tiempo = 1;
        for(Pair current: sPosicion) {
            dist_x = current.first - posicionInicial.first;
            dist_y = current.second - posicionInicial.second;
            DCM = dist_x * dist_x + dist_y * dist_y;
            sDCM.add(tiempo + "," + DCM);
            tiempo++;
        }
        writeFile(sDCM, "DCM1.csv", false);
    }

    public static void writeXYZ(ArrayList<String> output, String fileName, boolean append) {
        output.add(0, String.valueOf(output.size() + 4));
        output.add(1, "");

        output.add(2, "0.0 6.0 0.1 0 0\n" +
                "6.0 6.0 0.1 0 0\n" +
                "6.0 0.0 0.1 0 0\n" +
                "0.0 0.0 0.1 0 0");

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
            System.out.println("Ocurrio un error al querer crear/escribir el archivo " + fileName + '.');
            e.printStackTrace();
        }
    }
}
