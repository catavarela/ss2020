public class Particle {
    private int id;
    private double radius;
    private Coordinate position;
    private Coordinate velocity;

    public Particle(int id, double radius, double initial_x, double initial_y, double initial_vx, double initial_vy) {
        this.id = id;
        this.radius = radius;
        position = new Coordinate(initial_x, initial_y);
        velocity = new Coordinate(initial_vx, initial_vy);
    }

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public Coordinate getPosition() {
        return position;
    }

    public void setPosition(Coordinate position) {
        this.position = position;
    }

    public Coordinate getVelocity() {
        return velocity;
    }

    public void setVelocity(Coordinate velocity) {
        this.velocity = velocity;
    }

    public double getDistance() {
        return position.getLength();
    }

    public double getDistanceToParticle(Particle other) {
        return position.subtract(other.getPosition()).getLength();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public double getVX() {
        return velocity.getX();
    }

    public double getVY() {
        return velocity.getY();
    }

    public Coordinate getTangentVector() {
        return position.getTangentVector();
    }

    public Coordinate getNormalVector(Coordinate other) {
        return position.getNormalVector(other);
    }
}


