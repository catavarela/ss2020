import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

public class Calculator {

    private float l;
    private boolean primerCorrida;

    private ArrayList<String> sParticulas = new ArrayList<>();
    private Choque choque;
    private ArrayList<Particula> particulas;

    public Calculator(float l, ArrayList<Particula> particulas){
        this.l = l;
        primerCorrida = true;
        this.particulas = particulas;
    }

    public float actualizacion(){
        float tc = calcularTiempoProxEvento();
        recalcularPropiedades(tc);

        System.out.println("tc: " + tc);

        if(choque.getP2() == null)
            System.out.println("Choque con p1: " + choque.getP1().getId() + " y pared: " + choque.getP2());
        else
            System.out.println("Choque con p1: " + choque.getP1().getId() + " y p2: " + choque.getP2().getId());

        choque = null;

        primerCorrida = false;

        return tc;
    }

    public float calcularTiempoProxEvento(){
        float tMin = -1;
        Choque choqueConTMin;

        for(Particula particula : particulas){

            choqueConTMin = calcularTMinChoques(particula);

            if(choqueConTMin != null && (tMin == -1 || Float.compare(choqueConTMin.getTc(),tMin) < 0)){
                tMin = choqueConTMin.getTc();

                choque = choqueConTMin;

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

        //choque contra paredes: los chicos no se por que hacen chequeo de infinite y nan
        if(vx > 0) {
            currentChoque = new Choque((l - r - x) / vx, p, null, -vx, vy, 0, 0);

            if(currentChoque.getTc() > 0) {
                p.getChoquesParticulas().add(currentChoque);
                minChoque = currentChoque;
            }

        }else if(vx < 0) {
            currentChoque = new Choque((0 + r - x) / vx, p, null, -vx, vy, 0, 0);
            if(currentChoque.getTc() > 0){
                p.getChoquesParticulas().add(currentChoque);
                minChoque = currentChoque;
            }
        }

        if(vy > 0) {
            currentChoque = new Choque((l - r - y) / vy, p, null, vx, -vy, 0, 0);

            if(currentChoque.getTc() > 0){
                p.getChoquesParticulas().add(currentChoque);

            if(minChoque == null || Float.compare(minChoque.getTc(),currentChoque.getTc()) > 0) {
                minChoque = currentChoque;
            }}
        }else if(vy < 0) {
            currentChoque = new Choque((0 + r - y) / vy, p, null, vx, -vy, 0, 0);

            if(currentChoque.getTc() > 0){
                p.getChoquesParticulas().add(currentChoque);

            if(minChoque == null || Float.compare(minChoque.getTc(),currentChoque.getTc()) > 0) {
                minChoque = currentChoque;
            }}
        }

        //choque contra particulas
        for(Particula p2 : particulas) {
            if (p.getId() != p2.getId()) {
                omega = p2.getR() + r;

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
                    J = ((2 * p2.getMass() * p.getMass() * delta_v_r) / (omega * (p2.getMass() + p.getMass())));

                    j[0] = J * delta_r[0] / omega;
                    j[1] = J * delta_r[1] / omega;

                    currentChoque = new Choque(t_current, p, p2, vx - (j[0] / p.getMass()), vy - (j[1] / p.getMass()), p2.getVX() + (j[0] / p2.getMass()), p2.getVY() + (j[1] / p2.getMass()));
                    p.getChoquesParticulas().add(currentChoque);

                    //si no estaba en p2, agregalo
                    int index = p2.getChoquesParticulas().indexOf(currentChoque);

                    if(index == -1)
                        p2.getChoquesParticulas().add(currentChoque);
                    else if (p2.getChoquesParticulas().get(index).getTc() != currentChoque.getTc()){
                        p2.getChoquesParticulas().remove(index);
                        p2.getChoquesParticulas().add(currentChoque);
                    }

                    if (minChoque == null || (Float.compare(minChoque.getTc(), t_current) > 0))
                        minChoque = currentChoque;
                }
            }
        }
        return minChoque;
    }

    public void recalcularPropiedades(float tc){
        sParticulas.clear();

        Particula p1;
        Particula p2;

        int i;

        p1 = choque.getP1();
        p2 = choque.getP2();

        for (Particula p : particulas){
            p.setX(p.getX()+ p.getVX()*tc);
            p.setY(p.getY()+ p.getVY()*tc);

            if(p1.getId() == p.getId()){
                p.setVX(choque.getNuevo_vx_p1());
                p.setVY(choque.getNuevo_vy_p1());

                p.getChoquesParticulas().clear();

                if(p2 != null) {
                    i = particulas.indexOf(p2);

                    particulas.get(i).getChoquesParticulas().clear();

                    particulas.get(i).setVX(choque.getNuevo_vx_p2());
                    particulas.get(i).setVY(choque.getNuevo_vy_p2());

                    calcularChoques(particulas.get(i));
                }

                calcularChoques(p);
            }

            sParticulas.add(p.getX() + " " + p.getY() + " " + p.getR() + " " +  p.getMass() + " " + p.getVX() + " " + p.getVY());
        }
    }

    public ArrayList<String> toStringParticulas (){
        return sParticulas;
    }
}
