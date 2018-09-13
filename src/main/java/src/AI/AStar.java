
package src.AI;


// Own imports

import src.testing.VisualAStar;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Java imports

public class AStar {
    // <editor-fold defaultstate="collapsed" desc="NOTES"> 
    /* 
    init open list
    init closed list
    fill list of checkpoints
    fill start position and rotation
    
    decide initial target, create startnode:
        -target = closest checkpoint...point to the current position
        -start node => (position, g, h to target, null)
    openlist[0] = start node
    
    loop: (while openlist.count != 0)
        -current node = smallest f node in openlist
        -closedlist.add current node
        -openlist.remove current node
        -change checkpoints if current node is on checkpoint
            -if current node is on goal: stop loop
        -find possible succesor nodes
            -node =>(pos,v,a,rot,rotv,g,h,parentNode)
        -possible succesors fit in three cases:
            -already in closedlist -> ignore
            -already in openlist -> compare new and old route to node and choose cheapest
            -not in any list -> add to openlist (collision should go here)
    
    if path complete:
        -pathlist.add last node
        -pathlist.add node parents until back at the start
        -reverse pathlist
    profit
    
    
    NOTES:
    - for H: split possible positions into three (ONLY REQUIRES THE EDGE POINTS OF THE CHECKPOINTS): 
        - above the normals of the edge points of the checkpoint
        - between the normals (shortest line to checkpoint is just a normal from somewhere on the line
        - below the normals (shortest line to lowest checkpoint point)
        - Possible improvement: https://stackoverflow.com/questions/849211/shortest-distance-between-a-point-and-a-line-segment
            -explanation: point Q, line between P and P'. Projection of Q on line = Q'
             if D(Q,Q') is inside segment: D(Q,Q') is the distance
             else min( D(Q,P), D(Q,P')) is the distance
    - Are cases I and II really necessary? 
        - positions need to be exactly the same
        - only usefull for blackhole states: when it repeats the same (2) states forever
    
    TODO:
    - improve loop system
        - never go above/below max/min velocity
        - rotation with acceleration is still wrong
            -acceleration has been temporarily removed
        - scan for checkpoint proximity: update target or terminate loop
        - What should output look like? (probs node with acc, velo, pos and rot)
            - shorten the #node variables in pathlist: only necessary output
        - improve weights
        - g calculation with boundary detection
    - if checkpoint perfectly horizontal/vertical: nA or A = infinite: fix h for this situation
    - implement friendly input for the A* (info comes from other files)
    - shorten debug doubles to 4 decimals: https://docs.oracle.com/javase/tutorial/java/data/numberformat.html
    */ // </editor-fold>

