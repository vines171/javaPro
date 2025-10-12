package lesson3;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomThreadPool {
    private final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private final List<WorkerThread> workers = new ArrayList<>();
    private final Object lock = new Object();
    private volatile boolean isShutdown = false;
    private volatile boolean isTerminated = false;
    private CountDownLatch latch = new CountDownLatch(0);

    public CustomThreadPool(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        for (int i = 0; i < capacity; i++) {
            WorkerThread worker = new WorkerThread("PoolThread- " + i);
            workers.add(worker);
            worker.start();
        }
    }

    public void execute(Runnable task) {
        if (isShutdown) {
            throw new IllegalStateException("ThreadPool is shutdown");
        }
        synchronized (lock) {
            taskQueue.addLast(task);
            System.out.println(Thread.currentThread().getName() + ": " + task + " добавлена");
            lock.notify();
        }
        latch = new CountDownLatch((int) (latch.getCount() + 1));
    }

    public void shutdown() {
        isShutdown = true;
        for (WorkerThread worker : workers) {
            worker.interrupt();
        }
    }

    public boolean awaitTermination(Duration timeout) throws InterruptedException {
        long timeoutMillis = timeout.toMillis();
        long endTime = System.currentTimeMillis() + timeoutMillis;

        for (WorkerThread worker : workers) {
            long remainingTime = endTime - System.currentTimeMillis();
            if (remainingTime <= 0) {
                return false;
            }

            worker.join(remainingTime);
            if (worker.isAlive()) {
                return false;
            }
        }

        isTerminated = true;
        return true;
    }

    public boolean isShutdown() {
        return isShutdown;
    }

    public boolean isTerminated() {
        return isTerminated;
    }

    public int getActiveCount() {
        int count = 0;
        for (WorkerThread worker : workers) {
            if (worker.isWorking.get()) {
                count++;
            }
        }
        return count;
    }

    public int getQueueSize() {
        return taskQueue.size();
    }

    private class WorkerThread extends Thread {
        private final AtomicBoolean isWorking;

        public WorkerThread(String name) {
            super(name);
            this.isWorking = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            while (true) {
                Runnable task;
                try {
                    synchronized (lock) {
                        while (taskQueue.isEmpty() && !isShutdown) {
                            System.out.println(Thread.currentThread().getName() + " ожидает task");
                            lock.wait();
                        }
                        task = taskQueue.removeFirst();
                        latch.countDown();
                    }
                    try {
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (isShutdown && taskQueue.isEmpty()) {
                        break;
                    }
                    System.out.println(Thread.currentThread().getName() + " Run task " + task);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }
    }
}