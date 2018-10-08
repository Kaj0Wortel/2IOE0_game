package src.Assets;

import org.joml.Vector3f;
import src.OBJ.MTLObject;

import java.nio.IntBuffer;
import java.util.List;

public abstract class GraphicsObject {

    final protected String name;

    public abstract Vector3f getCenteredPosition();

    public abstract List<IntBuffer> getVao();
    public abstract int getVao(int id);

    public abstract List<Integer> getNrV();
    public abstract int getNrV(int id);

    public abstract List<MTLObject> getMaterials();
    
    public abstract int size();

    public GraphicsObject(String name){
        this.name = name;
    }
}
