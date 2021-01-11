package main.agh.arbitrary;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;

public class Dinner {
    private final int nPhilosophers;
    Semaphore arbiter;
    private final ArrayList<Semaphore> forks;
    private long waitTime;

    public Dinner(int nPhilosophers, ArrayList<Thread> philosophers){
        this.nPhilosophers = nPhilosophers;
        forks = new ArrayList<>(nPhilosophers);
        for(int i = 0; i < nPhilosophers; i++)
            forks.add(new Semaphore(1));
        arbiter = new Semaphore(nPhilosophers - 1);
    }

    public void take(int id){
        forks.get(id).release();
        forks.get((id  + 1) % nPhilosophers).release();
        arbiter.release();
    }

    public void put(int id) throws InterruptedException {
        arbiter.acquire();
        forks.get(id).acquire();
        forks.get((id + 1) % nPhilosophers).acquire();
    }

    public synchronized void addToWait(long wait){
        this.waitTime += wait;
    }

    public synchronized long getAverageWait(){
        return this.waitTime / this.nPhilosophers / 1000000;
    }
}
