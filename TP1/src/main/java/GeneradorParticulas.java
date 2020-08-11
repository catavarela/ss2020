import java.util.ArrayList;
import java.util.Random;

public class GeneradorParticulas {
    private int n;
    private float l;

    public GeneradorParticulas(int n, float l){
        this.n = n;
        this.l = l;
    }

    public ArrayList<String> generar(){
        Random rand = new Random();
        ArrayList<String> particulas = new ArrayList<String>(n);

        float x = 0f;
        float y = 0f;


        while(n > 0) {
            x = rand.nextFloat() * l;
            y = rand.nextFloat() * l;

            particulas.add(String.valueOf(x) + ' ' + String.valueOf(y));

            n--;
        }

        return particulas;
    }

}
