package src.Progress;


// Own imports

import org.joml.Vector3f;
import src.tools.Cloneable;

// Java imports


public class ProgressManager
        implements Cloneable, Comparable<ProgressManager> {
    public int checkPoint = 1;
    public int lap = 1;
    public boolean finished = false;
    public int cpAm = 16; // modifiable
    public int lapTotal = 1; // modifiable
    
    
    public void manageProgress(Vector3f pos, int pointAmount, int curPoint) {
        if (curPoint > pointAmount * checkPoint / cpAm
                && curPoint < pointAmount * (checkPoint + 1) / cpAm && !finished)
            checkPoint++;
        else if (checkPoint >= cpAm) {
            if (curPoint > 0 && curPoint < pointAmount / cpAm) {
                lap++;
                checkPoint = 1;
                System.out.println("LAP " + lap);
                if (lap == lapTotal + 1)
                    finished = true;
            }
        }
        else if (finished);
    }
    
    @Override
    public ProgressManager clone() {
        ProgressManager clone = new ProgressManager();
        clone.checkPoint = this.checkPoint;
        clone.lap = this.lap;
        clone.finished = false;
        clone.cpAm = this.cpAm;
        clone.lapTotal = this.lapTotal;
        return clone;
    }
    
    @Override
    public int compareTo(ProgressManager other) {
        int thisValue = cpAm * lap + checkPoint;
        int otherValue = cpAm * other.lap + other.checkPoint;
        return otherValue - thisValue;
    }
    
    
}
