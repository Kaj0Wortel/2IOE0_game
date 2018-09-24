package src.Assets;

import org.joml.Vector3f;
import src.OBJ.MTLObject;

import java.nio.IntBuffer;

public abstract class Object {

    final protected String name;

    protected MTLObject mltObject = null;
    protected IntBuffer vao;
    protected int nrV;

    public abstract Vector3f getCenteredPosition();

    public abstract IntBuffer getVao();

    public abstract int getNrV();

    public Object(String name){
        this.name = name;
    }
}
