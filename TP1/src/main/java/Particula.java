public class Particula {

    private int id;
    private float x;
    private float y;
    private Particula next;

    public Particula(int id, float x, float y, Particula next) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.next = next;
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
}
