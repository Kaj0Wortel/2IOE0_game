package src.Physics;

import org.joml.Vector3f;

public class Items {
    
    public double speedBoost(Vector3f startPos, double startV) {
        if (startPos.x + 3 > 40 - 0.5 &&
                40 + 0.5 > startPos.x - 3 &&
                startPos.y + 3 > 0 - 0.5 &&
                0 + 0.5 > startPos.y - 3) {
            System.out.println("NYOOM");
            return startV*2;
        }
        return startV;
    }
    
    public double SlowDownSpot (Vector3f startPos, double vMax) {
        if (startPos.x + 3 > 0 - 25 &&
                0 + 25 > startPos.x - 3 &&
                startPos.y + 3 > -100 - 25 &&
                -100 + 25 > startPos.y - 3) {
            return vMax/4;
        }
        return vMax;
    }
}
