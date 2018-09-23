package src;

import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import src.Assets.*;
import src.OBJ.LoadOBJ;
import src.tools.Binder;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.update.Updateable;
import src.tools.update.Updater;
import static src.tools.update.Updateable.Priority.UPDATE_ALWAYS;

import java.util.List;

import javax.swing.SwingUtilities;
import org.joml.Vector4f;
import src.tools.log.Logger;

public class Simulator implements Updateable {

    private GL2 gl;

    private Binder binder;
    CameraKeyAction[] cameraActions;

    public Simulator () {

        this.binder = new Binder();

        cameraActions = new CameraKeyAction[] {
                new CameraKeyAction(1, CameraKeyAction.MovementAction.LEFT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.RIGHT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.FORWARD),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.BACKWARD)
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
        
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj, new Texture(10,1));
            Instance cubeInstance = new Instance(new Vector3f(0f, 0f, 0f),
                    1f, 0, 0, 0, texturedCube);
            GS.addAsset(cubeInstance);
        }

        for (OBJObject obj : sp) {
            OBJTexture texturedCube = new OBJTexture(obj, new Texture(10,1));
            Instance cubeInstance = new Instance(new Vector3f(0f, 20f, -5f),
                    1, 0, 0, 0, texturedCube);
            GS.addAsset(cubeInstance);
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
        
        if(GS.getAssets().size() > 0) {
            GS.getAssets().get(0).roty(dt / 10f);
        }


        for (CameraKeyAction action : cameraActions) {
            List<ControllerKey> keys = GS.getKeys(action);
            if (keys == null) return;
            if (GS.keyDet.werePressed(keys)) {
                GS.getCameraController().processKey(action.getAction());
            }
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
}