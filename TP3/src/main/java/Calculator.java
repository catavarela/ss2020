import java.util.ArrayList;
import java.util.Iterator;

public class Calculator {

    private int n;
    private float l;
    private boolean primerCorrida;

    private ArrayList<String> sParticulas = new ArrayList<>();
    private ArrayList<Choque> choques = new ArrayList<>();
    private ArrayList<Particula> particulas;

    public Calculator(int n, float l, ArrayList<Particula> particulas){
        this.n = n;
        this.l = l;
        primerCorrida = true;
        this.particulas = particulas;
    }

    public float actualizacion(){
        float tc = calcularTiempoProxEvento();
        recalcularPropiedades(tc);

        primerCorrida = false;

        return tc;
    }

    public float calcularTiempoProxEvento(){
        float tMin = -1;
        Choque choqueConTMin;

        for(Particula particula : particulas){

            choqueConTMin = calcularTMinChoques(particula);

            if(choqueConTMin != null && (tMin == -1 || Float.compare(choqueConTMin.getTc(),tMin) < 0)){
                choques.clear();
                tMin = choqueConTMin.getTc();

                choques.add(choqueConTMin);

            }else if (choqueConTMin != null && Float.compare(choqueConTMin.getTc(),tMin) == 0) {
                if(!choques.contains(choqueConTMin))
                    choques.add(choqueConTMin); //va a poner solo una de las dos copias posibles
            }
        }

        return tMin;
    }

    public Choque calcularTMinChoques(Particula p){
        if (primerCorrida)
            return calcularChoques(p);

        return minChoque(p);
    }

    public Choque minChoque(Particula p){
        float t_min = -1;
        Choque minChoque = null;

        for (Choque choque : p.getChoquesParticulas()){
            if(t_min == -1 || Float.compare(t_min, choque.getTc()) > 0){
                t_min = choque.getTc();
                minChoque = choque;
            }
        }

        return minChoque;
    }

    public  Choque calcularChoques(Particula p){
        Choque minChoque =  null;
        Choque currentChoque;

        float vx = p.getVX(), vy = p.getVY(), x = p.getX(), y = p.getY(), r = p.getR();

        float [] delta_v = new float[2];
        float [] delta_r = new float[2];
        float [] j = new float[2];
        float omega, d, J, delta_v_r, t_current;

        //choque contra paredes
        if(vx > 0) {
            minChoque = new Choque((l - r - x) / vx, p, null, -vx, vy, 0, 0);
            p.getChoquesParticulas().add(minChoque);

        }else if(vx < 0) {
            minChoque = new Choque((0 + r - x) / vx, p, null, -vx, vy, 0, 0);
            p.getChoquesParticulas().add(minChoque);
        }

        if(vy > 0) {
            currentChoque = new Choque((l - r - y) / vy, p, null, vx, -vy, 0, 0);
            p.getChoquesParticulas().add(currentChoque);

            if(minChoque == null || Float.compare(minChoque.getTc(),currentChoque.getTc()) > 0) {
                minChoque = currentChoque;
            }
        }else if(vy < 0) {
            currentChoque = new Choque((0 + r - y) / vy, p, null, vx, -vy, 0, 0);
            p.getChoquesParticulas().add(currentChoque);

            if(minChoque == null || Float.compare(minChoque.getTc(),currentChoque.getTc()) > 0) {
                minChoque = currentChoque;
            }
        }

        //choque contra particulas
        for(Particula p2 : particulas) {
            if (p.getId() != p2.getId()) {
                omega = r + p2.getR();

                delta_v[0] = vx - p2.getVX();
                delta_v[1] = vy - p2.getVY();
                delta_r[0] = x - p2.getX();
                delta_r[1] = y - p2.getY();

                delta_v_r = (delta_v[0] * delta_r[0]) + (delta_v[1] * delta_r[1]);

                d = (float) (Math.pow(delta_v_r, 2) - ((Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)) * ((Math.pow(delta_r[0], 2) + Math.pow(delta_r[1], 2)) - Math.pow(omega, 2))));

                if (delta_v_r < 0 && d >= 0 && (delta_v_r + Math.sqrt(d) < 0) && Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2) >= 0)
                    t_current = -(float) ((delta_v_r + Math.sqrt(d)) / (Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)));
                else
                    t_current = -1;

                if (t_current != -1) {
                    J = ((2 * p.getMass() * p2.getMass() * delta_v_r) / (omega * (p.getMass() + p2.getMass())));

                    j[0] = J * delta_r[0] / omega;
                    j[1] = J * delta_r[1] / omega;

                    currentChoque = new Choque(t_current, p, p2, vx + (j[0] / p.getMass()), vy + (j[1] / p.getMass()), p2.getVX() - (j[0] / p2.getMass()), p2.getVY() - (j[1] / p2.getMass()));
                    p.getChoquesParticulas().add(currentChoque);

                    //si no estaba en p2, agregalo
                    if (!p2.getChoquesParticulas().contains(currentChoque))
                        p2.getChoquesParticulas().add(currentChoque);

                    if (minChoque == null || (Float.compare(minChoque.getTc(), t_current) > 0))
                        minChoque = currentChoque;
                }
            }
        }
        return minChoque;
    }

    public void recalcularPropiedades(float tc){
        sParticulas.clear();

        for (Particula p : particulas)
            recalcular(p, tc);
    }

    private void recalcular(Particula p, float tc){
        Particula p1;
        Particula p2;

        p.setX(p.getX()+ p.getVX()*tc);
        p.setY(p.getY()+ p.getVY()*tc);

        for (Choque choque : choques){
            p1 = choque.getP1();
            p2 = choque.getP2();

            if(p1.getId() == p.getId()){
                p.setVX(choque.getNuevo_vx_p1());
                p.setVY(choque.getNuevo_vy_p1());

                p.getChoquesParticulas().clear();

                if(p2 != null)
                    p2.getChoquesParticulas().clear();

                calcularChoques(p);
            }else if (p2 != null && p2.getId() == p.getId()){
                p.setVX(choque.getNuevo_vx_p2());
                p.setVY(choque.getNuevo_vy_p2());

                p.getChoquesParticulas().clear();
                p1.getChoquesParticulas().clear();

                calcularChoques(p);
            }
        }

        sParticulas.add(p.getX() + " " + p.getY() + " " + p.getR() + " " +  p.getMass() + " " + p.getVX() + " " + p.getVY());
    }

    public ArrayList<String> toStringParticulas (){
        return sParticulas;
    }
}
