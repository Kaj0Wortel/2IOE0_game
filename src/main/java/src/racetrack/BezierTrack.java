package src.racetrack;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import src.Shaders.ShaderProgram;
import src.tools.Binder;

import java.util.ArrayList;
import java.util.List;

public class BezierTrack extends Track{

    final private int nr_segment_vertices_col = 30;
    final private int nr_segment_vertices_row = 9; //Must be odd
    final private Vector3f position;
    final private float size;
    final private float rotx;
    final private float roty;
    final private float rotz;

    private Vector3f UP = new Vector3f(0,1,0);

    public BezierTrack(GL3 gl, Vector3f position, float size,
                       float rotx, float roty, float rotz) {
        this.position = position;
        this.rotx = rotx;
        this.roty = roty;
        this.rotz = rotz;
        this.size = size;

        super.setControl_points(new Vector3f[]{

                new Vector3f(0f,0f,0f), new Vector3f(0f,0f,30f), new Vector3f(10f,0f,40f), new Vector3f(40f,0f,40f),
                new Vector3f(40f,0f,40f), new Vector3f(70f,0f,40f), new Vector3f(80f,0f,80f), new Vector3f(80f,0f,40f),
                new Vector3f(80f,0f,40f), new Vector3f(80f,0f,-20f), new Vector3f(80f,0f,-60f), new Vector3f(60f,0f,-60f),
                new Vector3f(60f,0f,-60f), new Vector3f(-20f,0f,-60f), new Vector3f(0f,0f,-40f), new Vector3f(0f,0f,0f),
               /*
                new Vector3f(0f,0f,0f), new Vector3f(10f,0f,0f), new Vector3f(0f,0f,10f), new Vector3f(10f,0f,10f),
                new Vector3f(10f,0f,10f), new Vector3f(20f,0f,10f), new Vector3f(20f,0f,-10f), new Vector3f(0f,0f,-10f),
                new Vector3f(0f,0f,10f), new Vector3f(-20f,0f,-10f), new Vector3f(-20f,0f,10f), new Vector3f(-10f,0f,-10f),
                new Vector3f(-10f,0f,-10f), new Vector3f(0f,0f,10f), new Vector3f(-10f,0f,0f), new Vector3f(0f,0f,0f),
                */
        });

        super.setSegments(control_points.length/4);

        generateTrack(gl);
    }

    @Override
    public Vector3f getPoint(int segment, float t) {
        Vector3f point0 = new Vector3f(control_points[4*segment]);
        Vector3f point1 = new Vector3f(control_points[4*segment + 1]);
        Vector3f point2 = new Vector3f(control_points[4*segment + 2]);
        Vector3f point3 = new Vector3f(control_points[4*segment + 3]);

        point0.mul((float) (- Math.pow(t,3) + 3*Math.pow(t,2) - 3*t + 1));
        point1.mul((float) (3*Math.pow(t,3) - 6*Math.pow(t,2) + 3*t));
        point2.mul((float) (-3*Math.pow(t,3) + 3*Math.pow(t,2)));
        point3.mul((float) (Math.pow(t,3)));
        return new Vector3f().add(point0).add(point1).add(point2).add(point3);
    }

    public Vector3f getTangent(int segment, float t){
        Vector3f point0 = new Vector3f(control_points[4*segment]);
        Vector3f point1 = new Vector3f(control_points[4*segment + 1]);
        Vector3f point2 = new Vector3f(control_points[4*segment + 2]);
        Vector3f point3 = new Vector3f(control_points[4*segment + 3]);

        point0.mul((float) (-3*Math.pow(t,2) + 6*t - 3));
        point1.mul((float) (9*Math.pow(t,2) - 12*t + 3));
        point2.mul((float) (-9*Math.pow(t,2) * 6*t));
        point3.mul((float) (3*Math.pow(t,2)));

        return new Vector3f().add(point0).add(point1).add(point2).add(point3).normalize();
    }

    @Override
    public Vector3f[] getControlPoints() {
        return control_points;
    }

    @Override
    public Vector3f getHorizontalNormal(int segment, float t) {
        return null;
    }

