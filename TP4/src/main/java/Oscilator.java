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
    private List<String> error = new ArrayList<String>();

    public Oscilator() {
        m = 70; //kg
        k = Math.pow(10, 4); // N/m
        gamma = 100; // kg/s
        A = 1; // m
        setInitialConditions();
        error.add("Delta T, MSE");
    }

    public void resetResults(Metodo metodo) {
        results.clear();
        r.clear();
        v.clear();
        setInitialConditions();

        switch (metodo) {
            case VERLET:
                results.add("Time, Analytic Solution, Verlet");
                break;
            case BEEMAN:
                results.add("Time, Analytic Solution, Beeman");
                break;
            case GEAR:
                results.add("Time, Analytic Solution, Gear");
                break;
            case EULER:
                results.add("Time, Analytic Solution, Euler");
                break;
        }
    }

    private void setInitialConditions() {
        r.put(0d, 1d);
        v.put(0d, -A * gamma / (2 * m));
    }

    private double force(double t) {
        return -k * r.get(t) - gamma * v.get(t);
    }

    private double force(double r, double v) {
        return -k * r - gamma * v;
    }

    public double analyticSolution(double t) {
        double gamma_squared = Math.pow(gamma, 2);
        double m_squared = Math.pow(m, 2);
        return A * Math.exp(-(gamma / (2 * m)) * t) * Math.cos(Math.sqrt(k / m - gamma_squared / (4 * m_squared)) * t);
    }

    public double getR(double t) {
        return r.get(t);
    }

    private void EulerIteration(double t, double delta_t) {
        if (v.containsKey(t + delta_t) && r.containsKey(t + delta_t)) {
            return;
        }

        double velocity = v.get(t) + (delta_t / m) * force(t);
        v.put(t + delta_t, velocity);

        double position = r.get(t) + delta_t * v.get(t) + (Math.pow(delta_t, 2) / (2 * m)) * force(t);
        r.put(t + delta_t, position);
    }

    private double[] r_derivatives(double t) {
        double[] r_derivatives = new double[6];

        r_derivatives[0] = r.get(t);
        r_derivatives[1] = v.get(t);
        r_derivatives[2] = force(t) / m;
        r_derivatives[3] = (-k * r_derivatives[1] - gamma * r_derivatives[2]) / m;
        r_derivatives[4] = (-k * r_derivatives[2] - gamma * r_derivatives[3]) / m;
        r_derivatives[5] = (-k * r_derivatives[3] - gamma * r_derivatives[4]) / m;

        return r_derivatives;
    }

    private double[] predictGear(double t, double delta_t, double[] r_derivatives) {
        double[] r_predictions = new double[6];

        r_predictions[0] = r_derivatives[0] + r_derivatives[1] * delta_t + r_derivatives[2] * Math.pow(delta_t, 2) / 2 + r_derivatives[3] * Math.pow(delta_t, 3) / 6 + r_derivatives[4] * Math.pow(delta_t, 4) / 24 + r_derivatives[5] * Math.pow(delta_t, 5) / 120;
        r_predictions[1] = r_derivatives[1] + r_derivatives[2] * delta_t + r_derivatives[3] * Math.pow(delta_t, 2) / 2 + r_derivatives[4] * Math.pow(delta_t, 3) / 6 + r_derivatives[5] * Math.pow(delta_t, 4) / 24;
        r_predictions[2] = r_derivatives[2] + r_derivatives[3] * delta_t + r_derivatives[4] * Math.pow(delta_t, 2) / 2 + r_derivatives[5] * Math.pow(delta_t, 3) / 6;
        r_predictions[3] = r_derivatives[3] + r_derivatives[4] * delta_t + r_derivatives[5] * Math.pow(delta_t, 2) / 2;
        r_predictions[4] = r_derivatives[4] + r_derivatives[5] * delta_t;
        r_predictions[5] = r_derivatives[5];

        return r_predictions;
    }

    private double evaluateGear(double delta_t, double[] r_predictions) {
        double next_a = force(r_predictions[0], r_predictions[1]) / m;
        double delta_a = next_a - r_predictions[2];

        return delta_a * Math.pow(delta_t, 2) / 2;
    }

    private double[] correctGear(double delta_t, double[] r_predictions, double delta_R2) {
        double[] r_corrected = new double[2];

        r_corrected[0] = r_predictions[0] + (3.0 / 20) * delta_R2;
        r_corrected[1] = r_predictions[1] + (251.0 / 360) * delta_R2 / delta_t;

        return r_corrected;
    }

    private void GearIteration(double t, double delta_t) {
        double[] r_predictions;
        double[] r_derivatives;
        double[] r_corrected;

        double delta_R2;

        r_derivatives = r_derivatives(t);

        r_predictions = predictGear(t, delta_t, r_derivatives);

        delta_R2 = evaluateGear(delta_t, r_predictions);

        r_corrected = correctGear(delta_t, r_predictions, delta_R2);

        r.put(t + delta_t, r_corrected[0]);
        v.put(t + delta_t, r_corrected[1]);
    }

    public List<String> calculate(double final_t, double delta_t, Metodo metodo) {
        double current_t = 0d;
        double analyticSolution, numericSolution;
        double errorSum = 0;
        int iteration = 0;

        resetResults(metodo);

        while (current_t < final_t) {
            switch (metodo) {
                case VERLET:
                    velocityVerletIteration(current_t, delta_t);
                    break;
                case BEEMAN:
                    BeemanIteration(current_t, delta_t);
                    break;
                case GEAR:
                    GearIteration(current_t, delta_t);
                    break;
                case EULER:
                    EulerIteration(current_t, delta_t);
                    break;
            }

            analyticSolution = analyticSolution(current_t);
            numericSolution = getR(current_t);
            results.add(current_t + ", " + analyticSolution + ", " + numericSolution);
            errorSum += calculateError(analyticSolution, numericSolution);
            current_t += delta_t;
            iteration++;
        }

        error.add(delta_t + ", " + errorSum / iteration);
        return results;
    }

    private double calculateError(double analyticSolution, double numericSolution) {
        return Math.pow(analyticSolution - numericSolution, 2);
    }

    public List<String> getError(){
        return error;
    }

    private void velocityVerletIteration(double t, double delta_t) {
        double position = r.get(t) + delta_t * v.get(t) + (Math.pow(delta_t, 2) / m) * force(t);
        r.put(t + delta_t, position);

        double aux_velocity = v.get(t) + (delta_t / (2 * m)) * force(t);
        v.put(t + delta_t / 2, aux_velocity);

        double aux_position = r.get(t) + (delta_t / 2) * v.get(t) + (Math.pow(delta_t / 2, 2) / m) * force(t);
        r.put(t + delta_t / 2, aux_position);

        aux_velocity = v.get(t + delta_t / 2) + (delta_t / (2 * m)) * force(t + delta_t / 2);
        v.put(t + delta_t, aux_velocity);

        double velocity = v.get(t) + (delta_t / (2 * m)) * (force(t) + force(t + delta_t));
        v.put(t + delta_t, velocity);
    }

    private void BeemanIteration(double t, double delta_t) {
        double current_r, current_v, next_r, next_v, current_a, prev_a, next_a;

        if (r.containsKey(t - delta_t) && v.containsKey(t - delta_t))
            prev_a = force(t - delta_t) / m;
        else
            prev_a = 0d;

        current_r = r.get(t);
        current_v = v.get(t);
        current_a = force(t) / m;

        next_r = current_r + current_v * delta_t + (2.0 / 3) * current_a * Math.pow(delta_t, 2) - (1.0 / 6) * prev_a * Math.pow(delta_t, 2);

        next_v = current_v + (3.0 / 2) * current_a * delta_t - (1.0 / 2) * prev_a * delta_t;

        next_a = force(next_r, next_v) / m;

        next_v = current_v + (1.0 / 3) * next_a * delta_t + (5.0 / 6) * current_a * delta_t - (1.0 / 6) * prev_a * delta_t;

        r.put(t + delta_t, next_r);
        v.put(t + delta_t, next_v);
    }
}
