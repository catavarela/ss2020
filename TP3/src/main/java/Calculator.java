import java.util.ArrayList;

public class Calculator {
    private double l;

    private ArrayList<String> sParticulas = new ArrayList<>();
    private ArrayList<Particula> particulas;

    public Calculator(double l, ArrayList<Particula> particulas) {
        this.l = l;
        this.particulas = particulas;
    }

    public Choque actualizacion() {
        Choque choque = calcularTiempoProxEvento();
        recalcularPropiedades(choque);

        return choque;
    }

    public Choque calcularTiempoProxEvento() {
        Choque choqueConTMin;
        Choque choque = null;

        for (Particula particula : particulas) {
            choqueConTMin = calcularChoque(particula);

            if (choque == null || (choqueConTMin != null && (Double.compare(choqueConTMin.getTc(), choque.getTc()) < 0))) {
                choque = choqueConTMin;

            }
        }

        return choque;
    }

    public Choque calcularChoque(Particula p) {
        Choque minChoque = null;
        Choque currentChoque;

        double vx = p.getVX(), vy = p.getVY(), x = p.getX(), y = p.getY(), r = p.getR();

        double[] delta_v = new double[2];
        double[] delta_r = new double[2];
        double[] j = new double[2];
        double omega, d, delta_v_r, J, t_current;

        double paredX2 = l;
        double paredX1 = 0;

        //choque contra paredes: los chicos no se por que hacen chequeo de infinite y nan
        if (vx > 0) {
            currentChoque = new Choque((paredX2 - r - x) / vx, p, null, -vx, vy, 0, 0);
            if (currentChoque.getTc() > 0) {
                minChoque = currentChoque;
            }
        } else if (vx < 0) {
            currentChoque = new Choque((paredX1 + r - x) / vx, p, null, -vx, vy, 0, 0);
            if (currentChoque.getTc() > 0) {
                minChoque = currentChoque;
            }
        }

        if (vy > 0) {
            currentChoque = new Choque((l - r - y) / vy, p, null, vx, -vy, 0, 0);

            if (currentChoque.getTc() > 0) {

                if (minChoque == null || Double.compare(minChoque.getTc(), currentChoque.getTc()) > 0) {
                    minChoque = currentChoque;
                }
            }
        } else if (vy < 0) {
            currentChoque = new Choque((0 + r - y) / vy, p, null, vx, -vy, 0, 0);

            if (currentChoque.getTc() > 0) {

                if (minChoque == null || Double.compare(minChoque.getTc(), currentChoque.getTc()) > 0) {
                    minChoque = currentChoque;
                }
            }
        }

        //choque contra particulas
        for (Particula p2 : particulas) {
            if (p.getId() != p2.getId()) {
                omega = p2.getR() + r;

                delta_v[0] = vx - p2.getVX();
                delta_v[1] = vy - p2.getVY();
                delta_r[0] = x - p2.getX();
                delta_r[1] = y - p2.getY();
                //System.out.println("XJ: " + x);
                //System.out.println("XI: " + p2.getX());

                delta_v_r = (delta_v[0] * delta_r[0]) + (delta_v[1] * delta_r[1]);
                //System.out.println("DELTAV: " + delta_v[0]);
                //System.out.println("DELTAR: " + delta_r[0]);
                //System.out.println("DELTAVR: " + delta_v_r);

                d = (Math.pow(delta_v_r, 2) - ((Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)) * ((Math.pow(delta_r[0], 2) + Math.pow(delta_r[1], 2)) - Math.pow(omega, 2))));

                if (delta_v_r < 0 && d >= 0 && (delta_v_r + Math.sqrt(d) < 0) && Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2) >= 0)
                    t_current = -((delta_v_r + Math.sqrt(d)) / (Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)));
                else
                    t_current = -1;

                if (t_current != -1) {
                    J = (2 * p2.getMass() * p.getMass() * delta_v_r) / (omega * (p2.getMass() + p.getMass()));


                    j[0] = J * delta_r[0] / omega;
                    j[1] = J * delta_r[1] / omega;

                    currentChoque = new Choque(t_current, p, p2,
                            vx - (j[0] / p.getMass()),
                            vy - (j[1] / p.getMass()),
                            p2.getVX() + (j[0] / p2.getMass()),
                            p2.getVY() + (j[1] / p2.getMass())
                    );

                    if (minChoque == null || (Double.compare(minChoque.getTc(), t_current) > 0))
                        minChoque = currentChoque;
                }
            }
        }
        return minChoque;
    }

/*
    public Choque calcularChoque(Particula first) {
        float deltaPositionX;
        float deltaPositionY;
        float deltaSpeedX;
        float deltaSpeedY;
        float ro;
        float firstCondition;
        float secondCondition;
        float thirdCondition;
        float time;



        Choque currentMin=null;

        for(Particula second: particulas) {
            if (first.getId() != second.getId()) {
                deltaPositionX = first.getX() - second.getX();
                deltaPositionY = first.getY() - second.getY();
                deltaSpeedX = first.getVX() - second.getVX();
                deltaSpeedY = first.getVY() - second.getVY();
                ro = first.getR() + second.getR();
                firstCondition = calculateFirstCondition(deltaPositionX, deltaPositionY, deltaSpeedX, deltaSpeedY);
                secondCondition = calculateSecondCondition(deltaPositionX, deltaPositionY, deltaSpeedX, deltaSpeedY, ro);
                thirdCondition = (float)(Math.pow(deltaSpeedX, 2) + Math.pow(deltaSpeedY, 2));
                if (firstCondition < 0 && secondCondition >= 0 &&
                        (firstCondition +  Math.sqrt(secondCondition) < 0) && thirdCondition >= 0) {
                    time = analizeCrash(firstCondition, secondCondition,thirdCondition);

                    if(currentMin == null || currentMin.getTc() > time) {


                        currentMin = new Choque(time, first, second);
                    }

                }
            }
        }

        analizeWallCrashes(first);



    }
*/

    public void recalcularPropiedades(Choque choque) {
        sParticulas.clear();
        double tc = choque.getTc();

        float cantMov = 0;

        for (Particula p : particulas) {

            p.integrate(tc);
            cantMov += p.getMass() * (p.getVX() * p.getVX() + p.getVY() * p.getVY());

            sParticulas.add(p.getX() + " " + p.getY() + " " + p.getR() + " " + p.getMass() + " " + p.getVX() + " " + p.getVY());
        }


        choque.resolver();


        //System.out.println(cantMov);
    }

    public ArrayList<String> toStringParticulas() {
        return sParticulas;
    }

}
