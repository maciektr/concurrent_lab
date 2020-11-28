package main.agh.lab3;

import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    private static Buffer buffer;
    private static final ArrayList<Thread> threads = new ArrayList<>();
    private static final AccessLogger putLogger = new AccessLogger();
    private static final AccessLogger takeLogger = new AccessLogger();

    private static void spawn_threads(int n){
        int portion = 1;
        while(n-- > 0){
            threads.add(new Thread(new Producer(portion, buffer)));
            threads.add(new Thread(new Consumer(portion, buffer, false)));
            portion *= 2;
        }
    }

    public static void main(String []args){
        final int buffer_size = 16384;
        buffer = new Buffer(buffer_size, putLogger, takeLogger);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Completed put: "+ putLogger);
            System.out.println("Completed take: " + takeLogger);
        }));

        int prod_cons_count = 8;
        spawn_threads(prod_cons_count);
        for(Thread t : threads)
            t.start();
    }
}


abstract class Entity implements Runnable{
    int portion;
    Buffer buffer;

    public Entity(int portion, Buffer buffer){
        this.portion = portion;
        this.buffer = buffer;
    }
}


class Producer extends Entity{
    private long c = 0;

    public Producer(int portion, Buffer buffer){
        super(portion, buffer);
    }

    @Override
    public void run() {
        while(true){
            ArrayList<Object> product = new ArrayList<>();
            for(int i = 0; i<this.portion; i++){
                product.add("Box["+this.portion+"]{"+(c++)+"}");
            }
            try {
                super.buffer.put(product);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}


class Consumer extends Entity{
    private final boolean print;

    public Consumer(int portion, Buffer buffer, boolean print){
        super(portion, buffer);
        this.print = print;
    }

    @Override
    public void run() {
        while(true){
            List<Object> res = null;
            try {
                res = super.buffer.take(portion);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(res == null)
                continue;
            if(print)
                for(Object s : res)
                    System.out.println(s);
        }
    }
}


class Buffer {
    final Lock lock = new ReentrantLock();
    final Condition notFull  = lock.newCondition();
    final Condition notEmpty = lock.newCondition();

    Object[] items;
    int putptr, takeptr, count;

    private final AccessLogger putLogger;
    private final AccessLogger takeLogger;

    public Buffer(final int size, AccessLogger putLogger, AccessLogger takeLogger){
        this.items = new Object[size];
        this.putLogger = putLogger;
        this.takeLogger = takeLogger;
    }

    private void __put_all(List<Object> input){
        for(Object obj : input) {
            items[putptr++] = obj;
            putptr %= items.length;
            ++count;
        }
    }

    public void put(List<Object> input) throws InterruptedException {
        lock.lock();
        try {
            while (count + input.size() > items.length)
                notFull.await();
            this.__put_all(input);
            notEmpty.signalAll();
        } finally {
            putLogger.log(input.size());
            lock.unlock();
        }
    }

    private List<Object> __take_all(int n){
        List<Object> res = new ArrayList<>();
        while(--n >= 0){
            res.add(items[takeptr++]);
            takeptr %= items.length;
            --count;
        }
        return res;
    }

    public List<Object> take(int n) throws InterruptedException {
        lock.lock();
        try {
            while (count - n < 0)
                notEmpty.await();
            List<Object> res = __take_all(n);
            notFull.signalAll();
            return res;
        } finally {
            takeLogger.log(n);
            lock.unlock();
        }
    }
}


class AccessLogger{
    private final HashMap<Integer, Integer> accessLog = new HashMap<>();

    public void log(int x){
        accessLog.put(x, accessLog.getOrDefault(x, 0) + 1);
    }

    @Override
    public String toString() {
        return accessLog.toString();
    }
}
