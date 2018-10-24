package src.Physics;


// Own imports
import src.AI.AStarDataPack;
import src.AI.DequeRequestReader;
import src.AI.Processor;
import src.Assets.Items.ItemInterface;
import src.Assets.instance.Instance;
import src.Assets.instance.Instance.State;
import src.Assets.instance.Item;
import src.GS;
import src.Progress.ProgressManager;
import src.music.MusicManager;
import src.racetrack.Track;
import src.tools.PosHitBox3f;
import src.tools.log.Logger;
import src.tools.update.CollisionManager;
import src.tools.update.CollisionManager.Collision;
import src.tools.update.CollisionManager.Entry;


//Java imports
import org.joml.Matrix3f;
import org.joml.Vector3f;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.io.File;
import java.io.IOException;
import javax.swing.SwingUtilities;


public class Physics {
    
    public static class ModState {
        public PosHitBox3f box;
        public float sizex;
        public float sizey;
        public float sizez;
        public float rotx;
        public float roty;
        public float rotz;
        public float internRotx;
        public float internRoty;
        public float internRotz;
        public Vector3f internTrans;
        
        public float velocity;
        public float collisionVelocity;
        public float colAngle;
        public float verticalVelocity;
        public boolean onTrack;
        public boolean inAir;
        public int rIndex;
        public boolean isResetting;
        public boolean playBoing;
        
        public ItemInterface curItem;
        public List<ItemInterface> activeItems;
        
