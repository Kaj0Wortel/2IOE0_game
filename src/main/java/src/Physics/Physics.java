package src.Physics;


// Own imports
import java.util.ArrayList;
import java.util.List;
import src.Assets.instance.Instance;
import src.Assets.instance.Instance.State;


//Java imports
import java.util.Set;
import org.joml.Vector3f;
import src.Assets.instance.Car;
import src.Assets.instance.Item;
import src.racetrack.Track;
import src.tools.Box3f;
import src.tools.update.CollisionManager.Collision;
import src.tools.update.CollisionManager.Entry;
//import src.racetrack.BezierTrack;


public class Physics {
    
    public static class ModState {
        public Box3f box;
        public float size;
        public float rotx;
        public float roty;
        public float rotz;
        public float integratedRotation;
        
        public float velocity;
        public float collisionVelocity;
        public float verticalVelocity;
        
        
        public ModState(State state) {
            box = new Box3f(new Vector3f(
                    -state.box.pos().z,
                    -state.box.pos().x,
                    state.box.pos().y
            ), state.box.dx(), state.box.dy(), state.box.dz());
            size = state.size;
            rotx = state.rotx;
            this.roty = (float) Math.toRadians(state.roty);
            rotz = state.rotz;
            integratedRotation = state.integratedRotation;
            velocity = state.velocity;
            collisionVelocity = state.collisionVelocity;
            verticalVelocity = state.verticalVelocity;
        }
        
        /**
         * @return a new {@link State} from the current state. Note that the
         *     values are NOT cloned, as this class should be used as a
         *     "cast away shell".
         * 
         * @see State
         */
        public State createState() {
            // Convert back to instance space.
            Box3f newBox = new Box3f(new Vector3f(
                    -box.pos().y,
                    box.pos().z,
                    -box.pos().x
            ), box.dx(), box.dy(), box.dz());
            return new State(newBox, size,
                    rotx, (float) (Math.toDegrees(roty) % 360), rotz,
                    integratedRotation, velocity, collisionVelocity,
                    verticalVelocity);
        }
        
        
    }
    
    
    public static class ModPhysicsContext {
        public float linAccel;
        public float rotationalVelocity;
        public float maxLinearVelocity;
        public float frictionConstant;
        public float gravity;
        
        public float turnCorrection;
        public float knockback;
        public float knockbackDur;
        public float accBlockDur;
        
        public float largeSlowDown;
        public float bounceFactor;
        public float airControl;
        public float brakeAccel;
        
        
        public ModPhysicsContext(PhysicsContext pc) {
            this.linAccel = pc.linAccel;
            this.rotationalVelocity = pc.rotationalVelocity;
            this.maxLinearVelocity = pc.maxLinearVelocity;
            this.frictionConstant = pc.frictionConstant;
            this.gravity = pc.gravity;
            
            this.turnCorrection = pc.turnCorrection;
            this.knockback = pc.knockback;
            this.knockbackDur = pc.knockbackDur;
            this.accBlockDur = pc.accBlockDur;
            
            this.largeSlowDown = pc.largeSlowDown;
            this.bounceFactor = pc.bounceFactor;
            this.airControl = pc.airControl;
            this.brakeAccel = pc.brakeAccel;
        }
        
