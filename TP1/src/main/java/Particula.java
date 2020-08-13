import java.util.ArrayList;


//se puede mejorar el algoritmo agregandole a particula f y c
public class Particula {

    private int id;
    private float x;
    private float y;
    private Particula next;
    private ArrayList<Particula> vecinos;
    private float r;

    public Particula(int id, float x, float y, Particula next, float r) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.next = next;
        vecinos = new ArrayList<>();
        this.r = r;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getId() {
        return id;
    }

    public float getR() {
        return r;
    }

    public Particula getNext() {
        return next;
    }

    public ArrayList<Particula> getVecinos() {return vecinos;}

    public void setNext(Particula next) {this.next = next;}
}
