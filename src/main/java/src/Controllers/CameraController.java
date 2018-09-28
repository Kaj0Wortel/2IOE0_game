
package src.Controllers;


// Own imports
import java.util.List;
import src.GS;
import src.Renderer.Camera;
import src.tools.event.ControllerKey;
import src.tools.event.keyAction.CameraKeyAction;


public class CameraController
        extends Controller {

    Camera camera;
    
    final private CameraKeyAction[] cameraActions;

    public CameraController(Camera camera) {
        super(false);
        this.camera = camera;
        
        cameraActions = new CameraKeyAction[] {
                new CameraKeyAction(1, CameraKeyAction.MovementAction.LEFT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.RIGHT),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.FORWARD),
                new CameraKeyAction(1, CameraKeyAction.MovementAction.BACKWARD)
        };
    }
    
    @Override
    public void controlUpdate(long dt) {
        if (GS.getCamera().isOnPlayer()) return;
        
        for (CameraKeyAction action : cameraActions) {
            List<ControllerKey> keys = GS.getKeys(action);
            if (keys == null) continue;
            
            if (GS.keyDet.werePressed(keys)) {
                if (action.getAction() == CameraKeyAction.MovementAction.LEFT) {
                    camera.yaw(dt / 3f);
                }
                if (action.getAction() == CameraKeyAction.MovementAction.RIGHT) {
                    camera.yaw(dt / 3f);
                }
                if (action.getAction() == CameraKeyAction.MovementAction.FORWARD) {
                    camera.move(dt / 16f);
                }
                if (action.getAction() == CameraKeyAction.MovementAction.BACKWARD) {
                    camera.move(dt / 16f);
                }
            }
        }
        
        camera.calculateViewMatrix();
    }
    
    
}
