
package src.tools.io;


// Java imports
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import src.AI.Node;
import src.AI.NodeGlueFinal;
import src.GS;


/**
 * Writes {@link Node}s to a file.
 */
public class NodeWriter
        implements Closeable {
    private PrintWriter writer;
    
    
    /**
     * @param fileName the name of the file to write the node data to.
     * @throws IOException if the file could not be accessed.
     */
    public NodeWriter(String fileName)
            throws IOException {
        this(new File(fileName));
    }
    
    /**
     * @param file the file to write the node data to.
     * @throws IOException if the file could not be accessed.
     */
    public NodeWriter(File file)
            throws IOException {
        writer = new PrintWriter(
                new BufferedWriter(new FileWriter(file, false)));
    }
    
    /**
     * Writes a node chain to the given file.
     * 
     * @param node the first child node to start the chain from (inclusive)
     * @throws IOException if the file could not be accessed.
     */
    public void writeNodeChain(Node node)
            throws IOException {
        while (node != null) {
            writeNode(node);
            node = node.parentNode;
        }
    }
    
    /**
     * @param node the node to write to the given file.
     * @throws IOException if the file could not be accessed.
     */
    public void writeNode(Node node)
            throws IOException {
        String delim = ";";
        String writeData = node.pos.x + delim
                + node.pos.y + delim
                + node.v + delim
                + node.a + delim
                + node.rot + delim
                + node.rotV + delim
                + node.g + delim
                + node.h + delim
                + node.nextCP + GS.LS
                + "1";
        writer.write(writeData);
    }
    
    /**
     * Writes a node chain to the given file.
     * 
     * @param node the first child node to start the chain from (inclusive)
     * @throws IOException if the file could not be accessed.
     */
    public void writeNodeChain(NodeGlueFinal node)
            throws IOException {
        while (node != null) {
            writeNode(node);
            node = node.parentNode;
        }
    }
    
    /**
     * @param node the node to write to the given file.
     * @throws IOException if the file could not be accessed.
     */
    public void writeNode(NodeGlueFinal node)
            throws IOException {
        String delim = ";";
        String writeData = node.pos.x + delim
                + node.pos.y + delim
                + node.v + delim
                + node.a + delim
                + node.rot + delim
                + node.rotV + delim
                + node.g + delim
                + node.h + delim
                + node.nextCP + delim
                + node.turn + delim
                + node.accel + delim
                + "1" + GS.LS;
        writer.write(writeData);
    }

    @Override
    public void close()
            throws IOException {
        writer.close();
    }
    
    
    public static void main(String[] args) {
        // GS.RESOURCE_DIR is doing strange in single file-execution for me.
        // Please check filename before running!
        String fileName = System.getProperty("user.dir") + GS.FS
                + "src" + GS.FS + "main" + GS.FS + "java" + GS.FS
                + "src" + GS.FS + "res" + GS.FS
                + "A_star_data" + GS.FS + "test.vec";
        System.out.println(fileName);
        // Fill test node list.
        List<Node> nodes = new ArrayList<>();
        for (double i = 0; i < 10; i++) {
            nodes.add(new Node(new Point2D.Double(i, i), i, i, i, i, i, i,
                    (int) i, (nodes.isEmpty() 
                            ? null
                            : nodes.get(nodes.size() - 1))));
        }
        
        // Print node list.
        for (Node node : nodes) {
            System.out.println(node);
        }
        
        // Write test node list.
        try (NodeWriter nw = new NodeWriter(fileName)) {
            nw.writeNodeChain(nodes.get(nodes.size() - 1));
            
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    
}
