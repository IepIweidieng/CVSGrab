/*
 *  javainpractice.net 1999-2002
 *
 *  This software is intended to be used for educational purposes only.
 *
 *  We make no representations or warranties about the
 *  suitability of the software.
 *
 *  Any feedback relating to this software can be sent to
 *  info@javainpractice.net
 *
 */
package net.sourceforge.cvsgrab;

import java.util.ArrayList;
import java.util.List;


/** 
 * Simple fized sized Thread Pool
 */
public class ThreadPool {
    private static ThreadPool _instance;
    private final boolean _debugging = false;
    private List _allThreads = new ArrayList();
    private List _pool = new ArrayList();
    private boolean _stopped = false;
    
    /**
     * Initialise the thread pool
     * @param maxThreads The max number of threads
     */
    public static void init(int maxThreads) {
        CVSGrab.getLog().info("Using up to " + maxThreads + " simultaneous connections to load files");
        _instance = new ThreadPool(maxThreads);
    }
    
    /**
     * @return the singleton instance of the thread pool
     */
    public static ThreadPool getInstance() {
        return _instance;
    }

    /** 
     * Create a thread pool with max number of threads
     */
    public ThreadPool(int max) {
        for (int i = 0; i < max; i++) {
            WorkerThread worker = new WorkerThread(i);

            _pool.add(worker);
            _allThreads.add(worker);
            worker.start();
        }
    }

    /** 
     * Interrupt all threads and clear the pool
     * This method should only be used to tidy up.
     * The ThreadPool should not be used after invoking this method
     */
    public void destroy() {
        _stopped = true;

        for (int i = 0; i < _allThreads.size(); i++) {
            WorkerThread wt = (WorkerThread) _allThreads.get(i);

            if (_debugging) {
                System.err.println(Thread.currentThread().getName() + ".destroy: Killing " + wt.getName());
            }

            wt.kill();
        }
    }

    /** 
     * Execute a Runnable task
     */
    public void doTask(Runnable task) {
        WorkerThread worker = null;

        synchronized (_pool) {
            while (_pool.isEmpty()) {
                if (_stopped) {
                    return;
                }

                try {
                    _pool.wait();
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());

                    return;
                }
            }

            worker = (WorkerThread) _pool.remove(0);
        }

        worker.runTask(task);
    }

    private class WorkerThread extends Thread {
        private boolean stop = false;
        private Runnable task;

        public WorkerThread(int i) {
            setName("Worker" + i);
        }

        /** 
         * Interupt the WorkerThread
         */
        public synchronized void kill() {
            stop = true;
            notify();
        }

        public void runTask(Runnable runnable) {
            task = runnable;

            synchronized (this) {
                notifyAll();
            }
        }

        public void run() {
            if (_debugging) {
                System.err.println(getName() + ".run: ->");
            }

            while (!stop) {
                if (_debugging) {
                    System.err.println(getName() + ".run: Loop, task = " + task);
                }

                if (task == null) {
                    if (_debugging) {
                        System.err.println(getName() + ".run: Waiting for task ...");
                    }

                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException ex) {
                    }

                    if (_debugging) {
                        System.err.println(getName() + ".run: Done waiting");
                    }
                } else {
                    if (_debugging) {
                        System.err.println(getName() + ".run: Task = " + task);
                    }

                    try {

                        task.run();

                        if (_debugging) {
                            System.err.println(getName() + ".run: Done " + task);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                    }

                    task = null;

                    if (_stopped) {
                        if (_debugging) {
                            System.err.println(getName() + ".run: <- (Stopped)");
                        }

                        return;
                    }

                    synchronized (_pool) {
                        _pool.add(this);
                        _pool.notifyAll();
                    }
                }
            }

            if (_debugging) {
                System.err.println(getName() + ".run: <-");
            }
        }
    }
}
