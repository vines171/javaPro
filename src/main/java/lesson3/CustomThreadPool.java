package lesson3;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CustomThreadPool {
    private final LinkedList<Runnable> taskQueue = new LinkedList<>();
    private final List<WorkerThread> workers = new ArrayList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();
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
        lock.lock();
        try {
            taskQueue.addLast(task);
            System.out.println(Thread.currentThread().getName() + ": " + task + " добавлена");
            condition.signal();
        } finally {
            lock.unlock();
        }
    }

    public void shutdown() {
        lock.lock();
        try {
            isShutdown = true;
            condition.signalAll();
        } finally {
            lock.unlock();
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
        lock.lock();
        try {
            return taskQueue.size();
        } finally {
            lock.unlock();
        }
    }

    private class WorkerThread extends Thread {
        private final AtomicBoolean isWorking;

        public WorkerThread(String name) {
            super(name);
            this.isWorking = new AtomicBoolean(false);
        }

        @Override
        public void run() {
            while (shouldContinue()) {
                Runnable task = getTask();
                if (task != null) executeSafely(task);
            }
        }

        private boolean shouldContinue() {
            return !isShutdown || !taskQueue.isEmpty();
        }

        private Runnable getTask() {
            lock.lock();
            try {
                while (taskQueue.isEmpty() && !isShutdown) {
                    condition.await();
                }
                return taskQueue.poll();

            } catch (InterruptedException e) {
                return null;
            } finally {
                lock.unlock();
            }
        }

        private void executeSafely(Runnable task) {
            isWorking.set(true);
            try {
                task.run();
            } finally {
                isWorking.set(false);
            }
        }
    }
}