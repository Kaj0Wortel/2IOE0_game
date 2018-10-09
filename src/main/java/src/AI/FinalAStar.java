package src.AI;

// Own imports
import src.testing.VisualAStar; // Debug visualization
import src.Physics.Physics;
// Java imports
import java.awt.*; // 2D graphics helper
import java.awt.geom.Point2D; // Point2D.Doubles
import java.util.ArrayList; // Arralylists
import java.util.Collections; // Reverse Arraylist
import java.util.List; // ArrayLists

public class FinalAStar {
  // <editor-fold defaultstate="collapsed" desc="NOTES"> 
    /* 
    init open list
    init closed list
    fill list of checkpoints
    fill start position and rotation
    
    decide initial target, create startnode:
        -target = closest checkpoint...point to the current position
        -start node => initial values
    openlist[0] = start node
    
    loop: (while openlist.count != 0)
        -current node = smallest f node in openlist
        -closedlist.add current node
        -openlist.remove current node
        -change checkpoints if current node is on checkpoint
            -if current node is on goal: stop loop
        -find possible succesor nodes
            -node =>(pos,v,a,rot,rotv,g,h,nextCP,parentNode)
        -possible succesors fit in three cases:
            -already in closedlist -> ignore
            -already in openlist -> ignore and print
            -not in any list -> add to openlist (collision should go here)
    
    if path complete:
        -pathlist.add last node
        -pathlist.add node parents until back at the start
        -reverse pathlist
    profit
    
    SHOWCASE VERSION:
    checkPoints.add(new Point2D.Double(2, 1.5));
    checkPoints.add(new Point2D.Double(1.5, 1.1)); 
    checkPoints.add(new Point2D.Double(-0, 0.95));
    checkPoints.add(new Point2D.Double(0.5, 0.951));
    checkPoints.add(new Point2D.Double(0.6, 0));
    checkPoints.add(new Point2D.Double(0.601, -0.5));
    checkPoints.add(new Point2D.Double(2, 0));
    checkPoints.add(new Point2D.Double(2.5, -0.5)); 
    checkPoints.add(new Point2D.Double(2, 1.5));
    checkPoints.add(new Point2D.Double(1.5, 1.1));
    Point2D.Double startPos = new Point2D.Double(1.75, 1);
    
    visual.setForeground(Color.DARK_GRAY);
    Point2D.Double point1 = new Point2D.Double(0.5,-0);
    Point2D.Double point2 = new Point2D.Double(0.5,-2);
    Point2D.Double point3 = new Point2D.Double(1,-2);
    Point2D.Double point4 = new Point2D.Double(1,-0);
    visual.addRec(point1, point3);
    point1 = new Point2D.Double(-1,-0);
    point2 = new Point2D.Double(-1,-2);
    point3 = new Point2D.Double(-0.4,-2);
    point4 = new Point2D.Double(-0.4,-0);
    visual.addRec(point1, point3);
    point1 = new Point2D.Double(1,-0);
    point2 = new Point2D.Double(1,-0.5);
    point3 = new Point2D.Double(2,-0.5);
    point4 = new Point2D.Double(2,-0);
    visual.addRec(point1, point3);
    point1 = new Point2D.Double(1,-1.1);
    point2 = new Point2D.Double(1,-2);
    point3 = new Point2D.Double(1.5,-0.5);
    point4 = new Point2D.Double(1.5,-0.5);
    visual.addRec(point1, point3);
    
    if (sPos.x > 0.5 && sPos.x < 1 && sPos.y > 0 && sPos.y < 2) {
        sg = sg + 10;
    } else if (sPos.x > -1 && sPos.x < -0.4 && sPos.y > 0 && sPos.y < 2) {
        sg = sg + 10;
    }
    */ // </editor-fold>
    public static void runAlgorithm () {
        // Project imports
        VisualAStar visual = new VisualAStar();
       // Physics physics = new Physics();
        
        // <editor-fold defaultstate="collapsed" desc="VARIABLES">
        // Checkpoints and start position
        List<Point2D.Double> checkPoints = new ArrayList<Point2D.Double>();        
        for (int i = 0; i < 1; i++) {
            checkPoints.add(new Point2D.Double(8.5, 8.5));
            checkPoints.add(new Point2D.Double(5.5, 8.5));
            checkPoints.add(new Point2D.Double(5, 5.5));
            checkPoints.add(new Point2D.Double(1.1, 5.3));
            checkPoints.add(new Point2D.Double(1.4, 1.4));
            checkPoints.add(new Point2D.Double(8.6, 1.4));
        }
        checkPoints.add(new Point2D.Double(9, 6.2));
        Point2D.Double firstPos = new Point2D.Double(9, 5);
        Point2D.Double startPos = firstPos;
        
        // Maximum velocity (can be something else than +/-max or 0)
        double vMax = 2.01;
        // Maximum acceleration
        double a = 1;
        // Maximum angular velocity (rad/sec)
        double rotvMax = Math.PI/2;
        // Time interval between actions/itterations
        double tInt = 0.1;
        // Amount of itterations/time allowed per pathfind
        int iterAllowed = 50000;
        
        // Loop variables
        boolean alreadyInList = false;
        boolean pathComplete = false;
        boolean reachedCP = false;
        Node curNode = null;
        // </editor-fold>
        
        // The algorithm:
        System.out.println("---------------A* Debug---------------");
        // <editor-fold defaultstate="collapsed" desc="VISUALS">
        // Visuals for obstacle/void locations
            visual.setForeground(Color.DARK_GRAY);
            Point2D.Double point1 = new Point2D.Double(6,-2);
            Point2D.Double point2 = new Point2D.Double(6,-8);
            Point2D.Double point3 = new Point2D.Double(8,-8);
            Point2D.Double point4 = new Point2D.Double(8,-2);
            visual.addRec(point1, point3);
            point1 = new Point2D.Double(2,-2);
            point2 = new Point2D.Double(2,-5);
            point3 = new Point2D.Double(6,-5);
            point4 = new Point2D.Double(6,-2);
            visual.addRec(point1, point3);
            point1 = new Point2D.Double(3,-6);
            point2 = new Point2D.Double(3,-10);
            point3 = new Point2D.Double(4,-10);
            point4 = new Point2D.Double(4,-6);
            visual.addRec(point1, point3);
        // Visuals for checkpoint locations
            visual.setForeground(Color.CYAN);
            for (Point2D.Double cp : checkPoints){
                if (checkPoints.indexOf(cp) < checkPoints.size() - 1)
                    visual.addPointBig (new Point2D.Double(cp.x, -cp.y));
            }
        // Visuals for finish location
            visual.setForeground(Color.CYAN);
            visual.addLine(new Point2D.Double(8,-5), new Point2D.Double(10,-5));
        // </editor-fold>
            
        // Initialize open- and closed-list
        ArrayList<Node> openlist = new ArrayList<>();
        ArrayList<Node> closedlist = new ArrayList<>();
        ArrayList<Node> pathList = new ArrayList<>();
        
        // The target can change often (see notes for H above). 
        // Therefore, target is decided on each itteration inside the loop
        double h = 0;
        Point2D.Double curHPos = startPos;
        for (int i = 0; i < checkPoints.size(); i++) {
            Point2D.Double CP = checkPoints.get(i);
            h = h + Math.sqrt(Math.pow(curHPos.x - CP.x, 2)
                        + Math.pow(curHPos.y - CP.y, 2));
            curHPos = CP;
        }

        Node start = new Node(startPos, 0, 0, Math.PI/2, 0, 0, h, 0, null);
        openlist.add(start);
        
        // LOOP: until openlist is empty left OR when the goal is reached.
        // <editor-fold defaultstate="collapsed" desc="LOOP">
        for (int iter = 0; !openlist.isEmpty() && iter < iterAllowed; iter++) {
            // Consider node with smallest f (g+h) in openlist.
            curNode = openlist.get(0);
            for (Node o : openlist) {
                if (o.nextCP > curNode.nextCP) {
                    reachedCP = true;
                    curNode = o;
                } else if (o.g + o.h < curNode.g + curNode.h && o.nextCP >= curNode.nextCP) {
                    curNode = o;
                }
            }
            
            // curNode is added to the closed list.
            closedlist.add(curNode);
            openlist.remove(curNode);
            // Check if the current node has reached the target
            if (curNode.h < 0.2) { // ? does this number even matter?
                pathList.add(curNode);
                pathComplete = true;
                // Goal print statement
                System.out.println("COMPLETE: (" + firstPos.x + ", " + firstPos.y
                + ")->(" + curNode.pos.x + ", " + curNode.pos.y + "), with " +
                iter + "iterations and ");
                break;
            }
            // After checkpoint has been reached
            if (reachedCP) {
                openlist.clear();
                startPos = curNode.pos;
                System.out.println("Checkpoint reached: " + curNode.nextCP);
            }
            
            // i > turning: right, straight or left
            for (int i = -1; i <= 1; i++) {
                // j > Velocity: decelerate, constant, accelerate
                for (int j = -1; j <= 1; j++) {
                    // <editor-fold defaultstate="collapsed" desc="PHYSICS">
                    if (!(j == 1 && curNode.v + a*tInt > vMax)) {
                        // Calculate succesor node variables
                        Point2D.Double sPos;
                        // Used in Node
                        double sV, sA, sRot, sRotV, sg, sh;
                        // Not used in Node
                        double deltaRot, r, sr, s_deltaX, s_deltaY;
                        double distTravelled = curNode.v * tInt + 0.5*(j*a)*tInt*tInt;
                        if (reachedCP) {
                            sg = curNode.g +curNode.v * tInt; //+distTravelled
                        } else {
                            sg = curNode.g + curNode.v * tInt;//+ distTravelled;
                        }
                        if (i == 0) { // AI goes straight
                            sV = curNode.v + (j*a) * tInt;
                            sA = j * a;
                            sRot = curNode.rot;
                            sRotV = 0;
                            sPos = new Point2D.Double(
                                curNode.pos.x + Math.cos(curNode.rot)*distTravelled,
                                curNode.pos.y + Math.sin(curNode.rot)*distTravelled
                            );
                        } else { //AI turns
                            deltaRot = tInt*(i*rotvMax);
                            sV = curNode.v + (j*a)*tInt;
                            sA = j*a;
                            sRot = curNode.rot + deltaRot;
                            sRotV = i*rotvMax;
                            // Position
                            double sY = - (sV / sRotV)
                                            * Math.cos(curNode.rot + sRotV*tInt)
                                        + (sA / (sRotV*sRotV))
                                            * Math.sin(curNode.rot + sRotV*tInt)
                                        + (curNode.v / sRotV)
                                            * Math.cos(curNode.rot)
                                        - (sA / (sRotV*sRotV))
                                            * Math.sin(curNode.rot);
                            double sX = + (sV / sRotV)
                                            * Math.sin(curNode.rot + sRotV*tInt)
                                        + (sA / (sRotV*sRotV))
                                            * Math.cos(curNode.rot + sRotV*tInt)
                                        - (curNode.v / sRotV)
                                            * Math.sin(curNode.rot)
                                        - (sA / (sRotV*sRotV))
                                            * Math.cos(curNode.rot);
                            sPos = new Point2D.Double(
                                    curNode.pos.x + sX,
                                    curNode.pos.y + sY);
                        }

                        //Extra costs if new position is off track
                        if (sPos.x > 6 && sPos.x < 8 && sPos.y > 2 && sPos.y < 8) {
                            sg = sg + 10;
                        } else if (sPos.x > 2 && sPos.x < 6 && sPos.y > 2 && sPos.y < 5) {
                            sg = sg + 10;
                        } else if (sPos.x > 3 && sPos.x < 4 && sPos.y > 6 && sPos.y < 10) {
                            sg = sg + 10;
                        }
                    // </editor-fold>
                        
                        // Determine h  
                        sh = 0;
                        curHPos = sPos;
                        int sNextCP = curNode.nextCP;
                        reachedCP = false;
                        // Iterate through all, but only count the first two checkpoint
                        for (int k = sNextCP; k < checkPoints.size(); k++) {
                            Point2D.Double CP = checkPoints.get(k);
                            if (sNextCP == k) {
                                sh = sh + 1.25*Math.sqrt(Math.pow(curHPos.x - CP.x, 2)
                                    + Math.pow(curHPos.y - CP.y, 2));
                            } else if (sNextCP + 1 == k) {
                                sh = sh + 0.2*Math.sqrt(Math.pow(curHPos.x - CP.x, 2)
                                    + Math.pow(curHPos.y - CP.y, 2));
                            }                            
                            // Check if close to currently closest checkpoint
                            if (sh < 1.75) {
                                sNextCP++;
                                reachedCP = true;
                            }
                        }

                        //CASE I: already in closed
                        Point2D.Double compPos = new Point2D.Double
                            ((int)(10000*sPos.x), (int)(10000*sPos.y));
                        for (Node o : openlist) {
                            if ((int)(10000*o.pos.x) == compPos.x && (int)(10000*o.pos.y) == compPos.y) {
                                alreadyInList = true;
                                /*System.err.println("Unexpected node found in "
                                        + "closed list: " + o);*/
                            }
                        }
                        //CASE II: already in open
                        for (Node c : closedlist) {
                            if ((int)(10000*c.pos.x) == compPos.x && (int)(10000*c.pos.y) == compPos.y) {
                                alreadyInList = true;
                                System.err.println("Unexpected node found in "
                                        + "opened list: " + c);
                            }
                        }
                        //CASE III: not in any list
                        if (!alreadyInList) {
                            openlist.add(new Node(sPos, sV, sA, sRot, sRotV,
                                    sg, sh, sNextCP, curNode));
                            // Debug succesor logs
                            if(iter == 22) {
                                System.out.println("-"+(iter+1)+"->a: " + sA 
                                        + ", rotV: " + sRotV 
                                        + ", h: " + sh
                                        + ", g: " + sg
                                        + ", X: " + sPos.x + ", Y: " + sPos.y
                                        + ", Rot: "+sRot);
                            }
                            // Debug visuals
                            if (iter < 50000 && false) {
                                visual.setForeground(Color.LIGHT_GRAY);
                                visual.addPoint(new Point2D.Double(sPos.x, -sPos.y));
                                visual.setForeground(Color.WHITE);
                                visual.addPoint(new Point2D.Double(curNode.pos.x, -curNode.pos.y));
                            }
                        }
                        // Reset flag.
                        alreadyInList = false;
                    }
                }
            }
            
            // Show evaluated position
            if (curNode.parentNode != null) {
                System.out.println(iter + ": (" + curNode.pos.x + ", "
                    + curNode.pos.y + ")->" 
                    + curNode.h
                    + ", a = " + curNode.a 
                    + ", rotV = " + curNode.rotV
                    + ", v = " + curNode.v
                    //+ ", rot = " + curNode.rot
                    + ", g = " + curNode.g
                    //+", parent: ("+curNode.parentNode.pos.x+","+curNode.parentNode.pos.y+")"
                );
            }
            if (iter%1000 == 0)
                System.out.println(iter);
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="PATH CREATION">
        // If goal could be reached, create a path to it.
        if (pathComplete) {
            // Pathlist is created
            int test = 0;
            while (curNode.parentNode != null) {
                curNode = curNode.parentNode;
                pathList.add(curNode);
                test++;
            }
            // Goal print statement
            System.out.print (test + " nodes");
            // The path is greener the more it accelerates  
            for (Node p : pathList) {
                if (p.a > 0) {
                    visual.setForeground(Color.GREEN);
                } else if (p.a < 0) {
                    visual.setForeground(Color.RED);
                } else {
                    visual.setForeground(Color.YELLOW);
                }
                visual.addPoint(new Point2D.Double(p.pos.x, -p.pos.y));
            }
            //Visualize start and end point
            visual.setForeground(Color.GREEN);
            visual.addPointBig(new Point2D.Double (firstPos.x, -firstPos.y));
            visual.setForeground(Color.RED);
            visual.addPointBig(new Point2D.Double(pathList.get(0).pos.x, 
                    -pathList.get(0).pos.y));
            // First path element should be start, not end
            Collections.reverse(pathList);
            
        } else {
            System.err.println("NO PATH COULD BE MADE");
        }
        visual.repaint();
        System.out.println("--------------------------------------");
        // </editor-fold>
    }
    
    public static void main(String[] args) {
        AStarPointers.runAlgorithm();
    }
}