
package src.AI;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import src.tools.log.Logger;


// Own imports


// Java imports


/**
 * 
 * 
 * @author Kaj Wortel (0991586)
 */
public class DequeRequestReader<V>
        implements Closeable {
    
    final private File file;
    private int queueSize;
    protected BufferedReader reader;
    final private Thread readThread;
    
    
    final private Deque<V> buffer = new LinkedList<>();
    final private Lock lock = new ReentrantLock();
    final private Condition bufferFull = lock.newCondition();
    final private Condition bufferEmpty = lock.newCondition();
    private boolean producerIsWaiting = false;
    private boolean consumerIsWaiting = false;
    private boolean closed = false;
    
    
    @SuppressWarnings("CallToThreadStartDuringObjectConstruction")
    public DequeRequestReader(File file, int queueSize, Processor<V> processor)
            throws IOException {
        if (!file.exists()) throw new FileNotFoundException (
                "The requested file \"" + file + "\" does not exist or is protected.");
        this.file = file;
        this.queueSize = queueSize;
        this.reader = new BufferedReader(new FileReader(file));
        
        readThread = new Thread("read-thread-file-" + file.getName()) {
            @Override
            @SuppressWarnings("UseSpecificCatch")
            public void run() {
                while (!closed) {
                    try {
                        // Check if the buffer is full. If so,
                        // wait for an item to be pulled.
                        lock.lock();
                        try {
                            if (buffer.size() >= DequeRequestReader.this.queueSize) {
                                producerIsWaiting = true;
                                bufferFull.await();
                                producerIsWaiting = false;
                                if (closed) break;
                            }
                            
                        } finally {
                            lock.unlock();
                        }
                        
                        // Read data from file outside the lock to prevent
                        // unnecessary waiting
                        String data = reader.readLine();
                        if (data == null) {
                            reader.close();
                            createNewReader();
                            data = reader.readLine();
                            if (data == null) {
                                Logger.write("Given file is empty",
                                        Logger.Type.ERROR);
                                break;
                            }
                        }
                        V value = processor.process(data);
                        
                        // Put the data in the buffer.
                        lock.lock();
                        try {
                            buffer.push(value);
                            
                            if (consumerIsWaiting) {
                                bufferEmpty.signal();
                            }
                            
                        } finally {
                            lock.unlock();
                        }
                        
                    } catch (Exception e) {
                        Logger.write(new Object[] {
                            "Exception occured in read thread:",
                            e
                        }, Logger.Type.ERROR);
                    }
                }
                
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Logger.write(e);
                }
            }
        };
        readThread.start();
    }
    
    protected void createNewReader()
            throws IOException {
        reader = new BufferedReader(new FileReader(file));
    }
    
    public V getNextData() throws IOException { // tmp
        if (closed) throw new IllegalStateException("Reader is closed!");
        
        lock.lock();
        try {
            if (buffer.isEmpty()) {
                consumerIsWaiting = true;
                try {
                    Logger.write("Buffer could not keep up with reading file "
                            + file.getName(),
                            Logger.Type.WARNING);
                    //fwS.write("---------------------- BUFFER WAS EMPTY" + System.getProperty("line.separator"));
                    bufferEmpty.await();
                    
                } catch (InterruptedException e) {
                    Logger.write(e);
                    
                } finally {
                    consumerIsWaiting = false;
                }
            }
            
            if (producerIsWaiting) {
                bufferFull.signal();
            }
            
            return buffer.poll();
            
        } finally {
            lock.unlock();
        }
    }
    
    @Override
    public void close() {
        closed = true;
        
        lock.lock();
        try {
            if (producerIsWaiting) {
                bufferFull.signal();
            }
            
        } finally {
            lock.unlock();
        }
    }
    
    /*
    final private static String testFile = "C:\\Users\\s155587\\Documents\\_university"
            + "\\3_courses\\1_a1_2IOE0_DBL_Inter_intel\\tmp\\test.txt";
    final private static String testOutFile = "C:\\Users\\s155587\\Documents\\_university"
            + "\\3_courses\\1_a1_2IOE0_DBL_Inter_intel\\tmp\\testOut.txt";
    private static FileWriter fwS = null;
    static {
        try {
            fwS = new FileWriter(testOutFile, false);
            
        } catch (IOException e) {
            System.err.println(e);
            System.exit(-1);
        }
    }
    public static void main(String[] args) {
        try (FileWriter fw = new FileWriter(testFile, false)) {
            for (int i = 1; i <= 10_000; i++) {
                fw.write("line " + i + System.getProperty("line.separator"));
            }
            
        } catch (IOException e) {
            System.err.println(e);
        }
        try (DequeRequestReader drr = new DequeRequestReader(new File(testFile), 1000)) {
            Thread.sleep(100);
            for (int j = 0; j < 10; j++) {
                for (int i = 0; i < 1000; i++) {
                    fwS.write(drr.readLine() + System.getProperty("line.separator"));
                }
                Thread.sleep(1);
            }
            
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            
        }
        
    }/**/
    
}
