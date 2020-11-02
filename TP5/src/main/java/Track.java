import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Track {
    private List<Particle> particles = new ArrayList<Particle>();
    private double intRadius;
    private double extRadius;

    public Track() {
        intRadius = Constants.intTrackRadius;
        extRadius = Constants.extTrackRadius;
        createParticles();
    }

    private void createParticles(){
        Random rand = new Random();
        int tries = Constants.maxTries;

        while(tries-- > 0) {
            double x = -extRadius + 2 * extRadius * rand.nextDouble();
            double y = -extRadius + 2 * extRadius * rand.nextDouble();
            double radius = Constants.minPartRadius + (Constants.maxPartRadius - Constants.minPartRadius) * rand.nextDouble();
            Particle particle = new Particle(radius, x, y, 0, 0);

            if (isValid(particle)) {
                particles.add(particle);
                tries = Constants.maxTries;
            }
        }
    }

    private boolean isValid(Particle particle){
        if(particle.getDistance() - particle.getRadius() < intRadius || particle.getDistance() + particle.getRadius() > extRadius) return false;

        for(Particle other : particles) {
            double distance = particle.getDistanceToParticle(other);

            if(distance < particle.getRadius() + other.getRadius()) return false;
        }

        return true;
    }

    public List<String> getOutput() {
        List<String> output = new ArrayList<String>();

        for(Particle particle : particles) {
            output.add(particle.getX() + " " + particle.getY() + " " + particle.getRadius());
        }

        output.add(0, String.valueOf(particles.size()));
        output.add(1, "");

        return output;
    }
}
