package src.AI;

// Own imports
import src.testing.VisualAStar; // Debug visualization
import src.Physics.PStructAction;
import src.tools.Cloneable;


// Java imports
import java.awt.*; // 2D graphics helper
import java.awt.geom.Point2D; // Point2D.Doubles
import java.util.ArrayList; // Arralylists
import java.util.Collections; // Reverse Arraylist
import java.util.List; // ArrayLists
import org.joml.Vector3f;
import src.Assets.TextureImg;
import src.Assets.instance.Instance;
import src.Physics.Physics;
import src.Physics.Physics.ModState;
import src.Physics.PhysicsContext;
import src.racetrack.BezierTrack;
import src.tools.PosHitBox3f;


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
        BezierTrack track = new BezierTrack(new Vector3f(0,0,0),0,0,0,0,new TextureImg(0,0),new TextureImg(0,0));
        Physics.setTrack(track);
        // Project imports
        VisualAStar visual = new VisualAStar();
       //TEST
       Instance2 instance = new Instance2();
        
        // <editor-fold defaultstate="collapsed" desc="VARIABLES">
        // Checkpoints and start position
        List<Point2D.Double> checkPoints = new ArrayList<Point2D.Double>();        
        for (int i = 0; i < 1; i++) {
            checkPoints.add(new Point2D.Double(0, 10));
            
            //checkPoints.add(new Point2D.Double(8.5, 8.5));
            //checkPoints.add(new Point2D.Double(5.5, 8.5));
            //checkPoints.add(new Point2D.Double(5, 5.5));
            //checkPoints.add(new Point2D.Double(1.1, 5.3));
            //checkPoints.add(new Point2D.Double(1.4, 1.4));
            //checkPoints.add(new Point2D.Double(8.6, 1.4));
        }
        //checkPoints.add(new Point2D.Double(9, 6.2));
        //Point2D.Double firstPos = new Point2D.Double(9, 5);
        Point2D.Double firstPos = new Point2D.Double(-5, 0);
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
        FinalNode curNode = null;
        // </editor-fold>
        
        // The algorithm:
        System.out.println("---------------A* Debug---------------");
        // <editor-fold defaultstate="collapsed" desc="VISUALS">
        // Visuals for obstacle/void locations
        /*
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
        */
        // Initialize open- and closed-list
        ArrayList<FinalNode> openlist = new ArrayList<>();
        ArrayList<FinalNode> closedlist = new ArrayList<>();
        ArrayList<FinalNode> pathList = new ArrayList<>();
        
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

        FinalNode start = new FinalNode(startPos, 0, h, 0, instance.getState(), null);
        openlist.add(start);
        
        
        // LOOP: until openlist is empty left OR when the goal is reached.
        // <editor-fold defaultstate="collapsed" desc="LOOP">
        for (int iter = 0; !openlist.isEmpty() && iter < iterAllowed; iter++) {
            // Consider node with smallest f (g+h) in openlist.
            curNode = openlist.get(0);
            for (FinalNode o : openlist) {
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
                    //TEST: reach physiscs                        
                    float turn = i;
                    float acceleration = j;
                    float verticalVelocity = 0;
                    long dt = 16;
                    System.out.println("turn: " + turn + ", acc: " + acceleration);
                    System.out.println("pos: " + instance.getState().box.pos());
                    //System.out.println("on track?: " + instance.getState().onTrack);
                    PStructAction pStruct = new PStructAction(turn, acceleration, verticalVelocity, dt);

                    //ModState ms = new ModState(instance.getState());
                    instance.setState(curNode.state);

                    instance.movement(pStruct);

                    Point2D.Double sPos;
                    double sg, sh;
                    System.out.println("--"+instance.getState().box.pos()+"--");
                    sPos = new Point2D.Double (instance.getState().box.pos().x,
                            instance.getState().box.pos().y);
                    sg = curNode.g + curNode.state.velocity * tInt;

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
                    for (FinalNode o : openlist) {
                        if ((int)(10000*o.pos.x) == compPos.x && (int)(10000*o.pos.y) == compPos.y) {
                            alreadyInList = true;
                            /*System.err.println("Unexpected node found in "
                                    + "closed list: " + o);*/
                        }
                    }
                    //CASE II: already in open
                    for (FinalNode c : closedlist) {
                        if ((int)(10000*c.pos.x) == compPos.x && (int)(10000*c.pos.y) == compPos.y) {
                            alreadyInList = true;
                            System.err.println("Unexpected node found in "
                                    + "opened list: " + c);
                        }
                    }
                    //CASE III: not in any list
                    if (!alreadyInList) {
                        openlist.add(new FinalNode(sPos, /*sV, sA, sRot, sRotV,
                                sVertV,*/ sg, sh, sNextCP, instance.getState(), curNode));
                        // Debug succesor logs
                        if(iter == 0) {
                            System.out.println("-"+(iter+1)+"->h: " + sh
                                    + ", g: " + sg
                                    + ", X: " + sPos.x + ", Y: " + sPos.y);
                        }
                        // Debug visuals
                        if (iter < 50000 /*&& false*/) {
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
            
            // Show evaluated position
            if (curNode.parentNode != null) {
                System.out.println(iter + ": (" + curNode.pos.x + ", "
                    + curNode.pos.y + ")->" 
                    + curNode.h
                    //+ ", a = " + curNode.a 
                    //+ ", rotV = " + curNode.rotV
                    //+ ", v = " + curNode.v
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
            for (FinalNode p : pathList) {
                if (p.state.velocity > 0) {
                    visual.setForeground(Color.GREEN);
                } else if (p.state.velocity < 0) {
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
        FinalAStar.runAlgorithm();
    }
    
    
    
    public static class Instance2
            extends Instance
            implements Cloneable {
        final public Instance2 parent;
        
        
        public Instance2() {
            super(new PosHitBox3f(), 1f, 0, 0, 0, null, 0, new PhysicsContext());
            parent = null;
        }
        
        private Instance2(Instance2 inst) {
            super(new PosHitBox3f(), 1f, 0, 0, 0, null, 0, inst.physicsContext);
            setState(inst.getState());
            this.parent = inst;
        }
        
        @Override
        public boolean isStatic() {
            return true;
        }
        
        @Override
        public Instance2 clone() {
            return new Instance2(this);
        }
    } 
}