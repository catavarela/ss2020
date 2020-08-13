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
    private ArrayList<Particula> lista;


    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, String fileParticulas){
        this.n = n;
        this.l = l;
        this.rc = rc;
        this.contorno = contorno;
        file = fileParticulas;
        lista = new ArrayList<Particula>();
    }

    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, ArrayList<Particula> particulas){
        this.n = n;
        this.l = l;
        this.m = m;
        this.rc = rc;
        this.contorno = contorno;
        this.lista = particulas;
    }

    public static int mCalculator (float l, float rc){
        int m = 1;

        while (Float.compare(l/m, rc) > 0)
            m++;

        if(Float.compare(l/m, rc) == 0)
            m--;

        return m;
    }

    //si matrix es null, no se generó de antes la matrix
    public ArrayList<String> calcularVecinos(Particula[][] matrix){
        ArrayList<String> vecinos = new ArrayList<String>(n+1);
        Particula[][] heads;

        if(matrix == null)
            heads = leerParticulas();
        else
            heads = matrix;

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
          //  System.out.println("segundo while");
            while(vecinos_it.hasNext())
                s = s + ", " + vecinos_it.next().getId();

            //System.out.println("sali segundo while");
            vecinos.add(s);
        }

        //System.out.println("sali primer while");

        return vecinos;
    }

    private Particula[][] leerParticulas(){

        Particula[][]  heads = new Particula[m][m];

        if (file != null){

            int id = 1;

            try {
                Scanner lector = new Scanner(new File(file));

                while(lector.hasNext())
                    agregarParticula(heads, lector.nextLine(), id++);

                lector.close();

            } catch (FileNotFoundException e) {
                System.out.println("Ocurrió un error al leer el archivo de data dinámica" + ' ' + file + '.');
                e.printStackTrace();
                exit(1);
            }

        }else{ //entra cuando es puntual
            Iterator<Particula> it = lista.iterator();

            while(it.hasNext())
                agregarParticula(heads, it.next(), l, m);
        }

        return heads;
    }

    protected static void agregarParticula (Particula[][] heads, Particula particula, float l, int m){
        float x = particula.getX();
        float y = particula.getY();

        int f = calcFil(y, l, m);
        int c = calcCol(x, l, m);

        Particula head = heads[f][c];

        if(head == null) {
            heads[f][c] = particula;
        }
        else {
            heads[f][c] = particula;
            particula.setNext(head);
        }
    }

    protected static int calcFil(float y, float l, int m){
        return y == l ? 0 : m - (int)Math.floor(((double)y)/(l/m)) - 1;
    }

    protected static int calcCol(float x, float l, int m){
        return x == l ? m - 1 : (int)Math.floor(((double)x)/(l/m));
    }

    private void agregarParticula(Particula[][] heads, String particula, int id) {
        String[] tokens = particula.split(" ");

        float x = Float.valueOf(tokens[0]);
        float y = Float.valueOf(tokens[1]);
        float r = Float.valueOf(tokens[2]);

        int f = calcFil(y, l, m);
        int c = calcCol(x, l, m);

        Particula head = heads[f][c];

        if(head == null) {
            heads[f][c] = new Particula(id, x, y, null, r);
            lista.add(heads[f][c]);
        }
        else {
            heads[f][c] = new Particula(id, x, y, head, r);
            lista.add(heads[f][c]);
        }
    }

    private void chequearVecinos(Particula current, Particula vecino) {
        Particula potencial = vecino;

        do {

            do {
                if(estaEnRango(current, potencial, rc+current.getR()-potencial.getR(), l, m, contorno)) {
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

    protected static boolean estaEnRango(Particula current, Particula potencial, float distancia, float l, int m, boolean contorno){
        float dist_x = 0f, dist_y = 0f, x_c, x_p, y_c, y_p;
        double dist;
        int f_c, c_c, f_p, c_p;

        x_c = current.getX();
        x_p = potencial.getX();
        y_c = current.getY();
        y_p = potencial.getY();
        f_c = calcFil(y_c, l, m);
        c_c = calcCol(x_c, l, m);
        f_p = calcFil(y_p, l, m);
        c_p = calcCol(x_p, l, m);

        if(!contorno || (f_c == f_p && c_c == c_p) || (f_c != 0 && f_c != m-1 && c_c!= 0 && c_c!= m-1)) { //uso contorno como condicion de corte temprana
            //no estoy en un borde o lo estoy pero ambas estan en la misma celda
            dist_x = Math.abs(x_c - x_p);
            dist_y = Math.abs(y_c - y_p);
        }else{
            //estoy en un borde
            if(f_c == f_p && (c_c == 0 && c_p ==m-1)) {
                dist_x = (l - x_p) + x_c;
                dist_y = Math.abs(y_c - y_p);
            }else if(f_c == f_p && (c_p == 0 && c_c ==m-1)){
                dist_x = (l-x_c)+x_p;
                dist_y = Math.abs(y_c-y_p);
            }else if(c_c == c_p && (f_c == 0 && f_p ==m-1)){
                dist_x = Math.abs(x_c-x_p);
                dist_y = y_p+ (l-y_c);
            }else if(c_c == c_p && (f_c == m-1 && f_p ==0)) {
                dist_x = Math.abs(x_c-x_p);
                dist_y = y_c+ (l-y_p);
            } else if(c_c == 0 && c_p == m-1 && f_c==0 && f_p==m-1) {//estoy en una esquina//la part de la que estoy buscando vecinos está en esq izq arriba
                dist_x = (l - x_p) + x_c;
                dist_y = (l + y_p) - y_c;
            }else if(c_p == 0 && c_c == m-1 && f_p==0 && f_c==m-1){//la part de la que estoy buscando vecinos está en esq der abajo
                dist_x = (l-x_c)+x_p;
                dist_y = (l+y_c)-y_p;
            }else if(c_p == 0 && c_c == m-1 && f_p==m-1 && f_c==0){//la part de la que estoy buscando vecinos está en esq der arriba
                dist_x = (l-x_c)+x_p;
                dist_y = (l-y_c)+y_p;
            }else{//la part de la que estoy buscando vecinos está en esq izq abajo if(c1 == 0 && c2 == m-1 && f1==m-1 && f2==0)
                dist_x = (l-x_p)+x_c;
                dist_y = (l-y_p)+y_c;
            }
        }

        dist = Math.hypot(dist_x, dist_y);
        if(dist <= distancia)
            return true;
        else
            return false;
    }


}