    public static void runAlgorithm () {
        // <editor-fold defaultstate="collapsed" desc="VARIABLES">
        // TODO: add the following input variables:
        List<Point2D.Double> checkPoints = new ArrayList<Point2D.Double>();
        checkPoints.add(new Point2D.Double(0, 0));
        checkPoints.add(new Point2D.Double(0.1, 0.1));
        Point2D.Double startPos = new Point2D.Double(2, 0);
        
        // Maximum velocity (can be something else than +/-max or 0
        double vMax; // TODO REMARK: not used?
        // Maximum acceleration
        double a = 0; // TODO REMARK: make constant.
        // Maximum angular velocity (rad/sec)
        double rotvMax = Math.PI/2; // TODO REMARK: make constant.
        // Time interval between actions/itterations
        double tInt = 0.1; // TODO REMARK: make constant.
        // Amount of itterations/time allowed per pathfind
        int iterAllowed = 500000; // TODO REMARK: make constant.
        
        // succesor node variables
        //Point2D.Double s_Pos; // Used in Node
        //double s_V, s_A, s_Rot, s_RotV, s_g, s_h; // Used in Node
        //double deltaRot, r, s_r, s_deltaX, s_deltaY; // Not used in Node
        
        // Loop variables
        boolean alreadyInList = false;
        boolean pathComplete = false;
        Node curNode = null;
        // </editor-fold>
        
        
        // The algorithm:
        System.out.println("---------------A* Debug---------------");
        //DecimalFormat four = new DecimalFormat("#0.0000");
        VisualAStar visual = new VisualAStar();
        
        // Initialize open- and closed-list
        ArrayList<Node> openlist = new ArrayList<>();
        ArrayList<Node> closedlist = new ArrayList<>();
        ArrayList<Node> pathList = new ArrayList<>();
        
        // The target can change often (see notes for H above). 
        // Therefore, target is decided on each itteration inside the loop
        // <editor-fold defaultstate="collapsed" desc="TARGET DISTANCE CALCULATION">
        // Define checkpoints and normal lines
        Point2D.Double CP1 = checkPoints.get(0);
        Point2D.Double CP2 = checkPoints.get(1);
        // Normal lines: aX + bY + c = 0, [0] = X1 or X2, [1] = Y1 or Y2
        double nA = -(CP1.x - CP2.x) / (CP1.y - CP2.y); // nB is always -1
        double nC1 = -CP1.x*nA + CP1.y;
        double nC2 = -CP2.x*nA + CP2.y;
        // Distance from start point to both normal lines:
        // d = |a*sX + b*sY + c| / sqrt(a^2 + b^2).
        double d1 = Math.abs(nA*startPos.x - startPos.y + nC1)
                / Math.sqrt(nA*nA + 1);
        double d2 = Math.abs(nA*startPos.x - startPos.y + nC2)
                / Math.sqrt(nA*nA + 1);
        
        // Decide on target
        double difError = Math.sqrt(
                Math.pow(CP1.x - CP2.x, 2)
                + Math.pow(CP1.y - CP2.y, 2));
        double h;
        if (d1 > difError && d1 > d2) {
            h = Math.sqrt(Math.pow(startPos.x - CP2.x, 2)
                    + Math.pow(startPos.y - CP2.y, 2));
            
        } else if (d2 > difError && d1 < d2) {
            h = Math.sqrt(Math.pow(startPos.x - CP1.x, 2
                    + Math.pow(startPos.y - CP1.y, 2)));
            
        } else {
            double c = -CP1.x*(-1 / nA) + CP1.y;
            h = Math.abs((-1 / nA)*startPos.x - startPos.y + c)
                    / Math.sqrt(1 / (nA*nA) + 1);
        }
        // </editor-fold>

        Node start = new Node(startPos, 1, 0, Math.PI, 0, 0, h, null);
        openlist.add(start);
        
        // LOOP: until openlist is empty left OR when the goal is reached.
        // <editor-fold defaultstate="collapsed" desc="LOOP">
        for (int iter = 0; !openlist.isEmpty() && iter < iterAllowed; iter++) {
            // Consider node with smallest {@code f} in openlist.
            curNode = openlist.get(0);
            for (Node o : openlist) {
                if (o.g + o.h < curNode.g + curNode.h) {
                    curNode = o;
                }
            }
            
            // curNode is added to the closed list.
            closedlist.add(curNode);
            openlist.remove(curNode);
            
            // Check if the current node is close enough to the target.
            if (curNode.h < 0.05) { // Should be refined at some point
                pathList.add(curNode);
                // Show evaluated position
                /*System.out.println(iter + ": (" + curNode.pos.x + ", "
                        + curNode.pos.y + ")->" + curNode.h);*/
                System.out.println(iter);
                pathComplete = true;
                break;
            }
            
            // i > turning: right, straight or left
            for (int i = -1; i <= 1; i++) {
                // j > Velocity: decelerate, constant, accelerate
                //for (int j = -1; j <= 1; j++) {
                int j = 0;
                    // Calculate succesor node variables
                    Point2D.Double sPos;
                    // Used in Node
                    double sV, sA, sRot, sRotV, sg, sh;
                    // Not used in Node
                    double deltaRot, r, sr, s_deltaX, s_deltaY;
                    if (i == 0) { // AI goes straight
                        sg = curNode.v * tInt + 0.5 * (j*a) * tInt * tInt;
                        sPos = new Point2D.Double(
                                curNode.pos.x + Math.cos(curNode.rot)*sg,
                                curNode.pos.y + Math.sin(curNode.rot)*sg
                        );
                        sV = curNode.v + (j*a) * tInt;
                        sA = j * a;
                        sRot = curNode.rot;
                        sRotV = 0;
                        
                    } else { //AI turns
                        deltaRot = tInt*(i*rotvMax);
                        sV = curNode.v + (j*a)*tInt;
                        sA = j*a;
                        sRot = curNode.rot + deltaRot;
                        sRotV = i*rotvMax;
                        r = (sV/Math.abs(sV))*Math.abs(curNode.v/(i*rotvMax));
                        sr = (sV/Math.abs(sV))*Math.abs(sV/(i*rotvMax));
                        
                        // Position
                        /*s_deltaX = ((sV*sV-curNode.v*curNode.v)/(2*sRotV*sRotV))
                                *(Math.sin(tInt*sRotV) - Math.sin(curNode.rot));
                        s_deltaY = -((sV*sV-curNode.v*curNode.v)/(2*sRotV*sRotV))
                                *(Math.cos(tInt*sRotV) - Math.cos(curNode.rot));
                        sPos = new Point2D.Double(
                                curNode.pos.x + s_deltaX,
                                curNode.pos.y + s_deltaY*/
                        s_deltaX = r * Math.cos(curNode.rot + i*Math.PI/2)
                                + sr * Math.cos(sRot - i*Math.PI/2);
                        s_deltaY = r * Math.sin(curNode.rot + i*Math.PI/2)
                                + sr * Math.sin(sRot - i*Math.PI/2);
                        sPos = new Point2D.Double(
                                curNode.pos.x + s_deltaX,
                                curNode.pos.y + s_deltaY
                        );
                    }
                    // Determine g.
                    // spatial displacement != distance between
                    //     curNode and succesor
                    sg = curNode.v*tInt + 0.5*(j*a)*tInt*tInt;
                    //Extra costs if new position is off track
                    if (sPos.x > 0.5 && sPos.x < 1 && sPos.y > 0 && sPos.y < 2) {
                        sg = sg + 10;
                    }
                    
                    
                    // Determine h
                    // Define checkpoints and normal lines
                    CP1 = checkPoints.get(0);
                    CP2 = checkPoints.get(1);
                    
                    // Normal lines: aX + bY + c = 0, [0] = X1 or
                    // X2, [1] = Y1 or Y2
                    nA = -(CP1.x - CP2.x) / (CP1.y - CP2.y);// nB is always -1
                    nC1 = -CP1.x * nA + CP1.y;
                    nC2 = -CP2.x * nA + CP2.y;
                    
                    // Distance from start point to both normal lines:
                    // d = abs(a*sX + b*sY + c) / sqrt(a^2 + b^2)
                    d1 = Math.abs(nA*sPos.x - sPos.y + nC1)
                            / Math.sqrt(nA*nA + 1);
                    d2 = Math.abs(nA*sPos.x - sPos.y + nC2) 
                            / Math.sqrt(nA*nA + 1);
                    // Decide on target
                    difError = Math.sqrt(Math.pow(CP1.x - CP2.x, 2)
                            + Math.pow(CP1.y - CP2.y, 2));
                    if (d1 > difError && d1 > d2) {
                        sh = Math.sqrt(Math.pow(sPos.x - CP2.x, 2)
                                + Math.pow(sPos.y - CP2.y, 2));
                        
                    } else if (d2 > difError && d1 < d2){
                        sh = Math.sqrt(Math.pow(sPos.x - CP1.x, 2)
                                + Math.pow(sPos.y - CP1.y, 2));
                    } else {
                        double C = -CP1.x * (-1/nA) + CP1.y;
                        sh = Math.abs((-1 / nA) * sPos.x - sPos.y + C)
                                / Math.sqrt(1 / (nA*nA) + 1);
                    }
                    
                    // TODO REMARK: code smell over here.
                    //CASE I: already in closed
                    for (Node o : openlist) {
                        if (o.pos == sPos) {
                            alreadyInList = true;
                            System.err.println("Unexpected node found in "
                                    + "closed list: " + o);
                            //System.out.println("already in closed!?!");
                        }
                    }
                    
                    // TODO REMARK: code smell over here.
                    //CASE II: already in open
                    for (Node o : closedlist) {
                        if (o.pos == sPos) {
                            alreadyInList = true;
                            //TODO: (not easy): replace node if succ.g&h < o.g&h
                            // difficult because node after o might not be reachable from
                            // succ: the entire rest of the route has to be recalculated
                            System.err.println("Unexpected node found in "
                                    + "opened list: " + o);
                            //System.out.println("already in open?!?!");
                        }
                    }
                    
                    //CASE III: not in any list
                    if (!alreadyInList) {
                        openlist.add(new Node(sPos, sV, sA, sRot, sRotV,
                                sg, sh, curNode));
                        //TODO: remove after debug
                        if(iter == 0) {
                            System.out.println("-"+(iter+1)+"->a: " + sA + ", rotV: " 
                                    + sRotV + ", h: " + sh+", X: "+sPos.x+", Y: "+sPos.y+
                                    ", Rot: "+sRot);
                            if (sRotV == 0 || true) {
                                visual.setForeground(Color.YELLOW);
                                visual.addPoint(new Point2D.Double(sPos.x, -sPos.y));
                                visual.setForeground(Color.WHITE);
                            }
                        }
                    }
                    
                    // Reset flag.
                    alreadyInList = false;
                //}
            }
            
            // Show evaluated position
            if (curNode.parentNode != null) {
                /*System.out.println(iter + ": (" + curNode.pos.x + ", "
                    + curNode.pos.y + ")->" + curNode.h
                    + ". a = " + curNode.a + ", rotV = " + curNode.rotV +
                    ", v = " + curNode.v + ", rot = " + curNode.rot
                    +", parent: ("+curNode.parentNode.pos.x+","+curNode.parentNode.pos.y+")");
            */
            }
        }
        // </editor-fold>
        
        // If goal could be reached, create a path to it.
        if (pathComplete) {
            
            int test = 0;
            while (curNode.parentNode != null) {
                curNode = curNode.parentNode;
                pathList.add(curNode);
                test++;
            }
            System.out.println("COMPLETE: (" + startPos.x + ", " + startPos.y
                + ")->(" + curNode.pos.x + ", " + curNode.pos.y + "), with "
                + test + " nodes");
                        
            // TODO: remove temporary visualisation.
            //VisualAStar visual = new VisualAStar();
            visual.addNodes(pathList);
            visual.setForeground(Color.GREEN);
            //visual.addPoint(pathList.get(pathList.size() - 1).pos);
            visual.addPoint(new Point2D.Double(
                    pathList.get(pathList.size() - 1).pos.x, 
                    -pathList.get(pathList.size() - 1).pos.y));
            visual.setForeground(Color.RED);
            //visual.addPoint(pathList.get(0).pos);
            visual.addPoint(new Point2D.Double(pathList.get(0).pos.x, 
                    -pathList.get(0).pos.y));
            
            visual.setForeground(Color.ORANGE);
            for (Point2D.Double point : checkPoints) {
                visual.addPoint(new Point2D.Double(point.x, -point.y));
            }
            // Visuals for obstacle/void location
            visual.setForeground(Color.BLUE);
            visual.addPoint(new Point2D.Double(0.5,-0));
            visual.addPoint(new Point2D.Double(0.5,-2));
            visual.addPoint(new Point2D.Double(1,-2));
            visual.addPoint(new Point2D.Double(1,-0));
            visual.repaint();
            
            
            Collections.reverse(pathList);
            
            //pathComplete = false; // necessary if looped
        } else {
            System.err.println("NO PATH COULD BE MADE");
        }
        System.out.println("--------------------------------------");
    }
    
    public static void main(String[] args) {
        AStar.runAlgorithm();
    }
    
    
}
/*
    8 and 9 septempber
driving physics                                                         2 hr
AI input+output analysis                                                0.5 hr
A* (structure, H calculation, )                                         7 hr
case-by-case debugging                                                  1 hr
    10 september
working with the group:
-brainstorm, pathfind debugging, concrete program-structure setup       4 hr
    11, 12 and 13 september
driving physics improvement                                             2 hr
A* improvement and debugging                                            5 hr
*/