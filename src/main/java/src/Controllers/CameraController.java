
package src.Controllers;


// Own imports
import java.util.List;
import src.GS;
import src.Renderer.Camera;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CameraKeyAction;
import src.tools.event.keyAction.action.CameraMovementAction;


public class CameraController
        extends Controller {

    Camera camera;
    
    final private CameraKeyAction[] cameraActions;

    public CameraController(Camera camera) {
        super(false);
        this.camera = camera;
        
        CameraMovementAction[] values = CameraMovementAction.values();
        cameraActions = new CameraKeyAction[values.length];
        for (int i = 0; i < values.length; i++) {
            cameraActions[i] = new CameraKeyAction(1, values[i]);
        }
    }
    
    @Override
    public void controlUpdate(long dt) {
        if (!GS.camera.isOnPlayer()) {
            for (CameraKeyAction action : cameraActions) {
                List<ControllerKey> keys = GS.getKeys(action);
                if (keys == null) continue;
                
                if (GS.keyDet.werePressed(keys)) {
                    if (action.getAction() == CameraMovementAction.LEFT) {
                        camera.yaw(dt / 3f);
                    }
                    if (action.getAction() == CameraMovementAction.RIGHT) {
                        camera.yaw(dt / 3f);
                    }
                    if (action.getAction() == CameraMovementAction.FORWARD) {
                        camera.move(dt / 16f);
                    }
                    if (action.getAction() == CameraMovementAction.BACKWARD) {
                        camera.move(dt / 16f);
                    }
                }
            }
        }
        
        if (!camera.isOnPlayer() && GS.player != null) {
            camera.setFocus(GS.player);
        }
        
        if (camera.isOnPlayer()) {
            camera.calculateInstanceValues();
        }
    }
    
    
}
