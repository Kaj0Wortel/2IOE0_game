package src.AI;

// Own imports
import src.testing.VisualAStar; // Debug visualization
// Java imports
import java.awt.*; // 2D graphics helper
import java.awt.geom.Point2D; // Point2D.Doubles
import java.io.IOException;
import java.util.ArrayList; // Arralylists
import java.util.Collections; // Reverse Arraylist
import java.util.List; // ArrayLists
import org.joml.Vector3f;
import src.Assets.TextureImg;
import src.Progress.ProgressManager;
import src.racetrack.BezierTrack;
import src.racetrack.Track;
import src.tools.io.NodeWriter;
import src.tools.log.Logger;

public class AStarPointersGlueFinal {
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
    
    // Necessary for determining if on track
    final private static int POINTS_PER_SEGMENT = 500;
    private static Vector3f[] points = new Vector3f[0];
    private static Vector3f[] normals = new Vector3f[0];
    private static Vector3f[] tangents = new Vector3f[0];
    private static float trackSize = 0;
    private static float trackWidth = 0;
    
    public static void runAlgorithm () {
        // Project imports
        VisualAStar visual = new VisualAStar();
        BezierTrack track = new BezierTrack(new Vector3f(0,0,0),0,0,0,0,new TextureImg(0,0),new TextureImg(0,0));
        setTrack(track);
        ProgressManager progress = new ProgressManager();
        
        // <editor-fold defaultstate="collapsed" desc="VARIABLES">
        // Checkpoints and start position
        List<Point2D.Double> checkPoints = new ArrayList<Point2D.Double>();        
        for (int i = 0; i < 1; i++) {
            for (int k = 100; k < (int)(points.length*1f); k = k + 100) {
                checkPoints.add(new Point2D.Double(points[k].x, points[k].y));
            }
        }
        Point2D.Double firstPos = new Point2D.Double(points[0].x, points[0].y);
        Point2D.Double startPos = firstPos;
        
        
        // Time interval between actions/itterations
        float tInt = 0.1f;
        // Amount of itterations/time allowed per pathfind
        int iterAllowed = 50000;
        
        // Physics context
        float a = 0.8f; // Maximum acceleration
        float rotvMax = (float) Math.PI / 14f; // Maximum angular velocity (rad/sec)
        float vMax = 8/*15*/; // Maximum velocity (can be something else than +/-max or 0)
        float frictionConstant = 0.8f;
        float turnCorrection = 7f;
        float brakeAccel = 2;
        
        // Loop variables
        boolean alreadyInList = false;
        boolean pathComplete = false;
        boolean reachedCP = false;
        NodeGlueFinal curNode = null;
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
                if (checkPoints.indexOf(cp) < checkPoints.size())
                    visual.addPointBig (new Point2D.Double(cp.x, -cp.y));
            }
        // Visuals for finish location
            visual.setForeground(Color.CYAN);
            visual.addLine(new Point2D.Double(8,-5), new Point2D.Double(10,-5));
        

        // Visuals for track
        visual.setForeground(Color.LIGHT_GRAY);
        Vector3f normal = new Vector3f();
        normal.cross(new Vector3f(0,0,1)).normalize().mul(1f).mul((float)(7));
        for (int n = 0; n < points.length - 1; n++) {
            normal = new Vector3f(tangents[n]);
            normal.cross(new Vector3f(0,0,1)).normalize().mul(1f).mul((float)(7)-1f);
            //visual.addPoint(new Point2D.Double(points[n].x + 2*normal.x, -(points[n].y + 2*normal.y)));
            //visual.addPoint(new Point2D.Double(points[n].x - 2*normal.x, -(points[n].y - 2*normal.y)));
            
            visual.addLine(new Point2D.Double(points[n].x + 2*normal.x, -(points[n].y + 2*normal.y)),
                    new Point2D.Double(points[n+1].x + 2*normal.x, -(points[n+1].y + 2*normal.y)));
            visual.addLine(new Point2D.Double(points[n].x - 2*normal.x, -(points[n].y - 2*normal.y)),
                    new Point2D.Double(points[n+1].x - 2*normal.x, -(points[n+1].y - 2*normal.y)));
        }
        
