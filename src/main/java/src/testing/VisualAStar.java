package src.testing;

// Java imports

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import src.AI.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import org.joml.Vector2f;

public class VisualAStar
        extends JPanel {
    
    //final static private Rectangle SIZE = new Rectangle(0,-10,10,10);
    final static private Rectangle SIZE = new Rectangle(-200, -115, 460, 460);
    final private int IMG_SIZE = 5000;
    final private int CIRCLE_SIZE = 50;//20
    final private Color BACK = Color.BLACK;
    
    final private Rectangle size;
    
    private JFrame frame;
    
    private BufferedImage img;
    private Vector3f[] controlPoints;
    
    
    public VisualAStar() {
        this(SIZE);
    }
    
    public VisualAStar(Rectangle size) {
        super(null);
        this.size = size;
        setLayout(null);
        
        setForeground(Color.WHITE);
        
        img = new BufferedImage(IMG_SIZE, IMG_SIZE,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setPaint(BACK);
        g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
        g2d.dispose();
        
        frame = new JFrame("Visual A*");
        frame.setLayout(null);
        SwingUtilities.invokeLater(() -> {
            frame.add(this);
        });
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                Insets in = frame.getInsets();
                setSize(frame.getWidth() - in.left - in.right,
                        frame.getHeight() - in.top - in.bottom);
            }
        });
        frame.setSize(1000, 1000);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        int elevation = 0;
        
        controlPoints = new Vector3f[] {
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
            /*new Vector3f(0f,0f,0f), new Vector3f(0f,0f,3f), new Vector3f(1f,0f,4f), new Vector3f(4f,0f,4f),
            new Vector3f(4f,0f,4f), new Vector3f(7f,0f,4f), new Vector3f(8f,0f,8f), new Vector3f(8f,0f,4f),
            new Vector3f(8f,0f,4f), new Vector3f(8f,0f,-2f), new Vector3f(8f,0f,-6f), new Vector3f(6f,0f,-6f),
            new Vector3f(6f,0f,-6f), new Vector3f(-2f,0f,-6f), new Vector3f(0f,0f,-4f), new Vector3f(0f,0f,0f),*/
        };

        Vector3f UP = new Vector3f(0,1,0);
