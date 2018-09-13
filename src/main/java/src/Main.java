
package src;


// Own imports

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import org.joml.Vector3f;
import src.Controllers.CameraController;
import src.Renderer.Camera;
import src.Renderer.Renderer;
import src.tools.update.Updater;

// Java imports


/**
 * Class that is invoked when started.
 */
public class Main {
    
    public static void main(String[] args) {
        GS.init();

        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities cap = new GLCapabilities(profile);
        GLCanvas canvas = new GLCanvas(cap);

        FPSAnimator animator = new FPSAnimator(canvas, 60);

        Camera camera = new Camera(new Vector3f(0,0,0),0,0,0);
        GS.camera = camera;


        Simulator simulator = new Simulator();
        Renderer renderer = new Renderer(simulator, 1080, 720);

        canvas.addGLEventListener(renderer);
        canvas.setSize(1080, 720);


        CameraController cameraController = new CameraController(camera);

        GS.mainPanel.add(canvas);
        GS.mainPanel.setSize(1080, 720);

        animator.start();
        renderer.cleanup();
        
        Updater.start();
    }
    
}
