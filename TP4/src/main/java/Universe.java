import java.util.ArrayList;
import java.util.List;

public class Universe {
    private double G;
    private List<Body> celestial_bodies = new ArrayList<Body>();
    private List<String> results = new ArrayList<String>();

    public Universe(double G, double ms, double x0_s, double y0_s, double vx0_s, double vy0_s,
                    double mt, double x0_t, double y0_t, double vx0_t, double vy0_t,
                    double mm, double x0_m, double y0_m, double vx0_m, double vy0_m){

        this.G = G;

        celestial_bodies.add(new Body(ms, x0_s, y0_s, vx0_s, vy0_s, "Sol"));
        celestial_bodies.add(new Body(mt, x0_t, y0_t, vx0_t, vy0_t, "Tierra"));
        celestial_bodies.add(new Body(mm, x0_m, y0_m, vx0_m, vy0_m, "Marte"));
    }

    public void startResults(){
        String start = "Time";

        for(Body b : celestial_bodies)
            start += ", Position x " + b.getName() + ", Position y " + b.getName() + ", Velocity x " + b.getName() + ", Velocity y " + b.getName();


        results.add(start);
    }

    private void calculateNextIteration(double current_t, double delta_t, Metodo metodo){
        switch (metodo){
            case BEEMAN:
                Beeman(current_t, delta_t);
                break;
            case EULER:
                Euler(current_t, delta_t);
                break;
        }
    }

    public List<String> calculate(double final_t, double delta_t, Metodo metodo){
        double current_t = 0d;
        String result;

        startResults();

        while(current_t < final_t) {
            result = "";
            calculateNextIteration(current_t, delta_t, metodo);

            for (Body b : celestial_bodies)
                result += b.getOutput(current_t);

            results.add(current_t + ", " + result);
            current_t += delta_t;
        }

        return results;
    }

    private double force(double m1, double m2, double distance){
        return G*m1*m2/Math.pow(distance, 2);
    }


    private double[] force(double m1, double m2, Double[] r1, Double[] r2){
        double force, distance, teta;
        double [] decomp_force = new double[2];

        distance = Math.sqrt(Math.pow(r2[0]-r1[0], 2) + Math.pow(r2[1] - r1[1], 2));

        force = force(m1, m2, distance);

        teta = Math.atan((r2[1] - r1[1]) / (r2[0]-r1[0]));

        decomp_force[0] = force * Math.cos(teta);
        decomp_force[1] = force * Math.sin(teta);

        return decomp_force;
    }

    private double[] calculateForce(Body body, double t){
        double [] force = new double[2];
        force[0] = 0d;
        force[1] = 0d;
        double [] aux_force;

        for(Body other_body : celestial_bodies){
            if(!other_body.getName().equals(body.getName())) {
                aux_force = force(body.getM(), other_body.getM(), body.getR(t), other_body.getR(t));

                force[0] += aux_force[0];
                force[1] += aux_force[1];
            }
        }

        return force;
    }

    private double[] calculateForce(Body body, Double[] r_body, Double[][] other_r){
        double [] force = new double[2];
        force[0] = 0d;
        force[1] = 0d;
        double [] aux_force;
        Body other_b;

        for(int i = 0; i < celestial_bodies.size(); i++){
            other_b = celestial_bodies.get(i);

            if(!body.getName().equals(other_b.getName())){
                aux_force = force(body.getM(), other_b.getM(), r_body, other_r[i]);

                force[0] += aux_force[0];
                force[1] += aux_force[1];
            }
        }

        return force;
    }

    private void Euler(double t, double delta_t){
        for(Body b : celestial_bodies){
            if(!b.getName().equals("Sol")) {
                EulerIteration(t, delta_t, b);
            }
        }
    }

