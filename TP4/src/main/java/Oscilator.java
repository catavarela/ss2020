import java.util.HashMap;
import java.util.Map;

public class Oscilator {
    private double m;
    private double k;
    private double gamma;
    private double A;
    private Map<Double, Double> r = new HashMap<Double, Double>();
    private Map<Double, Double> v = new HashMap<Double, Double>();

    public Oscilator(){
        m = 70; //kg
        k = Math.pow(10,4); // N/m
        gamma = 100; // kg/s
        A = 1; // m
        r.put(0d, 1d);
        v.put(0d, -A * gamma / (2*m));
    }

    public double force(double t){
        return -k * r.get(t) - gamma * v.get(t);
    }

    public double analyticSolution(double t) {
        double gamma_squared = Math.pow(gamma, 2);
        double m_squared = Math.pow(m, 2);
        return A * Math.exp(-(gamma/(2*m))*t) * Math.cos(Math.sqrt(k/m - gamma_squared / (4*m_squared)) * t);
    }

    private void Euler(double t, double delta_t){
        double velocity = v.get(t) + (delta_t / m) * force(t);
        v.put(t + delta_t, velocity);

        double position = r.get(t) + delta_t * v.get(t) + (Math.pow(delta_t, 2) / (2*m)) * force(t);
        r.put(t + delta_t, position);
    }
}
