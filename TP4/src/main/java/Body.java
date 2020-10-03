import java.util.HashMap;
import java.util.Map;

public class Body {
    private double m;
    private String name;

    private Map<Double, Double[]> r = new HashMap<Double, Double[]>();
    private Map<Double, Double[]> v = new HashMap<Double, Double[]>();

    public Body(double m, double r0, double v0, String name){
        this.m = m;
        this.name = name;

        r.put(0d, descomposition(r0));
        v.put(0d, descomposition(v0));
    }

    //TODO: en un principio está descompuesto pero si en el post nos quedan archivos muy grandes podemos guardarlo sin descomponer y después descomponer en el post
    public String getOutput(double t){
        Double[] pos,vel;
        pos = r.get(t); vel = v.get(t);

        return "" + pos[0] + ", " + pos[1] + ", " + vel[0] + ", " + vel[1];
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

    //TODO: ver cómo es la descomposición
    public static Double[] descomposition(double value){
        Double[] componentes = new Double[2];

        componentes[0] = value;
        componentes[1] = value;

        return componentes;
    }

    public double getM() {
        return m;
    }

    public Double[] getR(double t) {
        return r.get(t);
    }

    public Double[] getV(double t) {
        return v.get(t);
    }

    public void putR(double t, Double[] r) {
        this.r.put(t, r);
    }

    public void putV(double t, Double[] v) {
        this.v.put(t, v);
    }
}
