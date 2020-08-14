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

/*
        ARCHIVO INPUT (static_data.txt) CON FORMATO:

            N (NRO > 0)
            L (NRO > 0)
            M (NRO > 0, SI ES 0 SE CONSIDERA QUE SERÁ CALCULADO POR EL PROGRAMA)
            RC (NRO > 0)
            COND_CONTORNO (TRUE/FALSE)


        ARCHIVO INPUT (dynamic_data.txt) CON FORMATO:

            STRING CON POSICIONES X Y PARA PART 1
            STRING CON POSICIONES X Y PARA PART 2
            .
            .
            .
            STRING CON POSICIONES X Y PARA PART N

        --------------

        ARCHIVO OUTPUT (vecinos.txt) CON FORMATO:

            t TIEMPO DE EJECUCIÓN EN SEGUNDOS
            STRING (NROS SEPARADOS POR COMA) CON VECINOS PARA PART EN POSICIÓN 1
            STRING (NROS SEPARADOS POR COMA) CON VECINOS PARA PART EN POSICIÓN 2
            .
            .
            .
            STRING (NROS SEPARADOS POR COMA) CON VECINOS PARA PART EN POSICIÓN N

         --------------

            VISUALIZACIONES:
            NECESITAMOS 2 VISUALIZACIONES DISTINTAS:
                1) MUESTRA LAS PARTICULAS, LA PARTICULA QUE SEGUIMOS, LOS VECINOS
                   Y EL RC.
                2) GRÁFICO DE 4 LINEAS DE TENDENCIA CON Ns Y Ms DISTINTOS. ACORDARSE
                   DE PONER EL |--O--|.


            OPINIÓN:
            PARA EL EJ2, YO OPINO DE TENERLO PREPARADO DE ANTES EL GRÁFICO SINO VAMOS
            A TENER QUE CORRERLO MIL VECES DISTINTAS EN EL MOMENTO Y NO TIENE TANTO
            SENTIDO.

         */

public class Main {

    public static void main(String[] args) {

        if(args.length < 2) {
            if(args[1] == null)
                System.out.println("Falta archivo de data estática");

            if(args[0] == null)
                System.out.println("Falta especificar el número de partícula a estudiar");

            exit(1);
        }

        int n = 0, m = 0;
        float l = 0f, rc = 0f, rMax = 0f;
        boolean contorno = false, igualRadio = false;
        File src;

        try {
            Scanner lector = new Scanner(new File(args[1]));

            n = Integer.valueOf(lector.nextLine());
            l = Float.valueOf(lector.nextLine());
            m = Integer.valueOf(lector.nextLine());
            rc = Float.valueOf(lector.nextLine());
            contorno = Boolean.valueOf(lector.nextLine());
            rMax = Float.valueOf(lector.nextLine());
            igualRadio = Boolean.valueOf(lector.nextLine());

            lector.close();

        } catch (FileNotFoundException e) {
            System.out.println("Ocurrió un error al leer el archivo de data estática" + ' ' + args[1] + '.');
            e.printStackTrace();
            exit(1);
        }

        if(m == 0)
            m = CalculadorVecinos.mCalculator(l, rc, rMax);

        CalculadorVecinos calculator;
        Particula [][] heads = null;

        if(args.length == 2){
            GeneradorParticulas g = new GeneradorParticulas(n, l, rMax, m, contorno);
            heads = g.generar(igualRadio);

            ArrayList<String> particulas = g.toStringParticulas();

            writeFile(particulas, "particulas.txt");
            calculator = new CalculadorVecinos(n, l, m, rc, contorno, g.getParticulas());
            src = new File("particulas.txt");

        }else {
            src = new File(args[2]);
            calculator = new CalculadorVecinos(n, l, m, rc, contorno, args[2]);
        }

        ArrayList<String> vecinos = calculator.calcularVecinos(heads);

        writeFile(vecinos, "vecinos.txt");

        long t_inicio = System.nanoTime();
        calculator.fuerzaBruta();
        long t_final = System.nanoTime();
        System.out.println("Cálculo mediante fuerza bruta: " + ((double) t_final - t_inicio) / 1000000000);

        try {
            File dst = new File("output.xyz");

            int particula_a_estudiar = Integer.parseInt(args[0]);

            String v = vecinos.get(particula_a_estudiar);
            String[] tokens = v.split(", ");
            int index = 0;

            List<String> lineas = Files.readAllLines(Paths.get(src.getName()));

            while(index < lineas.size()) {
                lineas.set(index, lineas.get(index++) + " 255 255 255");
            }

            String replace = lineas.get(particula_a_estudiar - 1);
            replace = replace.substring(0, replace.length() - 11) + "255 0 0";
            lineas.set(particula_a_estudiar - 1,  replace);

            index = 1;
            while(index < tokens.length) {
                int i = Integer.parseInt(tokens[index]);
                replace = lineas.get(i - 1);
                replace = replace.substring(0, replace.length() - 11) + "0 255 0";
                lineas.set(i - 1, replace);
                index++;
            }

            lineas.add(0, String.valueOf(lineas.size()));
            lineas.add(1, "");

            Files.write(Paths.get(dst.getName()), lineas);
        } catch (IOException e) {
            System.out.println("Ocurrió un error al clonar el archivo de particulas" + ' ' + src.getName() + '.');
            e.printStackTrace();
            exit(1);
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
