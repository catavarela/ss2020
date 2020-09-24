import java.util.ArrayList;


public class Particula {

    private int id;
    private double x;
    private double y;
    private double vx;
    private double vy;
    private double r;
    private double mass;

    public Particula(int id, double x, double y, double r, double mass, double vx, double vy) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.r = r;
        this.mass = mass;
        this.vx = vx;
        this.vy = vy;
    }

    public int getId() { return id; }
    public double getR() { return r; }
    public double getMass() { return mass; }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getVX() {
        return vx;
    }
    public double getVY() {
        return vy;
    }
    public void setVX(double vx) {this.vx = vx;}
    public void setVY(double vy) {this.vy = vy;}
    public String getColor() {return id == 1? "1 0" : "0 1";}


    public void integrate (double tc) {
        this.x += this.vx * tc;
        this.y += this.vy * tc;
    }
}
