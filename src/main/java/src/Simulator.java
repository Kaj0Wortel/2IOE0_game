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

import javax.swing.*;
import java.util.List;

import static src.tools.update.Updateable.Priority.UPDATE_ALWAYS;

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
        
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj, new Texture());
            Instance cubeInstance = new Instance(new Vector3f(0f, 0f, 5f),
                    1, 0, 0, 0, texturedCube);
            GS.addAsset(cubeInstance);
        }
    }

    public void cleanup(){
        binder.clean(gl);
    }


    @Override
    public void performUpdate(long timeStamp) throws InterruptedException {
        if(GS.getAssets().size() > 0) {
            GS.getAssets().get(0).rotx();
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
    public Priority getPriority() {
        return UPDATE_ALWAYS;
    }
}