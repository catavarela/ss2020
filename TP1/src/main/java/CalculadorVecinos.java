import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.exit;

public class CalculadorVecinos {
    private int n;
    private float l;
    private int m;
    private float rc;
    private boolean contorno;
    private String file = null;
    private ArrayList<String> particulas = null;


    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, String fileParticulas){
        this.n = n;
        this.l = l;
        this.rc = rc;
        this.contorno = contorno;

        file = fileParticulas;
    }

    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, ArrayList<String> particulas){
        this.n = n;
        this.l = l;
        this.rc = rc;
        this.contorno = contorno;

        this.particulas = particulas;
    }

    public static int mCalculator (float l, float rc){
        int m = 1;

        while (Float.compare(l/m, rc) > 0)
            m++;

        if(Float.compare(l/m, rc) == 0)
            m--;

        return m;
    }

    public ArrayList<String> calcularVecinos(){
        ArrayList<String> vecinos = new ArrayList<String>(n+1);

        ArrayList<String> heads = leerParticulas();

        //VER SI CONVIENE METERLE A TODA LA MATRIZ NULLs (CREO QUE NO PORQUE IMPLICA RECORRERLA UNA VEZ ANTES DE PONERLE
        // LOS VALORES DE PARTICULAS) ---> INTENTAR AGREGAR EN LAS POSICIONES INDEX Y VER SI EL RESTO SE PONE NULL SOLO (?

        /*
            ESTRUCTURA RECOMENDADA A USAR:
                ARRAY_HEAD --> TODAS LAS CELDAS CON UN REPRESENTANTE (HEAD)
                'ARRAY_LIST' --> LISTA DE PARTICULAS EN LA CELDA, ES UNA LISTA PROPIA, NO DEFINIDA COMO ARRAYLIST<ALGO>

            PARA HACER ESTO EN MI OPINION NECESITAMOS:
                CLASE PARTICULA (CON: ID, X, Y, NEXT_PART), YO NO PONDRÍA UNA LISTA DE HEADS_VECINOS PORQUE CREO QUE ESO
                NOS VA A LLEVAR A UN ALGORITMO MÁS FÁCIL PERO MÁS LENTO.

            PSEUDOCÓDIGO DEL ALGORITMO:

            ISH-DONE 1) CREAR LAS ESTRUCTURAS QUE NECESITO:

             if (file == null)
                LEER DEL ARCHIVO Y CREAR ARRAY_HEAD, ARRAY_LIST
             else
                LEER DE particulas Y CREAR ARRAY_HEAD, ARRAY_LIST

            2) RECORRER EN FORMA DE L PARA ENCONTRAR LAS CELDAS VECINAS Y VER SI ESTÁN DENTRO DEL RC PARA AGREGARLOS
               A VECINOS (TENIENDO EN CUENTA CONTORNO):



         */




        return vecinos;
    }

    private ArrayList<String> leerParticulas(){
        ArrayList<String> heads = new ArrayList<String>(m*m);

        if (file != null){

            try {
                Scanner lector = new Scanner(new File(file));

                while(lector.hasNext())
                    agregarParticula(heads, lector.nextLine());

                lector.close();

            } catch (FileNotFoundException e) {
                System.out.println("Ocurrió un error al leer el archivo de data dinámica" + ' ' + file + '.');
                e.printStackTrace();
                exit(1);
            }

        }else{
            Iterator<String> it = particulas.iterator();

            while(it.hasNext())
                agregarParticula(heads, it.next());
        }

        return heads;
    }

    private void agregarParticula (ArrayList<String> heads, String particula){
        //HACER CALCULOS PARA VER EN QUÉ CELDA CAE

        //AGARRAR EL HEAD Y PONERLO COMO TAIL DEL NUEVO HEAD (ACTUAL PARTICULA)
    }

}
