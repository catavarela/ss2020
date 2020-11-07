import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Track {
    private List<Particle> particles = new ArrayList<Particle>();
    private double int_radius;
    private double ext_radius;
    private int quantity;

    private List<String> xyz = new ArrayList<String>();

    public Track() {
        int_radius = Constants.intTrackRadius;
        ext_radius = Constants.extTrackRadius;
        quantity = Constants.quantity;
        createParticles();
    }

    private void createParticles() {
        Random rand = new Random();
        int id = 1;
        int tries = Constants.maxTries;

        while (quantity != 0 && tries-- > 0) {
            double x = -ext_radius + 2 * ext_radius * rand.nextDouble();
            double y = -ext_radius + 2 * ext_radius * rand.nextDouble();
            double radius = Constants.minPartRadius + (Constants.maxPartRadius - Constants.minPartRadius) * rand.nextDouble();

            Particle particle = new Particle(id, radius, x, y, 0, 0);

            if (isValid(particle)) {
                particles.add(particle);
                id++;
                tries = Constants.maxTries;

                quantity--;

            }
        }
    }

    private boolean isValid(Particle particle) {
        if (particle.getDistance() - particle.getRadius() < int_radius || particle.getDistance() + particle.getRadius() > ext_radius)
            return false;

        for (Particle other : particles) {
            if (particle.getId() != other.getId()) {
                double distance = particle.getDistanceToParticle(other);

                if (distance < particle.getRadius() + other.getRadius()) return false;
            }
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
            updateVelocityAndRadius();
            updatePositions();
            xyz.addAll(getOutput());
            current_time += Constants.delta_t;
        }

        return xyz;
    }

    private void updatePositions() {
        for (Particle particle : particles) {
            Coordinate new_position = particle.getPosition().sum(particle.getVelocity().multiply(Constants.delta_t));
            particle.setPosition(new_position);
        }
    }

    private void updateVelocityAndRadius() {
        List<Particle> new_list = new ArrayList<Particle>();

        for (Particle particle : particles) {
            Coordinate new_velocity;
            double new_radius;

            if (isValid(particle)) {
                new_velocity = calculateDesiredVelocity(particle);
                new_radius = calculateRadius(particle);
            } else {
                new_velocity = calculateEscapeVelocity(particle);
                new_radius = Constants.minPartRadius;
            }

            Particle new_particle = new Particle(particle.getId(), new_radius, particle.getX(), particle.getY(), new_velocity.getX(), new_velocity.getY());
            new_list.add(new_particle);
        }

        particles = new_list;
    }

    private Coordinate calculateDesiredVelocity(Particle particle) {
        double desired_velocity = Constants.maxSpeed * Math.pow((particle.getRadius() - Constants.minPartRadius) / (Constants.maxPartRadius - Constants.minPartRadius), Constants.beta);

        return particle.getTangentVector().multiply(desired_velocity);
    }

    private Coordinate calculateEscapeVelocity(Particle particle) {
        Coordinate escape_velocity = new Coordinate(0, 0);

        for (Particle other : particles) {
            if (particle.getId() != other.getId()) {
                double distance = particle.getDistanceToParticle(other);

                if (distance < particle.getRadius() + other.getRadius()) {
                    escape_velocity = escape_velocity.sum(particle.getNormalVector(other.getPosition()));
                }
            }
        }

        if (particle.getDistance() - particle.getRadius() <= int_radius) {
            escape_velocity = escape_velocity.sum(particle.getPosition().divide(particle.getDistance()));
        }

        if (particle.getDistance() + particle.getRadius() >= ext_radius) {
            escape_velocity = escape_velocity.sum((particle.getPosition().divide(particle.getDistance())).getOpposite());
        }

        escape_velocity = escape_velocity.divide(escape_velocity.getLength()).multiply(Constants.maxSpeed);

        return escape_velocity;
    }

    private double calculateRadius(Particle particle) {
        if (particle.getRadius() < Constants.maxPartRadius) {
            return particle.getRadius() + Constants.maxPartRadius / (Constants.tau / Constants.delta_t);
        } else {
            return Constants.maxPartRadius;
        }
    }

    private double getDensity() {

        double area = Math.PI * (Math.pow(ext_radius, 2) - Math.pow(int_radius, 2));

        return quantity / area;
    }

    private double meanVelocity(){
        double mean = 0d;

        for(Particle p : particles)
            mean += p.getVelocity().getLength();

        return mean / particles.size();
    }

    /* TODO: INFO PARA PARTE A - BORRAR */
    public List<String> getOutputA() {
        List<String> output = new ArrayList<String>();

        output.add(getDensity() + "," + meanVelocity());

        return output;
    }

    /* TODO: INFO PARA PARTE B - BORRAR */
    public List<String> getOutputB(){
        List<String> output = new ArrayList<String>();

        output.add(getDensity() + "," + meanVelocity() + "," + (ext_radius-int_radius));

        return output;
    }
}
