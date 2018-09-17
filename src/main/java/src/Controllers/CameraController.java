package src.Controllers;

import src.Renderer.Camera;
import src.tools.event.keyAction.CameraKeyAction;

public class CameraController {

    Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void processKey(CameraKeyAction.MovementAction e) {

        System.out.println(e);

        if(e == CameraKeyAction.MovementAction.LEFT) {
            camera.YawLeft();
        }
        if(e == CameraKeyAction.MovementAction.RIGHT) {
            camera.YawRight();
        }

        camera.calculateViewMatrix();
    }
}