    private void EulerIteration(double t, double delta_t, Body b) {
        if (b.containsKeyV(t + delta_t) && b.containsKeyR(t + delta_t)) {
            return;
        }

        Double[] velocity = new Double[2];
        double[] force = calculateForce(b, t);

        Double [] vt = b.getV(t);
        Double [] rt = b.getR(t);

        velocity[0] = vt[0] + (delta_t / b.getM()) * force[0];
        velocity[1] = vt[1] + (delta_t / b.getM()) * force[1];
        b.putV(t + delta_t, velocity);

        Double[] position = new Double[2];

        position[0] = rt[0] + delta_t * vt[0] + (Math.pow(delta_t, 2) / (2 * b.getM())) * force[0];
        position[1] = rt[1] + delta_t * vt[1] + (Math.pow(delta_t, 2) / (2 * b.getM())) * force[1];

        b.putR(t + delta_t, position);
    }

    //1) Calculo fuerzas para t-delta_t                     DONE
    //2) Calculo aceleraciones en t-delta_t                 DONE
    //3) Calculo r, v y a para t                            DONE
    //4) Calculo posiciones siguientes                      DONE
    //5) Calculo velocidades siguientes                     DONE
    //6) Calculo a siguientes con r y v siguientes          DONE
    //7) Calculo velocidades siguientes con siguiente a     DONE
    //8) Agrego r y v siguientes                            DONE

    private void Beeman(double t, double delta_t) {
        double[][] forces = new double[2][2];
        double[][] prev_a = new double[2][2];

        Double[][] current_r = new Double[2][2];
        Double[][] current_v = new Double[2][2];
        Double[][] current_a = new Double[2][2];

        Double[][] next_r = new Double[3][3];
        next_r[0] = celestial_bodies.get(0).getR(0);

        Double[][] next_v = new Double[2][2];
        double[][] next_a = new double[2][2];

        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);

            if(b.containsKeyR(t-delta_t) && b.containsKeyV(t-delta_t)) {
                forces[i] = calculateForce(b, t-delta_t);   //paso 1
                prev_a[i][0] = forces[i][0] / b.getM(); //paso 2
                prev_a[i][1] = forces[i][1] / b.getM(); //paso 2
            }
            else {
                prev_a[i][0] = 0d;      //paso 2
                prev_a[i][1] = 0d;      //paso 2
            }

            forces[i] = calculateForce(b, t);

            current_r[i] = b.getR(t);   //paso 3
            current_v[i] = b.getV(t);   //paso 3

            current_a[i][0] = forces[i][0] / b.getM();  //paso 3
            current_a[i][1] = forces[i][1] / b.getM();  //paso 3

            next_r[i+1][0] = current_r[i][0] + current_v[i][0] * delta_t + (2.0/3) * current_a[i][0] * Math.pow(delta_t, 2) - (1.0/6) * prev_a[i][0] * Math.pow(delta_t, 2);  //paso 4
            next_r[i+1][1] = current_r[i][1] + current_v[i][1] * delta_t + (2.0/3) * current_a[i][1] * Math.pow(delta_t, 2) - (1.0/6) * prev_a[i][1] * Math.pow(delta_t, 2);  //paso 4

            next_v[i][0] = current_v[i][0] + (3.0/2) * current_a[i][0] * delta_t - (1.0/2) * prev_a[i][0] * delta_t;    //paso 5
            next_v[i][1] = current_v[i][1] + (3.0/2) * current_a[i][1] * delta_t - (1.0/2) * prev_a[i][1] * delta_t;    //paso 5

        }

        for(int i = 0; i < celestial_bodies.size()-1; i++) {
            b = celestial_bodies.get(i + 1);

            forces[i] = calculateForce(b, next_r[i+1], next_r);

            next_a[i][0] = forces[i][0] / b.getM(); //paso 6
            next_a[i][1] = forces[i][1] / b.getM(); //paso 6

            next_v[i][0] = current_v[i][0] + (1.0/3) * next_a[i][0] * delta_t + (5.0/6) * current_a[i][0] * delta_t - (1.0/6) * prev_a[i][0] * delta_t; //paso 7
            next_v[i][1] = current_v[i][1] + (1.0/3) * next_a[i][1] * delta_t + (5.0/6) * current_a[i][1] * delta_t - (1.0/6) * prev_a[i][1] * delta_t; //paso 7


            b.putR(t + delta_t, next_r[i+1]); //paso 8
            b.putV(t + delta_t, next_v[i]); //paso 8
        }
    }

    public List<String> getResults() {
        return results;
    }
}
