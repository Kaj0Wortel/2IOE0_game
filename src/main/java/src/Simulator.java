package src;

import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import src.Assets.*;
import src.OBJ.LoadOBJ;
import src.tools.Binder;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.event.keyAction.PlayerKeyAction;
import src.tools.update.Updateable;
import src.tools.update.Updater;

import javax.swing.*;

import java.util.List;

import static src.tools.update.Updateable.Priority.UPDATE_ALWAYS;

public class Simulator implements Updateable {

    private GL2 gl;

    private Binder binder;
    CameraKeyAction[] cameraActions;
    PlayerKeyAction[] playerActions;

    Instance player;

    public Simulator () {
        this.binder = new Binder();

        cameraActions = new CameraKeyAction[] {
                new CameraKeyAction(1, CameraKeyAction.MovementAction.LEFT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.RIGHT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.FORWARD),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.BACKWARD)
        };

        playerActions = new PlayerKeyAction[] {
                new PlayerKeyAction(1, PlayerKeyAction.MovementAction.LEFT),
                new PlayerKeyAction(1, PlayerKeyAction.MovementAction.RIGHT),
                new PlayerKeyAction(1, PlayerKeyAction.MovementAction.FORWARD),
                new PlayerKeyAction(1, PlayerKeyAction.MovementAction.BACKWARD)
        };

        SwingUtilities.invokeLater(() -> {
            Updater.addTask(this);
        });
    }

    public void setGL(GL2 gl){
        this.gl = gl;
    }

    public void initAssets() {
        //OBJCollection col = LoadOBJ.load(gl, GS.OBJ_DIR + "test.obj");
        OBJCollection col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        OBJCollection sp = LoadOBJ.load(gl, GS.OBJ_DIR + "sphere.obj");
        OBJCollection quad = LoadOBJ.load(gl, GS.OBJ_DIR + "quad.obj");
        
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj, new TextureImg(5,0.5f));
            Instance cubeInstance = new Instance(new Vector3f(0f, 0f, 0f),
                    1f, 0, 0, 0, texturedCube);
            player = cubeInstance;
            GS.addAsset(cubeInstance);
        }

        for (OBJObject obj : sp) {
            OBJTexture texturedCube = new OBJTexture(obj, new TextureImg(5,0.5f));
            Instance cubeInstance = new Instance(new Vector3f(0f, 20f, -5f),
                    1, 0, 0, 0, texturedCube);
            GS.addAsset(cubeInstance);
        }

        for (OBJObject obj : quad) {
            OBJTexture texturedCube = new OBJTexture(obj, new TextureImg(gl, "test.jpg",5,0.5f));
            Instance cubeInstance = new Instance(new Vector3f(0f, -3f, 0f),
                    10, 0, 0, 0, texturedCube);
            GS.addTerrain(cubeInstance);
        }

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

        if(player != null) {
            GS.getCamera().setFocus(player);
            processInput();
            GS.getCamera().calculateInstanceValues();
        }


    }
    
    @Override
    public void ignoreUpdate(long timeStamp) {
        prevTimeStamp = timeStamp;
    }

    @Override
    public Priority getPriority() {
        return UPDATE_ALWAYS;
    }

    private void addCollection(OBJCollection colletion){

    }

    private void processInput(){

        if(GS.getCamera().isOnPlayer()){
            for (PlayerKeyAction action : playerActions) {
                List<ControllerKey> keys = GS.getKeys(action);
                if (keys == null) return;
                if (GS.keyDet.werePressed(keys)) {
                    GS.getPlayerController().processKey(action.getAction());
                }
            }
        }else {
            for (CameraKeyAction action : cameraActions) {
                List<ControllerKey> keys = GS.getKeys(action);
                if (keys == null) return;
                if (GS.keyDet.werePressed(keys)) {
                    GS.getCameraController().processKey(action.getAction());
                }
            }
        }
    }

    public Instance getPlayer(){
        return this.player;
    }
}