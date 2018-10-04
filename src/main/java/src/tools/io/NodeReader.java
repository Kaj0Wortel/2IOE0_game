
package src.tools.io;


// Java imports

import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import src.AI.Node;
import src.GS;
import src.tools.log.Logger;


/**
 * Reads {@link Node}s from a file.
 */
public class NodeReader
        implements Closeable {
    final private BufferedReader reader;

    /**
     * @param fileName the name of the file to read the node data from.
     * @throws IOException if the file could not be accessed.
     */
    public NodeReader(String fileName)
            throws IOException {
        this(new File(fileName));
    }

    /**
     * @param file the file to read the node data from.
     * @throws IOException if the file could not be accessed.
     */
    public NodeReader(File file)
            throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }


    /**
     * @return A list containing all node data described by the given file.
     * @throws IOException if the file could not be accessed.
     *
     *                     Note that the nodes are listed in reverse order!
     */
    public List<double[]> readAllNodeData()
            throws IOException {
        List<double[]> list = new ArrayList<>();
        double[] nodeData;
        while ((nodeData = readNodeData()) != null) {
            list.add(nodeData);
        }

        return list;
    }

    /**
     * Reads the data of a single node.
     *
     * @return the {@code Node} that was specified on the current line of
     * the input file. {@code null} if EOF was reached.
     * @throws IOException if the file could not be accessed.
     *
     *                     Note that the nodes are listed in reverse order!
     */
    public double[] readNodeData()
            throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        String[] data = line.split(GS.DELIM + "");          // Dirty convert from char to string
        return new double[]{
                0,
                Double.parseDouble(data[0]), // x
                Double.parseDouble(data[1]), // y
                Double.parseDouble(data[2]), // v
                Double.parseDouble(data[3]), // a
                Double.parseDouble(data[4]), // rot
                Double.parseDouble(data[5]), // rotV
//            Double.parseDouble(data[6]), // g
//            Double.parseDouble(data[7]), // h
                Integer.parseInt(data[6]) // next checkpoint
        };
    }

    /**
     * @return A list containing all nodes described by the given file.
     * @throws IOException if the file could not be accessed.
     *
     *                     Note that the nodes are listed in reverse order!
     */
    public List<Node> readNodes()
            throws IOException {
        List<Node> list = new ArrayList<>();
        Node node;
        while ((node = readNode()) != null) {
            list.add(node);
        }

        return list;
    }

    /**
     * Reads a single node.
     *
     * @return the {@code Node} that was specified on the current line of
     * the input file. {@code null} if EOF was reached.
     * @throws IOException if the file could not be accessed.
     *
     *                     Note that the nodes are returned in reverse order!
     */
    public Node readNode()
            throws IOException {
        String line = reader.readLine();
        if (line == null) return null;
        String[] data = line.split(GS.DELIM + "");          // Dirty convert from char to string
        try {
            return new Node(new Point2D.Double(
                    Double.parseDouble(data[0]),
                    Double.parseDouble(data[1])),
                    Double.parseDouble(data[2]),
                    Double.parseDouble(data[3]),
                    Double.parseDouble(data[4]),
                    Double.parseDouble(data[5]),
                    Double.parseDouble(data[6]),
                    Double.parseDouble(data[7]),
                    Integer.parseInt(data[8]),
                    null
            );
        } catch (NumberFormatException e) {
            Logger.write(e);
            return null;
        }
    }

    @Override
    public void close()
            throws IOException {
        reader.close();
    }

    public static void main(String[] args) {
        // GS.RESOURCE_DIR is doing strange in single file-execution for me.
        // Please check filename before running!
        String fileName = System.getProperty("user.dir") + GS.FS
                + "src" + GS.FS + "main" + GS.FS + "java" + GS.FS
                + "src" + GS.FS + "res" + GS.FS
                + "A_star_data" + GS.FS + "astar.txt";

        // Read node list.
        List<Node> nodes;
        try (NodeReader nr = new NodeReader(fileName)) {
            nodes = nr.readNodes();

        } catch (IOException e) {
            System.err.println(e);
            return;
        }

        // Print node list.
        for (Node node : nodes) {
            System.out.println(node);
        }
    }

}
