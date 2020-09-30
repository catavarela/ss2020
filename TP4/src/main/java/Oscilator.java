public class Oscilator {
    private double m;
    private double k;
    private double gamma;
    private double A;
    private double initial_r;
    private double initial_v;
    private double current_r;
    private double current_v;

    public Oscilator(){
        m = 70; //kg
        k = Math.pow(10,4); // N/m
        gamma = 100; // kg/s
        A = 1; // m
        initial_r = 1; // m
        initial_v = -A * gamma / (2*m); // m/s
        current_r = initial_r;
        current_v = initial_v;
    }

    public double force(){
        return -k * current_r - gamma * current_v;
    }

    public double analyticSolution(double t) {
        double gamma_squared = Math.pow(gamma, 2);
        double m_squared = Math.pow(m, 2);
        return A * Math.exp(-(gamma/(2*m))*t) * Math.cos(Math.sqrt(k/m - gamma_squared / (4*m_squared)) * t);
    }
}
