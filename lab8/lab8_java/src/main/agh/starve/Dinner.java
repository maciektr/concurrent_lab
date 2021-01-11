package main.agh.starve;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Dinner {
    private final int nPhilosophers;
//    private ArrayList<Thread> philosophers;
    private final Lock lock = new ReentrantLock();
    private final ArrayList<Integer> forks;
    private final ArrayList<Condition> philosophersConditions;

    public Dinner(int nPhilosophers, ArrayList<Thread> philosophers){
        this.nPhilosophers = nPhilosophers;
//        this.philosophers = philosophers;
        forks = new ArrayList<>(nPhilosophers);
        philosophersConditions = new ArrayList<>(nPhilosophers);
        for(int i = 0; i < nPhilosophers; i++) {
            forks.add(2);
            philosophersConditions.add(lock.newCondition());
        }
    }

    void forksCountIncrement(int pos){
        forks.set(pos, forks.get(pos) + 1);
    }

    void forksCountDecrement(int pos){
        forks.set(pos, forks.get(pos) - 1);
    }

    public void take(int id){
        lock.lock();

        int firstPos = (id + nPhilosophers - 1) % nPhilosophers;
        int secondPos = (id + 1) % nPhilosophers;

        forksCountIncrement(firstPos);
        forksCountIncrement(secondPos);

        philosophersConditions.get(firstPos).signal();
        philosophersConditions.get(secondPos).signal();
        lock.unlock();
    }

    public void put(int id){
        lock.lock();

        while (forks.get(id) < 2) {
            try {
                philosophersConditions.get(id).await();
            } catch (InterruptedException e) {
//                lock.unlock();
//                Thread.currentThread().interrupt();
                return;
            }
        }

        int firstPos = (id + nPhilosophers - 1) % nPhilosophers;
        int secondPos = (id + 1) % nPhilosophers;

        forksCountDecrement(firstPos);
        forksCountDecrement(secondPos);

        lock.unlock();
    }
}
