import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
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
            if(args[0] == null)
                System.out.println("Falta archivo de data estática");

            if(args[1] == null)
                System.out.println("Falta especificar el número de partícula a estudiar");

            exit(1);
        }

        int n = 0, m = 0;
        float l = 0f, rc = 0f;
        boolean contorno = false;

        try {
            Scanner lector = new Scanner(new File(args[0]));

            n = Integer.valueOf(lector.nextLine());
            l = Float.valueOf(lector.nextLine());
            m = Integer.valueOf(lector.nextLine());
            rc = Float.valueOf(lector.nextLine());
            contorno = Boolean.valueOf(lector.nextLine());

            lector.close();

        } catch (FileNotFoundException e) {
            System.out.println("Ocurrió un error al leer el archivo de data estática" + ' ' + args[0] + '.');
            e.printStackTrace();
            exit(1);
        }

        if(m == 0)
            m = CalculadorVecinos.mCalculator(l, rc);

        CalculadorVecinos calculator;

        if(args.length == 1){
            GeneradorParticulas g = new GeneradorParticulas(n, l);
            ArrayList<String> particulas = g.generar();

            writeFile(particulas, "particulas.txt");

            calculator = new CalculadorVecinos(n, l, m, rc, contorno, particulas);
        }else
            calculator = new CalculadorVecinos(n, l, m, rc, contorno, args[1]);

        long t_inicio = System.nanoTime();

        ArrayList<String> vecinos = calculator.calcularVecinos();

        long t_final = System.nanoTime();

        vecinos.add(0, String.valueOf(((double)t_final - t_inicio)/1000000000));
        writeFile(vecinos, "vecinos.txt");
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
