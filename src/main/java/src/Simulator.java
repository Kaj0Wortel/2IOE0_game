package src;

import com.jogamp.opengl.GL2;
import org.joml.Vector3f;
import src.Assets.AssetTexture;
import src.Assets.Cube;
import src.Assets.Instance;
import src.Assets.Texture;
import src.OBJ.LoadOBJ;
import src.Renderer.Camera;
import src.tools.Binder;

public class Simulator {

    private GL2 gl;

    private Binder binder;

    public Simulator () {
        this.binder = new Binder();
    }

    public void update(){
        GS.getAssets().get(0).rotx();
        GS.getAssets().get(0).roty();

        Camera c = GS.getCamera();
        //c.YawLeft();
        //c.YawLeft();

    }

    public void setGL(GL2 gl){
        this.gl = gl;
    }

    public void initAssets(){
        LoadOBJ.load(GS.OBJ_DIR + "test.obj");

        Cube cube = new Cube(gl,binder);
        AssetTexture texturedCube = new AssetTexture(cube, new Texture());
        Instance cubeInstance = new Instance(new Vector3f(0f,0f,-5f), 1, 0,0,0,texturedCube);
        GS.addAsset(cubeInstance);
    }

    public void cleanup(){
        binder.clean(gl);
    }
}
