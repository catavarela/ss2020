import java.util.ArrayList;

public class Calculator {

    private double l;

    private ArrayList<String> sParticulas = new ArrayList<>();
    private ArrayList<Particula> particulas;
    private ArrayList<Pair> sPosicion = new ArrayList<>();
    private Particula particulaGrande;
    private Particula particulaChica;
    private double x_inicial;
    private double y_inicial;


    public Calculator(double l, ArrayList<Particula> particulas, Particula particulaGrande) {
        this.l = l;
        this.particulas = particulas;
        this.particulaGrande = particulaGrande;
        this.particulaChica = particulas.get(1);
        x_inicial = particulaChica.getX();
        y_inicial = particulaChica.getY();
    }

    public void addsPosicion() {
        sPosicion.add(new Pair(particulaGrande.getX(), particulaGrande.getY()));
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
        double omega, d, delta_v_r, t_current;

        //choque contra paredes
        if (vx > 0) {
            currentChoque = new Choque((l - r - x) / vx, p, null, Pared.VERTICAL);

            if (currentChoque.getTc() > 0) {
                minChoque = currentChoque;
            }
        } else if (vx < 0) {
            currentChoque = new Choque((0 + r - x) / vx, p, null, Pared.VERTICAL);

            if (currentChoque.getTc() > 0) {
                minChoque = currentChoque;
            }
        }

        if (vy > 0) {
            currentChoque = new Choque((l - r - y) / vy, p, null, Pared.HORIZONTAL);

            if (currentChoque.getTc() > 0) {

                if (minChoque == null || Double.compare(minChoque.getTc(), currentChoque.getTc()) > 0) {
                    minChoque = currentChoque;
                }
            }
        } else if (vy < 0) {
            currentChoque = new Choque((0 + r - y) / vy, p, null, Pared.HORIZONTAL);

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

                delta_v_r = (delta_v[0] * delta_r[0]) + (delta_v[1] * delta_r[1]);

                d = (Math.pow(delta_v_r, 2) - ((Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)) * ((Math.pow(delta_r[0], 2) + Math.pow(delta_r[1], 2)) - Math.pow(omega, 2))));

                if (delta_v_r < 0 && d >= 0 && (delta_v_r + Math.sqrt(d) < 0) && Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2) >= 0)
                    t_current = -((delta_v_r + Math.sqrt(d)) / (Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)));
                else
                    t_current = -1;

                if (t_current != -1) {
                    currentChoque = new Choque(t_current, p, p2, Pared.NO_PARED);

                    if (minChoque == null || (Double.compare(minChoque.getTc(), t_current) > 0))
                        minChoque = currentChoque;
                }
            }
        }
        return minChoque;
    }

    public void recalcularPropiedades(Choque choque) {
        sParticulas.clear();

        double tc = choque.getTc();

        for (Particula p : particulas) {
            p.integrate(tc);
            sParticulas.add(p.getX() + " " + p.getY() + " " + p.getR() + " " + p.getColor());
        }

        recalcularVelocidadesDespuesDelChoque(choque);
    }

    private void recalcularVelocidadesDespuesDelChoque(Choque choque) {
        switch (choque.getPared()){
            case VERTICAL:
                choque.getP1().setVX(-1*choque.getP1().getVX());
                break;

            case HORIZONTAL:
                choque.getP1().setVY(-1*choque.getP1().getVY());
                break;

            case NO_PARED:
                recalcularVelocidadesDespuesDeChoqueEntreParticulas(choque.getP1(), choque.getP2());
                break;
        }
    }

    private void recalcularVelocidadesDespuesDeChoqueEntreParticulas(Particula p1, Particula p2) {
        double omega;
        double[] delta_r = new double[2];

        double J;
        double[] j = new double[2];

        double[] nuevas_v_p1 = new double[2];
        double[] nuevas_v_p2 = new double[2];

        omega = p1.getR() + p2.getR();
        delta_r[0] = p1.getX() - p2.getX();
        delta_r[1] = p1.getY() - p2.getY();


        J = jacobiano(p1, p2);
        j[0] = (J * delta_r[0]) / omega;
        j[1] = (J * delta_r[1]) / omega;


        nuevas_v_p1[0] = p1.getVX() - (j[0] / p1.getMass());
        nuevas_v_p1[1] = p1.getVY() - (j[1] / p1.getMass());

        nuevas_v_p2[0] = p2.getVX() + (j[0] / p2.getMass());
        nuevas_v_p2[1] = p2.getVY() + (j[1] / p2.getMass());

        p1.setVX(nuevas_v_p1[0]);
        p1.setVY(nuevas_v_p1[1]);

        p2.setVX(nuevas_v_p2[0]);
        p2.setVY(nuevas_v_p2[1]);

    }

    private double jacobiano(Particula p1, Particula p2) {
        double J, omega;

        double [] delta_r = new double[2];
        double[] delta_v = new double[2];

        delta_r[0] = p1.getX() - p2.getX();
        delta_r[1] = p1.getY() - p2.getY();
        delta_v[0] = p1.getVX() - p2.getVX();
        delta_v[1] = p1.getVY() - p2.getVY();

        omega = p1.getR() + p2.getR();

        J = (2 * p1.getMass() * p2.getMass()) * ((delta_v[0] * delta_r[0]) + (delta_v[1] * delta_r[1])) / (omega * (p1.getMass() + p2.getMass()));

        return J;

    }

    public ArrayList<String> toStringParticulas() {
        return sParticulas;
    }

    public String getPosicionParticulaGrande() {
        return particulaGrande.getX() + ";" + particulaGrande.getY();
    }

    public double getTemperatura() {
        double suma = 0;

        for(Particula p: particulas) {
            suma += p.getMass() * Math.pow(Math.hypot(p.getVX(), p.getVY()), 2);
        }

        return suma / (particulas.size() * 2 * 1.38064852 * Math.pow(10, -23));
    }

    public double getDCM(boolean grande) {
        double DCM;
        double dist_x;
        double dist_y;

        if(grande) {
            dist_x = Math.abs(particulaGrande.getX() - 3);
            dist_y = Math.abs(particulaGrande.getY() - 3);
        } else {
            dist_x = Math.abs(particulaChica.getX() - x_inicial);
            dist_y = Math.abs(particulaChica.getY() - y_inicial);
        }
        DCM = dist_x * dist_x + dist_y * dist_y;
        return DCM;
    }

    public ArrayList<Pair> getsPosicion() {
        return sPosicion;
    }
}
