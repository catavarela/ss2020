public class Particle {
    private double radius;
    private Coordinates position;
    private Coordinates velocity;

    public Particle(double radius, double initial_x, double initial_y, double initial_vx, double initial_vy){
        this.radius = radius;
        position = new Coordinates(initial_x, initial_y);
        velocity = new Coordinates(initial_vx, initial_vy);
    }

    public double getRadius() {
        return radius;
    }

    public Coordinates getPosition() {
        return position;
    }

    public Coordinates getVelocity() {
        return velocity;
    }

    public double getDistance() {
        return position.getLength();
    }

    public double getDistanceToParticle(Particle other) {
        return position.substract(other.getPosition()).getLength();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }

    public Coordinates getTangentVector() {
        return position.getTangentVector();
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setPosition(Coordinates position) {
        this.position = position;
    }

    public void setVelocity(Coordinates velocity) {
        this.velocity = velocity;
    }
}


