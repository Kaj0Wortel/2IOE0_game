package src.Controllers;

import src.Renderer.Camera;
import src.tools.event.keyAction.CameraKeyAction;

public class CameraController {

    Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void processKey(CameraKeyAction.MovementAction e) {
        if(!camera.isOnPlayer()) {
            if (e == CameraKeyAction.MovementAction.LEFT) {
                camera.YawLeft();
            }
            if (e == CameraKeyAction.MovementAction.RIGHT) {
                camera.YawRight();
            }
            if (e == CameraKeyAction.MovementAction.FORWARD) {
                camera.MoveForward();
            }
            if (e == CameraKeyAction.MovementAction.BACKWARD) {
                camera.MoveBackwards();
            }
        }

        camera.calculateViewMatrix();
    }
}
