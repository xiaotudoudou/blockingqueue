package org.example.blockingqueue;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author zhangyi
 */
public class MyBlockingQueue {
    public static void main(String[] args) {
        // 创建一个容量为10的阻塞队列
        MyBlockingQueue queue = new MyBlockingQueue(10);

        Producer producer1 = new Producer(queue);
        Consumer consumer1 = new Consumer(queue);

        producer1.start();
        consumer1.start();

    }
    /**
     *Queue<Integer>:存储整数元素
     *capacity:阻塞队列大小
     *lock:对象,用于同步访问队列
     */
    private final Queue<Integer> queue;
    private final int capacity;
    private final Object lock = new Object();
    public MyBlockingQueue(int capacity) {
        this.queue = new LinkedList<>();
        this.capacity = capacity;
    }

    public void put(Integer item) throws InterruptedException {
        synchronized (lock) {
            while (queue.size() == capacity) {
                System.out.println("Producer is blocked, queue is full");
                lock.wait();//队满，等待
            }
            queue.add(item);
            System.out.println("Produced: " + item);
            lock.notifyAll();//唤醒等待的进程
        }
    }

    public Integer take() throws InterruptedException {
        synchronized (lock){
            while (queue.isEmpty()) {
                System.out.println("Consumer is blocked, queue is empty");
                lock.wait();//队空，等待
            }
            Integer item = queue.poll();
            System.out.println("Consumed: " + item);
            lock.notifyAll();//唤醒等待的线程
            return item;
        }
    }
}
/**
 * 生产者进程
 * 在 run 方法中，生产者会无限循环地生成随机整数，并尝试将其添加到队列中。
 * 如果队列已满，生产者线程会等待。
 */
class Producer extends Thread {
    private final MyBlockingQueue queue;
    Producer(MyBlockingQueue queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try {
            while(!Thread.currentThread().isInterrupted()){
                int item = (int) (Math.random() * 100);
                queue.put(item);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

/**
 * 消费者进程
 * 在 run 方法中，消费者会无限循环地从队列中取出元素。
 * 如果队列为空，消费者线程会等待。
 */
class Consumer extends Thread {
    private final MyBlockingQueue queue;
    Consumer(MyBlockingQueue queue) {
        this.queue = queue;
    }
    @Override
    public void run() {
        try{
            while(!Thread.currentThread().isInterrupted()){
                Integer item = queue.take();
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}