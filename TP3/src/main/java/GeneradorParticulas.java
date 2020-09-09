import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private float l;
    private int m;

    private ArrayList<Particula> particulas;
    private ArrayList<String> stringPart;

    private float r;
    private float mass;
    private float vMax;

    public GeneradorParticulas(int n, float l,  float r, int m, float mass, float vMax){
        this.n = n;
        this.l = l;
        this.m = m;

        this.particulas = new ArrayList<>();
        stringPart = new ArrayList<>();

        this.r = r;
        this.mass = mass;
        this.vMax = vMax;
    }

    public ArrayList<Particula> generar(float R, float Mass,  float V, float X, float Y){
        Random rand = new Random();

        Particula[][] heads = new Particula[m][m];

        float x, y;

        int id = 1, f, c;

        boolean esValido = true;
        Particula current;
        Particula p;

        agregarGrande(R, Mass, V, X, Y, heads, id++);

        while(n > 0) {
            System.out.println("falta por crear: " + n);
            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            f = Calculator.calcFil(y, l, m);
            c = Calculator.calcCol(x, l, m);

            p = new Particula(id, x, y, null, r, mass, rand.nextFloat() * vMax, rand.nextFloat() * vMax);

            if(!tocaPared(p)) {

                if (heads[f][c] == null) {
                    agregar(heads, p);
                    id++;
                    n--;
                } else {

                    current = heads[f][c];

                    do {
                        if (seSuperponen(current, p))
                            esValido = false;
                        else
                            current = current.getNext();

                    } while (esValido && current != null);

                    if (esValido) {
                        agregar(heads, p);
                        n--;
                        id++;
                    }
                }
            }
        }

        return particulas;
    }

    private boolean tocaPared(Particula p){
        float x1, x2, y1, y2;

        x1 = y1 = 0f;
        x2 = y2 = l;

        if(Float.compare(x2-p.getR()-p.getX(), 0f) < 0)
            return true;

        if(Float.compare(y2-p.getR()-p.getY(), 0f) < 0)
            return true;

        if(Float.compare(x1+p.getR()-p.getX(), 0f) > 0)
            return true;

        if(Float.compare(y1+p.getR()-p.getY(), 0f) > 0)
            return true;

        return false;
    }

    private boolean seSuperponen(Particula current, Particula p){
        if(Double.compare(Math.pow(current.getX() - p.getX(), 2) + Math.pow(current.getY() - p.getY(), 2), Math.pow(current.getR() - p.getR(), 2)) > 0)
            return false;

        return true;
    }

    private void agregarGrande(float R, float Mass,  float V, float X, float Y, Particula[][] heads, int id){
        Particula p = new Particula(id, X, Y, null, R, Mass, V, V);
        agregar(heads, p);
    }

    private void agregar (Particula[][] heads, Particula p){
        //TODO: chequear si es necesario devolver todas las propiedades
        stringPart.add(p.getX() + " " + p.getY() + " " + p.getR() + " " + p.getMass() + " " + p.getVX() + " " + p.getVY());
        Calculator.agregarParticula(heads, p, l, m);
        particulas.add(p);
    }

    public ArrayList<String> toStringParticulas() {
        return stringPart;
    }
}
