
package src;


// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.*;
import src.Assets.instance.*;
import src.Assets.skybox.Skybox;
import src.Controllers.AIController;
import src.OBJ.LoadOBJ;
import src.Physics.PhysicsContext;
import src.racetrack.BezierTrack;
import src.tools.Binder;
import src.tools.PosHitBox3f;
import src.tools.update.Updateable;
import src.tools.update.Updater;

import javax.swing.*;

import static src.Simulator.TYPE.*;
import static src.tools.update.Updateable.Priority.UPDATE_ALWAYS;

// Own imports
// Java imports


public class Simulator
        implements Updateable {

    public static enum TYPE {
        CAR, ITEM, TRACK, ENVIRONMENT_TYPE, PLAYER, OTHER;
    }

    private GL3 gl;

    private Binder binder;

    public Simulator () {
        this.binder = new Binder();
        
        SwingUtilities.invokeLater(() -> {
            Updater.addTask(this);
        });
    }

    public void setGL(GL3 gl){
        this.gl = gl;
    }

    public void initAssets() {
        /*
        //Terrain terrain = new Terrain(gl);
        //OBJTexture texturedTerrain = new OBJTexture(terrain,
          //      new TextureImg(gl,"test.jpg",0, 0));
        PosHitBox3f box = new PosHitBox3f(
                new Vector3f(texturedTerrain.getAsset().getCenteredPosition())
                .add(new Vector3f(0, -2, 0)), new Vector3f(), 2f, 2f, 6f);
        Instance terrainInstance = new TerrainInstance(box, 1f, 0f, 0f, 0f,
                texturedTerrain, 0f, new PhysicsContext());
        GS.addTerrain(terrainInstance);
       */
        
        ThrowingItemFactory.init(gl);
        
        PosHitBox3f box;
        
        OBJCollection col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        OBJCollection sp = LoadOBJ.load(gl, GS.OBJ_DIR + "dragon.obj");
        OBJCollection car = LoadOBJ.load(gl, GS.OBJ_DIR + "car.obj");
        OBJCollection car2 = LoadOBJ.load(gl, GS.OBJ_DIR + "offroadcar.obj");
        
        // (0,0,0) REFERENCE
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(col,
                    new TextureImg(5, 0.5f));
            //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
            box = obj.createBoundingBox();
            //box.setPosKeepHitBox();
            box.translate(new Vector3f(0f, 0f, 0f));
            Instance cubeInstance = new Car(box,
                    1f, 0, 0, 0, texturedCube, 0, new PhysicsContext());
            GS.addAsset(cubeInstance);
        }


        addToGamestate(ENVIRONMENT_TYPE, col, new Vector3f(-40f, 1f, 0.001f), 1, 0, 0, 0, 0,
                new TextureImg(5, 0.5f), null, EnvironmentItem.Type.STATIC_OBSTACLE);

        addToGamestate(ENVIRONMENT_TYPE, col, new Vector3f(0f, 1f, -40f), 1, 0, 0, 0, 0,
                new TextureImg(5, 0.5f), null, EnvironmentItem.Type.SPEED_BOOST);



        // SLOWDOWN TEST
        /*for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(100f, -52f, 0), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    50f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SLOW_DOWN);
            GS.addAsset(cubeInstance);
        }*/
        // SLOPE TEST
        /*for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(-100f, -81f, -100f), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    150f, 0, -45, -35.26438968f, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SLOW_DOWN);
            GS.addAsset(cubeInstance);
        }*/
        

        addToGamestate(ITEM, col, new Vector3f(0f, -115f, 540f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-10f, -115f, 540f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(10f, -115f, 540f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1015f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1025f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1035f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(80f, -52f, -5f), 1, 45, 45, 45, 0, new TextureImg(5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-40f,-52f, 30f), 1, 45, 45, 45, 0, new TextureImg(5,0.5f), null, null);

        addToGamestate(OTHER, sp, new Vector3f(0f, -60f, 500f), 4, 0, -90, 0, 0, new TextureImg(5, 0.5f), null, null);

        Instance aiCar = addToGamestate(CAR, car, new Vector3f(0,2,0), 5,0,180,0,90, new TextureImg(5,0.5f),null,null);
        new AIController((Car) aiCar);

        addToGamestate(PLAYER, car2, new Vector3f(0,2,-4), 3, 0, 180, 0, -90, new TextureImg(5, 3f), null, null);

        addLight(new Vector3f(30000f, 50000f, 1f),
                new Vector3f(1f, 1f, 1f));

        addGUI(new TextureImg(gl,"test_icon.png"),
                new Vector2f(-0.5f, -0.5f), new Vector2f(0.25f, 0.25f));

        addToGamestate(TRACK, null, new Vector3f(0,1,-5), 3, 0,0,0, 0, new TextureImg(gl,"rainbow_road.png"),
                new TextureImg(gl, "tileNormalMap.png"), null);

        addSkybox();
        
        System.out.println("Assets initialized");

    }

    public void cleanup(){
        binder.clean(gl);
    }


    long prevTimeStamp = System.currentTimeMillis();
    @Override
    public void performUpdate(long timeStamp)
            throws InterruptedException {
        long dt = timeStamp - prevTimeStamp;
        prevTimeStamp = timeStamp;
    }
    
    @Override
    public void ignoreUpdate(long timeStamp) {
        prevTimeStamp = timeStamp;
    }
    
    @Override
    public Priority getPriority() {
        return UPDATE_ALWAYS;
    }

    public Instance addToGamestate(TYPE type, OBJCollection col,
            Vector3f position, int size, int rotx, int roty, int rotz,
            int integratedRotation, TextureImg texture, TextureImg normalMap,
            EnvironmentItem.Type envType){
        Instance cubeInstance = null;
        switch(type){
            case ITEM:{
                for(OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(-40f, -52f, 30f), 2f, 2f, 2f);
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new PickupItem(box,
                            size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext());
                    GS.addItem((Item) cubeInstance);
                }
                break;
            }
            case ENVIRONMENT_TYPE:{
                for (OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(0f, 1f, -40f), 2f, 2f, 6f);
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new EnvironmentItem(box,
                            size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext(),
                            envType);
                    GS.addAsset(cubeInstance);
                }
                break;
            }
            case TRACK:{
                BezierTrack testTrack = new BezierTrack(
                        position, size, rotx, roty, rotz,
                        texture,
                        normalMap);
                testTrack.generateTrack(gl);
                GS.setTrack(testTrack);
                break;
            }
            case CAR:{
                // CAR
                OBJTexture texturedCube = new OBJTexture(col,
                        texture);
                //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
                PosHitBox3f box = col.createBoundingBox();
                //box.setPosKeepHitBox();
                box.translate(position);
                cubeInstance = new Car(box,
                        size, rotx, roty, rotz, texturedCube,
                        integratedRotation, new PhysicsContext());
                GS.cars.add((Car) cubeInstance);
                GS.addMaterialAsset(cubeInstance);
                break;
            }
            case PLAYER:{
                // CAR
                OBJTexture texturedCube = new OBJTexture(col,
                        texture);
                //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
                PosHitBox3f box = col.createBoundingBox();
                //box.setPosKeepHitBox();
                box.translate(position);
                //box.pos().
                cubeInstance = new Car(box,
                        size/1.75f, rotx, roty, rotz, texturedCube, 
                        integratedRotation, new PhysicsContext());
                GS.player = (Car) cubeInstance;
                GS.cars.add((Car) cubeInstance);
                GS.addMaterialAsset(cubeInstance);
                break;
            }
            case OTHER:{
                for (OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(0f, -60f, 500f));
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new Car(box,
                            size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext());
                    GS.addAsset(cubeInstance);
                }
                break;
            }
        }
        
        return cubeInstance;
    }

    public void addGUI(TextureImg texture, Vector2f topright, Vector2f size){
        GUI test = new GUI(texture.getTexture(),
                topright, size);
        GS.addGUI(test);
    }

    public void addLight(Vector3f position, Vector3f color){
        Light light = new Light(position,
                color);
        GS.addLight(light);
    }

    public void addSkybox(){
        Skybox skybox = new Skybox(gl);
        GS.setSkybox(skybox);
    }
}