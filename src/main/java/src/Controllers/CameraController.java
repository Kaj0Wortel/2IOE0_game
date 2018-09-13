package src.Controllers;

import src.Renderer.Camera;
import src.tools.event.Key;

import java.util.Set;

public class CameraController {

    Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void processKey(Set<Key> e) {

        if(e.contains(Key.A)) {
            System.out.println("Hi");
            camera.YawLeft();
        }
        if(e.contains(Key.D)) {
            camera.YawRight();
        }

        camera.calculateViewMatrix();
    }
}
