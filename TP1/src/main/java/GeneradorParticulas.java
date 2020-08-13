import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private float l;
    private ArrayList<Particula> particulas;
    private ArrayList<String> stringPart;
    private float rMax;
    private int m;
    private boolean contorno;

    public GeneradorParticulas(int n, float l, float rMax, int m, boolean contorno){
        this.n = n;
        this.l = l;
        this.particulas = new ArrayList<>();
        this.rMax = rMax;
        this.m = m;
        this.contorno = contorno;
        stringPart = new ArrayList<>();
    }

    public Particula[][] generar(boolean igualRadio){
        if(Float.compare(rMax, 0f) == 0) {
            generarPuntuales();

            return null;
        }

        return generarConRadio(igualRadio);

    }

    private void generarPuntuales(){
        Random rand = new Random();

        float x = 0f;
        float y = 0f;

        int id = 1;

        while(n > 0) {
            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            stringPart.add(String.valueOf(x) + ' ' + String.valueOf(y) + " 0");
            particulas.add(new Particula(id++, x, y, null, 0));

            n--;
        }
    }

    //chequear casos borde donde se genera la particula sobre un borde de la celda
    public Particula[][] generarConRadio(boolean igualRadio){
        Random rand = new Random();

        Particula[][] heads = new Particula[m][m];

        float x, y, r = rMax;

        int id = 1, f, c;

        boolean esValido = true;
        Particula current;
        Particula p;

        while(n > 0) {

            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            f = CalculadorVecinos.calcFil(y, l, m);
            c = CalculadorVecinos.calcCol(x, l, m);


            //if (f1 < f2) return -1;
            //-----------------------------
            // c-1   c  |c-1    c   |         c+1
            //   |      |    |      |  |   |
            //  *.*     |    .**    |  | **.
            //   |      |    |      |  |   |


            //si estoy parada en un borde, busco de nuevo
            if(Float.compare(x, 0) == 0 || Float.compare(y, 0) == 0 || Float.compare(x, l) == 0 || Float.compare(y, l) == 0)
                continue;

            // si el diametro de la particula toca una celda, volver al while
            if(Float.compare(x, c*(l/m)) == 0 || Float.compare(y, (m-f-1)*(l/m)) == 0)
                continue;

            if(!igualRadio){
                do {
                    r = rand.nextFloat() * rMax;
                }while (Float.compare(r, 0f) == 0);
            }

            //1*2
            //-----------------------------
            //          |
            //f    *    | --.--   *
            //   --.--  |   *     *
            //f-1  *    |   *   --.--

            if(Float.compare(((c+1)*(l/m))-x, r) < 0 || Float.compare(x-(c*(l/m)), r) < 0 || Float.compare(((m-f)*(l/m))-y, r) < 0 || Float.compare(y-((m-f-1)*(l/m)), r) < 0)
                continue;


            p = new Particula(id, x, y, null, r);

            if(heads[f][c] == null) {
                stringPart.add(String.valueOf(x) + ' ' + String.valueOf(y) + ' ' + r);
                CalculadorVecinos.agregarParticula(heads, p, l, m);
                particulas.add(p);
                id++;
                n--;
            }else{

                current = heads[f][c];

                do{
                    if(CalculadorVecinos.estaEnRango(current, p, p.getR() + current.getR(), l, m, contorno))
                        esValido = false;
                    else
                        current = current.getNext();

                }while (esValido && current != null);

                if(esValido){
                    stringPart.add(String.valueOf(x) + ' ' + String.valueOf(y) + ' ' + r);
                    CalculadorVecinos.agregarParticula(heads, p, l, m);
                    particulas.add(p);
                    n--;
                    id++;
                }
            }
        }

        return heads;
    }

    public ArrayList<Particula> getParticulas() {
        return particulas;
    }

    public ArrayList<String> toStringParticulas() {
        return stringPart;
    }
}