/*
        float delta = 0.01f;
        for(float j = 0.0f; j < 1.0; j += delta){
            Vector3f point = getPoint(j);
            Vector3f tangent = getTangent(j);
            Vector3f addedTang = new Vector3f();
            point.add(tangent, addedTang);
            addPoint(new Point2D.Double(point.x, point.z));
            addLine(new Point2D.Double(point.x,point.z), new Point2D.Double(addedTang.x,addedTang.z));
        }
*/      
        int nr_segment_vertices_col = 60;
        int nr_segment_vertices_row = 3; //Must be odd
        int nr_of_segments = 14;
        Point2D.Double fPoint1  = new Point2D.Double(); 
        Point2D.Double fPoint2 = new Point2D.Double();
        for(int i = 0; i < nr_of_segments; i++){
            for(int col = 0; col < nr_segment_vertices_col; col ++){
                float t = (float)col/(float) nr_segment_vertices_col;
                Vector3f point = getPoint(i,t);
                Vector3f tangent = getTangent(i,t);
                Vector3f addedTang = new Vector3f();
                point.add(tangent, addedTang);
                
                //addLine(new Point2D.Double(point.x,point.z), new Point2D.Double(addedTang.x,addedTang.z));
                for(int j = 0; j < nr_segment_vertices_row; j++) {
                    Vector3f normal = new Vector3f(tangent);
                    normal.cross(UP).normalize().mul(1f).mul((float)(j*7)-1f);
                    Vector3f addedNormal = new Vector3f();
                    point.add(normal, addedNormal);
                    
                    // for finish visualisation
                    if (j == 0 && i == 0 && col == 0)
                        fPoint1 = new Point2D.Double(addedNormal.x, addedNormal.z);
                    if (j == 2 && i == 0 && col == 0)
                        fPoint2 = new Point2D.Double(addedNormal.x, addedNormal.z);
                    if (j != 1) {
                        //addPoint(new Point2D.Double(addedNormal.x, addedNormal.z));
                        addLine(new Point2D.Double(addedNormal.x, addedNormal.z)
                                , new Point2D.Double(addedNormal.x + 2*(addedTang.x - point.x)
                                        , addedNormal.z + 2*(addedTang.z - point.z)));
                    }
                }
            }
        }
        setForeground(Color.GREEN);
        addLine(fPoint1, fPoint2);
        setForeground(Color.WHITE);
    }

    
    /**
     * 
     * @param t
     * @return 
     * 
     * 
     * For conventience, since the matrices are by default written
     * as a transpose, we choose another way of multiplicating them.
     * We need:
     * {@code U^T * M * G}
     * But we have U, M^T and G^T. Note that:{@code
     *     U^T * (M * G)
     *       = (M * G)^T * U
     *       = G^T * M^T * U
     * }
     * 
     */
    public Vector3f getPoint(int segment, float t) {
        /**
        t *= 4;
        t %= 4;
        int segment = (int) Math.floor(t);
        t -= Math.floor(t);
        * 
        Vector3f point0 = new Vector3f(controlPoints[4*segment]);
        Vector3f point1 = new Vector3f(controlPoints[4*segment + 1]);
        Vector3f point2 = new Vector3f(controlPoints[4*segment + 2]);
        Vector3f point3 = new Vector3f(controlPoints[4*segment + 3]);
        
        point0.mul((float) (- Math.pow(t,3) + 3*Math.pow(t,2) - 3*t + 1));
        point1.mul((float) (3*Math.pow(t,3) - 6*Math.pow(t,2) + 3*t));
        point2.mul((float) (-3*Math.pow(t,3) + 3*Math.pow(t,2)));
        point3.mul((float) (Math.pow(t,3)));
        return new Vector3f().add(point0).add(point1).add(point2).add(point3);
        /**/
        // The number of control segments
        //int numSeg = (controlPoints.length) / 4;
        // The range of one segment
       // float range = 1 / (float) numSeg;
        // The current segment
        //int segment = (int) (t / range) % numSeg;
        // Calculate the new t value for the segment
        //float newT = (t - range*segment) * numSeg;
        int sc = 10;
        Vector3f p0 = new Vector3f(controlPoints[4*segment]).mul(sc);
        Vector3f p1 = new Vector3f(controlPoints[4*segment + 1]).mul(sc);
        Vector3f p2 = new Vector3f(controlPoints[4*segment + 2]).mul(sc);
        Vector3f p3 = new Vector3f(controlPoints[4*segment + 3]).mul(sc);
        
        Vector4f u = new Vector4f(
                (float) Math.pow(t, 3), (float) Math.pow(t, 2), t, 1);
        Matrix4f gTranspose = new Matrix4f(
                p0.x, p0.y, p0.z, 1,
                p1.x, p1.y, p1.z, 1,
                p2.x, p2.y, p2.z, 1,
                p3.x, p3.y, p3.z, 1
        );
        /*
        Vector4f umg = u.mul(m.mul(g, new Matrix4f()).transpose()).normalize();
        return new Vector3f(umg.x, umg.y, umg.z);
        /**/
        
        Vector4f gTmTu = u.mul(gTranspose.mul(mTranspose));
        return new Vector3f(gTmTu.x, gTmTu.y, gTmTu.z);
        /**/
    }
    
    final private static Matrix4f mTranspose = new Matrix4f(
            -1,  3, -3, 1,
             3, -6,  3, 0,
            -3,  3,  0, 0,
             1,  0,  0, 0
    );
    
    /**
     * 
     * @param t
     * @return 
     * 
     * Note that:
     * {@code U^T * (M * G) = G^T * M^T * U}
     * @see #getPoint(float)
     */
    public Vector3f getTangent(int segment, float t) {
        // The number of control segments
        //int numSeg = (controlPoints.length) / 4;
        // The range of one segment
        //float range = 1 / (float) numSeg;
        // The current segment
        //int segment = (int) (t / range) % numSeg;
        // Calculate the new t value for the segment
        //float newT = (t - range*segment) * numSeg;
        
        /*
        t *= 4;
        t %= 4;
        int segment = (int) Math.floor(t);
        t -= Math.floor(t);
        */
        int sc = 10;
        Vector3f p0 = new Vector3f(controlPoints[4*segment]).mul(sc);
        Vector3f p1 = new Vector3f(controlPoints[4*segment + 1]).mul(sc);
        Vector3f p2 = new Vector3f(controlPoints[4*segment + 2]).mul(sc);
        Vector3f p3 = new Vector3f(controlPoints[4*segment + 3]).mul(sc);
        
        Vector4f u = new Vector4f((float) (3*Math.pow(t, 2)), 2*t, 1, 0);
        Matrix4f gTranspose = new Matrix4f(
                p0.x, p0.y, p0.z, 1,
                p1.x, p1.y, p1.z, 1,
                p2.x, p2.y, p2.z, 1,
                p3.x, p3.y, p3.z, 1
        );
        
        Vector4f gTmTu = u.mul(gTranspose.mul(mTranspose));
        return new Vector3f(gTmTu.x, gTmTu.y, gTmTu.z).normalize();
        
        /*
        p0.mul((float) (-3*Math.pow(t,2) + 6*t - 3));
        p1.mul((float) (9*Math.pow(t,2) - 12*Math.pow(t,1) + 3));
        p2.mul((float) (-9*Math.pow(t,2) * 6*t));
        p3.mul((float) (3*Math.pow(t,2)));

        return new Vector3f().add(p0).add(p1).add(p2).add(p3).normalize();
        */
    }
    
    /**
     * @param vec 
     * 
     * @see #addPoint(Point2D.Double)
     */
    public void addPoint(Vector2f vec) {
        addPoint(new Point2D.Double(vec.x, vec.y));
    }
    
    /**
     * @param point the point to add.
     * @param g2d the graphics to paint on.
     */
    private void addPoint(Point2D.Double point, Graphics2D g2d, int size) {
        double x = (point.x - this.size.x) * (IMG_SIZE / this.size.width);
        double y = (point.y - this.size.y) * (IMG_SIZE / this.size.height);
        g2d.fillOval((int) (x - size*CIRCLE_SIZE/2.0), (int) (y - size*CIRCLE_SIZE/2.0),
                size*CIRCLE_SIZE, size*CIRCLE_SIZE);
    }
    
    /**
     * Adds a single point to the screen.
     * 
     * @param point the point to be added.
     */
    public void addPoint(Point2D.Double point) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        addPoint(point, g2d, 1);
    }
    
    public void addPointBig (Point2D.Double point) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        addPoint(point, g2d, 2);
    }
    
    /**
     * Adds all points in the list to the screen.
     * 
     * @param points points to be added.
     */
    public void addPoints(List<Point2D.Double> points) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        for (Point2D.Double point : points) {
            addPoint(point, g2d, 1);
        }
    }
     
    /**
     * @param point1 and point2 the points of the line to be added.
     * @param g2d the graphics to paint on.
     */
    private void addLine(Point2D.Double point1, Point2D.Double point2, Graphics2D g2d) {
        double x1 = (point1.x - size.x) * (IMG_SIZE / size.width);
        double y1 = (point1.y - size.y) * (IMG_SIZE / size.height);
        double x2 = (point2.x - size.x) * (IMG_SIZE / size.width);
        double y2 = (point2.y - size.y) * (IMG_SIZE / size.height);
        g2d.drawLine((int) x1,(int) y1,(int) x2,(int) y2);
    }
    
    /**
     * Adds a single line to the screen.
     * 
     * @param point1 and point2 the points of the line to be added.
     */
    public void addLine(Point2D.Double point1, Point2D.Double point2) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        g2d.setStroke(new BasicStroke(10));
        addLine(point1, point2, g2d);
    }
        
    /**
     * @param point1 and point2 the points of the line to be added.
     * @param g2d the graphics to paint on.
     */
    private void addRec(Point2D.Double point1, Point2D.Double point2, Graphics2D g2d) {
        double x1 = (Math.min(point1.x, point2.x) - size.x) * (IMG_SIZE / size.width);
        double y1 = (Math.min(point1.y, point2.y) - size.y) * (IMG_SIZE / size.height);
        double x2 = (Math.max(point1.x, point2.x) - size.x) * (IMG_SIZE / size.width);
        double y2 = (Math.max(point1.y, point2.y) - size.y) * (IMG_SIZE / size.height);
        g2d.fillRect((int) x1,(int) y1,(int) (x2 -x1),(int) (y2 - y1));
    }
    
    /**
     * Adds a single line to the screen.
     * 
     * @param point1 and point2 the points of the line to be added.
     */
    public void addRec(Point2D.Double point1, Point2D.Double point2) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        g2d.setStroke(new BasicStroke(10));
        addRec(point1, point2, g2d);
    }
    
    /**
     * Adds all nodes in the list to the screen.
     * 
     * @param nodes nodes to be added.
     */
    public void addNodes(List<Node> nodes) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        for (Node node : nodes) {
            System.out.println(node.a);
            addPoint(new Point2D.Double(node.pos.x, -node.pos.y), g2d, 1);
        }
    }
    
    /**
     * Clears the entire screen.
     */
    public void clear() {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(BACK);
        g2d.fillRect(0, 0, IMG_SIZE, IMG_SIZE);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.scale(((double) getWidth()) / img.getWidth(),
                ((double) getHeight()) / img.getHeight());
        if (img != null) g2d.drawImage(img, 0, 0, null);

    }
    
    public static void main(String[] args) {
       VisualAStar visual = new VisualAStar();
        
       /*
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                visual.setForeground(new Color(255 / (i + 1), 255, 255 / (i + 1)));
                visual.addPoint(new Point2D.Double(i, i));
            }
            
        } catch (InterruptedException e) {
            System.err.println(e);
        }
        */
    }
}