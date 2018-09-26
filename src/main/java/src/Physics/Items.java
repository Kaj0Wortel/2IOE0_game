package src.Physics;

import java.awt.geom.Point2D;

public class Items {
    
    public double speedBoost(Point2D.Double startPos, double startV) {
        if (startPos.x + 3 > 40 - 0.5 &&
                40 + 0.5 > startPos.x - 3 &&
                startPos.y + 3 > 0 - 0.5 &&
                0 + 0.5 > startPos.y - 3) {
            return startV*2;
        }
        return startV;
    }
    
    public double SlowDownSpot (Point2D.Double startPos, double vMax) {
        if (startPos.x + 3 > -150 - 25 &&
                -150 + 25 > startPos.x - 3 &&
                startPos.y + 3 > 0 - 25 &&
                0 + 25 > startPos.y - 3) {
            return vMax/4;
        }
        return vMax;
    }
}
