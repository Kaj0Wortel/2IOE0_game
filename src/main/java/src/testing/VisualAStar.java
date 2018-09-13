
package src.testing;


// Own imports


// Java imports

import src.AI.Node;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;


/**
 * 
 * 
 */
public class VisualAStar
        extends JPanel {
    
    final private Rectangle SIZE = new Rectangle(-1, -3, 4, 4);//-1,-5,7,7
    final private int IMG_SIZE = 5000;
    final private int CIRCLE_SIZE = 50;
    final private Color BACK = Color.BLACK;
    
    private JFrame frame;
    
    private BufferedImage img;
    
    
    public VisualAStar() {
        super(null);
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
    }
    
    /**
     * @param point the point to add.
     * @param g2d the graphics to paint on.
     */
    private void addPoint(Point2D.Double point, Graphics2D g2d) {
        double x = (point.x - SIZE.x) * (IMG_SIZE / SIZE.width);
        double y = (point.y - SIZE.y) * (IMG_SIZE / SIZE.height);
        g2d.fillOval((int) (x - CIRCLE_SIZE/2.0), (int) (y - CIRCLE_SIZE/2.0),
                CIRCLE_SIZE, CIRCLE_SIZE);
    }
    
    /**
     * Adds a single point to the screen.
     * 
     * @param point the point to be added.
     */
    public void addPoint(Point2D.Double point) {
        Graphics2D g2d = (Graphics2D) img.getGraphics();
        g2d.setPaint(getForeground());
        addPoint(point, g2d);
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
            addPoint(point, g2d);
        }
    }
     
    /**
     * @param point1 and point2 the points of the line to be added.
     * @param g2d the graphics to paint on.
     */
    private void addLine(Point2D.Double point1, Point2D.Double point2, Graphics2D g2d) {
        double x1 = (point1.x - SIZE.x) * (IMG_SIZE / SIZE.width);
        double y1 = (point1.y - SIZE.y) * (IMG_SIZE / SIZE.height);
        double x2 = (point2.x - SIZE.x) * (IMG_SIZE / SIZE.width);
        double y2 = (point2.y - SIZE.y) * (IMG_SIZE / SIZE.height);
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
        double x1 = (Math.min(point1.x, point2.x) - SIZE.x) * (IMG_SIZE / SIZE.width);
        double y1 = (Math.min(point1.y, point2.y) - SIZE.y) * (IMG_SIZE / SIZE.height);
        double x2 = (Math.max(point1.x, point2.x) - SIZE.x) * (IMG_SIZE / SIZE.width);
        double y2 = (Math.max(point1.y, point2.y) - SIZE.y) * (IMG_SIZE / SIZE.height);
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
            //TODO change back if y coordinate should not be reverted
            //addPoint(node.pos, g2d);
            addPoint(new Point2D.Double(node.pos.x, -node.pos.y), g2d);
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
        
        try {
            for (int i = 0; i < 5; i++) {
                Thread.sleep(1000);
                visual.setForeground(new Color(255 / (i + 1), 255, 255 / (i + 1)));
                visual.addPoint(new Point2D.Double(i, i));
            }
            
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }
    
    
}
