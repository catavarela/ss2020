import java.util.ArrayList;
import java.util.List;

public class Universe {
    private double G;
    private List<Body> celestial_bodies;
    private List<String> results = new ArrayList<String>();
    private List<String> rocket_speed = new ArrayList<String>();
    private boolean hay_cohete;
    private double dia_del_despegue;

    public Universe(double G, ArrayList<Body> celestial_bodies, boolean hay_cohete, double dia_del_despegue){

        this.G = G;
        this.celestial_bodies = celestial_bodies;
        this.hay_cohete = hay_cohete;
        this.dia_del_despegue = dia_del_despegue;
    }

    /*public void startResults(){
        String start = "Time";

        for(Body b : celestial_bodies)
            start += ", Position x " + b.getName() + ", Position y " + b.getName() + ", Velocity x " + b.getName() + ", Velocity y " + b.getName();


        results.add(start);
    }*/

    private void calculateNextIteration(double current_t, double delta_t, Metodo metodo){
        switch (metodo){
            case BEEMAN:
                Beeman(current_t, delta_t);
                break;
            case EULER:
                Euler(current_t, delta_t);
                break;
            case VERLET:
                Verlet(current_t, delta_t);
                break;
        }
    }

    public double calculate(double final_t, double delta_t, Metodo metodo){
        double current_t = 0d;
        double crash_time = 0d;
        boolean no_fue_agregado = true;
        int iteration = 0;
        Body crashed_body;

        //startResults();

        while(current_t < final_t) {
            //result = "";

            if(hay_cohete && no_fue_agregado && Double.compare(current_t, dia_del_despegue) >= 0) {
                no_fue_agregado = false;

                celestial_bodies.add(addRocket(current_t));
            }

            calculateNextIteration(current_t, delta_t, metodo);

            if(hay_cohete && !no_fue_agregado && ((crashed_body = rocketCrash(current_t)) != null)) {
                crash_time = current_t;
                celestial_bodies.remove(celestial_bodies.size() - 1);

                System.out.println("La nave chocó contra " + crashed_body.getName());
            }

            //if(iteration % 10000 == 0) {
                results.add("" + (int) current_t);

                for (Body b : celestial_bodies) {
                    results.add(b.getOutput(current_t));
                    if(b.getName().equals("Cohete")){
                        rocket_speed.add((int)current_t + "," + b.getSpeed(current_t));
                    }
                }
            //}

            //results.add(current_t + ", " + result);
            current_t += delta_t;
            iteration++;
        }

        return crash_time;
    }

    private Body rocketCrash(double t){
        Body cohete = celestial_bodies.get(celestial_bodies.size() - 1);
        double distance = -1, aux_distance;

        Body body_crashed = null;

        for(Body body : celestial_bodies){
            if(!body.getName().equals("Cohete")){
                aux_distance = Math.sqrt(Math.pow(cohete.getR(t)[0] - body.getR(t)[0], 2) + Math.pow(cohete.getR(t)[1] - body.getR(t)[1], 2));

                if(distance == -1 || Double.compare(distance,aux_distance) > 0){
                    distance = aux_distance;

                    body_crashed = body;
                }
            }
        }

        if(body_crashed != null && Double.compare(distance, body_crashed.getRadius()) <= 0)
            return body_crashed;

        return null;
    }

    private Body addRocket(double t){

        double rocket_mass = Constants.rocket_mass;
        double dist_to_space_station = Constants.dist_to_space_station;
        double orbital_velocity_of_space_station = Constants.orbital_velocity_of_space_station;
        double rocket_blastoff_velocity = Constants.rocket_blastoff_velocity;

        Body sol = null;
        Body tierra = null;

        double x0, y0, vx0, vy0;

        for(Body body : celestial_bodies){
            if(body.getName().equals("Sol"))
                sol = body;
            else if (body.getName().equals("Tierra"))
                tierra = body;
        }

        double teta = Math.atan2(tierra.getR(t)[1] - sol.getR(t)[1], tierra.getR(t)[0] - sol.getR(t)[0]);

        x0 = (tierra.getRadius() + dist_to_space_station) * Math.cos(teta) + tierra.getR(t)[0];
        y0 = (tierra.getRadius() + dist_to_space_station) * Math.sin(teta) + tierra.getR(t)[1];

        vx0 = tierra.getV(t)[0] + (rocket_blastoff_velocity + orbital_velocity_of_space_station) * Math.sin(teta);
        vy0 = tierra.getV(t)[1] + (rocket_blastoff_velocity + orbital_velocity_of_space_station) * Math.cos(teta);

        return new Body(t, rocket_mass, 0d, x0, y0, vx0, vy0, "Cohete");

    }