    private void generateTrack(GL3 gl){

        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Float> textureCoordinates = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for(int i = 0; i < nr_of_segments; i++){
            for(int col = 0; col < nr_segment_vertices_col; col ++){
                float t = (float)col/(float) nr_segment_vertices_col;

                Vector3f point = getPoint(i, t);
                Vector3f tangent = getTangent(i, t);
                Vector3f horNormal = new Vector3f(tangent).cross(UP).normalize();

                for(int row = 0; row < nr_segment_vertices_row; row ++){
                    Vector3f extrude = new Vector3f(horNormal);
                    extrude.mul((float)row-4.5f);
                    Vector3f curPoint = new Vector3f(point).add(extrude);
                    vertices.add(curPoint.x);
                    vertices.add(curPoint.y);
                    vertices.add(curPoint.z);
                    normals.add(UP.x);
                    normals.add(UP.y);
                    normals.add(UP.z);
                    textureCoordinates.add(t);
                    textureCoordinates.add(((float)row + 1.0f)/2f);
                }
            }
            System.out.println(vertices.size()/3);

        }
        for(int i = 0; i < nr_of_segments; i++){
            int pointer = i * nr_segment_vertices_row * nr_segment_vertices_col;

            for(int col = 0; col < nr_segment_vertices_col-1; col++){
                for(int row = 0; row < nr_segment_vertices_row-1; row++){
                    indices.add(col * (nr_segment_vertices_row) + row + pointer);
                    indices.add((col+1) * (nr_segment_vertices_row) + row + pointer);
                    indices.add(col * (nr_segment_vertices_row) + row + pointer + 1);

                    indices.add(col * (nr_segment_vertices_row) + row + pointer + 1);
                    indices.add((col+1) * (nr_segment_vertices_row) + row + pointer);
                    indices.add((col+1) * (nr_segment_vertices_row) + row + pointer + 1);
                }
            }

            int curCol = nr_segment_vertices_col - 1;

            int p;
            if(i+1 < 4){
                p = (i+1) * nr_segment_vertices_row * nr_segment_vertices_col;
            }else{
                p = 0;
            }
            for(int row = 0; row < nr_segment_vertices_row-1; row++){

                indices.add(curCol * nr_segment_vertices_row + row + pointer);
                indices.add(row + p);
                indices.add(curCol * nr_segment_vertices_row + row + pointer + 1);

                indices.add(curCol * nr_segment_vertices_row + row + pointer + 1);
                indices.add(row + p);
                indices.add(row + p + 1);

            }

        }

        setVAOValues(Binder.loadVAO(gl,
                toFloatArray(vertices),
                toFloatArray(textureCoordinates),
                toFloatArray(normals),
                toIntegerArray(indices)),
                indices.size());
    }

    private float[] toFloatArray(List<Float> floats){
        float[] floatar = new float[floats.size()];
        for(int i = 0; i < floats.size(); i++){
            floatar[i] = floats.get(i);
        }
        return floatar;
    }

    private int[] toIntegerArray(List<Integer> integers){
        int[] intar = new int[integers.size()];
        for(int i = 0; i < integers.size(); i++){
            intar[i] = integers.get(i);
        }
        return intar;
    }

    public void draw(GL3 gl, ShaderProgram shader){
        shader.loadModelMatrix(gl, getTransformationMatrix());
        //shader.loadTextureLightValues(gl, model.getTextureImg().getShininess(),
                //model.getTextureImg().getReflectivity());

        gl.glBindVertexArray(vao.get(0));
        gl.glEnableVertexAttribArray(0);
        gl.glEnableVertexAttribArray(1);
        gl.glEnableVertexAttribArray(2);
        gl.glDrawElements(GL2.GL_TRIANGLES, nrV,
                GL2.GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
        gl.glDisableVertexAttribArray(2);

        gl.glBindVertexArray(0);
    }

    public Matrix4f getTransformationMatrix() {
        Matrix4f transformationMatrix = new Matrix4f();
        transformationMatrix.identity();
        transformationMatrix.translate(position);
        transformationMatrix.rotate((float) Math.toRadians(rotx), 1, 0, 0);
        transformationMatrix.rotate(
                (float) Math.toRadians(roty), 0, 1, 0);
        transformationMatrix.rotate((float) Math.toRadians(rotz), 0, 0, 1);
        transformationMatrix.scale(size, size, size);

        return transformationMatrix;
    }

}