        public ModPhysicsContext(float linAccel, float rotationalVelocity,
                float maxLinearVelocity, float frictionConstant,
                float gravity, float turnCorrection, float knockback,
                float knockbackDur, float accBlockDur, float largeSlowDown,
                float bounceFactor, float airControl, float brakeAccel) {
            this.linAccel = linAccel;
            this.rotationalVelocity = rotationalVelocity;
            this.maxLinearVelocity = maxLinearVelocity;
            this.frictionConstant = frictionConstant;
            this.gravity = gravity;
            
            this.turnCorrection = turnCorrection;
            this.knockback = knockback;
            this.knockbackDur = knockbackDur;
            this.accBlockDur = accBlockDur;
            
            this.largeSlowDown = largeSlowDown;
            this.bounceFactor = bounceFactor;
            this.airControl = airControl;
            this.brakeAccel = brakeAccel;
        }
        
        
        /**
         * @return a new {@link PhysicsContext} from the current state.
         *     Note that the values are NOT cloned, as this class should
         *     be used as a "cast away shell".
         * 
         * @see PhysicsContext
         */
        public PhysicsContext createContext() {
            return new PhysicsContext(linAccel, rotationalVelocity,
                    maxLinearVelocity, frictionConstant, gravity,
                    turnCorrection, knockback, knockbackDur, accBlockDur,
                    largeSlowDown, bounceFactor, airControl, brakeAccel);
        }
        
        
    }
    
    
    final private static int pointsPerSegment = 10;
    private static Vector3f[] points = new Vector3f[0];
    private static Vector3f[] normals = new Vector3f[0];
    
    
    /**
     * Computes the position, velocity and rotation after a key input
     * using the position, velocity and rotation values before the input.
     * 
     * WORKING:
     * rotating + accelerating
     * updating its pStruct even when no key pressed
     * slowing down when no key is pressed
     * simple collision (rectangle colliders)
     * rotation correction for small velocities and negative velocities
     * 2 simple items/situations implemented
    \* Large slowdown when speed higher than vMax 
     * 
     * 
     * TODO:
     * Improved collision: differentiate between static/dynamic collision 
     *      - AI car necessary to test dynamic
     *      - long wall necessary to test static point-array-based static
     *      - Rotation necessary
     * Implement falling when not on ground
     *      - tumbling over
     *      - point2d.doubles need to become vector2f?
     * 
     * TODO?:
     * completely stop movement when velocity close to 0
     *      - Already really close, when forcing: physics go haywire
     *      - Not really necessary
     * Implement realistic collision
     *      - depends on how many hours we want to invest in this (claw?)
     * Refine rotation physics (-rotVmax<->rotVmax) instead of (-rotVmax/0/rotVmax)
     *      - controller would then be necessary instead of just an option
     *      - Controller script also needs to support for range detection
     * Refine rotation physics (increase rot velocity the longer you hold A/D)
     *      - Less ideal than previous idea
     *      - If implemented: should be subtle
     *      - 4-term rotation calculations -> 12-figure rotation calculations
     *      
     * 
     * turn: A/D => (1/-1)
     * acc: W/S => (1/-1)
     * a: max acc
     * rotV: max rot velocity (-max/0/max)
     * vMax: max lin velocity, (-max<->max)
     * tInt: time interval (key detection interval)
     * startStruct: begin position
     * velocity and rotation
     */
    public static void calcPhysics(Instance source, PStructAction pStruct,
            PhysicsContext pc, State state, Set<Instance> collisions) {
        // Create a modifyable state to reduce the number of objects creations.
        ModState s = new ModState(state);
        
        // ITEMS & CARS
        // If the instance intersects with a car or an item, use collision
        // dependant collision handeling.
        // Ignore the current actions.
        if (!collisions.isEmpty()) {
            ModPhysicsContext modPC = new ModPhysicsContext(pc);
            
            for (Instance instance : collisions) {
                if (instance instanceof Car) {
                    calcPhysics(source, pStruct, pc, s); // TODO
                    
                } else if (instance instanceof Item) {
                    System.out.println("hit item!");
                    ((Item) instance).physicsAtCollision(
                            source, pStruct, modPC, s);
                }
            }
            
            calcPhysics(source, pStruct, modPC.createContext(), s);
            source.setState(s.createState());
            
        } else {
            calcPhysics(source, pStruct, pc, s);
            source.setState(s.createState());
        }
    }
    
