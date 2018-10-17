package src.racetrack;

import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.Assets.TextureImg;
import src.Shaders.RacetrackShader;
import src.tools.Binder;

import java.util.ArrayList;

public class BezierTrack
        extends Track {

    final private int nrSegmentVerticesCol = 150;
    final private int nrSegmentVerticesRow = 17; //Must be odd
    private int laneWidth = 7;
    private int scalePoints = 10;

    private Vector3f UP = new Vector3f(0, 1, 0);

    public BezierTrack(Vector3f position, float size,
                       float rotx, float roty, float rotz, TextureImg texture, TextureImg bumbmap) {
        super(position, size, rotx, roty, rotz,texture, bumbmap);
        
        super.setControlPoints(new Vector3f[]{
                /*new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 3f), new Vector3f(1f, 0f, 4f), new Vector3f(4f, 0f, 4f),
                new Vector3f(4f, 0f, 4f), new Vector3f(7f, 0f, 4f), new Vector3f(8f, 0f, 3f), new Vector3f(8f, 0f, 0f),
                new Vector3f(8f, 0f, 0f), new Vector3f(8f, 0f, -3f), new Vector3f(7f, 0f, -4f), new Vector3f(4f, 0f, -4f),
                new Vector3f(4f, 0f, -4f), new Vector3f(1f, 0f, -4f), new Vector3f(0f, -0f, -3f), new Vector3f(0f, 0f, 0f),*/

                // CORRECT SIZE?
                // downhill speeding
                new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0.5f), new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 2f),
                new Vector3f(0f, 0f, 2f), new Vector3f(0f, 0f, 6f), new Vector3f(0f, -4f, 16f), new Vector3f(0f, -4f, 18f),
                new Vector3f(0f, -4f, 18f), new Vector3f(0f, -4f, 20f), new Vector3f(0f, -3f, 22f), new Vector3f(0f, -2f, 24f),
                //first jump
                new Vector3f(0f, -2f, 24f), new Vector3f(0f, -1f, 26f), new Vector3f(0f, -1f, 26f), new Vector3f(0f, -2f, 28f),
                //zigzag
                new Vector3f(0f, -2f, 28f), new Vector3f(0f, -3f, 30f), new Vector3f(4f, -2.8f, 34f), new Vector3f(6f, -2.5f, 32f),
                new Vector3f(6f, -2.5f, 32f), new Vector3f(8f, -2.2f, 30f), new Vector3f(10f, -2.2f, 32f), new Vector3f(12f, -2.5f, 34f),
                new Vector3f(12f, -2.5f, 34f), new Vector3f(14f, -2.8f, 36f), new Vector3f(17f, -2.2f, 30f), new Vector3f(18f, -2f, 26f),
                // upward slope and overpass
                new Vector3f(18f, -2f, 26f), new Vector3f(19f, -1.8f, 22f), new Vector3f(14f, -1f, 21f), new Vector3f(10f, -0.8f, 20f),
                new Vector3f(10f, -0.8f, 20f), new Vector3f(6f, -0.6f, 19f), new Vector3f(0f, -0.6f, 17f), new Vector3f(-4f, -0.8f, 16f),
                // downward turn 1
                new Vector3f(-4f, -0.8f, 16f), new Vector3f(-8f, -1f, 15f), new Vector3f(-8f, -1.8f, 3f), new Vector3f(-6f, -1.9f, 2f),
                new Vector3f(-6f, -1.9f, 2f), new Vector3f(-4f, -2f, 1f), new Vector3f(4f, -2f, 0f), new Vector3f(6f, -1.9f, 0f),
                // downward turn 2
                new Vector3f(6f, -1.9f, 0f), new Vector3f(8f, -1.8f, 0f), new Vector3f(8f, -1f, -6f), new Vector3f(6f, -0.9f, -8f),
                new Vector3f(6f, -0.9f, -8f), new Vector3f(4f, -0.8f, -10f), new Vector3f(0f, 0f, -4f), new Vector3f(0f, 0f, -2f),
                new Vector3f(0f, 0f, -2f), new Vector3f(0f, 0f, -1.5f), new Vector3f(0f, 0f, -0.5f), new Vector3f(0f, 0f, 0f),
                
                // MAP SHOWCASE SIZE
                // downhill speeding
                /*new Vector3f(0f, 0f, 0f), new Vector3f(0f, 0f, 0.25f), new Vector3f(0f, 0f, 0.5f), new Vector3f(0f, 0f, 1f),
                new Vector3f(0f, 0f, 1f), new Vector3f(0f, 0f, 3f), new Vector3f(0f, -2f, 8f), new Vector3f(0f, -2f, 9f),
                new Vector3f(0f, -2f, 9f), new Vector3f(0f, -2f, 10f), new Vector3f(0f, -1.5f, 11f), new Vector3f(0f, -1f, 12f),
                //first jump
                new Vector3f(0f, -1f, 12f), new Vector3f(0f, -0.5f, 13f), new Vector3f(0f, -0.5f, 13f), new Vector3f(0f, -1f, 14f),
                //zigzag
                new Vector3f(0f, -1f, 14f), new Vector3f(0f, -1.5f, 15f), new Vector3f(2f, -1.4f, 17f), new Vector3f(3f, -1.25f, 16f),
                new Vector3f(3f, -1.25f, 16f), new Vector3f(4f, -1.1f, 15f), new Vector3f(5f, -1.1f, 16f), new Vector3f(6f, -1.25f, 17f),
                new Vector3f(6f, -1.25f, 17f), new Vector3f(7f, -1.4f, 18f), new Vector3f(8.5f, -1.1f, 15f), new Vector3f(9f, -1f, 13f),
                // upward slope and overpass
                new Vector3f(9f, -1f, 13f), new Vector3f(9.5f, -0.9f, 11f), new Vector3f(7f, -0.5f, 10.5f), new Vector3f(5f, -0.4f, 10f),
                new Vector3f(5f, -0.4f, 10f), new Vector3f(3f, -0.3f, 9.5f), new Vector3f(0f, -0.3f, 8.5f), new Vector3f(-2f, -0.4f, 8f),
                // downward turn 1
                new Vector3f(-2f, -0.4f, 8f), new Vector3f(-4f, -0.5f, 7.5f), new Vector3f(-4f, -0.9f, 1.5f), new Vector3f(-3f, -0.95f, 1f),
                new Vector3f(-3f, -0.95f, 1f), new Vector3f(-2f, -1f, 0.5f), new Vector3f(2f, -1f, 0f), new Vector3f(3f, -0.95f, 0f),
                // downward turn 2
                new Vector3f(3f, -0.95f, 0f), new Vector3f(4f, -0.9f, 0f), new Vector3f(4f, -0.5f, -3f), new Vector3f(3f, -0.45f, -4f),
                new Vector3f(3f, -0.45f, -4f), new Vector3f(2f, -0.4f, -5f), new Vector3f(0f, 0f, -2f), new Vector3f(0f, 0f, -1f),
                new Vector3f(0f, 0f, -1f), new Vector3f(0f, 0f, -0.75f), new Vector3f(0f, 0f, -0.25f), new Vector3f(0f, 0f, 0f),*/
        });

        super.setSegments(controlPoints.length / 4);
    }

    @Override
    public void setShaderAndRenderMatrices(RacetrackShader shader, Matrix4f projectionMatrix, Matrix4f viewMatrix) {
        this.shader = shader;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
    }

    @Override
    public Vector3f getPoint(int segment, float t) {
        Vector3f p0 = new Vector3f(controlPoints[4 * segment]).mul(scalePoints);
        Vector3f p1 = new Vector3f(controlPoints[4 * segment + 1]).mul(scalePoints);
        Vector3f p2 = new Vector3f(controlPoints[4 * segment + 2]).mul(scalePoints);
        Vector3f p3 = new Vector3f(controlPoints[4 * segment + 3]).mul(scalePoints);

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
        Vector3f p0 = new Vector3f(controlPoints[4 * segment]).mul(scalePoints);
        Vector3f p1 = new Vector3f(controlPoints[4 * segment + 1]).mul(scalePoints);
        Vector3f p2 = new Vector3f(controlPoints[4 * segment + 2]).mul(scalePoints);
        Vector3f p3 = new Vector3f(controlPoints[4 * segment + 3]).mul(scalePoints);

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

        for (int i = 0; i < nrOfSegments; i++) {
            for (int col = 0; col < nrSegmentVerticesCol; col++) {
                float t = (float) col / (float) nrSegmentVerticesCol;
                Vector3f point = getPoint(i, t);
                Vector3f tangent = getTangent(i, t);
                Vector3f normal = new Vector3f(tangent).cross(UP).normalize();
                Vector3f vertNorm = new Vector3f(normal).cross(tangent).normalize();

                for (int row = 0; row < nrSegmentVerticesRow; row++) {
                    Vector3f mulNormal = new Vector3f(normal)
                            .mul(laneWidth).mul(nMap(row))
                            .sub(new Vector3f(vertNorm).mul(nMapSquared(row)));
                    Vector3f addedNormal = new Vector3f();
                    point.add(mulNormal, addedNormal);

                    vertices.add(addedNormal.x);
                    vertices.add(addedNormal.y);
                    vertices.add(addedNormal.z);
                    normals.add(UP.x);
                    normals.add(UP.y);
                    normals.add(UP.z);
                    textureCoordinates.add(t);
                    textureCoordinates.add(((float) row) / ((float) nrSegmentVerticesRow - 1));
                }
            }
        }
        for (int i = 0; i < nrOfSegments; i++) {
            int pointer = i * nrSegmentVerticesRow * nrSegmentVerticesCol;

            for (int col = 0; col < nrSegmentVerticesCol - 1; col++) {
                for (int row = 0; row < nrSegmentVerticesRow - 1; row++) {
                    indices.add(col * (nrSegmentVerticesRow) + row + pointer);
                    indices.add((col + 1) * (nrSegmentVerticesRow) + row + pointer);
                    indices.add(col * (nrSegmentVerticesRow) + row + pointer + 1);

                    indices.add(col * (nrSegmentVerticesRow) + row + pointer + 1);
                    indices.add((col + 1) * (nrSegmentVerticesRow) + row + pointer);
                    indices.add((col + 1) * (nrSegmentVerticesRow) + row + pointer + 1);
                }
            }

            int curCol = nrSegmentVerticesCol - 1;

            int p;
            if (i + 1 < nrOfSegments) {
                p = (i + 1) * nrSegmentVerticesRow * nrSegmentVerticesCol;
            } else {
                p = 0;
            }
            for (int row = 0; row < nrSegmentVerticesRow - 1; row++) {

                indices.add(curCol * nrSegmentVerticesRow + row + pointer);
                indices.add(row + p);
                indices.add(curCol * nrSegmentVerticesRow + row + pointer + 1);

                indices.add(curCol * nrSegmentVerticesRow + row + pointer + 1);
                indices.add(row + p);
                indices.add(row + p + 1);

            }

        }

        setVAOValues(Binder.loadVAO(gl, vertices, textureCoordinates, normals,
                indices), indices.size());
    }
    
    @Override
    public int getSize() {
        return scalePoints;
    }
    
    @Override
    public int getWidth() {
        return laneWidth;
    }

    @Override
    public void setShadowMap(int shadowMap) {
        this.shadowMap = shadowMap;
    }

    private float nMap(int i) {
        float nr = (float) nrSegmentVerticesRow - 1;
        float half = nr / 2;

        return ((float) i / half) - 1.0f;
    }
    
    private float nMapSquared(int i) {
        float nMap = nMap(i);
        return nMap * nMap * 1.5f;
    }
    
    
}
