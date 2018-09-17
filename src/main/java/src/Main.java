
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
import src.tools.event.Key;
import src.tools.event.keyAction.CarKeyAction;
import src.tools.event.keyAction.CarKeyAction.MovementAction;
import src.tools.update.Updateable;
import src.tools.update.Updater;

import java.util.List;

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

        Camera camera = new Camera(new Vector3f(0, 0, 0), 0, 0, 0);
        CameraController cameraController = new CameraController(camera);
        GS.camera = camera;
        GS.cameraController = cameraController;
        
        Simulator simulator = new Simulator();
        Renderer renderer = new Renderer(simulator, 1080, 720);

        canvas.addGLEventListener(renderer);
        canvas.setSize(1080, 720);

        GS.mainPanel.add(canvas);
        GS.mainPanel.setSize(1080, 720);

        animator.start();
        renderer.cleanup();
        
        Updater.start();
        Updateable up = new TmpUpdateable(1);
        Updater.addTask(up);
    }
    
    // BEGIN TMP
    private static class TmpUpdateable
            implements Updateable {
        
        final private CarKeyAction[] actions;
        
        public TmpUpdateable(int id) {
            actions = new CarKeyAction[] {
                new CarKeyAction(id, MovementAction.LEFT),
                new CarKeyAction(id, MovementAction.RIGHT)
            };
        }
        
        @Override
        public void performUpdate(long timeStamp)
                throws InterruptedException {
            for (CarKeyAction action : actions) {
                List<Key> keys = GS.getKeys(action);
                if (keys == null) return;
                if (GS.keyDet.werePressed(keys)) {
                    System.out.println(action.getAction());
                }
            }
        }
        
        @Override
        public Priority getPriority() {
            return Priority.UPDATE_ALWAYS;
        }
        
        
    }
    
    // END TMP
    
}
