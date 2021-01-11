package main.agh.starve;

import java.util.*;

public class Main {
    private static final int wait = 10;
    private static final int nThreads = 10;

    private static void spawn_threads(ArrayList<Thread> executor, Dinner dinner){
        for(int n = 0; n < nThreads; n++)
            executor.add(new Thread(new Philosopher(n, dinner)));
        for(Thread t : executor)
            t.start();
    }

    private static void kill_threads(ArrayList<Thread> executor) {
        for(Thread t : executor) {
            t.interrupt();
            t.stop();
        }
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored){
        }
    }

    public static void main(String[] args) {
        ArrayList<Thread> executor = new ArrayList<>();
        Dinner dinner = new Dinner(nThreads, executor);
        spawn_threads(executor, dinner);
//        kill_threads(executor);
    }
}
