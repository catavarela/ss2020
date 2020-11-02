import java.util.HashMap;
import java.util.Map;

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

    public double getDistance() {
        return position.getLength();
    }

    public double getDistanceToParticle(Particle other) {
        return position.getDifference(other.getPosition()).getLength();
    }

    public double getX() {
        return position.getX();
    }

    public double getY() {
        return position.getY();
    }
}


