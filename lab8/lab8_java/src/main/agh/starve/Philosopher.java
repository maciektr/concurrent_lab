package main.agh.starve;

public class Philosopher implements Runnable{
    private int id;
    private Dinner dinner;
    private boolean interrupted = false;

    public Philosopher(int id, Dinner dinner){
        this.id = id;
        this.dinner = dinner;
    }

    private void think() {
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            interrupted = true;
        }
    }

    private void eat(){
        try {
            Thread.sleep(2);
        } catch (InterruptedException e) {
            interrupted = true;
        }
    }

    @Override
    public void run() {
        while (true) {
            think();
            long startTime = System.nanoTime();
            dinner.put(id);
            dinner.addToWait(System.nanoTime() - startTime);
            eat();
//            Main.accesses.set(philosopherIndex, Main.accesses.get(philosopherIndex) + 1);
            dinner.take(id);
            if (interrupted)
                return;
        }
    }
}
