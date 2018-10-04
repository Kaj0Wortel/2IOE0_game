package src.racetrack;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.Assets.TextureImg;
import src.Shaders.RacetrackShader;
import src.tools.Binder;

import java.util.ArrayList;

public class BezierTrack extends Track {

    final private int nr_segment_vertices_col = 150;
    final private int nr_segment_vertices_row = 17; //Must be odd
    private int lane_width = 7;
    private int scale_points = 5;

    private Vector3f UP = new Vector3f(0, 1, 0);

    public BezierTrack(Vector3f position, float size,
                       float rotx, float roty, float rotz, TextureImg texture, TextureImg bumbmap) {
        super(position, size, rotx, roty, rotz,texture, bumbmap);

        super.setControl_points(new Vector3f[]{

                new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 3f), new Vector3f(1f, 0f, 4f), new Vector3f(4f, 0f, 4f),
                new Vector3f(4f, 0f, 4f), new Vector3f(7f, 0f, 4f), new Vector3f(8f, 0f, 8f), new Vector3f(8f, 0f, 4f),
                new Vector3f(8f, 0f, 4f), new Vector3f(8f, 0f, -2f), new Vector3f(8f, 0f, -6f), new Vector3f(6f, 0f, -6f),
                new Vector3f(6f, 0f, -6f), new Vector3f(-2f, 0f, -6f), new Vector3f(0f, 0f, -4f), new Vector3f(0f, 0f, 0f),
        });

        super.setSegments(control_points.length / 4);
    }

    @Override
    public void setShaderAndRenderMatrices(RacetrackShader shader, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        this.shader = shader;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
    }

    @Override
    public Vector3f getPoint(int segment, float t) {
        Vector3f p0 = new Vector3f(control_points[4 * segment]).mul(scale_points);
        Vector3f p1 = new Vector3f(control_points[4 * segment + 1]).mul(scale_points);
        Vector3f p2 = new Vector3f(control_points[4 * segment + 2]).mul(scale_points);
        Vector3f p3 = new Vector3f(control_points[4 * segment + 3]).mul(scale_points);

        Vector4f u = new Vector4f(
                (float) Math.pow(t, 3), (float) Math.pow(t, 2), t, 1);
        Matrix4f gTranspose = new Matrix4f(
                p0.x, p0.y, p0.z, 1,
                p1.x, p1.y, p1.z, 1,
                p2.x, p2.y, p2.z, 1,
                p3.x, p3.y, p3.z, 1
        );

        Vector4f gTmTu = u.mul(gTranspose.mul(mTranspose));
        return new Vector3f(gTmTu.x, gTmTu.y, gTmTu.z);
    }

    final private static Matrix4f mTranspose = new Matrix4f(
            -1, 3, -3, 1,
            3, -6, 3, 0,
            -3, 3, 0, 0,
            1, 0, 0, 0
    );

    /**
     * @param t
     * @return Note that:
     * {@code U^T * (M * G) = G^T * M^T * U}
     * @see #getPoint(int, float)
     */
    @Override
    public Vector3f getTangent(int segment, float t) {
        Vector3f p0 = new Vector3f(control_points[4 * segment]).mul(scale_points);
        Vector3f p1 = new Vector3f(control_points[4 * segment + 1]).mul(scale_points);
        Vector3f p2 = new Vector3f(control_points[4 * segment + 2]).mul(scale_points);
        Vector3f p3 = new Vector3f(control_points[4 * segment + 3]).mul(scale_points);

        Vector4f u = new Vector4f((float) (3 * Math.pow(t, 2)), 2 * t, 1, 0);
        Matrix4f gTranspose = new Matrix4f(
                p0.x, p0.y, p0.z, 1,
                p1.x, p1.y, p1.z, 1,
                p2.x, p2.y, p2.z, 1,
                p3.x, p3.y, p3.z, 1
        );

        Vector4f gTmTu = u.mul(gTranspose.mul(mTranspose));
        return new Vector3f(gTmTu.x, gTmTu.y, gTmTu.z).normalize().negate();
    }

    public void generateTrack(GL3 gl) {
        ArrayList<Float> vertices = new ArrayList<>();
        ArrayList<Float> normals = new ArrayList<>();
        ArrayList<Float> textureCoordinates = new ArrayList<>();
        ArrayList<Integer> indices = new ArrayList<>();

        for (int i = 0; i < nr_of_segments; i++) {
            for (int col = 0; col < nr_segment_vertices_col; col++) {
                float t = (float) col / (float) nr_segment_vertices_col;
                Vector3f point = getPoint(i, t);
                Vector3f tangent = getTangent(i, t);

                for (int row = 0; row < nr_segment_vertices_row; row++) {
                    Vector3f normal = new Vector3f(tangent);
                    normal.cross(UP).normalize().mul(lane_width).mul(nMap(row));
                    Vector3f addedNormal = new Vector3f();
                    point.add(normal, addedNormal);

                    vertices.add(addedNormal.x);
                    vertices.add(addedNormal.y);
                    vertices.add(addedNormal.z);
                    normals.add(UP.x);
                    normals.add(UP.y);
                    normals.add(UP.z);
                    textureCoordinates.add(t);
                    textureCoordinates.add(((float) row) / ((float) nr_segment_vertices_row - 1));
                }
            }
        }
        for (int i = 0; i < nr_of_segments; i++) {
            int pointer = i * nr_segment_vertices_row * nr_segment_vertices_col;

            for (int col = 0; col < nr_segment_vertices_col - 1; col++) {
                for (int row = 0; row < nr_segment_vertices_row - 1; row++) {
                    indices.add(col * (nr_segment_vertices_row) + row + pointer);
                    indices.add((col + 1) * (nr_segment_vertices_row) + row + pointer);
                    indices.add(col * (nr_segment_vertices_row) + row + pointer + 1);

                    indices.add(col * (nr_segment_vertices_row) + row + pointer + 1);
                    indices.add((col + 1) * (nr_segment_vertices_row) + row + pointer);
                    indices.add((col + 1) * (nr_segment_vertices_row) + row + pointer + 1);
                }
            }

            int curCol = nr_segment_vertices_col - 1;

            int p;
            if (i + 1 < nr_of_segments) {
                p = (i + 1) * nr_segment_vertices_row * nr_segment_vertices_col;
            } else {
                p = 0;
            }
            for (int row = 0; row < nr_segment_vertices_row - 1; row++) {

                indices.add(curCol * nr_segment_vertices_row + row + pointer);
                indices.add(row + p);
                indices.add(curCol * nr_segment_vertices_row + row + pointer + 1);

                indices.add(curCol * nr_segment_vertices_row + row + pointer + 1);
                indices.add(row + p);
                indices.add(row + p + 1);

            }

        }

        setVAOValues(Binder.loadVAO(gl,vertices,textureCoordinates,normals,indices),indices.size());
    }
    
    public int getSize() {
        return scale_points;
    }
    
    public int getWidth() {
        return lane_width;
    }

    private float nMap(int i){
        float nr = (float) nr_segment_vertices_row - 1;
        float half = nr / 2;

        return ((float) i / half) - 1.0f;
    }
}
