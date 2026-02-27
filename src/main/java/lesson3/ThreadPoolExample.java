package lesson3;

import java.time.Duration;

public class ThreadPoolExample {
    public static void main(String[] args) throws InterruptedException {
        CustomThreadPool pool = new CustomThreadPool(3);

        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            pool.execute(() -> {
                System.out.println("Task " + taskId + " executed by " + Thread.currentThread().getName());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown();
        System.out.println("Pool shutdown initiated");

        boolean terminated = pool.awaitTermination(Duration.ofMillis(Long.MAX_VALUE));
        if (terminated) {
            System.out.println("All tasks completed successfully");
        } else {
            System.out.println("Timeout waiting for tasks completion");
        }

        try {
            pool.execute(() -> System.out.println("This should not execute"));
        } catch (IllegalStateException e) {
            System.out.println("Correctly rejected task after shutdown: " + e.getMessage());
        }
    }
}
