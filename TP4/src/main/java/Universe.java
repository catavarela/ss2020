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
            case VERLET:
                Verlet(current_t, delta_t);
                break;
            case BEEMAN:
                Beeman(current_t, delta_t);
                break;
            case GEAR:
                Gear(current_t, delta_t);
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
        double force, distance;
        double [] decomp_force = new double[2];

        distance = Math.sqrt(Math.pow(r2[0]-r1[0], 2) + Math.pow(r2[1] - r1[1], 2));

        force = force(m1, m2, distance);

        decomp_force[0] = force * (r2[0] - r1[0])/distance;
        decomp_force[1] = force * (r2[1] - r1[1])/distance;

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
        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++) {
            b = celestial_bodies.get(i+1);

            EulerIteration(t, delta_t, b);
        }
    }

    private Double [] Euler1D(Double r, Double v, double delta_t, double m, double force){
        Double [] euler = new Double[2];

        euler[0] = r + delta_t * v + (Math.pow(delta_t, 2) / (2 * m)) * force;
        euler[1] = v + (delta_t / m) * force;

        return euler;
    }

    private void EulerIteration(double t, double delta_t, Body body) {
        if (body.containsKeyV(t + delta_t) && body.containsKeyR(t + delta_t)) {
            return;
        }

        double [] force = calculateForce(body, t);

        Double[] v = body.getV(t);
        Double[] r = body.getR(t);

        Double[] euler_x = Euler1D(r[0], v[0], delta_t, body.getM(), force[0]);
        Double[] euler_y = Euler1D(r[1], v[1], delta_t, body.getM(), force[1]);

        body.putR(t + delta_t, new Double[]{euler_x[0], euler_y[0]});
        body.putV(t + delta_t, new Double[]{euler_x[1], euler_y[1]});
    }

    //0) Calcular fuerza en t                                           DONE
    //1) calcular nuevas posiciones del sistema para t + delta_t        DONE
    //2) calcular nuevas velocidades del sistema para t + delta_t/2     DONE
    //3) calcular nuevas posiciones del sistema para t + delta_t/2      DONE
    //4) calcular fuerza en t + delta_t/2                               DONE
    //5) calcular nuevas velocidades del sistema para t + delta_t       DONE
    //6) calcular fuerza en t + delta_t                                 DONE
    //7) calcular nuevas velocidades del sistema para t + delta_t       DONE

    private void Verlet(double t, double delta_t){
        double[][] forces_t = new double[2][2];
        double[][] forces = new double[2][2];

        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces_t[i] = calculateForce(b, t); //paso 0
        }

        moveSistWithForceInTVerlet(t, delta_t, forces_t); //pasos 1, 2 y 3

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces[i] = calculateForce(b, t + delta_t/2); //paso 4
        }

        newVelWithForceTPlusHalfDeltaT(t, delta_t, forces); //paso 5

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces[i] = calculateForce(b, t + delta_t); //paso 6
        }

        newVelWithForceTPlusDeltaT(t, delta_t, forces, forces_t);   //paso 7
    }

    private void newVelWithForceTPlusDeltaT(double t, double delta_t, double [][] forces, double [][] forces_t){
        Body b;
        Double[] v;
        Double[] new_v = new Double[2];

        for(int i = 0; i < celestial_bodies.size()-1; i++){

            b = celestial_bodies.get(i+1);
            v=b.getV(t);

            new_v[0] = v[0] + (delta_t / (2*b.getM())) * (forces_t[i][0] + forces[i][0]);
            new_v[1] = v[1] + (delta_t / (2*b.getM())) * (forces_t[i][1] + forces[i][1]);
            b.putV(t + delta_t, new_v);
        }
    }

    private void newVelWithForceTPlusHalfDeltaT(double t, double delta_t, double [][] forces){
        Body b;
        Double[] v;
        Double[] new_v = new Double[2];

        for(int i = 0; i < celestial_bodies.size()-1; i++){

            b = celestial_bodies.get(i+1);
            v = b.getV(t + delta_t/2);

            new_v[0] = v[0] + (delta_t / (2*b.getM())) * forces[i][0];
            new_v[1] = v[1] + (delta_t / (2*b.getM())) * forces[i][1];
            b.putV(t + delta_t, new_v);
        }
    }

    private void moveSistWithForceInTVerlet(double t, double delta_t, double [][] forces){
        Body b;
        Double[] r, v;
        Double[] new_r = new Double[2];
        Double[] new_v = new Double[2];
        Double[] aux_r = new Double[2];

        for(int i = 0; i < celestial_bodies.size()-1; i++){

            b = celestial_bodies.get(i+1);
            r = b.getR(t);
            v = b.getV(t);

            new_r[0] = r[0] + delta_t * v[0] + (Math.pow(delta_t, 2) / b.getM()) * forces[i][0];
            new_r[1] = r[1] + delta_t * v[1] + (Math.pow(delta_t, 2) / b.getM()) * forces[i][1];
            b.putR(t + delta_t, new_r);

            new_v[0] = v[0] + (delta_t / (2*b.getM())) * forces[i][0];
            new_v[1] = v[1] + (delta_t / (2*b.getM())) * forces[i][1];
            b.putV(t + delta_t/2, new_v);

            aux_r[0] = r[0] + (delta_t/2) * v[0] + (Math.pow(delta_t/2, 2) / b.getM()) * forces[i][0];
            aux_r[1] = r[1] + (delta_t/2) * v[1] + (Math.pow(delta_t/2, 2) / b.getM()) * forces[i][1];
            b.putR(t + delta_t/2, aux_r);
        }
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

    private Double[][][] calculateDerivatives(double t){
        Double[][][] r_derivatives = new Double[2][6][2];
        Double [] all_r_derivatives_sun = new Double[]{0d,0d};
        Double[][] aux_r_derivatives = new Double[3][2];


        double [] aux_force;
        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++) {
            b = celestial_bodies.get(i + 1);

            r_derivatives[i][0] = b.getR(t);
            r_derivatives[i][1] = b.getV(t);

            aux_force = calculateForce(b, t);

            r_derivatives[i][2][0] = aux_force[0] /b.getM();
            r_derivatives[i][2][1] = aux_force[1] /b.getM();
        }

        aux_r_derivatives[0] = all_r_derivatives_sun;
        aux_r_derivatives[1] = r_derivatives[0][1];
        aux_r_derivatives[2] = r_derivatives[1][1];

        for (int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i + 1);

            //calcular todas las terceras derivadas

            aux_force = calculateForce(b, r_derivatives[i][1], aux_r_derivatives);

            r_derivatives[i][3][0] = aux_force[0]/b.getM();
            r_derivatives[i][3][1] = aux_force[1]/b.getM();

        }

        aux_r_derivatives[0] = all_r_derivatives_sun;
        aux_r_derivatives[1] = r_derivatives[0][2];
        aux_r_derivatives[2] = r_derivatives[1][2];

        for (int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i + 1);

            //calcular todas las cuartas derivadas

            aux_force = calculateForce(b, r_derivatives[i][2], aux_r_derivatives);

            r_derivatives[i][4][0] = aux_force[0]/b.getM();
            r_derivatives[i][4][1] = aux_force[1]/b.getM();

        }

        aux_r_derivatives[0] = all_r_derivatives_sun;
        aux_r_derivatives[1] = r_derivatives[0][3];
        aux_r_derivatives[2] = r_derivatives[1][3];

        for (int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i + 1);

            //calcular todas las quintas derivadas

            aux_force = calculateForce(b, r_derivatives[i][3], aux_r_derivatives);

            r_derivatives[i][5][0] = aux_force[0]/b.getM();
            r_derivatives[i][5][1] = aux_force[1]/b.getM();

        }

        return r_derivatives;
    }

    private Double [][] predictGear(double delta_t, Double [][] r_derivatives){
        Double [][] r_predictions = new Double[6][2];

        r_predictions[0][0] = r_derivatives[0][0] + r_derivatives[1][0]*delta_t + r_derivatives[2][0]*Math.pow(delta_t, 2)/2 + r_derivatives[3][0]*Math.pow(delta_t, 3)/6 + r_derivatives[4][0]*Math.pow(delta_t, 4)/24 + r_derivatives[5][0]*Math.pow(delta_t, 5)/120;
        r_predictions[0][1] = r_derivatives[0][1] + r_derivatives[1][1]*delta_t + r_derivatives[2][1]*Math.pow(delta_t, 2)/2 + r_derivatives[3][1]*Math.pow(delta_t, 3)/6 + r_derivatives[4][1]*Math.pow(delta_t, 4)/24 + r_derivatives[5][1]*Math.pow(delta_t, 5)/120;
        r_predictions[1][0] = r_derivatives[1][0] + r_derivatives[2][0]*delta_t + r_derivatives[3][0]*Math.pow(delta_t, 2)/2 + r_derivatives[4][0]*Math.pow(delta_t, 3)/6 + r_derivatives[5][0]*Math.pow(delta_t, 4)/24;
        r_predictions[1][1] = r_derivatives[1][1] + r_derivatives[2][1]*delta_t + r_derivatives[3][1]*Math.pow(delta_t, 2)/2 + r_derivatives[4][1]*Math.pow(delta_t, 3)/6 + r_derivatives[5][1]*Math.pow(delta_t, 4)/24;
        r_predictions[2][0] = r_derivatives[2][0] + r_derivatives[3][0]*delta_t + r_derivatives[4][0]*Math.pow(delta_t, 2)/2 + r_derivatives[5][0]*Math.pow(delta_t, 3)/6;
        r_predictions[2][1] = r_derivatives[2][1] + r_derivatives[3][1]*delta_t + r_derivatives[4][1]*Math.pow(delta_t, 2)/2 + r_derivatives[5][1]*Math.pow(delta_t, 3)/6;
        r_predictions[3][0] = r_derivatives[3][0] + r_derivatives[4][0]*delta_t + r_derivatives[5][0]*Math.pow(delta_t, 2)/2;
        r_predictions[3][1] = r_derivatives[3][1] + r_derivatives[4][1]*delta_t + r_derivatives[5][1]*Math.pow(delta_t, 2)/2;
        r_predictions[4][0] = r_derivatives[4][0] + r_derivatives[5][0]*delta_t;
        r_predictions[4][1] = r_derivatives[4][1] + r_derivatives[5][1]*delta_t;
        r_predictions[5][0] = r_derivatives[5][0];
        r_predictions[5][1] = r_derivatives[5][1];

        return r_predictions;
    }

    private double[] evaluateGear(double delta_t, Body body, Double [][] body_r_predictions, Double [][] other_r_predictions, double m){
        double [] next_a = new double[2];
        double [] delta_a = new double[2];
        double [] force;

        force = calculateForce(body, body_r_predictions[0], other_r_predictions);

        next_a[0] = force[0] / m;
        next_a[1] = force[1] / m;

        delta_a[0] = next_a[0] - body_r_predictions[2][0];
        delta_a[1] = next_a[1] - body_r_predictions[2][1];

        delta_a[0] = delta_a[0]*Math.pow(delta_t, 2)/2;
        delta_a[1] = delta_a[1]*Math.pow(delta_t, 2)/2;

        return delta_a;
    }

    private Double [][] correctGear(double delta_t, Double [][] r_predictions, double[] delta_R2){
        Double [][] corrected = new Double[2][2];

        corrected[0][0] = r_predictions[0][0] + (3.0/20)*delta_R2[0];
        corrected[0][1] = r_predictions[0][1] + (3.0/20)*delta_R2[0];

        corrected[1][0] = r_predictions[1][0] + (251.0/360)*delta_R2[1]/delta_t;
        corrected[1][1] = r_predictions[1][1] + (251.0/360)*delta_R2[1]/delta_t;

        return corrected;
    }

    //1) Saco derivadas                                                                         DONE
    //2) Predigo                                                                                DONE
    //3) Con lo que predigo, evalúo --> Paso donde necesito ya tener las predicciones del resto DONE
    //4) Corrijo                                                                                DONE

    private void Gear(double t, double delta_t){
        Double [][][] r_predictions = new Double[2][6][2];
        Double [][][] r_derivatives;
        Double [][] corrected;
        double [][] delta_R2 = new double[2][2];
        Body b;

        Double [][] aux_r_predictions = new Double[3][2];
        aux_r_predictions[0] = celestial_bodies.get(0).getR(0);

        r_derivatives = calculateDerivatives(t);

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            r_predictions[i] = predictGear(delta_t, r_derivatives[i]);
        }

        aux_r_predictions[1] = r_predictions[0][0];
        aux_r_predictions[2] = r_predictions[1][0];

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);

            delta_R2[i] = evaluateGear(delta_t, b, r_predictions[i], aux_r_predictions, b.getM());
            corrected = correctGear(delta_t, r_predictions[i], delta_R2[i]);

            b.putR(t + delta_t, corrected[0]);
            b.putV(t + delta_t, corrected[1]);
        }
    }

    public List<String> getResults() {
        return results;
    }
}
