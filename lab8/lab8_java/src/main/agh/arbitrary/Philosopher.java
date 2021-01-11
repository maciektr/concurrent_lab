package main.agh.arbitrary;

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
            try {
                dinner.put(id);
            } catch (InterruptedException e) {
                interrupted = true;
            }
            eat();
            dinner.take(id);
            if (interrupted)
                return;
        }
    }
}
