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

        ArrayList<Particula> heads = leerParticulas();

        //VER SI CONVIENE METERLE A TODA LA MATRIZ NULLs (CREO QUE NO PORQUE IMPLICA RECORRERLA UNA VEZ ANTES DE PONERLE
        // LOS VALORES DE PARTICULAS) ---> INTENTAR AGREGAR EN LAS POSICIONES INDEX Y VER SI EL RESTO SE PONE NULL SOLO (?
        //CONCLUSION --> HAY QUE PONERLE NULL PORQUE SINO ME VA A TIRAR UNA EXCEPCION DE OUT OF BOUNDS

        /*
            ESTRUCTURA RECOMENDADA A USAR:
                ARRAY_HEAD --> TODAS LAS CELDAS CON UN REPRESENTANTE (HEAD)
                'ARRAY_LIST' --> LISTA DE PARTICULAS EN LA CELDA, ES UNA LISTA PROPIA, NO DEFINIDA COMO ARRAYLIST<ALGO>

            PARA HACER ESTO EN MI OPINION NECESITAMOS:
                CLASE PARTICULA (CON: ID, X, Y, NEXT_PART), YO NO PONDRÍA UNA LISTA DE HEADS_VECINOS PORQUE CREO QUE ESO
                NOS VA A LLEVAR A UN ALGORITMO MÁS FÁCIL PERO MÁS LENTO.

            PSEUDOCÓDIGO DEL ALGORITMO:

            DONE 1) CREAR LAS ESTRUCTURAS QUE NECESITO:

             if (file == null)
                LEER DEL ARCHIVO Y CREAR ARRAY_HEAD, ARRAY_LIST
             else
                LEER DE particulas Y CREAR ARRAY_HEAD, ARRAY_LIST

            2) RECORRER EN FORMA DE L PARA ENCONTRAR LAS CELDAS VECINAS Y VER SI ESTÁN DENTRO DEL RC PARA AGREGARLOS
               A VECINOS (TENIENDO EN CUENTA CONTORNO).

               AL MOMENTO DE VER LAS CANDIDATAS (LAS QUE ESTAN EN LA CELDA VECINA) CALCULAR LA HIPOTENUSA DE LA PART A
               LA CANDIDATA. SI LA HIPOTENUSA =< RC => ES VECINA => APPENDEAR AL STRING EN LA POSICION DE ID-1 (ID EMPIEZA EN 1)



         */




        return vecinos;
    }

    private ArrayList<Particula> leerParticulas(){
        ArrayList<Particula> heads = new ArrayList<Particula>(m*m);

        int i = m*m;

        while(i > 0)
            heads.add(i--, null);

        int id = 0;

        if (file != null){

            try {
                Scanner lector = new Scanner(new File(file));

                while(lector.hasNext())
                    agregarParticula(heads, lector.nextLine(), ++id);

                lector.close();

            } catch (FileNotFoundException e) {
                System.out.println("Ocurrió un error al leer el archivo de data dinámica" + ' ' + file + '.');
                e.printStackTrace();
                exit(1);
            }

        }else{
            Iterator<String> it = particulas.iterator();

            while(it.hasNext())
                agregarParticula(heads, it.next(), ++id);
        }

        return heads;
    }

    private void agregarParticula (ArrayList<Particula> heads, String particula, int id){
        String[] tokens = particula.split(" ");

        float x = Float.valueOf(tokens[0]);
        float y = Float.valueOf(tokens[1]);


        //Resto por m para "construir" la matriz de arriba para abajo, porque el origen de coordenadas (x,y)
        //está en la esquina izq abajo en la matriz

        int f = m - (int)Math.floor(((double)x)/(l/m));
        int c = m - (int)Math.floor(((double)y)/(l/m));

        int posicion = f*m + c;

        Particula head = heads.get(posicion);

        if(head == null)
            heads.add(posicion, new Particula(id, x, y, null));
        else
            heads.add(posicion, new Particula(id, x, y, head));
    }

}
