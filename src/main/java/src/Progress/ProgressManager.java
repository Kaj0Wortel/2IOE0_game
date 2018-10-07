package src.Progress;

// Java imports
import org.joml.Vector3f;

public class ProgressManager {
    public int checkPoint = 1;
    private int lap = 1;
    
    public int cpAm = 16;
    public boolean finished = false;
    
    public void ManageProgress(Vector3f pos, int pointAmount, int curPoint) {
        if (curPoint > pointAmount * checkPoint / cpAm
                && curPoint < pointAmount * (checkPoint + 1) / cpAm && !finished)
            checkPoint++;
        else if (checkPoint == cpAm) {
            if (curPoint > 0 && curPoint < pointAmount / cpAm) {
                lap++;
                checkPoint = 1;
                System.out.println("LAP " + lap);
                if (lap == 4)
                    finished = true;
            }
        }
        else if (finished)
            System.out.println("FINISHED");
    }
}
