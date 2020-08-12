import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private float l;
    private ArrayList<Particula> particulas;

    public GeneradorParticulas(int n, float l){
        this.n = n;
        this.l = l;
        this.particulas = new ArrayList<Particula>();
    }

    public ArrayList<String> generar(){
        Random rand = new Random();
        ArrayList<String> particulas = new ArrayList<String>(n);

        float x = 0f;
        float y = 0f;

        int id = 1;

        while(n > 0) {
            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            particulas.add(String.valueOf(x) + ' ' + String.valueOf(y));
            this.particulas.add(new Particula(id++, x, y, null));

            n--;
        }

        return particulas;
    }

    public ArrayList<Particula> getParticulas() {
        return particulas;
    }
}
