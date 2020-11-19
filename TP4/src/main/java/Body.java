import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Body {
    private double m;
    private double radius;
    private String name;

    private Map<Double, Double[]> r = new HashMap<Double, Double[]>();
    private Map<Double, Double[]> v = new HashMap<Double, Double[]>();

    public Body(double t, double m, double radius, double x0, double y0, double vx0, double vy0, String name) {
        this.m = m;
        this.radius = radius;
        this.name = name;

        r.put(t, new Double[]{x0, y0});
        v.put(t, new Double[]{vx0, vy0});
    }

    public String getOutput(double t) {
        Double[] pos;
        pos = getR(t);

        return String.format(Locale.US, "%6.7e", pos[0]) + "    " + String.format(Locale.US, "%6.7e", pos[1]);
    }

    public String getName() {
        return name;
    }

    public double getRadius() {
        return radius;
    }

    public boolean containsKeyR(double t) {
        return r.containsKey(t);
    }

    public boolean containsKeyV(double t) {
        return v.containsKey(t);
    }

    public void clearR() {
        r.clear();
    }

    public void clearV() {
        v.clear();
    }

    public double getM() {
        return m;
    }

    public Double[] getR(double t) {
        if (name.equals("Sol"))
            return r.get(0d);

        return r.get(t);
    }

    public Double[] getV(double t) {
        if (name.equals("Sol"))
            return v.get(0d);

        return v.get(t);
    }

    public void putR(double t, Double[] r) {
        if (!name.equals("Sol"))
            this.r.put(t, new Double[]{r[0], r[1]});
    }

    public void putV(double t, Double[] v) {
        if (!name.equals("Sol"))
            this.v.put(t, new Double[]{v[0], v[1]});
    }

    public double getSpeed(double t) {
        Double speed[] = getV(t);

        return Math.hypot(speed[0], speed[1]);
    }
}
