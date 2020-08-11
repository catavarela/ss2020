import java.util.ArrayList;

public class CalculadorVecinos {
    private int n;
    private float l;
    private int m;
    private float rc;
    private boolean contorno;
    private String file = null;
    private ArrayList<String> particulas = null;


    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, String fileParticulas){
        this.n = n;
        this.l = l;
        this.rc = rc;
        this.contorno = contorno;

        file = fileParticulas;
    }

    public CalculadorVecinos(int n, float l, int m, float rc, boolean contorno, ArrayList<String> particulas){
        this.n = n;
        this.l = l;
        this.rc = rc;
        this.contorno = contorno;

        this.particulas = particulas;
    }

    public static int mCalculator (float l, float rc){
        int m = 1;

        while (Float.compare(l/m, rc) > 0)
            m++;

        if(Float.compare(l/m, rc) == 0)
            m--;

        return m;
    }

    public ArrayList<String> calcularVecinos(){
        ArrayList<String> vecinos = new ArrayList<String>(n+1);

        


        return vecinos;
    }

}
