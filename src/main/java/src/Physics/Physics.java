package src.Physics;

// Own imports
import src.testing.VisualAStar;
//Java imports
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
     * simple collision (rectangle colliders)
     * rotation correction for small velocities and negative velocities
     * 2 simple items/situations implemented
     * Large slowdown when speed higher than vMax 
     * 
     * 
     * TODO:
     * improved collision: differentiate between static/dynamic collision 
     *      - AI car necessary to test dynamic
     *      - long wall necessary to test static point-array-based static
     *      - Rotation necessary
     * implement falling when not on ground
     *      - tumbling over
     *      - point2d.doubles need to become vector2f?
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
        
        // INPUT
        double turnCorrection = 2; // How fast you have to go to reach rotVmax
        double knockback = 0.3; // How strong the knockback is
        double knockbackDur = 0.95; // The closer to 1, the longer the knockback
        double accBlockDur = 20; // The higher, the longer acceleration is blocked after colliding
        double largeSlowDown = 4; // Extra de-acceleration when velocity is too big
        
        // struct disection: input position, velocity, rotation before key input
        Point2D.Double startPos = startStruct.pos;
        double startRot = startStruct.rot;
        double startV = startStruct.v;
        double colV = startStruct.colV;
        // Used in physics calculations
        double distTravelled, eV, eRot;
        Point2D.Double ePos;
        
        
        // ITEMS
        Items items = new Items();
        // Speed boost
        if (Math.abs(startV) < vMax)
            startV = items.speedBoost(startPos, startV); // not refined
        // Slow down spot
        vMax = items.SlowDownSpot(startPos, vMax); // not refined
        
        // ACCELERATION
        // Max speed regulation
        if ((acc == 1 && startV + a*tInt > vMax) ||(acc == -1 && startV - a*tInt < -vMax))
            acc = 0;
        // Block manual acceleration when collision just happened
        if (colV > knockback / accBlockDur)
            acc = 0;
        
        // Temporary slowdown after speedboost: not refined
        if (startV + a*tInt > vMax * 1.1 || startV - a*tInt < -vMax * 1.1)
            a = (Math.abs(startV - vMax*1.1) + 1) * fric * a * largeSlowDown;
        
        // Friction: When acceleration is 0, abs(v) decreases
        if (acc == 0) {
            if (startV > 0)
                a = -fric * a;
            else if (startV < 0)
                a = fric * a;
        }  else
            a = (acc * a);
        
        //ROTATION
        // Turn correction for small velocities
        if (Math.abs(startV) < 0.05)
            turn = 0;
        else if (Math.abs(startV) < turnCorrection)
            rotV = (Math.abs(startV) * rotV / turnCorrection);
        
        // Turn correction for negative velocities
        if (startV < 0)
            turn = -turn;

        
        // END PSTRUCT CALCULATION
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
        
        // COLLISION CALCULATION
        // These should be integrated into other classes and sent to here
        Point2D.Double colPos = new Point2D.Double(0.0001, 40);
        double colRange = 2;
        double carRange = 6;
        // Collision detection
        if (startPos.x + carRange/2 > colPos.x - colRange/2 &&
                colPos.x + colRange/2 > startPos.x - carRange/2 &&
                startPos.y + carRange/2 > colPos.y - colRange/2 &&
                colPos.y + colRange/2 > startPos.y - carRange/2) {
            
            double colAngle = Math.atan2( startPos.x - colPos.x, startPos.y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            // Can only receive knockback once the last knockback is sufficiently small
            if (colV < 1) {
                colV = Math.abs(startV) * knockback;
                ePos = new Point2D.Double(ePos.x + colV * Math.cos(colAngle), 
                    ePos.y + colV * Math.sin(colAngle));
            } 
        } 
        // Moments after collision
        else if (colV > knockback/1000000000) {
            // Slowly diminish the knockback over time
            colV = colV * knockbackDur;
            // Angle can change during bump: maybe looks better?
            double colAngle = Math.atan2( startPos.x - colPos.x, startPos.y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            ePos = new Point2D.Double(ePos.x + colV * Math.cos(colAngle), 
                    ePos.y + colV * Math.sin(colAngle));
        } 
        // No collision happening
        else {
            // set collision velocity to 0 when it was already really small
            colV = 0;
        }

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

