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
}
