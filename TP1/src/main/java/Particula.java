import java.util.ArrayList;

public class Particula {

    private int id;
    private float x;
    private float y;
    private Particula next;
    private ArrayList<Particula> vecinos;

    public Particula(int id, float x, float y, Particula next) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.next = next;
        vecinos = new ArrayList<Particula>();
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

    public Particula getNext() {
        return next;
    }

    public ArrayList<Particula> getVecinos() {return vecinos;}

    public void addVecino(Particula v) { vecinos.add(v);}
}
