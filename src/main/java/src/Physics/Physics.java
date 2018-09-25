package src.Physics;

// Own imports
import src.testing.VisualAStar;
//Java imports
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Physics {
    /**
     * Computes the position, velocity and rotation after a key input
     * using the position, velocity and rotation values before the input.
     * 
     * WORKING:
     * rotating + accelerating
     * updating its pStruct even when no key pressed
     * slowing down when no key is pressed
     * 
     * TODO:
     * implement simple collision
     * implement slowdown when off track (track + track-detection first)
     * 
     * TODO?:
     * completely stop movement when velocity close to 0
     *      - Already really close, when forcing: physics go haywire
     *      - Not really necessary
     * implement realistic collision
     *      - depends on how many hours we want to invest in this (claw?)
     * refine rotation physics (-rotVmax<->rotVmax) instead of (-rotVmax/0/rotVmax)
     *      - controller would then be necessary instead of just an option
     *      - Controller script also needs to support for range detection
     * refine rotation physics (increase rot velocity the longer you hold A/D)
     *      - Less ideal than previous idea
     *      - If implemented: should be subtle
     *      - 4-term rotation calculations -> 12-figure rotation calculations
     *      
     * 
     * 
     * turn: A/D => (1/-1)
     * acc: W/S => (1/-1)
     * a: max acc
     * rotV: max rot velocity (-max/0/max)
     * vMax: max lin velocity, (-max<->max)
     * tInt: time interval (key detection interval)
     * startStruct: begin position
     * velocity and rotation
     */
    public PStruct calcPhysics(int turn, int acc, double a, double rotV,
            double vMax, double tInt, PStruct startStruct) {
             
        // struct disection: input position, velocity, rotation before key input
        Point2D.Double startPos = startStruct.pos;
        double startRot = startStruct.rot;
        double startV = startStruct.v;
        // Used in physics calculations
        double distTravelled, eV, eRot;
        Point2D.Double ePos;
        
        // Max speed regulation
        if ((acc == 1 && startV + a*tInt > vMax) ||
                (acc == -1 && startV - a*tInt < -vMax)) {
            acc = 0;
        }
        
        // Friction abs(v) decrease
        if (acc == 0) {
            if (startV > 0) {
                a = -0.2 * a;
            } else if (startV < 0) {
                a = 0.2 * a;
            }
        }  else {
            a = (acc * a);
        }
        
        if (turn == 0) { // Straight
            distTravelled = startV * tInt + 0.5 * a * tInt * tInt;
            
            eV = startV + a * tInt;
            eRot = startRot;
            ePos = new Point2D.Double (
                    startPos.x + Math.cos(startRot) * distTravelled,
                    startPos.y + Math.sin(startRot) * distTravelled);

        } else { // Turn
            rotV = turn * rotV;
            
            eV = startV + a * tInt;
            eRot = startRot + rotV * tInt;
            double deltaX = + (eV / rotV) * Math.sin(eRot)
                        + (a / (rotV*rotV)) * Math.cos(eRot)
                        - (startV / rotV) * Math.sin(startRot)
                        - (a / (rotV*rotV)) * Math.cos(startRot);
            double deltaY = - (eV / rotV) * Math.cos(eRot)
                        + (a / (rotV*rotV)) * Math.sin(eRot)
                        + (startV / rotV) * Math.cos(startRot)
                        - (a / (rotV*rotV)) * Math.sin(startRot);
            ePos = new Point2D.Double(startPos.x + deltaX, startPos.y +deltaY);
        }
        // velocity should be 0 if really close
        /*if (Math.abs(eV) < 0.005) {
            eV = 0;
        }*/
        // new position, velocity and rotation after input
        return new PStruct(ePos, eV, eRot);
    }
    
    public static void physicsTestVisuals () {
        // Own class declarations
        VisualAStar visual = new VisualAStar();
        Physics physics = new Physics();
        // Test arraylist
        ArrayList<PStruct> testDrive = new ArrayList<>();
        
        // Start position, rotation and velocity
        PStruct currentStruct = new PStruct(new Point2D.Double(0,0), 0, 0);
        testDrive.add(currentStruct);
        
        // INSERT TEST COMMANDS
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.calcPhysics(1, 0, 1,
                    Math.PI/2, 2.01, 0.1, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, currentStruct);
            testDrive.add(currentStruct);
        }
        
        // Visualization
        for (int i = 0; i < testDrive.size(); i++) {
            System.out.println(new Point2D.Double(testDrive.get(i).pos.x, testDrive.get(i).pos.y));
            visual.addPoint(new Point2D.Double(testDrive.get(i).pos.x, -testDrive.get(i).pos.y));
        }
        visual.repaint();
    }
    
    public static void main(String[] args) {
        Physics.physicsTestVisuals();
    }
}

