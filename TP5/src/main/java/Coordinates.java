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

    public Coordinates substract(Coordinates other) {
        double diffX = x - other.getX();
        double diffY = y - other.getY();

        return new Coordinates(diffX, diffY);
    }

    public Coordinates sum(Coordinates other) {
        double sumX = x + other.getX();
        double sumY = y + other.getY();

        return new Coordinates(sumX, sumY);
    }

    public Coordinates multiply(double scalar) {
        double prodX = x * scalar;
        double prodY = y * scalar;

        return new Coordinates(prodX, prodY);
    }

    public Coordinates divide(double scalar) {
        double divX = x / scalar;
        double divY = y / scalar;

        return new Coordinates(divX, divY);
    }

    public Coordinates getTangentVector() {
        double tanX = x * Math.cos(Constants.ninetyDegrees) - y * Math.sin(Constants.ninetyDegrees);
        double tanY = x * Math.sin(Constants.ninetyDegrees) + y * Math.cos(Constants.ninetyDegrees);

        Coordinates tangent = new Coordinates(tanX, tanY);

        double unitX = tanX / tangent.getLength();
        double unitY = tanY / tangent.getLength();

        return new Coordinates(unitX, unitY);
    }


}
