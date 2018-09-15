
package src.tools.log;

import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


// Own imports


// Java imports


/**
 * 
 */
public class ThreadLogger
        extends Logger {
    final private Deque<Runnable> requestQueue = new LinkedList<>();
    final private Condition addedRequest;
    final private Logger logger;
    
    
    /**
     * Creates a new thread logger for the given logger.
     * 
     * @param logger the logger to execute on a different thread.
     */
    public ThreadLogger(Logger logger) {
        lock = new ReentrantLock(true);
         addedRequest = lock.newCondition();
        this.logger = logger;
        createAndStartThread();
    }
    
    @Override
    protected void writeE(Exception e, Type type, Date timeStamp) {
        requestQueue.addLast(() -> {
            logger.writeE(e, type, timeStamp);
        });
        notifyThread();
    }
    
    @Override
    protected void writeO(Object obj, Type type, Date timeStamp) {
        requestQueue.addLast(() -> {
            logger.writeO(obj, type, timeStamp);
        });
        notifyThread();
    }
    
    @Override
    protected void close() {
        requestQueue.addLast(() -> {
            logger.close();
        });
        notifyThread();
    }
    
    @Override
    protected void flush() {
        requestQueue.addLast(() -> {
            logger.flush();
        });
        notifyThread();
    }
    
    private void notifyThread() {
        lock.lock();
        try {
            addedRequest.signal();
            
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Creates and starts the logger thread.
     */
    private void createAndStartThread() {
        new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        while (!requestQueue.isEmpty()) {
                            requestQueue.pollFirst().run();
                        }
                        
                        lock.lock();
                        try {
                            if (requestQueue.isEmpty()) {
                                addedRequest.await();
                            }
                            
                        } finally {
                            lock.unlock();
                        }
                        
                    } catch (InterruptedException e) {
                        System.err.println(e);
                    }
                }
            }
        }.start();
    }
    
    
}
