import java.util.ArrayList;


//se puede mejorar el algoritmo agregandole a particula f y c
public class Particula {

    private int id;
    private float x;
    private float y;
    private float vx;
    private float vy;
    private float r;
    private float mass;

    private Particula next;
    private ArrayList<Particula> vecinos;

    public Particula(int id, float x, float y, Particula next, float r, float mass, float vx, float vy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.next = next;
        vecinos = new ArrayList<>();
        this.r = r;
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() { return id; }
    public float getR() { return r; }
    public float getMass() { return mass; }

    public float getX() {
        return x;
    }
    public float getY() {
        return y;
    }
    public float getVX() {
        return vx;
    }
    public float getVY() {
        return vy;
    }
    public void setX(float x) {this.x = x;}
    public void setY(float y) {this.y = y;}
    public void setVX(float vx) {this.vx = vx;}
    public void setVY(float vy) {this.vy = vy;}


    public Particula getNext() {
        return next;
    }

    public ArrayList<Particula> getVecinos() {return vecinos;}

    public void setNext(Particula next) {this.next = next;}
}
