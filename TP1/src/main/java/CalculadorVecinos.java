import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import static java.lang.System.exit;

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

public class CalculadorVecinos {
    private int n;
    private float l;
    private int m;
    private float rc;
    private boolean contorno;
    private String file = null;
    private ArrayList<String> particulas = null;
    private ArrayList<Particula> lista = new ArrayList<Particula>();


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
        this.m = m;
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

        Particula[][] heads = leerParticulas();

        int f, c;
        for(f = 0; f < m; f++) {
            for(c = 0; c < m; c++) {
                Particula current = heads[f][c];
                if(current != null) {
                    //chequeo arriba
                    if((f - 1 >= 0) && (heads[f - 1][c] != null)) {
                        chequearVecinos(current, heads[f - 1][c]);
                    } else if((f - 1 < 0) && contorno && (heads[m - 1][c] != null)) {
                        chequearVecinos(current, heads[m - 1][c]);
                    }

                    //chequeo esquina superior derecha
                    if((f - 1 >= 0) && (c + 1 <= m - 1) && (heads[f - 1][c + 1] != null)) {
                        chequearVecinos(current, heads[f - 1][c + 1]);
                    } else if((f - 1 < 0) && (c + 1 > m - 1) && contorno && (heads[m - 1][0] != null)) {
                        chequearVecinos(current, heads[m - 1][0]);
                    }

                    //chequeo derecha
                    if((c + 1 <= m - 1) && (heads[f][c + 1] != null)) {
                        chequearVecinos(current, heads[f][c + 1]);
                    } else if((c + 1 > m - 1) && contorno && (heads[f][0] != null)) {
                        chequearVecinos(current, heads[f][0]);
                    }

                    //chequeo esquina inferior derecha
                    if((f + 1 <= m - 1) && (c + 1 <= m - 1) && (heads[f + 1][c + 1] != null)) {
                        chequearVecinos(current, heads[f + 1][c + 1]);
                    } else if((f + 1 > m - 1) && (c + 1 > m - 1) && contorno && (heads[0][0] != null)) {
                        chequearVecinos(current, heads[0][0]);
                    }

                    if (current.getNext() != null) {
                        chequearVecinos(current, current.getNext());
                    }
                }
            }
        }

        Iterator<Particula> it = lista.iterator();

        while(it.hasNext()) {
            Particula current = it.next();
            String s = String.valueOf(current.getId());

            Iterator<Particula> vecinos_it = current.getVecinos().iterator();

            while(vecinos_it.hasNext())
                s = s + ", " + vecinos_it.next().getId();

            vecinos.add(s);
        }

        return vecinos;
    }

    private Particula[][] leerParticulas(){
        Particula[][]  heads = new Particula[m][m];

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

    private void agregarParticula (Particula[][] heads, String particula, int id){
        String[] tokens = particula.split(" ");

        float x = Float.valueOf(tokens[0]);
        float y = Float.valueOf(tokens[1]);

        int f = y == l ? 0 : m - (int)Math.floor(((double)y)/(l/m)) - 1;
        int c = x == l ? m - 1 : (int)Math.floor(((double)x)/(l/m));

        Particula head = heads[f][c];

        if(head == null) {
            heads[f][c] = new Particula(id, x, y, null);
            lista.add(heads[f][c]);
        }
        else {
            heads[f][c] = new Particula(id, x, y, head);
            lista.add(heads[f][c]);
        }
    }

    private void chequearVecinos(Particula current, Particula vecino) {
        Particula potencial = vecino;

        do {
            do {

                float dist_x = Math.abs(current.getX() - potencial.getX());
                float dist_y = Math.abs(current.getY() - potencial.getY());
                double dist = Math.hypot(dist_x, dist_y);

                if(dist < rc) {
                    current.getVecinos().add(potencial);
                    potencial.getVecinos().add(current);
                }
                potencial = potencial.getNext();
            } while(potencial != null);

            current = current.getNext();
            potencial = vecino;

            if(current != null && current.getId() == potencial.getId()) {
                potencial = vecino.getNext();
                vecino = potencial;
            }
        } while(current != null && potencial != null);
    }

}
