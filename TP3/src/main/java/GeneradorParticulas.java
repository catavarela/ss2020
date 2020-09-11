import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private float l;

    private ArrayList<Particula> particulas;
    private ArrayList<String> stringPart;

    private float r;
    private float mass;
    private float vMax;

    public GeneradorParticulas(int n, float l,  float r, float mass, float vMax){
        this.n = n;
        this.l = l;

        this.particulas = new ArrayList<>();
        stringPart = new ArrayList<>();

        this.r = r;
        this.mass = mass;
        this.vMax = vMax;
    }

    public ArrayList<Particula> generar(float R, float Mass,  float V, float X, float Y){
        Random rand = new Random();

        float x, y;

        int id = 1;

        Particula p;

        agregarGrande(R, Mass, V, X, Y, id++);

        while(n > 0) {

            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            p = new Particula(id, x, y, r, mass, rand.nextFloat() * vMax, rand.nextFloat() * vMax);

            if(!tocaPared(p) && !seSuperpone(p)) {
                agregar(p);
                n--;
                id++;
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

    private boolean seSuperpone(Particula p){

        for (Particula current : particulas){
            if(Double.compare(Math.pow(current.getX() - p.getX(), 2) + Math.pow(current.getY() - p.getY(), 2), Math.pow(current.getR() + p.getR(), 2)) <= 0)
                return true;
        }

        return false;
    }

    private void agregarGrande(float R, float Mass,  float V, float X, float Y, int id){
        Particula p = new Particula(id, X, Y, R, Mass, V, V);
        agregar(p);
    }

    private void agregar (Particula p){
        //TODO: chequear si es necesario devolver todas las propiedades
        stringPart.add(p.getX() + " " + p.getY() + " " + p.getR() + " " + p.getMass() + " " + p.getVX() + " " + p.getVY());
        particulas.add(p);
    }

    public ArrayList<String> toStringParticulas() {
        return stringPart;
    }
}
