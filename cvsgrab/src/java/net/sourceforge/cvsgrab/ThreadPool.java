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
    private final boolean debugging = false;
    private List allThreads = new ArrayList();
    private List pool = new ArrayList();
    private boolean stopped = false;

    /** 
     * Create a thread pool with max number of threads
     */
    public ThreadPool(int max) {
        for (int i = 0; i < max; i++) {
            WorkerThread worker = new WorkerThread(i);

            pool.add(worker);
            allThreads.add(worker);
            worker.start();
        }
    }

    /** 
     * Interrupt all threads and clear the pool
     * This method should only be used to tidy up.
     * The ThreadPool should not be used after invoking this method
     */
    public void destroy() {
        stopped = true;

        for (int i = 0; i < allThreads.size(); i++) {
            WorkerThread wt = (WorkerThread) allThreads.get(i);

            if (debugging) {
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

        synchronized (pool) {
            while (pool.isEmpty()) {
                if (stopped) {
                    return;
                }

                try {
                    pool.wait();
                } catch (InterruptedException ex) {
                    System.err.println(ex.getMessage());

                    return;
                }
            }

            worker = (WorkerThread) pool.remove(0);
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
            if (debugging) {
                System.err.println(getName() + ".run: ->");
            }

            while (!stop) {
                if (debugging) {
                    System.err.println(getName() + ".run: Loop, task = " + task);
                }

                if (task == null) {
                    if (debugging) {
                        System.err.println(getName() + ".run: Waiting for task ...");
                    }

                    try {
                        synchronized (this) {
                            wait();
                        }
                    } catch (InterruptedException ex) {
                    }

                    if (debugging) {
                        System.err.println(getName() + ".run: Done waiting");
                    }
                } else {
                    if (debugging) {
                        System.err.println(getName() + ".run: Task = " + task);
                    }

                    try {
                        task.run();

                        if (debugging) {
                            System.err.println(getName() + ".run: Done " + task);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace(System.err);
                    }

                    task = null;

                    if (stopped) {
                        if (debugging) {
                            System.err.println(getName() + ".run: <- (Stopped)");
                        }

                        return;
                    }

                    synchronized (pool) {
                        pool.add(this);
                        pool.notifyAll();
                    }
                }
            }

            if (debugging) {
                System.err.println(getName() + ".run: <-");
            }
        }
    }
}
