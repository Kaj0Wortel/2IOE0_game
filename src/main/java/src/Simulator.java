
package src;


// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import org.joml.Vector3f;
import src.Assets.*;
import src.Assets.instance.Car;
import src.Assets.instance.EnvironmentItem;
import src.Assets.instance.Instance;
import src.Assets.instance.TerrainInstance;
import src.Assets.skybox.Skybox;
import src.OBJ.LoadOBJ;
import src.Physics.PhysicsContext;
import src.racetrack.BezierTrack;
import src.tools.Binder;
import src.tools.Box3f;
import src.tools.update.Updateable;
import src.tools.update.Updater;

import javax.swing.*;

import static src.tools.update.Updateable.Priority.UPDATE_ALWAYS;

// Own imports
// Java imports


public class Simulator
        implements Updateable {

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
       Terrain terrain = new Terrain(gl);
        OBJTexture texturedTerrain = new OBJTexture(terrain,
                new TextureImg(gl,"test.jpg",0, 0));
        Box3f box = new Box3f(new Vector3f(texturedTerrain.getAsset()
                .getCenteredPosition())
                .add(new Vector3f(0, -2, 0)), 2f, 2f, 6f);
        Instance terrainInstance = new TerrainInstance(box, 1f, 0f, 0f, 0f,
                texturedTerrain, 0f, new PhysicsContext());
        GS.addTerrain(terrainInstance);
        //Box3f box; 
       
       
        OBJCollection col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        OBJCollection sp = LoadOBJ.load(gl, GS.OBJ_DIR + "dragon.obj");
        OBJCollection car = LoadOBJ.load(gl, GS.OBJ_DIR + "car.obj");
        
        // (0,0,0) REFERENCE
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
            Instance cubeInstance = new Car(box,
                    1f, 0, 0, 0, texturedCube, 0, new PhysicsContext());
            GS.addAsset(cubeInstance);
        }
        
        // COLLISION TEST
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(-40f, 1f, 0.001f), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    2f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.STATIC_OBSTACLE);
            GS.addAsset(cubeInstance);
        }
        
        // SPEEDBOOST TEST
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(0f, 1f, -40f), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    1f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SPEED_BOOST);
            GS.addAsset(cubeInstance);
        }
        
        // SLOWDOWN TEST
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(100f, -52f, 0), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    50f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SLOW_DOWN);
            GS.addAsset(cubeInstance);
        }
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
        
        // CAR
        for (OBJObject obj : car){
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
            Instance cubeInstance = new Car(box,
                    5f, 0, 180, 0, texturedCube, 90, new PhysicsContext());
            GS.player = cubeInstance;
            GS.addAsset(cubeInstance);
        }
        
        // REALLY COOL DRAGON
        for (OBJObject obj : sp) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(0f, 0f, -5f));
            Instance cubeInstance = new Car(box,
                    1, 0, 0, 0, texturedCube, 0, new PhysicsContext());
            GS.addAsset(cubeInstance);
        }

        Light light = new Light(new Vector3f(10000f, 50000f, -10000f),
                new Vector3f(1f, 1f, 1f));
        GS.addLight(light);

        GUI test = new GUI(new TextureImg(gl,"test_icon.png").getTexture(), new Vector2f(-0.5f,-0.5f), new Vector2f(0.25f,0.25f));
        GS.addGUI(test);

        BezierTrack testTrack = new BezierTrack(new Vector3f(0,1,-5),3f,0,0,0, new TextureImg(gl,"rainbow_road.png"), new TextureImg(gl, "road_normal.png"));
        testTrack.generateTrack(gl);
        GS.setTrack(testTrack);

        Skybox skybox = new Skybox(gl);
        GS.setSkybox(skybox);
        
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
}