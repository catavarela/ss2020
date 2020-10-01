import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Oscilator {
    private double m;
    private double k;
    private double gamma;
    private double A;
    private Map<Double, Double> r = new HashMap<Double, Double>();
    private Map<Double, Double> v = new HashMap<Double, Double>();
    private List<String> results = new ArrayList<String>();

    public Oscilator() {
        m = 70; //kg
        k = Math.pow(10, 4); // N/m
        gamma = 100; // kg/s
        A = 1; // m
        r.put(0d, 1d);
        v.put(0d, -A * gamma / (2 * m));
    }

    public void resetResults(Metodo metodo){
        results.clear();

        switch (metodo){
            case VERLET:
                results.add("Time, Analytic Solution, Verlet");
                break;
            case BEEMAN:
                results.add("Time, Analytic Solution, Beeman");
                break;
            case GEAR:
                results.add("Time, Analytic Solution, Gear");
                break;
        }
    }

    public double force(double t) {
        return -k * r.get(t) - gamma * v.get(t);
    }

    public double force(double r, double v) {
        return -k * r - gamma * v;
    }

    public double analyticSolution(double t) {
        double gamma_squared = Math.pow(gamma, 2);
        double m_squared = Math.pow(m, 2);
        return A * Math.exp(-(gamma / (2 * m)) * t) * Math.cos(Math.sqrt(k / m - gamma_squared / (4 * m_squared)) * t);
    }

    public double getR(double t){
        return r.get(t);
    }

    private void Euler(double t, double delta_t) {
        if (v.containsKey(t + delta_t) && r.containsKey(t + delta_t)) {
            return;
        }

        double velocity = v.get(t) + (delta_t / m) * force(t);
        v.put(t + delta_t, velocity);

        double position = r.get(t) + delta_t * v.get(t) + (Math.pow(delta_t, 2) / (2 * m)) * force(t);
        r.put(t + delta_t, position);
    }


    public List<String> calculate(double final_t, double delta_t, Metodo metodo){
        double current_t = 0d;

        resetResults(metodo);

        while(current_t < final_t) {
            switch (metodo){
                case VERLET:
                    velocityVerletIteration(current_t, delta_t);
                    break;
                case BEEMAN:
                    BeemanIteration(current_t, delta_t);
                    break;
                case GEAR:
                    //TODO
                    break;
            }

            results.add(current_t + ", " + analyticSolution(current_t) + ", " + getR(current_t));
            current_t += delta_t;
        }

        return results;
    }

    private void velocityVerletIteration(double t, double delta_t) {
        //Euler(0, -delta_t/2);

        double position = r.get(t) + delta_t * v.get(t) + (Math.pow(delta_t, 2) / m) * force(t);
        r.put(t + delta_t, position);

        double aux_velocity = v.get(t) + (delta_t / (2*m)) * force(t);
        v.put(t + delta_t/2, aux_velocity);

        double aux_position = r.get(t) + (delta_t/2) * v.get(t) + (Math.pow(delta_t/2, 2) / m) * force(t);
        r.put(t + delta_t/2, aux_position);

        aux_velocity = v.get(t + delta_t/2) + (delta_t / (2*m)) * force(t + delta_t/2);
        v.put(t + delta_t, aux_velocity);

        double velocity = v.get(t) + (delta_t / (2*m)) * (force(t) + force(t + delta_t));
        v.put(t + delta_t, velocity);
    }

    private void BeemanIteration(double t, double delta_t){
        double current_r, current_v, next_r, next_v, current_a, prev_a, next_a;

        if(r.containsKey(t-delta_t) && v.containsKey(t-delta_t))
            prev_a = force(t-delta_t) / m;
        else
            prev_a = 0d;

        current_r = r.get(t);
        current_v = v.get(t);
        current_a = force(t) / m;

        next_r = current_r + current_v * delta_t + (2.0/3) * current_a * Math.pow(delta_t, 2) - (1.0/6) * prev_a * Math.pow(delta_t, 2);

        next_v = current_v + (3.0/2) * current_a * delta_t - (1.0/2) * prev_a * delta_t;

        next_a = force(next_r, next_v) / m;

        next_v = current_v + (1.0/3) * next_a * delta_t + (5.0/6) * current_a * delta_t - (1.0/6) * prev_a * delta_t;

        r.put(t + delta_t, next_r);
        v.put(t + delta_t, next_v);
    }
}