    private double force(double m1, double m2, double distance){
        return G*m1*m2/Math.pow(distance, 2);
    }


    private double[] force(double m1, double m2, Double[] r1, Double[] r2){
        double force, distance, teta;
        double [] decomp_force = new double[2];

        distance = Math.sqrt(Math.pow(r2[0]-r1[0], 2) + Math.pow(r2[1] - r1[1], 2));

        force = force(m1, m2, distance);

        teta = Math.atan2((r2[1] - r1[1]), (r2[0]-r1[0]));

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

                if(other_body.getName().equals("Cohete") && (t < dia_del_despegue))
                    aux_force = new double[]{0d, 0d};
                else
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

    //0) Calcular fuerza en t                                           DONE
    //1) calcular nuevas posiciones del sistema para t + delta_t        DONE
    //2) calcular nuevas velocidades del sistema para t + delta_t/2     DONE
    //3) calcular nuevas posiciones del sistema para t + delta_t/2      DONE
    //4) calcular fuerza en t + delta_t/2                               DONE
    //5) calcular nuevas velocidades del sistema para t + delta_t       DONE
    //6) calcular fuerza en t + delta_t                                 DONE
    //7) calcular nuevas velocidades del sistema para t + delta_t       DONE

    private void Verlet(double t, double delta_t){
        int size = celestial_bodies.size()-1;
        double[][] forces_t = new double[size][2];
        double[][] forces = new double[size][2];

        Body b;

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces_t[i] = calculateForce(b, t); //paso 0
        }

        moveSistWithForceInTVerlet(t, delta_t, forces_t, size); //pasos 1, 2 y 3

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces[i] = calculateForce(b, t + delta_t/2); //paso 4
        }

        newVelWithForceTPlusHalfDeltaT(t, delta_t, forces, size); //paso 5

        for(int i = 0; i < celestial_bodies.size()-1; i++){
            b = celestial_bodies.get(i+1);
            forces[i] = calculateForce(b, t + delta_t); //paso 6
        }

        newVelWithForceTPlusDeltaT(t, delta_t, forces, forces_t, size);   //paso 7
    }

    private void newVelWithForceTPlusDeltaT(double t, double delta_t, double [][] forces, double [][] forces_t, int size){
        Body b;
        Double[] v;
        Double[] new_v = new Double[size];

        for(int i = 0; i < celestial_bodies.size()-1; i++){

            b = celestial_bodies.get(i+1);
            v=b.getV(t);

            new_v[0] = v[0] + (delta_t / (2*b.getM())) * (forces_t[i][0] + forces[i][0]);
            new_v[1] = v[1] + (delta_t / (2*b.getM())) * (forces_t[i][1] + forces[i][1]);
            b.putV(t + delta_t, new_v);
        }
    }

    private void newVelWithForceTPlusHalfDeltaT(double t, double delta_t, double [][] forces, int size){
        Body b;
        Double[] v;
        Double[] new_v = new Double[size];

        for(int i = 0; i < celestial_bodies.size()-1; i++){

            b = celestial_bodies.get(i+1);
            v = b.getV(t + delta_t/2);

            new_v[0] = v[0] + (delta_t / (2*b.getM())) * forces[i][0];
            new_v[1] = v[1] + (delta_t / (2*b.getM())) * forces[i][1];
            b.putV(t + delta_t, new_v);
        }
    }

    private void moveSistWithForceInTVerlet(double t, double delta_t, double [][] forces, int size){
        Body b;
        Double[] r, v;
        Double[] new_r = new Double[size];
        Double[] new_v = new Double[size];
        Double[] aux_r = new Double[size];

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
        int size = celestial_bodies.size()-1;

        double[][] forces = new double[size][2];
        double[][] prev_a = new double[size][2];

        Double[][] current_r = new Double[size][2];
        Double[][] current_v = new Double[size][2];
        Double[][] current_a = new Double[size][2];

        Double[][] next_r = new Double[size+1][2];
        next_r[0] = celestial_bodies.get(0).getR(0);

        Double[][] next_v = new Double[size][2];
        double[][] next_a = new double[size][2];

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

    public List<String> getRocketSpeed() {
        rocket_speed.add(0, "Time,Speed");
        return rocket_speed;
    }
}
