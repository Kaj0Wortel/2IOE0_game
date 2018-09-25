package src.Physics;

// Own imports
import src.testing.VisualAStar;
//Java imports
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Physics {
    // turn: A/D, acc: W/S, a: max acc, rotV: max rot velocity, vMax: max lin velocity, 
    // tInt: time interval, startStruct: begin position, velocity and rotation
    public PStruct Physics (int turn, int acc, double a, double rotV, double vMax, double tInt, 
                    PStruct startStruct) {
        
        // Calculation constants
        double fricOffset = 0;        
        // struct disection
        Point2D.Double startPos = startStruct.pos;
        double startRot = startStruct.rot;
        double startV = startStruct.v;
        // Used in physics calculations
        double distTravelled, eV, eRot;
        Point2D.Double ePos;
        
        // Max speed regulation
        if ((acc == 1 && startV + a*tInt > vMax) || (acc == -1 && startV - a*tInt < -vMax)) {
            acc = 0;
        }
        // Friction abs(v) decrease
        if (acc == 0) {
            
        }
        
        a = (acc * a) - fricOffset;
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
            currentStruct = physics.Physics(0, 1, 1, Math.PI/2, 2.01, 0.1, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.Physics(1, 0, 1, Math.PI/2, 2.01, 0.1, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 4; i++) {
            currentStruct = physics.Physics(0, 1, 1, Math.PI/2, 2.01, 0.1, currentStruct);
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

