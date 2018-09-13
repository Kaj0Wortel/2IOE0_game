package src.Controllers;

import src.Renderer.Camera;

import java.awt.event.KeyEvent;

public class CameraController {

    Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public void processKey(KeyEvent e) {
        switch(e.getKeyChar()){
            case 'a':
                camera.YawLeft();
                break;
            case 'd':
                camera.YawRight();
                break;
            case 'w':

                break;
            case 's':
                break;
        }

        camera.calculateViewMatrix();
    }
}
