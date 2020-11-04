import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Track {
    private List<Particle> particles = new ArrayList<Particle>();
    private double int_radius;
    private double ext_radius;
    private List<String> xyz = new ArrayList<String>();

    public Track() {
        int_radius = Constants.intTrackRadius;
        ext_radius = Constants.extTrackRadius;
        createParticles();
    }

    private void createParticles() {
        Random rand = new Random();
        int tries = Constants.maxTries;

        while (tries-- > 0) {
            double x = -ext_radius + 2 * ext_radius * rand.nextDouble();
            double y = -ext_radius + 2 * ext_radius * rand.nextDouble();
            double radius = Constants.minPartRadius + (Constants.maxPartRadius - Constants.minPartRadius) * rand.nextDouble();
            Particle particle = new Particle(radius, x, y, 0, 0);

            if (isValid(particle)) {
                particles.add(particle);
                tries = Constants.maxTries;
            }
        }
    }

    private boolean isValid(Particle particle) {
        if (particle.getDistance() - particle.getRadius() < int_radius || particle.getDistance() + particle.getRadius() > ext_radius)
            return false;

        for (Particle other : particles) {
            double distance = particle.getDistanceToParticle(other);

            if (distance < particle.getRadius() + other.getRadius()) return false;
        }

        return true;
    }

    public List<String> getOutput() {
        List<String> output = new ArrayList<String>();
        double angle = 0;

        for (Particle particle : particles) {
            output.add(particle.getX() + " " + particle.getY() + " " + particle.getRadius());
        }

        // Draw borders
        while (angle < 2 * Math.PI) {
            output.add(Math.cos(angle) * Constants.intTrackRadius + " " + Math.sin(angle) * Constants.intTrackRadius + " 0.05");
            output.add(Math.cos(angle) * Constants.extTrackRadius + " " + Math.sin(angle) * Constants.extTrackRadius + " 0.05");
            angle += 0.01;
        }

        output.add(0, String.valueOf(output.size()));
        output.add(1, "");

        return output;
    }

    public List<String> run() {
        double current_time = 0;

        while (current_time < Constants.final_t) {
            System.out.println("Current time: " + current_time);
            updateVelocities();
            updatePositions();
            //updateRadius();
            xyz.addAll(getOutput());
            current_time += Constants.delta_t;
        }

        return xyz;
    }

    private void updatePositions() {
        for (Particle particle : particles) {
            Coordinates new_position = particle.getPosition().sum(particle.getVelocity().multiply(Constants.delta_t));
            particle.setPosition(new_position);
        }
    }

    private void updateVelocities() {
        for (Particle particle : particles) {
            Coordinates new_velocity = particle.getTangentVector().multiply(Constants.maxSpeed);
            particle.setVelocity(new_velocity);
        }
    }
}
