
package src.Controllers;


// Own imports
import src.Renderer.Camera;
import src.tools.event.keyAction.CameraKeyAction;


public class CameraController {

    Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void processKey(CameraKeyAction.MovementAction e, long dt) {
        if(!camera.isOnPlayer()) {
            if (e == CameraKeyAction.MovementAction.LEFT) {
                camera.yaw(dt / 3f);
            }
            if (e == CameraKeyAction.MovementAction.RIGHT) {
                camera.yaw(dt / 3f);
            }
            if (e == CameraKeyAction.MovementAction.FORWARD) {
                camera.move(dt / 16f);
            }
            if (e == CameraKeyAction.MovementAction.BACKWARD) {
                camera.move(dt / 16f);
            }
        }

        camera.calculateViewMatrix();
    }
}
