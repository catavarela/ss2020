import java.util.ArrayList;
import java.util.List;

public class Universe {
    private double G;
    private List<Body> celestial_bodies = new ArrayList<Body>();
    private List<String> results = new ArrayList<String>();

    public Universe(double G, double r0_s, double v0_s, double r0_t, double v0_t, double r0_m, double v0_m, double ms, double mt, double mm){
        this.G = G;

        celestial_bodies.add(new Body(ms, r0_s, v0_s, "Sol"));
        celestial_bodies.add(new Body(mt, r0_t, v0_t, "Tierra"));
        celestial_bodies.add(new Body(mm, r0_m, v0_m, "Marte"));
    }

    public void resetResults(){
        results.clear();

        String bodies = "";

        for(Body b : celestial_bodies){
            b.clearR();
            b.clearV();

            bodies = bodies + ", Position x " + b.getName() + ", Position y " + b.getName() + ", Velocity x " + b.getName() + ", Velocity y " + b.getName();
        }

        results.add("Time, " + bodies);
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
        String result = "";

        resetResults();

        while(current_t < final_t) {
            calculateNextIteration(current_t, delta_t, metodo);

            for (Body b : celestial_bodies)
                result += b.getOutput(current_t);

            results.add(current_t + result);
            current_t += delta_t;
        }

        return results;
    }

    private double force(double m1, double m2, double distance){
        return G*m1*m2/Math.pow(distance, 2);
    }

    //TODO: hacer la decomp de los e's y calc dist
    public double[] force(double m1, double m2, Double[] r1, Double[] r2){
        double force, distance = 0;
        double [] decomp = new double[2];

        force = force(m1, m2, distance);

        decomp[0] = force * 0;
        decomp[1] = force * 0;

        return decomp;
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

    //TODO: hacer chequeando qué se necesita en realidad
    private double[] calculateForce(Body body, double t, Double[][] r, Double[][] v){
        return null;
    }

    private void Euler(double t, double delta_t){
        for(Body b : celestial_bodies)
            EulerIteration(t, delta_t, b);
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

        Double[] [] current_r = new Double[2][2];
        Double[] [] current_v = new Double[2][2];
        Double[] [] current_a = new Double[2][2];

        Double[] [] next_r = new Double[2][2];
        Double[] [] next_v = new Double[2][2];
        double[][] next_a = new double[2][2];

        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);

            if(b.containsKeyR(t-delta_t) && b.containsKeyV(t-delta_t)) {
                forces[i] = calculateForce(b, t-delta_t);   //paso 1
                prev_a[i][0] = forces[i][0] / b.getM(); //paso 2
                prev_a[i][1] = forces[i][1] / b.getM(); //paso 2
            }
            else
                prev_a[i][0] = prev_a[i][1] = 0d;   //paso 2

            forces[i] = calculateForce(b, t);

            current_r[i] = b.getR(t);   //paso 3
            current_v[i] = b.getV(t);   //paso 3

            current_a[i][0] = forces[i][0] / b.getM();  //paso 3
            current_a[i][1] = forces[i][1] / b.getM();  //paso 3

            next_r[i][0] = current_r[i][0] + current_v[i][0] * delta_t + (2.0/3) * current_a[i][0] * Math.pow(delta_t, 2) - (1.0/6) * prev_a[i][0] * Math.pow(delta_t, 2);  //paso 4
            next_r[i][1] = current_r[i][1] + current_v[i][1] * delta_t + (2.0/3) * current_a[i][1] * Math.pow(delta_t, 2) - (1.0/6) * prev_a[i][1] * Math.pow(delta_t, 2);  //paso 4

            next_v[i][0] = current_v[i][0] + (3.0/2) * current_a[i][0] * delta_t - (1.0/2) * prev_a[i][0] * delta_t;    //paso 5
            next_v[i][1] = current_v[i][1] + (3.0/2) * current_a[i][1] * delta_t - (1.0/2) * prev_a[i][1] * delta_t;    //paso 5

        }

        for(int i = 0; i < celestial_bodies.size()-1; i++) {
            b = celestial_bodies.get(i + 1);

            forces[i] = calculateForce(b, t, next_r, next_v); //TODO: ver este force si se llega a cambiar la func

            next_a[i][0] = forces[i][0] / b.getM(); //paso 6
            next_a[i][1] = forces[i][1] / b.getM(); //paso 6

            next_v[i][0] = current_v[i][0] + (1.0/3) * next_a[i][0] * delta_t + (5.0/6) * current_a[i][0] * delta_t - (1.0/6) * prev_a[i][0] * delta_t; //paso 7
            next_v[i][1] = current_v[i][1] + (1.0/3) * next_a[i][1] * delta_t + (5.0/6) * current_a[i][1] * delta_t - (1.0/6) * prev_a[i][1] * delta_t; //paso 7


            b.putR(t + delta_t, next_r[i]); //paso 8
            b.putR(t + delta_t, next_v[i]); //paso 8
        }
    }

    private Double [][] r_derivatives(double t, Body b){
        Double [][] r_derivatives = new Double[6][2];
        double [] aux_force;

        r_derivatives[0] = b.getR(t);
        r_derivatives[1] = b.getV(t);

        aux_force = calculateForce(b, t);

        r_derivatives[2][0] = aux_force[0] /b.getM();
        r_derivatives[2][1] = aux_force[1] /b.getM();

        // TODO: G*m1*m2/Math.pow(distance, 2) a funcion de derivadas que me tire la otra parte de las sig derivadas:
        /*
        r_derivatives[3][0] = (G*r_derivatives[1][0] - gamma*r_derivatives[2][0])/b.getM();
        r_derivatives[3][1] = (G*r_derivatives[1][1] - gamma*r_derivatives[2][1])/b.getM();

        r_derivatives[4][0] = (G*r_derivatives[2][0] - gamma*r_derivatives[3][0])/b.getM();
        r_derivatives[4][1] = (G*r_derivatives[2][1] - gamma*r_derivatives[3][1])/b.getM();

        r_derivatives[5][0] = (G*r_derivatives[3][0] - gamma*r_derivatives[4][0])/b.getM();
        r_derivatives[5][1] = (G*r_derivatives[3][1] - gamma*r_derivatives[4][1])/b.getM();
*/
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

    //TODO: chequear como se saca el forceeeeee
    private double[] evaluateGear(double delta_t, Double [][] r_predictions, double m){
        double [] next_a = new double[2];
        double [] delta_a = new double[2];

        next_a[0] = force(r_predictions[0][0], r_predictions[1][0]) / m;
        next_a[1] = force(r_predictions[0][1], r_predictions[1][1]) / m;

        delta_a[0] = next_a[0] - r_predictions[2][0];
        delta_a[1] = next_a[1] - r_predictions[2][1];

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
        Double [][][] r_derivatives = new Double[2][6][2];
        Double [][] corrected;
        double [][] delta_R2 = new double[2][2];
        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);

            r_derivatives[i] = r_derivatives(t, b);
            r_predictions[i] = predictGear(delta_t, r_derivatives[i]);
        }

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);

            delta_R2[i] = evaluateGear(delta_t, r_predictions[i], b.getM());
            corrected = correctGear(delta_t, r_predictions[i], delta_R2[i]);

            b.putR(t + delta_t, corrected[0]);
            b.putV(t + delta_t, corrected[1]);
        }
    }
}
