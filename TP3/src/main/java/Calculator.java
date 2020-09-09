import java.util.ArrayList;
import java.util.Iterator;

public class Calculator {

    private int n;
    private float l;
    private  int m;

    ArrayList<String> sParticulas = new ArrayList<>();
    ArrayList<Particula> chocadas = new ArrayList<>();
    ArrayList<Particula> particulas;

    public Calculator(int n, float l, int m, ArrayList<Particula> particulas){
        this.n = n;
        this.l = l;
        this.m = m;

        this.particulas = particulas;
    }

    public float actualizacion(){
        float tc = calcularTiempoProxEvento();
        recalcularPropiedades(tc);

        return tc;
    }

    public float calcularTiempoProxEvento(){
        Particula current;
        float tMin = -1;
        float currentChoque [];

        Iterator<Particula> it = particulas.iterator();

        while (it.hasNext()){
            current = it.next();

            currentChoque = calcularTMinChoques(current);

            if(currentChoque[0] != -1 && (tMin == -1 || Float.compare(currentChoque[0],tMin) < 0)){
                chocadas.clear();
                tMin = currentChoque[0];

                System.out.println("tMin: "+ tMin);

                chocadas.add(new Particula(current.getId(),current.getX(), current.getY(), current.getNext(), current.getR(), current.getMass(), currentChoque[1], currentChoque[2]));

            }else if (Float.compare(currentChoque[0],tMin) == 0)
                chocadas.add(new Particula(current.getId(),current.getX(), current.getY(), current.getNext(), current.getR(), current.getMass(), currentChoque[1], currentChoque[2]));
        }

        return tMin;
    }

    public  float[] calcularTMinChoques(Particula p){
        float t_current;

        float [] minChoque = new float[3];
        minChoque[0] = -1;
        minChoque[1] = -1;
        minChoque[2] = -1;

        float vx = p.getVX(), vy = p.getVY(), x = p.getX(), y = p.getY(), r = p.getR();

        Particula current = null;

        float [] delta_v = new float[2];
        float [] delta_r = new float[2];
        float [] j = new float[2];
        float omega, d, J, delta_v_r;

        //choque contra paredes
        if(vx > 0) {
            minChoque[0] = (l - r - x) / vx;
            minChoque[1] = -vx;
            minChoque[2] = vy;
        }else if(vx < 0) {
            minChoque[0] = (0 + r - x) / vx;
            minChoque[1] = -vx;
            minChoque[2] = vy;
        }

        if(vy > 0) {
            t_current = (l - r - y) / vy;
            if(minChoque[0] == -1 || Float.compare(minChoque[0],t_current) > 0) {
                minChoque[0] = t_current;
                minChoque[1] = vx;
                minChoque[2] = -vy;
            }
        }else if(vy < 0) {
            t_current = (0 + r - y) / vy;
            if(minChoque[0] == -1 || Float.compare(minChoque[0],t_current) > 0) {
                minChoque[0] = t_current;
                minChoque[1] = vx;
                minChoque[2] = -vy;
            }
        }

        //choque contra particulas
        Iterator<Particula> it = particulas.iterator();

        while (it.hasNext()){
            current = it.next();
                if(p.getId() != current.getId()){
                    omega = r + current.getR();

                    delta_v[0] = vx - current.getVX();
                    delta_v[1] = vy - current.getVY();
                    delta_r[0] = x - current.getX();
                    delta_r[1] = y - current.getY();

                    delta_v_r = (delta_v[0]*delta_r[0]) + (delta_v[1]*delta_r[1]);

                    d = (float)(Math.pow(delta_v_r, 2) -
                        ((Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2))*
                        ((Math.pow(delta_r[0], 2) + Math.pow(delta_r[1], 2)) -
                        Math.pow(omega, 2))));

                    if(Float.compare(delta_v_r, 0) >= 0)
                        t_current = -1;
                    else if (Float.compare(d, 0) < 0)
                        t_current = -1;
                    else {
                        t_current = (float) -((delta_v_r + Math.sqrt((double) d)) / (Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)));
                    }

                    if(t_current != -1 && (minChoque[0] == -1 || (Float.compare(minChoque[0],t_current) > 0))){
                        minChoque[0] = t_current;

                        J = ((2*(p.getMass() + current.getMass())*delta_v_r)/(omega * (p.getMass() + current.getMass())));

                        j[0] = J * delta_r[0] / omega;
                        j[1] = J * delta_r[1] / omega;

                        minChoque[1] = vx + (j[0]/p.getMass());
                        minChoque[2] = vy + (j[1]/p.getMass());

                    }
                }
            }

        System.out.println("minChoque: "+minChoque[0]);

        return minChoque;
    }

    public void recalcularPropiedades(float tc){
        sParticulas.clear();

        Iterator<Particula> it = particulas.iterator();

        while (it.hasNext())
            recalcular(it.next(), tc);
    }

    private int esChocada(Particula p){
        Iterator<Particula> it = chocadas.iterator();
        int i = 0;

        while (it.hasNext()){
            if(it.next().getId() == p.getId())
                return i;

            i++;
        }

        return -1;
    }

    private void recalcular(Particula p, float tc){
        int chocada = 0;

        chocada = esChocada(p);

        p.setX(p.getX()+ p.getVX()*tc);
        p.setY(p.getY()+ p.getVY()*tc);

        if(chocada >= 0){
            p.setVY(chocadas.get(chocada).getVY());
            p.setVX(chocadas.get(chocada).getVX());
        }

        sParticulas.add(p.getX() + " " + p.getY() + " " + p.getR() + " " +  p.getMass() + " " + p.getVX() + " " + p.getVY());
    }

    public ArrayList<String> toStringParticulas (){
        return sParticulas;
    }

    public static int mCalculator (float l, float rc, float rMax){
        int m = (int)Math.floor(l/(rc+(2*rMax)));

        return m;
    }

    protected static int calcFil(float y, float l, int m){
        return y == l ? 0 : m - (int)Math.floor(((double)y)/(l/m)) - 1;
    }


    protected static int calcCol(float x, float l, int m){
        return x == l ? m - 1 : (int)Math.floor(((double)x)/(l/m));
    }

    protected static void agregarParticula (Particula[][] heads, Particula particula, float l, int m){
        float x = particula.getX();
        float y = particula.getY();

        int f = calcFil(y, l, m);
        int c = calcCol(x, l, m);

        Particula head = heads[f][c];

        if(head == null) {
            heads[f][c] = particula;
        }
        else {
            heads[f][c] = particula;
            particula.setNext(head);
        }
    }
}
