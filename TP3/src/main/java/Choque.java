public class Choque{
    private double tc;
    private Particula p1;
    private Particula p2;
    private double nuevo_vx_p1;
    private double nuevo_vy_p1;
    private double nuevo_vx_p2;
    private double nuevo_vy_p2;

    public Choque (double tc, Particula p1, Particula p2, double nuevo_vx_p1, double nuevo_vy_p1, double nuevo_vx_p2, double nuevo_vy_p2){
        this.tc = tc;
        this.p1 = p1;
        this.p2 = p2;
        this.nuevo_vx_p1 = nuevo_vx_p1;
        this.nuevo_vy_p1 = nuevo_vy_p1;
        this.nuevo_vx_p2 = nuevo_vx_p2;
        this.nuevo_vy_p2 = nuevo_vy_p2;
    }

    public double getNuevo_vx_p1() {
        return nuevo_vx_p1;
    }

    public double getNuevo_vy_p2() {
        return nuevo_vy_p2;
    }

    public double getTc() {
        return tc;
    }

    public double getNuevo_vx_p2() {
        return nuevo_vx_p2;
    }

    public double getNuevo_vy_p1() {
        return nuevo_vy_p1;
    }

    public Particula getP1() {
        return p1;
    }

    public Particula getP2() {
        return p2;
    }

    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Choque)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Choque c = (Choque) o;

        // Compare the data members and return accordingly
        return ((p1 == c.getP1() && p2 == c.getP2()) || (p2 == c.getP1() && p1 == c.getP2()));
    }


    void resolver() {
        if (p2 == null) {
            // paredes
            p1.setVX(nuevo_vx_p1);
            p1.setVY(nuevo_vy_p1);
        }
        else {
            double omega = p2.getR() + p1.getR();
            double delta_v[] = new double[2];
            double delta_r[] = new double[2];

            delta_v[0] = p1.getVX() - p2.getVX();
            delta_v[1] = p1.getVY() - p2.getVY();
            delta_r[0] = p1.getX() - p2.getX();
            delta_r[1] = p1.getY() - p2.getY();
            //System.out.println("XJ: " + x);
            //System.out.println("XI: " + p2.getX());

            double delta_v_r = (delta_v[0] * delta_r[0]) + (delta_v[1] * delta_r[1]);
            //System.out.println("DELTAV: " + delta_v[0]);
            //System.out.println("DELTAR: " + delta_r[0]);
            //System.out.println("DELTAVR: " + delta_v_r);

            double d = (Math.pow(delta_v_r, 2) - ((Math.pow(delta_v[0], 2) + Math.pow(delta_v[1], 2)) * ((Math.pow(delta_r[0], 2) + Math.pow(delta_r[1], 2)) - Math.pow(omega, 2))));


            double J = (2 * p2.getMass() * p1.getMass() * delta_v_r) / (omega * (p2.getMass() + p1.getMass()));
            double j[] = new double[2];

            j[0] = J * delta_r[0] / omega;
            j[1] = J * delta_r[1] / omega;

            p1.setVX(p1.getVX() - (j[0] / p1.getMass()));
            p1.setVY(p1.getVY() - (j[1] / p1.getMass()));

            p2.setVX(p2.getVX() + (j[0] / p2.getMass()));
            p2.setVY(p2.getVY() + (j[1] / p2.getMass()));
        }
    }
}
