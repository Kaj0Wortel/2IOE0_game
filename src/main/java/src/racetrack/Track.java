package src.racetrack;

import org.joml.Vector3f;

import java.nio.IntBuffer;

public abstract class Track {

    protected Vector3f[] control_points;
    protected int nr_of_segments;

    protected IntBuffer vao;
    protected int nrV;

    public abstract Vector3f getPoint(int segment, float t);

    public abstract Vector3f[] getControlPoints();

    public abstract Vector3f getHorizontalNormal(int segment, float t);

    public void setControl_points(Vector3f[] control_points){
        this.control_points = control_points;
    }

    public void setSegments(int nr){
        nr_of_segments = nr;
    }

    public void setVAOValues(IntBuffer vao, int nrV){
        this.vao = vao;
        this.nrV = nrV;
    }

    public IntBuffer getVao() {
        return vao;
    }

    public int getNrV() {
        return nrV;
    }
}
