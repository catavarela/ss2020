public class Coordinate {
    private double x;
    private double y;

    public Coordinate(double x, double y) {
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
        return Math.hypot(x, y);
    }

    public Coordinate subtract(Coordinate other) {
        double diffX = x - other.getX();
        double diffY = y - other.getY();

        return new Coordinate(diffX, diffY);
    }

    public Coordinate sum(Coordinate other) {
        double sumX = x + other.getX();
        double sumY = y + other.getY();

        return new Coordinate(sumX, sumY);
    }

    public Coordinate multiply(double scalar) {
        double prodX = x * scalar;
        double prodY = y * scalar;

        return new Coordinate(prodX, prodY);
    }

    public Coordinate divide(double scalar) {
        double divX = x / scalar;
        double divY = y / scalar;

        return new Coordinate(divX, divY);
    }

    public Coordinate getTangentVector() {
        double tanX = x * Math.cos(Constants.ninetyDegrees) - y * Math.sin(Constants.ninetyDegrees);
        double tanY = x * Math.sin(Constants.ninetyDegrees) + y * Math.cos(Constants.ninetyDegrees);

        Coordinate tangent = new Coordinate(tanX, tanY);

        return tangent.divide(tangent.getLength());
    }

    public Coordinate getNormalVector(Coordinate other) {
        Coordinate normal = subtract(other);

        return normal.divide(normal.getLength());
    }

    public Coordinate getOpposite() {
        return new Coordinate(-x, -y);
    }
}
