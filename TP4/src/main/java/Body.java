import java.util.HashMap;
import java.util.Map;

public class Body {
    private double m;
    private String name;

    private Map<Double, Double[]> r = new HashMap<Double, Double[]>();
    private Map<Double, Double[]> v = new HashMap<Double, Double[]>();

    public Body(double m, double x0, double y0, double vx0, double vy0, String name){
        this.m = m;
        this.name = name;

        r.put(0d, new Double[]{x0, y0});
        v.put(0d, new Double[]{vx0, vy0});
    }

    public String getOutput(double t){
        Double[] pos,vel;
        pos = getR(t); vel = getV(t);

        return "" + pos[0] + ", " + pos[1] + ", " + vel[0] + ", " + vel[1] + ", ";
    }

    public String getName() {
        return name;
    }

    public boolean containsKeyR(double t){
        return r.containsKey(t);
    }

    public boolean containsKeyV(double t){
        return v.containsKey(t);
    }

    public void clearR(){
        r.clear();
    }

    public void clearV(){
        v.clear();
    }

    /*
    public static Double[] descomposition(double value){
        Double[] componentes = new Double[2];

        componentes[0] = value;
        componentes[1] = value;

        return componentes;
    }*/

    public double getM() {
        return m;
    }

    public Double[] getR(double t) {
        if(name.equals("Sol"))
            return r.get(0d);

        return r.get(t);
    }

    public Double[] getV(double t) {
        if(name.equals("Sol"))
            return v.get(0d);

        return v.get(t);
    }

    public void putR(double t, Double[] r) {
        this.r.put(t, r);
    }

    public void putV(double t, Double[] v) {
        this.v.put(t, v);
    }
}