        // Conversion matrix from model -> physics.
        final public static Matrix3f CONV_MAT = new Matrix3f(
                0, -1, 0, 0, 0, 1, -1, 0, 0);
        // Inverse conversion matrix (from physics -> model).
        final public static Matrix3f CONV_MAT_INV = new Matrix3f(
                0, 0, -1, -1, 0, 0, 0, 1, 0);
        
        
        public ModState(State state) {
            box = state.box.convert(CONV_MAT);
            sizex = state.sizez;
            sizey = state.sizex;
            sizez = state.sizey;
            this.rotx = (float) Math.toRadians(state.rotx);
            this.roty = (float) Math.toRadians(state.roty);
            this.rotz = (float) Math.toRadians(state.rotz);
            this.internRotx = (float) Math.toRadians(state.internRotx);
            this.internRoty = (float) Math.toRadians(state.internRoty);
            this.internRotz = (float) Math.toRadians(state.internRotz);
            this.internTrans = state.internTrans.mul(CONV_MAT, new Vector3f());
            this.velocity = state.velocity;
            this.collisionVelocity = state.collisionVelocity;
            this.verticalVelocity = state.verticalVelocity;
            this.colAngle = state.colAngle;
            this.onTrack = state.onTrack;
            this.inAir = state.inAir;
            this.rIndex = state.rIndex;
            this.isResetting = state.isResetting;
            this.curItem = state.curItem;
            this.activeItems = new CopyOnWriteArrayList<ItemInterface>(state.activeItems);
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
            return new State(box.convert(CONV_MAT_INV), sizey, sizez, sizex,
                    (float) (Math.toDegrees(rotx) % 360), 
                    (float) (Math.toDegrees(roty) % 360), 
                    (float) (Math.toDegrees(rotz) % 360),
                    (float) (Math.toDegrees(internRotx) % 360), 
                    (float) (Math.toDegrees(internRoty) % 360), 
                    (float) (Math.toDegrees(internRotz) % 360),
                    internTrans.mul(CONV_MAT_INV),
                    velocity, collisionVelocity, colAngle,
                    verticalVelocity, onTrack, inAir, rIndex, isResetting,
                    curItem, new CopyOnWriteArrayList<ItemInterface>(activeItems));
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
    
    // Necessary for determining if on track
    final private static int POINTS_PER_SEGMENT = 500;
    private static Track track = null;
    private static Vector3f[] points = new Vector3f[0];
    private static Vector3f[] normals = new Vector3f[0];
    private static Vector3f[] tangents = new Vector3f[0];
    private static float trackSize = 0;
    private static float trackWidth = 0;
    private static boolean playBoing;
    private static int beepCounter = 0;
    private static long lastBeepTime = 0;
    private static Map<Instance, DequeRequestReader<AStarDataPack>> readers
            = new ConcurrentHashMap<>();
    
    
    public static void registerReader(Instance inst, File file,
            Processor<AStarDataPack> processor) {
        try {
            DequeRequestReader<AStarDataPack> reader = new DequeRequestReader<>(
                    file, 1000, processor);
            readers.put(inst, reader);
            
            AStarDataPack data = reader.getNextData();
            SwingUtilities.invokeLater(() -> {
                State s = inst.getState();
                Vector3f pos = s.box.pos();
                pos.x = data.pos.x;
                pos.y = data.pos.y;
                pos.z = data.pos.z;
            });
            
        } catch (IOException e) {
            Logger.write(e);
        }
    }
    
    
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
     * Large slowdown when speed higher than vMax 
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
    static boolean raceStarted = false;
    
    public static void calcPhysics(Instance source, PStructAction pStruct,
            PhysicsContext pc, State state, Set<Instance> collisions, ProgressManager progress) {
        // <editor-fold defaultstate="collapsed" desc="ROUND START COUNTDOWN">
        if (beepCounter < 4 || GS.time < 0) {
            //System.out.println("Time is"+GS.time);
            pStruct.turn = 0.0f;
            pStruct.throwItem = false;
            pStruct.accel = 0.0f;
            pStruct.verticalVelocity = 0.0f;
            if (GS.time >= -4000 && beepCounter == 0){
                MusicManager.play("start_beep.wav", MusicManager.MUSIC_SFX);
                lastBeepTime = GS.time;
                beepCounter = 1;
                //System.out.println("Time is"+GS.time);
            } else if ((GS.time - lastBeepTime) >= 1000 && beepCounter == 1){
                MusicManager.play("start_beep.wav", MusicManager.MUSIC_SFX);
                lastBeepTime = GS.time;
                beepCounter = 2;
                //System.out.println("Time is"+GS.time);
            } else if ((GS.time - lastBeepTime) >= 1000 && beepCounter == 2){
                MusicManager.play("start_beep.wav", MusicManager.MUSIC_SFX);
                lastBeepTime = GS.time;
                beepCounter = 3;
                //System.out.println("Time is"+GS.time);
            } else if ((GS.time - lastBeepTime) >= 1000 && beepCounter == 3){
                MusicManager.play("start_start.wav", MusicManager.MUSIC_SFX);
                //System.out.println("Time is"+GS.time);
                beepCounter = 4;
            }
        } else {
            raceStarted = true;
        }
        // </editor-fold>

        // Create a modifyable state to reduce the number of objects creations.
        ModState s = new ModState(state);
        
        // ITEMS & CARS
        // If the instance intersects with a car or an item, use collision
        // dependant collision handeling.
        // Ignore the current actions.
        ModPhysicsContext modPC = new ModPhysicsContext(pc);
        for(ItemInterface item : s.activeItems){
            item.perform(source, pStruct, modPC, s);
        }

        if (collisions != null && !collisions.isEmpty()) {
            boolean calcPhysics = true;
            
            for (Instance instance : collisions) {
                boolean isDynamic = !source.isStatic() && !instance.isStatic();
                
                if (isDynamic) {
                    // Double non-static collisions are handled later.
                    calcPhysics = false;
                    CollisionManager.addCollision(source, instance,
                            pStruct, modPC, s, progress);
                    
                } else {
                    // Full or single static collisions are handled here.
                    if (instance instanceof Item) {
                        ((Item) instance).physicsAtCollision(
                                source, pStruct, modPC, s);
                    }
                }
            }
            
            if (calcPhysics) {
                calcPhysics(source, pStruct, modPC.createContext(), s, progress);
                source.setState(s.createState());
            }
            
        } else {
            calcPhysics(source, pStruct, modPC.createContext(), s, progress);
            source.setState(s.createState());
        }
    }
    
    // FOR AI TEST
    static int testCount = 0;
    
    /**
     * 
     * @param source
     * @param pStruct
     * @param pc
     * @param s
     */
    public static void calcPhysics(Instance source, PStructAction pStruct,
            PhysicsContext pc, ModState s, ProgressManager progress) {

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
        // air state detection helpers
        float gndClamp = 0.05f * (5 + Math.abs(s.velocity));
        boolean inAir = false; // If true this update, possible to fall next one
        // definite death barrier: never go this low
        float gndZ = -500;
        // Difference in height if inAir this update
        double deltaZ = dt * (s.verticalVelocity + 0.5 * pc.gravity * dt);
        
        if (!s.isResetting) {
            if (!source.isAI()) {
                // <editor-fold defaultstate="collapsed" desc="TRACK DETECTION"> 
                // Find road point closest to car
                float shortestDist = Float.POSITIVE_INFINITY;
                float dist;
                int ind = 0; // Current road point index
                // Check from checkpoint up until current point
                for (int i = Math.max(
                        (int) Math.floor(points.length * (progress.checkPoint / progress.cpAm)) - 5,
                        (int) Math.floor(points.length * (progress.checkPoint / progress.cpAm)));
                        i < Math.min(s.rIndex + 10, points.length); i++) {
                    if (Math.abs(s.box.pos().z - points[i].z) < 50) {
                        dist = (float)Math.sqrt(Math.pow(s.box.pos().x - points[i].x, 2) 
                                + (float)Math.pow(s.box.pos().y - points[i].y, 2));
                        if (dist < shortestDist) {
                            shortestDist = dist;
                            ind = i;
                        }
                    }
                }


                if (s.rIndex > points.length - 10) { // Early on track: check end points
                    for (int i = 0; i < 10; i++) {
                        if (Math.abs(s.box.pos().z - points[i].z) < 20) {
                            dist = (float)Math.sqrt(Math.pow(s.box.pos().x - points[i].x, 2) 
                                    + (float)Math.pow(s.box.pos().y - points[i].y, 2)
                                    /*+ (float)Math.pow(s.box.pos().z - points[i].z, 2)*/);
                            if (dist < shortestDist) {
                                shortestDist = dist;
                                ind = i;
                            }
                        }
                    }
                } else if (s.rIndex < 10) { // Late on track: check start points
                    for (int i = points.length - 10; i < points.length; i++) {
                        if (Math.abs(s.box.pos().z - points[i].z) < 20) {
                            dist = (float)Math.sqrt(Math.pow(s.box.pos().x - points[i].x, 2) 
                                    + (float)Math.pow(s.box.pos().y - points[i].y, 2)
                                    /*+ (float)Math.pow(s.box.pos().z - points[i].z, 2)*/);
                            if (dist < shortestDist) {
                                shortestDist = dist;
                                ind = i;
                            }
                        }
                    }
                }
                // Update the globally last reached road index
                if (s.onTrack)
                    s.rIndex = ind;
                // Find distance from the middle road curve
                float t = ((points[ind].x - s.box.pos().x)*tangents[ind].x
                        + (points[ind].y - s.box.pos().y)*tangents[ind].y
                        + (points[ind].z - s.box.pos().z)*tangents[ind].z)/
                        -(tangents[ind].x*tangents[ind].x + tangents[ind].y*tangents[ind].y 
                        + tangents[ind].z*tangents[ind].z);
                // direction and magnitude towards the track
                Vector3f dDir = new Vector3f(points[ind].x - s.box.pos().x + tangents[ind].x*t,
                        points[ind].y - s.box.pos().y + tangents[ind].y*t,
                        points[ind].z - s.box.pos().z + tangents[ind].z*t);
                dist = (float)Math.sqrt(dDir.x*dDir.x + dDir.y*dDir.y + dDir.z*dDir.z);
                // If you are outside of the track
                if (dist > trackWidth && s.onTrack) {
                    s.onTrack = false;
                    MusicManager.play("will_scream.wav", MusicManager.MUSIC_SFX);
                }
                //Vector3f rN = new Vector3f(-(float)Math.sqrt(6)/6, -(float)Math.sqrt(6)/6
                //        , (float)Math.sqrt(6)/3);
                Vector3f roadPos = new Vector3f(points[ind]);
                Vector3f rN = normals[ind];
                // </editor-fold>

                if (s.onTrack) {
                    // <editor-fold defaultstate="collapsed" desc="AIR TIME DETECTION"> 
                    gndZ = roadPos.z 
                            - (s.box.pos().x - roadPos.x) * normals[ind].x / normals[ind].z
                            - (s.box.pos().y - roadPos.y) * normals[ind].y / normals[ind].z;
                    // extra part after gnd is to compensate for small rounding errors
                    if (s.box.pos().z - gndZ < gndClamp && s.verticalVelocity <= 0.01) {
                        s.inAir = false;
                        s.box.pos().z = gndZ;
                    } else {
                        inAir = true;

                        //System.out.println(s.velocity + ": " + (s.box.pos().z - gndZ));
                    }
                    // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="HORIZONTAL ROTATION"> 
                    double y = normals[ind].y;
                    double x = normals[ind].x;
                    double z = normals[ind].z;
                    //calculating the values needed
                    double yz = Math.sqrt(y*y + z*z);
                    double yz_ang = Math.atan2(y, z);
                    double rotz = Math.atan2(x, yz);

                    //write the needed rotation to the rotation only do this with the final value
                    s.rotz = (float) (rotz * Math.sin(yz_ang - s.roty));
                    s.rotx = (float) (-rotz * Math.cos(yz_ang - s.roty));
                    // </editor-fold>

                    // <editor-fold defaultstate="collapsed" desc="PROGRESS MANAGEMENT"> 
                    progress.manageProgress(s.box.pos(), points.length, ind);
                    if (progress.finished) {
                        pStruct.accel = 0;
                        pStruct.turn = 0;
                    }
                    // </editor-fold>
                }
                // </editor-fold>
                else {
                    s.internRotx -= 0.15 * dt * s.velocity/Math.abs(s.velocity);
                }

                // <editor-fold defaultstate="collapsed" desc="LINEAR IMPROVEMENTS"> 
                // (ACCEL) Max speed regulation
                boolean noFriction = false;
                if ((pStruct.accel > 0 && s.velocity + linAccel*dt > pc.maxLinearVelocity) ||
                        (pStruct.accel < 0 && s.velocity - linAccel*dt < -pc.maxLinearVelocity)) {
                    pStruct.accel = 0;
                    noFriction = true;
                }
                // (ACCEL) Block manual acceleration when collision just happened
                if (s.collisionVelocity > pc.knockback / pc.accBlockDur) {
                    if (s.velocity > 1) {
                        s.velocity -= 0.5f;
                    } else if (s.velocity < -1) {
                        s.velocity += 0.5f;
                    }

                    //pStruct.accel = 0;
                }

                // (LINACCEL) Temporary slowdown after speedboost: not refined
                if (s.velocity + linAccel*dt > pc.maxLinearVelocity * 1.1 ||
                        s.velocity - linAccel*dt < -pc.maxLinearVelocity * 1.1)
                    linAccel *= (Math.abs(s.velocity - pc.maxLinearVelocity*1.1) + 1)
                            * pc.frictionConstant * pc.largeSlowDown;


                // (LINACCEL)/(VEL) Friction: When acceleration is 0, abs(v) decreases
                if (pStruct.accel == 0 && !noFriction) {
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
                        linAccel *= pc.brakeAccel;
                    }
                    linAccel *= pStruct.accel;
                }
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="ROTATIONAL IMPROVEMENTS"> 
                // (TURN)/(ROTVELOCITY) Turn correction for small velocities
                if (Math.abs(s.velocity) < 0.05) // not 0: causes teleport bug
                    pStruct.turn = 0;
                else if (Math.abs(s.velocity) < pc.turnCorrection)
                    rotationalVelocity *= (Math.abs(s.velocity) / pc.turnCorrection);

                // (TURN) Turn correction for negative velocities
                if (s.velocity < 0)
                    pStruct.turn = -pStruct.turn;
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="OTHER IMPROVEMENTS"> 
                // (ROTVELOCITY)/(LINACCEL) Air movement
                if (s.inAir) {
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
                    carDir = new Vector3f (
                            (float) Math.cos(s.roty),
                            (float) Math.sin(s.roty),
                            0);
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
                    carDir = new Vector3f (
                            (float) Math.cos(distAngle),
                            (float) Math.sin(distAngle),
                            0);
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
                if (s.verticalVelocity == 0 && !s.inAir)
                    s.verticalVelocity += pStruct.verticalVelocity;
                // Play boing if jumping
                if(playBoing){
                    MusicManager.play("boing.wav", MusicManager.MUSIC_SFX);
                    playBoing = false;
                }

                // forced air detection when jumping
                if (s.verticalVelocity > 0.3) {
                    s.inAir = true;
                }

                // When in the air
                if (s.inAir || !s.onTrack)  {
                    s.verticalVelocity += pc.gravity * dt;
                    ePos.z += deltaZ;
                    playBoing = false;
                }
                // When bouncing on the ground
                else if (Math.abs(s.verticalVelocity) > 0.01 && s.onTrack) {
                    s.verticalVelocity = -s.verticalVelocity * pc.bounceFactor;
                    playBoing = true;
                } 
                // When kinda done bouncing
                else {
                    s.verticalVelocity = 0;
                    playBoing = false;
                }

                // Limit upwards velocity
                if (s.verticalVelocity > 20)
                    s.verticalVelocity = 10;
                //Death barrier: reset
                if (ePos.z < points[s.rIndex].z - 50)
                    s.isResetting = true;
                // If in air this update, make decisions on that next update
                if (inAir)
                    s.inAir = true;
                // </editor-fold>

                // <editor-fold defaultstate="collapsed" desc="COLLISION CALCULATIONS"> 
                // Should be integrated into another class but still change pStruct
                Vector3f colPos = new Vector3f (0.0001f, 40, 1);
                double colRange = 2;
                double carRange = 6;
                // Collision detection
                /*if (s.box.pos().x + carRange/2 > colPos.x - colRange/2 &&
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
                } else */if (s.collisionVelocity > pc.knockback/1_000_000_000) {
                    // Slowly diminish the knockback over time
                    s.collisionVelocity *= pc.knockbackDur;
                    // Angle can change during bump: maybe looks better?
                    ePos = new Vector3f(
                           (float) (ePos.x + s.collisionVelocity * Math.cos(s.colAngle)), 
                           (float) (ePos.y + s.collisionVelocity * Math.sin(s.colAngle)),
                           ePos.z);

                } else {
                    // No collision happening.
                    // Set collision velocity to 0 when it was already really small.
                    s.collisionVelocity = 0;
                }
                // </editor-fold>
            } else if (raceStarted) {
                if (GS.time < 0) return;
                // <editor-fold defaultstate="collapsed" desc="TEST AI">
                DequeRequestReader<AStarDataPack> reader = readers.get(source);
                AStarDataPack data = reader.getNextData();
                ePos = data.pos;
                
                // Height calculation
                float shortestDist = Float.POSITIVE_INFINITY;
                float dist;
                int ind = 0; // Current road point index
                // Check from checkpoint up until current point
                for (int i = 0; i < points.length; i++) {
                    if (Math.abs(s.box.pos().z - points[i].z) < 50) {
                        dist = (float)Math.sqrt(Math.pow(s.box.pos().x - points[i].x, 2) 
                                + (float)Math.pow(s.box.pos().y - points[i].y, 2));
                        if (dist < shortestDist) {
                            shortestDist = dist;
                            ind = i;
                        }
                    }
                }
                ePos.z = points[ind].z + 0.5f;
                
                eV = data.v;
                eRot = data.rot - (float)Math.PI/2;
                // </editor-fold>
            } else {
                ePos = new Vector3f(4.5f,0,2.5f);
                eV = 0;
                eRot = (float)Math.PI/2;
            }
        }
        // <editor-fold defaultstate="collapsed" desc="RESET MOTION">
        // When the car has to reset to the last checkpoint
        else {
            // Find last checkpoint (last one)
            int resetInd = Math.round(points.length*(progress.checkPoint-1)
                    / progress.cpAm);
            // Intermediate position
            Vector3f diffPos = new Vector3f(points[resetInd].x - s.box.pos().x,
                    points[resetInd].y - s.box.pos().y, 
                    points[resetInd].z - s.box.pos().z);
            ePos = new Vector3f ((float)(s.box.pos().x + 0.1*diffPos.x),
                    (float)(s.box.pos().y + 0.1*diffPos.y),
                    (float)(s.box.pos().z + 0.1*diffPos.z));
            // Intermediate horizontal rotation
            float finalRot = (float)Math.atan2(-tangents[resetInd].x, -tangents[resetInd].y);
            finalRot = (float)((-(finalRot - Math.PI/2) + Math.PI*2) % (Math.PI*2));
            float diffRot = (float)(finalRot - ((s.roty)%(Math.PI*2)));
            eRot = (float)(s.roty + 0.1*diffRot);
            // Intermediate vertical
            double y = normals[resetInd].y;
            double x = normals[resetInd].x;
            double z = normals[resetInd].z;
            double yz = Math.sqrt(Math.pow(y,2) + Math.pow(z,2));
            double yz_ang = Math.atan2(y, z);
            double rotz = Math.atan2(x, yz);
            float rotzDif = (float) (rotz * Math.sin(yz_ang - s.roty));            
            float rotxDif = (float) (rotz * Math.cos(yz_ang - s.roty));
//            s.rotz = (float) (rotz * Math.sin(yz_ang - s.roty));
            s.rotx = (float) (-rotz * Math.cos(yz_ang - s.roty));
            s.rotz += 0.1*(rotzDif - s.rotz);
//            s.rotx += 0.1*(rotxDif - s.rotx);
            // Final velocity
            eV = 0;

//                s.rotz = (float) (rotz * Math.sin(yz_ang - s.roty));           
            s.internRotx -= 0.1 * s.internRotx;
//            s.internRotz += 0.1*(x - s.internRotz);
//            s.internRoty = 0;
//            s.internRotz = 0;
            
            if (Math.sqrt(diffPos.x*diffPos.x + diffPos.y*diffPos.y + diffPos.z*diffPos.z) < 1) {
                s.collisionVelocity = 0;
                s.verticalVelocity = 4;
                s.onTrack = true;
                eRot = finalRot;
                s.internRotx = 0;
                //
                float dist = 0;
                float shortestDist = 1000000;
                
                for (int i = 0; i < points.length; i++) {
                    if (Math.abs(s.box.pos().z - points[i].z) < 50) {
                        dist = (float)Math.sqrt(Math.pow(s.box.pos().x - points[i].x, 2) 
                                + (float)Math.pow(s.box.pos().y - points[i].y, 2));
                        if (dist < shortestDist) {
                            shortestDist = dist;
                            s.rIndex = i;
                        }
                    }
                }
                
                //s.rIndex = (int)Math.floor(points.length * (progress.checkPoint / progress.cpAm));
                // Drive again
                s.isResetting = false;
            }
            
//            s.internTrans = new Vector3f(); 
        }
        // </editor-fold>

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
        
        if (e2 == null) {
            Logger.write("collision e2 entry is empty", Logger.Type.ERROR);
            return;
        }
        
        if (!e2.inst.isAI() && !e1.inst.isAI()) {
            // <editor-fold defaultstate="collapsed" desc="COLLISION CALCULATIONS"> 
            double colAngle = Math.atan2( e1.ms.box.pos().x - e2.ms.box.pos().x, 
                    e1.ms.box.pos().y - e2.ms.box.pos().y);
            colAngle = (-(colAngle - Math.PI/2) + Math.PI*2) % (Math.PI*2);

            // Can only receive knockback once the last knockback is sufficiently small
            if (e1.ms.collisionVelocity < 1) {
                float difAngle1 = (float)Math.abs(e1.ms.internRoty - colAngle);
                if (difAngle1 > Math.PI/2)
                    difAngle1 -= Math.PI/2;
                float difAngle2 = (float)Math.abs(e2.ms.internRoty - colAngle);
                if (difAngle2 > Math.PI/2)
                    difAngle2 -= Math.PI/2;

                e1.ms.collisionVelocity = Math.abs(e1.ms.velocity) * e1.mpc.knockback * 0.5f
                        *(1f + (float)Math.cos(difAngle1)*0.5f);
                e1.ms.box.pos().x += e1.ms.collisionVelocity * Math.cos(colAngle);
                e1.ms.box.pos().y += e1.ms.collisionVelocity * Math.sin(colAngle);
                e1.ms.verticalVelocity = 1f + Math.abs(e1.ms.velocity)/8;

                e2.ms.collisionVelocity = Math.abs(e1.ms.velocity) * e1.mpc.knockback * 0.5f
                        *(1 + (float)Math.cos(difAngle2)*0.5f);
                e2.ms.box.pos().x += e1.ms.collisionVelocity * Math.cos(colAngle+Math.PI);
                e2.ms.box.pos().y += e1.ms.collisionVelocity * Math.sin(colAngle+Math.PI);
                e2.ms.verticalVelocity = 1f + Math.abs(e1.ms.velocity)/8;
            }
            e1.ms.colAngle = (float)colAngle;
        }
    }
    
    /**
     * Calculates and updates the physics of the instance given in the
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
                entry.ms, entry.progress);
        entry.inst.setState(entry.ms.createState());
        //System.out.println("Entry wants to calculate physics");
    }
    
    /**
     * Sets the points and normals of the given track as reference points.
     * 
     * @param track the track to get the points and normals of.
     */
    public static void setTrack(Track track) {
        Physics.track = track;
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
            d.out.println(new Point2D.Double(testDrive.get(i).pos.x, testDrive.get(i).pos.y));
            visual.addPoint(new Point2D.Double(testDrive.get(i).pos.x, -testDrive.get(i).pos.y));
        }
        visual.repaint();
        */
    }
    
    public static void main(String[] args) {
        Physics.physicsTestVisuals();
    }
}

