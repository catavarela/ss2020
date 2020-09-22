import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private double l;

    private ArrayList<Particula> particulas;
    private ArrayList<String> stringPart;

    private double r;
    private double mass;
    private double vMax;
    private Particula particulaGrande;

    public GeneradorParticulas(int n, double l,  double r, double mass, double vMax){
        this.n = n;
        this.l = l;

        this.particulas = new ArrayList<>();
        stringPart = new ArrayList<>();

        this.r = r;
        this.mass = mass;
        this.vMax = vMax;
    }

    public ArrayList<Particula> generar(double R, double Mass,  double V, double X, double Y){
        Random rand = new Random();

        double x, y, vx, vy, v, teta;

        int id = 1;

        Particula p;

        agregarGrande(R, Mass, V, X, Y, id++);

        while(n > 0) {

            x = rand.nextDouble() * l;
            y = rand.nextDouble() * l;

            v = rand.nextDouble() * vMax;
            teta =  rand.nextDouble() * Math.PI;

            vx = Math.cos(teta) * v;

            vy = Math.sin(teta) * v;

            p = new Particula(id, x, y, r, mass, vx, vy);

            if(!tocaPared(p) && !seSuperpone(p)) {
                agregar(p);
                n--;
                id++;
            }
        }

        return particulas;
    }

    private boolean tocaPared(Particula p){
        double x1, x2, y1, y2;

        x1 = y1 = 0f;
        x2 = y2 = l;

        if(Double.compare(x2-p.getR()-p.getX(), 0f) < 0)
            return true;

        if(Double.compare(y2-p.getR()-p.getY(), 0f) < 0)
            return true;

        if(Double.compare(x1+p.getR()-p.getX(), 0f) > 0)
            return true;

        if(Double.compare(y1+p.getR()-p.getY(), 0f) > 0)
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

    private void agregarGrande(double R, double Mass,  double V, double X, double Y, int id){
        Particula p = new Particula(id, X, Y, R, Mass, V, V);
        agregar(p);
        particulaGrande = p;
    }

    private void agregar (Particula p){
        //TODO: chequear si es necesario devolver todas las propiedades
        stringPart.add(p.getX() + " " + p.getY() + " " + p.getR() + " " + p.getMass());
        particulas.add(p);
    }

    public ArrayList<String> toStringParticulas() {
        return stringPart;
    }

    public Particula getParticulaGrande() {
        return particulaGrande;
    }
}
