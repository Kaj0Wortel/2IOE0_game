package src.Progress;

// Java imports
import org.joml.Vector3f;

public class ProgressManager {
    public int checkPoint = 1;
    private int lap = 1;
    public boolean finished = false;
    public int cpAm = 16; // modifiable
    public int lapTotal = 1; // modifiable
    
    public void ManageProgress(Vector3f pos, int pointAmount, int curPoint) {
        if (curPoint > pointAmount * checkPoint / cpAm
                && curPoint < pointAmount * (checkPoint + 1) / cpAm && !finished)
            checkPoint++;
        else if (checkPoint == cpAm) {
            if (curPoint > 0 && curPoint < pointAmount / cpAm) {
                lap++;
                checkPoint = 1;
                System.out.println("LAP " + lap);
                if (lap == lapTotal + 1)
                    finished = true;
            }
        }
        else if (finished)
            System.out.println("FINISHED");
    }
}
