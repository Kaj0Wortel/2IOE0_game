package src.Progress;

// Java imports
import org.joml.Vector3f;

public class ProgressManager {
    public int checkPoint = 0;
    private int lap = 1;
    
    public void ManageProgress(Vector3f pos, int pointAmount, int curPoint) {
        switch (checkPoint) {
            case 0:
                if (curPoint > pointAmount /4 && curPoint < pointAmount * 3/8)
                    checkPoint++;
                break;
            case 1:
                if (curPoint > pointAmount /2 && curPoint < pointAmount * 5/8)
                    checkPoint++;
                break;
            case 2:
                if (curPoint > pointAmount* 3/4 && curPoint < pointAmount * 7/8)
                    checkPoint++;
                break;
            case 3:
                if (curPoint > 0 && curPoint < pointAmount * 1 / 8) {
                    if (lap == 3) {
                        checkPoint++;
                    } else {
                        checkPoint = 0;
                        lap++;
                        System.out.println("LAP " + lap);
                    }
                }
                break;
            case 4:
                System.out.println("GOAL");
                break;
            default:
                System.err.println("WAIT WHY CHECKPOINT " + checkPoint);
                break;      
        }
    }
}