    /**
     * 
     * @param source
     * @param pStruct
     * @param pc
     * @param s
     */
    public static void calcPhysics(Instance source, PStructAction pStruct,
            PhysicsContext pc, ModState s) {
        
        // Variables used in physics calculations.
        float dt = pStruct.dt / 160f;
        float linAccel = pc.linAccel;
        float rotationalVelocity = pc.rotationalVelocity;
        float distTravelled;
        // vFactor
        Vector3f carDir, u, uNorm, vFactor;
        float udist;
        // End pStruct
        float eV;
        float eRot;
        Vector3f ePos;
        // State description
        boolean onTrack = true;
        boolean inAir = true;
        // temp before complete track implementation
        double gndZ = 0;
        
        // <editor-fold defaultstate="collapsed" desc="TRACK DETECTION"> 
        // When off-track (temporary: no track to infer from yet)
        /*if (s.box.pos().x > 125 ||
                -125 > s.box.pos().x ||
                s.box.pos().y > 125 ||
                -125 > s.box.pos().y) {
            onTrack = false;
        }*/
        //BezierTrack bezierTrack = new BezierTrack();
        
        //Vector3f rN = new Vector3f(-0.5f, -0.5f, (float)Math.sqrt(2)/2);
        //Vector3f rN = new Vector3f(-(float)Math.sqrt(6)/6, -(float)Math.sqrt(6)/6
        //        , (float)Math.sqrt(6)/3);
        Vector3f rN = new Vector3f(0,0,1);
        Vector3f roadPos = new Vector3f(0,0,1);
        // </editor-fold>
        
        if (onTrack) {
            // <editor-fold defaultstate="collapsed" desc="AIR TIME DETECTION"> 
            //TODO:
            // check what current situation is (what is gndz compared to epos)
            //      camera seems to shake?
            //- check at ePos if under GNDz: teleport up to gnd
            //(maybe also check at ePos if over  GNDz (airtime?))
            // re-organise air time and vertical movement parts
            gndZ = roadPos.z 
                    - (s.box.pos().x - roadPos.x) *rN.x / rN.z
                    - (s.box.pos().y - roadPos.y) *rN.y / rN.z;
            if (s.box.pos().z <= gndZ)
                inAir = false;
            // </editor-fold>
        }
        
        // <editor-fold defaultstate="collapsed" desc="LINEAR IMPROVEMENTS"> 
        // (ACCEL) Max speed regulation
        if ((pStruct.accel > 0 && s.velocity + linAccel*dt > pc.maxLinearVelocity) ||
                (pStruct.accel < 0 && s.velocity - linAccel*dt < -pc.maxLinearVelocity))
            pStruct.accel = 0;
        // (ACCEL) Block manual acceleration when collision just happened
        if (s.collisionVelocity > pc.knockback / pc.accBlockDur)
            pStruct.accel = 0;
        
        // (LINACCEL) Temporary slowdown after speedboost: not refined
        if (s.velocity + linAccel*dt > pc.maxLinearVelocity * 1.1 ||
                s.velocity - linAccel*dt < -pc.maxLinearVelocity * 1.1)
            linAccel *= (Math.abs(s.velocity - pc.maxLinearVelocity*1.1) + 1)
                    * pc.frictionConstant * pc.largeSlowDown;
        
        
        // (LINACCEL)/(VEL) Friction: When acceleration is 0, abs(v) decreases
        if (pStruct.accel == 0) {
            if (s.velocity > linAccel * pc.frictionConstant * dt)
                linAccel = -pc.frictionConstant * linAccel;
            else if (s.velocity < -linAccel * pc.frictionConstant * dt)
                linAccel = pc.frictionConstant * linAccel;
            else { // Stop moving when v close to 0
                s.velocity = 0;
                linAccel = 0;
            }
        } else { // When accelerate
            if (s.velocity > linAccel * pc.frictionConstant * dt && pStruct.accel < 0 
                    || s.velocity < -linAccel * pc.frictionConstant * dt && pStruct.accel > 0) {
                linAccel *=pc.brakeAccel;
            }
            linAccel *= pStruct.accel;
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="ROTATIONAL IMPROVEMENTS"> 
        // (TURN)/(ROTVELOCITY) Turn correction for small velocities
        if (Math.abs(s.velocity) == 0)
            pStruct.turn = 0;
        else if (Math.abs(s.velocity) < pc.turnCorrection)
            rotationalVelocity *= (Math.abs(s.velocity) / pc.turnCorrection);
        
        // (TURN) Turn correction for negative velocities
        if (s.velocity < 0)
            pStruct.turn = -pStruct.turn;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="OTHER IMPROVEMENTS"> 
        // (ROTVELOCITY)/(LINACCEL) Air movement
        if (inAir) {
            rotationalVelocity *= pc.airControl;
            linAccel *= (1.45 * pc.airControl);
        }
        // </editor-fold>
        
        
        // <editor-fold defaultstate="collapsed" desc="HORIZONTAL MOVEMENT CALCULATIONS"> 
        if (pStruct.turn == 0) { // Straight
            distTravelled = dt * (s.velocity + 0.5f * linAccel * dt);
            // Calculate end rotation and velocity
            eV = s.velocity + linAccel * dt;
            eRot = s.roty;
            // Calculate the vFactor in the direction of XY movement
            carDir = new Vector3f ((float)Math.cos(s.roty), (float)Math.sin(s.roty), 0);
            u = new Vector3f(carDir.y*rN.z - carDir.z*rN.y,
                    carDir.z*rN.x - carDir.x*rN.z,
                    carDir.x*rN.y - carDir.y*rN.x);
            udist = (float)Math.sqrt(u.x*u.x + u.y*u.y + u.z*u.z);
            uNorm = new Vector3f(u.x/udist, u.y/udist, u.z/udist);
            vFactor = new Vector3f(rN.y*uNorm.z - rN.z*uNorm.y,
                    rN.z*uNorm.x - rN.x*uNorm.z,
                    rN.x*uNorm.y - rN.y*uNorm.x); // vFactor = roadTan
            
            // Calculate the end position
            ePos = new Vector3f (
                    (float) (s.box.pos().x + vFactor.x * distTravelled),
                    (float) (s.box.pos().y + vFactor.y * distTravelled),
                    (float) (s.box.pos().z + vFactor.z * distTravelled));
            
        } else { // Turn
            rotationalVelocity = pStruct.turn * rotationalVelocity;
            // Calculate end rotation and velocity
            eV = s.velocity + linAccel * dt;
            eRot = s.roty + rotationalVelocity * dt;
            // Calculate direction and magnitude of XY movement during this frame
            float aRotVSquared = linAccel / (rotationalVelocity * rotationalVelocity);
            float deltaX = (float) ((eV / rotationalVelocity) * Math.sin(eRot)
                        + aRotVSquared * Math.cos(eRot)
                        - (s.velocity / rotationalVelocity) * Math.sin(s.roty)
                        - aRotVSquared * Math.cos(s.roty));
            float deltaY = (float) (-(eV / rotationalVelocity) * Math.cos(eRot)
                        + aRotVSquared * Math.sin(eRot)
                        + (s.velocity / rotationalVelocity) * Math.cos(s.roty)
                        - aRotVSquared * Math.sin(s.roty));
            distTravelled = (float)Math.sqrt(deltaX*deltaX + deltaY*deltaY);
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
            ePos = new Vector3f(
                    (float)(s.box.pos().x + vFactor.x * distTravelled),
                    (float)(s.box.pos().y + vFactor.y * distTravelled), 
                    (float)(s.box.pos().z + vFactor.z * distTravelled));
        }
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc="VERTICAL MOVEMENT CALCULATIONS"> 
        // Do not jump if already jumping
        if (s.verticalVelocity == 0 && s.box.pos().z < gndZ + 0.1)
            s.verticalVelocity += pStruct.verticalVelocity;
        // Change in height when falling
        double deltaZ = dt * (s.verticalVelocity + 0.5 * pc.gravity * dt);
        // When in the air
        if (s.box.pos().z + deltaZ > gndZ) {
            s.verticalVelocity += pc.gravity * dt;
            ePos.z += deltaZ;
        }
        // When bouncing on the ground
        else if (Math.abs(s.verticalVelocity) > 0.01 && onTrack) {
            s.verticalVelocity = -s.verticalVelocity * pc.bounceFactor;
            
        } 
        // When on track on the ground
        else if (onTrack) {
            s.verticalVelocity = 0;
            ePos.z = (float)gndZ;
        }
        if (!onTrack) {
            s.verticalVelocity += pc.gravity * dt;
            ePos.z += deltaZ;
        }
        // Limit upwards velocity
        if (s.verticalVelocity > 10)
            s.verticalVelocity = 10;
        //Death barrier: reset
        if (ePos.z < -100) {
            ePos = new Vector3f(0,0,2);
            eV = 0;
            eRot = (float) Math.PI/2;
            s.collisionVelocity = 0;
            s.verticalVelocity = 0;
        }
        // </editor-fold>
        
        
        // <editor-fold defaultstate="collapsed" desc="COLLISION CALCULATIONS"> 
        // Should be integrated into another class but still change pStruct
        Vector3f colPos = new Vector3f (0.0001f, 40, 1);
        double colRange = 2;
        double carRange = 6;
        // Collision detection
        if (s.box.pos().x + carRange/2 > colPos.x - colRange/2 &&
                colPos.x + colRange/2 > s.box.pos().x - carRange/2 &&
                s.box.pos().y + carRange/2 > colPos.y - colRange/2 &&
                colPos.y + colRange/2 > s.box.pos().y - carRange/2 &&
                s.box.pos().z + carRange/2 > colPos.z - colRange/2 &&
                colPos.z + colRange/2 > s.box.pos().z - carRange/2) {
            
            double colAngle = Math.atan2( s.box.pos().x - colPos.x, s.box.pos().y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            // Can only receive knockback once the last knockback is sufficiently small
            if (s.collisionVelocity < 1) {
                s.collisionVelocity = Math.abs(s.velocity) * pc.knockback;
                ePos = new Vector3f(
                    (float)(ePos.x + s.collisionVelocity * Math.cos(colAngle)), 
                    (float)(ePos.y + s.collisionVelocity * Math.sin(colAngle)),
                    ePos.z);
                s.verticalVelocity = 0.5f + Math.abs(s.velocity)/8;
            } 
            
            // Moments after collision
        } else if (s.collisionVelocity > pc.knockback/1_000_000_000) {
            // Slowly diminish the knockback over time
            s.collisionVelocity *= pc.knockbackDur;
            // Angle can change during bump: maybe looks better?
            double colAngle = Math.atan2(s.box.pos().x - colPos.x, s.box.pos().y - colPos.y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);
            ePos = new Vector3f(
                   (float) (ePos.x + s.collisionVelocity * Math.cos(colAngle)), 
                   (float) (ePos.y + s.collisionVelocity * Math.sin(colAngle)),
                   ePos.z);
            
        } else {
            // No collision happening.
            // Set collision velocity to 0 when it was already really small.
            s.collisionVelocity = 0;
        }
        // </editor-fold>
           
        //System.out.println(eRot + ", " + s.verticalVelocity + ", " + ePos);
        
        // Update the state.
        s.box.setPosition(ePos);
        s.velocity = eV;
        s.roty = eRot;
    }
    
    /**
     * Calculate a double non-static collision.
     * 
     * @param col the collision data.
     */
    public static void exeCollision(Collision col) {
        Entry e1 = col.getEntry1();
        Entry e2 = col.getEntry2();
        
        // TODO: do stuff with the entries.
    }
    
    /**
     * Calculates and updates the physics of te instance given in the
     * entry with the given {@link PStructAction}, {@link ModPhysicsContext}
     * and {@link ModState}.
     * Updates the state of the instance afterwards.
     * 
     * @param entry the entry to get the data from.
     * 
     * Should be called when the entry was updated outside it's
     * own update cycle.
     */
    public static void calcAndUpdatePhysics(Entry entry) {
        calcPhysics(entry.inst, entry.pStruct, entry.mpc.createContext(),
                entry.ms);
        entry.inst.setState(entry.ms.createState());
    }
    
    /**
     * Sets the points and normals of the given track as reference points.
     * 
     * @param track the track to get the points and normals of.
     */
    public static void setTrack(Track track) {
        List<Vector3f> pointList = new ArrayList<Vector3f>();
        List<Vector3f> normalList = new ArrayList<Vector3f>();
        
        for (int i = 0; i < track.getNrOfSegments(); i++) {
            float delta = 1.0f / pointsPerSegment;
            for (float t = 0; t < 1.0; t += delta) {
                pointList.add(track.getPoint(i, t));
                normalList.add(Track.calcNormal(track.getTangent(i, t)));
            }
        }
        
        points = pointList.toArray(new Vector3f[pointList.size()]);
        normals = normals = normalList.toArray(new Vector3f[normalList.size()]);
    }
    
    
    public static void physicsTestVisuals () {
        /*
        // Own class declarations
        VisualAStar visual = new VisualAStar();
        // Test arraylist
        ArrayList<PStruct> testDrive = new ArrayList<>();
        
        // Start position, rotation and velocity
        PStruct currentStruct = new PStruct(new Vector3f(1,1,0), 0, 0, 0, 0, 0);
        testDrive.add(currentStruct);
        
        // INSERT TEST COMMANDS
        for (int i = 0; i < 20; i++) {
            currentStruct = Physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 7; i++) {
            currentStruct = Physics.calcPhysics(1, 0, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 10; i++) {
            currentStruct = Physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 10; i++) {
            currentStruct = Physics.calcPhysics(0, 0, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 20; i++) {
            currentStruct = Physics.calcPhysics(1, -1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 35; i++) {
            currentStruct = Physics.calcPhysics(-1, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        for (int i = 0; i < 30; i++) {
            currentStruct = Physics.calcPhysics(0, 1, 1,
                    Math.PI/2, 2.01, 0.1, 0, currentStruct);
            testDrive.add(currentStruct);
        }
        
        // Visualization        
        for (int i = 0; i < testDrive.size(); i++) {
            System.out.println(new Point2D.Double(testDrive.get(i).pos.x, testDrive.get(i).pos.y));
            visual.addPoint(new Point2D.Double(testDrive.get(i).pos.x, -testDrive.get(i).pos.y));
        }
        visual.repaint();
        */
    }
    
    public static void main(String[] args) {
        Physics.physicsTestVisuals();
    }
}

