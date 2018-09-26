package src.Physics;

// Own imports
import src.testing.VisualAStar;
//Java imports
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.awt.geom.Point2D;

public class Physics {
    /**
     * Computes the position, velocity and rotation after a key input
     * using the position, velocity and rotation values before the input.
     * 
     * WORKING:
     * rotating + accelerating
     * updating its pStruct even when no key pressed
     * slowing down when no key is pressed
     * simple collision (cylinder colliders)
     * 
     * TODO:
     * ? a = 0 when collision bump is still busy?
     * implement falling when not on ground
     *      - tumbling over
     *      - point2d.doubles need to become vector2f?
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
            double vMax, double tInt, double fric, PStruct startStruct) {
             
        // struct disection: input position, velocity, rotation before key input
        Point2D.Double startPos = startStruct.pos;
        double startRot = startStruct.rot;
        double startV = startStruct.v;
        double colV = startStruct.colV;
        // Used in physics calculations
        double distTravelled, eV, eRot;
        Point2D.Double ePos;
        
        // Max speed regulation
        if ((acc == 1 && startV + a*tInt > vMax) ||
                (acc == -1 && startV - a*tInt < -vMax)) {
            acc = 0;
        }
        
        // Friction: abs(v) decreases when W/S are not pressed
        if (acc == 0) {
            if (startV > 0) {
                a = -fric * a;
            } else if (startV < 0) {
                a = fric * a;
            }
        }  else {
            a = (acc * a);
        } 
        
        // velocity should be 0 if really small
        /*if (Math.abs(startV) < 0.02) { // (for a = 0.4 (linear scale)))
            startV = 0;
        }*/
        
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
        
        
        //COLLISION TEST ZONE
        Point2D.Double colPos = new Point2D.Double(0.0001, 40);
        double colRange = 2;
        double carRange = 5;
        
        if (Math.sqrt(Math.pow(startPos.x - colPos.x, 2) 
                    + Math.pow(startPos.y - colPos.y, 2)) < (colRange + carRange)) {
            double colAngle = Math.atan2( startPos.x - colPos.x, startPos.y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            
            if (colV < 0.05) {
                colV = Math.abs(startV) * 0.05;
                ePos = new Point2D.Double(ePos.x + colV * 10 * Math.cos(colAngle), 
                    ePos.y + colV * 10 * Math.sin(colAngle));
            } 
        } else {
            colV = colV * 0.95;
            if (colV < 0.0000000005)
                colV = 0;
            
            // ANGLE WILL CHANGE AFTER BUMP: maybe looks better?
            double colAngle = Math.atan2( startPos.x - colPos.x, startPos.y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            
            ePos = new Point2D.Double(ePos.x + colV * 10 * Math.cos(colAngle), 
                    ePos.y + colV * 10 * Math.sin(colAngle));
        }
        //-------------------
        
        // new position, velocity and rotation after input
        return new PStruct(ePos, eV, eRot, colV);
    }
    
    public static void physicsTestVisuals () {
        // Own class declarations
        VisualAStar visual = new VisualAStar();
        Physics physics = new Physics();
        // Test arraylist
        ArrayList<PStruct> testDrive = new ArrayList<>();
        
        // Start position, rotation and velocity
        PStruct currentStruct = new PStruct(new Point2D.Double(0,0), 0, 0, 0);
        testDrive.add(currentStruct);
        
        // INSERT TEST COMMANDS
        for (int i = 0; i < 10; i++) {
            currentStruct = physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        /*for (int i = 0; i < 4; i++) {
            currentStruct = physics.calcPhysics(1, 0, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }*/
        
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

