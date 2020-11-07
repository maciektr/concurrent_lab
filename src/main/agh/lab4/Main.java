package main.agh.lab4;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Main {
    private static final int wait = 10;

    private static void spawn_threads(ExecutorService executor, Buffer buffer, int n, boolean uniform){
        Random seedrand = new Random();
        while(n-- > 0){
            executor.submit(new Thread(new Producer(buffer, new RandomGenerator(seedrand.nextInt(), buffer.size, uniform))));
            executor.submit(new Thread(new Consumer(buffer, new RandomGenerator(seedrand.nextInt(), buffer.size, uniform), false)));
        }
    }

    private static void print_csv_header(){
        System.out.println("buffer_size,prod_or_cons,portion_size,prod_cons_conf,fair,uniform,count");
    }

    private static void print_log(HashMap<Integer, Integer> map, String type, int buffer_size, int prod_cons_count, boolean fair, boolean uniform){
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            System.out.println(buffer_size+","+type+","+key+","
                    +prod_cons_count+"+"+prod_cons_count+","+fair+","+uniform+","+value);
        }
    }

    private static void test(int buffer_size, int prod_cons_count, boolean fair, boolean uniform) throws InterruptedException {
        AccessLogger putLogger = new AccessLogger();
        AccessLogger takeLogger = new AccessLogger();
        Buffer buffer = new Buffer(buffer_size, putLogger, takeLogger, fair);
        ExecutorService executor = Executors.newFixedThreadPool(2*prod_cons_count);

        spawn_threads(executor, buffer, prod_cons_count, uniform);
        Thread.sleep(1000 * wait);
        executor.shutdownNow();
        Thread.sleep(100);

        print_log(putLogger.get_log(), "prod", buffer_size, prod_cons_count, fair, uniform);
        print_log(takeLogger.get_log(), "cons", buffer_size, prod_cons_count, fair, uniform);
    }

    public static void main(String []args) throws InterruptedException {
        OptionsParser options = new OptionsParser(args);
        if(options.print_header)
            print_csv_header();
        test(options.buffer_size, options.prod_cons_count, options.fair, options.uniform);
    }
}


abstract class Entity implements Runnable{
    private final RandomGenerator rand;
    Buffer buffer;

    int portion(){
        return rand.next();
    }

    public Entity(Buffer buffer, RandomGenerator rand){
        this.buffer = buffer;
        this.rand = rand;
    }
}


class Producer extends Entity{
    private long c = 0;

    public Producer(Buffer buffer, RandomGenerator rand){
        super(buffer, rand);
    }

    @Override
    public void run() {
        ArrayList<Object> product = new ArrayList<>();
        while(true){
            product.clear();
            int p = this.portion();
            for(int i = 0; i<p; i++){
                product.add("Box["+p+"]{"+(c++)+"}");
            }
            try {
                super.buffer.put(product);
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}


class Consumer extends Entity{
    private final boolean print;

    public Consumer(Buffer buffer, RandomGenerator rand, boolean print){
        super(buffer, rand);
        this.print = print;
    }

    @Override
    public void run() {
        List<Object> res = new ArrayList<Object>();
        while(true){
            try {
                super.buffer.take(this.portion(), res);
            } catch (InterruptedException e) {
                return;
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
    final Lock prodLock = new ReentrantLock(true);
    final Lock consLock = new ReentrantLock(true);
    final Condition notFull  = lock.newCondition();
    final Condition notEmpty = lock.newCondition();

    Object[] items;
    int putptr, takeptr, count;
    public final int size;
    private final boolean fair;

    private final AccessLogger putLogger;
    private final AccessLogger takeLogger;

    public Buffer(final int size, AccessLogger putLogger, AccessLogger takeLogger, boolean fair){
        this.size = size;
        this.items = new Object[size];
        this.putLogger = putLogger;
        this.takeLogger = takeLogger;
        this.fair = fair;
    }

    private void __put_all(List<Object> input){
        for(Object obj : input) {
            items[putptr++] = obj;
            putptr %= items.length;
            ++count;
        }
    }

    public void put(List<Object> input) throws InterruptedException {
        if(fair)
            prodLock.lock();
        lock.lock();
        try {
            while (count + input.size() > items.length)
                notFull.await();
            this.__put_all(input);
            putLogger.log(input.size());
            notEmpty.signalAll();
        } finally {
            if(fair)
                prodLock.unlock();
            lock.unlock();
        }
    }

    private void __take_all(int n, List<Object> res){
        while(--n >= 0){
            res.add(items[takeptr++]);
            takeptr %= items.length;
            --count;
        }
    }

    public void take(int n, List<Object> res) throws InterruptedException {
        res.clear();
        if(fair)
            consLock.lock();
        lock.lock();
        try {
            while (count - n < 0)
                notEmpty.await();
            __take_all(n, res);
            takeLogger.log(n);
            notFull.signalAll();
        } finally {
            if(fair)
                consLock.unlock();
            lock.unlock();
        }
    }
}


class AccessLogger{
    private final HashMap<Integer, Integer> accessLog = new HashMap<>();

    public HashMap<Integer, Integer> get_log(){
        return new HashMap<>(accessLog);
    }

    public void log(int x){
        accessLog.put(x, accessLog.getOrDefault(x, 0) + 1);
    }

    @Override
    public String toString() {
        return accessLog.toString();
    }
}


class RandomGenerator{
    private final Random rand;
    private final boolean uniform;
    private final int buffer_size;

    public RandomGenerator(int seed, int buffer_size, boolean uniform){
        this.uniform = uniform;
        this.rand = new Random(seed);
        this.buffer_size = buffer_size;
    }

    private int randpos(){
        return Math.abs(rand.nextInt())%buffer_size;
    }

    public int next(){
        int pos = randpos();
        if(!uniform)
            return pos > buffer_size / 2 ? randpos() : pos;
        return pos;
    }
}


class OptionsParser{
    public final int prod_cons_count;
    public final int buffer_size;
    public final boolean fair;
    public final boolean uniform;
    public final boolean print_header;

    public OptionsParser(String []args){
        int pcc = -1, bs = -1;
        boolean f = false, u=false, ph=false;
        for(int i = 0; i<args.length-1; i+=2){
            String arg = args[i + 1];
            switch (args[i]) {
                case "--prod_cons_count":
                    pcc = parseInt(arg);
                    break;
                case "--buffer_size":
                    bs = parseInt(arg);
                    break;
                case "--fair":
                    f = parseBoolean(arg);
                    break;
                case "--uniform":
                    u = parseBoolean(arg);
                    break;
                case "--print_header":
                    ph = parseBoolean(arg);
                    break;
            }
        }
        prod_cons_count = pcc;
        buffer_size = bs;
        fair = f;
        uniform = u;
        print_header = ph;
    }

    private boolean parseBoolean(String x){
        return x.toLowerCase().equals("true");
    }

    private int parseInt(String x){
        return Integer.parseInt(x);
    }
}
