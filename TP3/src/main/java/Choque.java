public class Choque{
    private double tc;
    private Particula p1;
    private Particula p2;
    private Pared pared;

    public Choque (double tc, Particula p1, Particula p2, Pared pared){
        this.tc = tc;
        this.p1 = p1;
        this.p2 = p2;

        this.pared = pared;
    }

    public double getTc() {
        return tc;
    }

    public Particula getP1() {
        return p1;
    }

    public Particula getP2() {
        return p2;
    }

    public Pared getPared() {
        return pared;
    }
}
