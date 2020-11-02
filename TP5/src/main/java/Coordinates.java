public class Coordinates {
    double x;
    double y;

    public Coordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getLength() {
        return Math.hypot(x,y);
    }

    public Coordinates getDifference(Coordinates other) {
        double diffX = x - other.getX();
        double diffY = y - other.getY();

        return new Coordinates(diffX, diffY);
    }
}