        //visual.setForeground(Color.WHITE);
        //for (int n = 0; n < points.length; n++) {
            //visual.addPoint(new Point2D.Double(points[n].x, -points[n].y));
        //}
        // </editor-fold>
            
        // Initialize open- and closed-list
        ArrayList<NodeGlueFinal> openlist = new ArrayList<>();
        ArrayList<NodeGlueFinal> closedlist = new ArrayList<>();
        ArrayList<NodeGlueFinal> pathList = new ArrayList<>();
        
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

        NodeGlueFinal start = new NodeGlueFinal(startPos, 0, 0, Math.PI, 0, 0, h, 0, null, 0, 0, 0);
        openlist.add(start);
        
        // LOOP: until openlist is empty left OR when the goal is reached.
        // <editor-fold defaultstate="collapsed" desc="LOOP">
        for (int iter = 0; !openlist.isEmpty() && iter < iterAllowed; iter++) {
            // Consider node with smallest f (g+h) in openlist.
            curNode = openlist.get(0);
            for (NodeGlueFinal o : openlist) {
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
            if (curNode.h < 1/*0.2*/) { // ? does this number even matter?
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
                    //VARIABLES
                    int turn = i;
                    int acc = j;
                    
                    // delta time
                    float dt = 0.1f;
                    // vFactor
                    Vector3f carDir, u, uNorm, vFactor;
                    float udist;
                    
                    // road position index number
                    int rIndex = curNode.rIndex;
                    
                    // Calculate succesor node variables
                    Point2D.Double sPos;
                    // Used in Node
                    double sV, sA, sRot, sRotV;
                    // Not used in Node
                    //double deltaRot, r, sr, s_deltaX, s_deltaY;
                    //double distTravelled = curNode.v * tInt + 0.5*(j*a)*tInt*tInt;
                    
                    // Reset physics constants
                    a = 0.8f; // Maximum acceleration
                    
                    
                    // <editor-fold defaultstate="collapsed" desc="TRACK DETECTION"> 
                    // Find road point closest to car
                    float shortestDist = 10000000;
                    float dist;
                    int ind = 0; // Current road point index
                    // Check from checkpoint up until current point
                    for (int n = Math.max((int)Math.floor(points.length * (progress.checkPoint / progress.cpAm)) - 5, 
                                    (int)Math.floor(points.length * (progress.checkPoint / progress.cpAm))); 
                                    n < Math.min(rIndex + 10, points.length); n++) {
                        dist = (float)Math.sqrt(Math.pow(curNode.pos.x - points[n].x, 2) 
                                + (float)Math.pow(curNode.pos.y - points[n].y, 2));
                        if (dist < shortestDist) {
                            shortestDist = dist;
                            ind = n;
                        }
                    }
                    if (rIndex > points.length - 10) { // Early on track: check end points
                        for (int n = 0; n < 10; n++) {
                            dist = (float)Math.sqrt(Math.pow(curNode.pos.x - points[n].x, 2) 
                                    + (float)Math.pow(curNode.pos.y - points[n].y, 2));
                            if (dist < shortestDist) {
                                shortestDist = dist;
                                ind = n;
                            }
                        }
                    } else if (rIndex < 10) { // Late on track: check start points
                        for (int n = points.length - 10; n < points.length; n++) {
                            dist = (float)Math.sqrt(Math.pow(curNode.pos.x - points[n].x, 2) 
                                    + (float)Math.pow(curNode.pos.y - points[n].y, 2));
                            if (dist < shortestDist) {
                                shortestDist = dist;
                                ind = n;
                            }
                        }
                    }
                    Vector3f rN = normals[ind];
                    Vector3f roadPos = new Vector3f(points[ind].x, points[ind].y, points[ind].z);
                    //System.out.println(ind);
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc="LINEAR IMPROVEMENTS"> 
                    // (acc) Max speed regulation
                    boolean noFriction = false;
                    if ((acc > 0 && curNode.v + a*dt > vMax) ||
                            (acc < 0 && curNode.v - a*dt < -vMax)) {
                        acc = 0;
                        noFriction = true;
                    }

                    // (a)/(v) Friction: When acceleration is 0, abs(v) decreases
                    if (acc == 0 && !noFriction) {
                        if (curNode.v > a * frictionConstant * dt)
                            a *= -frictionConstant;
                        else if (curNode.v < -a * frictionConstant * dt)
                            a *= frictionConstant;
                        else { // Stop moving when v close to 0
                            curNode.v = 0;
                            a = 0;
                        }
                    } else { // When accelerate
                        if (curNode.v > a * frictionConstant * dt && acc < 0 
                                || curNode.v < -a* frictionConstant * dt && acc > 0) {
                            a *= brakeAccel;
                        }
                        a *= acc;
                    }
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc="ROTATIONAL IMPROVEMENTS"> 
                    // (turn)/(rotvmax) Turn correction for small velocities
                    if (Math.abs(curNode.v) == 0) // not 0: causes teleport bug
                        turn = 0;
                    /*else if (Math.abs(curNode.v) < turnCorrection)
                        rotvMax *= (Math.abs(curNode.v) / turnCorrection);
                    */
                    // (turn) Turn correction for negative velocities
                    if (curNode.v < 0)
                        turn = -turn;
                    // </editor-fold>
                    
                    
                    // <editor-fold defaultstate="collapsed" desc="HORIZONTAL MOVEMENT CALCULATIONS"> 
                    sA = a;
                    if (turn == 0) { // Straight
                        double distTravelled = dt * (curNode.v + 0.5f * a * dt);
                        // Calculate end rotation and velocity
                        sV = curNode.v + a * dt;
                        sRot = curNode.rot;
                        sRotV = 0;
                        // Calculate the vFactor in the direction of XY movement
                        carDir = new Vector3f ((float)Math.cos(curNode.rot), (float)Math.sin(curNode.rot), 0);
                        u = new Vector3f(carDir.y*rN.z - carDir.z*rN.y,
                                carDir.z*rN.x - carDir.x*rN.z,
                                carDir.x*rN.y - carDir.y*rN.x);
                        udist = (float)Math.sqrt(u.x*u.x + u.y*u.y + u.z*u.z);
                        uNorm = new Vector3f(u.x/udist, u.y/udist, u.z/udist);
                        vFactor = new Vector3f(rN.y*uNorm.z - rN.z*uNorm.y,
                                rN.z*uNorm.x - rN.x*uNorm.z,
                                rN.x*uNorm.y - rN.y*uNorm.x); // vFactor = roadTan

                        // Calculate the end position
                        sPos = new Point2D.Double (
                                curNode.pos.x + vFactor.x * distTravelled,
                                curNode.pos.y + vFactor.y * distTravelled);

                    } else { // Turn
                        sRotV = turn * rotvMax;
                        // Calculate end rotation and velocity
                        sV = curNode.v + a * dt;
                        sRot = curNode.rot + sRotV * dt;
                        // Calculate direction and magnitude of XY movement during this frame
                        float aRotVSquared = (float)(a / (sRotV * sRotV));
                        float deltaX = (float) ((sV / sRotV) * Math.sin(sRot)
                                    + aRotVSquared * Math.cos(sRot)
                                    - (curNode.v / sRotV) * Math.sin(curNode.rot)
                                    - aRotVSquared * Math.cos(curNode.rot));
                        float deltaY = (float) (-(sV / sRotV) * Math.cos(sRot)
                                    + aRotVSquared * Math.sin(sRot)
                                    + (curNode.v / sRotV) * Math.cos(curNode.rot)
                                    - aRotVSquared * Math.sin(curNode.rot));
                        double distTravelled = (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
                        double distAngle = Math.atan2(deltaX, deltaY);
                        distAngle = (-(distAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);

                        // Calculate the vFactor in the direction of XY movement
                        carDir = new Vector3f ((float)Math.cos(distAngle), (float)Math.sin(distAngle), 0);
                        u = new Vector3f(carDir.y*rN.z - carDir.z*rN.y,
                                carDir.z*rN.x - carDir.x*rN.z,
                                carDir.x*rN.y - carDir.y*rN.x);
                        udist = (float)Math.sqrt(u.x*u.x + u.y*u.y + u.z*u.z);
                        uNorm = new Vector3f(u.x/udist, u.y/udist, u.z/udist);
                        vFactor = new Vector3f(rN.y*uNorm.z - rN.z*uNorm.y,
                                rN.z*uNorm.x - rN.x*uNorm.z,
                                rN.x*uNorm.y - rN.y*uNorm.x); // vFactor = roadTan
                        // Calculate the end position
                        sPos = new Point2D.Double(
                                curNode.pos.x + vFactor.x * distTravelled,
                                curNode.pos.y + vFactor.y * distTravelled);
                    }
                    // </editor-fold>
                    // </editor-fold>
                    
                    // <editor-fold defaultstate="collapsed" desc="G & H">
                    double sg, sh;
                    // Determine g
                    sg = curNode.g + curNode.v * tInt;
                    
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
                        if (sh < 5/*1.75*/) {
                            sNextCP++;
                            reachedCP = true;
                        }
                    }
                    // </editor-fold>
                    
                    //CASE I: already in closed
                    Point2D.Double compPos = new Point2D.Double
                        ((int)(10000*sPos.x), (int)(10000*sPos.y));
                    for (NodeGlueFinal o : openlist) {
                        if ((int)(10000*o.pos.x) == compPos.x && (int)(10000*o.pos.y) == compPos.y) {
                            alreadyInList = true;
                            /*System.err.println("Unexpected node found in "
                                    + "closed list: " + o);*/
                        }
                    }
                    //CASE II: already in open
                    for (NodeGlueFinal c : closedlist) {
                        if ((int)(10000*c.pos.x) == compPos.x && (int)(10000*c.pos.y) == compPos.y) {
                            alreadyInList = true;
                            System.err.println("Unexpected node found in "
                                    + "opened list: " + c);
                        }
                    }
                    //CASE III: not in any list
                    if (!alreadyInList) {
                        openlist.add(new NodeGlueFinal(sPos, sV, sA, sRot, sRotV,
                                sg, sh, sNextCP, curNode, ind, i, j));
                        // Debug succesor logs
                        if(iter == 0) {
                            System.out.println("-"+(iter+1)+"->a: " + sA 
                                    + ", rotV: " + sRotV 
                                    + ", h: " + sh
                                    + ", g: " + sg
                                    + ", X: " + sPos.x + ", Y: " + sPos.y
                                    + ", Rot: "+sRot);
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
        
        try (NodeWriter nw = new NodeWriter("C:\\Users\\s152102\\Documents\\101_Courses\\Y3Q1 DBL interactive intelligent systems\\Gitkraken game folder\\2IOE0_game\\src\\main\\java\\src\\res\\A_star_data\\Node.csv")) {
            nw.writeNodeChain(curNode);
            
        } catch (IOException e) {
            Logger.write(e);
        }
        
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
            for (NodeGlueFinal p : pathList) {
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
        AStarPointersGlueFinal.runAlgorithm();
    }
    
    public static void setTrack(Track track) {
        List<Vector3f> pointList = new ArrayList<Vector3f>();
        List<Vector3f> normalList = new ArrayList<Vector3f>();
        List<Vector3f> tangentList = new ArrayList<Vector3f>();
        
        trackSize = track.getSize()/3;
        trackWidth = track.getWidth()*trackSize;
        for (int i = 0; i < track.getNrOfSegments(); i++) {
            float delta = 1.0f / POINTS_PER_SEGMENT;
            for (float t = 0; t < 1.0; t += delta) {
                // positions
                pointList.add(new Vector3f(-(track.getPoint(i, t).z - 1.5f) * trackSize,
                -track.getPoint(i, t).x * trackSize, 
                track.getPoint(i, t).y * trackSize + 2f));
                // normals
                normalList.add(new Vector3f(-Track.calcNormal(track.getTangent(i, t)).z,
                -Track.calcNormal(track.getTangent(i, t)).x,
                Track.calcNormal(track.getTangent(i, t)).y));
                // tangents
                tangentList.add(new Vector3f(-track.getTangent(i, t).z,
                -track.getTangent(i, t).x,
                track.getTangent(i, t).y));
            }
        }
        
        points = pointList.toArray(new Vector3f[pointList.size()]);
        normals = normals = normalList.toArray(new Vector3f[normalList.size()]);
        tangents = tangentList.toArray(new Vector3f[tangentList.size()]);
    }
}